import java.util.ListIterator;

public class DetourAlgorithm{


	/*
	*Compute the distance between two consecutive points in a path
	*Ideally, we could pre-compute this in the GeoPoint class and 
	*cache the distances
	**/
	public double computeDistance(GeoPoint g1,GeoPoint g2){

		//use Pythagoras theorem
		double y2= Math.pow((g2.y-g1.y),2);
		double x2=Math.pow((g2.x-g1.x),2);

		return Math.sqrt(y2+x2);

	}

	/*
	*Algorithm to compute whether two orders should be combined
	**/
	public boolean hasNegligibleDetour(GeoPolyline l1,GeoPolyline l2, double maxAllowedDetour){
		if(l1 ==null || l2==null)
			return false;// or probably throw an exception here
		if(l1==l2)
			return true;//trivial case true
		//iterators to help us traverse the paths
		ListIterator<GeoPoint> l1Iterator=l1.pathNavigator(0);
		ListIterator<GeoPoint> l2Iterator=null;
		GeoPoint currentAD=null;
		GeoPoint currentBC=null;
		boolean foundIntersection=false; //initially no intersections between the paths
		//start from A until you find an Intersection with BC or reach destination D
		while(l1Iterator.hasNext()){
			currentAD= l1Iterator.next();
			if(l2.contains(currentAD)){//check for intersection between the two paths
				foundIntersection=true;
				currentBC=currentAD; //we will investigate the nature of paths from here
				break;
			} 
		}

		//another trivial case where the two GeoPolylines never intersect
		if(!foundIntersection)
			return false;
		
		GeoPoint intersection=currentAD;
		int indexOfBC=l2.indexOf(currentBC); //we disallowed duplicates which means Point is unique in this path
		int indexOfAD=l1.indexOf(currentAD);
		l2Iterator= l2.pathNavigator(indexOfBC);

		//case 1: this is the picking point for BC 
		
		if(currentBC.equals(l2.getStart())){
			//eliminate all common points
            while(l2Iterator.hasNext() && l1Iterator.hasNext()){
                currentAD=l1Iterator.next();
                currentBC=l2Iterator.next();
                if(!(currentAD.equals(currentBC)))
                   break;
            }

            //check if we have  a destination so far
                if(!l1Iterator.hasNext() || !l2Iterator.hasNext())
                    return true;//no detour so far
                
            //we have a detour at this point
            //check whether we can combine within constraints
			
                GeoPoint adPrev=null;
                GeoPoint bcPrev=null;
				//compute distance to both destinations from intersection point
				double distanceAD=0.0;
				//reset both iterators to intersections
				currentAD=l1Iterator.previous();
				currentBC=l2Iterator.previous();
				while(l1Iterator.hasNext()){
					adPrev=currentAD;
					currentAD=l1Iterator.next();
					distanceAD +=computeDistance(adPrev,currentAD);
					if((distanceAD *2) >maxAllowedDetour) 
						break; //we cannot first deliver to this destination, it exceeds detour 
				}

				//check wether we can first deliver to AD within constraints
				if((distanceAD *2)<=maxAllowedDetour)
					return true; //we have a negligible detour

				double distanceBC=0.0;
				while(l2Iterator.hasNext()){
					bcPrev=currentBC;
					currentBC=l2Iterator.next();
					distanceBC +=computeDistance(bcPrev,currentBC);
					if((distanceBC * 2)>maxAllowedDetour)
						return false; // we cannot first deliver to this destination C within detour constraints
				}

				if((distanceBC *2)<=maxAllowedDetour)
					return true; //we can first deliver to C then come back and deliver to D within constraints
                return false; //we cannot combine
			
			

		}
		//the intersection is not the pick up point for BC
		else{
			
			// two cases to consider
			//is this is the only intersection(shared point(s)) between AD and BC?,
			//if this is the only intersection,  B is not on ur way to D, we have a detour
			//else B is on our way to D, we need to reach last intersection point(Shared point) to compute detour
			GeoPoint bcPrev=currentBC;
			GeoPoint adPrev=currentAD;
			currentBC=l2Iterator.previous();
			currentAD=l2Iterator.next();

			if(currentAD.equals(currentBC)){ //we have more shared points(intersections) B is on our way to D
				//iterate until we find  the last intersection. Last point shared by AD and BC
				while(l1Iterator.hasNext() && l2Iterator.hasPrevious()){
					currentAD= l1Iterator.next();
					currentBC=l2Iterator.previous();
					if(!currentBC.equals(currentAD)) //at last the final shared point
						break;

				}

				//check whether this is point D
				if(!l1Iterator.hasNext())
					return true;

			}
			else{//we have a detour, B is not on our way to D

				//can we deliver to D, then come pick up within constraints
				double distanceAD=0.0;
				//reset iterators to intersections
				currentAD=l1Iterator.previous();
				indexOfAD=l1.indexOf(currentAD);
				while(l1Iterator.hasNext()){
					adPrev=currentAD;
					currentAD=l1Iterator.next();
					distanceAD +=computeDistance(adPrev,currentAD);
					if((distanceAD *2) >maxAllowedDetour) 
						break; //we cannot first deliver to this destination, it exceeds detour 
				}

				//check wether we can first deliver to AD within constraints
				if((distanceAD *2)<=maxAllowedDetour)
					return true; //we have a negligible detour 

				//can we pickup from B, deliver to C and come back to intersection within constraints
				//i have to try BC might be a very short distance
				currentBC=l2Iterator.next();
				int intersectionIndex=l2.indexOf(currentBC);
				double distanceBC=0.0;
				while(l2Iterator.hasPrevious()){
					bcPrev=currentBC;
					currentBC=l2Iterator.previous();
					distanceBC +=computeDistance(bcPrev,currentBC);
					if((distanceBC*2)>maxAllowedDetour) //we can't even get to the pick up point without violating constraints
						return false;

				}

				//we have a package from B and back to intersection within the maxDetour constraints
				//but can we drop it to C?
				l1Iterator=l1.pathNavigator(indexOfAD);
				l2Iterator=l2.pathNavigator(intersectionIndex);
				while(l1Iterator.hasNext() && l2Iterator.hasNext()){
					currentAD=l1Iterator.next();
					currentBC=l2Iterator.next();
					if(!currentBC.equals(currentAD))
						break;
				}
				currentBC=l2Iterator.previous();//last common point
				//add distance to B and back to intersection to distance to D and back to intersection
				while(l2Iterator.hasNext()){
					bcPrev=currentBC;
					currentBC=l2Iterator.next();
					distanceBC +=computeDistance(bcPrev,currentBC);
					if((distanceBC*2)>maxAllowedDetour)
						return false;
				}

				if((distanceBC*2)<=maxAllowedDetour)
					return true; //we can still pick from B and Drop to C then continue to D


			}
				
		}

		return false;


	}

	public static void main(String[] args) {

		DetourAlgorithm alg= new DetourAlgorithm();
		GeoPolyline ad= new GeoPolyline();
		ad.addPoint(0,0);
		ad.addPoint(3,0);
		ad.addPoint(7,0);
		GeoPolyline bc= new GeoPolyline();
		bc.addPoint(3,0);
		bc.addPoint(7,0);
		bc.addPoint(9,0);

		GeoPolyline gp= new GeoPolyline();
		gp.addPoint(4,5);
		gp.addPoint(12,17);

		System.out.println(alg.hasNegligibleDetour(ad,gp,2.5));

	}
} 
