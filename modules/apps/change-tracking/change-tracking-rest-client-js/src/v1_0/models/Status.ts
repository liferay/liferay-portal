/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author David Truong
 * @generated
 */

	export class Status {
			"code"?: number;
			"label"?: string;
			"label_i18n"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "code",
			name: "code",
			type: "number",
		},
		{
			baseName: "label",
			name: "label",
			type: "string",
		},
		{
			baseName: "label_i18n",
			name: "label_i18n",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return Status.attributeTypeMap;
		}
	}
