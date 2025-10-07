/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface Actions {
	create?: HTTPMethod;
	delete?: HTTPMethod;
	get?: HTTPMethod;
	permissions?: HTTPMethod;
	update?: HTTPMethod;
}

interface CreateObjectField {
	aggregationField?: string;
	aggregationFieldFunction?: string;
	aggregationFieldRelationship?: string;
	attachmentSource?: string;
	autoIncrementInitialValue?: string;
	formulaFieldOutput?: 'Decimal' | 'Integer';
	listTypeDefinitionName?: string;
	objectFieldBusinessType: string;
	objectFieldLabel: string;
}

interface DataObject {
	[K: string]: unknown;
}

type Direction = 'bottom' | 'left' | 'right' | 'top';

type ExcludesFilterOperator = {
	not: {
		in: string[] | number[];
	};
};

interface HTTPMethod {
	href: string;
	method: string;
}

type IncludesFilterOperator = {
	in: string[] | number[];
};

interface LabelNameObject {
	label: string;
	name: string;
}

interface ListTypeDefinition {
	actions: Actions;
	externalReferenceCode: string;
	id: number;
	key: string;
	listTypeEntries: ListTypeEntry[];
	name: string;
	name_i18n: LocalizedValue<string>;
	system: boolean;
}

interface ListTypeDefinitions {
	actions: Actions;
	items: ListTypeDefinition[];
}

interface ListTypeEntry {
	externalReferenceCode: string;
	id: number;
	key: string;
	name: string;
	name_i18n: LocalizedValue<string>;
}

type LocalizedValue<T> = Liferay.Language.LocalizedValue<T>;

interface NameValueObject {
	name: string;
	value: string;
}

interface ObjectEntry {

	// replace with model generated with object-rest-impl

	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	keywords: [];
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
	textField: string;
	[key: string]: any;
}

type ObjectFieldDateRangeFilterSettings = {
	[key: string]: string;
};

type ObjectFieldFilterSetting = {
	filterBy?: string;
	filterType?: string;
	json:
		| {
				[key: string]:
					| string
					| string[]
					| ObjectFieldDateRangeFilterSettings
					| undefined;
		  }
		| ExcludesFilterOperator
		| IncludesFilterOperator
		| string;
};

type ObjectRelationshipType = 'Many to Many' | 'One to Many';
