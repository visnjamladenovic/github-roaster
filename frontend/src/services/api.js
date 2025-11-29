// API service for GitHub Roaster application

const API_BASE_URL = 'http://localhost:8080/api/github';

// Function to check if the backend is reachable
export const pingBackend = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/ping`);
    return await response.text();
  } catch (error) {
    console.error('Error pinging backend:', error);
    throw error;
  }
};

// Function to get roast data for a GitHub user
export const getRoastData = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/roast-data/${username}`);
    
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error(`GitHub user "${username}" not found. Please check the username and try again.`);
      } else {
        throw new Error(`Failed to fetch roast data: ${response.status} ${response.statusText}`);
      }
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error fetching roast data:', error);
    throw error;
  }
};

// Function to get user profile data
export const getUserProfile = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/user/${username}`);
    
    if (!response.ok) {
      throw new Error(`Failed to fetch user profile: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error fetching user profile:', error);
    throw error;
  }
};