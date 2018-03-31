
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Person {
    private String name;
    private ArrayList diet;
    private String cuisine;
    private ArrayList<String> type;
    private ArrayList<Date> available;
    private ArrayList<Date> unavailable;

    public Person() {
        this.name = null;
        this.diet = new ArrayList();
        this.cuisine = null;
        this.type = new ArrayList();
        this.available = new ArrayList();
        this.unavailable = new ArrayList();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiet(ArrayList diet) {
        int j = 0;
        if((diet.size() + this.diet.size()) <= 7) {
            for (int i = 0; i < diet.size(); i++) {
                this.diet.add(diet.get(i));
            }
        }
        else {
            System.out.println("Error: unable to set diet, array input is too long.");
        }
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public void setType(ArrayList<String> type) {
        int j = 0;
        if((type.size() + this.type.size()) <= 7) {
            for (int i = 0; i < type.size(); i++) {
                this.type.add(type.get(i));
            }
        }
        else {
            System.out.println("Error: unable to set type, array input is too long.");
        }
    }

    public void addAvailable(Date date) {
        if(this.unavailable.isEmpty()) {
            this.available.add(date);
        }
        else {
            //throw exception
        }
    }

    public void addUnavailable(Date date) {
        if(this.available.isEmpty()) {
            this.unavailable.add(date);
        }
        else {
            //throw exception
        }
    }

    public ArrayList<Date> getAvailable() {
        if(this.available.isEmpty()) {
            Collections.sort(this.unavailable, new DateSortByMonth());
            Date s = (Date) this.unavailable.remove(0);
            for (Date d: this.unavailable) {
                if (s.getCalendar_s().get(Calendar.DAY_OF_MONTH) == d.getCalendar_s().get(Calendar.DAY_OF_MONTH)){
                    this.available.add(new Date(d.getMonth(), d.getDay(), s.getE_hour(), s.getE_min(), d.getS_hour(), s.getS_min(), false, d.getYear()));
                }
                else {
                    if(s.getE_hour() != 22) {
                        this.available.add(new Date(s.getMonth(), s.getDay(), s.getE_hour(), s.getE_min(), 22, 0, false, s.getYear()));
                    }
                    if (d.getDay() - s.getDay() == 1) {
                        if(s.getE_hour() > 9) {
                            this.available.add(new Date(d.getMonth(), d.getDay(), 9, 0, d.getS_hour(), d.getS_min(), false, s.getYear()));
                        }
                    }
                    else {
                        int dif = d.getDay()- s.getDay();
                        int i = s.getDay();
                        while (dif > 1) {
                            i++;
                            if (Date.isValidDate(s.getMonth(), i, s.getYear())) {
                                this.available.add(new Date(s.getMonth(), i, 9, 0, 22, 0, true, s.getYear()));
                            }
                            dif --;
                        }
                    }
                }
            }
        }
        return this.available;
    }
}