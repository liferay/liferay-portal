/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {renderHook} from '@testing-library/react';
import React from 'react';

import {useMapKeyboardNav} from '../../../src/main/resources/META-INF/resources/js/map_chart/hooks/useMapKeyboardNav';

const VALID_INDEXES = [0, 2, 3];

function createKeyDownEvent(
	key: string
): React.KeyboardEvent & {preventDefault: jest.Mock} {
	return {
		key,
		preventDefault: jest.fn(),
	} as unknown as React.KeyboardEvent & {preventDefault: jest.Mock};
}

describe('useMapKeyboardNav', () => {
	it('moves focus to the next valid index on ArrowRight', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('ArrowRight'), 0);

		expect(focus).toHaveBeenCalledWith(2);
	});

	it('moves focus to the next valid index on ArrowDown', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('ArrowDown'), 2);

		expect(focus).toHaveBeenCalledWith(3);
	});

	it('wraps to the first valid index on ArrowRight from the last one', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('ArrowRight'), 3);

		expect(focus).toHaveBeenCalledWith(0);
	});

	it('moves focus to the previous valid index on ArrowLeft', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('ArrowLeft'), 2);

		expect(focus).toHaveBeenCalledWith(0);
	});

	it('wraps to the last valid index on ArrowLeft from the first one', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('ArrowLeft'), 0);

		expect(focus).toHaveBeenCalledWith(3);
	});

	it('jumps to the first valid index on Home', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('Home'), 3);

		expect(focus).toHaveBeenCalledWith(0);
	});

	it('jumps to the last valid index on End', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('End'), 0);

		expect(focus).toHaveBeenCalledWith(3);
	});

	it('ignores keys that are not navigation keys', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('Enter'), 0);

		expect(focus).not.toHaveBeenCalled();
	});

	it('does nothing when the current index is not a valid index', () => {
		const focus = jest.fn();
		const {result} = renderHook(() =>
			useMapKeyboardNav(VALID_INDEXES, focus)
		);

		result.current(createKeyDownEvent('ArrowRight'), 1);

		expect(focus).not.toHaveBeenCalled();
	});

	it('does nothing when there are no valid indexes', () => {
		const focus = jest.fn();
		const {result} = renderHook(() => useMapKeyboardNav([], focus));

		result.current(createKeyDownEvent('ArrowRight'), 0);

		expect(focus).not.toHaveBeenCalled();
	});
});
