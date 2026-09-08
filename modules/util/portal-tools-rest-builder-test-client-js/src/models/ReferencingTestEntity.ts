/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ExternalScopedTestEntity} from './ExternalScopedTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

	export class ReferencingTestEntity {
			"externalScopedTestEntity"?: ExternalScopedTestEntity;

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
		];

		static getAttributeTypeMap() {
				return ReferencingTestEntity.attributeTypeMap;
		}
	}
