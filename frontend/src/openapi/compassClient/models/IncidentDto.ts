/* tslint:disable */
/* eslint-disable */
/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { mapValues } from '../runtime';
import type { UserDto } from './UserDto';
import {
    UserDtoFromJSON,
    UserDtoFromJSONTyped,
    UserDtoToJSON,
} from './UserDto';

/**
 * 
 * @export
 * @interface IncidentDto
 */
export interface IncidentDto {
    /**
     * 
     * @type {number}
     * @memberof IncidentDto
     */
    id?: number;
    /**
     * 
     * @type {string}
     * @memberof IncidentDto
     */
    title?: string;
    /**
     * 
     * @type {string}
     * @memberof IncidentDto
     */
    description?: string;
    /**
     * 
     * @type {Date}
     * @memberof IncidentDto
     */
    date?: Date;
    /**
     * 
     * @type {UserDto}
     * @memberof IncidentDto
     */
    user?: UserDto;
}

/**
 * Check if a given object implements the IncidentDto interface.
 */
export function instanceOfIncidentDto(value: object): boolean {
    return true;
}

export function IncidentDtoFromJSON(json: any): IncidentDto {
    return IncidentDtoFromJSONTyped(json, false);
}

export function IncidentDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): IncidentDto {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'] == null ? undefined : json['id'],
        'title': json['title'] == null ? undefined : json['title'],
        'description': json['description'] == null ? undefined : json['description'],
        'date': json['date'] == null ? undefined : (new Date(json['date'])),
        'user': json['user'] == null ? undefined : UserDtoFromJSON(json['user']),
    };
}

export function IncidentDtoToJSON(value?: IncidentDto | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'id': value['id'],
        'title': value['title'],
        'description': value['description'],
        'date': value['date'] == null ? undefined : ((value['date']).toISOString().substring(0,10)),
        'user': UserDtoToJSON(value['user']),
    };
}

