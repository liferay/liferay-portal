/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	buildFieldTree,
	filterFieldTree,
	getExpandedKeys,
	getSelectedKeys,
	toRestrictFields,
} from '../../../src/main/resources/META-INF/resources/js/profiles/restrict_fields/utils';
import {mockPageTool} from '../../mocks/mockPageTool';
import {mockTool} from '../../mocks/mockTool';

import type {FieldTreeItem} from '../../../src/main/resources/META-INF/resources/js/profiles/restrict_fields/types';

function flatten(items: FieldTreeItem[]): string[] {
	return items.flatMap((item) => [item.id, ...flatten(item.children ?? [])]);
}

describe('restrict fields utils', () => {
	const tree = buildFieldTree(mockTool.outputSchema);

	describe('buildFieldTree', () => {
		it('lists the top level fields sorted by name', () => {
			expect(tree.map((item) => item.name)).toEqual([
				'auditEvents',
				'description',
				'embeddedTaxonomyCategory',
				'keywords',
				'modifiedBy',
				'name',
				'promptStatus',
				'taxonomyCategoryBriefs',
			]);
		});

		it('drops write-only, localized and marker properties', () => {
			const ids = flatten(tree);

			expect(ids).not.toContain('taxonomyCategoryIds');
			expect(ids).not.toContain('friendlyUrlPath_i18n');
			expect(ids).not.toContain('promptStatus.name_i18n');
			expect(ids).not.toContain('actions');
			expect(ids).not.toContain('x-schema-name');
		});

		it('nests object properties under a dotted path', () => {
			expect(flatten(tree)).toEqual(
				expect.arrayContaining([
					'modifiedBy.id',
					'modifiedBy.name',
					'promptStatus.key',
				])
			);
		});

		it('nests the item properties of an array of objects under the array name', () => {
			expect(flatten(tree)).toEqual(
				expect.arrayContaining([
					'taxonomyCategoryBriefs.scope.key',
					'taxonomyCategoryBriefs.taxonomyCategoryName',
					'auditEvents.creator.name',
					'modifiedBy.userGroupBriefs.name',
				])
			);
		});

		it('keeps objects without properties and scalar arrays as leaves', () => {
			const leaves = tree.filter((item) => !item.children);

			expect(leaves.map((item) => item.id)).toEqual([
				'description',
				'embeddedTaxonomyCategory',
				'keywords',
				'name',
			]);
		});

		it('counts the fields under every field, itself included', () => {
			const modifiedBy = tree.find((item) => item.id === 'modifiedBy');

			expect(modifiedBy).toMatchObject({fieldCount: 6});
			expect(modifiedBy?.children).toContainEqual(
				expect.objectContaining({
					fieldCount: 3,
					id: 'modifiedBy.userGroupBriefs',
				})
			);
			expect(
				tree.find((item) => item.id === 'description')
			).toMatchObject({fieldCount: 1});
		});

		it('lists the item fields of a tool returning a page', () => {
			expect(buildFieldTree(mockPageTool.outputSchema)).toEqual(tree);
		});

		it('lists the item fields of a tool returning an array', () => {
			expect(
				buildFieldTree({items: mockTool.outputSchema, type: 'array'})
			).toEqual(tree);
		});

		it('returns no fields when the tool has no output schema', () => {
			expect(buildFieldTree(undefined)).toEqual([]);
		});
	});

	describe('toRestrictFields', () => {
		it('stores a selected parent instead of its descendants', () => {
			expect(
				toRestrictFields(
					tree,
					new Set(['modifiedBy', 'modifiedBy.id', 'modifiedBy.name'])
				)
			).toBe('modifiedBy');
		});

		it('joins selected leaves whose parent is not selected with commas', () => {
			expect(
				toRestrictFields(
					tree,
					new Set(['description', 'taxonomyCategoryBriefs.scope.key'])
				)
			).toBe('description,taxonomyCategoryBriefs.scope.key');
		});

		it('stores an empty string when nothing is selected', () => {
			expect(toRestrictFields(tree, new Set())).toBe('');
		});
	});

	describe('getExpandedKeys', () => {
		it('expands every ancestor of a restricted field but not the field', () => {
			expect([
				...getExpandedKeys(
					'description,modifiedBy.userGroupBriefs.name,promptStatus.key'
				),
			]).toEqual([
				'modifiedBy',
				'modifiedBy.userGroupBriefs',
				'promptStatus',
			]);
		});

		it('expands nothing without restricted fields', () => {
			expect(getExpandedKeys(undefined).size).toBe(0);
		});
	});

	describe('getSelectedKeys', () => {
		it('selects each restricted field with its whole subtree', () => {
			expect([
				...getSelectedKeys(
					tree,
					'description,modifiedBy.userGroupBriefs'
				),
			]).toEqual([
				'description',
				'modifiedBy.userGroupBriefs',
				'modifiedBy.userGroupBriefs.id',
				'modifiedBy.userGroupBriefs.name',
			]);
		});

		it('ignores restricted fields missing from the tree', () => {
			expect([...getSelectedKeys(tree, 'name,removedField')]).toEqual([
				'name',
			]);
		});

		it('selects nothing when the profile tool has no restricted fields', () => {
			expect(getSelectedKeys(tree, undefined).size).toBe(0);
			expect(getSelectedKeys(tree, '').size).toBe(0);
		});
	});

	describe('filterFieldTree', () => {
		it('keeps a deep match with its ancestors and expands the path to it', () => {
			const {expandedKeys, matchCount, visibleKeys} = filterFieldTree(
				tree,
				'label'
			);

			expect(matchCount).toBe(1);

			expect(visibleKeys).toEqual(
				new Set([
					'taxonomyCategoryBriefs',
					'taxonomyCategoryBriefs.scope',
					'taxonomyCategoryBriefs.scope.label',
				])
			);
			expect(expandedKeys).toEqual(
				new Set([
					'taxonomyCategoryBriefs',
					'taxonomyCategoryBriefs.scope',
				])
			);
		});

		it('does not drag the children of a matching parent', () => {
			const {expandedKeys, visibleKeys} = filterFieldTree(
				tree,
				'modifiedBy'
			);

			expect(visibleKeys).toEqual(new Set(['modifiedBy']));
			expect(expandedKeys.size).toBe(0);
		});

		it('matches the field name case insensitively at any position', () => {
			const {matchCount, visibleKeys} = filterFieldTree(tree, 'NAME');

			expect(matchCount).toBe(6);

			expect(visibleKeys).toContain('name');
			expect(visibleKeys).toContain('auditEvents.creator.name');
			expect(visibleKeys).toContain(
				'taxonomyCategoryBriefs.taxonomyCategoryName'
			);
			expect(visibleKeys).not.toContain('description');
		});

		it('treats regular expression characters as plain text', () => {
			expect(filterFieldTree(tree, '.').visibleKeys.size).toBe(0);
			expect(filterFieldTree(tree, '(').visibleKeys.size).toBe(0);
		});

		it('ignores the whitespace around the query', () => {
			expect(filterFieldTree(tree, '  label  ')).toEqual(
				filterFieldTree(tree, 'label')
			);
		});

		it('hides and expands nothing without a query', () => {
			const {expandedKeys, matchCount, visibleKeys} = filterFieldTree(
				tree,
				'   '
			);

			expect(visibleKeys.size).toBe(0);
			expect(expandedKeys.size).toBe(0);
			expect(matchCount).toBe(0);
		});
	});
});
