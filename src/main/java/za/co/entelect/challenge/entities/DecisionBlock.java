package za.co.entelect.challenge.entities;

public class DecisionBlock implements Comparable<DecisionBlock>{
    public Lane lane;
    public Double distanceFromSource;

    public DecisionBlock(Lane lane, Double distanceFromSource) {
        this.lane = lane;
        this.distanceFromSource = distanceFromSource;
    }


    @Override
    public int compareTo(DecisionBlock o) {
        if (o.distanceFromSource == distanceFromSource) {
            return 0;
        }
        if (o.distanceFromSource > distanceFromSource) {
            return -1;
        } else {
            return 1;
        }
    }
}
