/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import '@testing-library/jest-dom';

import ProfilesFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/props_transformer/ProfilesFDSPropsTransformer';

describe('ProfilesFDSPropsTransformer', () => {
	const copyURL = (name: string, profilePath: string) => {
		const {onActionDropdownItemClick} = ProfilesFDSPropsTransformer({});

		onActionDropdownItemClick({
			action: {data: {id: 'copyURL'}},
			itemData: {description: 'Description', name, profilePath},
			loadData: jest.fn(),
		});
	};

	beforeEach(() => {
		userEvent.setup();
	});

	it('alerts when the clipboard is unavailable', async () => {
		Object.defineProperty(navigator, 'clipboard', {
			configurable: true,
			value: undefined,
		});

		copyURL('Tool Search', 'tool-search');

		expect(
			await screen.findByText('failed-to-copy-to-clipboard')
		).toBeInTheDocument();
	});

	it('copies the URL of the profile', async () => {
		copyURL('Tool Search', 'tool-search');

		await waitFor(async () =>
			expect(await navigator.clipboard.readText()).toBe(
				`${window.location.origin}/o/mcp/tool-search`
			)
		);
	});

	it('copies the base URL of the default profile', async () => {
		copyURL('Default', '');

		await waitFor(async () =>
			expect(await navigator.clipboard.readText()).toBe(
				`${window.location.origin}/o/mcp`
			)
		);
	});
});
