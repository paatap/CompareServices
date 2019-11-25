/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author paatap
 */
@WebServlet(name = "mainService", urlPatterns = {"/mainService"})
public class mainService extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

//response.getWriter().write(tools.SendEmailTLS.SendEmailTLS());
        try {

            BufferedReader br = request.getReader();
            System.out.println("----BODY-----");
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                byte[] bytes = line.getBytes(StandardCharsets.ISO_8859_1);
                line = new String(bytes, StandardCharsets.UTF_8);
                System.out.println(line);
                result += line + "\n";
            }

            System.out.println("111111111" + result);
            JsonElement el = new JsonParser().parse(result);
            System.out.println("22222");
            JsonObject job = el.getAsJsonObject();
            System.out.println("333");
            String command = tools.functions.jsonget(job, "command");
            System.out.println("command=" + command);

            if (command.equals("login")) {
// login
                String user = tools.functions.jsonget(job, "user");
                System.out.println("user=" + user);
                String pass = tools.functions.jsonget(job, "pass");
                System.out.println("pass=" + pass);
                String qwr = "select u.id,name_first,name_last from users u left join crm_contact cc on u.id=cc.userid where username='" + user + "'  and password='" + pass + "'";

                /*
                InitialContext ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/workflow");
                conn = ds.getConnection();
                statement = conn.createStatement();
                //           resultSet = statement.executeQuery("select 'hhhaaaaaaa' as ttt");
                String qwr="select u.id,name_first,name_last from users u left join crm_contact cc on u.id=cc.userid where username='"+user+"'  and password='"+pass+"'";
                System.out.println(qwr);
                resultSet = statement.executeQuery(qwr);
                while (resultSet.next()) {
                    response.getWriter().write(command + " " + resultSet.getString("name_first")+","+resultSet.getString("name_last"));
                    System.out.println(command + " " + resultSet.getString("name_first")+","+resultSet.getString("name_last"));
                    
                }
                 */
                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"login\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + s2.get(0)[0] + "\",\n"
                            + "\"namefirst\":\"" + s2.get(0)[1] + "\",\n"
                            + "\"namelast\":\"" + s2.get(0)[2] + "\"\n}";
                } else {
                    ss = "{\n\"command\":\"login\",\n"
                            + "\"result\":\"usernotfound\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);
            } else if (command.equals("register")) {

// register NameFirst NameLast email etc ...               
                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String namefirstlat = tools.functions.jsonget(job, "namefirstlat");
                System.out.println("namefirstlat=" + namefirstlat);

                String namelastlat = tools.functions.jsonget(job, "namelastlat");
                System.out.println("namelastlat=" + namelastlat);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday=" + birthday);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String phonepre = tools.functions.jsonget(job, "phonepre");
                System.out.println("phonepre=" + phonepre);

                String qwr = "Insert into users (username) values ('" + email + "') returning id ;";

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                System.out.println("1.   s1=" + s1.get(0)[0]);

                qwr = "insert into crm_contact  (name,email,userid,pid,contact_type_id,name_first,name_last,gender,birthday,phone,phonepre,name_first_lat,name_last_lat)"
                        + "values('" + namefirst + " " + namelast + "','" + email + "'," + s1.get(0)[0] + ",'" + personal_n + "',1,'"
                        + namefirst + "','" + namelast + "','" + gender + "','" + birthday + "','" + phone + "','" + phonepre
                        + "','" + namefirstlat + "','" + namelastlat + "') returning id;";

                System.out.println(qwr);

                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                System.out.println("2 s2=     " + s2.get(0)[0]);

                qwr = "insert into msg_link (userid) values (" + s1.get(0)[0] + ")  returning id; ";

                ArrayList<String[]> s3 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                System.out.println("3.   s3=" + s3.get(0)[0]);
                System.out.println(qwr);

                qwr = "select linkmd5 from msg_link where id= " + s3.get(0)[0];

                ArrayList<String[]> s4 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                System.out.println("4.   s4=" + s4.get(0)[0]);
                System.out.println(qwr);

                String[] args = null;

                String subTxt = "მოგესალმებათ info@compare.ge /Greeting from info@compare.ge";

                String msgTxt = "compare.ge გთხოვთ გადახვიდეთ ლინკზე/please folow to link"
                        + " http://192.168.18.22:9080/myweb1?register=" + s4.get(0)[0]
                        + "\n\n ლინკი აქტიურია 1 საათის განმავლობაში/ Link is valid 1 Hour "
                        + "\n\n compare.ge Please do not spam my email!";

                Thread thread = new Thread(new Runnable() {

                    public void run() {
                        System.out.println("kuku");
                        tools.SendEmailTLS.SendEmailTLS(email, subTxt, msgTxt);
                        System.out.println("register  mail Sent");

                    }

                });
                thread.start();

                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"register\",\n"
                            + "\"result\":\"sendmail\"\n}";

                } else {
                    ss = "{\n\"command\":\"register\",\n"
                            + "\"result\":\"userexist\"\n}";
                }
                response.getWriter().write(ss);

            } else if (command.equals("register2")) {

                String linkmd5 = tools.functions.jsonget(job, "link");
                System.out.println("linkmd5=" + linkmd5);

                String qwr = "select cc.userid,cc.name_first,cc.name_last from msg_link ml\n"
                        + "left join crm_contact cc on ml.userid=cc.userid \n"
                        + "  where linkmd5='" + linkmd5 + "' and not (now()>end_date) ";

                System.out.println(qwr);
                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                String ss;

                if (s1.size() > 0) {

                    ss = "{\n\"command\":\"register2\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + s1.get(0)[0] + "\",\n"
                            + "\"namefirst\":\"" + s1.get(0)[1] + "\",\n"
                            + "\"namelast\":\"" + s1.get(0)[2] + "\"\n}";

                    System.out.println("id=" + s1.get(0)[0]);
                    System.out.println("namefirst=" + s1.get(0)[1]);
                    System.out.println("namelast=" + s1.get(0)[2]);

                } else {
                    ss = "{\n\"command\":\"register2\",\n"
                            + "\"result\":\"linknotfound\"\n}";
                    System.out.println(s1.size());

                }
                response.getWriter().write(ss);

            } else if (command.equals("register3")) {

// set password from link
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);
                String pass = tools.functions.jsonget(job, "pass");
                System.out.println("pass=" + pass);

                String qwr = "update users set password='" + pass + "' where id=" + userid + ";";

                System.out.println(qwr);
                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                qwr = "select u.id,cc.name_first,cc.name_last,u.username from users u left join crm_contact cc on u.id=cc.userid where userid='" + userid + "'  and password='" + pass + "'";

                System.out.println(qwr);

                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"register3\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + s2.get(0)[0] + "\",\n"
                            + "\"namefirst\":\"" + s2.get(0)[1] + "\",\n"
                            + "\"namelast\":\"" + s2.get(0)[2] + "\",\n"
                            + "\"username\":\"" + s2.get(0)[3] + "\"\n}";
                } else {
                    ss = "{\n\"command\":\"register3\",\n"
                            + "\"result\":\"usernotfound\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);
            } else if (command.equals("changepassword")) {

// change password
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);
                String passold = tools.functions.jsonget(job, "passold");
                System.out.println("passold=" + passold);
                String pass = tools.functions.jsonget(job, "pass");
                System.out.println("pass=" + pass);

                String qwr = "update users set password= '" + pass + "' where id=" + userid + " and password='" + passold + "' returning id";
                System.out.println(qwr);

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                String ss;
                if (s1.size() > 0) {
                    ss = "{\n\"command\":\"changepassword\",\n"
                            + "\"result\":\"ok\"\n}";
                } else {
                    ss = "{\n\"command\":\"changepassword\",\n"
                            + "\"result\":\"passworderror\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);

            } else if (command.equals("getparameters")) {

//  get User Params ..... 
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                //name,email,userid,pid,contact_type_id,name_first,name_last,gender
                String qwr = "select cc.userid,cc.name_first,cc.name_last,cc.email,cc.info2mail,cc.pid,cc.birthday,cc.phone,cc.phonepre,cc.gender,"
                        + "cc.name_first_lat,cc.name_last_lat"
                        + " from crm_contact cc  \n"
                        + "  where userid=" + userid + ";";
                System.out.println(qwr);
                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"getparameters\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + s2.get(0)[0] + "\",\n"
                            + "\"namefirst\":\"" + s2.get(0)[1] + "\",\n"
                            + "\"namelast\":\"" + s2.get(0)[2] + "\",\n"
                            + "\"username\":\"" + s2.get(0)[3] + "\",\n"
                            + "\"info2mail\":\"" + s2.get(0)[4] + "\",\n"
                            + "\"pid\":\"" + s2.get(0)[5] + "\",\n"
                            + "\"birthday\":\"" + s2.get(0)[6] + "\",\n"
                            + "\"phone\":\"" + s2.get(0)[7] + "\",\n"
                            + "\"phonepre\":\"" + s2.get(0)[8] + "\",\n"
                            + "\"gender\":\"" + s2.get(0)[9] + "\",\n"
                            + "\"namefirstlat\":\"" + s2.get(0)[10] + "\",\n"
                            + "\"namelastlat\":\"" + s2.get(0)[11] + "\"\n}";
                } else {
                    ss = "{\n\"command\":\"getparameters\",\n"
                            + "\"result\":\"usernotfound\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);

            } else if (command.equals("setparameters")) {

//  change user parameters                
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String namefirstlat = tools.functions.jsonget(job, "namefirstlat");
                System.out.println("namefirstlat=" + namefirstlat);

                String namelastlat = tools.functions.jsonget(job, "namelastlat");
                System.out.println("namelastlat=" + namelastlat);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday=" + birthday);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String info2mail = tools.functions.jsonget(job, "info2mail");
                System.out.println("info2mail=" + info2mail);
                if (info2mail.equals("")) {
                    info2mail = "";
                } else {
                    info2mail = ", info2mail='" + info2mail + "',";
                }
                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String phonepre = tools.functions.jsonget(job, "phonepre");
                System.out.println("phonepre=" + phonepre);

                String qwr = "update crm_contact set  "
                        + "name='" + namefirst + " " + namelast + "',"
                        + "name_first='" + namefirst + "',"
                        + "name_last='" + namelast + "',"
                        + "pid='" + personal_n + "',"
                        + "birthday='" + birthday + "',"
                        + "gender='" + gender + "',"
                        + "phone='" + phone + "',"
                        + "phonepre='" + phonepre + "',"
                        + "email='" + email + "'"
                        + info2mail
                        + "name_first_lat='" + namefirstlat + "',"
                        + "name_last_lat='" + namelastlat + "'"
                        + "  where userid=" + userid + " returning id";

                System.out.println(qwr);

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(qwr);

                String ss;
                if (s1.size() > 0) {
                    ss = "{\n\"command\":\"setparameters\",\n"
                            + "\"result\":\"ok\"\n}";

                } else {
                    ss = "{\n\"command\":\"setparameters\",\n"
                            + "\"result\":\"false\"\n}";
                }

                System.out.println(ss);
                response.getWriter().write(ss);

            } else if (command.equals("resetpassword")) {

// Reset Password     
                String ss;
                String user = tools.functions.jsonget(job, "user");
                System.out.println("user=" + user);

                String qwr = "select id from users where username='" + user + "';";

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                System.out.println(qwr);

                if (s1.size() > 0) {

                    System.out.println("1.   s1=" + s1.get(0)[0]);

                    qwr = "insert into msg_link (userid) values (" + s1.get(0)[0] + ")  returning id; ";

                    ArrayList<String[]> s3 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                    System.out.println("3.   s3=" + s3.get(0)[0]);
                    System.out.println(qwr);

                    qwr = "select linkmd5 from msg_link where id= " + s3.get(0)[0];

                    ArrayList<String[]> s4 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                    System.out.println("4.   s4=" + s4.get(0)[0]);
                    System.out.println(qwr);

                    String[] args = null;
                    String email = user;

                    String subTxt = "მოგესალმებათ info@compare.ge /Greeting from info@compare.ge";

                    String msgTxt = "compare.ge გთხოვთ გადახვიდეთ ლინკზე/please folow to link"
                            + " http://192.168.18.22:9080/myweb1?register=" + s4.get(0)[0]
                            + "\n\n ლინკი აქტიურია 1 საათის განმავლობაში/ Link is valid 1 Hour "
                            + "\n\n compare.ge Please do not spam my email!";

                    Thread thread = new Thread(new Runnable() {

                        public void run() {

                            tools.SendEmailTLS.SendEmailTLS(email, subTxt, msgTxt);
                            System.out.println("pass change mail Sent");

                        }

                    });
                    thread.start();

                    if (s4.size() > 0) {
                        ss = "{\n\"command\":\"resetpassword\",\n"
                                + "\"result\":\"sendmail\"\n}";

                    } else {
                        ss = "{\n\"command\":\"resetpassword\",\n"
                                + "\"result\":\"nolink\"\n}";
                    }

                } else {

                    System.out.println("kuku");
                    ss = "{\n\"command\":\"resetpassword\",\n"
                            + "\"result\":\"wrongmail\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);
            } else if (command.equals("getcars")) {
// get car models

                String qwr = "select id,mark,model,supported from car_models order by mark,model ";

                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"getcars\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"cars\":[\n";
                    for (int i = 0; i < s2.size(); i++) {

                        if (i == 0) {
                            ss += "{\"id\":\"" + s2.get(i)[0] + "\","
                                    + "\"mark\":\"" + s2.get(i)[1] + "\","
                                    + "\"model\":\"" + s2.get(i)[2] + "\","
                                    + "\"supported\":\"" + s2.get(i)[3] + "\"}";
                        } else {
                            ss += ",\n{\"id\":\"" + s2.get(i)[0] + "\","
                                    + "\"mark\":\"" + s2.get(i)[1] + "\","
                                    + "\"model\":\"" + s2.get(i)[2] + "\","
                                    + "\"supported\":\"" + s2.get(i)[3] + "\"}";
                        }
                    }
                    ss += "\n]\n}";
                } else {
                    ss = "{\n\"command\":\"getcars\",\n"
                            + "\"result\":\"nocarmodels\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);
            } else if (command.equals("getpersondata")) {

//  get person data  ..... 
                String userid = tools.functions.jsonget(job, "userid");
                String pid = tools.functions.jsonget(job, "personal_n");
                System.out.println("userid=" + userid);
                System.out.println("personal_n=" + pid);

                //name,email,userid,pid,contact_type_id,name_first,name_last,gender
                String qwr = "select cc.userid,cc.name_first,cc.name_last,cc.email,cc.info2mail,cc.pid,cc.birthday,cc.phone,cc.phonepre,cc.gender,cc.name_first_lat,cc.name_last_lat"
                        + " from crm_contact cc  \n"
                        + "  where pid='" + pid + "' and userid=" + userid + ";";
                System.out.println(qwr);
                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"getpersondata\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + s2.get(0)[0] + "\",\n"
                            + "\"namefirst\":\"" + s2.get(0)[1] + "\",\n"
                            + "\"namelast\":\"" + s2.get(0)[2] + "\",\n"
                            + "\"username\":\"" + s2.get(0)[3] + "\",\n"
                            + "\"info2mail\":\"" + s2.get(0)[4] + "\",\n"
                            + "\"pid\":\"" + s2.get(0)[5] + "\",\n"
                            + "\"birthday\":\"" + s2.get(0)[6] + "\",\n"
                            + "\"phone\":\"" + s2.get(0)[7] + "\",\n"
                            + "\"phonepre\":\"" + s2.get(0)[8] + "\",\n"
                            + "\"gender\":\"" + s2.get(0)[9] + "\",\n"
                            + "\"namefirstlat\":\"" + s2.get(0)[10] + "\",\n"
                            + "\"namelastlat\":\"" + s2.get(0)[11] + "\"\n}";

                } else {
                    ss = "{\n\"command\":\"getpersondata\",\n"
                            + "\"result\":\"usernotfound\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);

            } else if (command.equals("getcardata")) {

//  get car data  ..... 
                String userid = tools.functions.jsonget(job, "userid");
                String carnumber = tools.functions.jsonget(job, "carnumber");
                System.out.println("userid=" + userid);
                System.out.println("carnumber=" + carnumber);

                //name,email,userid,pid,contact_type_id,name_first,name_last,gender
                String qwr = "select mc.owner_id,mc.car_number,mc.vincode,mc.man_date,cm.mark,cm.model,mc.car_model_id from ma_mtpl_cars mc left join car_models  cm on mc.car_model_id=cm.id "
                        + "where  mc.car_number='" + carnumber + "' and mc.owner_id=" + userid + ";";
                System.out.println(qwr);
                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"getcardata\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + s2.get(0)[0] + "\",\n"
                            + "\"carnumber\":\"" + s2.get(0)[1] + "\",\n"
                            + "\"vincode\":\"" + s2.get(0)[2] + "\",\n"
                            + "\"year\":\"" + s2.get(0)[3] + "\",\n"
                            + "\"mark\":\"" + s2.get(0)[4] + "\",\n"
                            + "\"model\":\"" + s2.get(0)[5] + "\",\n"
                            + "\"modelid\":\"" + s2.get(0)[6] + "\"\n}";

                } else {
                    ss = "{\n\"command\":\"getcardata\",\n"
                            + "\"result\":\"carnotfound\"\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);

            } else if (command.equals("getcasco")) {

//  change user getcasco 
                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);

                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday=" + birthday);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String info2mail = tools.functions.jsonget(job, "info2mail");
                System.out.println("info2mail=" + info2mail);
                if (info2mail.equals("")) {
                    info2mail = "";
                } else {
                    info2mail = ", info2mail='" + info2mail + "',";
                }

                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String phonepre = tools.functions.jsonget(job, "phonepre");
                System.out.println("phonepre=" + phonepre);

                String qwr = "update crm_contact set  name='" + namefirst + " " + namelast + "',"
                        + "name_first='" + namefirst + "',"
                        + "name_last='" + namelast + "',"
                        + "pid='" + personal_n + "',"
                        + "birthday='" + birthday + "',"
                        + "gender='" + gender + "',"
                        + "phone='" + phone + "',"
                        + "phonepre='" + phonepre + "',"
                        + "email='" + email + "'"
                        + info2mail
                        + " where userid=" + userid + " returning id";

                System.out.println(qwr);

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(qwr);

                String ss;
                if (s1.size() > 0) {
                    ss = "{\n\"command\":\"getcasco\",\n"
                            + "\"result\":\"ok\"\n}";

                } else {
                    ss = "{\n\"command\":\"getcasco\",\n"
                            + "\"result\":\"false\"\n}";
                }

                System.out.println(ss);
                response.getWriter().write(ss);
            } else if (command.equals("getliability")) {

//   change user getliability 
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday");
                System.out.println("birthday=" + birthday);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

// editional person
                String personal_n2 = tools.functions.jsonget(job, "2personal_n");
                System.out.println("2personal_n=" + personal_n2);

                String namelast2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("namelast2=" + namelast2);

                String namefirst2 = tools.functions.jsonget(job, "2namefirstlat");
                System.out.println("namelast2=" + namefirst2);

                String birthday2 = tools.functions.jsonget(job, "2birthday");
                System.out.println("birthday2=" + birthday2);

                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender);
                
                String liabilitylimit = tools.functions.jsonget(job, "liabilitylimit");
                System.out.println("liabilitylimit=" + liabilitylimit);
                
                String currency = tools.functions.jsonget(job, "currency");
                System.out.println("currency=" + currency);
                
                int curr=0;
                
                if (currency.equals("_lari")) curr=12;
                else if (currency.equals("_usd")) curr=14;
                else if (currency.equals("_eur")) curr=37;
