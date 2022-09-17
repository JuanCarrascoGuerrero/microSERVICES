||-----------------||...........CONFIGURACION CENTRALIZADA -> *Spring Cloud Config*............||------------------------||
     APARTADO 1                                                                                  SETTING THE CLOUD SERVER

Resuelve multiples cambios en los .properties, .yml de los microservicios sin la necesidad de detener y recompilar la aplicacion.

¡Configuración externa!
Almacenamos una version del properties en un repositorio de Git con las configuraciones que puedan cambiar
ConfigServer -> lector del repositorio Git

#     PASOS     #-----------------------------------------------------------------------------------------------

#1		

Crear directorio "server-config" > Abrir en IDE
Crear un .properties para cada microservicio con copias para entornos "development" y "production". Ejemplo:
	booking-microservice ->
		booking-microservice-dev.properties
		booking-microservice-prod.properties
	product-microservice ->
		product-microservice-dev.properties
		product-microservice-prod.properties
	...etc para cada microservice

#2		------------------------------------------------------------------------------------------------

Creamos repositorio git del proyecto service-configuration
Añadimos las referencias a cada .properties
Añadimos las main variables de nuestros microservicios al perfil "development" del mismo
Git -> Push

#3		------------------------------------------------------------------------------------------------

Spring Initializr -> Proyecto con dependencias "Spring Boot Actuator" y "Config Server"
@EnableConfigServer en la clase main
.properties ->
	#1 Name and default port
	spring.application.name=config-server
	server.port=8888
	#2 Git repository URL to fetch
	spring.cloud.config.server.git.uri=https://github.com/JuanCarrascoGuerrero/service-configuration
	#3 Clone at initialization
	spring.cloud.config.server.git.clone-on-start=true

#4 Run		------------------------------------------------------------------------------------------------
Comprobamos que se ejecuta correctamente llamando en el puerto del config-server al development de algún microservice

				     > Postman > Llamada al puerto del config server/microservice/dev
	       ...En nuestro ejemplo > Postman GET > http://localhost:8888/booking-microservice/dev

Debiendo obtener de respuesta las keys del .properties incluidas en ese microservice .properties development
	       ...En nuestro ejemplo > 
{
    "name": "booking-microservice",
    "profiles": [
        "dev"
    ],
    "label": null,
    "version": "9a7dc5d9de06ef4df0810b73ae497d37a40c6894",
    "state": null,
    "propertySources": [
        {
            "name": "https://github.com/JuanCarrascoGuerrero/service-configuration/file:C:\\Users\\34629\\AppData\\Local\\Temp\\config-repo-16330424646264790888\\booking-microservice-dev.properties",
            "source": {
                "booking.property": "Booking Profile Dev",
                "spring.jpa.hibernate.ddl-auto": "update ",
                "spring.jpa.show-sql": "true",
                "spring.jpa.generate-ddl": "true"
            }
        }
    ]
}

EN RESUMEN HASTA AQUÍ -> Tenemos un repositorio git de properties de nuestros microservicios para setear las keys
		      -> Config-Server microservice read&fetch dicho repositorio de git

						----   	+    ----
----------------------------------CONECTAR MICROSERVICIOS con CONFIG-SERVER--------------------------------------------------------


1.-Para esto los microservicios dependientes deben incluir dependencia *spring-cloud-config-client* de springframeworkcloud
2.-Dependencia para que realize el fetch de los archivos de configuración cuando el microservicio se inicie *spring-cloud-starter-bootstrap*
		>Dicha dependency exige crear un nuevo .properties > bootstrap.properties
		>Aqui definimos url y port donde esta corriendo nuestro config-server
			...para nuestro ejemplo: 
				spring.cloud.config.uri=http://localhost:8888
		>Además debemos añadir en el .properties principal una llave para señalizar que perfil de la configuración centralizada tomamos
			...para nuestro ejemplo ("development"):
				spring.cloud.config.profile=dev

