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

    public SThread(int regionMin, int regionMax, int[] primes, AtomicInteger id, int end, int check, int uniqueID, AtomicInteger index) {
        //checks if [regionMin, regionMax] is prime, inclusive of boundaries.
        if(regionMin > check){
		this.regionMin = regionMin;
        }else{
            this.regionMin = check;
        }

        this.regionMinCopy = this.regionMin;
        this.regionMax = regionMax;
        this.primes = primes;
        this.smallPrimes = new boolean[regionMax - regionMinCopy + 1];
        this.id = id;
        this.index = index;
        this.end = end;
        this.uniqueID = uniqueID;
    }

    public void run () {

    for(int i=1; i<end; i++){
        if(i % 2 == 1){
        regionMin = regionMinCopy;
        int remainder = regionMin % primes[i];
        if(remainder > 0){
            regionMin += (primes[i] - remainder);
        }
        if(regionMin % 2 == 0){
            regionMin += primes[i];
        }
        while(regionMin <= regionMax && regionMin > 0){
            smallPrimes[regionMin - regionMinCopy] = true;      
            regionMin = regionMin + primes[i] + primes[i];
        } 
        }else{
            regionMin = regionMax;
            int remainder = regionMin % primes[i];
            if(remainder > 0){
            regionMin -= remainder;
            }
            if(regionMin % 2 == 0){
            regionMin -= primes[i];
            }
        while(regionMin >= regionMinCopy){
            smallPrimes[regionMin - regionMinCopy] = true;      
            regionMin = regionMin - primes[i] - primes[i];
        } 
        } 
    } 
        while(id.get() != uniqueID){
            if(id.get() == uniqueID){
                break;
            }
        }
        int ind = index.get();
        //System.out.print("Thread " + uniqueID + " is starting at primes index " + ind);
        int i = 0;
        if(regionMinCopy % 2 == 0){
            i++;
        }
        while(i<smallPrimes.length){
            if(!smallPrimes[i]){
                primes[ind] = i+regionMinCopy;
                ind++;
            }
            i=i+2;
        }
        //System.out.println(" and ending at index " + ind);
        index.set(ind);
        id.set(uniqueID+1);
    }
}