/// insert into
              String qwr = "select provider_id,provider.name,amount_limit,amount_price,p.id from ma_mtpl_params p,provider \n" +
"where provider_id=provider.id and p.amount_limit='"+liabilitylimit+"' and exchange_rate_id='"+curr+"'";

             System.out.println("kukuuuuuuuuuuu"+qwr);

               ArrayList<String[]> provider = tools.functions.getResult(qwr, tools.functions.isnewcompare);
               String ss;

               
               if (provider.size()==0){
                        ss = "{\n\"command\":\"getliability\",\n"
                            + "\"result\":\"noproposals\"\n}";
               }else{
                                                      ss = "{\n\"command\":\"getliability\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + 94 + "\",\n"
                            + "\"proposals\":[" ;
               for (int i=0;i<provider.size();i++){
                   String sql="select name from ma_mtpl_params p left join ma_mtpl_benefits b on p.id=b.ma_mtpl_params_id where p.id='"+
                           provider.get(i)[4]+"'";
                   ArrayList<String[]> benefits2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                   String benefits="";
                   for (int j=0;j<benefits2.size();j++)
                       if (j==0) benefits=benefits2.get(j)[0]; else benefits+=","+benefits2.get(j)[0];
                        String proposal = "{\n\"providerid\":\"" + provider.get(i)[0] + "\",\n"
                        + "\"providername\":\"" + provider.get(i)[1] + "\",\n"
                        + "\"productid\":\"" + 111 + "\",\n"
                        + "\"limit\":\"" + liabilitylimit + "\",\n"
                     
                        //+ "\"benefits\":" + benefits + ",\n"
                        //+ "\"detals\":" + detals + ",\n"
                        + "\"price\":\"" + provider.get(i)[3] +" "+currency+ "\"\n}";
                        if (i==0) ss+=proposal;
                        else ss+=","+proposal;
               }
               ss+="]\n}";
               }
                //System.out.println("2 s1=     " + s1.get(0)[0]);

               System.out.println(ss);
