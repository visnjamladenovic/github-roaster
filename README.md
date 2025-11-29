# GitHub Roaster

A full-stack application that analyzes GitHub profiles and generates humorous AI-powered "roasts" based on a user's GitHub activity.

## Description

GitHub Roaster is a web application that combines a Spring Boot backend with a React frontend to provide a fun and entertaining way to analyze GitHub profiles. The application fetches data from the GitHub API, analyzes it, and uses OpenAI's GPT model to generate witty, personalized roasts about a user's coding habits, repository patterns, and GitHub activity.

The application can be used to:
- Retrieve GitHub user profiles and activity data
- Analyze repositories, commits, and coding patterns
- Generate AI-powered humorous "roasts" based on the analysis
- Share roasts on social media platforms

## Features

### GitHub Data Analysis
- User profile analysis
- Repository analysis (star counts, languages, naming patterns)
- Commit pattern analysis (frequency, timing, message quality)
- Activity and contribution pattern analysis

### AI-Powered Roasting
- Integration with OpenAI's GPT-4o-mini model
- Personalized, context-aware roasts based on actual GitHub data
- Fallback roasting mechanism when API is unavailable

### User-Friendly Interface
- Simple, intuitive React frontend
- Easy sharing options (copy to clipboard, Twitter, LinkedIn, Facebook)
- Mobile-responsive design

## Requirements

### Backend
- Java 17 or higher
- Gradle 7.6 or higher
- Kotlin 1.9.25
- OpenAI API key
- GitHub API token (optional, but recommended to avoid rate limiting)

### Frontend
- Node.js 14 or higher
- npm or yarn

## Installation and Setup

### Backend Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/github-roaster.git
   cd github-roaster
   ```

2. Set up environment variables:
   - Create a `.env` file in the project root with:
   ```
   OPENAI_API_KEY=your_openai_api_key
   GITHUB_TOKEN=your_github_token
   ```

3. Build and run the backend:
   ```bash
   ./gradlew bootRun
   ```

   The backend will start on port 8080.

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the frontend development server:
   ```bash
   npm start
   ```

   The frontend will start on port 3000 and open in your default browser.

## Usage

1. Open the application in your browser (http://localhost:3000 if running locally)
2. Enter a GitHub username in the input field
3. Click "Roast!" to generate a personalized roast
4. Share the roast using the provided sharing options

## API Endpoints

### Roast Endpoints

#### Get AI-Generated Roast
```
GET /api/github/roast/{username}
```
Returns an AI-generated roast for the specified GitHub user.

#### Get Roast Data
```
GET /api/github/roast-data/{username}
```
Returns comprehensive data about a user that can be used for roasting purposes.

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

- `src/main/kotlin/com/snjavi/githubroaster/`
  - `controller/`: REST controllers that define API endpoints
  - `service/`: Business logic for GitHub API interaction, data analysis, and OpenAI integration
  - `dto/`: Data transfer objects representing GitHub entities
  - `config/`: Application configuration

- `frontend/`
  - `src/`: React application source code
  - `public/`: Static assets

## Technologies Used

### Backend
- Kotlin
- Spring Boot
- WebFlux for reactive programming
- OpenAI API

### Frontend
- React
- JavaScript/ES6
- CSS3

## Limitations

- The application requires an OpenAI API key for full functionality
- Without a GitHub token, the application is subject to GitHub API rate limiting
- The roast quality depends on the amount and quality of data available on the user's GitHub profile

## License

[MIT License](LICENSE)

## Acknowledgements

- OpenAI for providing the GPT API
- GitHub for their public API
- All contributors to this project