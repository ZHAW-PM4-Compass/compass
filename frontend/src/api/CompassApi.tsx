import { env } from 'process';
import { Configuration, DaySheetControllerApi } from './compassClient'; // Adjust the import path as necessary
import axios from 'axios';

class CompassApi {
  getApiConfiguration = () => {
    return this.fetchAccessToken().then(accessToken => {
      return new Configuration({
        basePath: env.REACT_APP_API_BASE_PATH, // Use the environment variable for the base path
        baseOptions: {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          }
        }
      })
    });
  }

  private fetchAccessToken = () => {
    return axios.get("/api/auth/token").then(response => {
      return response.data.accessToken;
    });
  };

  public daySheetApi = async () => {
    return this.getApiConfiguration().then(config => {
      return new DaySheetControllerApi(config);
    });
  };
}

export default CompassApi;