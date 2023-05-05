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

        //System.out.println("regionMin is " + regionMin + " and regionMax is " + regionMax);
       /* int cursor;
        for(int i=1; i<end; i++){
            cursor = regionMin;
            if(cursor % primes[i] > 0){
                cursor += (primes[i] - (cursor % primes[i]));
            }
            while(cursor <= regionMax && cursor > 0){
                smallPrimes[cursor - regionMinCopy] = true;
                cursor += primes[i];
            }
        } */
    for(int i=1; i<end; i++){
        regionMin = regionMinCopy;
        while(regionMin % primes[i] > 0){
            regionMin += 1;
        }
        while(regionMin <= regionMax && regionMin > 0){
            //makes sure the regionMin is X1, X3, X7, X9.
            smallPrimes[regionMin - regionMinCopy] = true;      
            regionMin += primes[i];
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
