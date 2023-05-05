public class RThread implements Runnable {

    private int regionMin;
    private int regionMax;
    private boolean[] smallPrimes;

    public RThread(int regionMin, int regionMax, boolean[] smallPrimes) {
		this.regionMin = regionMin;
        this.regionMax = regionMax;
        this.smallPrimes = smallPrimes;
    }
    //smallPrimes[4] = 11, smallPrimes[5] = 13, smallPrimes[6] = 17, smallPrimes[7] = 19
    //smallPrimes[8] = 21, smallPrimes[9] = 23, smallPrimes[10] = 27, smallPrimes[11] = 29
    public void run () {

        if(regionMin < 4){
            regionMin = 4;
        }
       // System.out.println("regionMin is " + regionMin + " and regionMax is " + regionMax);
        int real;
        boolean prime;
        while(regionMin <= regionMax){
            //makes sure the regionMin is X1, X3, X7, X9.
            real = 10*(regionMin/4) + 1 + 2*(regionMin % 4);
            if(regionMin % 4 == 2 || regionMin % 4 == 3){
                real += 2;
            }
            prime = false;
            for(int i=3; i<=real; i++){
                if(real % i == 0){
                    break;
                }
                if(i*i > real){
                    prime = true;
                    break;
                }
            }

            smallPrimes[regionMin] = prime;
            
            regionMin++;
        }

    }
}
