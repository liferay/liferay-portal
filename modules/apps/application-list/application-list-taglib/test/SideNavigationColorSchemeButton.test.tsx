/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import SideNavigationColorSchemeButton from '../src/main/resources/META-INF/resources/js/SideNavigationColorSchemeButton';

const runAnimationFrames = () =>
	act(() => {
		jest.runOnlyPendingTimers();
		jest.runOnlyPendingTimers();
	});

describe('SideNavigationColorSchemeButton', () => {
	const util = Liferay.Util;

	beforeEach(() => {
		jest.useFakeTimers();

		Liferay.Util = {
			...util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};

		delete document.documentElement.dataset.colorScheme;

		document.body.classList.remove('c-prefers-reduced-motion');
	});

	afterEach(() => {
		jest.useRealTimers();

		Liferay.Util = util;
	});

	const renderComponent = () => {
		render(
			<SideNavigationColorSchemeButton
				colorScheme="light"
				colorSchemeSessionKey="colorScheme"
			/>
		);

		return userEvent.setup({advanceTimers: jest.advanceTimersByTime});
	};

	it('suspends motion while the color scheme switches', async () => {
		const user = renderComponent();

		await user.click(screen.getByLabelText('switch-to-dark-mode'));

		expect(document.documentElement.dataset.colorScheme).toBe('dark');
		expect(document.body).toHaveClass('c-prefers-reduced-motion');

		runAnimationFrames();

		expect(document.body).not.toHaveClass('c-prefers-reduced-motion');
	});

	it('keeps motion reduced when the accessibility menu reduces it', async () => {
		document.body.classList.add('c-prefers-reduced-motion');

		const user = renderComponent();

		await user.click(screen.getByLabelText('switch-to-dark-mode'));

		runAnimationFrames();

		expect(document.documentElement.dataset.colorScheme).toBe('dark');
		expect(document.body).toHaveClass('c-prefers-reduced-motion');
	});
});
