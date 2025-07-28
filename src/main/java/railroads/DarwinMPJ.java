package railroads;

import dto.EvaluatedSolution;
import dto.EvolutionResults;
import dto.MPJScatterSettings;
import dto.Solution;
import models.*;
import mpi.MPI;

import java.util.*;
import java.util.stream.Collectors;

public class DarwinMPJ {

    private int generation = 0;
    private ArrayList<Agent> agents;
    private final Random rand;
    private Board initBoard;

    private int mpjRank;
    private int mpjCount;

    private MPJScatterSettings mpjScatterSettings;

    public DarwinMPJ(long randomSeed) {
        this.agents = new ArrayList<>();
        this.rand = new Random(randomSeed);
        mpjRank = MPI.COMM_WORLD.Rank();
        mpjCount = MPI.COMM_WORLD.Size();
        mpjScatterSettings = calculateScatterSettings();
        initGen0();
    }

    private void initGen0(){
        long board_seed = rand.nextLong();
        this.initBoard = new Board(board_seed);
        if(mpjRank==0){
            for (int i = 0; i < Settings.MAX_AGENTS; i++) {
                Agent agent = Agent.generateAgent(rand.nextLong());
                this.agents.add(agent);
            }
        }
    }

    public EvolutionResults evolve(){


        Agent[] myAgents = getMyAgents();

        var agentbatch = new ArrayList<>(Arrays.asList(myAgents));
        var results = runAgents(agentbatch);
        var localEvaluatedResults = evaluateSolutions(results);

        var evalresults = gatherSolutions(localEvaluatedResults.toArray(EvaluatedSolution[]::new));
        if(mpjRank!=0) return null;
        var evaluatedResults = new ArrayList<>(Arrays.asList(evalresults));

        evaluatedResults.sort(Comparator.comparingLong(EvaluatedSolution::evaluation));
        var newGeneration = repopulateAgents(evaluatedResults, rand.nextLong());
        generation++;
        agents = newGeneration;
        var winner = evaluatedResults.stream().filter(EvaluatedSolution::success).findFirst();
        EvaluatedSolution best = evaluatedResults.getFirst();
        if(winner.isPresent()){
            best = winner.get();
        }
        boolean allSuccess = evaluatedResults.stream().allMatch(EvaluatedSolution::success);
        if(allSuccess){
            System.out.println("ALL SUCCESS");
        }
        return new EvolutionResults(
                generation,
                best,
                evaluatedResults.getLast()
        );
    }

    private Agent[] getMyAgents(){

        Agent[] sendBuffer = null;
        if (mpjRank == 0) {
            sendBuffer = this.agents.toArray(Agent[]::new);
        }

        Agent[] agents = new Agent[mpjScatterSettings.counts()[mpjRank]];

        MPI.COMM_WORLD.Scatterv(sendBuffer,0,mpjScatterSettings.counts(),mpjScatterSettings.displacementIndex(), MPI.OBJECT,
                agents, 0, mpjScatterSettings.counts()[mpjRank], MPI.OBJECT, 0);

        return agents;
    }

    private EvaluatedSolution[] gatherSolutions(EvaluatedSolution[] lResults){
        EvaluatedSolution[] allSolution = null;
        if(mpjRank == 0){
            allSolution = new EvaluatedSolution[Settings.MAX_AGENTS];
        }
        MPI.COMM_WORLD.Gatherv(lResults, 0,mpjScatterSettings.counts()[mpjRank],
                MPI.OBJECT, allSolution, 0, mpjScatterSettings.counts(),
                mpjScatterSettings.displacementIndex(), MPI.OBJECT, 0);
        return allSolution;
    }
    private ArrayList<Solution> runAgents(ArrayList<Agent> agents){
        var solutions = new ArrayList<Solution>();
        for (Agent agent : agents) {
            var board = new Board(initBoard);
            solutions.add(agent.solve(board));
        }
        return solutions;
    }
    private ArrayList<EvaluatedSolution> evaluateSolutions(ArrayList<Solution> solutions){
        ArrayList<EvaluatedSolution> evaluatedSolutions = new ArrayList<>();
        for (Solution solution : solutions) {
            evaluatedSolutions.add(evaluateSolution(solution));
        }
        return evaluatedSolutions;
    }

    private EvaluatedSolution evaluateSolution(Solution solution){
        boolean failed = false;
        HashSet<Gene> flood = new HashSet<>();
        HashSet<Gene> minimals = new HashSet<>();
        StationTrack[] stations = initBoard.getStations();
        HashSet<Gene> genes = new HashSet<>();
        long score = 0;
        for(var track: stations){
            var tf = floodTrack(solution, track);
            var tm = findMinimalWeightedPath(solution, track);
            flood.addAll(tf);
            minimals.addAll(tm);
            if(tm.isEmpty()){
                score += tf.stream().map(Gene::getTile).mapToInt(t -> t.evalValue).sum()* 10L;
                genes.addAll(tf);
                failed = true;
            }else{
                score += tm.stream().map(Gene::getTile).mapToInt(t -> t.evalValue).sum();
                genes.addAll(tm);
            }
        }

        var sf = Arrays.stream(solution.solution().getAllTiles()).mapToInt(t -> t.evalValue).sum();
        var sm = minimals.stream().map(Gene::getTile).mapToInt(t -> t.evalValue).sum();
        score += (sf-sm)*1000L;

        return new EvaluatedSolution(genes, score, solution, !failed, flood);
    }

