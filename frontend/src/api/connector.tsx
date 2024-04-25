import { getSession } from '@auth0/nextjs-auth0';
import { Configuration, DaySheetControllerApi, UserControllerApi } from './compassClient';

async function getApiConfiguration() {
  const session = await getSession();
  const config = new Configuration({
      basePath: process.env.API_BASE_PATH,
      baseOptions: {
          headers: {
              Authorization: `Bearer ${session?.accessToken}`,
          }
      }
  });
  return config;
}

export async function getDaySheetControllerApi() {
  const config = await getApiConfiguration();
  return new DaySheetControllerApi(config);
}

export async function getUserControllerApi() {
  const config = await getApiConfiguration();
  return new UserControllerApi(config);
}