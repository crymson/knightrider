package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.Lane;

public class PathHolder {
    public Command prevCommand;
    public int prevX;
    public int prevY;

    public PathHolder(Command prevCommand, int prevX, int prevY) {
        this.prevCommand = prevCommand;
        this.prevX = prevX;
        this.prevY = prevY;
    }
}
