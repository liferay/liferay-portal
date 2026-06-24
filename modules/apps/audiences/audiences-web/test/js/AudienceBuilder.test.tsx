/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AudienceBuilder from '../../src/main/resources/META-INF/resources/js/AudienceBuilder';

describe('AudienceBuilder', () => {
	it('renders editor, updates name, back and cancel link to backURL', async () => {
		const {getByLabelText, getByText, queryByText} = render(
			<AudienceBuilder backURL="/back" namespace="_test_" />
		);

		expect(getByText('new-audience')).toBeTruthy();
		expect(getByText('cancel')).toBeTruthy();
		expect(getByText('save')).toBeTruthy();
		expect(getByText('attributes-types')).toBeTruthy();

		expect(getByLabelText('back').getAttribute('href')).toBe('/back');
		expect(getByText('cancel').getAttribute('href')).toBe('/back');

		const input = getByLabelText('name');

		expect(input.getAttribute('name')).toBe('_test_name');
		expect(input.getAttribute('maxLength')).toBe('75');
		expect(input.hasAttribute('required')).toBe(true);

		expect((input as HTMLInputElement).value).toBe('');
		expect(input.getAttribute('placeholder')).toBe('new-audience');

		await userEvent.type(input, 'My Audience');

		expect(getByText('My Audience')).toBeTruthy();
		expect(queryByText('new-audience')).toBeNull();
	});
});
