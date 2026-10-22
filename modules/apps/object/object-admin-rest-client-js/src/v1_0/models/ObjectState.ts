/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectStateTransition} from './ObjectStateTransition';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectState {
			"id"?: number;
			"key"?: string;
			"objectStateTransitions"?: Array<ObjectStateTransition>;

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
			baseName: "key",
			name: "key",
			type: "string",
		},
		{
			baseName: "objectStateTransitions",
			name: "objectStateTransitions",
			type: "Array<ObjectStateTransition>",
		},
		];

		static getAttributeTypeMap() {
				return ObjectState.attributeTypeMap;
		}
	}
