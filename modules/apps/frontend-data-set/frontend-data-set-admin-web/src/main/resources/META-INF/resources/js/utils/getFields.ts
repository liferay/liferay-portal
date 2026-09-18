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

import {EFieldType, IField, IProperties, ISchemas} from './types';

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

			const reference =
				propertyValue.$ref ?? propertyValue.allOf?.[0]?.$ref;

			if (propertyValue.items?.$ref) {
				field.name = `${field.name}${FDS_ARRAY_FIELD_NAME_PARENT_SUFFIX}`;
				field.type = type ? type : 'array';
				targetSchemaName = propertyValue.items.$ref.replace(
					/^.*\//,
					''
				);
			}
			else if (reference) {
				field.name = `${field.name}${FDS_NESTED_FIELD_NAME_PARENT_SUFFIX}`;
				field.type = type ? type : 'object';
				targetSchemaName = reference.replace(/^.*\//, '');
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

export default function getFields({
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

function getFilterableFields({
	restSchema,
	schemas,
}: {
	restSchema: string;
	schemas: ISchemas;
}): IField[] {
	if (!schemas[restSchema]['x-filterable']) {
		return [];
	}

	const filterablePaths = schemas[restSchema]['x-filterable'];

	if (!filterablePaths) {
		return [];
	}

	const filterableItemList = Object.keys(filterablePaths);

	return filterableItemList.map((item) => {
		const type = filterablePaths[item].type;
		const entityFieldType =
			type === EFieldType.ARRAY
				? filterablePaths[item].items?.type
				: type;

		const field: IField = {
			entityFieldType,
			label: item,
			name: item,
			type,
		};

		if (type === EFieldType.ARRAY) {
			field.entityFieldType = `collection-${field.entityFieldType}` as
				| EFieldType.COLLECTION_INTEGER
				| EFieldType.COLLECTION_STRING;
		}

		if (field.type === EFieldType.DATE_TIME) {
			field.entityFieldType = EFieldType.STRING;
		}

		return field;
	});
}

export {getValidFields, getFilterableFields};
