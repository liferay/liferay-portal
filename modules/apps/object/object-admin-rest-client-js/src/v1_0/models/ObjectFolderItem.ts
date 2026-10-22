/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectDefinition} from './ObjectDefinition';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectFolderItem {
			"linkedObjectDefinition"?: boolean;
			"objectDefinition"?: ObjectDefinition;
			"objectDefinitionExternalReferenceCode"?: string;
			"positionX"?: number;
			"positionY"?: number;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "linkedObjectDefinition",
			name: "linkedObjectDefinition",
			type: "boolean",
		},
		{
			baseName: "objectDefinition",
			name: "objectDefinition",
			type: "ObjectDefinition",
		},
		{
			baseName: "objectDefinitionExternalReferenceCode",
			name: "objectDefinitionExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "positionX",
			name: "positionX",
			type: "number",
		},
		{
			baseName: "positionY",
			name: "positionY",
			type: "number",
		},
		];

		static getAttributeTypeMap() {
				return ObjectFolderItem.attributeTypeMap;
		}
	}
