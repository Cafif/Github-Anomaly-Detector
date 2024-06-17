# Github-Anomaly-Detector

This app scans your organization's Github and detects suspicious activities!

##Suspicious Behaviors Detected By This App
- Code pushed in specific hours (14:00 to 16:00 by default)
- Teams created with suspicious prefixes ("hacker" prefix by default)
- Repositories created and immediately deleted (within 10 minutes by default).

## How To Build, Configure & Run

### Build Application
If you do not have a Java JDK or Maven installed on your machine, please follow this quick guide:
  https://www.baeldung.com/install-maven-on-windows-linux-mac
 
Go to the project's base folder, open a terminal and run: `mvn clean install`
After Maven finishes building the application you should see that a `target` folder has been created, and inside of it there should be a jar file called `github_detector-0.0.1-SNAPSHOT.jar`

### Configure

#### ***If you want to run the app with default configurations you can ignore this part***

Navigate to `target/classes`, there should be an `application.yml` file there, this file contains configurations for the application, here's the default configurations and explanations for what they do:

```
logging:
  file:
    name: logs/github-anomalies.log <- path where log file is saved

github:
  hours-diff: 3 <- timezone difference in hours between Github server and local machine running the app

scanners:
  delete-repo:
    min-minutes: 10 <- number of minutes that need to pass after a repository has been created before it can be deleted without triggering a suspicious activity
  push: <- push operations to the organization's github within these hours will be considered suspicious
    forbidden-start-time: "14:00"
    forbidden-end-time: "16:00"
  create-team:
    invalid-prefixes: "hacker" <- team name prefixes that are considered suspicious, separated by a comma (f.e "hacker,alice,bob")

notify:
  log: true <- log suspicious activities
  console: true <- report suspicious activities in the app's console
```

to change these configurations you can edit this file, or alternatively you can edit the source code's file in this path: `src/main/resources/application.yml` and run `mvn clean install` again


### Run
In the root directory run `java -jar target/github_detector-0.0.1-SNAPSHOT.jar`, after a second or two the app should start.


## Setup Github Organization's Webhooks To Your Local App

- Setup a tool that will tunnel requests to the local port that your app is running on (8080 by default) , I used ngrok for testing.
- Once you receive a static URL go to your Organization's Github `-> Settings -> Webhooks` and paste the static URL followed by `/github-event` in the payload URL (f.e if ngrok gave you this url: `app.ngrok-free.app` then you need the payload URL to be `app.ngrok-free.app/github-event`), change the content type to `application/json` and disable SSL.

This is pretty much it, once you are doing setting up the webhook the app should be detecting suspicious activities in your organization's Github.



