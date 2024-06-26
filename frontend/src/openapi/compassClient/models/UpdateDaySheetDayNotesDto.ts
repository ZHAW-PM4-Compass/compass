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
 * @interface UpdateDaySheetDayNotesDto
 */
export interface UpdateDaySheetDayNotesDto {
    /**
     * 
     * @type {number}
     * @memberof UpdateDaySheetDayNotesDto
     */
    id?: number;
    /**
     * 
     * @type {string}
     * @memberof UpdateDaySheetDayNotesDto
     */
    dayNotes?: string;
}

/**
 * Check if a given object implements the UpdateDaySheetDayNotesDto interface.
 */
export function instanceOfUpdateDaySheetDayNotesDto(value: object): boolean {
    return true;
}

export function UpdateDaySheetDayNotesDtoFromJSON(json: any): UpdateDaySheetDayNotesDto {
    return UpdateDaySheetDayNotesDtoFromJSONTyped(json, false);
}

export function UpdateDaySheetDayNotesDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): UpdateDaySheetDayNotesDto {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'] == null ? undefined : json['id'],
        'dayNotes': json['day_notes'] == null ? undefined : json['day_notes'],
    };
}

export function UpdateDaySheetDayNotesDtoToJSON(value?: UpdateDaySheetDayNotesDto | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'id': value['id'],
        'day_notes': value['dayNotes'],
    };
}

