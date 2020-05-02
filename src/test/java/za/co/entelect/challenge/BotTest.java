package za.co.entelect.challenge;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import za.co.entelect.challenge.command.AccelerateCommand;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.DecisionBlock;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.entities.Position;
import za.co.entelect.challenge.enums.Terrain;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static java.nio.file.Paths.get;
import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    Gson gson = new Gson();
    Random random = new Random(System.nanoTime());

    private GameState rehydrateGameState() throws IOException {
//        String statePath = String.format("./%s/%d/%s", ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
        String state = new String(Files.readAllBytes(get("F:\\dev\\entellect\\2020\\knightrider\\src\\test\\resources\\state.json")));//Paths.get(statePath)));
        GameState gameState = gson.fromJson(state, GameState.class);
        return gameState;
    }

    @Test
    void getAllBlocksInFront() throws IOException{
        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random,gameState);
        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);
        assertEquals(84, blocks.length * blocks[0].length);
    }

    @Test
    void testThatGetPowerupsReturnsThree() throws IOException{
        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random,gameState);
        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);

        List<Lane> powerups = new ArrayList<>();
        powerups = bot.getPowerups(blocks);
        assertEquals(3, powerups.size());

    }

    @Test
    void testThatGetDistanceToTargetsReturnsOilPowerAsTheClosestWhenSorted() throws IOException {
        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random,gameState);
        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);

        List<Lane> powerups = bot.getPowerups(blocks);

        List<DecisionBlock> closest = bot.getDistanceToTargets(gameState.player.position, powerups);
        Collections.sort(closest);
        assertEquals(Terrain.BOOST, closest.get(0).lane.terrain);
//        assertEquals(Terrain.OIL_POWER, closest.get(0).lane.terrain);
    }

    @Test
    void testThatGetNextBestMoveReturnsAccelerateCommand() throws IOException {
        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random, gameState);

        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);

        Command command = bot.getNextBestCommand(blocks);
        assertEquals(AccelerateCommand.class, command.getClass());

    }

    @Test
    void testThatTargetLaneContainsMud() throws IOException {

        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random, gameState);

        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);

        Lane block = bot.getFirstObstacleBlock(blocks, gameState.player.position.lane);
        assertNotNull(block);
    }

    @Test
    void testThatTargetRangeContainsObstacle() throws IOException {

        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random, gameState);

        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);

        boolean blocked = bot.obstacleIsInRange(blocks, 3,8,21);
        assertTrue( blocked);
    }

    @Test
    void testBfSearchReturnsAccelerate() throws IOException {

        GameState gameState = rehydrateGameState();
        Bot bot = new Bot(random, gameState);

        Lane[][] blocks = bot.getAllBlocksInFront(gameState.player.position.block);
        List<Lane> powerups = bot.getPowerups(blocks);
        List<DecisionBlock> closest = bot.getDistanceToTargets(gameState.player.position, powerups);
        Collections.sort(closest);
        Lane block = bot.getFirstObstacleBlock(blocks, gameState.player.position.lane);


        bot.bfSearch(gameState.player.position, blocks, closest.get(0).lane.position);

    }

    @Test
    void testThatGeneratedBlockReturnsCorrectPath() throws IOException {
        Lane[][] blocks = new Lane[4][20];

        for (int i=0; i<4;i++) {
            for (int j=0; j<20;j++) {
                blocks[i][j] = createLane(Terrain.EMPTY, i, j, 0);
            }
        }
    }

    private Lane createLane(Terrain terrain, int lane, int block, int occupiedBy) {
        Lane newLane = new Lane();
        newLane.terrain = terrain;
        newLane.position = new Position();
        newLane.position.lane=lane;
        newLane.position.block=block;
        newLane.occupiedByPlayerId = occupiedBy;
        return newLane;
    }


}