import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class PeopleContainer {
    public static ArrayList<Person> PeopleList = new ArrayList();
    public static ArrayList<PDate> dates = new ArrayList();
    public static ArrayList<PDate> overlap_dates = new ArrayList();

    public static void addPerson(Person P) {
        PeopleList.add(P);
    }

    public static void init_dates() {
        for(Person p: PeopleList) {
            for(PDate d : p.getAvailable()) {
                dates.add(d);
            }
        }
    }

    public static ArrayList<PDate> getDates() {
        return dates;
    }

    public static ArrayList<Person> getPeopleList() {
        return PeopleList;
    }

    public static ArrayList<PDate> schedule() {
        ArrayList<PDate> A = new ArrayList<>();
        int num_ppl = PeopleList.size();
        System.out.println(PeopleList.size());
        Collections.sort(dates, new DateSortByMonth());

        DateSortByMonth comp = new DateSortByMonth();

        int i = 0;
        int num_sets = 0;
        PDate temp;

        while(i < dates.size()) {
            // error is here
            if(A.isEmpty()) {
                A.add(dates.get(i));
                i++;
                num_sets = 1;
            }
            else {
                if (A.get(A.size() - 1).overlap(dates.get(i))) {
                    temp = new PDate(dates.get(i).getMonth(), dates.get(i).getDay(), dates.get(i).getS_hour(), dates.get(i).getS_min(), A.get(A.size() - 1).getE_hour(), A.get(A.size() - 1).getE_min(), false, A.get(A.size() - 1).getYear());
                    A.add(temp);
                    num_sets++;
                    System.out.println(overlap_dates.size());
                    if (num_ppl == num_sets) {
                        overlap_dates.add(A.get(A.size() - 1));
                        System.out.println(overlap_dates.size());
                        //i ++;
                        if (i < dates.size()) {
                            A.add(dates.get(i));
                            num_sets = 1;
                        }
                        else {
                            break;
                        }
                    }
                }
                else {
                    num_sets = 1;
                    A.add(dates.get(i));
                }
                i++;
            }

        }
        Collections.sort(overlap_dates, new DateSortByMonth());

        return overlap_dates;
    }

    public static String[][] toArray() {
        String[][] peoples = new String[PeopleList.size()][3];
        for(int i = 0; i < PeopleList.size(); i++){
            peoples[i][1] = "";
            if (!(PeopleList.get(i).getAllergy() == null)) {
                peoples[i][1] = peoples[i][1] + "," +  PeopleList.get(i).getAllergy();
            }
            if(!(PeopleList.get(i).getDiet() == null)) {
                peoples[i][2] = PeopleList.get(i).getDiet();
            }
            peoples[i][0] = PeopleList.get(i).getName();
        }
        return peoples;
    }

    public static ArrayList<String[]> DatetoArrayString(String[] month) {
        schedule();
        ArrayList<String[]> arr = new ArrayList<>();
        String[] temp = new String[2];
        PDate s = overlap_dates.get(0);
        PDate d;
        int i = 1;
        while (i < overlap_dates.size()) {
            arr.add(temp.clone());
            d = overlap_dates.get(i);
            if (s.getCalendar_s().get(Calendar.DAY_OF_YEAR) == d.getCalendar_s().get(Calendar.DAY_OF_YEAR)) {
                if(s.getS_hour() != s.getE_hour()) {
                    if(s.getS_min() != s.getE_min()) {
                        arr.get(arr.size()-1)[0] = "" + month[(s.getMonth()-1)].substring(0, 1).toUpperCase() + month[(s.getMonth()-1)].substring(1) + " " + s.getDay() + " " + s.getYear();
                        //System.out.println(temp[0]);
                        arr.get(arr.size()-1)[1] = "" + "<br />" + "&emsp;&emsp;" + time_toString(s.getS_hour()) + ':' + time_toString(s.getS_min()) + " to " + time_toString(s.getE_hour()) + ':' + time_toString(s.getE_min());
                        //System.out.println(temp[1]);
                    }
                }
                while((s.getCalendar_s().get(Calendar.DAY_OF_YEAR) == d.getCalendar_s().get(Calendar.DAY_OF_YEAR)) && (i < overlap_dates.size())) {
                    if(d.getS_hour() != d.getE_hour()) {
                        if(s.getS_min() != s.getE_min()) {
                            arr.get(arr.size()-1)[1] = arr.get(arr.size()-1)[1] + "<br />" + "&emsp;&emsp;" + time_toString(d.getS_hour()) + ':' + time_toString(d.getS_min()) + " to " + time_toString(d.getE_hour()) + ':' + time_toString(d.getE_min());
                            //System.out.println(temp[1]);
                        }
                    }
                    s = overlap_dates.get(i);
                    i++;
                }

            }
            else {
                if(s.getS_hour() != s.getE_hour()) {
                    if(s.getS_min() != s.getE_min()) {
                        arr.get(arr.size()-1)[0] = ""  + month[(s.getMonth()-1)].substring(0, 1).toUpperCase() + month[(s.getMonth()-1)].substring(1) + " " + s.getDay() + " " + s.getYear();
                        //System.out.println(temp[0]);
                        arr.get(arr.size()-1)[1] = "" + "<br />" + "&emsp;&emsp;" + time_toString(s.getS_hour()) + ':' + time_toString(s.getS_min()) + " to " + time_toString(s.getE_hour()) + ':' + time_toString(s.getE_min());
                        //System.out.println(temp[1]);
                    }
                }
                s = overlap_dates.get(i);
                i++;
            }
        }
        arr.add(temp.clone());
        if(s.getS_hour() != s.getE_hour()) {
            if (s.getS_min() != s.getE_min()) {
                arr.get(arr.size()-1)[0] = ""  + month[(s.getMonth()-1)].substring(0, 1).toUpperCase() + month[(s.getMonth()-1)].substring(1) + " " + s.getDay() + " " + s.getYear();
                //System.out.println(temp[0]);

                arr.get(arr.size()-1)[1] = "" + "<br />" + "&emsp;&emsp;" + time_toString(s.getS_hour()) + ':' + time_toString(s.getS_min()) + " to " + time_toString(s.getE_hour()) + ':' + time_toString(s.getE_min());

            }
        }

        return arr;
    }

    public static String time_toString(int time) {
        if (time < 10) {
            return "" + 0 + time;
        }
        return "" + time;
    }

    public static void main(String[] args) {
        PDate d0 = new PDate(9, 20, 9, 0, 20, 30, false, 2017);
        PDate d1 = new PDate(9, 20, 9, 0, 23, 59, true, 2017);
        PDate d2 = new PDate(9, 19, 9, 0, 23, 59, true, 2017);
        PDate d3 = new PDate(9, 19, 14, 0, 22, 59, false, 2017);

        Person p1 = new Person();
        Person p2 = new Person();
        p2.addAvailable(d0);
        p2.addAvailable(d3);
        p1.addAvailable(d1);
        p1.addAvailable(d2);

        addPerson(p2);
        addPerson(p1);

        //init_dates();
        //ArrayList<PDate> av_d = schedule();

        String[] monthStr = {"january", "february", "march", "april", "may", "june",
                "july", "august", "september", "october", "november",
                "december"};
        DatetoArrayString(monthStr);
        toArray();
    }

}