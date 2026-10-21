/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import Highlight from '../../src/main/resources/META-INF/resources/js/components/Highlight';

describe('Highlight', () => {
	it('renders the text as is when there is no query', () => {
		render(<Highlight query="" text="Email Address" />);

		expect(screen.getByText('Email Address')).toBeInTheDocument();
	});

	it('marks the matching segment, case-insensitively', () => {
		const {container} = render(
			<Highlight query="ADDR" text="Email Address" />
		);

		expect(container).toHaveTextContent('Email Address');
		expect(screen.getByText('Addr').tagName).toBe('MARK');
	});

	it('renders a matched value with markup characters literally', () => {
		const {container} = render(
			<Highlight query="m & j" text="Tom & Jerry <img src=x>" />
		);

		expect(container).toHaveTextContent('Tom & Jerry <img src=x>');
		expect(container.querySelector('img')).toBeNull();
		expect(screen.getByText('m & J').tagName).toBe('MARK');
	});
});
