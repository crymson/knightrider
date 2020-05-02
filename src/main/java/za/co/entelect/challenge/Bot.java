package za.co.entelect.challenge;

import za.co.entelect.challenge.algorithm.BFSResult;
import za.co.entelect.challenge.algorithm.PathHolder;
import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {

    private static final int maxSpeed = 9;
    public static final int BOOST_SPEED = 15;
    private static int y;
    private static int x;
    private List<Integer> directionList = new ArrayList<>();

    Random random;
    GameState gameState;
    Car opponent;
    Car myCar;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;

        directionList.add(-1);
        directionList.add(1);
    }

    public Command run() {
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);
        Lane[][] frontBlocks = getAllBlocksInFront(myCar.position.block);

        return getNextBestCommandBFS(frontBlocks);
//        Command cmd = findBestPath(gameState);
//        if (blocks.contains(Terrain.MUD)) {
//            int i = random.nextInt(directionList.size());
//            return new ChangeLaneCommand(directionList.get(i));
//        }
//        return new AccelerateCommand();
    }

    private Command getNextBestCommandBFS(Lane[][] frontBlocks) {
        List<Lane> powerups = getBoosts(frontBlocks);
        List<DecisionBlock> closest = getDistanceToTargets(gameState.player.position, powerups);
        Collections.sort(closest);
        Command returnCommand = new AccelerateCommand();

//        if (powerups.size() > 0) {
//            returnCommand = bfSearch(gameState.player.position, frontBlocks, closest.get(0).lane.position);
//            if ((returnCommand instanceof AccelerateCommand) && hasBoostAvailable(gameState.player.powerups)) {
//                return new BoostCommand();
//            }
//        } else {
//            get the closest path to the end points but for now just be arbitrary
            returnCommand = bfSearch(gameState.player.position, frontBlocks, frontBlocks[gameState.player.position.lane-1][frontBlocks[0].length-1].position);
            if ((returnCommand instanceof AccelerateCommand) && hasBoostAvailable(gameState.player.powerups)) {
                return new BoostCommand();
//            }

        }
        return returnCommand;
    }

    private boolean hasBoostAvailable(PowerUps[] powerups) {
        for (PowerUps powerup : powerups){
            if (powerup.equals(PowerUps.BOOST)) return true;
        }
        return false;
    }

