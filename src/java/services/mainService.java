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
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.IOException;
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
                System.out.println(line);
                result += line + "\n";
            }

            JsonElement el = new JsonParser().parse(result);

            JsonObject job = el.getAsJsonObject();

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

                qwr = "insert into crm_contact  (name,email,userid,pid,contact_type_id,name_first,name_last,gender,birthday,phone,phonepre)"
                        + "values('" + namefirst + " " + namelast + "','" + email + "'," + s1.get(0)[0] + ",'" + personal_n + "',1,'"
                        + namefirst + "','" + namelast + "','" + gender + "','" + birthday + "','" + phone + "','" + phonepre + "') returning id;";

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

                //  change User FirstName LastName PID ..... 
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                //name,email,userid,pid,contact_type_id,name_first,name_last,gender
                String qwr = "select cc.userid,cc.name_first,cc.name_last,cc.email,cc.info2mail,cc.pid,cc.birthday,cc.phone,cc.phonepre,cc.gender"
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
                            + "\"gender\":\"" + s2.get(0)[9] + "\"\n}";
                } else {
                    ss = "{\n\"command\":\"register3\",\n"
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
                        + "email='" + email + "',"
                        + "info2mail='" + info2mail + "'  where userid=" + userid + " returning id";

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
            }

        } catch (Exception e) {
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
