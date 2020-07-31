import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;

public class FetchAllLeetCodeProblems {
    public static void main(String[] args){
        String outputPath = System.getProperty("user.dir") + "/output/lcProblems.tsv";

        String url = "https://leetcode.com/api/problems/algorithms/";
        //URL url = new URL(problemsUrl);
        JsonArray problems = lcProblemsJson(url).getAsJsonArray("stat_status_pairs");

        try{
            FileWriter output = new FileWriter(outputPath);
            for(JsonElement e : problems) {
                JsonObject jo = e.getAsJsonObject();
                JsonObject stat = jo.getAsJsonObject("stat");
                output.write(stat.get("question__title_slug").getAsString());
                output.write("\t");
                output.write(jo.get("paid_only").getAsString());
                output.write('\n');
                //int a = 1;
            }
            output.close();
        } catch(Exception e){
            e.printStackTrace();
        }



    }

    public static JsonObject lcProblemsJson(String url){
        String contents = "";
        try(java.io.InputStream input = new java.net.URL(url).openStream()){
            contents = new String(input.readAllBytes());
        } catch(Exception e){
            e.printStackTrace();
        }

        JsonElement json = new JsonParser().parse(contents);
        JsonObject jObject = json.getAsJsonObject();
        return jObject;
    }
}
