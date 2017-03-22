package clustering;

import java.util.List;

public class Cluster {
	private Point centroid;
	private List<Point> points;

	public Point getCentroid() {
		if (centroid == null) {
			double x = 0;
			double y = 0;
			for (int i = 0; i < points.size(); i++) {
				Point point = points.get(i);
				x += point.getX();
				y += point.getY();
			}
			x /= points.size();
			y /= points.size();
			centroid = new Point(x, y);
		}
		return centroid;
	}

	public Cluster(Point centroid, List<Point> points) {
		super();
		this.centroid = centroid;
		this.points = points;
	}

	public List<Point> getPoints() {
		return points;
	}

	public Point getPoint(int x) {
		if (points == null || x > points.size()) {
			System.err.println("Points is not initialized or this index is about of bounds");
			return null;
		}
		return points.get(x);
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
