# FitFeed
FitFeed is social-focused workout Android application created for CSE 476 - Mobile App Development.
FitFeed allows users to save custom workouts, which can be posted with an optional photo. Users can add friends, and friends posts are viewable in the feed. 

## Development
### Data Sources and Sinks 

For the backend server, MySQL is used to backup users and user data. Keycloak is used as the Identity and Access Management service, which handles user registration and authentication. A Spring based REST API service runs alongside the latter services (running as Docker containers) and accepts HTTP requests to ingress/egress data and authenticate users. Keycloak and the REST API use separate databases in MySQL. 

For the API’s database structure, there are tables for Workouts and Exercises. Users are managed by Keycloak, so only their IDs are stored when associated with other records. Workouts have a one-to-many relationship with Exercises. 

The FitFeed app makes requests to the API via HTTP to login and save/load workouts. Threading is done using ExecutorService, which performs API tasks asynchronously in a single, separate thread. 

### Local Data Storage 

FitFeed saves user’s workouts to a single JSON file. When a workout is saved, the file manager appends the newest workout to the end of the file. The JSON structure contains the workout name, timestamp, and each exercise performed along with the weight, sets, and reps. Saving to a JSON file was the chosen approach since the remote loading of friends’ workouts is in the same JSON format, allowing the feed to be reusable for personal and feed workouts. 

EncryptedSharedPreferences was used to store the access tokens for the FitFeed API, as well as the username and password if the user opts to stay logged in. This was chosen for its simplicity as well as its security compared to regular SharedPreferences. 

### Remote Data Storage 

The remote server runs on one cloud VM for ease of access and to ensure it is always available. The VM is hosting the Spring API as a jar file which is managed by the VM and restarted if shutdown for some reason such as internal server error. The database and IAM services are both running alongside the API server as Docker containers exposed to the local host so they can interact with each other. MySQL was used as the DB of choice since it is easy to setup and integrates with both the Spring app and the Keycloak IAM service. Keycloak was chosen for user authentication because it is open source and secure, enabling us to focus more on our app development and less on user security and authentication. In the future, it will also enable us to customize our user authentication with more powerful features like email verification and OAuth2. For future versions of this app, we will investigate the possibility of using services such as Kubernetes to allow us to automate deployment of our backend services and reduce downtime for updates. 

### Client/Server Communication 

The Android app uses HTTP requests to access data from the server. This decision was made because a REST API is extremely easy to set up and manage and allows for future web applications and easier testing. 

## Contributors
Owen Haiar, Alex Holt, Mary Holt, Jake Youngerman, John Landers, Eli Gudeman
