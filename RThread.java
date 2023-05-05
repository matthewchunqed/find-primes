public class RThread implements Runnable {

    private int regionMin;
    private int regionMax;
    private boolean[] smallPrimes;

    public RThread(int regionMin, int regionMax, boolean[] smallPrimes) {
        //checks if [regionMin, regionMax] is prime, inclusive of boundaries.
		this.regionMin = regionMin;
        this.regionMax = regionMax;
        this.smallPrimes = smallPrimes;
    }

    public void run () {

        if(regionMin < 11){
            regionMin = 11;
        }else{

        if(regionMin % 2 == 0){
            regionMin += 1;
        }
        if(regionMin % 5 == 0){
            regionMin += 2;
        }

    }
       // System.out.println("regionMin is " + regionMin + " and regionMax is " + regionMax);
        while(regionMin <= regionMax){
            //makes sure the regionMin is X1, X3, X7, X9.
            int div = 2;
            for(int i=3; i<=regionMax; i++){
                if(regionMin % i == 0){
                    div = i;
                    break;
                }
            }

            if(regionMin == div){
                smallPrimes[regionMin] = true;
            }
            if(regionMin % 10 == 3){
                regionMin += 4;
            }else{
                regionMin += 2;
            }
        }

    }
}
