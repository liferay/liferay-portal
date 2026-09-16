/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	export class ExternalTestEntity1 {
			"type"?: 'ExternalChildTestEntity1' | 'ExternalChildTestEntity2';

		static "discriminator": string | undefined = "type";

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "type",
			name: "type",
			type: "'ExternalChildTestEntity1' | 'ExternalChildTestEntity2'",
		},
		];

		static getAttributeTypeMap() {
				return ExternalTestEntity1.attributeTypeMap;
		}
	}
