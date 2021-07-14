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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tools.functions;
import tools.pdfDesigner;

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
        String ptable = "";
        String buytable = "";
        String[] invoice_params;
        String franchise = "";

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
//
            System.out.println("111111111" + result);
            JsonElement el = new JsonParser().parse(result);
            System.out.println("22222");
            JsonObject job = el.getAsJsonObject();
            System.out.println("333");
            String command = tools.functions.jsonget(job, "command");
            System.out.println("command=" + command);

            String tqwr = "insert into global_transactions(transactionbody) values('" + result + "') returning id";
            //        insert into global_transactions (transactionbody)values('{"name":"John"}')

            ArrayList<String[]> ts2 = tools.functions.getResult(tqwr, tools.functions.isnewcompare);
            System.out.println("========================================transaction_id==========" + ts2.get(0)[0]);
            String transactin_id = ts2.get(0)[0];

            if (command.equals("login")) {
// login
                String user = tools.functions.jsonget(job, "user");
                System.out.println("user=" + user);
                String pass = tools.functions.jsonget(job, "pass");
                System.out.println("pass=" + pass);
                String qwr = "select u.id,name_first,name_last from users u left join crm_contact cc on u.id=cc.userid where username='" + user + "'  and password='" + pass + "'";

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
            } else if (command.equals("getpolicelist")) {
// getpolicelist
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String lang = tools.functions.jsonget(job, "namefirst");
                System.out.println("lang=" + lang);

                //               String qwr = "select u.id,name_first,name_last from users u left join crm_contact cc on u.id=cc.userid where username='" + user + "'  and password='" + pass + "'";
                //            String qwr = "select now()";
                String qwr = "select policyholder,policyowner,creation_date,id,product_name,company_name,filename,date(start_date),date(end_date),price0,payprice,payment_schedule from order_params where  user_id=" + userid + " order by creation_date desc ";
                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                String ss = "{\n\"command\":\"getpolicelist\",\n"
                        + "\"result\":\"ok\",\n"
                        + "\"userid\":\"" + userid + "\",\n"
                        + "\"policelist\":\"";
                ss += "<style>  table.orderlist td,th {border: 1px solid #dddddd;text-align: left;padding: 8px;width:50%}</style>\\n"
                        + "<h2>გადახდილი პოლისები</h2>\\n";

                if (s2.size() > 0) {

                    for (String[] s22 : s2) {

                        System.out.println("s9999999999999999999999999999999" + s22[3]);

                        ss += "<table class='orderlist'  style='cursor:hand;width:100%;margin-top: 20px' id='policelist" + s22[3] + "a'onclick=\\\"$('#policelist" + s22[3] + "a').css('display', 'none');$('#policelist" + s22[3] + "b').css('display', 'table');\\\">\\n"
                                + "<tr style='color:blue'> <td >?_" + s22[4] + "?</th><td><img style='margin: auto;' src='icons/" + s22[5] + ".png'></td></tr>\\n"
                                + "</table>\\n"
                                //            +"<table class='orderlist' id='policelist1b' style='display: none;width:100%;margin-top: 20px'>\\n"
                                + "<table class='orderlist' id='policelist" + s22[3] + "b' style='display: none;width:100%;margin-top: 20px'>\\n"
                                + "<tr onclick=\\\"$('#policelist" + s22[3] + "b').css('display', 'none');$('#policelist" + s22[3] + "a').css('display', 'table');\\\" style='color:blue;cursor:hand;'><td  colspan='2' style='width:50%'>?_" + s22[4] + "?</td></tr>\\n"
                                //                          
                                + "<tr> <td style='padding-left: 20px;'>პროვაიდერი</td> <td><img style='margin: auto;' src='icons/" + s22[5] + ".png'></td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;'>დაზღვეული</td> <td >" + s22[1] + "</td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;'>შეძენისთარიღი</td> <td>" + s22[2] + "</td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;'>მოქმედებისვადა</td> <td>" + s22[7] + " - " + s22[8] + "</td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;'>ღირებულება</td> <td>" + s22[9] + " აქედან რომელი - " + s22[10] + "</td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;cursor:hand;text-decoration: underline;' onclick=\\\"detalssubmitajax('" + s22[3] + "','getmodalgraphic');\\\">გადახდის გრაფიკი</td> <td>მიმდინარე გადასახადი 30 ლარი</td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;cursor:hand;text-decoration: underline;' onclick=\\\"detalssubmitajax('" + s22[4] + "','getmodalphoto');\\\">ფოტოსატვირთვა</td> <td>ფოტოებს არ საჭიროებს</td> </tr>\\n"
                                + "<tr> <td style='padding-left: 20px;cursor:hand;text-decoration: underline;' onclick=\\\"showpdf('pdf/getliability" + s22[5] + ".pdf')\\\">ხელშეკრულება</td> <td>იხილეთ მიმაგრებული ხელშეკრულება</td></tr>\\n"
                                + "<tr> <td style='padding-left: 20px;cursor:hand;text-decoration: underline;' onclick=\\\"showpdf('pdf/" + s22[6] + ".pdf')\\\">პოლისი</td> <td>იხილეთ მიმაგრებული პოლისი</td></tr>\\n"
                                + "</table>\\n";

                    }

                    ss += "\"}";
                    //                + "\"result\":\"passworderror\"\n}";
                } else {
                    ss = "{\n\"command\":\"getpolicelist\",\\n"
                            + "\"result\":\"usernotfound\"\\n}";
                }
                System.out.println(ss);
                response.getWriter().write(ss);

            } else if (command.equals("getmodalgraphic")) {
// getpolicelist
//                String userid = tools.functions.jsonget(job, "userid");
//                System.out.println("userid=" + userid);
//
//                String lang = tools.functions.jsonget(job, "namefirst");
//                System.out.println("lang=" + lang);

                String productid = tools.functions.jsonget(job, "productid");
                System.out.println("productid=" + productid);
                //           productid = "123456";

                //               String qwr = "select u.id,name_first,name_last from users u left join crm_contact cc on u.id=cc.userid where username='" + user + "'  and password='" + pass + "'";
                String qwr = "select payment_schedule,payprice from order_params where id=" + productid;
                System.out.println("qwr=" + qwr);
                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                int pschedule = 0;

                if (s2.size() > 0) {
                    System.out.println("s2-size=" + s2.size());

                    for (String[] s22 : s2) {

                        String[] myschedule = tools.paymentshedule.makeshedule(s22[0]);

                        String ss;
                        String monthlyAmountValue = s22[1];
                        String currency = "GEL";

                        ss = "{\n\"command\":\"getmodalgraphic\",\n"
                                + "\"result\":\"ok\",\n"
                                + "\"productid\":\"" + productid + "\",\n"
                                //    + "\"txt\":\"<style>  table.modalgraphiclist td,th {border: 1px solid #dddddd;text-align: left;padding: 8px;width:100%} </style>\\n"
                                + "\"txt\":\"<style>  table.modalgraphiclist td,th {border: 1px solid #dddddd;border-collapse: collapse; text-align: left;padding: 8px;}  </style>\\n"
                                + "<table class='modalgraphiclist' style='width: 100%; margin-bottom:30px;' ><tbody>"
                                + "<tr><td colspan='2'>გადახდის გრაფიკი</td></tr>"
                                + "<tr><td>თარიღი</td><td>თანხა</td></tr>";

//                        for (int i = 0; i < 12; i++) {
                        for (int i = 0; i < myschedule.length; i++) {
                            System.out.println("myscheduleLenght=" + myschedule.length);
                            //  rulesVal.addCell("2018." + String.valueOf(01 + i) + ".01 - 70 ლარი");
                            if (!myschedule[i].equals("")) {
                                System.out.println("" + myschedule[i]);

                                System.out.println("yahoooo" + myschedule[i] + " - " + monthlyAmountValue + currency);
                                ss = ss + "<tr><td>" + myschedule[i] + "</td><td>" + monthlyAmountValue + " ლარი</td></tr>";

                            }
//                            else 
//                                ss = ss + "{\n\"command\":\"getmodalgrapbhic\",\\n"
//                                        + "\"result\":\"productnotfound\"\\n}";
//                            
                        }
                        ss = ss + "</tbody></table>\\n"
                                + "\"}";

                        System.out.println("ss=" + ss);
                        response.getWriter().write(ss);
                    }
                }
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

                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);

                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String phonepre = tools.functions.jsonget(job, "phonepre");
                System.out.println("phonepre=" + phonepre);

                String qwr = "Insert into users (username) values ('" + email + "') returning id ;";

                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);

                System.out.println("1.   s1=" + s1.get(0)[0]);

                qwr = "insert into crm_contact  (name,email,idn,userid,pid,contact_type_id,name_first,name_last,gender,birthday,phone,phonepre,name_first_lat,name_last_lat)"
                        + "values('" + namefirst + " " + namelast + "','" + email + "','" + citizenship_code + "'," + s1.get(0)[0] + ",'" + personal_n + "',1,'"
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
                        + " https://compare.ge/compare?register=" + s4.get(0)[0]
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
                        + "cc.name_first_lat,cc.name_last_lat,idn,address"
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
                            + "\"namelastlat\":\"" + s2.get(0)[11] + "\",\n"
                            + "\"citizenship_code\":\"" + s2.get(0)[12] + "\",\n"
                            + "\"myaddress\":\"" + s2.get(0)[13]
                            + "\"\n}";
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

                String address = tools.functions.jsonget(job, "myaddress");
                System.out.println("address=" + address);

                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);

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
                        + "address='" + address + "',"
                        + "email='" + email + "',"
                        + "idn='" + citizenship_code + "',"
                        //                        + "info2mail="+
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
            } else if (command.equals("debugpay")) {

//  make payments                
//                String userid = tools.functions.jsonget(job, "userid");
//                System.out.println("userid=" + userid);
                //        String userid = "94";
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String productid = tools.functions.jsonget(job, "productid");
                System.out.println("productid=" + productid);

                String type = tools.functions.jsonget(job, "type");
                System.out.println("type=" + type);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String policyowner = namefirst + " " + namelast;

                String namefirst_2 = tools.functions.jsonget(job, "2namefirst");
                System.out.println("2namefirst=" + namefirst_2);
                if (namefirst_2.equals("")) {
                    namefirst_2 = namefirst;
                }

                String namelast_2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("2namelast=" + namelast_2);
                if (namelast_2.equals("")) {
                    namelast_2 = namelast;
                }

                String policyholder = namefirst_2 + " " + namelast_2;

                //2namefirst
                String price0 = tools.functions.jsonget(job, "price0");
                System.out.println("price0=" + price0);
                String payprice = tools.functions.jsonget(job, "payprice");
                System.out.println("payprice=" + payprice);

                String price1 = tools.functions.jsonget(job, "price1");
                System.out.println("price1=" + price1);

                String limit = tools.functions.jsonget(job, "limit");
                System.out.println("limit=" + limit);

                String paymentschedule = tools.functions.jsonget(job, "paymentschedule");
                System.out.println("paymentschedule=" + paymentschedule);
                String pnumberinsurer = tools.functions.jsonget(job, "personal_n");
                System.out.println("pnumberinsurer=" + pnumberinsurer);
                String pnumberinsured = tools.functions.jsonget(job, "2personal_n");
                System.out.println("pnumberinsured=" + pnumberinsured);
                String birthday = tools.functions.jsonget(job, "birthday");
                System.out.println("birthday=" + birthday);
                String birthday2 = tools.functions.jsonget(job, "2birthday");
                System.out.println("birthday2=" + birthday2);
                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);
                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender2);
                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);
                String citizenship_code2 = tools.functions.jsonget(job, "2citizenship_code");
                System.out.println("citizenship_code2=" + citizenship_code2);
                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);
                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String addressinsurer = tools.functions.jsonget(job, "myaddress");
                System.out.println("addressinsurer=" + addressinsurer);

                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);
                String carvin = tools.functions.jsonget(job, "carvin");
                System.out.println("carvin=" + carvin);
                String year = tools.functions.jsonget(job, "year");
                System.out.println("year=" + year);

                // property
                String property_address = tools.functions.jsonget(job, "address");
                System.out.println("property_address=" + property_address);

                String addressinsured = tools.functions.jsonget(job, "2myaddress");
                System.out.println("address=" + addressinsured);

                String startdate = tools.functions.jsonget(job, "date12");
                System.out.println("startdate=" + startdate);
                String enddate = tools.functions.jsonget(job, "date22");
                System.out.println("enddate=" + enddate);
                String currency = tools.functions.jsonget(job, "currency");
                System.out.println("currency=" + currency);
                String luggage = tools.functions.jsonget(job, "baggageinsurance");
                System.out.println("luggage=" + luggage);
                String flight = tools.functions.jsonget(job, "reisinsurance");
                System.out.println("flight=" + flight);
                if (luggage.equals("true")) {
                    luggage = "  yes";
                } else {
                    luggage = "  no";
                }
                if (flight.equals("true")) {
                    flight = "  yes";
                } else {
                    flight = "  no";
                }

                String country_code = "";
                String marca = "";
                String model = "";
                String modelname = "";
                String carnumber = "";

                country_code = tools.functions.jsonget(job, "country_code");
                if (country_code.equals(null) || country_code.equals("")) {
                    country_code = "ge";
                }
                System.out.println("country_code=" + country_code);
                String countryqwe = "select name from global_countries where idn='" + country_code + "'";
                System.out.println("countryqwe=" + countryqwe);
                ArrayList<String[]> tcountry = tools.functions.getResult(countryqwe, tools.functions.isnewcompare);
                System.out.println("2 tcountry=     " + tcountry.get(0)[0]);

                if (forwho.equals("forme")) {
                    pnumberinsured = "pnumberinsurer";
                    birthday2 = birthday;
                    gender2 = gender;
                    System.out.println("forwho= " + forwho);
                    citizenship_code2 = citizenship_code;
                    addressinsured = addressinsurer;

                }

                String covering = "";
                String tablename = type.substring(3);
                if (tablename.equals("liability")) {
                    tablename = "ma_mtpl";
                    franchise = tools.functions.jsonget(job, "liabilitylimit");
                    marca = tools.functions.jsonget(job, "marca");
                    System.out.println("marca=" + marca);
                    model = tools.functions.jsonget(job, "model");
                    System.out.println("model=" + model);
                    modelname = tools.functions.jsonget(job, "modelname");
                    System.out.println("modelname=" + modelname);
                    carnumber = tools.functions.jsonget(job, "carnumber");
                    System.out.println("carnumber=" + carnumber);

                } else if (tablename.equals("casco")) {
                    tablename = "casco";
                } else if (tablename.equals("property")) {
                    tablename = "property";
                    franchise = tools.functions.jsonget(job, "limit");
                    System.out.println("franchise=" + franchise);
                    //           covering=",covering";
                } else if (tablename.equals("health")) {
                    tablename = "health";
                } else if (tablename.equals("travel")) {
                    tablename = "travel";
                    franchise = tools.functions.jsonget(job, "insurancelimit");
                    System.out.println("franchise=" + franchise);

                }
                int pschedule = 0;
                int ischedule = 0;
                if (paymentschedule.equals("inonce")) {
                    pschedule = 1;
                } else if (paymentschedule.equals("inyear2")) {
                    pschedule = 2;
                    ischedule = 6;
                } else if (paymentschedule.equals("inkvart")) {
                    pschedule = 4;
                    ischedule = 4;
                } else if (paymentschedule.equals("inmounth")) {
                    pschedule = 12;
                    ischedule = 1;
                }

                // Make Schedule
                //         String[] schedule = new String[11];
                String[] schedule = {"", "", "", "", "", "", "", "", "", "", "", ""};
                java.util.Date now = new Date();
                Calendar myCal = Calendar.getInstance();
                myCal.setTime(now);
                //      myCal.add(Calendar.MONTH, +1);
                now = myCal.getTime();

                System.out.println("now=" + now);
                for (int i = 0; i < pschedule; i++) {

                    now = myCal.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ");
                    //          System.out.println(formatter.format(now));
                    schedule[i] = formatter.format(now);
                    System.out.println(schedule[i]);

                    System.out.println("shedule[" + i + "]=" + schedule[i]);
                    System.out.println(i);
                    myCal.add(Calendar.MONTH, +ischedule);
                }

                // endSchedule
                String pqwr = "select p.name,headergeo,headereng,addressgeo,phone,mail,covering,amount_text from " + tablename + "_params pp left join provider p on pp.provider_id=p.id where pp.id=" + productid;
                System.out.println("pqwr=" + pqwr);

                ArrayList<String[]> spqwr = tools.functions.getResult(pqwr, tools.functions.isnewcompare);
                // make police    begin        
                String insurer = namefirst + " " + namelast;
                String insured = namefirst_2 + " " + namelast_2;;
                String covered = spqwr.get(0)[6];
                String amount_text = spqwr.get(0)[7];

                String creationdate = java.time.LocalDateTime.now().toString();
                System.out.println("Creation Date _1 =" + creationdate);

                String filename = tools.pdfDesigner.makepolice(tablename, userid, insurer, insured, spqwr.get(0)[0], spqwr.get(0)[1],
                        spqwr.get(0)[2], spqwr.get(0)[3], spqwr.get(0)[4], spqwr.get(0)[5], franchise, pnumberinsurer, pnumberinsured, birthday,
                        birthday2, gender, gender2, citizenship_code, citizenship_code2, phone, email, addressinsurer, carvin, year, price0, payprice,
                        addressinsured, startdate, enddate, tcountry.get(0)[0], marca, modelname, carnumber, currency, schedule, covered, property_address, amount_text, luggage, flight, creationdate);
                // make police end

                String qwr = "insert into order_params (user_id,product_id,product_name,payment_schedule,policyholder,policyowner,company_name,filename,"
                        + "addressinsurer,addressinsured,start_date,end_date,price0,payprice,luggage,flight,transactionid)"
                        + " values (" + userid + "," + productid + ",'" + tablename + "','" + paymentschedule + "','" + policyowner + "','"
                        + policyholder + "','" + spqwr.get(0)[0] + "','" + filename + "','" + addressinsurer + "','" + addressinsured + "','"
                        + startdate + "','" + enddate + "','" + price0 + "','" + payprice + "','" + luggage + "','" + flight + "','" + transactin_id + "')  returning creation_date; ";

                System.out.println(qwr);
                ArrayList<String[]> s1 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                System.out.println("2 s1 Creation_date=     " + s1.get(0)[0]);

                String ss;
                if (s1.size() > 0) {
                    ss = "{\n\"command\":\"debugpay\",\n"
                            + "\"result\":\"ok\"\n}";

                } else {
                    ss = "{\n\"command\":\"debugpay\",\n"
                            + "\"result\":\"false\"\n}";
                }

                System.out.println(ss);
                response.getWriter().write(ss);

                //  pdfDesigner.invoice(tablename);
            } else if (command.equals("getcities")) {
// get cities

                String qwr = "select id,name from city order by name ";

                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                if (s2.size() > 0) {
                    ss = "{\n\"command\":\"getcities\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"cities\":[\n";
                    for (int i = 0; i < s2.size(); i++) {

                        if (i == 0) {
                            ss += "{\"id\":\"" + s2.get(i)[0] + "\","
                                    + "\"cityname\":\"" + s2.get(i)[1] + "\"}";
                        } else {
                            ss += ",\n{\"id\":\"" + s2.get(i)[0] + "\","
                                    + "\"cityname\":\"" + s2.get(i)[1] + "\"}";
                        }
                    }
                    ss += "\n]\n}";
                } else {
                    ss = "{\n\"command\":\"getcities\",\n"
                            + "\"result\":\"nocities\"\n}";
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
                String qwr = "select cc.userid,cc.name_first,cc.name_last,cc.email,cc.info2mail,cc.pid,cc.birthday,cc.phone,cc.phonepre,cc.gender,cc.name_first_lat,cc.name_last_lat,cc_idn"
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
                            + "\"citizenship_code\":\"" + s2.get(0)[11] + "\",\n"
                            + "\"namelastlat\":\"" + s2.get(0)[12] + "\"\n}";

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

                String namefirstlat = tools.functions.jsonget(job, "namefirstlat");
                System.out.println("namefirstlat=" + namefirstlat);

                String namelastlat = tools.functions.jsonget(job, "namelastlat");
                System.out.println("namelastlat=" + namelastlat);

                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);

// editional person
                String personal_n2 = tools.functions.jsonget(job, "2personal_n");
                System.out.println("2personal_n=" + personal_n2);

                String namefirst2 = tools.functions.jsonget(job, "2namefirst");
                System.out.println("namefirst2=" + namefirst2);

                String namelast2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("namelast2=" + namelast2);

                String namefirstlat2 = tools.functions.jsonget(job, "2namefirstlat");
                System.out.println("namefirstlat2=" + namefirstlat2);

                String namelastlat2 = tools.functions.jsonget(job, "2namelastlat");
                System.out.println("namelastlat2=" + namelastlat2);

                String birthdayp2 = tools.functions.jsonget(job, "2birthday2");
                System.out.println("birthdayp2=" + birthdayp2);

                String citizenship_code2 = tools.functions.jsonget(job, "2citizenship_code");
                System.out.println("citizenship_code2=" + citizenship_code2);

                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender);

                if (forwho.equals("forme")) {
                    personal_n2 = tools.functions.jsonget(job, "personal_n");
                    System.out.println("2personal_n=" + personal_n2);

                    namefirst2 = tools.functions.jsonget(job, "namefirst");
                    System.out.println("namefirst2=" + namefirst2);

                    namelast2 = tools.functions.jsonget(job, "namelast");
                    System.out.println("namelast2=" + namelast2);

                    namefirstlat2 = tools.functions.jsonget(job, "namefirstlat");
                    System.out.println("namefirstlat2=" + namefirstlat2);

                    namelastlat2 = tools.functions.jsonget(job, "namelastlat");
                    System.out.println("namelastlat2=" + namelastlat2);

                    birthdayp2 = tools.functions.jsonget(job, "birthday2");
                    System.out.println("birthdayp2=" + birthdayp2);

                    citizenship_code2 = tools.functions.jsonget(job, "citizenship_code");
                    System.out.println("citizenship_code2=" + citizenship_code2);

                    gender2 = tools.functions.jsonget(job, "gender");
                    System.out.println("gender2=" + gender);

                }

