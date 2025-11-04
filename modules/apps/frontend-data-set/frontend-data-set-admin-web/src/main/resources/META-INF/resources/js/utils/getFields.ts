/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FDS_ARRAY_FIELD_NAME_DELIMITER,
	FDS_ARRAY_FIELD_NAME_PARENT_SUFFIX,
	FDS_NESTED_FIELD_NAME_DELIMITER,
	FDS_NESTED_FIELD_NAME_PARENT_SUFFIX,
} from '@liferay/frontend-data-set-web';

import openDefaultFailureToast from './openDefaultFailureToast';
import {
	EFieldFormat,
	EFieldType,
	IField,
	IFilterable,
	IProperties,
	ISchemas,
} from './types';

export const BLACKLISTED_FIELDS = [
	'actions',
	'scopeKey',
	'x-class-name',
	'x-schema-name',
];

const LOCALIZABLE_PROPERTY_SUFFIX = '_i18n';

const validSchemaPropertyFilter = (propertyKey: string) => {
	return (
		!BLACKLISTED_FIELDS.includes(propertyKey) &&
		!propertyKey.includes(LOCALIZABLE_PROPERTY_SUFFIX)
	);
};

function getValidFields({
	contextPath,
	schemaName,
	schemas,
	visitedFields,
}: {
	contextPath: string;
	schemaName: string;
	schemas: ISchemas;
	visitedFields: string[];
}): Array<IField> {
	const fields: Array<IField> = [];

	const properties: IProperties = schemas[schemaName]?.properties;

	if (!properties) {
		return fields;
	}

	Object.keys(properties)
		.filter(validSchemaPropertyFilter)
		.map((propertyKey) => {
			const propertyValue = properties[propertyKey];

			const type = propertyValue.type;
			contextPath = contextPath.replace(/\*/g, '');
			const field: IField = {
				format: propertyValue.format,
				label: propertyKey,
				name: `${contextPath}${propertyKey}`,
				type,
			};

			let targetSchemaName;

			if (propertyValue.items?.$ref) {
				field.name = `${field.name}${FDS_ARRAY_FIELD_NAME_PARENT_SUFFIX}`;
				field.type = type ? type : 'array';
				targetSchemaName = propertyValue.items.$ref.replace(
					/^.*\//,
					''
				);
			}
			else if (propertyValue.$ref) {
				field.name = `${field.name}${FDS_NESTED_FIELD_NAME_PARENT_SUFFIX}`;
				field.type = type ? type : 'object';
				targetSchemaName = propertyValue.$ref.replace(/^.*\//, '');
			}
			else if (propertyValue['x-parent-map'] === 'properties') {
				const schemaNames = Object.keys(schemas);
				const parentSchemaName = schemaNames.find((schemaName) => {
					return (
						schemaName.toLowerCase() ===
						propertyKey.toLocaleLowerCase()
					);
				});

				if (parentSchemaName) {
					field.name = `${field.name}${FDS_NESTED_FIELD_NAME_PARENT_SUFFIX}`;
					field.type = schemas[parentSchemaName]?.type || 'object';
					targetSchemaName = parentSchemaName;
				}
			}

			field.sortable =
				field.type !== 'object' &&
				field.type !== 'array' &&
				!contextPath.includes(FDS_NESTED_FIELD_NAME_DELIMITER) &&
				!contextPath.includes(FDS_ARRAY_FIELD_NAME_DELIMITER);

			if (targetSchemaName && !visitedFields.includes(targetSchemaName)) {
				field.children = getValidFields({
					contextPath: field.name,
					schemaName: targetSchemaName,
					schemas,
					visitedFields: [...visitedFields, targetSchemaName],
				});
			}

			fields.push(field);
		});

	return fields;
}

export default async function getFields({
	restSchema,
	schemas,
}: {
	restSchema: string;
	schemas: ISchemas;
}) {
	return getValidFields({
		contextPath: '',
		schemaName: restSchema,
		schemas,
		visitedFields: [],
	});
}

async function getFilterableFields({
	restSchema,
	schemas,
}: {
	restSchema: string;
	schemas: ISchemas;
}): Promise<IField[]> {
	if (!schemas[restSchema]['x-filterable']) {
		openDefaultFailureToast();

		return [];
	}

	const filterablePaths: IFilterable = schemas[restSchema]['x-filterable'];
	const filterableItemList = Object.keys(filterablePaths);

	if (!filterableItemList) {
		return [];
	}

	const filterableFields: Array<IField> = [];

	filterableItemList.forEach((item) => {
		const type = filterablePaths[item].type;
		const entityFieldType =
			type === EFieldType.ARRAY ? filterablePaths[item].items : type;

		const field: IField = {
			entityFieldType,
			entityFieldTypeCollection: false,
			format: type,
			label: item,
			name: item,
			type,
		};

		if (type === EFieldType.ARRAY) {
			field.entityFieldTypeCollection = true;
		}

		if (field.type === EFieldFormat.F_DATE_TIME) {
			field.entityFieldType = EFieldType.STRING;
		}

		filterableFields.push(field);
	});

	return filterableFields;
}

export {getValidFields, getFilterableFields};
