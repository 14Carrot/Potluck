//Grammar
//Lexer rules start with Upper case letter, parser rules start with lower case letter
/*todo: maybe output all the availability / person / recipe info as separate .txt files, but main .csv file should
include the basic schedule info. unsure whether to dynamically assess date / start time or hardcode
idea is to separate two main paths, SCHEDULE(solidify the csv) AND PLAN(person + food)
note, { java code in here } i.e. in brackets */

//note, to handle OPTIONALS "?", syntax: if ($BLAH.text != null...)

grammar test;


@header{
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.ParseException;

}
@members {
Map<String, String> recipeDict = new HashMap<>();
Map<String, Integer> diets = new HashMap<>();
int[] dietInt = new int[8];
String[] dietStr = {"vegan", "ovo vegetarian", "lacto vegetarian",
                    "vegetarian", "pescetarian", "paleo", "primal"};
String[][] rescipeArr = new String[][]{{"Arugula Pumpkin Seed Pesto Sauerkraut Lasagna", "https://spoonacular.com/recipes/-8799", "https://spoonacular.com/recipeImages/8799-312x231.jpg"}, {"Asparagus", "https://spoonacular.com/recipes/-18502", "https://spoonacular.com/recipeImages/18502-312x231.jpg"}, {"Barley Risotto With Golden Beets & Greens", "https://spoonacular.com/recipes/-20433", "https://spoonacular.com/recipeImages/20433-312x231.jpg"}, {"Fretwell (italian Vegetable) Soup", "https://spoonacular.com/recipes/-20494", "https://spoonacular.com/recipeImages/20494-312x231.jpg"}, {"Pasta Pomodoro My Way'", "https://spoonacular.com/recipes/-26885", "https://spoonacular.com/recipeImages/26885-312x231.jpg"}};
boolean inRecipe = false;

Map<String, String> csvDict = new HashMap<>();
SimpleDateFormat inputDate = new SimpleDateFormat("d MMMM yyyy");
SimpleDateFormat outputDate = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat inputTime = new SimpleDateFormat("H:m");
SimpleDateFormat outputTime = new SimpleDateFormat("h:mm a");
Date newDateObj;
Date newTimeObj1;
Date newTimeObj2;
String newDate = "";
String newTime = "";


String[] monthStr = {"january", "february", "march", "april", "may", "june",
                    "july", "august", "september", "october", "november",
                    "december"};
int[] date = new int[7];
boolean available;
int index = 0;

}

prog locals[API myAPI = new API(),
            Output myOutput = new Output(),
            csvCleaner csvCleanerInit = new csvCleaner(),
            String str = ""]

    : (schedule | plan | recipe)+ EOF
    {
    if(csvDict.containsKey("Subject") && csvDict.containsKey("Start Date")){
        $csvCleanerInit.clean(csvDict);
    }
    recipeDict.put("diet", "vegetarian");
    $myOutput.writeHTML(rescipeArr, recipeDict, csvDict, monthStr);

    }
    ;
//option to set private for google csv output
//format is : schedule{$value = $schedule.value;}
//plan($value = #$plan.value)
//all string returns
schedule: PRIVATE? EVENT '{' event '}';
//time mandatory, any combo of name, details, and place but only one
//might need a separate rule for unknown date

//Event scheduler
//date needs to be appended twice in order to show properly on google calender (needs start and end date)
//time locate and description all in start asterisk to imply any order.  ease of parsing vs. safety/user experience
event : (name | date | time | location | description)*
      ;

//name is "Subject"
name :NAME '{'p=DESC'}'{csvDict.put("Subject", $p.text);};

//i am assuming START DATE is END DATE //mandatory
//ddmmyyyy
//maybe convert date format here vs. the backend cleanup...
//SimpleDateFormat inputDate = new SimpleDateFormat("d MMMM yyyy");
//        SimpleDateFormat outputDate = new SimpleDateFormat("dd/MM/yyyy");
//        Date date;
//        date = inputDate.parse("20 April 2018");
//        String newDate = outputDate.format(date);
date locals[String dateStr="";]
    :