//   product parameters
                String carnumber = tools.functions.jsonget(job, "carnumber");
                System.out.println("carnumber=" + carnumber);

                String carvin = tools.functions.jsonget(job, "carvin");
                System.out.println("carvin=" + carvin);

                String marca = tools.functions.jsonget(job, "marca");
                System.out.println("marca=" + marca);

                String model = tools.functions.jsonget(job, "model");
                System.out.println("model=" + model);

                String modelname = tools.functions.jsonget(job, "modelname");
                System.out.println("modelname=" + modelname);

                String caryear = tools.functions.jsonget(job, "year");
                System.out.println("caryear=" + caryear);

                String liabilitylimit = tools.functions.jsonget(job, "liabilitylimit");
                System.out.println("liabilitylimit=" + liabilitylimit);

                String currency = tools.functions.jsonget(job, "currency");
                System.out.println("currency=" + currency);

                String paymentschedule = tools.functions.jsonget(job, "paymentschedule");
                System.out.println("paymentschedule=" + paymentschedule);
                String paymentschedulet = "";
                if (paymentschedule.equals("inmounth")) {
                    paymentschedulet = "ყოველთვიური";

                } else if (paymentschedule.equals("inkvart")) {
                    paymentschedulet = "კვარტალში ერთხელ";
                } else if (paymentschedule.equals("inyear2")) {
                    paymentschedulet = "წელიწადში ორჯერ";
                    // paymentschedule2=paymentschedule+"br";
                } else if (paymentschedule.equals("inonce")) {
                    paymentschedulet = "წელიწადში ერთხელ";
                }

                String datestart = tools.functions.jsonget(job, "date12");
                System.out.println("date12=" + datestart);

                String dateend = tools.functions.jsonget(job, "date22");
                System.out.println("dateend=" + dateend);

                int curr = 0;

                if (currency.equals("_lari")) {
                    curr = 12;
                } else if (currency.equals("_usd")) {
                    curr = 14;
                } else if (currency.equals("_eur")) {
                    curr = 37;
                }

                String qwr = "select provider_id,provider.name,amount_limit,amount_price,p.id,add_html,franchise from ma_mtpl_params p,provider \n"
                        + "where provider_id=provider.id and p.amount_limit='" + liabilitylimit + "' and exchange_rate_id='" + curr + "'";

                System.out.println("qwr=    " + qwr);

                ArrayList<String[]> provider = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;

                if (provider.size() == 0) {
                    ss = "{\n\"command\":\"getliability\",\n"
                            + "\"result\":\"noproposals\"\n}";
                } else {
                    // ptable space
                    ptable = "<style>\\n"
                            + "table.pparameters {font-size: 14px;font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}\\n"
                            + "table.pparameters td, th {border: 1px solid #dddddd;text-align: left;padding: 8px}\\n"
                            + "</style>\\n"
                            + "<table class='pparameters'>\\n"
                            + "<tr><td style='background-color: #dddddd'><b>დამზღვევის მონაცემები </b></td> <td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthday + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender + "</td></tr>\\n"
                            + "<tr><td><b>ტელეფონი</b></td><td>" + phone + "</td></tr>\\n"
                            + "<tr><td><b>eMail</b></td><td>" + email + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>დაზღვეულის მონაცემები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n2 + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthdayp2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat2 + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code2 + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender2 + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>პასუხისმგებლობის დეტალები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(1)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>სახელმწიფო ნომერი</b></td><td>" + carnumber + "</td></tr>\\n"
                            + "<tr><td><b>ვინკოდი</b></td><td>" + carvin + "</td></tr>\\n"
                            + "<tr><td><b>მარკა</b></td><td>" + marca + "</td></tr>\\n"
                            + "<tr><td><b>მოდელი</b></td><td>" + modelname + "</td></tr>\\n"
                            + "<tr><td><b>წელი</b></td><td>" + caryear + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო პერიოდი</b></td><td>" + datestart + "-" + dateend + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო თანხა</b> </td><td>" + liabilitylimit + " ?" + currency + "?</td></tr>\\n"
                            + "<tr><td><b>გადახდის გრაფიკი</b></td><td>" + paymentschedulet + "</td></tr>\\n"
                            //                   + "<tr><td><b>ჯამური თანხა</b></td><td>14 ლარი</td></tr>\\n"

                            + "</table>\\n";
                    // end ptable
                    ss = "{\n\"command\":\"getliability\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"addtable\":\"" + ptable + "\",\n"
                            + "\"addtable2\":\"" + ptable + "\",\n"
                            + "\"userid\":\"" + userid + "\",\n"
                            + "\"proposals\":[";
                    for (int i = 0; i < provider.size(); i++) {
                        System.out.println("provider=  " + provider.get(i)[1]);
                        String sql = "select name from ma_mtpl_params p left join ma_mtpl_benefits b on p.id=b.ma_mtpl_params_id where p.id='"
                                + provider.get(i)[4] + "'";
                        ArrayList<String[]> benefits2 = tools.functions.getResult(sql, tools.functions.isnewcompare);
                        String benefits = "";
                        for (int j = 0; j < benefits2.size(); j++) {
                            if (j == 0) {
                                benefits = "\"" + benefits2.get(j)[0] + "\"";
                            } else {
                                benefits += ",\"" + benefits2.get(j)[0] + "\"";
                            }
                        }
                        String details = "";
                        double price = 0;
                        String paymentschedule2 = paymentschedule;
                        if (paymentschedule.equals("inmounth")) {
                            price = functions.str2double0(provider.get(i)[3]) / 12;

                        } else if (paymentschedule.equals("inkvart")) {
                            price = functions.str2double0(provider.get(i)[3]) / 4;
                        } else if (paymentschedule.equals("inyear2")) {
                            price = functions.str2double0(provider.get(i)[3]) / 2;
                            // paymentschedule2=paymentschedule+"br";
                        } else if (paymentschedule.equals("inonce")) {
                            price = functions.str2double0(provider.get(i)[3]);
                        }
                        System.out.println("price=" + provider.get(i)[3] + "=" + functions.str2int0(provider.get(i)[3]) + "=" + price);
                        details = "\"limit;" + provider.get(i)[2] + " " + currency + "\",\"price;" + provider.get(i)[3] + " " + currency + "\",\"" + paymentschedule + ";" + String.format("%.2f", price) + " " + currency + "\"";
                        String mypdf = command + provider.get(i)[1];
                        //              + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"
                        String addhtml = provider.get(i)[5];
                        if (addhtml == null) {
                            addhtml = "";
                        } else {
                            addhtml = addhtml.replace("\n", "");
                        }
                        String proposal = "{\n\"providerid\":\"" + provider.get(i)[0] + "\",\n"
                                + "\"providername\":\"" + provider.get(i)[1] + "\",\n"
                                + "\"productid\":\"" + provider.get(i)[4] + "\",\n"
                                + "\"limit\":\"" + liabilitylimit + "\",\n"
                                + "\"franchise\":\"" + provider.get(i)[6] + "\",\n"
                                + "\"benefits\":[" + benefits + "],\n"
                                + "\"addhtml\":\"" + addhtml + "\",\n"
                                + "\"detals\":[" + details + "],\n"
                                + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"
                                + "\"price\":\"" + provider.get(i)[3] + " " + currency + "\",\n"
                                + "\"payprice\":\"" + String.format("%.2f", price) + " " + currency + "\"\n}";
                        if (i == 0) {
                            ss += proposal;
                        } else {
                            ss += "," + proposal;
                        }
                    }
                    ss += "]\n}";
                }
                //System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(ss);

                response.getWriter().write(ss);
            } else if (command.equals("gettravel")) {

//   change user gettravel
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday=++" + birthday);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String namefirstlat = tools.functions.jsonget(job, "namefirstlat");
                System.out.println("namefirstlat=" + namefirstlat);

                String namelastlat = tools.functions.jsonget(job, "namelastlat");
                System.out.println("namelastlat=" + namelastlat);

                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String birthday2 = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday2=" + birthday2);

                String country_code = tools.functions.jsonget(job, "country_code");
                System.out.println("country_code=" + country_code);

// editional person
                String personal_n2 = tools.functions.jsonget(job, "2personal_n");
                System.out.println("2personal_n=" + personal_n2);

                String namefirst2 = tools.functions.jsonget(job, "2namefirst");
                System.out.println("namefirst2=" + namefirst2);

                String namelast2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("namelast2=" + namelast2);

                String namefirstlat2 = tools.functions.jsonget(job, "2namefirstlat");
                System.out.println("namefirstlat2=" + namefirstlat2);

                String namelastlat2 = tools.functions.jsonget(job, "2namelastlat");
                System.out.println("namelastlat2=" + namelastlat2);

                String birthdayp2 = tools.functions.jsonget(job, "2birthday2");
                System.out.println("birthdayp2=" + birthdayp2);

                String citizenship_code2 = tools.functions.jsonget(job, "2citizenship_code");
                System.out.println("citizenship_code2=" + citizenship_code2);

                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender);

                if (forwho.equals("forme")) {
                    personal_n2 = tools.functions.jsonget(job, "personal_n");
                    System.out.println("2personal_n=" + personal_n2);

                    namefirst2 = tools.functions.jsonget(job, "namefirst");
                    System.out.println("namefirst2=" + namefirst2);

                    namelast2 = tools.functions.jsonget(job, "namelast");
                    System.out.println("namelast2=" + namelast2);

                    namefirstlat2 = tools.functions.jsonget(job, "namefirstlat");
                    System.out.println("namefirstlat2=" + namefirstlat2);

                    namelastlat2 = tools.functions.jsonget(job, "namelastlat");
                    System.out.println("namelastlat2=" + namelastlat2);

                    birthdayp2 = tools.functions.jsonget(job, "birthday2");
                    System.out.println("birthdayp2=" + birthdayp2);

                    citizenship_code2 = tools.functions.jsonget(job, "citizenship_code");
                    System.out.println("citizenship_code2=" + citizenship_code2);

                    gender2 = tools.functions.jsonget(job, "gender");
                    System.out.println("gender2=" + gender);

                }

