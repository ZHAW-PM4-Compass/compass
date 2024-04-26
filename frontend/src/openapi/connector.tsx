import { Configuration, DaySheetControllerApi, UserControllerApi } from './compassClient';

function getApiConfiguration() {
  const config = new Configuration({
      basePath: "/api/proxy",
  });
  return config;
}

export function getDaySheetControllerApi() {
  const config = getApiConfiguration();
  return new DaySheetControllerApi(config);
}

export function getUserControllerApi() {
  const config = getApiConfiguration();
  return new UserControllerApi(config);
}