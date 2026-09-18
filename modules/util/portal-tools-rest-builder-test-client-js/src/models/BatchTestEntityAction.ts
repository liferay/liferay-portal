/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	/**
	* Reproduces an operation whose schema already names its parent, so the generated method is post<Parent><Schema> and carries no path parameter.
	*/
	export class BatchTestEntityAction {
			"name"?: string;
			"sourceBatchTestEntityId"?: number;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "sourceBatchTestEntityId",
			name: "sourceBatchTestEntityId",
			type: "number",
		},
		];

		static getAttributeTypeMap() {
				return BatchTestEntityAction.attributeTypeMap;
		}
	}
