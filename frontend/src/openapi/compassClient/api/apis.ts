export * from './baseControllerApi';
import { BaseControllerApi } from './baseControllerApi';
export * from './daySheetControllerApi';
import { DaySheetControllerApi } from './daySheetControllerApi';
export * from './timestampControllerApi';
import { TimestampControllerApi } from './timestampControllerApi';
export * from './userControllerApi';
import { UserControllerApi } from './userControllerApi';
import * as http from 'http';

export class HttpError extends Error {
    constructor (public response: http.IncomingMessage, public body: any, public statusCode?: number) {
        super('HTTP request failed');
        this.name = 'HttpError';
    }
}

export { RequestFile } from '../model/models';

export const APIS = [BaseControllerApi, DaySheetControllerApi, TimestampControllerApi, UserControllerApi];
