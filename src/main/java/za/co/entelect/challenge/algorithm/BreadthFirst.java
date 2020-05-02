package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.entities.Lane;

import java.util.PriorityQueue;

public class BreadthFirst {
    private final Lane[][] frontblocks;
    private Lane[][] visited;
    private PriorityQueue open = new PriorityQueue();
    private PriorityQueue closed = new PriorityQueue();

    public BreadthFirst(Lane[][] frontBlocks, int lane) {
        this.frontblocks = frontBlocks;
        visited = new Lane[frontblocks.length][];
        open.add(this.frontblocks[lane][0]);

    }

}
