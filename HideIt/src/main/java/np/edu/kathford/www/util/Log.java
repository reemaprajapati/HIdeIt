package np.edu.kathford.www.util;

public class Log {
    public static Boolean debug = false;
    public static void d(String message){
        if(debug){
            System.out.println(message);
        }
    }
}
