/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {TestEntity} from './TestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

	export class ChildTestEntity1 extends TestEntity {
			"property1"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "property1",
			name: "property1",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return super.getAttributeTypeMap().concat(ChildTestEntity1.attributeTypeMap);
		}
	}
