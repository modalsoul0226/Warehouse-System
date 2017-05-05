package simulation;

public class Test {
  
    public static double recurse(double first, double last) {
      if (first > last) {
        return 1;
      } else if (first==last){
        return first;
      } else {
        double mid=(first+last)/2;
        double firstHalf=recurse(first,mid);
        double secondHalf=recurse(mid+1,last);
        return firstHalf*secondHalf;
      }        
    }
    
    public static void main(String[] args){
      System.out.println(recurse(1,5));
    }
}
