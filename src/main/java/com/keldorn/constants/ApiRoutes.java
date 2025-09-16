package com.keldorn.constants;

public final class ApiRoutes {
    public ApiRoutes() {}

    public static final String BASE_USERS = "/users";
    public static final String BASE_TODOS = "/todos";
    
    public static final String USER_BY_ID = BASE_USERS + "/{id}";
    public static final String USER_TODOS = BASE_USERS + "/{id}/todos";
    public static final String TODO_BY_ID = BASE_TODOS + "/{id}";
    public static final String TODO_USER = BASE_TODOS + "/{id}/user";
}
