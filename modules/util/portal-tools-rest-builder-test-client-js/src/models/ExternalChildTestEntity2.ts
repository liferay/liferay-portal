/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ExternalTestEntity1} from './ExternalTestEntity1';

/**
 * @author Alejandro Tardín
 * @generated
 */

	export class ExternalChildTestEntity2 extends ExternalTestEntity1 {
			"externalReferenceCode"?: string;
			"scope"?: any;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "scope",
			name: "scope",
			type: "any",
		},
		];

		static getAttributeTypeMap() {
				return super.getAttributeTypeMap().concat(ExternalChildTestEntity2.attributeTypeMap);
		}
	}
