# spring-cloud-play

## Build and Run
Build all the docker containers
`./runall.sh`

Run the docker-compose

`docker-compose up`

or 

`docker-compose up -d` for detached mode


Try `localhost:8080/transaction`


## Key points

- containerized using docker 
- managed by docker-compose (as this is proof of concept, in reality use k8s or other orchastrator)
- spring cloud 
- config server 
- discovery server (backed by Netflix Eureka)
- single router service which is like a api gateway 
- single micro-service id-service with 2 replicas
- spring load balancer to use id-service replica's in round robin fashion
- circuit breaker with Resilience4J to manage unexpected behaviors