//   product parameters
                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String baggageinsurance = tools.functions.jsonget(job, "baggageinsurance");
                System.out.println("baggageinsurance=" + baggageinsurance);
                if (baggageinsurance.equals("true")) {
                    baggageinsurance = "yes";
                } else {
                    baggageinsurance = "no";
                }
                String reisinsurance = tools.functions.jsonget(job, "reisinsurance");
                System.out.println("reisinsurance=" + reisinsurance);

                if (reisinsurance.equals("true")) {
                    reisinsurance = "yes";
                } else {
                    reisinsurance = "no";
                }
//
//                String email2 = tools.functions.jsonget(job, "email");
//                System.out.println("email=" + email);

                String insurancelimit = tools.functions.jsonget(job, "insurancelimit");
                System.out.println("insurancelimit=" + insurancelimit);

                String currency = tools.functions.jsonget(job, "currency");
                System.out.println("currency=" + currency);

                String paymentschedule = tools.functions.jsonget(job, "paymentschedule");

                System.out.println("paymentschedule=" + paymentschedule);

                String datestart = tools.functions.jsonget(job, "date12");
                System.out.println("date12=" + datestart);

                String dateend = tools.functions.jsonget(job, "date22");
                System.out.println("dateend=" + dateend);

                int curr = 0;

                if (currency.equals("_lari")) {
                    curr = 12;
                } else if (currency.equals("_usd")) {
                    curr = 14;
                } else if (currency.equals("_euro")) {
                    curr = 15;
                }
