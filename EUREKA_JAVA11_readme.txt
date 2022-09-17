									EUREKA Discovery Server (For JAVA JDK 11 Amazon Corretto)

-------------------------------------------------------#1 EUREKA microservice
Create project witk Eureca Server Dependency
Add to .propperties:
	#Establish Eureka Port
	server.port=8761
	#To avoid auto registration of Eureka in Discovery Server...
	eureka.client.registry-with-eureka=false
	eureka.client.fetch-registry=false
...and to main.class -> @EnablesEurekaServer

--------------------------------------------------------#2 Microservices subscribed to EUREKA Microservice
	First..
Go to pom.xml and press Alt+Insert
Write in the search field the dependency in between *dependency*
In this case-> *netflix-eureka-client*
Look for: spring-cloud-starter-netflix-eureka-client -> add
	...then...
Go to .propperties to set keys
	#NAME
spring.application.name=product-microservice (for example)
	#PORT set to 0 to turn it dynamic
server.port=0
	#Instancy ID
eureka.instance.instance-id=${spring.application.name}:${random.uuid}
	...and ¡Finally!
Go to main.class and add @EnableEurekaClient

-------------------------------->>>>RUN DiscoveryService EUREKA & Microservices attached<<<<-----------------------------------

Microservices running displayed at EUREKA server port (Our case: http://localhost:8761/)

------------------------------									WARNING! Take notice that...

Sometimes .propperties conecction setting does not work throwing an replicator error,
in these cases we have to set the connection via YML file.
The script of this file is:

-------For the client(example):

spring:
  application:
    name: booking-microservice
server:
  port: 0
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
  instance:
    preferIpAddress: true

------For the server(example):

server:
  port: 8761
eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false



Ojo! Look at this!!
MULTIPLE INSTANCIES of a running app
>Run/Debug Configuration > Edit configurations > Modify options > Allow multiple instancies > Apply > Ok
