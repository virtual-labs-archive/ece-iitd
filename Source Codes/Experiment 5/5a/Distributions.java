import java.util.Random;

public class Distributions {

    /**
     * Return real number uniformly in [0, 1).
     */
    public static double uniform() {
        return Math.random();
    }

    /**
     * Return real number uniformly in [a, b).
     */
    public static double uniform(double a, double b) {
        return a + Math.random() * (b - a);
    }

    /**
     * Return an integer uniformly between 1 and N.
     */
    public static int uniform(int N) {
        return (int) (Math.random() * N) + 1;
    }

    /**
     * Return a boolean, which is true with probability p, and false otherwise.
     */
    public static boolean bernoulli(double p) {
        return Math.random() < p;
    }

    /**
     * Return a boolean, which is true with probability .5, and false otherwise.
     */
    public static boolean bernoulli() {
        return bernoulli(0.5);
    }

    public static int Binomial(int n, double p) {
        int x = 0;
        for (int i = 0; i < n; i++) {
            if (Math.random() < p) {
                x++;
            }
        }
        return x;
    }

    /**
     * Return an integer with a geometric distribution with mean 1/p.
     */
    public static int geometric(double p) {

        return (int) Math.ceil(Math.log(uniform()) / Math.log(1.0 - p));
    }

    /**
     * Return an integer with a Poisson distribution with mean lambda.
     */
    public static int poisson(double lambda) {

        int k = 0;
        double p = 1.0;
        double L = Math.exp(-lambda);
        do {
            k++;
            p *= uniform();
        } while (p >= L);
        return k - 1;
    }

    /**
     * Return a number from a discrete distribution: i with probability a[i].
     */
    public int discrete(double[] a) {
        // precondition: sum of array entries equals 1
        double r = Math.random();
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
            if (sum >= r) {
                return i;
            }
        }
        assert (false);
        return -1;
    }

    /**
     * Return a real number from an exponential distribution with rate lambda.
     */
    public double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }


    // swaps array elements i and j
    private void exch(String[] a, int i, int j) {
        String swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    /**
     * Rearrange the elements of an array in random order.
     */
    public void shuffle(Object[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N - i);     // between i and N-1

            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of a double array in random order.
     */
    public void shuffle(double[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N - i);     // between i and N-1

            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of an int array in random order.
     */
    public void shuffle(int[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N - i);     // between i and N-1

            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public void shuffle(Object[] a, int lo, int hi) {
        if (lo < 0 || lo > hi || hi >= a.length) {
            throw new RuntimeException("Illegal subarray range");
        }
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi - i + 1);     // between i and hi

            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public void shuffle(double[] a, int lo, int hi) {
        if (lo < 0 || lo > hi || hi >= a.length) {
            throw new RuntimeException("Illegal subarray range");
        }
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi - i + 1);     // between i and hi

            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public void shuffle(int[] a, int lo, int hi) {
        if (lo < 0 || lo > hi || hi >= a.length) {
            throw new RuntimeException("Illegal subarray range");
        }
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi - i + 1);     // between i and hi

            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }
    
    public static double Gaussian(double a,double sigma){
        Random r = new Random();
        double g = r.nextGaussian();
        return (sigma*g + a);
    }
}
