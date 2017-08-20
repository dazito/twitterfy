# twitterfy
Connect a Tweet stream to different queues and databases.

Subscribe to and get notified when someone tweets your keywords. **Typical use cases are: receive sales, discount and special deals alerts, data mining, sentimental analyses and others.**

This is a work in progress, pull requests are welcome.

 - Get notified via:
   - Web sockets
   - Amazon SNS
   - Amazon SQS
   - Google Cloud Pubsub
   - **More services/technologies to come**
 - Persist tweets on
   - MySQL database
   - DynamoDB database
   - **More databases to come**

Using:
 - Java 8
 - JOOQ with MySQL
 - Twitter4J - Twitter API client
 - Akka Framework
 - Vertx

This is a work in progress.

# How to run
1- Edit the following files with your configuration details:
  - configuration.properties
  - pom.xml

Run Main.class

To create a fat/executable jar, run the following command in the project folder
 - mvn clean compile assembly:single

Next features:
  - ~~Run on a docker container~~
  - ~~Send email notifications~~ :: Implemented
  - ~~Publish notification to Amazon Web Services SNS - Simple Notification Service~~ :: Implemented