    private boolean validateMin(ArrayList<Gene> path){
        boolean isValid = true;
        for (int i = 1; i < path.size(); i++) {
            Gene prevGene = path.get(i - 1);
            Gene currGene = path.get(i);
            Transform prev = prevGene.getTransform();
            Transform curr = currGene.getTransform();
            Tile prevTile = prevGene.getTile();
            Tile currTile = currGene.getTile();

            int dx = curr.x - prev.x;
            int dy = curr.y - prev.y;

            boolean connection = false;
            if (dx == 1 && dy == 0) { // right
                connection = prevTile.right && currTile.left;
            } else if (dx == -1 && dy == 0) { // left
                connection = prevTile.left && currTile.right;
            } else if (dx == 0 && dy == 1) { // down
                connection = prevTile.down && currTile.up;
            } else if (dx == 0 && dy == -1) { // up
                connection = prevTile.up && currTile.down;
            }

            if (!connection) {
                isValid = false;
                System.out.println("Invalid connection between: " + prev + " (" + prevTile + ") and " + curr + " (" + currTile + ")");
                return isValid;
            }
        }
        return true;
    }

    private HashSet<Gene> floodTrack(Solution solution, StationTrack track){
        Board board = solution.solution();
        HashSet<Transform> traversed = new HashSet<>();
        Stack<Transform> queue = new Stack<>();
        HashSet<Gene> genes = new HashSet<>();
        boolean fail = true;
        queue.add(track.getEndpoint());
        queue.add(track.getStartpoint());
        while(!queue.isEmpty()){
            var t = queue.pop();
            if(traversed.contains(t)) continue;
            var tile = board.getTile(t.x, t.y);
            traversed.add(t);
            if(tile == Tile.None) continue;
            if(tile.up) {
                var tn = new Transform(t.x, t.y - 1);
                var tnt = board.getTile(tn.x, tn.y);
                if(tnt.down)
                    queue.add(tn);
            };
            if(tile.down) {
                var tn = new Transform(t.x, t.y + 1);
                var tnt =  board.getTile(tn.x, tn.y);
                if(tnt.up)
                    queue.add(tn);
            };
            if(tile.left) {
                var tn  = new Transform(t.x - 1, t.y);
                var tnt  =  board.getTile(tn.x, t.y);
                if (tnt.right)
                    queue.add(tn);
            };
            if(tile.right) {
                var tn  = new Transform(t.x + 1, t.y);
                var tnt   =  board.getTile(tn.x, t.y);
                if(tnt.left)
                    queue.add(tn);
            };
            if(tile == Tile.Station) continue;
            genes.add(new Gene(new Transform(t), tile));
        }
        return genes;
    }

