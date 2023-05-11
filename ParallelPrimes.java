import java.util.ArrayList;
import java.util.concurrent.*;

public class ParallelPrimes {

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int ROOT_MAX = Primes.ROOT_MAX;
    public static final String TEAM_NAME = "Benchmark";

    // Number of tasks
    public static final int N_TASKS = MAX_VALUE / (ROOT_MAX * 64);

    public static void optimizedPrimes(int[] primes) {

        int NUM_THREADS = 4;

        int arrayLength = (int)(Math.sqrt(MAX_VALUE));
        int length = ((arrayLength/10) << 2);
        boolean smallPrimes[] = new boolean[length];
        smallPrimes[0] = true; primes[0] = 2; 
        smallPrimes[1] = true; primes[1] = 3; 
        smallPrimes[2] = true; primes[2] = 5; 
        smallPrimes[3] = true; primes[3] = 7; 
        //2, 3, 5, 7 prime. hard code before checking X1/X3/X7/X9 elements >10.

        int threadDivide = (length/NUM_THREADS);
        
        Thread[] threads = new Thread[NUM_THREADS];
		// initialize threads
		for (int i = 0; i < NUM_THREADS-1; i++) {
			threads[i] = new Thread(new RThread((i*threadDivide), ((i+1)*threadDivide)-1, smallPrimes));
            threads[i].start();
		}
			threads[NUM_THREADS-1] = new Thread(new RThread(((NUM_THREADS-1)*threadDivide), length-1, smallPrimes));
            threads[NUM_THREADS-1].start();

        //wait for threads to complete
        try{
            for(int i=0; i<NUM_THREADS; i++){
                threads[i].join();
            }
        } catch (InterruptedException e) {
            } 

        //populate primes[] with the results from smallPrimes.
        int real;
        int count = 4;
        for(int i=4; i<smallPrimes.length; i++){
            if(smallPrimes[i]){
               real = 10*(i >> 2) + 1 + ((i & 0b11) << 1) + (((i&0b11)>>1) << 1);
                primes[count] = real;
                count++; 
            }
        }
        
        int N_THREADS = Runtime.getRuntime().availableProcessors();
        if(N_THREADS > 100){
            N_THREADS = 70;
        }

        //create a pool and a task board for the primes
        ExecutorService pool = Executors.newFixedThreadPool(N_THREADS);
        ArrayList<Future<int[]>> results = new ArrayList<>(N_TASKS);

        //add the results to the Futures and wait for them to finish
        for (long curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += (ROOT_MAX * 64)) {
            results.add(pool.submit(new findPrimes(primes, count, (int) curBlock)));
        }
        try {
            //pull the results from the Futures and merge them into primes.
            for (Future<int[]> result : results) {
                int[] component = result.get();
                for (int i = 0; i < component.length; i++) {
                    primes[count++] = component[i];
                }
            }
        } catch (Exception e) {
        }
        pool.shutdown();
    }
}

class findPrimes implements Callable<int[]> {
    private int[] primes;
    private int regionMin;
    private int regionMinCopy;
    private int regionMax;
    private boolean[] smallPrimes;
    private int end;

    /*
    defines the regionMin and regionMax of ints to be checked for prime candidacy
    and "end", which denotes to the thread where the smallPrimes list ends in primes[]
     */
    public findPrimes(int[] primes, int end, int regionMin) {
        this.regionMin = regionMin;
        this.regionMax = this.regionMin + (ParallelPrimes.ROOT_MAX * 64) - 1;
        if(regionMax <= 0){
            regionMax = Integer.MAX_VALUE;
        }
        if((this.regionMin & 0b1) == 0){
            this.regionMin++;
        }
        if((this.regionMax & 0b1) == 0){
            this.regionMax++;
        }
        this.regionMinCopy = this.regionMin;
        this.primes = primes;
        this.smallPrimes = new boolean[((regionMax - regionMinCopy) >> 1) + 1];
        this.end = end;
    }

    @Override
    public int[] call() {

    int counter = smallPrimes.length;
    int remainder;
    int cursor; 
    //loops in alternating directions for spatial locality
    for(int i=1; i<end; i++){
        cursor = primes[i];
        if((i & 0b1) == 1){
        regionMin = regionMinCopy;
        remainder = regionMin % cursor;
        if(remainder > 0){
            regionMin += (cursor - remainder);
        }
        if((regionMin & 0b1) == 0){
            regionMin += cursor;
        }
        /*above sets regionMin pointer to be an odd multiple of prime[i].
        below code applies the sieve to all future odd multiples of prime[i] to mark them off.
        */
        while(regionMin <= regionMax && regionMin > 0){
            if(!smallPrimes[(regionMin - regionMinCopy) >> 1]){
                smallPrimes[(regionMin - regionMinCopy) >> 1] = true;  
                counter--;
            }    
            regionMin = regionMin + cursor + cursor;
        } 
        }else{
            regionMin = regionMax;
            remainder = regionMin % cursor;
            if(remainder > 0){
            regionMin -= remainder;
            }
            if((regionMin & 0b1) == 0){
            regionMin -= cursor;
            }
        /*above sets regionMin pointer to be an odd multiple of prime[i].
        below code applies the sieve to all future odd multiples of prime[i] to mark them off.
        */
        while(regionMin >= regionMinCopy){
            if(!smallPrimes[(regionMin - regionMinCopy) >> 1]){
                smallPrimes[(regionMin - regionMinCopy) >> 1] = true;  
                counter--;
            }    
            regionMin = regionMin - cursor - cursor;
        } 
        } 
    }

    int res[] = new int[counter];
    int ind = 0;
    int store = regionMinCopy;
    //dumps the results into a res[] array which will be merged by the Futures.
    for(int i=0; i<smallPrimes.length; i++){
        if(!smallPrimes[i]){
            res[ind] = store;
            ind++;
        }
            store += 2;
    }
    return res;

    }
}
