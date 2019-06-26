package tools;

/**
 * Created by user on 3/10/17.
 */
public class sets {
    public static boolean firstinit =true;
    public static  void init(){
        if (firstinit){
            firstinit=false;
            String ss=functions.file2str("/opt/cc_crm.conf");
            db_stringnewcc=functions.getparam(ss,"db_stringnewcc",db_stringnewcc);
            db_usernewcc=functions.getparam(ss,"db_usernewcc",db_usernewcc);
            db_passnewcc=functions.getparam(ss,"db_passnewcc",db_passnewcc);
            debug=functions.getparam(ss,"debug","").equals("true");
            messagestring=functions.getparam(ss,"messagestring",messagestring);
            System.out.println("db_stringnewcc="+db_stringnewcc);
            System.out.println("db_usernewcc="+db_usernewcc);
            System.out.println("debug="+debug);
            System.out.println("messagestring="+messagestring);

        }
    }
    public static boolean debug=false;
    public static final String db_driver = "org.postgresql.Driver";



    public static final String db_stringnew = "jdbc:postgresql://192.168.1.63:5432/callcenter";
    public static final String db_stringbackup = "jdbc:postgresql://192.168.1.69:5432/callcenter";
    public static final String db_stringgwt = "jdbc:postgresql://192.168.18.43:5432/gwtadmin";


    public static final String db_user = "cc_crm";
    public static final String db_pass = "";


    public static  String db_stringnewcc = "111111111111111jdbc:postgresql://192.168.27.30:5432/cc_crm";
    public static  String db_usernewcc = "cc_crm";
    public static  String db_passnewcc = "";
    public static  String messagestring = "ws://127.0.0.1:9080/CallCenter/message";


    public static String db_drivermysql = "com.mysql.jdbc.Driver";
    public static String db_string12 = new String("11111111111111jdbc:mysql://192.168.27.12:3306/");
    public static String db_user12 = new String("ccmaster-manage");
    public static String db_pass12 = new String("uH8Zj23Ha8mN");

    public static String db_string27 = new String("jdbc:mysql://192.168.27.27:3306/");
    public static String db_user27 = new String("oldcc-manage-m");
    public static String db_pass27 = new String("Z9VhUKY3aDTXfg4B");

    public static final int   mobile        =0;
    public static final int   gov           =2;
    public static final int   magtisat      =13;
    public static final int   magtifix      =3;
    public static final int   marketing     =5;
    public static final int   nophone     =-1;

    public static final int   SendRingFile  =200;
    public static final int   LOGIN 		=201;
    public static final int   BUSY		=202;
    public static final int   READY		=203;
    public static final int   RINGING	=204;
    public static final int   REST		=205;
    public static final int   ANSWER	=206;
    public static final int   CON		=207;
    public static final int   TERMINATE	=208;
    public static final int   END		=209;
    public static final int   PlayWtOpFile  =210;
    public static final int   PlayMenu      =211;
    public static final int OFFBUTTON = 212;

    public static final int   RESTWARNING		=301;

    public static int RestFullTime=1800;
    public static int RestWarnTime=600;
}