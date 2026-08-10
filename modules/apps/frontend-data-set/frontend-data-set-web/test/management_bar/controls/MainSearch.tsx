/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import MainSearch from '../../../src/main/resources/META-INF/resources/management_bar/controls/MainSearch';

const DEBOUNCE_DELAY = 300;

describe('MainSearch', () => {
	let onClear: jest.Mock;
	let onSearch: jest.Mock;
	let user: ReturnType<typeof userEvent.setup>;

	function renderMainSearch({
		apiURL = '/o/products',
		searchAsYouType = false,
	} = {}) {
		render(
			<FrontendDataSetContext.Provider
				value={{apiURL, onSearch, searchAsYouType} as any}
			>
				<MainSearch onClear={onClear} />
			</FrontendDataSetContext.Provider>
		);

		return screen.getByRole('searchbox');
	}

	function elapse(milliseconds: number) {
		act(() => {
			jest.advanceTimersByTime(milliseconds);
		});
	}

	beforeEach(() => {
		jest.useFakeTimers();

		onClear = jest.fn();
		onSearch = jest.fn();

		user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('searches once for a burst of keystrokes when search as you type is enabled', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc');

		expect(onSearch).not.toHaveBeenCalled();

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('does not search while typing when search as you type is disabled', async () => {
		const input = renderMainSearch();

		await user.type(input, 'abc');

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).not.toHaveBeenCalled();
	});

	it('searches on Enter when search as you type is disabled', async () => {
		const input = renderMainSearch();

		await user.type(input, 'abc{Enter}');

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('ignores Enter when search as you type is enabled', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc{Enter}');

		expect(onSearch).not.toHaveBeenCalled();

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('searches on the search button when search as you type is disabled', async () => {
		const input = renderMainSearch({searchAsYouType: false});

		await user.type(input, 'abc');
		await user.click(screen.getByRole('button', {name: 'search'}));

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('ignores the search button when search as you type is enabled', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc');
		await user.click(screen.getByRole('button', {name: 'search'}));

		expect(onSearch).not.toHaveBeenCalled();

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('drops the pending search when the input is cleared', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc');
		await user.clear(input);

		elapse(DEBOUNCE_DELAY);

		expect(onClear).toHaveBeenCalled();
		expect(onSearch).not.toHaveBeenCalled();
	});

	it('searches on every keystroke when the items are filtered client side', async () => {
		const input = renderMainSearch({apiURL: '', searchAsYouType: true});

		await user.type(input, 'ab');

		expect(onSearch).toHaveBeenCalledTimes(2);
		expect(onSearch).toHaveBeenLastCalledWith({query: 'ab'});
	});

	it('searches client side items on Enter when search as you type is disabled', async () => {
		const input = renderMainSearch({apiURL: '', searchAsYouType: false});

		await user.type(input, 'ab');

		expect(onSearch).not.toHaveBeenCalled();

		await user.type(input, '{Enter}');

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'ab'});
	});
});
