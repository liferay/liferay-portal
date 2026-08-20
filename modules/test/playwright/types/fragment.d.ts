/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type FragmentCollection = {
	fragmentCollectionId: string;
	groupId: string;
};

type FragmentConfiguration = {
	fieldSets: FragmentConfigurationFieldSet[];
};

type FragmentConfigurationField = {
	dataType?: string;
	defaultValue?: boolean | string;
	label: string;
	localizable?: boolean;
	name: string;
	type:
		| 'itemSelector'
		| 'checkbox'
		| 'collectionSelector'
		| 'select'
		| 'text'
		| 'url';
	typeOptions?: {
		enableSelectTemplate?: boolean;
		itemSubtype?: string;
		itemType?: string;
		max?: number;
		min?: number;
		numberOfItems?: number;
		placeholder?: string;
		type?: string;
		validValues?: {
			label?: string;
			value: string;
		}[];
		validation?: {
			max?: number;
			maxLength?: number;
			min?: number;
			minLength?: number;
			required?: boolean;
			type?: string;
		};
	};
};

type FragmentTypeOptions = {
	fieldTypes: string[];
};

type FragmentConfigurationFieldSet = {
	fields: FragmentConfigurationField[];
	label?: string;
};

type FragmentEntry = {
	fragmentEntryId: string;
	groupId: string;
};

type FragmentEntryType = 'component' | 'input';
