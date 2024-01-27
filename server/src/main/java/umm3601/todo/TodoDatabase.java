package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

// import io.javalin.http.BadRequestResponse;

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
        if (queryParams.containsKey("contains")){
            String targetContains = queryParams.get("contains").get(0);
            filteredTodos = todosContain(filteredTodos, targetContains);

        }
        if (queryParams.containsKey("status")){
          String targetStatus = queryParams.get("status").get(0).toLowerCase();
          boolean boolStatus;
          if(targetStatus.equals("complete")){
            boolStatus = true;
          }
          else if(targetStatus.equals("incomplete")){
            boolStatus = false;
          }
          else{boolStatus = false;}

          filteredTodos = filterTodosByStatus(filteredTodos, boolStatus);
        }

        if (queryParams.containsKey("limit")) {
          String paramLimit = queryParams.get("limit").get(0);
          try {
              int targetLimit = Integer.parseInt(paramLimit);
              filteredTodos = limitTodos(filteredTodos, targetLimit);
            } catch (NumberFormatException e) {
              throw new BadRequestResponse("Specified age '" + paramLimit + "' can't be parsed to an integer");
            }
        }

        if (queryParams.containsKey("orderBy")){
          String targetToOrderBy = queryParams.get("orderBy").get(0);
          filteredTodos = sortTodos(filteredTodos, targetToOrderBy);
        }

        return filteredTodos;
    }
    public Todo[] todosContain(Todo[] todos, String targetContains) {
      return Arrays.stream(todos).filter(x -> x.body.contains(targetContains)).toArray(Todo[]::new);
  }
    public Todo[] filterTodosByStatus(Todo[] todos, boolean boolStatus) {
      return Arrays.stream(todos).filter(x -> x.status == boolStatus).toArray(Todo[]::new);
  }

    public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
        return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByCategory(Todo[] todos, String targetCat) {
        return Arrays.stream(todos).filter(x -> x.category.equals(targetCat)).toArray(Todo[]::new);
    }
    public Todo[] limitTodos(Todo[] todos, int targetLimit) {
        Todo[] copyArr = new Todo[targetLimit];
        for(int i = 0; i < targetLimit; i++) {
             copyArr[i] = todos[i];
       }
       return copyArr;
    }

    public Todo[] sortTodos(Todo[] todos, String targetToOrderBy) {
       String sortBy = targetToOrderBy.toLowerCase();
       todos = Arrays.stream(todos).sorted((todo1, todo2) ->{
        switch (sortBy) {
          case "owner":
            return todo1.owner.compareTo(todo2.owner);
          case "category":
            return todo1.category.compareTo(todo2.category);
          case "body":
          return todo1.body.compareTo(todo2.body);
          default:
            return 0;
        }
      })
      .toArray(Todo[]::new);
      return todos;

    }


}
