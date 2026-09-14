/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Key} from 'react';

import {JSONSchema} from '../../types';
import {FieldTreeItem} from './types';

const EXCLUDED_FIELD_NAMES = new Set([
	'actions',
	'x-class-name',
	'x-schema-name',
]);

const LOCALIZED_FIELD_NAME_SUFFIX = '_i18n';

const RESTRICT_FIELDS_SEPARATOR = ',';

export function buildFieldTree(
	schema: JSONSchema | undefined
): FieldTreeItem[] {
	const itemsSchema =
		schema?.type === 'array' ? schema : schema?.properties?.items;

	return buildFieldTreeItems(
		itemsSchema?.type === 'array' ? itemsSchema.items : schema
	);
}

export function getExpandedKeys(restrictFields: string | undefined): Set<Key> {
	return new Set(
		fromRestrictFields(restrictFields).flatMap((restrictedFieldName) => {
			const parts = restrictedFieldName.split('.');

			return parts
				.slice(0, -1)
				.map((_, index) => parts.slice(0, index + 1).join('.'));
		})
	);
}

export function getSelectedKeys(
	tree: FieldTreeItem[],
	restrictFields: string | undefined
): Set<Key> {
	return new Set(
		getSelectedFieldIds(tree, new Set(fromRestrictFields(restrictFields)))
	);
}

export function toRestrictFields(
	tree: FieldTreeItem[],
	selectedKeys: Set<Key>
): string {
	return getRestrictedFieldIds(tree, selectedKeys).join(
		RESTRICT_FIELDS_SEPARATOR
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

function fromRestrictFields(restrictFields: string | undefined): string[] {
	return (restrictFields ?? '')
		.split(RESTRICT_FIELDS_SEPARATOR)
		.filter(Boolean);
}

function getChildSchema(schema: JSONSchema): JSONSchema | undefined {
	if (schema.type === 'array') {
		return schema.items?.properties ? schema.items : undefined;
	}

	return schema.properties ? schema : undefined;
}

function getFieldIds(tree: FieldTreeItem[]): string[] {
	return tree.flatMap((item) => [
		item.id,
		...getFieldIds(item.children ?? []),
	]);
}

function getRestrictedFieldIds(
	tree: FieldTreeItem[],
	selectedKeys: Set<Key>
): string[] {
	return tree.flatMap((item) => {
		if (selectedKeys.has(item.id)) {
			return [item.id];
		}

		return getRestrictedFieldIds(item.children ?? [], selectedKeys);
	});
}

function getSelectedFieldIds(
	tree: FieldTreeItem[],
	restrictedFieldNames: Set<string>
): string[] {
	return tree.flatMap((item) =>
		restrictedFieldNames.has(item.id)
			? getFieldIds([item])
			: getSelectedFieldIds(item.children ?? [], restrictedFieldNames)
	);
}
