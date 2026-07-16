/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	AudiencesCriteria,
	CriteriaNode,
	Group,
	SerializedGroup,
} from '../../../src/main/resources/META-INF/resources/js/types';
import {addGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/addGroup';
import {addRule} from '../../../src/main/resources/META-INF/resources/js/util/tree/addRule';
import {canGroupNode} from '../../../src/main/resources/META-INF/resources/js/util/tree/canGroupNode';
import {createGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/createGroup';
import {createRule} from '../../../src/main/resources/META-INF/resources/js/util/tree/createRule';
import {deleteEmptyGroups} from '../../../src/main/resources/META-INF/resources/js/util/tree/deleteEmptyGroups';
import {deleteRule} from '../../../src/main/resources/META-INF/resources/js/util/tree/deleteRule';
import {duplicateRule} from '../../../src/main/resources/META-INF/resources/js/util/tree/duplicateRule';
import {isGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/isGroup';
import {moveGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/moveGroup';
import {moveRule} from '../../../src/main/resources/META-INF/resources/js/util/tree/moveRule';
import {parseRootGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/parseRootGroup';
import {reorderGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/reorderGroup';
import {serializeGroup} from '../../../src/main/resources/META-INF/resources/js/util/tree/serializeGroup';
import {setConjunction} from '../../../src/main/resources/META-INF/resources/js/util/tree/setConjunction';
import {unwrapRedundantGroups} from '../../../src/main/resources/META-INF/resources/js/util/tree/unwrapRedundantGroups';
import {updateRule} from '../../../src/main/resources/META-INF/resources/js/util/tree/updateRule';

function getNode(root: Group, path: number[]): CriteriaNode | undefined {
	let node: CriteriaNode = root;

	for (const index of path) {
		if (!isGroup(node)) {
			return undefined;
		}

		node = node.items[index];

		if (!node) {
			return undefined;
		}
	}

	return node;
}

const AGE: AudiencesCriteria = {
	icon: 'user',
	inputType: 'text',
	key: 'age',
	label: 'Age',
	options: [],
	type: 'number',
};

const COUNTRY: AudiencesCriteria = {
	icon: 'globe',
	inputType: 'text',
	key: 'country',
	label: 'Country',
	options: [],
	type: 'string',
};

const NESTED_TREE: SerializedGroup = {
	conjunction: 'OR',
	rules: [
		{attribute: 'age', operator: 'gt', value: '18'},
		{
			conjunction: 'AND',
			rules: [
				{attribute: 'country', operator: 'eq', value: 'US'},
				{
					conjunction: 'OR',
					rules: [{attribute: 'age', operator: 'lt', value: '65'}],
				},
			],
		},
	],
};

describe('tree', () => {
	describe('parse and serialize', () => {
		it('round-trips a nested tree', () => {
			expect(serializeGroup(parseRootGroup(NESTED_TREE))).toEqual(
				NESTED_TREE
			);
		});

		it('assigns ids and preserves nested groups on parse', () => {
			const root = parseRootGroup(NESTED_TREE);

			expect(root.id).toEqual(expect.stringMatching(/^group-/));
			expect(isGroup(root.items[1])).toBe(true);
			expect((root.items[1] as Group).items).toHaveLength(2);
			expect(getNode(root, [1, 1])).toMatchObject({conjunction: 'OR'});
		});

		it('returns an empty AND group for missing criteria', () => {
			const root = parseRootGroup();

			expect(root.conjunction).toBe('AND');
			expect(root.items).toHaveLength(0);
		});

		it('drops blank-attribute rules at any depth', () => {
			const root = parseRootGroup({
				conjunction: 'AND',
				rules: [
					{attribute: 'age', operator: 'gt', value: '18'},
					{
						conjunction: 'OR',
						rules: [
							{attribute: '', operator: '', value: ''},
							{
								attribute: 'country',
								operator: 'eq',
								value: 'US',
							},
						],
					},
				],
			});

			expect(root.items).toHaveLength(2);

			const nested = root.items[1] as Group;

			expect(nested.items).toHaveLength(1);
			expect(nested.items[0]).toMatchObject({attribute: 'country'});
		});
	});

	describe('mutations', () => {
		it('adds a rule to the root and to a nested group', () => {
			let root = parseRootGroup(NESTED_TREE);

			root = addRule(root, [], AGE);
			root = addRule(root, [1], COUNTRY, 0);

			expect(root.items).toHaveLength(3);
			expect(getNode(root, [2])).toMatchObject({attribute: 'age'});
			expect(getNode(root, [1, 0])).toMatchObject({
				attribute: 'country',
			});
			expect((root.items[1] as Group).items).toHaveLength(3);
		});

		it('deletes and updates a nested rule', () => {
			let root = parseRootGroup(NESTED_TREE);

			root = updateRule(root, [1, 0], {
				attribute: 'country',
				id: 'rule-x',
				operator: 'not_eq',
				value: 'CA',
			});
			root = deleteRule(root, [0]);

			expect(root.items).toHaveLength(1);
			expect(getNode(root, [0, 0])).toMatchObject({
				operator: 'not_eq',
				value: 'CA',
			});
		});

		it('duplicates a group with fresh ids', () => {
			const root = parseRootGroup(NESTED_TREE);
			const duplicated = duplicateRule(root, [1]);

			const original = duplicated.items[1] as Group;
			const clone = duplicated.items[2] as Group;

			expect(duplicated.items).toHaveLength(3);
			expect(clone.id).not.toBe(original.id);
			expect(clone.items[0].id).not.toBe(original.items[0].id);
			expect(serializeGroup(clone)).toEqual(serializeGroup(original));
		});

		it('changes a nested group conjunction and reorders items', () => {
			let root = parseRootGroup(NESTED_TREE);

			root = setConjunction(root, [1], 'OR');
			root = reorderGroup(root, [], [root.items[1], root.items[0]]);

			expect((root.items[0] as Group).conjunction).toBe('OR');
			expect(getNode(root, [1])).toMatchObject({attribute: 'age'});
		});

		it('groups the target rule and a new rule together', () => {
			const root = parseRootGroup(NESTED_TREE);
			const targetId = root.items[0].id;

			const grouped = addGroup(root, targetId, COUNTRY);

			expect(grouped.items).toHaveLength(2);

			const newGroup = grouped.items[0] as Group;

			expect(isGroup(newGroup)).toBe(true);
			expect(newGroup.items).toHaveLength(2);
			expect(newGroup.items[0].id).toBe(targetId);
			expect(newGroup.items[1]).toMatchObject({attribute: 'country'});
		});

		it('adds a sibling instead of a redundant group for the only rule', () => {
			const ageRule = createRule(AGE);
			const root = createGroup('OR', [ageRule]);

			const result = addGroup(root, ageRule.id, COUNTRY);

			expect(result.conjunction).toBe('OR');
			expect(result.items).toHaveLength(2);
			expect(result.items.every((node) => !isGroup(node))).toBe(true);
			expect(result.items[0].id).toBe(ageRule.id);
			expect(result.items[1]).toMatchObject({attribute: 'country'});
		});

		it('deletes a group once its last rule is deleted', () => {
			const ageRule = createRule(AGE);
			let root = createGroup('AND', [
				ageRule,
				createGroup('OR', [createRule(COUNTRY)]),
			]);

			root = deleteEmptyGroups(deleteRule(root, [1, 0]));

			expect(root.items).toHaveLength(1);
			expect(root.items[0].id).toBe(ageRule.id);
		});
	});

	describe('unwrapRedundantGroups', () => {
		it('unwraps a redundant group and adopts its conjunction', () => {
			const root = createGroup('AND', [
				createGroup('OR', [createRule(AGE), createRule(COUNTRY)]),
			]);

			const collapsed = unwrapRedundantGroups(root);

			expect(collapsed.conjunction).toBe('OR');
			expect(collapsed.items).toHaveLength(2);
			expect(collapsed.items.every((node) => !isGroup(node))).toBe(true);
		});

		it('keeps a multi-rule group that has siblings', () => {
			const root = createGroup('AND', [
				createRule(AGE),
				createGroup('OR', [createRule(COUNTRY), createRule(AGE)]),
			]);

			const collapsed = unwrapRedundantGroups(root);

			expect(collapsed.conjunction).toBe('AND');
			expect(collapsed.items).toHaveLength(2);
			expect(isGroup(collapsed.items[1])).toBe(true);
		});

		it('dissolves a group left with a single rule', () => {
			const ageRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const root = createGroup('AND', [
				ageRule,
				createGroup('OR', [countryRule]),
			]);

			const unwrapped = unwrapRedundantGroups(root);

			expect(unwrapped.items).toHaveLength(2);
			expect(unwrapped.items.every((node) => !isGroup(node))).toBe(true);
			expect(unwrapped.items[1].id).toBe(countryRule.id);
		});

		it('unwraps nested redundant groups in one pass', () => {
			const root = createGroup('AND', [
				createGroup('OR', [createGroup('AND', [createRule(AGE)])]),
			]);

			const collapsed = unwrapRedundantGroups(root);

			expect(collapsed.items).toHaveLength(1);
			expect(collapsed.items[0]).toMatchObject({attribute: 'age'});
		});
	});

	describe('moveRule', () => {
		it('deletes the source group once its last rule moves out', () => {
			const ageRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const nested = createGroup('OR', [countryRule]);
			const root = createGroup('AND', [ageRule, nested]);

			const moved = moveRule(root, countryRule.id, root.id, 2);

			expect(moved.items).toHaveLength(2);
			expect(moved.items.map((node) => node.id)).toEqual([
				ageRule.id,
				countryRule.id,
			]);
		});

		it('moves a rule into another group', () => {
			const ageRule = createRule(AGE);
			const keepRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const nested = createGroup('OR', [countryRule]);
			const root = createGroup('AND', [ageRule, keepRule, nested]);

			const moved = moveRule(root, ageRule.id, nested.id, 1);

			expect(moved.items).toHaveLength(2);

			const movedNested = moved.items[1] as Group;

			expect(movedNested.items.map((node) => node.id)).toEqual([
				countryRule.id,
				ageRule.id,
			]);
		});

		it('reorders downward within the same group', () => {
			const ageRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const lastRule = createRule(AGE);
			const root = createGroup('AND', [ageRule, countryRule, lastRule]);

			const moved = moveRule(root, ageRule.id, root.id, 3);

			expect(moved.items.map((node) => node.id)).toEqual([
				countryRule.id,
				lastRule.id,
				ageRule.id,
			]);
		});

		it('reorders upward within the same group', () => {
			const ageRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const lastRule = createRule(AGE);
			const root = createGroup('AND', [ageRule, countryRule, lastRule]);

			const moved = moveRule(root, lastRule.id, root.id, 0);

			expect(moved.items.map((node) => node.id)).toEqual([
				lastRule.id,
				ageRule.id,
				countryRule.id,
			]);
		});

		it('returns the tree unchanged for an unknown node', () => {
			const root = createGroup('AND', [createRule(AGE)]);

			expect(moveRule(root, 'missing', root.id, 0)).toBe(root);
		});
	});

	describe('moveGroup', () => {
		it('groups the target rule and the moved rule together', () => {
			const ageRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const keepRule = createRule(AGE);
			const root = createGroup('AND', [ageRule, countryRule, keepRule]);

			const grouped = moveGroup(root, ageRule.id, countryRule.id);

			expect(grouped.items).toHaveLength(2);

			const newGroup = grouped.items[0] as Group;

			expect(isGroup(newGroup)).toBe(true);
			expect(newGroup.items.map((node) => node.id)).toEqual([
				countryRule.id,
				ageRule.id,
			]);
			expect(grouped.items[1].id).toBe(keepRule.id);
		});

		it('keeps the parent conjunction when grouping wraps the whole parent', () => {
			const ageRule = createRule(AGE);
			const countryRule = createRule(COUNTRY);
			const root = createGroup('OR', [ageRule, countryRule]);

			const result = moveGroup(root, ageRule.id, countryRule.id);

			expect(result.conjunction).toBe('OR');
			expect(result.items).toHaveLength(2);
			expect(result.items.every((node) => !isGroup(node))).toBe(true);
			expect(result.items.map((node) => node.id).sort()).toEqual(
				[ageRule.id, countryRule.id].sort()
			);
		});

		it('returns the tree unchanged when dropped onto itself', () => {
			const ageRule = createRule(AGE);
			const root = createGroup('AND', [ageRule]);

			expect(moveGroup(root, ageRule.id, ageRule.id)).toBe(root);
		});
	});

	describe('depth limits', () => {
		it('blocks grouping a node once it would exceed the max depth', () => {
			expect(canGroupNode([0])).toBe(true);
			expect(canGroupNode([1, 0])).toBe(true);
			expect(canGroupNode([1, 1, 0])).toBe(false);
		});
	});

	describe('createGroup', () => {
		it('defaults to an empty AND group', () => {
			const group = createGroup();

			expect(group.conjunction).toBe('AND');
			expect(group.items).toHaveLength(0);
			expect(group.id).toEqual(expect.stringMatching(/^group-/));
		});
	});
});
