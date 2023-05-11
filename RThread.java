public class RThread implements Runnable {

    private int regionMin;
    private int regionMax;
    private int regionMinCopy;
    private boolean[] smallPrimes;
    private int[] primes;

    public RThread(int regionMin, int regionMax, boolean[] smallPrimes, int[] primes) {
		this.regionMin = regionMin;
        this.regionMax = regionMax;
        this.smallPrimes = smallPrimes;
        this.primes = primes;
    }

    public void run () {

        if(regionMin < 4){
            regionMin = 4;
        }
        regionMinCopy = regionMin;
        int real;
        boolean prime;
        //makes sure the regionMin is X1, X3, X7, X9, then computes the prime for the corresponding smallPrimes[i].
        while(regionMin <= regionMax){
                           
            real = 10*(regionMin >> 2) + 1 + ((regionMin & 0b11) << 1) + (regionMin&0b11 >>1 << 1);

            prime = false;
            //above applies array bijection -> X1/X3/X7/X9, and below checks all factors
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
        int count;
        if(regionMinCopy < 10){
            count = 1392;
        }else if(regionMinCopy < 5000){
            count = 2584;
        }
        else if(regionMinCopy < 10000){
            count = 3710;
        }else{
            count = 4791;
        }
        for(int i=regionMax-1; i>=regionMinCopy; i--){
            if(smallPrimes[i]){
                primes[count] = 10*(i >> 2) + 1 + ((i & 0b11) << 1) + (((i&0b11)>>1) << 1);
                count--; 
            }
        }
    }
}
