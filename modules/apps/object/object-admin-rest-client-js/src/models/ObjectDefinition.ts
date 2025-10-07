/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Creator} from './Creator';
			import {ObjectAction} from './ObjectAction';
			import {ObjectDefinitionSetting} from './ObjectDefinitionSetting';
			import {ObjectField} from './ObjectField';
			import {ObjectLayout} from './ObjectLayout';
			import {ObjectRelationship} from './ObjectRelationship';
			import {ObjectValidationRule} from './ObjectValidationRule';
			import {ObjectView} from './ObjectView';
			import {Status} from './Status';
			import {WorkflowDefinitionLink} from './WorkflowDefinitionLink';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectDefinition {
			"accountEntryRestricted"?: boolean;
			"accountEntryRestrictedObjectFieldName"?: string;
			"actions"?: {[key: string]: {[key: string]: string;};};
			"active"?: boolean;
			"className"?: string;
			"creator"?: Creator;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"defaultLanguageId"?: string;
			"enableCategorization"?: boolean;
			"enableComments"?: boolean;
			"enableFormContainer"?: boolean;
			"enableFriendlyURLCustomization"?: boolean;
			"enableIndexSearch"?: boolean;
			"enableLocalization"?: boolean;
			"enableObjectEntryDraft"?: boolean;
			"enableObjectEntryHistory"?: boolean;
			"enableObjectEntrySchedule"?: boolean;
			"enableObjectEntrySubscription"?: boolean;
			"enableObjectEntryVersioning"?: boolean;
			"externalReferenceCode"?: string;
			"friendlyURLSeparator"?: string;
			"id"?: number;
			"label"?: {[key: string]: string;};
			"modifiable"?: boolean;
			"name"?: string;
			"objectActions"?: Array<ObjectAction>;
			"objectDefinitionSettings"?: Array<ObjectDefinitionSetting>;
			"objectFields"?: Array<ObjectField>;
			"objectFolderExternalReferenceCode"?: string;
			"objectLayouts"?: Array<ObjectLayout>;
			"objectRelationships"?: Array<ObjectRelationship>;
			"objectValidationRules"?: Array<ObjectValidationRule>;
			"objectViews"?: Array<ObjectView>;
			"panelAppOrder"?: string;
			"panelCategoryKey"?: string;
			"parameterRequired"?: boolean;
			"pluralLabel"?: {[key: string]: string;};
			"portlet"?: boolean;
			"restContextPath"?: string;
			"rootObjectDefinitionExternalReferenceCode"?: string;
			"scope"?: string;
			"status"?: Status;
			"storageType"?: string;
			"system"?: boolean;
			"titleObjectFieldName"?: string;
			"workflowDefinitionLinks"?: Array<WorkflowDefinitionLink>;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "accountEntryRestricted",
			name: "accountEntryRestricted",
			type: "boolean",
		},
		{
			baseName: "accountEntryRestrictedObjectFieldName",
			name: "accountEntryRestrictedObjectFieldName",
			type: "string",
		},
		{
			baseName: "actions",
			name: "actions",
			type: "{[key: string]: {[key: string]: string;};}",
		},
		{
			baseName: "active",
			name: "active",
			type: "boolean",
		},
		{
			baseName: "className",
			name: "className",
			type: "string",
		},
		{
			baseName: "creator",
			name: "creator",
			type: "Creator",
		},
		{
			baseName: "dateCreated",
			name: "dateCreated",
			type: "Date",
		},
		{
			baseName: "dateModified",
			name: "dateModified",
			type: "Date",
		},
		{
			baseName: "defaultLanguageId",
			name: "defaultLanguageId",
			type: "string",
		},
		{
			baseName: "enableCategorization",
			name: "enableCategorization",
			type: "boolean",
		},
		{
			baseName: "enableComments",
			name: "enableComments",
			type: "boolean",
		},
		{
			baseName: "enableFormContainer",
			name: "enableFormContainer",
			type: "boolean",
		},
		{
			baseName: "enableFriendlyURLCustomization",
			name: "enableFriendlyURLCustomization",
			type: "boolean",
		},
		{
			baseName: "enableIndexSearch",
			name: "enableIndexSearch",
			type: "boolean",
		},
		{
			baseName: "enableLocalization",
			name: "enableLocalization",
			type: "boolean",
		},
		{
			baseName: "enableObjectEntryDraft",
			name: "enableObjectEntryDraft",
			type: "boolean",
		},
		{
			baseName: "enableObjectEntryHistory",
			name: "enableObjectEntryHistory",
			type: "boolean",
		},
		{
			baseName: "enableObjectEntrySchedule",
			name: "enableObjectEntrySchedule",
			type: "boolean",
		},
		{
			baseName: "enableObjectEntrySubscription",
			name: "enableObjectEntrySubscription",
			type: "boolean",
		},
		{
			baseName: "enableObjectEntryVersioning",
			name: "enableObjectEntryVersioning",
			type: "boolean",
		},
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "friendlyURLSeparator",
			name: "friendlyURLSeparator",
			type: "string",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "label",
			name: "label",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "modifiable",
			name: "modifiable",
			type: "boolean",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "objectActions",
			name: "objectActions",
			type: "Array<ObjectAction>",
		},
		{
			baseName: "objectDefinitionSettings",
			name: "objectDefinitionSettings",
			type: "Array<ObjectDefinitionSetting>",
		},
		{
			baseName: "objectFields",
			name: "objectFields",
			type: "Array<ObjectField>",
		},
		{
			baseName: "objectFolderExternalReferenceCode",
			name: "objectFolderExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "objectLayouts",
			name: "objectLayouts",
			type: "Array<ObjectLayout>",
		},
		{
			baseName: "objectRelationships",
			name: "objectRelationships",
			type: "Array<ObjectRelationship>",
		},
		{
			baseName: "objectValidationRules",
			name: "objectValidationRules",
			type: "Array<ObjectValidationRule>",
		},
		{
			baseName: "objectViews",
			name: "objectViews",
			type: "Array<ObjectView>",
		},
		{
			baseName: "panelAppOrder",
			name: "panelAppOrder",
			type: "string",
		},
		{
			baseName: "panelCategoryKey",
			name: "panelCategoryKey",
			type: "string",
		},
		{
			baseName: "parameterRequired",
			name: "parameterRequired",
			type: "boolean",
		},
		{
			baseName: "pluralLabel",
			name: "pluralLabel",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "portlet",
			name: "portlet",
			type: "boolean",
		},
		{
			baseName: "restContextPath",
			name: "restContextPath",
			type: "string",
		},
		{
			baseName: "rootObjectDefinitionExternalReferenceCode",
			name: "rootObjectDefinitionExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "scope",
			name: "scope",
			type: "string",
		},
		{
			baseName: "status",
			name: "status",
			type: "Status",
		},
		{
			baseName: "storageType",
			name: "storageType",
			type: "string",
		},
		{
			baseName: "system",
			name: "system",
			type: "boolean",
		},
		{
			baseName: "titleObjectFieldName",
			name: "titleObjectFieldName",
			type: "string",
		},
		{
			baseName: "workflowDefinitionLinks",
			name: "workflowDefinitionLinks",
			type: "Array<WorkflowDefinitionLink>",
		},
		];

		static getAttributeTypeMap() {
				return ObjectDefinition.attributeTypeMap;
		}
	}
