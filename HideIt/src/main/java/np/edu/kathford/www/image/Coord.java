package np.edu.kathford.www.image;


public class Coord {
    public int x;
    public int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coord) {
            Coord o = (Coord) obj;
            return (o.x == this.x) && (o.y == this.y);
        }
        return false;
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
