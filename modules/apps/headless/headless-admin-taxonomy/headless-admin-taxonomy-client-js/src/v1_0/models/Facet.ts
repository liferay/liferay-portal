/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {FacetValue} from './FacetValue';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class Facet {
			"facetCriteria"?: string;
			"facetValues"?: Array<FacetValue>;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "facetCriteria",
			name: "facetCriteria",
			type: "string",
		},
		{
			baseName: "facetValues",
			name: "facetValues",
			type: "Array<FacetValue>",
		},
		];

		static getAttributeTypeMap() {
				return Facet.attributeTypeMap;
		}
	}
