package gautamhans.xyz.bakeaid.retrofit;

import java.util.ArrayList;

import gautamhans.xyz.bakeaid.pojos.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Gautam on 06-Aug-17.
 */

public interface RecipesAPI {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
