/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectDefinitionValidationError {
			"errorMessage"?: string;
			"exceptionClassName"?: string;
			"objectDefinitionName"?: string;
			"objectFieldName"?: string;
			"objectFieldValue"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "errorMessage",
			name: "errorMessage",
			type: "string",
		},
		{
			baseName: "exceptionClassName",
			name: "exceptionClassName",
			type: "string",
		},
		{
			baseName: "objectDefinitionName",
			name: "objectDefinitionName",
			type: "string",
		},
		{
			baseName: "objectFieldName",
			name: "objectFieldName",
			type: "string",
		},
		{
			baseName: "objectFieldValue",
			name: "objectFieldValue",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return ObjectDefinitionValidationError.attributeTypeMap;
		}
	}
