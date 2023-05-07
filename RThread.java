public class RThread implements Runnable {

    private int regionMin;
    private int regionMax;
    private boolean[] smallPrimes;

    public RThread(int regionMin, int regionMax, boolean[] smallPrimes) {
		this.regionMin = regionMin;
        this.regionMax = regionMax;
        this.smallPrimes = smallPrimes;
    }

    public void run () {

        if(regionMin < 4){
            regionMin = 4;
        }
        int real;
        boolean prime;
        //makes sure the regionMin is X1, X3, X7, X9, then computes the prime for the corresponding smallPrimes[i].
        while(regionMin <= regionMax){
            real = 10*(regionMin/4) + 1 + 2*(regionMin % 4);
            if(regionMin % 4 == 2 || regionMin % 4 == 3){
                real += 2;
            }
            prime = false;
            for(int i=3; i<=real; i=i+2){
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
