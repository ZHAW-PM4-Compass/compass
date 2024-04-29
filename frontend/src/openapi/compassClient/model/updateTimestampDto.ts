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

import { RequestFile } from './models';

export class UpdateTimestampDto {
    'id'?: number;
    'daySheetId'?: number;
    'startTime'?: Date;
    'endTime'?: Date;

    static discriminator: string | undefined = undefined;

    static attributeTypeMap: Array<{name: string, baseName: string, type: string}> = [
        {
            "name": "id",
            "baseName": "id",
            "type": "number"
        },
        {
            "name": "daySheetId",
            "baseName": "day_sheet_id",
            "type": "number"
        },
        {
            "name": "startTime",
            "baseName": "start_time",
            "type": "Date"
        },
        {
            "name": "endTime",
            "baseName": "end_time",
            "type": "Date"
        }    ];

    static getAttributeTypeMap() {
        return UpdateTimestampDto.attributeTypeMap;
    }
}

