/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import {act} from 'react-dom/test-utils';

import {ItemSelectorUrl} from '../src/main/resources/META-INF/resources/js/index';

describe('ItemSelectorUrl', () => {
	afterEach(() => {
		jest.clearAllTimers();
		cleanup();
	});

	let itemSelectorUrl;
	beforeEach(() => {
		jest.useFakeTimers();
		itemSelectorUrl = render(<ItemSelectorUrl eventName="eventName" />);
	});

	it('renders a disabled button ', () => {
		expect(itemSelectorUrl.getByRole('button')).toBeDisabled();
	});

	describe('when the user types an invalid url', () => {
		beforeEach(() => {
			fireEvent.change(itemSelectorUrl.getByLabelText('url'), {
				target: {value: 'test'},
			});
		});

		it('renders a disabled button ', () => {
			expect(itemSelectorUrl.getByRole('button')).toBeDisabled();
		});

		describe('and after 5 seconds', () => {
			beforeEach(() => {
				act(() => {
					jest.advanceTimersByTime(5000);
				});
			});

			it('renders no preview available message', () => {
				expect(
					itemSelectorUrl.getByText('there-is-no-preview-available')
				).toBeTruthy();
			});

			it('enables the button', () => {
				expect(itemSelectorUrl.getByRole('button')).toBeEnabled();
			});
		});
	});
});
