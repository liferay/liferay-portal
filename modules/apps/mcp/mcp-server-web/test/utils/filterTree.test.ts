/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import filterTree from '../../src/main/resources/META-INF/resources/js/utils/filterTree';

type Node = {
	children?: Node[];
	id: string;
	name: string;
};

const tree: Node[] = [
	{
		children: [
			{id: 'site.name', name: 'name'},
			{
				children: [{id: 'site.owner.name', name: 'name'}],
				id: 'site.owner',
				name: 'owner',
			},
		],
		id: 'site',
		name: 'site',
	},
	{id: 'description', name: 'description'},
];

describe('filterTree', () => {
	it('keeps a deep match with its ancestors and expands the path to it', () => {
		const {expandedKeys, matchCount, visibleKeys} = filterTree(
			tree,
			(item) => item.id === 'site.owner.name'
		);

		expect(matchCount).toBe(1);
		expect(visibleKeys).toEqual(
			new Set(['site', 'site.owner', 'site.owner.name'])
		);
		expect(expandedKeys).toEqual(new Set(['site', 'site.owner']));
	});

	it('counts every matching node, parents included', () => {
		const {expandedKeys, matchCount, visibleKeys} = filterTree(
			tree,
			(item) => item.name === 'name'
		);

		expect(matchCount).toBe(2);
		expect(visibleKeys).toEqual(
			new Set(['site', 'site.name', 'site.owner', 'site.owner.name'])
		);
		expect(expandedKeys).toEqual(new Set(['site', 'site.owner']));
	});

	it('does not expand or drag the children of a matching parent', () => {
		const {expandedKeys, visibleKeys} = filterTree(
			tree,
			(item) => item.id === 'site'
		);

		expect(visibleKeys).toEqual(new Set(['site']));
		expect(expandedKeys.size).toBe(0);
	});

	it('leaves nothing visible when nothing matches', () => {
		const {expandedKeys, matchCount, visibleKeys} = filterTree(
			tree,
			() => false
		);

		expect(matchCount).toBe(0);
		expect(visibleKeys.size).toBe(0);
		expect(expandedKeys.size).toBe(0);
	});
});
