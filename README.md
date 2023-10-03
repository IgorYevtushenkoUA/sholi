# SHOLI: Do your link shorter :)

## Instalation
You need JDK and maven

### Maven command
    
    mvn clean install 

### Docker commands
First command to build docker image from Dockerfile

    docker build -t sholi .

Next command starts the container and downloads\pulls images from Docker Hub

    docker compose up


## Part 2
To improve the performance of our application on a large number of applications, we can take the following steps
1) Increase the cache storage time in Redis
   In the current program, we store the timeUnit cache in minutes, and change it to hours

2) We can add an additional lastAccessedTime field, which will record the last search of our url and delete urls that have not been used for more than 3 days

3) MongoDB sharding can be done using shortenedUrl -> this has its advantages in terms of correction sizes on different shards

4) We can make a separate table with ip addressing to know which regions we have the most requests for and place the instance there