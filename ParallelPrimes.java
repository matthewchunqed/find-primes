import java.util.ArrayList;
import java.util.concurrent.*;

public class ParallelPrimes {

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int ROOT_MAX = Primes.ROOT_MAX;
    // replace this string with your team name
    public static final String TEAM_NAME = "Benchmark";
    // Number of threads available for use
    public static final int N_THREADS = Runtime.getRuntime().availableProcessors();
    // Number of tasks, should be 46341
    public static final int N_TASKS = MAX_VALUE / (ROOT_MAX * 64);

    public static void optimizedPrimes(int[] primes) {

        int NUM_THREADS = 4;

        int arrayLength = (int)(Math.sqrt(MAX_VALUE));
        int length = (arrayLength/10)*4;
        boolean smallPrimes[] = new boolean[length];
        smallPrimes[0] = true; primes[0] = 2; //2 is prime
        smallPrimes[1] = true; primes[1] = 3; //3 is prime
        smallPrimes[2] = true; primes[2] = 5; //5 is prime
        smallPrimes[3] = true; primes[3] = 7; //7 is prime

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

        int real;
        int count = 4;
        for(int i=4; i<smallPrimes.length; i++){
            if(smallPrimes[i]){
               real = 10*(i/4) + 1 + 2*(i % 4);
                if(i % 4 == 2 || i % 4 == 3){
                    real += 2;
                }
                primes[count] = real;
                count++; 
            }
        }

        // We don't need to compute small primes since it is hard-coded
//        int[] smallPrimes = Primes.getSmallPrimes();
        int nPrimes = primes.length; // Should be the same as N_PRIMES (105_097_565)

        // check if we've already filled primes, and return if so
//        if (nPrimes == minSize) {
//            return;
//        }

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
                for (int i = 0; i < component.length && count < nPrimes; i++) {
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
