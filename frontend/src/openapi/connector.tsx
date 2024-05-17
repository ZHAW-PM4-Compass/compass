import { Configuration, DaysheetControllerApi, TimestampControllerApi, UserControllerApi, CategoryControllerApi } from './compassClient';
export function getMiddleWareControllerApi() {
  const config = new Configuration({
    basePath: process.env.API_BASE_PATH || "http://localhost:8080/api",
  });
  return new UserControllerApi(config);
}
function getApiConfiguration() {
  const config = new Configuration({
      basePath: "/api/proxy",
  });
  return config;
}

export function getDaySheetControllerApi() {
  const config = getApiConfiguration();
  return new DaysheetControllerApi(config);
}

export function getUserControllerApi() {
  const config = getApiConfiguration();
  return new UserControllerApi(config);
}

export function getCategoryControllerApi() {
  const config = getApiConfiguration();
  return new CategoryControllerApi(config);
}

export function getTimestampControllerApi() {
  const config = getApiConfiguration();
  return new TimestampControllerApi(config);
}