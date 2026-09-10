/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {buildFieldTree} from '../../../src/main/resources/META-INF/resources/js/profiles/restrict_fields/utils';
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
});