DATE '{'
    k=MONTH
    l=NUM
    m=YEAR'}'
    { try
    {$dateStr = $l.text + " " +$k.text + " " + $m.text;
    newDateObj = inputDate.parse($dateStr);
    newDate += outputDate.format(newDateObj);
    csvDict.put("Start Date", newDate);
    csvDict.put("End Date", newDate);}
    catch(ParseException e){
         System.out.println("Exception Found!");
         }
    }
    ;

//convert FROM to START TIME, TO to "END TIME"
time : FROM '{' g=TIME TO i=TIME '}'
    {
    try{
    newTimeObj1 = inputTime.parse($g.text);
    newTimeObj2 = inputTime.parse($i.text);
    newTime = outputTime.format(newTimeObj1);
    csvDict.put("Start Time", newTime);
    newTime = outputTime.format(newTimeObj2);
    csvDict.put("End Time", newTime);
    }
    catch(ParseException e){
    System.out.println("Exception Found!");
    }
    }
    ;


//whitespace change
//description locals[String food = "";] $food += $f.text at the end
//this is optional so let's see if this works without if statements != null checks, etc.
description locals[String descStr = ""]:
        DESCRIPTION
        '{'d=DESC{$descStr += $d.text;}
        (e=DESC{$descStr += $e.text;}|f=SPACE{$descStr += $f.text;})*
        '}'{csvDict.put("Description", $descStr);}
        ;
location
 :
     LOCATION '{'
     b=DESC{csvDict.put("Location", $b.text);}
     '}'
 ;

//Plan branch, goes to attendee(schedule who's free/diet restrictions) or recipe(create dish)
//both output to a .txt


plan
    : PLAN '{' attendee+ '}'

    ;
//if last entry, don't end in comma
attendee : (ATTENDEE {PeopleContainer.addPerson(new Person());}
        '{' pname (allergy | diet | availability)* '}')
         {
         index++;};


allergy locals[String aller = ""]
    : ALLERGY '{' (a1 = ALLERGIES ',' {$aller += ($a1.text.replace(" ", "+")) + "%2C+";}
    | a2 = ALLERGIES {$aller += ($a2.text.replace(" ", "+"));})+ '}'
    {
    if(! inRecipe){
        PeopleContainer.getPeopleList().get(index).setAllergy($aller);
    }
    if(recipeDict.containsKey("intolerances")){
        recipeDict.put("intolerances", recipeDict.get("intolerances") + "%2C+" + $aller);
    }else{recipeDict.put("intolerances", $aller);}
    }
    ;

diet
    : DIET '{' d = DIETTYPE '}'
    {
    if(! inRecipe){
        PeopleContainer.getPeopleList().get(index).setDiet($d.text);
    }
    dietInt[Arrays.asList(dietStr).indexOf($d.text)] += 1;
    }
    ;

pname locals[String nam = ""]
    : NAME '{' n = DESC {$nam = ($n.text.replaceAll("[^A-Za-z0-9]", ""));}'}'
    {

    PeopleContainer.getPeopleList().get(index).setName($nam);}
    ;

availability locals[String avail = ""]
    : (f = ( AVAILABLE | UNAVAILABLE)
    {
    $avail = ($f.text.replaceAll("[^A-Za-z0-9]", ""));
    if($avail.equals("available")) {
        available = true;
    }
    else if ($avail.equals("unavailable")) {
        available = false;
    }
    }
    '{' pdate '}')
    ;

pdate locals[String mo = "", String da = "", String ye = "", String st = "", String et = "", String t =""]
   : ('{' ( m = MONTH {$mo = ($m.text.replaceAll("[^A-Za-z0-9]", ""));}
   d = NUM {$da = ($d.text.replaceAll("[^A-Za-z0-9]", ""));}
   y = YEAR {$ye = ($y.text.replaceAll("[^A-Za-z0-9]", ""));}
   FROM s = TIME {$st = ($s.text.replaceAll("[^A-Za-z0-9]", ""));} TO
   e = TIME {$et = ($e.text.replaceAll("[^A-Za-z0-9]", ""));})
   {

   date[0] = Arrays.asList(monthStr).indexOf($mo)+1;

   date[1] = Integer.parseInt($da);

   date[2] = Integer.parseInt($ye);

   $t = "" + $st.charAt(0)+$st.charAt(1);
   date[3] = Integer.parseInt($t);


   $t = "" + $st.charAt(2)+$st.charAt(3);
   date[4] = Integer.parseInt($t);


   $t = "" + $et.charAt(0)+$et.charAt(1);
   date[5] = Integer.parseInt($t);


   $t = "" + $et.charAt(2)+$et.charAt(3);
   date[6] = Integer.parseInt($t);


   if(available) {
        PeopleContainer.getPeopleList().get(index).addAvailable(new PDate(date[0], date[1], date[3], date[4], date[5], date[6], false, date[2]));
   }
   else {
        PeopleContainer.getPeopleList().get(index).addUnavailable(new PDate(date[0], date[1], date[3], date[4], date[5], date[6], false, date[2]));
   }
   }'}' ','?)+
   ;

