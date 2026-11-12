/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import {useUnifiedSidebarExpandedKeys} from '../../src/main/resources/META-INF/resources/unified_sidebar/useUnifiedSidebarExpandedKeys';

describe('useUnifiedSidebarExpandedKeys', () => {
	const util = Liferay.Util;

	beforeEach(() => {
		jest.clearAllMocks();

		Liferay.Util = {
			...util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};
	});

	afterEach(() => {
		Liferay.Util = util;
	});

	it('updates the expanded keys before saving them', () => {
		(Liferay.Util.Session.set as jest.Mock).mockReturnValueOnce(
			new Promise(() => {})
		);

		const {result} = renderHook(() =>
			useUnifiedSidebarExpandedKeys({
				expandedKeys: ['content'],
				expandedKeysSessionKey: 'expandedKeysSessionKey',
				isFilterActive: false,
			})
		);

		act(() =>
			result.current.updateExpandedKeys(new Set(['content', 'design']))
		);

		expect(result.current.expandedKeys).toEqual(
			new Set(['content', 'design'])
		);
		expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
			'expandedKeysSessionKey',
			'content,design'
		);
	});

	it('does not save the keys collapsed while filtering', () => {
		const filterExpandedKeys = new Set<React.Key>(['content', 'design']);

		const {result} = renderHook(() =>
			useUnifiedSidebarExpandedKeys({
				expandedKeys: [],
				expandedKeysSessionKey: 'expandedKeysSessionKey',
				filterExpandedKeys,
				isFilterActive: true,
			})
		);

		expect(result.current.expandedKeys).toBe(filterExpandedKeys);

		act(() => result.current.updateExpandedKeys(new Set(['design'])));

		expect(result.current.expandedKeys).toEqual(new Set(['design']));
		expect(Liferay.Util.Session.set).not.toHaveBeenCalled();
	});
});
