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
        int length = (((arrayLength/10) << 2) >> 6)+1;
        long smallPrimes[] = new long[length];
        smallPrimes[0] = 0b1111; primes[0] = 2; primes[1] = 3; primes[2] = 5; primes[3] = 7;
        //2, 3, 5, 7 prime. hard coded before checking X1/X3/X7/X9 elements

        int threadDivide = (((arrayLength/10) << 2)/NUM_THREADS);
        
        Thread[] threads = new Thread[NUM_THREADS];
		// initialize threads
		for (int i = 0; i < NUM_THREADS-1; i++) {
			threads[i] = new Thread(new RThread((i*threadDivide), ((i+1)*threadDivide)-1, smallPrimes));
            threads[i].start();
		}
			threads[NUM_THREADS-1] = new Thread(new RThread(((NUM_THREADS-1)*threadDivide), ((arrayLength/10) << 2), smallPrimes));
            threads[NUM_THREADS-1].start();

        //wait for threads to complete
        try{
            for(int i=0; i<NUM_THREADS; i++){
                threads[i].join();
            }
        } catch (InterruptedException e) {
            } 

        int count = 4;
        long curr = smallPrimes[0];
        //apply compression bijection: smallPrimesOld[i] = smallPrimes[(i >> 5) << (i&0b11111)]
        for(int j=4; j<64; j++){
            if(((curr >> j) & 0b1) == 1){
                primes[count] = 10*(j >> 2) + 1 + ((j & 0b11) << 1) + (((j&0b11)>>1) << 1);
                count++; 
            }
        }
        for(int i=1; i<smallPrimes.length; i++){
            curr = smallPrimes[i];
            for(int j=0; j<64; j++){
            if(((curr >> j) & 0b1) == 1){
                primes[count] = 10*(((i << 6)+j) >> 2) + 1 + ((((i << 6)+j) & 0b11) << 1) + (((((i << 6)+j)&0b11)>>1) << 1);
                count++; 
            }
            }
        }
        
        // Number of threads available for use
        int N_THREADS = Runtime.getRuntime().availableProcessors();
        if(N_THREADS > 100){
            N_THREADS = 70;
        }

        ExecutorService pool = Executors.newFixedThreadPool(N_THREADS);
        //creates a pool of threads that will jointly work on the tasks
        ArrayList<Future<int[]>> results = new ArrayList<>(N_TASKS);
        //creates a task board for completion by thread pool
        for (long curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += (ROOT_MAX * 64)) {
            //add the results to the Futures and wait for them to finish
            results.add(pool.submit(new findPrimes(primes, count, (int) curBlock)));
        }
        try {
            //pull the results from the Futures and add them to primes.
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
    PARAMS:
    boolean[] isPrime: True if a number is prime, False if composite
    int[] smallPrimes: An array of primes, from 1 to 46340
    int curBlock: The offset, so that we know where to start from
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
        //isPrime[i] = regionMin + 2*i
    }

    //given the class variables, return a result array of primes.
    @Override
    public int[] call() {

    int counter = smallPrimes.length;
    int remainder;
    int cursor; 
    //loops in alternating directions for spatial locality (lines 39 & 52)
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
        /*
        above sets regionMin pointer to be an odd multiple of prime[i].
        below code applies sieve to all future odd multiples of prime[i].
        */
        while(regionMin <= regionMax && regionMin > 0){
            if(!smallPrimes[(regionMin - regionMinCopy) >> 1]){
                smallPrimes[(regionMin - regionMinCopy) >> 1] = true;  
                counter--;
            }    
            regionMin = regionMin + (cursor << 1);
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
        /*
        above sets regionMin pointer to be an odd multiple of prime[i].
        below code applies sieve to all future odd multiples of prime[i].
        */
        while(regionMin >= regionMinCopy){
            if(!smallPrimes[(regionMin - regionMinCopy) >> 1]){
                smallPrimes[(regionMin - regionMinCopy) >> 1] = true;  
                counter--;
            }    
            regionMin = regionMin - (cursor << 1);
        } 
        } 
    }

    int res[] = new int[counter];
    int ind = 0;
    int store = regionMinCopy;
    //dumps the result into res[] array which gets merged into Primes[] by Future.
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
