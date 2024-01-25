package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

public class TodoDatabase {
    
    private Todo[] allTodos;

    public TodoDatabase(String todoDataFile) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(todoDataFile);
        if(resourceAsStream == null){
            throw new IOException("Could not find " + todoDataFile);
        }
        InputStreamReader reader = new InputStreamReader(resourceAsStream);
        ObjectMapper objectMapper = new ObjectMapper();
        allTodos = objectMapper.readValue(reader, Todo[].class);
    }
    public int size() {
        return allTodos.length;
}
    

    public Todo getTodo(String id) {
        return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
    }
    public Todo[] listTodos(Map<String, List<String>> queryParams){
        Todo[] filteredTodos = allTodos;

        if (queryParams.containsKey("owner")){
            String targetOwner = queryParams.get("owner").get(0);  //takes the first one and creates a list of all users
            //equal to what you are searching for so if age=25 is what you are searching for it creates that list
            filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
        }
        if (queryParams.containsKey("category")){
            String targetCat = queryParams.get("category").get(0);
            filteredTodos = filterTodosByCategory(filteredTodos, targetCat);
        }
        return filteredTodos;
    }
    


    public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
        return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
    }

    
}