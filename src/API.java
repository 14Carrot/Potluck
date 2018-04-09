import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;


public class API {

    public static String[][] makeArray(Map<String, String> recipeDict, int[] dietInt, String[] dietStr){
        //$myAPI.makeArray(recipeDict, dietInt, dietStr)
        int max = 1;
        ArrayList chooseDiet = new ArrayList();
        for (int i = 0; i < dietInt.length; i++){
            if (dietInt[i] > max) {
                max = dietInt[i];
                chooseDiet.clear();
                chooseDiet.add(i);
            }else if (dietInt[i] == max) {
                chooseDiet.add(i);
            }
        }
        if(! chooseDiet.isEmpty()) {
            recipeDict.put("diet", dietStr[(int) chooseDiet.get(0)].replace(" ", "+")); //takes the most specific diet
        }
        String site = getSite(recipeDict);
        String[][] arr;
        try {
            HttpResponse<JsonNode> APIOut = getRecipe(site, "z15dbiUFf9msheVdXugVA0bKmJIYp1BiJBnjsnDH64cg20I6Qg");
            arr = recipeArr(APIOut);
        } catch (Exception e) {System.out.println(e.toString()); arr = new String[0][0];System.out.println("Unrecognized Ingredient");}
        return arr;
    }


    public static String getSite(Map filter) {

        //"https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?diet=vegan&includeIngredients=onions%2C+lettuce%2C+tomato&limitLicense=false&instructionsRequired=false&number=3&cuisine=american&intolerances=peanut%2C+shellfish&type=main+course&ranking=0&query=burger&addRecipeInformation=false&excludeIngredients=coconut%2C+mango&offset=0";
        //diet, includeIngredients, cuisine, intolerances, type, query, excludeIngredients

        String site = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?";
        String limitLicense = "limitLicense=false&";
        String instructionsRequired = "instructionsRequired=false&";
        String number = "number=3&";
        String ranking = "ranking=1&";
        String addRecipeInformation = "addRecipeInformation=false&";
        String offset = "offset=0";

        if(filter.containsKey("diet")){
            site += "diet=" + filter.get("diet") + "&";
        }if(filter.containsKey("includeIngredients")){
            site += "includeIngredients=" + filter.get("includeIngredients") + "&";
        }if(filter.containsKey("number")){
            number = "number=" + filter.get("number") + "&";
        }

        site += limitLicense+instructionsRequired+number;

        if(filter.containsKey("cuisine")){
            site += "cuisine=" + filter.get("cuisine") + "&";
        }if(filter.containsKey("intolerances")){
            site += "intolerances=" + filter.get("intolerances") + "&";
        }if(filter.containsKey("type")){
            site += "type=" + filter.get("type") + "&";
        }

        site += ranking;

        if(filter.containsKey("query")){
            site += "query=" + filter.get("query") + "&";
        }
        site += addRecipeInformation;

        if(filter.containsKey("excludeIngredients")){
            site += "excludeIngredients=" + filter.get("excludeIngredients") + "&";
        }

        site += offset;

        return site;
    }

    public static HttpResponse<JsonNode> getRecipe(String site, String key) throws Exception{
        HttpResponse<JsonNode> response = Unirest.get(site)
                .header("X-Mashape-Key", key)
                .header("X-Mashape-Host", "spoonacular-recipe-food-nutrition-v1.p.mashape.com")
                .asJson();

        return response;
    }

    public static String[][] recipeArr(HttpResponse<JsonNode> text){
        //String recipes = "[{\"image\":\"https://spoonacular.com/recipeImages/677-312x231.jpg\",\"missedIngredientCount\":2,\"id\":677,\"title\":\"Onion Tartlets\",\"imageType\":\"jpg\",\"usedIngredientCount\":1,\"likes\":0},{\"image\":\"https://spoonacular.com/recipeImages/980-312x231.jpg\",\"missedIngredientCount\":2,\"id\":980,\"title\":\"Domestic Diva's Sea Bass With Roasted Eggplant Puree & Olive An\",\"imageType\":\"jpg\",\"usedIngredientCount\":1,\"likes\":0},{\"image\":\"https://spoonacular.com/recipeImages/4511-312x231.jpg\",\"missedIngredientCount\":2,\"id\":4511,\"title\":\"Grilled Salmon in Tomato Water\",\"imageType\":\"jpg\",\"usedIngredientCount\":2,\"likes\":0}]}";

        String recipes = text.getBody().getObject().get("results").toString();
        String[] recipeList = recipes.substring(2, recipes.length()-3).split("},\\{");

        String[][] arr = new String[recipeList.length][3];
        for(int i = 0; i < recipeList.length; i++){
            arr[i][0] = parceLine(recipeList[i], "title");
            arr[i][1] = "https://spoonacular.com/recipes/-" + parceLine(recipeList[i], "id");
            String pic = parceLine(recipeList[i], "image");
            arr[i][2] = pic.substring(0, 5) + ":" + pic.substring(5);
        }
        return arr;
    }

    public static String parceLine(String s, String t){
        return s.split(t)[1].split(",")[0].replaceAll("\"", "").replaceAll(":","");
    }



}
