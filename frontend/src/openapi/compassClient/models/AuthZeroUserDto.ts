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
/**
 * 
 * @export
 * @interface AuthZeroUserDto
 */
export interface AuthZeroUserDto {
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    email?: string;
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    givenName?: string;
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    familyName?: string;
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    role?: string;
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    userId?: string;
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    password?: string;
    /**
     * 
     * @type {string}
     * @memberof AuthZeroUserDto
     */
    connection?: string;
}

/**
 * Check if a given object implements the AuthZeroUserDto interface.
 */
export function instanceOfAuthZeroUserDto(value: object): boolean {
    return true;
}

export function AuthZeroUserDtoFromJSON(json: any): AuthZeroUserDto {
    return AuthZeroUserDtoFromJSONTyped(json, false);
}

export function AuthZeroUserDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): AuthZeroUserDto {
    if (json == null) {
        return json;
    }
    return {
        
        'email': json['email'] == null ? undefined : json['email'],
        'givenName': json['given_name'] == null ? undefined : json['given_name'],
        'familyName': json['family_name'] == null ? undefined : json['family_name'],
        'role': json['role'] == null ? undefined : json['role'],
        'userId': json['user_id'] == null ? undefined : json['user_id'],
        'password': json['password'] == null ? undefined : json['password'],
        'connection': json['connection'] == null ? undefined : json['connection'],
    };
}

export function AuthZeroUserDtoToJSON(value?: AuthZeroUserDto | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'email': value['email'],
        'given_name': value['givenName'],
        'family_name': value['familyName'],
        'role': value['role'],
        'user_id': value['userId'],
        'password': value['password'],
        'connection': value['connection'],
    };
}

