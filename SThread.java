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
        if(regionMin > check){
		this.regionMin = regionMin;
        }else{
            this.regionMin = check;
        }
        this.regionMax = regionMax;
        if(this.regionMin % 2 == 0){
            this.regionMin++;
        }
        if(this.regionMax % 2 == 0){
            this.regionMax--;
        }

        this.regionMinCopy = this.regionMin;
        this.primes = primes;
        this.smallPrimes = new boolean[(regionMax - regionMinCopy)/2 + 1];
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
            smallPrimes[(regionMin - regionMinCopy)/2] = true;      
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
            smallPrimes[(regionMin - regionMinCopy)/2] = true;      
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
        for(int i=0; i<smallPrimes.length; i++){
            if(!smallPrimes[i]){
                primes[ind] = i+i+regionMinCopy;
                ind++;
            }
        }
        index.set(ind);
        id.set(uniqueID+1);
    }
}
