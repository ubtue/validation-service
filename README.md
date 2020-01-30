# validation-service
RESTful web service for validation of PDF and PDF/A documents. The service uses veraPDF and FITS for format identification and validation.


## Requirements
- Jakarta EE Application Server (e.g. Payara Micro)
- Java 8 RE
- Tools veraPDF and FITS (details shown below)

Windows is not supported yet...

## Setup

### Configuration

The service needs environment parameters like database connection information and the location of the home directory containing 
the tools. For details check [WEB-INF/web.xml](https://github.com/FabianHamm/validation-service/blob/master/service/src/main/webapp/WEB-INF/web.xml).

#### Payara Micro Properties

In case you choose to run on payara micro, add the following config parameters to a properties file:
```
#### --- Folder locations --- ####

# Location of the service directory
directory.home=/home/someuser/SERVICE_HOME

# Path to the temp folder. Used for storing uploaded files and temporary files created during validation process
directory.temp=/home/someuser/service_tmp

#### --- Database Access --- ####

db.server.name=localhost
db.port=1527
db.name=DspaceValidatorDB
db.user=user596
db.pwd=Kq8j9aU

#### --- Json Web Token Setup --- ####

# JWT expiration after minutes
jwt.expiration.minutes = 35

# String key to be used for JWT 
jwt.signature.key=_S1mpl3keyO8!5-+

```
#### Home Directory

The home directory contains the tools the service is dependent on and has two subfolders:

```
HOME_DIRECTORY
  
  -- verapdf
       verapdf.jar
       
  -- fits
       fits.sh
      
```
The folder fits contains the default FITS application with fits.sh at the root level.

The folder verapdf contains the executable verapdf.jar. This is a simple command line wrapper for the batch api and the source code for building it can be found [here](https://github.com/FabianHamm/verapdf-wrp-exec). VeraPDF was integrated into the service like this because it was originally not threadsafe. This however [has changed](https://github.com/veraPDF/veraPDF-library/issues/1037) in the meantime...

### Using the service

You can access the service via the optional frontend application, or directly via the REST api.


#### Predefined Users
By default two users with default passwords will be created:

  **user : user**
  
  **admin : admin**





