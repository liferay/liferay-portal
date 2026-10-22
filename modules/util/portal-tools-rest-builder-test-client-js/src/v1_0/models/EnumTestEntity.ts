/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	export class EnumTestEntity {
			"testEnum"?: '-1em' | '-0.95em' | '1em' | '0.95em';

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "testEnum",
			name: "testEnum",
			type: "'-1em' | '-0.95em' | '1em' | '0.95em'",
		},
		];

		static getAttributeTypeMap() {
				return EnumTestEntity.attributeTypeMap;
		}
	}