//Plan for Recipe, basic 'what's in your fridge'
//figure out how to "pass" this value up to the top (to "prog" rule or w/e.. do we even have to?)
recipe
    : RECIPE ('(' r = NUM ')' {recipeDict.put("number", $r.text); inRecipe = true;})?
    '{' (ingredients | cuisine | type | allergy | diet)+ '}'
    {inRecipe = false;}
    ;

ingredients locals[String food = ""]
    : INGREDIENTS '{' (f=DESC ',' {$food += ($f.text.replace(" ", "+").replace("\"", "")) + "%2C+";}
    | g=DESC {$food += ($g.text.replace(" ", "+").replace("\"", ""));})+ '}'
    {recipeDict.put("includeIngredients", $food);}
    ;

cuisine
    : CUISINE '{' c = CUISINES '}'
    {recipeDict.put("cuisine", $c.text.replace(" ", "+"));}
    ;
type
    : TYPE '{' t = TYPES '}'
    {recipeDict.put("type", $t.text.replace(" ", "+"));}
    ;


//SCANNER
ALLERGIES : 'Dairy' | 'Eggs' | 'Gluten' | 'Grains' | 'Peanuts' | 'Seafood' | 'Sesame' | 'Shellfish' | 'Soy' |
        'Sulfite' | 'Tree Nuts' | 'Wheat';
DIETTYPE : 'pescetarian' | 'lacto vegetarian' | 'ovo vegetarian' | 'vegan' | 'paleo' | 'primal' | 'vegetarian';
MONTH : 'january' | 'february' | 'march' | 'april' | 'may' | 'june' | 'july' | 'august' | 'september' | 'october'
        | 'november' | 'december' | 'January' | 'February' | 'March' | 'April' | 'May' | 'June' | 'July'
        | 'August' | 'September' | 'October' | 'November' | 'December' | 'Jan' | 'Feb' | 'Mar'
        | 'Apr' | 'May' | 'Aug' | 'Sep' | 'Sept' | 'Oct' | 'Nov' | 'Dec';
CUISINES : 'African' | 'American' | 'British' | 'Cajun' | 'Caribbean'| 'Chinese' | 'French' | 'German'
        | 'Greek' | 'Indian' | 'Italian' | 'Japanese' | 'Jewish' | 'Korean' | 'Latin American'| 'Mexican' | 'Middle Eastern'
        | 'Southern'  | 'Thai' | 'Vietnamese';
CUISINE : 'cuisine';
TYPE : 'type';
TYPES : 'main course'| 'side dish'| 'dessert'| 'appetizer'| 'salad'| 'bread'| 'breakfast'| 'soup'| 'beverage'| 'sauce'| 'drink';
DIET : 'diet';
ALLERGY : 'allergy';
PRIVATE : 'private';
EVENT : 'event';
NAME : 'name';
PLAN : 'plan';
DESCRIPTION : 'description';
LOCATION : 'location';
FROM : 'from';
TO : 'to';
DATE : 'date';
RECIPE : 'recipe';
ATTENDEE: 'attendee';
UNAVAILABLE : 'unavailable';
AVAILABLE : 'available';
INGREDIENTS : 'ingredients';
NUM : ([0-3][0-9] | [1-9]);
//add colon in middle if you wish for "24 hour military time"
TIME : [0-2][0-9]':'[0-5][0-9];
YEAR : [2][0][1-9][0-9];
//good until the year 2099
DESC : '"' ( ~('"') )* '"' ;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
SPACE : [ ]+;