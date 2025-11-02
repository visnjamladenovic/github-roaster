# GitHub Roaster

A Spring Boot application that provides a REST API for fetching and aggregating GitHub user data, including profiles, repositories, commits, and events.

## Description

GitHub Roaster is a Kotlin-based Spring Boot application that serves as a wrapper around the GitHub API. It allows you to fetch various data about GitHub users and repositories, and provides a consolidated "roast data" endpoint that aggregates multiple types of information about a user.

The application can be used to:
- Retrieve GitHub user profiles
- List repositories for a specific user
- Get repository details
- Fetch commit history for repositories
- View recent user events
- Collect comprehensive data about a user for "roasting" purposes

## Requirements

- Java 17 or higher
- Gradle 7.6 or higher
- Kotlin 1.9.25

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/github-roaster.git
   cd github-roaster
   ```

2. Build the application:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start on the default port 8080.

## API Endpoints

### User Endpoints

#### Get User Profile
```
GET /api/github/user/{username}
```
Returns detailed information about a GitHub user.

#### Get User Repositories
```
GET /api/github/user/{username}/repos
```
Returns a list of repositories owned by the specified user.

#### Get User Events
```
GET /api/github/user/{username}/events?limit={limit}
```
Returns recent events for the specified user. The `limit` parameter is optional (default: 30).

### Repository Endpoints

#### Get Repository Details
```
GET /api/github/repo/{owner}/{repoName}
```
Returns detailed information about a specific repository.

#### Get Repository Commits
```
GET /api/github/repo/{owner}/{repoName}/commits
```
Returns a list of commits for the specified repository.

### Roast Data Endpoint

#### Get Comprehensive User Data
```
GET /api/github/roast-data/{username}
```
Returns comprehensive data about a user, including their profile, repositories, recent commits, and events. This aggregated data can be used for "roasting" purposes.

## Example Usage

### Fetch a user profile
```bash
curl http://localhost:8080/api/github/user/octocat
```

### Get repositories for a user
```bash
curl http://localhost:8080/api/github/user/octocat/repos
```

### Get repository details
```bash
curl http://localhost:8080/api/github/repo/octocat/Hello-World
```

### Get commits for a repository
```bash
curl http://localhost:8080/api/github/repo/octocat/Hello-World/commits
```

### Get user events
```bash
curl http://localhost:8080/api/github/user/octocat/events?limit=10
```

### Get comprehensive roast data
```bash
curl http://localhost:8080/api/github/roast-data/octocat
```

## Project Structure

- `controller`: Contains REST controllers that define API endpoints
- `service`: Contains business logic for interacting with the GitHub API
- `dto`: Contains data transfer objects that represent GitHub entities

## Limitations

- The application currently does not support authentication with the GitHub API, so it is subject to rate limiting
- Error handling is minimal and could be improved
- No caching mechanism is implemented

## License

[MIT License](LICENSE)