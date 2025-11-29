import React, { useState } from 'react';
import './App.css';
import { getRoastData } from './services/api';

// Function to generate a funny roast based on GitHub data
const generateRoast = (data) => {
  if (!data || !data.profile) {
    return "I can't roast what I can't see. Are you even on GitHub?";
  }

  const roasts = [];
  
  // Roast based on profile
  if (data.profile) {
    if (!data.profile.bio) {
      roasts.push("No bio? Let me guess, you're too busy copying code from Stack Overflow to write one.");
    } else if (data.profile.bio.length < 20) {
      roasts.push(`"${data.profile.bio}" - That's your bio? Even your commit messages are probably more descriptive.`);
    }
    
    if (data.profile.followers === 0) {
      roasts.push("Zero followers? Even npm bots have more social life than you.");
    } else if (data.profile.followers < 10) {
      roasts.push(`${data.profile.followers} followers? Your mom and alternate accounts don't count.`);
    }
  }
  
  // Roast based on repositories
  if (data.repositories) {
    if (data.repositories.length === 0) {
      roasts.push("No repositories? GitHub isn't just for stalking other developers, you know.");
    } else {
      const forkCount = data.repositories.filter(repo => repo.fork).length;
      const forkPercentage = (forkCount / data.repositories.length) * 100;
      
      if (forkPercentage > 70) {
        roasts.push(`${forkPercentage.toFixed(0)}% of your repos are forks. Copying code doesn't make you a developer.`);
      }
      
      const lowStarRepos = data.repositories.filter(repo => repo.stars < 2).length;
      if (lowStarRepos === data.repositories.length && data.repositories.length > 3) {
        roasts.push("Not a single star on any of your repos? Even 'Hello World' projects get pity stars.");
      }
    }
  }
  
  // Roast based on commits
  if (data.commits && data.commits.length > 0) {
    const shortCommitMessages = data.commits.filter(commit => 
      commit.commit && commit.commit.message && commit.commit.message.length < 10
    );
    
    if (shortCommitMessages.length > data.commits.length / 2) {
      roasts.push("'Fixed bug', 'Update', 'WIP'... Your commit messages are as descriptive as your dating profile.");
    }
  }
  
  // Add some generic roasts if we don't have enough specific ones
  if (roasts.length < 2) {
    roasts.push(
      "Your code is like a mystery novel, but not the good kind where everything makes sense in the end.",
      "Your GitHub profile is like a desert - barren, empty, and makes people want to leave immediately.",
      "I've seen more impressive GitHub activity from bootcamp students in their first week.",
      "Your commit history looks like you're trying to spell SOS in Morse code."
    );
  }
  
  // Return 1-2 roasts
  return roasts.slice(0, 2).join(" ");
};

function App() {
  const [username, setUsername] = useState('');
  const [roastData, setRoastData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim()) return;

    setLoading(true);
    setError(null);
    setRoastData(null);

    try {
      const data = await getRoastData(username);
      setRoastData(data);
    } catch (err) {
      setError(err.message || 'Failed to fetch roast data. Please check the username and try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>GitHub Roaster 🔥</h1>
        <p className="subtitle">Enter a GitHub username and get roasted!</p>
      </header>

      <main className="App-main">
        <form onSubmit={handleSubmit} className="username-form">
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter GitHub username"
            className="username-input"
          />
          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? 'Roasting...' : 'Roast!'}
          </button>
        </form>

        {error && <div className="error-message">{error}</div>}

        {loading && <div className="loading">Preparing your roast...</div>}

        {roastData && (
          <div className="roast-result">
            <div className="user-info">
              {roastData.profile?.avatarUrl && (
                <img 
                  src={roastData.profile.avatarUrl} 
                  alt={`${username}'s avatar`} 
                  className="user-avatar" 
                />
              )}
              <h2>{roastData.profile?.name || username}</h2>
              {roastData.profile?.bio && <p className="user-bio">{roastData.profile.bio}</p>}
            </div>
            
            <div className="roast-content">
              <h3>🔥 Your Roast 🔥</h3>
              <p className="roast-text">
                {generateRoast(roastData) || "Hmm, there's not much to roast here. Are you even a developer?"}
              </p>
              
              {roastData.commits && roastData.commits.length > 0 && (
                <div className="commit-section">
                  <h4>Notable Commit Messages:</h4>
                  <ul className="commit-list">
                    {roastData.commits.slice(0, 3).map((commit, index) => (
                      <li key={index}>{commit.commit?.message || "Empty commit message"}</li>
                    ))}
                  </ul>
                </div>
              )}
              
              {roastData.repositories && roastData.repositories.length > 0 && (
                <div className="repos-section">
                  <h4>Repository Stats:</h4>
                  <p>You have {roastData.repositories.length} public repositories.</p>
                  <p>
                    Most starred repo: {
                      roastData.repositories.sort((a, b) => b.stars - a.stars)[0]?.name || "None"
                    } with {
                      roastData.repositories.sort((a, b) => b.stars - a.stars)[0]?.stars || 0
                    } stars.
                  </p>
                </div>
              )}
            </div>
          </div>
        )}
      </main>
      
      <footer className="App-footer">
        <p>© {new Date().getFullYear()} GitHub Roaster | For entertainment purposes only</p>
      </footer>
    </div>
  );
}

export default App;
