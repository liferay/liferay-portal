/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';
import React from 'react';

import Avatar from '../../js/Assignee/Avatar';

describe('Avatar', () => {
	it('renders the initials of the first two words of a multi-word name', () => {
		const {container} = render(<Avatar name="foo bar baz" />);

		expect(
			container.querySelector('.object-field__assignee-initials')
		).toHaveTextContent('FB');
	});

	it('renders the initials of a two-word name', () => {
		const {container} = render(<Avatar name="foo bar" />);

		expect(
			container.querySelector('.object-field__assignee-initials')
		).toHaveTextContent('FB');
	});

	it('renders the first two letters of a single long word name', () => {
		const {container} = render(<Avatar name="foo" />);

		expect(
			container.querySelector('.object-field__assignee-initials')
		).toHaveTextContent('FO');
	});

	it('renders the first letter of a single short word name', () => {
		const {container} = render(<Avatar name="f" />);

		expect(
			container.querySelector('.object-field__assignee-initials')
		).toHaveTextContent('F');
	});
});
