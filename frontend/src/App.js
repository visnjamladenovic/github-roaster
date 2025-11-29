import React, { useState } from 'react';
import './App.css';
import { getRoast } from './services/api';

function App() {
    const [username, setUsername] = useState('');
    const [roastText, setRoastText] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [copySuccess, setCopySuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!username.trim()) return;

        setLoading(true);
        setError(null);
        setRoastText('');
        setCopySuccess(false);

        try {
            const roastResponse = await getRoast(username);
            setRoastText(roastResponse.roast);
        } catch (err) {
            setError(err.message || 'Failed to fetch roast. Please check the username and try again.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleCopyToClipboard = () => {
        const textToCopy = `🔥 GitHub Roast for @${username} 🔥\n\n${roastText}\n\n👉 Get roasted at: ${window.location.origin}`;

        navigator.clipboard.writeText(textToCopy).then(() => {
            setCopySuccess(true);
            setTimeout(() => setCopySuccess(false), 2000);
        }).catch(err => {
            console.error('Failed to copy:', err);
        });
    };

    const handleShareTwitter = () => {
        const text = `🔥 I just got roasted by GitHub Roaster! 🔥\n\n"${roastText.substring(0, 150)}..."\n\n`;
        const url = window.location.origin;
        const twitterUrl = `https://twitter.com/intent/tweet?text=${encodeURIComponent(text)}&url=${encodeURIComponent(url)}`;
        window.open(twitterUrl, '_blank');
    };

    const handleShareLinkedIn = () => {
        const url = window.location.origin;
        const linkedInUrl = `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(url)}`;
        window.open(linkedInUrl, '_blank');
    };

    const handleShareFacebook = () => {
        const url = window.location.origin;
        const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`;
        window.open(facebookUrl, '_blank');
    };

    const handleNewRoast = () => {
        setUsername('');
        setRoastText('');
        setError(null);
        setCopySuccess(false);
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

                {roastText && (
                    <div className="roast-result">
                        <div className="roast-content">
                            <h3>🔥 Your Roast 🔥</h3>
                            <div className="username-badge">@{username}</div>
                            <p className="roast-text">
                                {roastText}
                            </p>

                            {/* Share Buttons */}
                            <div className="share-section">
                                <h4>Share Your Roast:</h4>
                                <div className="share-buttons">
                                    <button
                                        className="share-button copy-button"
                                        onClick={handleCopyToClipboard}
                                        title="Copy to clipboard"
                                    >
                                        {copySuccess ? '✓ Copied!' : '📋 Copy'}
                                    </button>
                                    <button
                                        className="share-button twitter-button"
                                        onClick={handleShareTwitter}
                                        title="Share on Twitter"
                                    >
                                        🐦 Twitter
                                    </button>
                                    <button
                                        className="share-button linkedin-button"
                                        onClick={handleShareLinkedIn}
                                        title="Share on LinkedIn"
                                    >
                                        💼 LinkedIn
                                    </button>
                                    <button
                                        className="share-button facebook-button"
                                        onClick={handleShareFacebook}
                                        title="Share on Facebook"
                                    >
                                        📘 Facebook
                                    </button>
                                </div>
                            </div>

                            {/* New Roast Button */}
                            <div className="action-buttons">
                                <button
                                    className="new-roast-button"
                                    onClick={handleNewRoast}
                                >
                                    🔥 Roast Another User
                                </button>
                            </div>
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