import { env } from 'process';
import { Configuration, DaySheetControllerApi } from './compassClient'; // Adjust the import path as necessary


class CompassApi {
  // Function to get the API configuration


  // Function to fetch the token; assumed to be asynchronous
  //async function fetchAccessToken(): Promise<string> {
  //  const response = await fetch("/api/auth/token");
  //  const { accessToken } = await response.json();
  //  return accessToken;
  //}

  getApiConfiguration() {
    return new Configuration({
      basePath: env.REACT_APP_API_BASE_PATH, // Use the environment variable for the base path
      //accessToken: () => fetchAccessToken(), // Use the async function to fetch the token
    });
  }


  public daySheetApi = () => {
    const api = new DaySheetControllerApi(this.getApiConfiguration());
    return api;
  };


  
}

export default CompassApi;