/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {localStorage} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import AccessibilityMenu from '../src/main/resources/META-INF/resources/js/AccessibilityMenu';

const HIGH_CONTRAST_SETTING = {
	attributeName: 'data-prefers-contrast',
	attributeValue: 'more',
	className: null,
	defaultValue: false,
	description: 'high-contrast-description',
	key: 'ACCESSIBILITY_SETTING_HIGH_CONTRAST',
	label: 'high-contrast',
	sessionClicksValue: false,
} as const;

function openAccessibilityMenu() {
	const [, openAccessibilityMenuListener] = (
		Liferay.on as jest.Mock
	).mock.calls.find(([eventName]) => eventName === 'openAccessibilityMenu');

	act(() => openAccessibilityMenuListener());
}

describe('AccessibilityMenu', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(Liferay.ThemeDisplay.isSignedIn as jest.Mock).mockReturnValue(false);
	});

	afterEach(() => {
		(Liferay.ThemeDisplay.isSignedIn as jest.Mock).mockReturnValue(true);

		document.documentElement.removeAttribute('data-prefers-contrast');

		window.localStorage.clear();
	});

	it('toggles the High Contrast attribute on the html element', async () => {
		render(<AccessibilityMenu settings={[HIGH_CONTRAST_SETTING]} />);

		openAccessibilityMenu();

		const highContrastToggle =
			await screen.findByLabelText('high-contrast');

		expect(document.documentElement).not.toHaveAttribute(
			'data-prefers-contrast'
		);

		await userEvent.click(highContrastToggle);

		expect(document.documentElement).toHaveAttribute(
			'data-prefers-contrast',
			'more'
		);
		expect(
			localStorage.getItem(
				HIGH_CONTRAST_SETTING.key,
				localStorage.TYPES.FUNCTIONAL
			)
		).toBe('true');

		await userEvent.click(highContrastToggle);

		expect(document.documentElement).not.toHaveAttribute(
			'data-prefers-contrast'
		);
		expect(
			localStorage.getItem(
				HIGH_CONTRAST_SETTING.key,
				localStorage.TYPES.FUNCTIONAL
			)
		).toBe('false');
	});
});