//    private Command findBestPath(GameState currentState) {
//        Lane[][] blocks = getAllBlocksInFront(currentState.player.position.block);
//        int bestVal = 0;
//        Command nextCommand = getNextCommand(gameState, blocks);
//        updateGameState(gameState, nextCommand);
//        int currentVal = minimax(gameState, 10);
//        return null;
//    }

    private Command getNextCommand(GameState gameState, Lane[][] blocks) {
        int currentLane = gameState.player.position.lane;
        //First move will be forward
        if (blocks[currentLane][0].terrain.equals(Terrain.EMPTY)
                ||(blocks[currentLane][0].terrain.equals(Terrain.BOOST))
                ||(blocks[currentLane][0].terrain.equals(Terrain.OIL_POWER))){
            if (gameState.player.speed < maxSpeed) {

                return new AccelerateCommand();
            }
        }
        //Check left lane
        if ((currentLane > 0)
            && ((!blocks[currentLane-1][0].terrain.equals(Terrain.MUD)||!blocks[currentLane-1][0].terrain.equals(Terrain.MUD)))){
            return new ChangeLaneCommand(currentLane-1);
        }
        //check right lane
        if ((currentLane < 3)
                && ((!blocks[currentLane+1][0].terrain.equals(Terrain.MUD)||!blocks[currentLane+1][0].terrain.equals(Terrain.MUD)))){
            return new ChangeLaneCommand(currentLane+1);
        }
        return new DoNothingCommand();
    }

    Lane[][] getAllBlocksInFront(int block) {
        Lane[][] blocks = new Lane[gameState.lanes.size()][];

        for (int i = 0; i < gameState.lanes.size(); i++) {
            blocks[i] = Arrays.copyOfRange(gameState.lanes.get(i),
                                      (gameState.lanes.get(i).length-1)-(gameState.lanes.get(i)[gameState.lanes.get(i).length-1].position.block - block),
                                        gameState.lanes.get(0).length);
        }
        return blocks;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    public List<Lane> getBoosts(Lane[][] blocks) {
        List<Lane> returnArray = new ArrayList<>();

        for (Lane[] block : blocks) {
            for (Lane lane : block) {
                if (lane.terrain.equals(Terrain.BOOST)) {
                    returnArray.add(lane);
                }
            }
        }
        return returnArray;
    }

    public List<DecisionBlock> getDistanceToTargets(Position position, List<Lane> powerups) {
        List<DecisionBlock> targets = new ArrayList<>();
        for (Lane lane: powerups) {
            Double currentDistance = calculateDistance(gameState.player.position, lane.position);
            targets.add(new DecisionBlock(lane,currentDistance));
        }

        return targets;
    }

    private Double calculateDistance(Position source, Position target) {
        // SQRT ((X2-X2)^2 + (Y2-Y1)^2)
        return Math.sqrt(Math.pow((target.lane-1 - source.lane), 2) + Math.pow((target.block - source.block),2));
    }

//    public Command getNextBestCommand(Lane[][] blocks) {
//        List<DecisionBlock> closest = getDistanceToTargets(gameState.player.position, getBoosts(blocks));
//        Collections.sort(closest);
//        int i=0;
//        // Iterate through the powerups
//        for (DecisionBlock block : closest) {
//            if (playerInLane(block)) {
//                Lane obstacle = getFirstObstacleBlock(blocks, block.lane.position.lane);
//                if (obstacle != null) {
//                    System.out.println("Obstacle is:" + obstacle.terrain);
//                    if (targetBlockInRangeOfObstacle(block, obstacle)) {
//                        return changeLane();
//                    } else {
//                        return boostIfAvailable();
//                    }
//                } else {
//                    return boostIfAvailable();
//                }
//            } else {
//                if (block.lane.position.lane > gameState.player.position.lane) {
//                    return new ChangeLaneCommand(1);
//                }else {
//                    return new ChangeLaneCommand(0);
//                }
////                Lane obstacle = getFirstObstacleBlock(blocks, block.lane.position.lane);
////
////                if (obstacle != null) {
////                } else {
////                    return boostIfAvailable();
////                }
//
//            }
//        }
//
//        System.out.println("DOING NOTHING");
//        return new DoNothingCommand();
//    }

    private boolean playerInLane(DecisionBlock block) {
        return block.lane.position.lane == gameState.player.position.lane;
    }

    private boolean boostAvailable() {
        return !gameState.player.boosting && Arrays.asList(gameState.player.powerups).contains(PowerUps.BOOST);
    }

    private boolean targetBlockInRangeOfObstacle(DecisionBlock block, Lane obstacle) {
        return obstacle.position.block < block.lane.position.block + gameState.player.speed;
    }

    private Command changeLane() {
        if (gameState.player.position.lane < 4 && gameState.player.position.lane >= 1) {
            return new ChangeLaneCommand(1);
        }
        if (gameState.player.position.lane == 0) {
            return new ChangeLaneCommand(1);
        }
        //Move right
        if (gameState.player.position.lane == 4) {
            return new ChangeLaneCommand(0);
        }
        return null;
    }

    Lane getFirstObstacleBlock(Lane[][] blocks, int lane) {
        for (Lane block : blocks[lane-1]) {
            if (block.terrain.equals(Terrain.MUD) || (block.terrain.equals(Terrain.OIL_SPILL))) {
                return block;
            }
        }
        return null;
    }

    public Command bfSearch(Position start, Lane[][] blocksConsidered, Position destination){
        // This is unlikely but we'll check it
        if ((start.lane == destination.lane) && (start.block == destination.block)) {
            return new AccelerateCommand();
        }
        Position destinationPoint = new Position();
        destinationPoint.lane = destination.lane;
        destinationPoint.block = destination.block - start.block;

        Queue lane = new LinkedList();
        Queue block = new LinkedList();
        Queue<Car> player = new LinkedList<>();

        boolean[][] visited = new boolean[blocksConsidered.length][blocksConsidered[0].length];
        PathHolder[][] prev = new PathHolder[blocksConsidered.length][blocksConsidered[0].length];

        enqueueCoordinates(0, lane, block, start.lane-1, player, gameState.player);

        visited[start.lane-1][0] = true;
        prev[start.lane-1][0] = null;

        boolean reachedEnd=false;

        while (!lane.isEmpty()) {
            int y = ((int) lane.remove());
            int x = ((int) block.remove());
            Car car = player.remove();

            Lane current = blocksConsidered[y][x];
//            if (current.position.block > destination.block) {
//                current.position = destination;
//            }
            if (current.position.equals(destination)) {
                reachedEnd=true;
                //Got to be clever here -
                break;
            }

            visitNeighbours(y,x, blocksConsidered, lane, block, player, car, prev, visited, destinationPoint);

        }
        if (reachedEnd) {
            return getFirstCommand(prev, start, destination);
        }else {
            return new AccelerateCommand();
        }
    }

    private Command getFirstCommand(PathHolder[][] prev, Position start, Position destination) {
        String commands = "";
        y = destination.lane-1;
        x = destination.block-start.block;

        commands += prev[y][x].prevCommand + "";
        int tempX = prev[y][x].prevX;
        int tempY = prev[y][x].prevY;
        x=tempX;
        y=tempY;

        BFSResult result = new BFSResult(null,0);

        Command currentCommand = new AccelerateCommand();
        while (null != prev[y][x]) {
            currentCommand = prev[y][x].prevCommand;
            commands += prev[y][x].prevCommand + "";
            tempX = prev[y][x].prevX;
            tempY = prev[y][x].prevY;
            x=tempX;
            y=tempY;
            result.cost++;

        }
        result.command=currentCommand;

        return currentCommand;
    }

    private void visitNeighbours(int y, int x, Lane[][] blocksConsidered, Queue lane, Queue block, Queue<Car> player, Car car, PathHolder[][] prev, boolean[][] visited, Position destination) {
        int laneRight = y + 1;
        int laneLeft = y - 1;

        int blockInFront = x + car.speed;

        if (blockInFront > destination.block) {
            blockInFront = destination.block;
        }

        if ((laneRight < 3) && !isAnObstacle(blocksConsidered[laneRight][x]) && !visited[laneRight][x]) {
            updateMatrices(new ChangeLaneCommand(1), x, x, y, laneRight, prev, visited);
            enqueueCoordinates(x, lane, block, laneRight, player, car);
        }
        if ((laneLeft > 0) && !isAnObstacle(blocksConsidered[laneLeft][x]) && !visited[laneLeft][x]) {
            updateMatrices(new ChangeLaneCommand(0), x, x, y, laneLeft, prev, visited);
            enqueueCoordinates(x, lane, block, laneLeft, player, car);
        }
//        if ((blockInFront < (blocksConsidered[y].length)) && !isAnObstacle(blocksConsidered[y][blockInFront])&& !visited[y][blockInFront]) {
        if ((blockInFront < (blocksConsidered[y].length)) && !obstacleIsInRange(blocksConsidered, y, x, blockInFront) && !visited[y][blockInFront]) {
            Command moveCommand = new AccelerateCommand();
//            if (!obstacleIsInRange(blocksConsidered, y, x, x + BOOST_SPEED) && !visited[y][(x + BOOST_SPEED) % visited[y].length]) {
//                blockInFront = (x + BOOST_SPEED) % visited[y].length;
//                moveCommand = new BoostCommand();
//            }
            car.speed = (car.speed +1) % 9;
            updateMatrices(moveCommand, x, blockInFront, y, y, prev, visited);
            enqueueCoordinates(blockInFront, lane, block, y, player, car);
        }


    }

    private void enqueueCoordinates(int x, Queue lane, Queue block, int y, Queue<Car> player, Car car) {
        lane.add(y);
        block.add(x);
        player.add(car);
    }

    private void updateMatrices(Command command, int origX, int targetX, int origY, int targetY, PathHolder[][] prev, boolean[][] visited) {
        visited[targetY][targetX] = true;
        prev[targetY][targetX] = new PathHolder(command, origX, origY);
    }

    private boolean isAnObstacle(Lane lane) {
        return lane.terrain.equals(Terrain.MUD) || lane.terrain.equals(Terrain.OIL_SPILL);
    }

    public boolean obstacleIsInRange(Lane[][] blocks, int lane, int blocksFrom, int blocksTo) {
        for (int i=blocksFrom; i <= (blocksTo % blocks[lane].length); i++) {
            if (blocks[lane][i].terrain.equals(Terrain.MUD) || blocks[lane][i].terrain.equals(Terrain.OIL_SPILL)) {
                return true;
            }
        }
        return false;
    }
}
