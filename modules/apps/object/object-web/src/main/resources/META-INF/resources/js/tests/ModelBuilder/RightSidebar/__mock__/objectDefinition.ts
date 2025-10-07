/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const objectDefinition = {
	accountEntryRestricted: false,
	accountEntryRestrictedObjectFieldId: '',
	accountEntryRestrictedObjectFieldName: '',
	actions: {
		delete: {
			href: '',
			method: 'DELETE',
		},
		get: {
			href: '',
			method: 'GET',
		},
		update: {
			href: '',
			method: 'PATCH',
		},
	},
	active: true,
	dateCreated: '',
	dateModified: '',
	defaultLanguageId: 'en-US',
	enableCategorization: true,
	enableComments: true,
	enableFriendlyURLCustomization: true,
	enableIndexSearch: true,
	enableLocalization: true,
	enableObjectEntryDraft: true,
	enableObjectEntryHistory: true,
	enableObjectEntrySchedule: true,
	enableObjectEntrySubscription: true,
	externalReferenceCode: '',
	friendlyURLSeparator: '',
	id: 1,
	label: {
		'en-US': 'Object Definition',
	},
	modifiable: true,
	name: 'Object Definition',
	objectActions: [],
	objectFields: [
		{
			businessType: 'Text',
			defaultValue: '',
			externalReferenceCode: '',
			id: 1,
			indexed: true,
			indexedAsKeyword: false,
			label: {en_US: 'Field'},
			listTypeDefinitionId: 0,
			name: 'field',
			objectFieldSettings: [],
			required: false,
			system: false,
			type: 'String',
		},
	],
	objectFolderExternalReferenceCode: '',
	objectLayouts: [],
	objectRelationships: [],
	objectViews: [],
	panelCategoryKey: '',
	pluralLabel: {
		'en-US': '',
	},
	portlet: true,
	restContextPath: '',
	rootObjectDefinitionExternalReferenceCode: '',
	scope: 'company',
	status: {
		code: 0,
		label: 'active',
		label_i18n: 'Active',
	},
	storageType: 'default',
	system: false,
	titleObjectFieldId: '',
	titleObjectFieldName: '',
};
