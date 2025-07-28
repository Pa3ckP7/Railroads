package railroads;

import dto.EvaluatedSolution;
import dto.EvolutionResults;
import dto.Solution;
import models.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DarwinParallel {

    private int generation = 0;
    private ArrayList<Agent> agents;
    private final Random rand;
    private Board initBoard;
    private ExecutorService executor;

    public DarwinParallel(long randomSeed) {
        this.agents = new ArrayList<>();
        this.rand = new Random(randomSeed);
        var maxThreads = Runtime.getRuntime().availableProcessors();
        this.executor =  Executors.newFixedThreadPool(Math.max(maxThreads-1, 1)); //to account for the main darwin thread
        initGen0();
    }

    private void initGen0(){
        long board_seed = rand.nextLong();
        this.initBoard = new Board(board_seed);
        ArrayList<Callable<Agent>> cAgents = new ArrayList<>();
        for (int i = 0; i < Settings.MAX_AGENTS; i++) {
            final var aseed = rand.nextLong();
            cAgents.add( () -> Agent.generateAgent(aseed));
        }

        try {
            var fAgents = executor.invokeAll(cAgents);
            for(var fAgent : fAgents){
                this.agents.add(fAgent.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public EvolutionResults evolve(){
        var results =  runAgents(this.agents);
        var evaluatedResults = evaluateSolutions(results);
        evaluatedResults.sort(Comparator.comparingLong(EvaluatedSolution::evaluation));
        var newGeneration = repopulateAgents(evaluatedResults, rand.nextLong());
        generation++;
        agents = newGeneration;
        var winner = evaluatedResults.stream().filter(EvaluatedSolution::success).findFirst();
        EvaluatedSolution best = evaluatedResults.getFirst();
        if(winner.isPresent()){
            best = winner.get();
        }
        boolean allSuccess = evaluatedResults.stream().parallel().allMatch(EvaluatedSolution::success);
        if(allSuccess){
            System.out.println("ALL SUCCESS");
        }
        return new EvolutionResults(
                generation,
                best,
                evaluatedResults.getLast()
        );
    }

    private ArrayList<Solution> runAgents(ArrayList<Agent> agents){
        var solutions = new ArrayList<Solution>();
        var cSolutions = new ArrayList<Callable<Solution>>();
        for (final Agent agent : agents) {
            final var board = new Board(initBoard);
            cSolutions.add(() -> agent.solve(board));
        }
        try {
            var fSolutions = executor.invokeAll(cSolutions);
            for (var fsolution : fSolutions) {
                solutions.add(fsolution.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return solutions;
    }
    private ArrayList<EvaluatedSolution> evaluateSolutions(ArrayList<Solution> solutions){
        ArrayList<EvaluatedSolution> evaluatedSolutions = new ArrayList<>();
        ArrayList<Callable<EvaluatedSolution>> cEvaluatedSolutions = new ArrayList<>();
        for (final Solution solution : solutions) {
            cEvaluatedSolutions.add(() -> evaluateSolution(solution));
        }

        try {
            var fSolutions = executor.invokeAll(cEvaluatedSolutions);
            for (var fsolution : fSolutions) {
                evaluatedSolutions.add(fsolution.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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
        var futureChildren =  new ArrayList<Callable<Agent>>();
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
                if(selector.nextDouble() > Settings.CROSS_SKIP) break;
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

            HashSet<Transform> mask = parentA.flood().stream().parallel().map(Gene::getTransform).collect(Collectors.toCollection(HashSet::new));
            HashSet<Transform> maskB = parentB.flood().stream().parallel().map(Gene::getTransform).collect(Collectors.toCollection(HashSet::new));
            mask.addAll(maskB);
            for(int j=0; j < Settings.MUTATION_RADIUS; j++){
                mask = growPath(mask);
            }


            final var aSeed = selector.nextLong();
            final var bSeed = selector.nextLong();
            final var fMask = mask;
            final var fParentA = parentA;
            final var fParentB = parentB;
            final var fGenesA = genesA;
            final var fGenesB = genesB;
            futureChildren.add(() -> finalizeAgent(fParentA.solution().signer().evolve(fGenesB), aSeed, fMask));
            futureChildren.add( () -> finalizeAgent(fParentB.solution().signer().evolve(fGenesA), bSeed, fMask));

            parentA = null;
        }

        try {
            var fchildren = executor.invokeAll(futureChildren);
            for(var child: fchildren){
                newAgents.add(child.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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
}
