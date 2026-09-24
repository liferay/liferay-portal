/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Status} from './Status';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectAction {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"active"?: boolean;
			"conditionExpression"?: string;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: {[key: string]: string;};
			"errorMessage"?: {[key: string]: string;};
			"externalReferenceCode"?: string;
			"id"?: number;
			"label"?: {[key: string]: string;};
			"name"?: string;
			"objectActionExecutorKey"?: string;
			"objectActionTriggerKey"?: string;
			"parameters"?: {[key: string]: any;};
			"status"?: Status;
			"system"?: boolean;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "actions",
			name: "actions",
			type: "{[key: string]: {[key: string]: string;};}",
		},
		{
			baseName: "active",
			name: "active",
			type: "boolean",
		},
		{
			baseName: "conditionExpression",
			name: "conditionExpression",
			type: "string",
		},
		{
			baseName: "dateCreated",
			name: "dateCreated",
			type: "Date",
		},
		{
			baseName: "dateModified",
			name: "dateModified",
			type: "Date",
		},
		{
			baseName: "description",
			name: "description",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "errorMessage",
			name: "errorMessage",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "label",
			name: "label",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "objectActionExecutorKey",
			name: "objectActionExecutorKey",
			type: "string",
		},
		{
			baseName: "objectActionTriggerKey",
			name: "objectActionTriggerKey",
			type: "string",
		},
		{
			baseName: "parameters",
			name: "parameters",
			type: "{[key: string]: any;}",
		},
		{
			baseName: "status",
			name: "status",
			type: "Status",
		},
		{
			baseName: "system",
			name: "system",
			type: "boolean",
		},
		];

		static getAttributeTypeMap() {
				return ObjectAction.attributeTypeMap;
		}
	}
