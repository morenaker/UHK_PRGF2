package Solid;

public class Part {
    private final int start;
    private final int count;
    private final TopologyType type;

    public Part(int start, int count, TopologyType type) {
        this.start = start;
        this.count = count;
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public TopologyType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }
}