IMPORTANTE!->  El resto de configuraciones en el .properties de nuestro microservicio conviene transferirlo al bootstrap.properties ya que este
tiene prioridad de lectura para config-server
			

**Para comprobar que ejecuta normalmente:
	1 Ejecutamos los microservicios + Eureka Port Server + Cloud Config Server > No hay conflictos ni errores > siguiente paso...
	2 Leemos en los logs el puerto asignado al microservicio a evaluar
	3 Hacemos un request del microservice/api en el puerto donde se encuentre a través de Postman y evaluamos respuesta


||-----------------||...........CONFIGURACION CENTRALIZADA -> *Spring Cloud Config*............||------------------------||
     APARTADO 2											  REFRESH CONFIGURATIONS

La idea central es que, conectados ya los microservicios con el config server (lector del repositorio de configuraciones on cloud "git")
consultando por sus respectivos perfiles, estos puedan actualizar cambios introducidos en el Cloud Properties Server MIENTRAS SE ESTAN
EJECUTANDO. Es decir, sin necesidad de parar y volver a levantar los microservicios.

Uso del metadato o comentario @RefreshScope a nivel del Configurationpara que al realizar llamadas API se "refresquen"/actualizen las configuraciones
(Ojo! solo funciona a nivel de @Configuration!!)

Asimismo vamos a hacer uso de la dependencia "Spring Boot Actuator" que nos permite exponer informacion operativa de la aplicacion en ejecucion
(Estadísticas, métricas, información, volcado) a traves de endpoints http o beans html


#     PASOS     #-----------------------------------------------------------------------------------------------

#1

Framework............................................................................
-> @Configuration con @Value(${app.value}) 
	-> inyectado del "..-dev.properties" del Git Cloud Server 
		-> @Autowired con un @RestController con llamada /api al value
.....................................................................................

Insertamos la dependencia *spring.boot.starter.actuator* en nuestros microservicios

	Nuestro EJEMPLO...
	1 - Hemos creado un package .congif con la clase "DefaultConfiguration.java" (@Configuration) donde incluir aquellos valores inyectados desde
	.properties centralizados en el repositorio de Git y accesibles con el microservicio read&fetch config-server (port:8888)
	2 - Hemos creado un RestController "CategoryController.java" que instancia (@Autowired) nuestro "DefaultConfiguration.java"
	3 - Dicho RestController devuelve a través de una /api - @GetMapping el valor inyectado.

¿Funciona? -> Postman -> GET Request del /api que devuelve el valor inyectado

#2		------------------------------------------------------------------------------------------------

Ahora tratemos de conseguir que UPDATE ON RUNNING, es decir, sin necesidad de parar el microservicio...

1 - Añadimos la llave para acceder a los endpoints de actuator (REFRESH, metrics, loadings,...etc)
	a) En BOOTSTRAP.PROPERTIES:
management.endpoints.web.exposure.include=*      >   Lo seteamos a "*" pues queremos todos sus endpoints
	b) En APPLICATION.YML:
management:
  endpoints:
    web:
      exposure:
        include: refresh

2 - Añadimos @RefreshScope a nuestro DefaultConfiguration (@Configuration) que refrescara el valor sin necesidad de reiniciarse

3 - Para que la ACTUALIZACIÓN DEL VALOR se actualize efectivamente debemos llamar al POST endpoint "/refresh" de actuator en el mismo puerto 
donde se ejecuta el microservicio. Para esto vamos a Postman y  ejecutamos un POST Request de la url donde esta el microservicio corriendo 
"../actuator/refresh". 


Ejemplo ->       http://localhost:[microservice port]/actuator/refresh
El resultado debería ser:
Ejemplo ->       ["config.client.version","app.testProp"]



... Y la llave del .properties cambiada debe de haberse actualizado en el microservicio sin necesidad de pararlo.
Podemos comprobarlo con Postman.

#3		------------------------------------------------------------------------------------------------
Un MessageBroker hara las llamadas a "/actuator/refresh" más adelante para automatizar este proceso (ya lo veremos)







