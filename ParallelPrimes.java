import java.util.concurrent.atomic.AtomicInteger;
public class ParallelPrimes {

    // replace this string with your team name
    public static final String TEAM_NAME = "Benchmark";
    public static final int MAX = Integer.MAX_VALUE;

    public static void optimizedPrimes(int[] primes) {

    int arrayLength = (int)(Math.sqrt(MAX));

    int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    int length = ((arrayLength+NUM_THREADS)/10)*4;
    boolean smallPrimes[] = new boolean[length];
    smallPrimes[0] = true; //2 is prime
    smallPrimes[1] = true; //3 is prime
    smallPrimes[2] = true; //5 is prime
    smallPrimes[3] = true; //7 is prime
    //starting at smallPrimes[4] = X1, smallPrimes[5] = X3, smallPrimes[6] = X7, smallPrimes[7] = X9
    //check X1, X3, X7, X9 up to (Math.sqrt(primes.length)) to see if they're prime.

    int threadDivide = (length/NUM_THREADS);

    Thread[] threads = new Thread[NUM_THREADS];

		// initialize threads with a welcome message
		for (int i = 0; i < NUM_THREADS-1; i++) {
			threads[i] = new Thread(new RThread((i*threadDivide), ((i+1)*threadDivide)-1, smallPrimes));
            threads[i].start();
		}
			threads[NUM_THREADS-1] = new Thread(new RThread(((NUM_THREADS-1)*threadDivide), length-1, smallPrimes));
            threads[NUM_THREADS-1].start();

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
    
    int index = 4;
    primes[0] = 2;
    primes[1] = 3;
    primes[2] = 5;
    primes[3] = 7;
    for(int i=4; i<smallPrimes.length; i++){
        if(smallPrimes[i]){
            int real = 10*(i/4) + 1 + 2*(i % 4);
            if(i % 4 == 2 || i % 4 == 3){
                real += 2;
            }
            primes[index] = real;
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
