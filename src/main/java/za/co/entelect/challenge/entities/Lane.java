package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.enums.Terrain;

public class Lane {
    @SerializedName("position")
    public za.co.entelect.challenge.entities.Position position;

    @SerializedName("surfaceObject")
    public za.co.entelect.challenge.enums.Terrain terrain;

    @SerializedName("occupiedByPlayerId")
    public int occupiedByPlayerId;
}
