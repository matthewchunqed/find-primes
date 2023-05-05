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
        if(this.regionMin % 2 == 0){
            this.regionMin += 1;
        }
        if(this.regionMin % 5 == 0){
            this.regionMin += 2;
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

        System.out.println("regionMin is " + regionMin + " and regionMax is " + regionMax);
        while(regionMin <= regionMax){
            //makes sure the regionMin is X1, X3, X7, X9.
            boolean prime = true;
            for(int i=0; i<end; i++){
                if(regionMin % primes[i] == 0){
                    prime = false;
                    break;
                }
            }
            smallPrimes[regionMin - regionMinCopy] = prime;
            
            if(regionMin % 10 == 3){
                regionMin += 4;
            }else{
                regionMin += 2;
            }
        }
        while(id.get() != uniqueID){
            if(id.get() == uniqueID){
                break;
            }
        }
        int ind = index.get();
        System.out.print("Thread " + uniqueID + " is starting at primes index " + ind);
        for(int i=0; i<smallPrimes.length; i++){
            if(smallPrimes[i]){
                primes[ind] = i+regionMinCopy;
                ind++;
            }
        }
        System.out.println(" and ending at index " + ind);
        index.set(ind);
        id.set(uniqueID+1);
    }
}
