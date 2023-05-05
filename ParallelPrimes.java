import java.util.concurrent.atomic.AtomicInteger;
public class ParallelPrimes {

    // replace this string with your team name
    public static final String TEAM_NAME = "Benchmark";
    public static final int MAX = Integer.MAX_VALUE;

    public static void optimizedPrimes(int[] primes) {

    int arrayLength = (int)(Math.sqrt(MAX));

    int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    boolean smallPrimes[] = new boolean[arrayLength+NUM_THREADS];
    smallPrimes[2] = true; //2 is prime
    smallPrimes[3] = true; //3 is prime
    smallPrimes[5] = true; //5 is prime
    smallPrimes[7] = true; //7 is prime
    //check X1, X3, X5, X7 up to (Math.sqrt(primes.length)) to see if they're prime.

    int threadDivide = ((arrayLength)/NUM_THREADS)+1;

    Thread[] threads = new Thread[NUM_THREADS];

		// initialize threads with a welcome message
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new Thread(new RThread((i*threadDivide), ((i+1)*threadDivide)-1, smallPrimes));
            threads[i].start();
		}

        try{
            for(int i=0; i<NUM_THREADS; i++){
                threads[i].join();
            }
        } catch (InterruptedException e) {
            } 
        

    int check = arrayLength;

    if(check % 2 == 0){
        check += 1;
    }
    if(check % 5 == 0){
        check += 2;
    }
    
    int index = 0;
    for(int i=2; i<smallPrimes.length; i++){
        if(smallPrimes[i]){
            primes[index] = i;
            index++; 
        }
    }
    int end = index;

    AtomicInteger id = new AtomicInteger(0);
    AtomicInteger ind = new AtomicInteger(end);

    threadDivide = (MAX/NUM_THREADS)+1;

    for (int i = 0; i < NUM_THREADS; i++) {
        int max = ((i+1)*threadDivide)-1;
        if(max < 0){
            max = MAX;
        }
        threads[i] = new Thread(new SThread((i*threadDivide), max, primes, id, end, check, i, ind));
        threads[i].start();
    }

	return;
	
    }
}
