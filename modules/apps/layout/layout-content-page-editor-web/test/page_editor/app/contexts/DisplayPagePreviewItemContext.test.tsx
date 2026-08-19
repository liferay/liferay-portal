/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react-hooks';
import React from 'react';

import {
	DisplayPagePreviewItemContextProvider,
	useDisplayPagePreviewItem,
	useDisplayPageRecentPreviewItemList,
	useSelectDisplayPagePreviewItem,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/DisplayPagePreviewItemContext';

function getHook() {
	const wrapper = ({children}: {children: React.ReactNode}) => (
		<DisplayPagePreviewItemContextProvider>
			{children}
		</DisplayPagePreviewItemContextProvider>
	);

	return renderHook(
		() => ({
			item: useDisplayPagePreviewItem(),
			recentItemList: useDisplayPageRecentPreviewItemList(),
			selectItem: useSelectDisplayPagePreviewItem(),
		}),
		{wrapper}
	);
}

describe('DisplayPagePreviewItemContext', () => {
	it('provides the latest selected item', () => {
		const hook = getHook();

		act(() => {
			hook.result.current.selectItem({data: {}, label: 'First'});
		});

		expect(hook.result.current.item).toEqual({data: {}, label: 'First'});
	});

	it('provides a list of recent items', () => {
		const hook = getHook();

		act(() => {
			hook.result.current.selectItem({data: {}, label: 'Vero'});
			hook.result.current.selectItem({data: {}, label: 'Sandro'});
		});

		expect(hook.result.current.recentItemList).toEqual([
			{data: {}, label: 'Sandro'},
			{data: {}, label: 'Vero'},
		]);
	});

	it('avoids having duplicated items', () => {
		const hook = getHook();

		act(() => {
			hook.result.current.selectItem({data: {}, label: 'First'});
			hook.result.current.selectItem({data: {}, label: 'Second'});
			hook.result.current.selectItem({data: {}, label: 'First'});
		});

		expect(hook.result.current.recentItemList).toEqual([
			{data: {}, label: 'Second'},
			{data: {}, label: 'First'},
		]);
	});

	it('maintains ordering when selecting existing item', () => {
		const hook = getHook();

		act(() => {
			hook.result.current.selectItem({data: {}, label: 'First'});
			hook.result.current.selectItem({data: {}, label: 'Second'});
			hook.result.current.selectItem({data: {}, label: 'Second'});
		});

		expect(hook.result.current.recentItemList).toEqual([
			{data: {}, label: 'Second'},
			{data: {}, label: 'First'},
		]);
	});

	it('limits the size of recent items to 100', () => {
		const hook = getHook();

		act(() => {
			for (let i = 0; i < 110; i++) {
				hook.result.current.selectItem({data: {}, label: `Item ${i}`});
			}
		});

		expect(hook.result.current.recentItemList.length).toBe(100);
	});
});
