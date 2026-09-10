/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {JSONSchema} from '../../types';
import {FieldTreeItem} from './types';

const EXCLUDED_FIELD_NAMES = new Set([
	'actions',
	'x-class-name',
	'x-schema-name',
]);

const LOCALIZED_FIELD_NAME_SUFFIX = '_i18n';

export function buildFieldTree(
	schema: JSONSchema | undefined
): FieldTreeItem[] {
	const itemsSchema =
		schema?.type === 'array' ? schema : schema?.properties?.items;

	return buildFieldTreeItems(
		itemsSchema?.type === 'array' ? itemsSchema.items : schema
	);
}

function buildFieldTreeItems(
	schema: JSONSchema | undefined,
	parentPath = ''
): FieldTreeItem[] {
	const properties = schema?.properties;

	if (!properties) {
		return [];
	}

	return Object.keys(properties)
		.filter(
			(name) =>
				!EXCLUDED_FIELD_NAMES.has(name) &&
				!name.endsWith(LOCALIZED_FIELD_NAME_SUFFIX) &&
				!properties[name].writeOnly
		)
		.sort()
		.map((name) => {
			const id = parentPath ? `${parentPath}.${name}` : name;

			const children = buildFieldTreeItems(
				getChildSchema(properties[name]),
				id
			);

			return children.length ? {children, id, name} : {id, name};
		});
}

function getChildSchema(schema: JSONSchema): JSONSchema | undefined {
	if (schema.type === 'array') {
		return schema.items?.properties ? schema.items : undefined;
	}

	return schema.properties ? schema : undefined;
}