/// select Query
//                String qwr = "select provider_id,provider.name,amount_limit,amount_price,p.id,add_html,franchise from travel_params p,provider \n"
//                        + "where provider_id=provider.id and p.amount_limit='" + insurancelimit + "' and exchange_rate_id='" + curr
//                        + "' and  date_part('year', age( now(),'" + birthday + "'))>age_min and  date_part('year', age( now(),'" + birthday + "'))<age_max" //      date_part('year', age( now(),'1972-1-23'))>age_min  and    date_part('year', age( now(),'1972-1-23'))<age_max
//                        ;

                String qwr = "select provider_id,provider.name,amount_limit,amount_price * ((date '" + dateend + "'- date'" + datestart + "')+1),p.id,add_html,franchise from travel_params p,provider \n"
                        + "where provider_id=provider.id and p.amount_limit='" + insurancelimit + "' and exchange_rate_id='" + curr
                        + "' and  date_part('year', age( now(),'" + birthday + "'))>age_min and  date_part('year', age( now(),'" + birthday + "'))<age_max" //      date_part('year', age( now(),'1972-1-23'))>age_min  and    date_part('year', age( now(),'1972-1-23'))<age_max
                        ;
                System.out.println("curr=" + curr);
                System.out.println("qwr=    " + qwr);

                ArrayList<String[]> provider = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                currency = "_lari";
                if (provider.size() == 0) {
                    ss = "{\n\"command\":\"gettravel\",\n"
                            + "\"result\":\"noproposals\"\n}";
                } else {

                    ptable = "<style>\\n"
                            + "table.pparameters {font-size: 14px;font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}\\n"
                            + "table.pparameters td, th {border: 1px solid #dddddd;text-align: left;padding: 8px}\\n"
                            + "</style>\\n"
                            + "<table class='pparameters'>\\n"
                            + "<tr><td style='background-color: #dddddd'><b>დამზღვევის მონაცემები </b></td> <td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthday + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender + "</td></tr>\\n"
                            + "<tr><td><b>ტელეფონი</b></td><td>" + phone + "</td></tr>\\n"
                            + "<tr><td><b>eMail</b></td><td>" + email + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>დაზღვეულის მონაცემები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n2 + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthdayp2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat2 + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code2 + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender2 + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>მოგზაურობის დეტალები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(1)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>რომელ ქვეყანაში მოგზაურობთ</b></td><td>" + country_code + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო პერიოდი</b></td><td>" + datestart + "-" + dateend + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო თანხა</b> </td><td>" + insurancelimit + " ?" + currency + "?</td></tr>\\n"
                            + "<tr><td><b>ბარგის დაზღვევა</b></td><td>" + baggageinsurance + "</td></tr>\\n"
                            + "<tr><td><b>რეისის დაზღვევა</b></td><td>" + reisinsurance + "</td></tr>\\n"
                            //                   + "<tr><td><b>ჯამური თანხა</b></td><td>14 ლარი</td></tr>\\n"

                            + "</table>\\n";

                    buytable = "<style>\\n"
                            + "table.pparameters {font-size: 14px;font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}\\n"
                            + "table.pparameters td, th {border: 1px solid #dddddd;text-align: left;padding: 8px}\\n"
                            + "</style>\\n"
                            + "<table class='pparameters'>\\n"
                            + "<tr><td style='background-color: #dddddd' colspan='2'> <b>დამზღვევის მონაცემები </b></td> </tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthday + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender + "</td></tr>\\n"
                            + "<tr><td><b>ტელეფონი</b></td><td>" + phone + "</td></tr>\\n"
                            + "<tr><td><b>eMail</b></td><td>" + email + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' colspan='2'><b>დაზღვეულის მონაცემები</b></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n2 + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthdayp2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat2 + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code2 + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender2 + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' colspan='2' ><b>მოგზაურობის დეტალები</b></td></tr>\\n"
                            + "<tr><td><b>რომელ ქვეყანაში მოგზაურობთ</b></td><td>" + country_code + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო პერიოდი</b></td><td>" + datestart + "-" + dateend + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო თანხა</b> </td><td>" + insurancelimit + " ?" + currency + "?</td></tr>\\n"
                            + "<tr><td><b>ბარგის დაზღვევა</b></td><td>" + baggageinsurance + "</td></tr>\\n"
                            + "<tr><td><b>რეისის დაზღვევა</b></td><td>" + reisinsurance + "</td></tr>\\n"
                            //                   + "<tr><td><b>ჯამური თანხა</b></td><td>14 ლარი</td></tr>\\n"

                            + "</table>\\n";

                    ss = "{\n\"command\":\"gettravel\",\n"
                            + "\"result\":\"ok\",\n"
                            //                           + "\"addtable\":\"mytable\",\n"
                            + "\"addtable\":\"" + ptable + "\",\n"
                            + "\"addtable2\":\"" + buytable + "\",\n"
                            //                            + "\"addtable\":\"" + 94 + "\",\n"
                            + "\"userid\":\"" + 94 + "\",\n"
                            + "\"proposals\":[";
                    for (int i = 0; i < provider.size(); i++) {
                        System.out.println("provider=  " + provider.get(i)[1]);
                        String sql = "select name from ma_mtpl_params p left join ma_mtpl_benefits b on p.id=b.ma_mtpl_params_id where p.id='"
                                + provider.get(i)[4] + "'";
                        ArrayList<String[]> benefits2 = tools.functions.getResult(sql, tools.functions.isnewcompare);
                        String benefits = "";
                        for (int j = 0; j < benefits2.size(); j++) {
                            if (j == 0) {
                                benefits = "\"" + benefits2.get(j)[0] + "\"";
                            } else {
                                benefits += ",\"" + benefits2.get(j)[0] + "\"";
                            }
                        }
                        String details = "";
                        double price = 0;
                        String paymentschedule2 = paymentschedule;
                        if (paymentschedule.equals("inmounth")) {
                            price = functions.str2double0(provider.get(i)[3]) / 12;

                        } else if (paymentschedule.equals("inkvart")) {
                            price = functions.str2double0(provider.get(i)[3]) / 4;
                        } else if (paymentschedule.equals("inyear2")) {
                            price = functions.str2double0(provider.get(i)[3]) / 2;
                            // paymentschedule2=paymentschedule+"br";
                        } else if (paymentschedule.equals("inonce")) {
                            price = functions.str2double0(provider.get(i)[3]);
                        }
                        System.out.println("price=" + provider.get(i)[3] + "=" + functions.str2int0(provider.get(i)[3]) + "=" + price);
                        details = "\"limit;" + provider.get(i)[2] + " " + currency + "\",\"price;" + provider.get(i)[3] + " " + currency + "\",\"" + paymentschedule + ";" + String.format("%.2f", price) + " " + currency + "\"";
                        String mypdf = command + provider.get(i)[1];
                        //              + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"

                        String addhtml = provider.get(i)[5];
                        if (addhtml == null) {
                            addhtml = "";
                        } else {
                            addhtml = addhtml.replace("\n", "");
                        }
                        String proposal = "{\n\"providerid\":\"" + provider.get(i)[0] + "\",\n"
                                + "\"providername\":\"" + provider.get(i)[1] + "\",\n"
                                + "\"productid\":\"" + provider.get(i)[4] + "\",\n"
                                + "\"limit\":\"" + insurancelimit + "\",\n"
                                + "\"franchise\":\"" + provider.get(i)[6] + "\",\n"
                                + "\"benefits\":[" + benefits + "],\n"
                                + "\"addhtml\":\"" + addhtml + "\",\n"
                                + "\"detals\":[" + details + "],\n"
                                + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"
                                + "\"price\":\"" + provider.get(i)[3] + " " + currency + "\",\n"
                                + "\"payprice\":\"" + provider.get(i)[3] + " " + currency + "\"\n}";
                        if (i == 0) {
                            ss += proposal;
                        } else {
                            ss += "," + proposal;
                        }
                    }

                    ss += "]\n}";
                }
                //System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(ss);

                response.getWriter().write(ss);

            } else if (command.equals("getproperty")) {

//   change user getproperty
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday=++" + birthday);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String namefirstlat = tools.functions.jsonget(job, "namefirstlat");
                System.out.println("namefirstlat=" + namefirstlat);

                String namelastlat = tools.functions.jsonget(job, "namelastlat");
                System.out.println("namelastlat=" + namelastlat);

                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String birthday2 = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday2=" + birthday2);

                String country_code = tools.functions.jsonget(job, "country_code");
                System.out.println("country_code=" + country_code);

// editional person
                String personal_n2 = tools.functions.jsonget(job, "2personal_n");
                System.out.println("2personal_n=" + personal_n2);

                String namefirst2 = tools.functions.jsonget(job, "2namefirst");
                System.out.println("namefirst2=" + namefirst2);

                String namelast2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("namelast2=" + namelast2);

                String namefirstlat2 = tools.functions.jsonget(job, "2namefirstlat");
                System.out.println("namefirstlat2=" + namefirstlat2);

                String namelastlat2 = tools.functions.jsonget(job, "2namelastlat");
                System.out.println("namelastlat2=" + namelastlat2);

                String birthdayp2 = tools.functions.jsonget(job, "2birthday2");
                System.out.println("birthdayp2=" + birthdayp2);

                String citizenship_code2 = tools.functions.jsonget(job, "2citizenship_code");
                System.out.println("citizenship_code2=" + citizenship_code2);

                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender);

                if (forwho.equals("forme")) {
                    personal_n2 = tools.functions.jsonget(job, "personal_n");
                    System.out.println("2personal_n=" + personal_n2);

                    namefirst2 = tools.functions.jsonget(job, "namefirst");
                    System.out.println("namefirst2=" + namefirst2);

                    namelast2 = tools.functions.jsonget(job, "namelast");
                    System.out.println("namelast2=" + namelast2);

                    namefirstlat2 = tools.functions.jsonget(job, "namefirstlat");
                    System.out.println("namefirstlat2=" + namefirstlat2);

                    namelastlat2 = tools.functions.jsonget(job, "namelastlat");
                    System.out.println("namelastlat2=" + namelastlat2);

                    birthdayp2 = tools.functions.jsonget(job, "birthday2");
                    System.out.println("birthdayp2=" + birthdayp2);

                    citizenship_code2 = tools.functions.jsonget(job, "citizenship_code");
                    System.out.println("citizenship_code2=" + citizenship_code2);

                    gender2 = tools.functions.jsonget(job, "gender");
                    System.out.println("gender2=" + gender);

                }

                // product parameters
                String insurancelimit = tools.functions.jsonget(job, "homeinsurancelimit");
                System.out.println("homeinsurancelimit=" + insurancelimit);

                String currency = tools.functions.jsonget(job, "currency");
                System.out.println("currency=" + currency);

                //         inmounth inkvart inkvart inyear2  inonce
                String paymentschedule = tools.functions.jsonget(job, "paymentschedule");
                System.out.println("paymentschedule=" + paymentschedule);
                String paymentschedulet = "";
                if (paymentschedule.equals("inmounth")) {
                    paymentschedulet = "ყოველთვიური";

                } else if (paymentschedule.equals("inkvart")) {
                    paymentschedulet = "კვარტალში ერთხელ";
                } else if (paymentschedule.equals("inyear2")) {
                    paymentschedulet = "წელიწადში ორჯერ";
                    // paymentschedule2=paymentschedule+"br";
                } else if (paymentschedule.equals("inonce")) {
                    paymentschedulet = "წელიწადში ერთხელ";
                }

                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String datestart = tools.functions.jsonget(job, "date12");
                System.out.println("date12=" + datestart);

                String dateend = tools.functions.jsonget(job, "date22");
                System.out.println("dateend=" + dateend);

                String area = tools.functions.jsonget(job, "totalarea");
                System.out.println("totalarea=" + area);

                String cadastrcode = tools.functions.jsonget(job, "cadastrcode");
                System.out.println("cadastrcode=" + cadastrcode);

                String propertyused = tools.functions.jsonget(job, "propertyused");
                System.out.println("propertyused=" + propertyused);

                String town = tools.functions.jsonget(job, "own");
                System.out.println("town=" + town);

                String address = tools.functions.jsonget(job, "address") + " " + town;
                System.out.println("address=" + address);

                String floor = tools.functions.jsonget(job, "floor") + " სართული";
                System.out.println("floor=" + floor);

                String makeyear = " შენობის ასაკი " + tools.functions.jsonget(job, "makeyear");
                System.out.println("makeyear=" + makeyear);

                String areatxt = " ფართი მკვ " + area;

                String fulldetail = floor + makeyear + areatxt;

                String neighborinsurance = tools.functions.jsonget(job, "neighborinsurance");
                System.out.println("neighborinsurance=" + neighborinsurance);

                String neighborsel = " and neighbor_price =0 ";

                if (neighborinsurance.equals("yes")) {
                    neighborsel = " and neighbor_price > 0 ";
                }

                Double aread = functions.str2double0(area);
                System.out.println("aread=" + aread);
