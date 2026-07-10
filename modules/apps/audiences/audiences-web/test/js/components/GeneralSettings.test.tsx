/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import GeneralSettings from '../../../src/main/resources/META-INF/resources/js/components/GeneralSettings';

function GeneralSettingsWrapper() {
	const [externalReferenceCode, setExternalReferenceCode] = useState('');

	return (
		<GeneralSettings
			externalReferenceCode={externalReferenceCode}
			namespace="_test_"
			onExternalReferenceCodeChange={setExternalReferenceCode}
		/>
	);
}

describe('GeneralSettings', () => {
	it('starts collapsed, expands on interaction, and edits the external reference code', async () => {
		render(<GeneralSettingsWrapper />);

		const toggle = screen.getByRole('button', {name: 'general-settings'});

		expect(toggle.getAttribute('aria-expanded')).toBe('false');

		await userEvent.click(toggle);

		expect(toggle.getAttribute('aria-expanded')).toBe('true');

		const input = screen.getByRole('textbox', {
			name: 'erc',
		});

		expect(input.getAttribute('id')).toBe(
			'_test_externalReferenceCodeInput'
		);
		expect((input as HTMLInputElement).value).toBe('');

		await userEvent.type(input, 'ABC-123');

		expect((input as HTMLInputElement).value).toBe('ABC-123');

		await userEvent.click(toggle);

		expect(toggle.getAttribute('aria-expanded')).toBe('false');

		await userEvent.click(toggle);

		expect(toggle.getAttribute('aria-expanded')).toBe('true');
		expect(
			(
				screen.getByRole('textbox', {
					name: 'erc',
				}) as HTMLInputElement
			).value
		).toBe('ABC-123');
	});
});
