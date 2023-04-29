public class ParallelPrimes {

    // replace this string with your team name
    public static final String TEAM_NAME = "Benchmark";
    public static final int MAX = Integer.MAX_VALUE;
    //public static final int MAX = Integer.MAX_VALUE;

    public static void optimizedPrimes(int[] primes) {

    int arrayLength = (int)(Math.sqrt(MAX));

    boolean smallPrimes[] = new boolean[arrayLength];
    smallPrimes[2] = true; //2 is prime
    smallPrimes[3] = true; //3 is prime
    smallPrimes[5] = true; //5 is prime
    smallPrimes[7] = true; //7 is prime
    //check X1, X3, X5, X7 up to (Math.sqrt(primes.length)) to see if they're prime.
    //parallelize this, then do the bigger numbers sequentially.
    	
    int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    int threadDivide = ((arrayLength-11)/NUM_THREADS);

    Thread[] threads = new Thread[NUM_THREADS];

		// initialize threads with a welcome message
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new Thread(new RThread(11+(i*threadDivide), 11+((i+1)*threadDivide)-1, smallPrimes));
            threads[i].start();
		}

        try{
            for(int i=0; i<NUM_THREADS; i++){
                threads[i].join();
            }
        } catch (InterruptedException e) {
                // handle the exception
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
    
    /*for(int i=0; i<end; i++){
        System.out.print("primes[" + i + "] is " + primes[i] + ", ");
    } */

	while(check < MAX && check > 0){
        
        boolean prime = true;
        for(int i=0; i<end; i++){
            if(check % primes[i] == 0){
                prime = false;
                break;
            }
        }
        if(prime){
            primes[index] = check;
            index++;
        }
        if(check % 10 == 3){
            check += 4;
        }else{
            check += 2;
        }
    }

    
	return;

    // replace this with your optimized method
	//Primes.baselinePrimes(primes);
	
    }
}
