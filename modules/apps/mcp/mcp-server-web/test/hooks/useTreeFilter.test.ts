/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';
import {Key} from 'react';

import useTreeFilter from '../../src/main/resources/META-INF/resources/js/hooks/useTreeFilter';
import filterTree from '../../src/main/resources/META-INF/resources/js/utils/filterTree';

type Node = {
	children?: Node[];
	id: string;
	name: string;
};

const items: Node[] = [
	{
		children: [
			{id: 'system/email', name: 'Email Address'},
			{id: 'system/phone', name: 'Phone Number'},
		],
		id: 'system',
		name: 'System',
	},
	{
		children: [{id: 'custom/codename', name: 'Project Codename'}],
		id: 'custom',
		name: 'Custom',
	},
];

function filter(tree: Node[], query: string) {
	const loweredQuery = query.trim().toLowerCase();

	if (!loweredQuery) {
		return {
			expandedKeys: new Set(tree.map((group) => group.id)),
			matchCount: 0,
			visibleKeys: new Set<string>(),
		};
	}

	return filterTree(
		tree,
		(item) =>
			!item.children && item.name.toLowerCase().includes(loweredQuery)
	);
}

function renderUseTreeFilter({
	expandedKeys = new Set<Key>(),
	restoreExpansion = false,
}: {expandedKeys?: Set<Key>; restoreExpansion?: boolean} = {}) {
	const onExpandedChange = jest.fn();

	const hook = renderHook(() =>
		useTreeFilter({
			expandedKeys,
			filter,
			items,
			onExpandedChange,
			restoreExpansion,
		})
	);

	return {...hook, onExpandedChange};
}

describe('useTreeFilter', () => {
	it('starts with an empty query, no matches and every row visible', () => {
		const {result} = renderUseTreeFilter();

		expect(result.current.query).toBe('');
		expect(result.current.trimmedQuery).toBe('');
		expect(result.current.matchCount).toBe(0);
		expect(result.current.isVisible(items[0].children![1])).toBe(true);
		expect(result.current.getRowClassName(items[1])).toBeUndefined();
	});

	it('keeps the raw query, trims it for matching and hides the rows that do not match', () => {
		const {result} = renderUseTreeFilter();

		act(() => {
			result.current.onSearch('  email ');
		});

		expect(result.current.query).toBe('  email ');
		expect(result.current.trimmedQuery).toBe('email');
		expect(result.current.matchCount).toBe(1);
		expect(result.current.isVisible(items[0])).toBe(true);
		expect(result.current.isVisible(items[0].children![0])).toBe(true);
		expect(result.current.getRowClassName(items[0].children![1])).toBe(
			'd-none'
		);
		expect(result.current.getRowClassName(items[1])).toBe('d-none');
	});

	it('expands the groups the filter resolves on every search', () => {
		const {onExpandedChange, result} = renderUseTreeFilter();

		act(() => {
			result.current.onSearch('codename');
		});

		expect(onExpandedChange).toHaveBeenLastCalledWith(new Set(['custom']));

		act(() => {
			result.current.onSearch('');
		});

		expect(onExpandedChange).toHaveBeenLastCalledWith(
			new Set(['system', 'custom'])
		);
	});

	it('restores the expansion from before the filter when asked to', () => {
		const expandedKeys = new Set<Key>(['system']);

		const {onExpandedChange, result} = renderUseTreeFilter({
			expandedKeys,
			restoreExpansion: true,
		});

		act(() => {
			result.current.onSearch('codename');
		});

		expect(onExpandedChange).toHaveBeenLastCalledWith(new Set(['custom']));

		act(() => {
			result.current.onSearch('phone');
		});

		act(() => {
			result.current.onSearch(' ');
		});

		expect(onExpandedChange).toHaveBeenLastCalledWith(expandedKeys);
		expect(result.current.trimmedQuery).toBe('');
	});
});
