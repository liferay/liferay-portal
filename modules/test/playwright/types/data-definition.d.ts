/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type Locale = Liferay.Language.Locale;

type DataDefinition = {
	availableLanguageIds: Locale[];
	dataDefinitionFields: DefinitionField[];
	defaultDataLayout: DataLayout;
	defaultLanguageId: Locale;
	id: string;
	name: {[keys: string]: string};
};

type DefinitionField = {
	customProperties: {
		dataType: 'string';
		displayStyle: 'singleline' | 'multiline';
		fieldReference: string;
		options?: Options;
	};
	defaultValue: {[keys: string]: string};
	fieldType:
		| 'document_library'
		| 'image'
		| 'journal_article'
		| 'select'
		| 'text';
	indexType: 'keyword' | 'text' | 'none';
	label: {[keys: string]: string};
	localizable: boolean;
	name: string;
	repeatable: boolean;
	required?: boolean;
	showLabel: boolean;
};

type DataLayoutRow = {
	dataLayoutColumns: [
		{
			columnSize: number;
			fieldNames: string[];
		},
	];
};

type DataLayout = {
	dataLayoutPages: [
		{
			dataLayoutRows: DataLayoutRow[];
			description: {[keys: string]: string};
			title: {[keys: string]: string};
		},
	];
	name: {[keys: string]: string};
	paginationMode: 'single-page';
};

type Option = {
	label: string;
	reference: string;
	value: string;
};

type Options = {
	[key: string]: Option[];
};
