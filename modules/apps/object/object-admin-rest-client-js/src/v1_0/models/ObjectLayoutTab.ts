/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectLayoutBox} from './ObjectLayoutBox';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectLayoutTab {
			"id"?: number;
			"name"?: {[key: string]: string;};
			"objectLayoutBoxes"?: Array<ObjectLayoutBox>;
			"objectRelationshipExternalReferenceCode"?: string;
			"objectRelationshipId"?: number;
			"priority"?: number;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "name",
			name: "name",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "objectLayoutBoxes",
			name: "objectLayoutBoxes",
			type: "Array<ObjectLayoutBox>",
		},
		{
			baseName: "objectRelationshipExternalReferenceCode",
			name: "objectRelationshipExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "objectRelationshipId",
			name: "objectRelationshipId",
			type: "number",
		},
		{
			baseName: "priority",
			name: "priority",
			type: "number",
		},
		];

		static getAttributeTypeMap() {
				return ObjectLayoutTab.attributeTypeMap;
		}
	}
