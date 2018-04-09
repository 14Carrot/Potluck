import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class PDate {
    private int month;
    private int day;
    private int year;
    private int s_hour;
    private int s_min;
    private int e_hour;
    private int e_min;
    private boolean allday;
    private Calendar calendar_s;
    private Calendar calendar_e;

    public PDate(int month, int day, int s_hour, int s_min, int e_hour, int e_min, boolean allday, int year) {

        this.year = year;
        switch (month){
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                if( day > 0 && day <= 31) {
                    this.month = month;
                    this.day = day;
                    break;
                }
                else {
                    System.out.println("Error in creating object Date: integer for day must be between 1 and 31 for month input.");
                    System.exit(1);
                    break;
                }
            case 2:
                if( day > 0 && day <= 28) {
                    this.month = month;
                    this.day = day;
                    break;
                }
                else {
                    System.out.println("Error in creating object Date: integer for day must be between 1 and 28 for feb.");
                    System.exit(1);
                    break;
                }
            case 4: case 6: case 9: case 11:
                if( day > 0 && day <= 30) {
                    this.month = month;
                    this.day = day;
                    break;
                }
                else {
                    System.out.println("Error in creating object Date: integer for day must be between 1 and 30 for month input.");
                    System.exit(1);
                    break;
                }
            default:
                System.out.println("Error in creating object Date: integer for month must be between 1 and 12.");
                System.exit(1);
                break;
        }
        //System.out.println(s_hour);
        //System.out.println(e_hour);
        //System.out.println(s_min);
        //System.out.println(e_min);

        if (allday){
            this.allday = allday;
            this.s_hour = 9;
            this.s_min = 0;
            this.e_hour = 22;
            this.e_min = 0;

            this.calendar_s = new GregorianCalendar(year,month,day,0,0);

            this.calendar_e = new GregorianCalendar(year,month,day,21,59);
        }
        else {
            this.allday = allday;
            if (s_hour < e_hour) {
                this.s_hour = s_hour;
                this.e_hour = e_hour;
                this.s_min = s_min;
                this.e_min = e_min;

            } else if (s_hour == e_hour) {
                this.s_hour = s_hour;
                this.e_hour = e_hour;
                if (s_min <= e_min) {
                    this.s_min = s_min;
                    this.e_min = e_min;
                }
            }
            else {

                System.out.println("End time must be greater than start time");
                System.exit(1);
            }

            this.calendar_s = new GregorianCalendar(year,month,day,s_hour,s_min);

            this.calendar_e = new GregorianCalendar(year,month,day,e_hour,e_min);

        }


    }

    public boolean overlap(PDate d) {
        if(this.getYear() == d.getYear()) {
            if (this.getMonth() == d.getMonth()) {
                if (this.getCalendar_s().get(Calendar.DAY_OF_YEAR) == d.getCalendar_s().get(Calendar.DAY_OF_YEAR)) {
                    if ((d.getS_hour() - this.getE_hour()) <= 0) {
                        //System.out.println(true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public int getS_hour() {
        return s_hour;
    }

    public int getE_hour() {
        return e_hour;
    }

    public int getS_min() {
        return s_min;
    }

    public int getE_min() {
        return e_min;
    }

    public boolean isAllday() {
        return allday;
    }

    public Calendar getCalendar_e() {
        return calendar_e;
    }

    public Calendar getCalendar_s() {
        return calendar_s;
    }


    public String time_toString(int time) {
        if (time < 10) {
            return "" + 0 + time;
        }
        return "" + time;
    }

    public String toString(String month) {
        return ("" + month + " " + this.day + " " + this.year + " from " + this.time_toString(s_hour) + ":" +
                this.time_toString(s_min) + " to " + this.time_toString(e_hour) + ":" + this.time_toString(e_min));
    }

}


class DateSortByMonth implements Comparator <PDate> {

    public int compare(PDate date1, PDate date2) {
        DateSortByDay dsd = new DateSortByDay();

        if (date1.getMonth() > date2.getMonth())
            return 1;
        else if (date1.getMonth() < date2.getMonth())
            return -1;

        return dsd.compare(date1, date2);
    }
}

class DateSortByDay implements Comparator <PDate> {

    public int compare(PDate date1, PDate date2) {

        DateSortByHourStart dsd = new DateSortByHourStart();

        if (date1.getDay() > date2.getDay())
            return 1;
        else if (date1.getDay() < date2.getDay())
            return -1;

        return dsd.compare(date1, date2);
    }
}

class DateSortByHourStart implements Comparator <PDate> {

    public int compare(PDate date1, PDate date2) {

        DateSortByMinStart dsd = new DateSortByMinStart();

        if (date1.getS_hour() > date2.getS_hour())
            return 1;
        else if (date1.getS_hour() < date2.getS_hour())
            return -1;

        return dsd.compare(date1, date2);
    }
}

class DateSortByMinStart implements Comparator <PDate> {

    public int compare(PDate date1, PDate date2) {

        if (date1.getS_min() > date2.getS_min())
            return 1;
        else if (date1.getS_min() < date2.getS_min())
            return -1;

        return 0;
    }
}

class DateSortByHourEnd implements Comparator <PDate> {

    public int compare(PDate date1, PDate date2) {

        DateSortByMinEnd dsd = new DateSortByMinEnd();

        if (date1.getS_hour() > date2.getS_hour())
            return 1;
        else if (date1.getS_hour() < date2.getS_hour())
            return -1;

        return dsd.compare(date1, date2);
    }
}

class DateSortByMinEnd implements Comparator <PDate> {

    public int compare(PDate date1, PDate date2) {

        if (date1.getS_min() > date2.getS_min())
            return 1;
        else if (date1.getS_min() < date2.getS_min())
            return -1;

        return 0;
    }
}