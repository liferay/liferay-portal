/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectLayoutRow} from './ObjectLayoutRow';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectLayoutBox {
			"collapsable"?: boolean;
			"id"?: number;
			"name"?: {[key: string]: string;};
			"objectLayoutRows"?: Array<ObjectLayoutRow>;
			"priority"?: number;
			"type"?: 'categorization' | 'regular' | 'seo';

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "collapsable",
			name: "collapsable",
			type: "boolean",
		},
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
			baseName: "objectLayoutRows",
			name: "objectLayoutRows",
			type: "Array<ObjectLayoutRow>",
		},
		{
			baseName: "priority",
			name: "priority",
			type: "number",
		},
		{
			baseName: "type",
			name: "type",
			type: "'categorization' | 'regular' | 'seo'",
		},
		];

		static getAttributeTypeMap() {
				return ObjectLayoutBox.attributeTypeMap;
		}
	}
