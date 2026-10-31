# Covid Dashboard App

This project is a small Java-based web application that reads COVID-19 statistics from a local JSON dataset, cleans the values, serves them through a lightweight API, and displays them in a browser as a bar chart. The purpose of the project is simple: demonstrate how a backend, data processing logic, and front-end visualization can work together without the overhead of a larger framework.

## What the app does

The application loads a dataset of daily COVID-19 figures, normalizes the raw values so they can be used consistently in Java and JavaScript, and exposes that data through an HTTP endpoint. The browser then fetches the data and renders a chart showing the trend in total confirmed cases over time.

The app is intentionally minimal and focused on one core idea: moving data from a source file to a usable front-end dashboard.

## How the code works

### 1. Backend server

The main logic is in `src/main/java/com/example/JavalinJsonApp.java`.

When the application starts:

- it loads the data from `src/main/resources/covid.json`
- it sanitizes the values to make them usable in Java and JavaScript
- it creates a Javalin web server
- it serves static frontend files from the classpath
- it exposes the cleaned dataset at `/api/covid-data`

The backend uses Javalin to handle the HTTP layer. Javalin is a lightweight Java framework that allows the app to run a web server with very little setup. The server also enables CORS so the front-end can request data from the same application without browser restrictions.

### 2. Data cleaning logic

The dataset contains values that are formatted with spaces, such as `"1 139"`, `"27 403"`, and `"3 471"`. Those values are not suitable for direct numeric charting, so the Java code removes the whitespace and converts the resulting string into an integer.

The cleaning process works like this:

- if a field is a date, it keeps the value as a string
- for all other fields, it strips spaces from the value
- it attempts to parse the cleaned value as an integer
- it stores the cleaned number in the response JSON

This step is important because the chart needs numeric values to calculate bar heights correctly.

### 3. API endpoint

The app exposes a single endpoint:

- `GET /api/covid-data`

This endpoint returns the cleaned dataset as JSON. The front-end then reads that response and builds the chart from the values it receives.

### 4. Front-end dashboard

The browser-side code lives in:

- `src/main/resources/public/index.html`
- `src/main/resources/public/app.js`
- `src/main/resources/public/styles.css`

When the page loads:

- the JavaScript runs after the DOM is ready
- it sends a `fetch()` request to the API
- it receives the JSON data
- it calculates the largest value in the dataset
- it builds a series of bars whose heights are proportional to the total confirmed case count

The chart is built using regular HTML elements and CSS rather than a charting library. This keeps the app lightweight and easier to understand.

## Project structure

- `pom.xml` — Maven configuration and dependency setup
- `src/main/java/com/example/JavalinJsonApp.java` — backend server and data processing logic
- `src/main/resources/covid.json` — source data for the dashboard
- `src/main/resources/public/index.html` — page structure
- `src/main/resources/public/app.js` — browser-side chart rendering logic
- `src/main/resources/public/styles.css` — chart and page styling

## External libraries and requirements

This project depends on the following tools and libraries:

### Required software

- Java 21
- Maven

### Java libraries

- Javalin 6.3.0 — lightweight web server and routing
- Jackson Databind 2.17.2 — JSON parsing and serialization
- SLF4J Simple 2.0.13 — logging support for the server

No extra front-end library is required for the chart itself. The UI is built with native HTML, CSS, and JavaScript.

## How to run the project

From the project directory:

```bash
mvn clean package
java -jar target/covid-dashboard-app.jar
```

Then open this in a browser:

```text
http://localhost:8080/
```

## Why this project is useful

This project is a strong example of a complete, small-scale full-stack application:

- data is loaded from a file
- values are cleaned and normalized
- a backend serves the processed data
- the browser consumes that API
- a chart is rendered from the results

It is not a production-grade public health dashboard, but it does show how to build a simple data-driven web application in a clear, manageable way.

## Summary

The app is a practical example of taking raw data, preparing it for use, exposing it through an API, and building a straightforward dashboard in the browser. It demonstrates core Java web development concepts and front-end integration in a way that is easy to follow while still showing meaningful functionality.
