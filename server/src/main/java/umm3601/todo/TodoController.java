package umm3601.todo;

import java.io.IOException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Controller;

public class TodoController implements Controller {
    private TodoDatabase todoDatabase;

    public TodoController(TodoDatabase todoDatabase) {
        this.todoDatabase = todoDatabase;
    }

    public static TodoController buildTodoController(String todoDataFile) throws IOException {
        TodoController todoController = null;

        TodoDatabase todoDatabase = new TodoDatabase(todoDataFile);
        todoController = new TodoController(todoDatabase);

        return todoController;
    }



    public void getTodo(Context ctx) {
        String id = ctx.pathParam("id");
        Todo todo = todoDatabase.getTodo(id);
        if (todo != null) {
            ctx.json(todo);
            ctx.status(HttpStatus.OK);
        } else {
            throw new NotFoundResponse("No todo with id " + id + " was found.");
        }
    }

    public void getTodos(Context ctx) {
        Todo[] todos = todoDatabase.listTodos(ctx.queryParamMap());
        ctx.json(todos);
    }

    @Override
    public void addRoutes(Javalin server) {
        server.get("/api/todos/{id}", this::getTodo);
        server.get("/api/todos", this::getTodos);
    }

}
