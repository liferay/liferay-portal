/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class WorkflowDefinitionLink {
			"groupExternalReferenceCode"?: string;
			"workflowDefinitionName"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "groupExternalReferenceCode",
			name: "groupExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "workflowDefinitionName",
			name: "workflowDefinitionName",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return WorkflowDefinitionLink.attributeTypeMap;
		}
	}