    public ArrayList<Gene> findMinimalWeightedPath(Solution solution, StationTrack track) {
        Board board = solution.solution();
        Transform start = track.getStartpoint();
        Transform end = track.getEndpoint();
        HashMap<Transform, Transform> cameFrom = new HashMap<>();
        HashMap<Transform, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Transform> queue = new PriorityQueue<>(Comparator.comparingInt(costSoFar::get));

        costSoFar.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Transform current = queue.poll();
            if (current.x == end.x && current.y == end.y) {
                var path = new ArrayList<Gene>();
                for (Transform at = end; at != null; at = cameFrom.get(at)) {
                    path.add(new Gene(new Transform(at), board.getTile(at.x, at.y)));
                }
                return path;

            }

            Tile tile = board.getTile(current.x, current.y);

            ArrayList<Transform> nextPositions = new ArrayList<>();

            if (tile.up) {
                Transform next = new Transform(current.x, current.y - 1);
                Tile nextTile = board.getTile(next.x, next.y);
                if (nextTile.down) {
                    nextPositions.add(next);
                }
            }
            if (tile.down) {
                Transform next = new Transform(current.x, current.y + 1);
                Tile nextTile = board.getTile(next.x, next.y);
                if (nextTile.up) {
                    nextPositions.add(next);
                }
            }
            if (tile.left) {
                Transform next = new Transform(current.x - 1, current.y);
                Tile nextTile = board.getTile(next.x, next.y);
                if (nextTile.right) {
                    nextPositions.add(next);
                }
            }
            if (tile.right) {
                Transform next = new Transform(current.x + 1, current.y);
                Tile nextTile = board.getTile(next.x, next.y);
                if (nextTile.left) {
                    nextPositions.add(next);
                }
            }

            for (Transform next : nextPositions) {
                Tile nextTile = board.getTile(next.x, next.y);
                int newCost = costSoFar.get(current) + nextTile.evalValue;

                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    cameFrom.put(next, current);
                    queue.add(next);
                }
            }
        }
        return new ArrayList<>();
    }

    private ArrayList<Agent> repopulateAgents(ArrayList<EvaluatedSolution> evaluatedSolutions, long seed){
        var selector =  new Random(seed);
        var exclude = new HashSet<Agent>();
        evaluatedSolutions.sort(Comparator.comparingLong(EvaluatedSolution::evaluation));
        var newAgents = new ArrayList<Agent>();
        var oldAgents = new ArrayList<>(evaluatedSolutions);
        var elitism = (int)Math.ceil(oldAgents.size() * Settings.AGENT_ELITISM);
        var elite = oldAgents.stream().filter(EvaluatedSolution::success).collect(Collectors.toCollection(ArrayList::new));
        for(int i = 0; i < elitism && !elite.isEmpty(); i++){
            EvaluatedSolution a = elite.removeFirst();
            newAgents.add(a.solution().signer());
        }

        EvaluatedSolution parentA = null;
        while(newAgents.size() < Settings.MAX_AGENTS && !oldAgents.isEmpty()){
            int pi = 0;
            while(pi < oldAgents.size()-1){
                if(rand.nextDouble() > Settings.CROSS_SKIP) break;
                pi++;
            }
            if(parentA == null){
                parentA = oldAgents.remove(pi);
                pi = 0;
                continue;
            }
            var parentB = oldAgents.remove(pi);
            pi = 0;
            var genesA = new TreeMap<Transform, Gene>();
            var genesB = new TreeMap<Transform, Gene>();
            for(var gene: parentA.rawEvaluation()){
                genesA.put(new Transform(gene.getTransform()), gene);
            }

            for(var gene: parentB.rawEvaluation()){
                genesB.put(new Transform(gene.getTransform()), gene);
            }

            HashSet<Transform> mask = parentA.flood().stream().map(Gene::getTransform).collect(Collectors.toCollection(HashSet::new));
            HashSet<Transform> maskB = parentB.flood().stream().map(Gene::getTransform).collect(Collectors.toCollection(HashSet::new));
            mask.addAll(maskB);
            for(int j=0; j < Settings.MUTATION_RADIUS; j++){
                mask = growPath(mask);
            }

            var childA = finalizeAgent(parentA.solution().signer().evolve(genesB), selector.nextLong(), mask);
            var childB = finalizeAgent(parentB.solution().signer().evolve(genesA), selector.nextLong(), mask);

            newAgents.add(childA);
            newAgents.add(childB);
            parentA = null;
        }
        while(newAgents.size() > Settings.MAX_AGENTS){
            newAgents.removeLast();
        }
        return newAgents;
    }

    private HashSet<Transform> growPath(HashSet<Transform> path){
        HashSet<Transform> mutPath = new HashSet<>();
        for(var t: path){
            mutPath.add(t);
            if(t.x + 1 < Settings.BOARD_WIDTH){
                mutPath.add(new Transform(t.x + 1, t.y));
            }
            if(t.x - 1 >= 0){
                mutPath.add(new Transform(t.x-1, t.y));
            }
            if(t.y + 1 < Settings.BOARD_HEIGHT){
                mutPath.add(new Transform(t.x, t.y + 1));
            }
            if(t.y - 1 >= 0){
                mutPath.add(new Transform(t.x, t.y-1));
            }
        }
        return mutPath;
    }

    public Agent finalizeAgent(TreeMap<Transform, Gene> genome, long seed, HashSet<Transform> mask){

        var tiles = Tile.values();
        var rand = new Random(seed);

        for(var t:mask){
            if(rand.nextDouble() > Settings.MUTATION_CHANCE) continue;
            var tile = tiles[rand.nextInt(tiles.length)];
            if (tile == Tile.Station) continue;
            if (tile == Tile.None) continue;
            genome.put(t, new Gene(t, tile));
        }
        for(int y =  0; y < Settings.BOARD_HEIGHT; y++){
            for(int x = 0; x < Settings.BOARD_WIDTH; x++){
                if(rand.nextDouble() > Settings.DELETE_MUTATION_CHANCE) continue;
                genome.remove(new Transform(x, y));
            }
        }

        return new Agent(genome);
    }

    public Board getInitBoard() {
        return initBoard;
    }

    private MPJScatterSettings calculateScatterSettings(){
        int baseChunk = Settings.MAX_AGENTS / mpjCount;
        int remainder = Settings.MAX_AGENTS % mpjCount;

        int[] counts = new int[mpjCount];
        int[] displs = new int[mpjCount];
        int offset = 0;
        for (int i = 0; i < mpjCount; i++) {
            counts[i] = baseChunk + (i < remainder ? 1 : 0);
            displs[i] = offset;
            offset += counts[i];
        }
        return new MPJScatterSettings(counts, displs);
    }
}
