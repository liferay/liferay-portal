/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectLayoutColumn} from './ObjectLayoutColumn';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectLayoutRow {
			"id"?: number;
			"objectLayoutColumns"?: Array<ObjectLayoutColumn>;
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
			baseName: "objectLayoutColumns",
			name: "objectLayoutColumns",
			type: "Array<ObjectLayoutColumn>",
		},
		{
			baseName: "priority",
			name: "priority",
			type: "number",
		},
		];

		static getAttributeTypeMap() {
				return ObjectLayoutRow.attributeTypeMap;
		}
	}
