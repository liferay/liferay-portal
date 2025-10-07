/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface Props {
	defaultLanguageId: Locale;
	fields: Field[];
	name: string;
}

interface Field {
	fieldType?:
		| 'document_library'
		| 'image'
		| 'journal_article'
		| 'select'
		| 'text';
	localizable?: boolean;
	name: string;
	options?: Options;
	repeatable?: boolean;
	required?: boolean;
}

export default function getDataStructureDefinition({
	defaultLanguageId,
	fields,
	name,
}: Props): DataDefinition {
	return {
		availableLanguageIds: [defaultLanguageId],
		dataDefinitionFields: fields.map(
			({
				fieldType = 'text',
				localizable = true,
				name: fieldName,
				options,
				repeatable = false,
				required = false,
			}) => {
				return {
					customProperties: {
						dataType: 'string',
						displayStyle: 'singleline',
						fieldReference: fieldName,
						options,
					},
					defaultValue: {},
					fieldType,
					indexType: 'keyword',
					label: {
						[defaultLanguageId]: fieldName,
					},
					localizable,
					name: fieldName,
					repeatable,
					required,
					showLabel: true,
				};
			}
		),
		defaultDataLayout: {
			dataLayoutPages: [
				{
					dataLayoutRows: fields.map((field) => {
						return {
							dataLayoutColumns: [
								{
									columnSize: 12,
									fieldNames: [field.name],
								},
							],
						};
					}),
					description: {
						[defaultLanguageId]: '',
					},
					title: {
						[defaultLanguageId]: '',
					},
				},
			],
			name: {
				[defaultLanguageId]: name,
			},
			paginationMode: 'single-page',
		},
		defaultLanguageId,
		id: '',
		name: {
			[defaultLanguageId]: name,
		},
	};
}
