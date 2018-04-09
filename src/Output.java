import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

public class Output{


    public static void writeHTML(String[][] rescipeArr, Map<String, String> recipeDict, Map<String, String> eventDict, String[] monthStr){

        String name = "Potluckr";
        if (eventDict.containsKey("Subject")) {
            name = eventDict.get("Subject").replace("\"", "");
        }


        String Top = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<title>Potluckr</title>\n" +
                "<link rel=\"icon\" href=\"http://icons.iconarchive.com/icons/icons8/windows-8/512/Food-Restaurant-2-icon.png\" type=\"image/png\">\n" +
                "</head>";

        String Style = "<style>\n" +
                "body, html {\n" +
                "    height: 75%;\n" +
                "    margin: 5;\n" +
                "}\n" +
                ".hero-image {\n" +
                "  background-image: url(\"https://www.frameworkhomeownership.org/images/uploads/potluck-like-a-pro-large-fb-share-image.png\");\n" +
                "  height: 50%;\n" +
                "  background-position: center;\n" +
                "  background-repeat: no-repeat;\n" +
                "  background-size: cover;\n" +
                "  position: relative;\n" +
                "}\n" +
                ".hero-text {\n" +
                "  text-align: center;\n" +
                "  position: absolute;\n" +
                "  top: 50%;\n" +
                "  left: 50%;\n" +
                "  transform: translate(-50%, -50%);\n" +
                "  \n" +
                "}\n" +
                "mark {\n" +
                "    padding: 5px 5px;" +
                "    background-color: RGB(126, 70, 62);\n" +
                "    color: RGB(255, 254, 233);\n" +
                "}\n" +
                "\n" +
                "</style>";

        String Body ="<body style=\"background-color: RGB(255, 254, 233);\">\n" +
                "<div class=\"hero-image\">\n" +
                "<div class=\"hero-text\">\n" +
                "<h1 style=\"font-size:60px\"><mark>"+name+"<mark></h1>\n" +
                "</div></div>" +
                "\n";

        String End = "\n" +
                "</body>\n" +
                "</html>" +
                "";
        String str = "" + Top + Style + Body + eventStr(eventDict) + attendeeStr() + availabilityStr(monthStr) + resipieStr(rescipeArr, recipeDict) + End;
        writeFile("Potluckr", str);
    }

    public static void writeFile(String name, String str){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(name+".html"));
            writer.write(str);
            writer.close();
        }catch (Exception e) {System.out.println(e.toString());}
    }

    public static String resipieStr(String[][] rescipeArr, Map<String, String> recipeDict) {
        String resipes ="<h2>Recipes</h2>\n" + "<p>";
        if(recipeDict.size() > 0){
            if (recipeDict.containsKey("cuisine")) {
                resipes += "Cuisine: " + recipeDict.get("cuisine").replace("+", " ") + "<br />";
            }if (recipeDict.containsKey("type")) {
                resipes += "Type: " + recipeDict.get("type").replace("+", " ") + "<br />";
            }if (recipeDict.containsKey("includeIngredients")) {
                resipes += "Ingredients: " + recipeDict.get("includeIngredients").replace("%2C+", ", ").replace("+", " ") + "<br />";
            }if (recipeDict.containsKey("intolerances")) {
                resipes += "Allergies: " + recipeDict.get("intolerances").replace("%2C+", ", ").replace("+", " ") + "<br />";
            }if (recipeDict.containsKey("diet")) {
                resipes += "Diet: " + recipeDict.get("diet").replace("+", " ") + "<br />";
            }
            resipes += "</p>";
        }
        if(rescipeArr.length > 0) {
            resipes += "<p><b>Choices: </b></p>";
            for (int i = 0; i < rescipeArr.length; i++) {
                resipes += "<p><a href=\"" + rescipeArr[i][1] + "\"target=\"_blank\"><mark>" + rescipeArr[i][0] + "</mark></a></p>\n\n";
                resipes += "<a href=\"" + rescipeArr[i][1] + "\" target=\"_blank\"><img src=\"" + rescipeArr[i][2] + "\" width=\"350\" height=\"250\"></a>\n\n";
            }
        }

        return resipes;
    }

    public static String eventStr(Map<String, String> eventDict) {
        String event = "";
        if(eventDict.size() > 1 || (eventDict.size() == 1 && ! eventDict.containsKey("Subject"))){
            event += "<h2>Event</h2>\n" + "<p>";
            if (eventDict.containsKey("Description")) {
                event += "Description: " + eventDict.get("Description").replace("\"", "") + "<br />";
            }
            if (eventDict.containsKey("Location")) {
                event += "Location: " + eventDict.get("Location").replace("\"", "") + "<br />";
            }
            if (eventDict.containsKey("Start Date")) {
                event += "Date: " + eventDict.get("Start Date") + "<br />";
            }

            if (eventDict.containsKey("Start Time")) {
                event += "Time: " + eventDict.get("Start Time");
                if (eventDict.containsKey("End Time")) {
                    event += " - " + eventDict.get("End Time");
                }
                event += "<br />";
            }
            if (eventDict.containsKey("Private")) {
                if (eventDict.get("Private") == "True") {
                    event += "Private: True<br />";
                }
            }
            event += "</p>";

        }
        return event;
    }

    public static String attendeeStr() {
        int size = PeopleContainer.toArray().length;
        if(size > 0) {
            String str = "<h2>Attendees</h2>\n" + "<p>";
            for (int i = 0; i < size; i++) {
                str += "<b>" + PeopleContainer.toArray()[i][0] + "</b><br />";
                if (PeopleContainer.toArray()[i][1] != "") {
                    str += "Allergies: " + PeopleContainer.toArray()[i][1].substring(1).replace("%2C+", ", ").replace("+", " ") + "<br />";
                }
                if (PeopleContainer.toArray()[i][2] != null) {
                    str += "Diet: " + PeopleContainer.toArray()[i][2] + "<br />";
                }
                str += "<br />";
            }
            str += "</p>";
            return str;
        }else{return "";}
    }
    public static String availabilityStr(String[] monthStr) {
        PeopleContainer.init_dates();
        if(! PeopleContainer.dates.isEmpty()) {
            ArrayList<String[]> array = PeopleContainer.DatetoArrayString(monthStr);
            int size = array.size();
            String str = "<h2>Availability</h2>\n" + "<p>";
            for (int i = 0; i < size; i++) {
                str += "<b>" + array.get(i)[0] + ":</b>";
                str += array.get(i)[1] + "<br />";
            }
            return str;
        }else{return "";}
    }
}