package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.command.Command;

public class BFSResult {
    public Command command;
    public int cost;

    public BFSResult(Command command, int cost) {
        this.command = command;
        this.cost = cost;
    }
}
