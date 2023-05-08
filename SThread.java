import java.util.concurrent.atomic.AtomicInteger;
public class SThread implements Runnable {

    private int regionMin;
    private int regionMax;
    private int[] primes;
    private AtomicInteger id;
    private AtomicInteger index;
    private int regionMinCopy;
    private int end;
    private int uniqueID;
    private boolean[] smallPrimes;

    public SThread(int regionMin, int regionMax, int[] primes, AtomicInteger id, int end, int uniqueID, AtomicInteger index) {
		this.regionMin = regionMin;
        this.regionMax = regionMax;
        if(this.regionMin % 2 == 0){
            this.regionMin++;
        }
        if(this.regionMax % 2 == 0){
            this.regionMax--;
        }
        //makes sure region boundaries are odd, to create the array pattern search <-> odd array bijection

        this.regionMinCopy = this.regionMin;
        this.primes = primes;
        this.smallPrimes = new boolean[(regionMax - regionMinCopy)/2 + 1];
        this.id = id;
        this.index = index;
        this.end = end;
        this.uniqueID = uniqueID;
    }

    public void run () {

    int remainder;
    //loops in alternating directions for spatial locality (lines 39 & 52)
    for(int i=1; i<end; i++){
        if(i % 2 == 1){
        regionMin = regionMinCopy;
        remainder = regionMin % primes[i];
        if(remainder > 0){
            regionMin += (primes[i] - remainder);
        }
        if(regionMin % 2 == 0){
            regionMin += primes[i];
        }
        while(regionMin <= regionMax && regionMin > 0){
            smallPrimes[(regionMin - regionMinCopy)/2] = true;      
            regionMin = regionMin + primes[i] + primes[i];
        } 
        }else{
            regionMin = regionMax;
            remainder = regionMin % primes[i];
            if(remainder > 0){
            regionMin -= remainder;
            }
            if(regionMin % 2 == 0){
            regionMin -= primes[i];
            }
        while(regionMin >= regionMinCopy){
            smallPrimes[(regionMin - regionMinCopy)/2] = true;      
            regionMin = regionMin - primes[i] - primes[i];
        } 
        } 
    }
    /*the above code works by applying the sieve repeatedly, skipping any even number checks. Note that if
    We deliberately set smallPrimes to only represent the odd numbers. We first aligned the regionMin cursor with a multiple of primes[i].
    (which is on line 40-47 and 53-60)
    After aligning with multiples of primes[i], we mark every second instance of primes[i] (e.g. mark 3, 9, 15, etc. for primes[i] = 3), because our array only represents odd numbers.
    This occurs on lines 48-51 and 61-64.

    (There was a slowdown observed when comments were placed in the loop, so comments are out here.)
    */

        while(id.get() != uniqueID){
            if(id.get() == uniqueID){
                break;
            }
        }
        //waits for thread i's chance to go, then dumps in its results in the prime numbers into primes[] after thread i-1 and before thread i+1. starts depositing at index ind.get(), then updates ind at the end.
        int ind = index.get();
        int store = regionMinCopy;
        for(int i=0; i<smallPrimes.length; i++){
            if(!smallPrimes[i]){
                primes[ind] = store;
                ind++;
            }
            store += 2;
        }
        index.set(ind);
        id.set(uniqueID+1);
        //permits the next thread to access primes[].
    }
}