/*
                String benefititem = "";
                String benefits = "[\"100 ლარის საწვავი ვისოლში\",\"100 ლარის საწვავი ვისოლში\",\"100 ლარის საწვავი ვისოლში\"]";
                String benefits2 = "[\"მანქანის 10ჯერ უფასოდ რეცხვა\",\"150 ლარის საწვავი ვისოლში\",\"150 ლარის საწვავი ვისოლში\"]";
                String benefits6 = "[\"100 ლარის საწვავი ლუკოილში\",\"100 ლარის საწვავი ლუკოილში\",\"100 ლარის საწვავი ლუკოილში\"]";

                ArrayList<String> Detailitem=new ArrayList<String>();               
                Detailitem.add( "ძირითადი დაფარვა;ლიმიტი;ფრანშიზა;header"); 
                Detailitem.add ("სასწრაფო სამედიცინო დაფარვის,ულიმიტო;$0");
                Detailitem.add( "გადაუდებელი ჰოსპიტალური მკურნალობის ხარჯები;$500 დღე (სულ$20K);$0");
                Detailitem.add( "გადაუდებელი ამბულატორიული მკურნალობის ხარჯები;$5K;$100");
                Detailitem.add( "გადაუდებელი სტომატოლოგიური მკურნალობის ხარჯები;$500;$100");
                Detailitem.add( "გადაუდებელი ოფთალმოლოგიური მკურნალობის ხარჯები;$1000;$100");
                Detailitem.add( "დაზღვეულის რეპატრიაცია;$1000;$0");
                
                String detals;
                
                detals=  "[\"ძირითადი დაფარვა;ლიმიტი;ფრანშიზა\","
                        + "\"ძირითადი დაფარვა1;1000;25\","
                        + "\"ძირითადი დაფარვა2;2000;75\","
                        + "\"ძირითადი დაფარვა3;3000;35\""
                        + "]";
                String proposal = "{\n\"providerid\":\"" + 5 + "\",\n"
                        + "\"providername\":\"" + "aldagi" + "\",\n"
                        + "\"productid\":\"" + 111 + "\",\n"
                        + "\"limit\":\"" + 15000 + "\",\n"
                        + "\"franchise\":\"" + "1%" + "\",\n"
                        + "\"benefits\":" + benefits + ",\n"
                        + "\"detals\":" + detals + ",\n"
                        + "\"price\":\"" + "14_gel" + "\"\n}";

                String proposal3 = "{\n\"providerid\":\"" + 6 + "\",\n"
                        + "\"providername\":\"" + "gpi" + "\",\n"
                        + "\"productid\":\"" + 112 + "\",\n"
                        + "\"limit\":\"" + 16000 + "\",\n"
                        + "\"franchise\":\"" + "1%" + "\",\n"
                        + "\"benefits\":" + benefits + ",\n"
                        + "\"price\":\"" + "5.50_eu" + "\"\n}";
                String proposal4 = "{\n\"providerid\":\"" + 7 + "\",\n"
                        + "\"providername\":\"" + "tbc" + "\",\n"
                        + "\"productid\":\"" + 113 + "\",\n"
                        + "\"limit\":\"" + 16000 + "\",\n"
                        + "\"franchise\":\"" + "1%" + "\",\n"
                        + "\"benefits\":" + benefits + ",\n"
                        + "\"price\":\"" + "15.50_gel" + "\"\n}";
                String proposal5 = "{\n\"providerid\":\"" + 8 + "\",\n"
                        + "\"providername\":\"" + "benefits" + "\",\n"
                        + "\"productid\":\"" + 114 + "\",\n"
                        + "\"limit\":\"" + 16000 + "\",\n"
                        + "\"franchise\":\"" + "1%" + "\",\n"
                        + "\"benefits\":" + benefits6 + ",\n"
                        + "\"price\":\"" + "5.5_usd" + "\"\n}";

                //            proposal=proposal2="\"kuku\"";

                if (provider.size() > 0) {
                    ss = "{\n\"command\":\"getliability\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + 94 + "\",\n"
                            + "\"proposals\":[" + proposal + ","
                            + proposal3 + "," + proposal4 + "," + proposal5 + "]\n}";

                } else {
                    ss = "{\n\"command\":\"getliability\",\n"
                            + "\"result\":\"noproposals\"\n}";
                }

                System.out.println(ss);*/
                response.getWriter().write(ss);
            } else if (command.equals("getliabilitydetails")) {

//   getliability details
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday");
                System.out.println("birthday=" + birthday);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

// editional person
                String personal_n2 = tools.functions.jsonget(job, "2personal_n");
                System.out.println("2personal_n=" + personal_n2);

                String namelast2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("namelast2=" + namelast2);

                String namefirst2 = tools.functions.jsonget(job, "2namefirstlat");
                System.out.println("namelast2=" + namefirst2);

                String birthday2 = tools.functions.jsonget(job, "2birthday");
                System.out.println("birthday2=" + birthday2);

                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender);

                String productid = tools.functions.jsonget(job, "productid");
                System.out.println("productid=" + productid);

