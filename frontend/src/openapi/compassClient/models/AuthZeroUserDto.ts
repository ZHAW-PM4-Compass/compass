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
    role?: AuthZeroUserDtoRoleEnum;
    /**
     * 
     * @type {boolean}
     * @memberof AuthZeroUserDto
     */
    blocked?: boolean;
}


/**
 * @export
 */
export const AuthZeroUserDtoRoleEnum = {
    SocialWorker: 'SOCIAL_WORKER',
    Participant: 'PARTICIPANT',
    Admin: 'ADMIN',
    NoRole: 'NO_ROLE'
} as const;
export type AuthZeroUserDtoRoleEnum = typeof AuthZeroUserDtoRoleEnum[keyof typeof AuthZeroUserDtoRoleEnum];


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
        'blocked': json['blocked'] == null ? undefined : json['blocked'],
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
        'blocked': value['blocked'],
    };
}

