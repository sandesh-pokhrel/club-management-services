## Club Management Microservices Application
### This is the application which manages various aspects of club management. The main overall objectives are summarized below:
##

####* Discovery server
####* Client config server
####* Authorization server
####* Client service
####* User service
####* Purchase service
####* Gateway service
##

## Detailed info about the services
###* Discovery service : *9000 (port)*
#### &ensp;&ensp;&ensp; Purpose of this service is to register all other services in our microservices' environment. Because of this service, others can change their url or port as long as they register to this service.
###* Configuration service :
#### &ensp;&ensp;&ensp; This service holds the configuration related properties for all the microservices. It removes the difficulty of managing the property for each of the services individually by having them in one location.
###* Authorization service :
#### &ensp;&ensp;&ensp; This service is for handling the authorization via OAUTH.
###* Client service :
#### &ensp;&ensp;&ensp; Contains the endpoint for handling different client related tasks.
###* Purchase service :
#### &ensp;&ensp;&ensp; This service handles the purchase and schedule related tasks.
###* Gateway service :
#### &ensp;&ensp;&ensp; This acts as the service for handling the initial requests, then forwards them to the respective resource owners.


