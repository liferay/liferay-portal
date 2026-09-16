/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ExternalScopedTestEntity} from './ExternalScopedTestEntity';
			import {ExternalTestEntity1} from './ExternalTestEntity1';
			import {ExternalTestEntity2} from './ExternalTestEntity2';

/**
 * @author Alejandro Tardín
 * @generated
 */

	export class ReferencingTestEntity {
			"externalScopedTestEntity"?: ExternalScopedTestEntity;
			"externalTestEntity1"?: ExternalTestEntity1;
			"externalTestEntity2"?: ExternalTestEntity2;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "externalScopedTestEntity",
			name: "externalScopedTestEntity",
			type: "ExternalScopedTestEntity",
		},
		{
			baseName: "externalTestEntity1",
			name: "externalTestEntity1",
			type: "ExternalTestEntity1",
		},
		{
			baseName: "externalTestEntity2",
			name: "externalTestEntity2",
			type: "ExternalTestEntity2",
		},
		];

		static getAttributeTypeMap() {
				return ReferencingTestEntity.attributeTypeMap;
		}
	}
