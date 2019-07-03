FROM java:8-alpine
ADD demo2.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