// product parameters

                int curr = 0;

                if (currency.equals("_lari")) {
                    curr = 12;
                } else if (currency.equals("_usd")) {
                    curr = 14;
                } else if (currency.equals("_eur")) {
                    curr = 37;
                }
/// insert into
                String qwr = "select provider_id,provider.name,amount_limit*" + aread + ",area_price*" + aread + "+neighbor_price,p.id,add_html,franchise_txt from property_params p,provider \n"
                        + "where provider_id=provider.id " + neighborsel;
                //                      + "where provider_id=provider.id and p.amount_limit='" + insurancelimit + "' and exchange_rate_id='" + curr + "'";
                System.out.println("qwr=    " + qwr);

                ArrayList<String[]> provider = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;

                if (provider.size() == 0) {
                    ss = "{\n\"command\":\"getproperty\",\n"
                            + "\"result\":\"noproposals\"\n}";
                } else {
                    ptable = "<style>\\n"
                            + "table.pparameters {font-size: 14px;font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}\\n"
                            + "table.pparameters td, th {border: 1px solid #dddddd;text-align: left;padding: 8px}\\n"
                            + "</style>\\n"
                            + "<table class='pparameters'>\\n"
                            + "<tr><td style='background-color: #dddddd'><b>დამზღვევის მონაცემები </b></td> <td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthday + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender + "</td></tr>\\n"
                            + "<tr><td><b>ტელეფონი</b></td><td>" + phone + "</td></tr>\\n"
                            + "<tr><td><b>eMail</b></td><td>" + email + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>დაზღვეულის მონაცემები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n2 + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthdayp2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat2 + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code2 + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>დაზღვევის დეტალები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(1)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>რა მიზნით გამოიყენება ქონება</b></td><td>" + propertyused + "</td></tr>\\n"
                            + "<tr><td><b>საკადასტრო კოდი</b></td><td>" + cadastrcode + "</td></tr>\\n"
                            + "<tr><td><b>მისამართი</b></td><td>" + address + "</td></tr>\\n"
                            + "<tr><td><b>დეტალური ინფორმაცია</b></td><td>" + fulldetail + "</td></tr>\\n"
                            + "<tr><td><b>გსურს მეზობლის მიმართ პასუხისმგებლობის დაზღვევა</b></td><td>" + neighborinsurance + "</td></tr>\\n"
                            + "<tr><td><b>გადახდის გრაფიკი</b></td><td>" + paymentschedulet + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო პერიოდი</b></td><td>" + datestart + "-" + dateend + "</td></tr>\\n"
                            //                   + "<tr><td><b>ჯამური თანხა</b></td><td>14 ლარი</td></tr>\\n"

                            + "</table>\\n";
                    buytable = "<style>\\n"
                            + "table.pparameters {font-size: 14px;font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}\\n"
                            + "table.pparameters td, th {border: 1px solid #dddddd;text-align: left;padding: 8px}\\n"
                            + "</style>\\n"
                            + "<table class='pparameters'>\\n"
                            + "<tr><td style='background-color: #dddddd' colspan='2'> <b>დამზღვევის მონაცემები </b></td> </tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthday + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender + "</td></tr>\\n"
                            + "<tr><td><b>ტელეფონი</b></td><td>" + phone + "</td></tr>\\n"
                            + "<tr><td><b>eMail</b></td><td>" + email + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' colspan='2'><b>დაზღვეულის მონაცემები</b></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n2 + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthdayp2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat2 + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code2 + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender2 + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' colspan='2' ><b>მოგზაურობის დეტალები</b></td></tr>\\n"
                            + "<tr><td><b>რომელ ქვეყანაში მოგზაურობთ</b></td><td>" + country_code + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო პერიოდი</b></td><td>" + datestart + "-" + dateend + "</td></tr>\\n"
                            //                   + "<tr><td><b>ჯამური თანხა</b></td><td>14 ლარი</td></tr>\\n"

                            + "</table>\\n";

                    ss = "{\n\"command\":\"getproperty\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"addtable\":\"" + ptable + "\",\n"
                            + "\"addtable2\":\"" + ptable + "\",\n"
                            + "\"userid\":\"" + userid + "\",\n"
                            + "\"proposals\":[";
                    for (int i = 0; i < provider.size(); i++) {
                        System.out.println("provider=  " + provider.get(i)[1]);
                        String sql = "select name from ma_mtpl_params p left join ma_mtpl_benefits b on p.id=b.ma_mtpl_params_id where p.id='"
                                + provider.get(i)[4] + "'";
                        ArrayList<String[]> benefits2 = tools.functions.getResult(sql, tools.functions.isnewcompare);
                        String benefits = "";
                        for (int j = 0; j < benefits2.size(); j++) {
                            if (j == 0) {
                                benefits = "\"" + benefits2.get(j)[0] + "\"";
                            } else {
                                benefits += ",\"" + benefits2.get(j)[0] + "\"";
                            }
                        }
                        String details = "";
                        double price = 0;
                        String paymentschedule2 = paymentschedule;
                        if (paymentschedule.equals("inmounth")) {
                            price = functions.str2double0(provider.get(i)[3]) / 12;

                        } else if (paymentschedule.equals("inkvart")) {
                            price = functions.str2double0(provider.get(i)[3]) / 4;
                        } else if (paymentschedule.equals("inyear2")) {
                            price = functions.str2double0(provider.get(i)[3]) / 2;
                            // paymentschedule2=paymentschedule+"br";
                        } else if (paymentschedule.equals("inonce")) {
                            price = functions.str2double0(provider.get(i)[3]);
                        }
                        System.out.println("price=" + provider.get(i)[3] + "=" + functions.str2int0(provider.get(i)[3]) + "=" + price);

                        details = "\"limit;" + provider.get(i)[2] + " " + currency + "\",\"price;" + provider.get(i)[3] + " " + currency + "\",\"" + paymentschedule + ";" + String.format("%.2f", price) + " " + currency + "\"";
                        String mypdf = command + provider.get(i)[1];
                        //              + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"

                        String addhtml = provider.get(i)[5];
                        if (addhtml == null) {
                            addhtml = "";
                        } else {
                            addhtml = addhtml.replace("\n", "");
                        }
                        String proposal = "{\n\"providerid\":\"" + provider.get(i)[0] + "\",\n"
                                + "\"providername\":\"" + provider.get(i)[1] + "\",\n"
                                + "\"productid\":\"" + provider.get(i)[4] + "\",\n"
                                + "\"limit\":\"" + provider.get(i)[2] + "\",\n"
                                + "\"franchise\":\"" + provider.get(i)[6] + "%\",\n"
                                + "\"benefits\":[" + benefits + "],\n"
                                + "\"addhtml\":\"" + addhtml + "\",\n"
                                + "\"detals\":[" + details + "],\n"
                                + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"
                                //              + "\"price\":\"" + provider.get(i)[3] + " " + currency + "\"\n}";
                                + "\"price\":\"" + provider.get(i)[3] + " " + currency + "\",\n"
                                + "\"payprice\":\"" + String.format("%.2f", price) + " " + currency + "\"\n}";
                        if (i == 0) {
                            ss += proposal;
                        } else {
                            ss += "," + proposal;
                        }
                    }
                    ss += "]\n}";
                }
                //System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(ss);

                response.getWriter().write(ss);
            } else if (command.equals("gethealth")) {

//   change user gethealth
                String userid = tools.functions.jsonget(job, "userid");
                System.out.println("userid=" + userid);

                String forwho = tools.functions.jsonget(job, "forwho");
                System.out.println("forwho=" + forwho);

                String personal_n = tools.functions.jsonget(job, "personal_n");
                System.out.println("personal_n=" + personal_n);

                String birthday = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday=++" + birthday);

                String namefirst = tools.functions.jsonget(job, "namefirst");
                System.out.println("namefirst=" + namefirst);

                String namelast = tools.functions.jsonget(job, "namelast");
                System.out.println("namelast=" + namelast);

                String namefirstlat = tools.functions.jsonget(job, "namefirstlat");
                System.out.println("namefirstlat=" + namefirstlat);

                String namelastlat = tools.functions.jsonget(job, "namelastlat");
                System.out.println("namelastlat=" + namelastlat);

                String citizenship_code = tools.functions.jsonget(job, "citizenship_code");
                System.out.println("citizenship_code=" + citizenship_code);

                String gender = tools.functions.jsonget(job, "gender");
                System.out.println("gender=" + gender);

                String phone = tools.functions.jsonget(job, "phone");
                System.out.println("phone=" + phone);

                String email = tools.functions.jsonget(job, "email");
                System.out.println("email=" + email);

                String birthday2 = tools.functions.jsonget(job, "birthday2");
                System.out.println("birthday2=" + birthday2);

                String country_code = tools.functions.jsonget(job, "country_code");
                System.out.println("country_code=" + country_code);

// editional person
                String personal_n2 = tools.functions.jsonget(job, "2personal_n");
                System.out.println("2personal_n=" + personal_n2);

                String namefirst2 = tools.functions.jsonget(job, "2namefirst");
                System.out.println("namefirst2=" + namefirst2);

                String namelast2 = tools.functions.jsonget(job, "2namelast");
                System.out.println("namelast2=" + namelast2);

                String namefirstlat2 = tools.functions.jsonget(job, "2namefirstlat");
                System.out.println("namefirstlat2=" + namefirstlat2);

                String namelastlat2 = tools.functions.jsonget(job, "2namelastlat");
                System.out.println("namelastlat2=" + namelastlat2);

                String birthdayp2 = tools.functions.jsonget(job, "2birthday2");
                System.out.println("birthdayp2=" + birthdayp2);

                String citizenship_code2 = tools.functions.jsonget(job, "2citizenship_code");
                System.out.println("citizenship_code2=" + citizenship_code2);

                String gender2 = tools.functions.jsonget(job, "2gender");
                System.out.println("gender2=" + gender);

                if (forwho.equals("forme")) {
                    personal_n2 = tools.functions.jsonget(job, "personal_n");
                    System.out.println("2personal_n=" + personal_n2);

                    namefirst2 = tools.functions.jsonget(job, "namefirst");
                    System.out.println("namefirst2=" + namefirst2);

                    namelast2 = tools.functions.jsonget(job, "namelast");
                    System.out.println("namelast2=" + namelast2);

                    namefirstlat2 = tools.functions.jsonget(job, "namefirstlat");
                    System.out.println("namefirstlat2=" + namefirstlat2);

                    namelastlat2 = tools.functions.jsonget(job, "namelastlat");
                    System.out.println("namelastlat2=" + namelastlat2);

                    birthdayp2 = tools.functions.jsonget(job, "birthday2");
                    System.out.println("birthdayp2=" + birthdayp2);

                    citizenship_code2 = tools.functions.jsonget(job, "citizenship_code");
                    System.out.println("citizenship_code2=" + citizenship_code2);

                    gender2 = tools.functions.jsonget(job, "gender");
                    System.out.println("gender2=" + gender);

                }

                // product parameters
                String insurancelimit = tools.functions.jsonget(job, "homeinsurancelimit");
                System.out.println("insurancelimit=" + insurancelimit);

                String currency = tools.functions.jsonget(job, "currency");
                System.out.println("currency=" + currency);

                String paymentschedule = tools.functions.jsonget(job, "paymentschedule");
                System.out.println("paymentschedule=" + paymentschedule);
                String paymentschedulet = "";
                if (paymentschedule.equals("inmounth")) {
                    paymentschedulet = "ყოველთვიური";

                } else if (paymentschedule.equals("inkvart")) {
                    paymentschedulet = "კვარტალში ერთხელ";
                } else if (paymentschedule.equals("inyear2")) {
                    paymentschedulet = "წელიწადში ორჯერ";
                    // paymentschedule2=paymentschedule+"br";
                } else if (paymentschedule.equals("inonce")) {
                    paymentschedulet = "წელიწადში ერთხელ";
                }
                String checkboxrule = tools.functions.jsonget(job, "checkboxrule");
                System.out.println("checkboxrule=" + checkboxrule);

                String datestart = tools.functions.jsonget(job, "date12");
                System.out.println("date12=" + datestart);

                String dateend = tools.functions.jsonget(job, "date22");
                System.out.println("dateend=" + dateend);

                int curr = 12;

                if (currency.equals("_lari")) {
                    curr = 12;
                } else if (currency.equals("_usd")) {
                    curr = 14;
                } else if (currency.equals("_eur")) {
                    curr = 37;
                }
/// insert into
                String qwr = "select provider_id,provider.name,amount_limit,amount_price,p.id,add_html,franchise from health_params p,provider \n"
                        + "where provider_id=provider.id  and exchange_rate_id='" + curr + "'";

                System.out.println("qwr=    " + qwr);

                ArrayList<String[]> provider = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;

                if (provider.size() == 0) {
                    ss = "{\n\"command\":\"gethealth\",\n"
                            + "\"result\":\"noproposals\"\n}";
                } else {
                    ptable = "<style>\\n"
                            + "table.pparameters {font-size: 14px;font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}\\n"
                            + "table.pparameters td, th {border: 1px solid #dddddd;text-align: left;padding: 8px}\\n"
                            + "</style>\\n"
                            + "<table class='pparameters'>\\n"
                            + "<tr><td style='background-color: #dddddd'><b>დამზღვევის მონაცემები </b></td> <td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthday + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code + "</td></tr>\\n"
                            + "<tr><td><b>სქესი</b></td><td>" + gender + "</td></tr>\\n"
                            + "<tr><td><b>ტელეფონი</b></td><td>" + phone + "</td></tr>\\n"
                            + "<tr><td><b>eMail</b></td><td>" + email + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>დაზღვეულის მონაცემები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(0)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                            + "<tr><td><b>პირადი N</b></td><td>" + personal_n2 + "</td></tr>\\n"
                            + "<tr><td><b>დაბადების თარიღი</b></td><td>" + birthdayp2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი</b></td><td>" + namefirst2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი</b></td><td>" + namelast2 + "</td></tr>\\n"
                            + "<tr><td><b>სახელი ლათინურად</b></td><td>" + namefirstlat2 + "</td></tr>\\n"
                            + "<tr><td><b>გვარი ლათინურად</b></td><td>" + namelastlat2 + "</td></tr>\\n"
                            + "<tr><td><b>მოქალაქეობა</b></td><td>" + citizenship_code2 + "</td></tr>\\n"
                            + "<tr><td style='background-color: #dddddd' ><b>დაზღვევის დეტალები</b></td><td style='background-color: #dddddd'><a href='#' onclick='myshowtab(1)' >პარამეტრების ცვლილება</a></td></tr>\\n"
                             + "<tr><td><b>გადახდის გრაფიკი</b></td><td>" + paymentschedulet + "</td></tr>\\n"
                            + "<tr><td><b>სადაზღვევო პერიოდი</b></td><td>" + datestart + "-" + dateend + "</td></tr>\\n"
                            //                   + "<tr><td><b>ჯამური თანხა</b></td><td>14 ლარი</td></tr>\\n"

                            + "</table>\\n";

                    ss = "{\n\"command\":\"gethealth\",\n"
                            + "\"result\":\"ok\",\n"
                            + "\"addtable\":\"" + ptable + "\",\n"
                            + "\"addtable2\":\"" + ptable + "\",\n"
                            + "\"userid\":\"" + 94 + "\",\n"
                            + "\"proposals\":[";
                    for (int i = 0; i < provider.size(); i++) {
                        System.out.println("provider=  " + provider.get(i)[1]);
                        String sql = "select name from ma_mtpl_params p left join ma_mtpl_benefits b on p.id=b.ma_mtpl_params_id where p.id='"
                                + provider.get(i)[4] + "'";
                        ArrayList<String[]> benefits2 = tools.functions.getResult(sql, tools.functions.isnewcompare);
                        String benefits = "";
                        for (int j = 0; j < benefits2.size(); j++) {
                            if (j == 0) {
                                benefits = "\"" + benefits2.get(j)[0] + "\"";
                            } else {
                                benefits += ",\"" + benefits2.get(j)[0] + "\"";
                            }
                        }
                        String details = "";
                        double price = 0;
                        String paymentschedule2 = paymentschedule;
                        if (paymentschedule.equals("inmounth")) {
                            price = functions.str2double0(provider.get(i)[3]) / 12;

                        } else if (paymentschedule.equals("inkvart")) {
                            price = functions.str2double0(provider.get(i)[3]) / 4;
                        } else if (paymentschedule.equals("inyear2")) {
                            price = functions.str2double0(provider.get(i)[3]) / 2;
                            // paymentschedule2=paymentschedule+"br";
                        } else if (paymentschedule.equals("inonce")) {
                            price = functions.str2double0(provider.get(i)[3]);
                        }
                        System.out.println("price=" + provider.get(i)[3] + "=" + functions.str2int0(provider.get(i)[3]) + "=" + price);
                        details = "\"limit;" + provider.get(i)[2] + " " + currency + "\",\"price;" + provider.get(i)[3] + " " + currency + "\",\"" + paymentschedule + ";" + String.format("%.2f", price) + " " + currency + "\"";
                        String mypdf = command + provider.get(i)[1];
                        String addhtml = provider.get(i)[5];
                        if (addhtml == null) {
                            addhtml = "";
                        } else {
                            addhtml = addhtml.replace("\n", "");
                        }

                        String proposal = "{\n\"providerid\":\"" + provider.get(i)[0] + "\",\n"
                                + "\"providername\":\"" + provider.get(i)[1] + "\",\n"
                                + "\"productid\":\"" + provider.get(i)[4] + "\",\n"
                                + "\"limit\":\"" + provider.get(i)[2] + "\",\n"
                                + "\"franchise\":\"" + provider.get(i)[6] + "\",\n"
                                + "\"benefits\":[" + benefits + "],\n"
                                + "\"addhtml\":\"" + addhtml + "\",\n"
                                + "\"detals\":[" + details + "],\n"
                                + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n"
                                //              + "\"price\":\"" + provider.get(i)[3] + " " + currency + "\"\n}";
                                + "\"price\":\"" + provider.get(i)[3] + " " + currency + "\",\n"
                                + "\"payprice\":\"" + String.format("%.2f", price) + " " + currency + "\"\n}";
                        if (i == 0) {
                            ss += proposal;
                        } else {
                            ss += "," + proposal;
                        }
                    }
                    ss += "]\n}";
                }
                //System.out.println("2 s1=     " + s1.get(0)[0]);

                System.out.println(ss);

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

                ArrayList<String> Detailitem = new ArrayList<String>();

//                Detailitem.add("დამზღვევი;"+namefirst+" "+namelast);
//                Detailitem.add("დაზღვეული;"+namefirst2+" "+namelast2);
                Detailitem.add("დამზღვევი;ალექსანდრე სარჩიმელია");
                Detailitem.add("დაზღვეული;ალექსანდრე სარჩიმელია");
                Detailitem.add("სახ. ნომერი;" + tools.functions.jsonget(job, "carnumber"));
                Detailitem.add("ვინ. კოდი;" + tools.functions.jsonget(job, "carvin"));
                Detailitem.add("ლიმიტი;" + tools.functions.jsonget(job, "liabilitylimit"));
                Detailitem.add("სადაზღვევო პერიოდი;" + tools.functions.jsonget(job, "date1") + "-" + tools.functions.jsonget(job, "date2"));

                Detailitem.add("");

                Detailitem.add("ძირითადი დაფარვა;ლიმიტი;ფრანშიზა;header");
                Detailitem.add("სასწრაფო სამედიცინო დაფარვის,ულიმიტო;$0");
                Detailitem.add("გადაუდებელი ჰოსპიტალური მკურნალობის ხარჯები;$500 დღე (სულ$20K);$0");
                Detailitem.add("გადაუდებელი ამბულატორიული მკურნალობის ხარჯები;$5K;$100");
                Detailitem.add("გადაუდებელი სტომატოლოგიური მკურნალობის ხარჯები;$500;$100");
                Detailitem.add("გადაუდებელი ოფთალმოლოგიური მკურნალობის ხარჯები;$1000;$100");
                Detailitem.add("დაზღვეულის რეპატრიაცია;$1000;$0");

                ArrayList<String[]> s2 = tools.functions.getResult(qwr, tools.functions.isnewcompare);
                String ss;
                //               String mypdf = command + provider.get(i)[1];
                //              + "\"pdf\":\"pdf/" + mypdf + ".pdf\",\n" 
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
                            + "\"date2\":\"" + tools.functions.jsonget(job, "date2") + "\",\n"
                            + "\"productid\":\"" + tools.functions.jsonget(job, "productid") + "\",\n"
                            + "\"birthday2\":\"" + tools.functions.jsonget(job, "birthday2") + "\",\n"
                            + "\"2birthday2\":\"" + tools.functions.jsonget(job, "2birthday2") + "\",\n"
                            + "\"date12\":\"" + tools.functions.jsonget(job, "date12") + "\",\n"
                            + "\"date22\":\"" + tools.functions.jsonget(job, "date22") + "\",\n"
                            + "\"gender\":\"" + tools.functions.jsonget(job, "gender") + "\",\n"
                            + "\"modelid\":\"" + tools.functions.jsonget(job, "modelid") + "\",\n"
                            + "\"Detailitem\":[";
                    ss += "\"\"";
                    for (int i = 0; i < Detailitem.size(); i++) {
                        ss += ",\"" + Detailitem.get(i) + "\"\n";
                    }

                    //ss+="\"\"";
                    //for (int i=0;i<Detailitem2.size();i++)
                    //    ss+="\""+ Detailitem2.get(i) + "\",\n";
                    ss += "],\n}";

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