/// ინსერტ ინთო
                String qwr = "select * from ma_mtpl_cars limit 1";

                System.out.println(qwr);

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(qwr);

                String benefititem = "";
                String benefits = "[\"100 ლარის საწვავი ვისოლში\",\"100 ლარის საწვავი ვისოლში\",\"100 ლარის საწვავი ვისოლში\"]";
                String benefits2 = "[\"მანქანის 10ჯერ უფასოდ რეცხვა\",\"150 ლარის საწვავი ვისოლში\",\"150 ლარის საწვავი ვისოლში\"]";
                String benefits6 = "[\"100 ლარის საწვავი ლუკოილში\",\"100 ლარის საწვავი ლუკოილში\",\"100 ლარის საწვავი ლუკოილში\"]";

           
                ArrayList<String> Detailitem=new ArrayList<String>();
                
//                Detailitem.add("დამზღვევი;"+namefirst+" "+namelast);
//                Detailitem.add("დაზღვეული;"+namefirst2+" "+namelast2);
                Detailitem.add("დამზღვევი;ალექსანდრე სარჩიმელია");
                Detailitem.add("დაზღვეული;ალექსანდრე სარჩიმელია");
                Detailitem.add("სახ. ნომერი;"+ tools.functions.jsonget(job, "carnumber") );
                Detailitem.add("ვინ. კოდი;"+ tools.functions.jsonget(job, "carvin"));
                Detailitem.add("ლიმიტი;"+ tools.functions.jsonget(job, "liabilitylimit")); 
                Detailitem.add("სადაზღვევო პერიოდი;"+ tools.functions.jsonget(job, "date1")+"-"+ tools.functions.jsonget(job, "date2")); 
                
                Detailitem.add("");
                
                Detailitem.add( "ძირითადი დაფარვა;ლიმიტი;ფრანშიზა;header"); 
                Detailitem.add ("სასწრაფო სამედიცინო დაფარვის,ულიმიტო;$0");
                Detailitem.add( "გადაუდებელი ჰოსპიტალური მკურნალობის ხარჯები;$500 დღე (სულ$20K);$0");
                Detailitem.add( "გადაუდებელი ამბულატორიული მკურნალობის ხარჯები;$5K;$100");
                Detailitem.add( "გადაუდებელი სტომატოლოგიური მკურნალობის ხარჯები;$500;$100");
                Detailitem.add( "გადაუდებელი ოფთალმოლოგიური მკურნალობის ხარჯები;$1000;$100");
                Detailitem.add( "დაზღვეულის რეპატრიაცია;$1000;$0");
                
              

                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    
                    
                    
                    
                    ss = "{\n\"command\":\"getliabilitydetails\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"userid\":\"" + userid + "\",\n"
                            + "\"forwho\":\"" + forwho + "\",\n"
                            + "\"personal_n\":\"" + personal_n + "\",\n"
                            + "\"birthday\":\"" + birthday + "\",\n"
                            + "\"namefirstlat\":\"" + namefirst + "\",\n"
                            + "\"namelastlat\":\"" + namelast + "\",\n"
                            + "\"phone\":\"" + phone + "\",\n"
                            + "\"email\":\"" + email + "\",\n"
                            + "\"2personal_n\":\"" + personal_n2 + "\",\n"
                            + "\"2namefirstlat\":\"" + namefirst2 + "\",\n"
                            + "\"2namelastlat\":\"" + namelast2 + "\",\n"
                            + "\"2birthday\":\"" + birthday2 + "\",\n"
                            + "\"checkboxrule\":\"" + tools.functions.jsonget(job, "checkboxrule") + "\",\n"
                            + "\"carnumber\":\"" + tools.functions.jsonget(job, "carnumber") + "\",\n"
                            + "\"carvin\":\"" + tools.functions.jsonget(job, "carvin") + "\",\n"
                            + "\"liabilitylimit\":\"" + tools.functions.jsonget(job, "liabilitylimit") + "\",\n"
                            + "\"date1\":\"" + tools.functions.jsonget(job, "date1") + "\",\n"
                            + "\"date2\":\""  + tools.functions.jsonget(job, "date2") + "\",\n"
                            + "\"productid\":\"" + tools.functions.jsonget(job, "productid") + "\",\n"
                            + "\"birthday2\":\"" + tools.functions.jsonget(job, "birthday2") + "\",\n"
                            + "\"2birthday2\":\"" + tools.functions.jsonget(job, "2birthday2") + "\",\n"
                            + "\"date12\":\"" + tools.functions.jsonget(job, "date12") + "\",\n"
                            + "\"date22\":\"" + tools.functions.jsonget(job, "date22") + "\",\n"
                            + "\"gender\":\""  + tools.functions.jsonget(job, "gender") + "\",\n"
                            + "\"modelid\":\""  + tools.functions.jsonget(job, "modelid")+ "\",\n"
                            + "\"Detailitem\":[";
                    ss+="\"\"";
                    for (int i=0;i<Detailitem.size();i++)
                        ss+=",\""+ Detailitem.get(i) + "\"\n";
 
                    //ss+="\"\"";
                    //for (int i=0;i<Detailitem2.size();i++)
                    //    ss+="\""+ Detailitem2.get(i) + "\",\n";
                    
                    
                            ss+="],\n}";



                } else {
                    ss = "{\n\"command\":\"getliabilitydetails\",\n"
                            + "\"result\":\"productnotfound\"\n}";
                }


                System.out.println(ss);
                response.getWriter().write(ss);
            }

        } catch (Exception e) {
            System.out.println("tryend" + e.toString());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
