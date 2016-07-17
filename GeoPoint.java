
public class GeoPoint{

	
	final double x;
	final double y;

	public GeoPoint(double x,double y){
		this.x=x;
		this.y=y;
	}

	/**
	*Override the equals such that for any two Geopoints g1,g2 
	*if g1.x==g2.x and g1.y==g2.y then they refer to the same point
	*/


	@Override
	public boolean equals(Object other){
		if(!(other instanceof GeoPoint ))
				return false;
		if(other==this)
			return true;

		GeoPoint rhs= (GeoPoint)other;
		if(this.x==rhs.x && this.y==rhs.y)
			return true;
		return false;
	}

	/**
	*Two equivalent GeoPoints should hash to the same code
	*/

	@Override
	public int hashCode(){
		int result=17;
		int xhash=Double.valueOf(x).hashCode();
		int yhash=Double.valueOf(y).hashCode();
		result=37*result+xhash;
		result=37*result+yhash;
		return result;
	}


}