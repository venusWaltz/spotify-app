<h1 align=center>Bebopt</h1>

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\Spotify_Logo_CMYK_Green.png" width="170" />
</p>

<div align="center">
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot">
    <img src="https://img.shields.io/badge/Vaadin-00B4F0?style=for-the-badge&logo=Vaadin&logoColor=white">
    <img src="https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black">
    <img src="https://img.shields.io/badge/json-5E5C5C?style=for-the-badge&logo=json&logoColor=white">
    <img src="https://img.shields.io/badge/Spotify-1ED760?style=for-the-badge&logo=spotify&logoColor=white">
    <img src="https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white">
    <img src="https://img.shields.io/badge/-selenium-%43B02A?style=for-the-badge&logo=selenium&logoColor=white">
</div>

## Overview
Bebopt is a web app that allows users to view their Spotify statistics and manage playlists.

## Features
+ Authenticate with your personal Spotify account.
+ View your top tracks and artists.
+ Receive personalized music recommendations.
+ Sort, filter, or merge your playlists.

## Getting Started
### Prerequisites
+ Java Runtime Environment (JRE)
+ [Maven](https://maven.apache.org/)
+ Web browser
+ [Spotify](http://www.spotify.com/) account

### Configure your Spotify application
1. Log in to your [Spotify for Developers](https://developer.spotify.com/) account using the sign-in for your regular Spotify account.

2. Follow [this guide](https://developer.spotify.com/documentation/web-api/concepts/apps) to create a new application.

3. When setting up your application, set the redirect URI to http://127.0.0.1:8080/callback. Make note of your Client ID and Client Secret.

> The Client ID is the unique identifier of the app, and the Client Secret is the key that will be used to authorize Web API calls.

### Installation
1. Clone the repository.
```
git clone https://github.com/venusWaltz/spotify-app.git
```
2. Complete the setup in your project directory.
```
chmod +x setup-env.sh && ./setup-env.sh
```

### Running the application

Run using standard debugging tools in VS Code, or run the `Application.java` file.

**You can also import the project to other IDEs.** Follow [this guide](https://vaadin.com/docs/latest/guide/step-by-step) for information about importing and running Vaadin Spring Boot projects in an IDE. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code) and [how to run Vaadin projects from different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/running).

## Usage
Open a web browser and navigate to http://127.0.0.1:8080.

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\screenshots\1-home-page.png" width="700"/>
</p>

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\screenshots\3-stats-page_tracks.png" width="700"/>
</p>

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\screenshots\4-stats-page_artists.png" width="700"/>
</p>

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\screenshots\5-playlists-page.png" width="700"/>
</p>

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\screenshots\6-recs-page-tracks.png" width="700"/>
</p>

<p align="center">
    <img src="src\main\resources\META-INF\resources\images\readme-imgs\screenshots\7-recs-page-artists.png" width="700"/>
</p>

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/bebopt-1.0-SNAPSHOT.jar`

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- For more information about Spotify's Web API, visit [developer.spotify.com/documentation/web-api](https://developer.spotify.com/documentation/web-api).

## License
[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

## Credits
+ [Fariah Saleh](https://github.com/venusWaltz)
+ [Kevin DeAtley](https://github.com/kdeatley)
+ [John Vieyra](https://github.com/vieyrajohn13)

The GitHub repository: https://github.com/venusWaltz/spotify-app.
