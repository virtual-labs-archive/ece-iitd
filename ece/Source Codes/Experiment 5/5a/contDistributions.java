import java.util.*;

public class contDistributions {

    public static double Gaussian(double a, double sigma) {
        Random r = new Random();
        double g = r.nextGaussian();
        return (sigma * g + a);
    }

    public static double Uniform(double a, double b) {
        Random generator = new Random();
        double r = generator.nextDouble();
        return a + (b - a) * r;

    }

    public static double Deterministic(double a) {
        return a;
    }
    
    public static double Weibull(double a,double b){
        Random generator = new Random();
        return a*Math.pow(-generator.nextDouble(),1/b);
    }
    
    public static double Pereto(double a , double b){
        Random generator = new Random();
        return b*Math.pow(generator.nextDouble(),-1.0/a);
    }
    
    public static double Gamma(double k, double lambda){
        Random generator = new Random();
        double p=1.0;
        for( int i=1; i<=k ; i++){
            p=p*generator.nextDouble();
        }
        return -Math.log(p)/lambda;
    }
    
    public static double Erlang(int k, double lambda ){
        Random generator = new Random();
        double p=1.0;
        for( int i=1; i<=k ; i++){
            p=p*generator.nextDouble();
        }
        return -Math.log(p)/lambda;
    }
    
    public static double LogNormal(double mu, double sigma2){
        Random generator = new Random();
        double p=1.0;
        for( int i=1; i<=12 ; i++){
            p=p*generator.nextDouble();
        }
        return Math.exp(mu)*Math.exp(sigma2*(p-6.0));
    }               
}