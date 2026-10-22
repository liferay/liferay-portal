/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Facet} from './Facet';
			import {ObjectLayout} from './ObjectLayout';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class PageObjectLayout {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"facets"?: Array<Facet>;
			"items"?: Array<ObjectLayout>;
			"lastPage"?: number;
			"page"?: number;
			"pageSize"?: number;
			"totalCount"?: number;

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
			baseName: "facets",
			name: "facets",
			type: "Array<Facet>",
		},
		{
			baseName: "items",
			name: "items",
			type: "Array<ObjectLayout>",
		},
		{
			baseName: "lastPage",
			name: "lastPage",
			type: "number",
		},
		{
			baseName: "page",
			name: "page",
			type: "number",
		},
		{
			baseName: "pageSize",
			name: "pageSize",
			type: "number",
		},
		{
			baseName: "totalCount",
			name: "totalCount",
			type: "number",
		},
		];

		static getAttributeTypeMap() {
				return PageObjectLayout.attributeTypeMap;
		}
	}
