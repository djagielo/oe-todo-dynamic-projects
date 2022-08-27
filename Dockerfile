FROM amazoncorretto:17

WORKDIR /app

COPY /build/libs/oe-todo-dynamic-projects-*.jar /app/overengineered-todo-dynamic-projects.jar

EXPOSE 9991

ENTRYPOINT ["java", "-jar", "overengineered-todo-dynamic-projects.jar"]