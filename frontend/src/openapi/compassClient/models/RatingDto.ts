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
import type { CategoryDto } from './CategoryDto';
import {
    CategoryDtoFromJSON,
    CategoryDtoFromJSONTyped,
    CategoryDtoToJSON,
} from './CategoryDto';

/**
 * 
 * @export
 * @interface RatingDto
 */
export interface RatingDto {
    /**
     * 
     * @type {CategoryDto}
     * @memberof RatingDto
     */
    category?: CategoryDto;
    /**
     * 
     * @type {number}
     * @memberof RatingDto
     */
    rating?: number;
    /**
     * 
     * @type {string}
     * @memberof RatingDto
     */
    ratingRole?: RatingDtoRatingRoleEnum;
}


/**
 * @export
 */
export const RatingDtoRatingRoleEnum = {
    SocialWorker: 'SOCIAL_WORKER',
    Participant: 'PARTICIPANT'
} as const;
export type RatingDtoRatingRoleEnum = typeof RatingDtoRatingRoleEnum[keyof typeof RatingDtoRatingRoleEnum];


/**
 * Check if a given object implements the RatingDto interface.
 */
export function instanceOfRatingDto(value: object): boolean {
    return true;
}

export function RatingDtoFromJSON(json: any): RatingDto {
    return RatingDtoFromJSONTyped(json, false);
}

export function RatingDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): RatingDto {
    if (json == null) {
        return json;
    }
    return {
        
        'category': json['category'] == null ? undefined : CategoryDtoFromJSON(json['category']),
        'rating': json['rating'] == null ? undefined : json['rating'],
        'ratingRole': json['ratingRole'] == null ? undefined : json['ratingRole'],
    };
}

export function RatingDtoToJSON(value?: RatingDto | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'category': CategoryDtoToJSON(value['category']),
        'rating': value['rating'],
        'ratingRole': value['ratingRole'],
    };
}

