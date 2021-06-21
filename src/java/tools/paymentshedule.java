/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author paata
 */
public class paymentshedule {

    public static String[] makeshedule(String paymentschedule) {
        System.out.println("I am in schedurer  paymentschedule==="+paymentschedule);
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

        return schedule;
    }
}
