package routesearch.data.javafile;

/* 駅間の情報 */ 
public class StationBranch{
  private Station stationA, stationB; //駅A 駅B
  private float distance; //重み

  public StationBranch(Station stA, Station stB, float distance){
    this.stationA = stA; 
    this.stationB = stB; 
    this.distance = distance; 
  }
  
  public Station nextStation(Station s) { 
      if (this.stationA.equals(s)) 
          return stationB;
      else if (this.stationB.equals(s))
          return stationA;
      else
          return null;
  }

  /* getter setter */

  public Station getStationA() {
    return stationA;
  }

  public Station getStationB() {
    return stationB;
  }

  public float getDistance(){
    return distance;
  }
}
