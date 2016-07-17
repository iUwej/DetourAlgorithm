import java.util.LinkedList;
import java.util.ListIterator;
/**
*GeoPolyline class  represents a path. It is a set of ordered
*Geopoints(coordinates) represented internally as a LinkedList<Geopoint>.
*/
public class GeoPolyline {

	//used to represent a GeoPolyline. We can get the next and previous points
	//to effecively compute the distance and traverse the path
	private LinkedList<GeoPoint> geoPath;

	public GeoPolyline(){
		geoPath= new LinkedList<GeoPoint>();
	}

	public boolean addPoint(GeoPoint point){
		//, we assume shortest paths, eliminate duplicates
		if(geoPath.contains(point))
			return false;
		geoPath.add(point);// add point to end of linklist
		return true;

	}

	public boolean addPoint(double x, double y){
		return addPoint(new GeoPoint(x,y));
	}

	/**
	*Will use this method to check whether two GeoPolylines intersect
	*/
	public boolean contains(GeoPoint point){
		return geoPath.contains(point);
	}


	public int indexOf(GeoPoint point){
		return geoPath.indexOf(point);
	}
 
	/*
	*We will use this method to tranverse from a point in a path to another
	*allow us to navigate in either direction
	**/
	public ListIterator<GeoPoint> pathNavigator(int firstIndex){
		return geoPath.listIterator(firstIndex);
	}

	/**Get the starting point of a path**/
	public GeoPoint getStart(){
		return geoPath.getFirst();
	}

	/*Get the destination of a particular path**/
	public GeoPoint getEnd(){
		return geoPath.getLast();
	}

	public int getPointsCount(){
		return geoPath.size();
	}


}