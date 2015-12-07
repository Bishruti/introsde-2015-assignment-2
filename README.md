# Introduction to Service Design And Engineering Assignment 2
Submitted By: Bishruti Siku

This assignment is mainly focused on using RESTful APIs to design and implement a Client and Server architecture. In this project I have stored data in database using SQLite and performed CRUD operations with their respective APIs. This project is deployed in Heroku and can be used to perform the CRUD operations.
[Heroku Link](http://introsdeassignmentehealth.herokuapp.com/ehealth/)

#### Structure

In the root file I have the following files.

**Source Folder (src)**

Possess source code files utilized in this application.

*DAO*

DAO stands for Data Access Objects and files in this folder are responsible to provide datas. It provides Object Relational Mapping (ORM) to map the models into database. In this project we are using 
`LifeCoachDao.java`.

*MODEL*

In this folder we have java classes which define the database as well as various operations that we can perform in our database This project mainly consists of `HealthMeasureHistory.java`, `HealthProfile.java`, `MeasureTypes.java` and `Person.java`.

*RESOURCES*

This folder consists of the java classes which are responsible to implement the API requests sent by the client in the server. It mainly consists of `PersonResource.java`, `PersonCollectionResource.java`, `MeasureTypesResource.java`, `HealthMeasureHistoryResource.java` and `HealthMeasureHistoryDetailResource.java`.

*App.java*

Responsible to establish server.

*ClientApp.java*

It is the client which sends various API requests to the server.

*MyApplicationConfig.java*

Configures the application.

*ExceptionListener.java*

Used for Exception Handling.

*WebContent*

Consists of the files that are required for setup.

1. `persistence.xml`

Required to generate the database from the models.

2. `web.xml`

Required to set up server.

*Procfile*

Required to run command in Heroku.

*app.json*

Consists Heroku setups.

*build.xml*

It is a low-level mechanism to package. It compiles and archives source code into a single `jar` file.

*ivy.xml*

Contains description of the dependencies of a module, its published artifacts and its configurations.

*client-server-json.log*

Logs the `JSON` output acquired from the server by the client by hitting API request.

*client-server-xml.log*

Logs the `XML` output acquired from the server by the client by hitting API request.

*ehealth.sqlite*

Database for the project.

#### Supported API Requests

`Request #1: GET /person`

Lists all the people in the database.

`Request #2: GET /person/{id}`

Gives all the personal information plus current measures of person identified by {id}.

`Request #3: PUT /person/{id}`

Updates the personal information of the person identified by {id}.

`Request #4: POST /person`

Creates a new person and returns the newly created person.

`Request #5: DELETE /person/{id}`

Deletes the person identified by {id}.

`Request #6: GET /person/{id}/{measureType}`

Returns the list of values of {measureType} for person identified by {id}.

`Request #7: GET /person/{id}/{measureType}/{mid}`

Returns the value of {measureType} identified by {mid} for person identified by {id}.

`Request #8: POST /person/{id}/{measureType}`

Saves a new value for the {measureType} of person identified by {id} and archive the old value in the history.

`Request #9: GET /measureTypes`

Returns the list of measures.


#### Program Execution

To Execute Server:

1. Open the terminal.

2. Go to the root directory of the program.

3. Run `ant start`

To Execute Client:

Run `ant execute.client`
