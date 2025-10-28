/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import Loading from '../Loading';

describe('Loading', () => {
	it('renders Loading component without crashing', async () => {
		const {container} = render(<Loading />);

		const loadingAnimationChild = container.querySelector(
			'span.loading-animation'
		);

		const loadingElement = screen.getByTestId('loading');

		expect(loadingElement).toBeInTheDocument();

		expect(loadingElement.contains(loadingAnimationChild)).toBe(true);
	});

	it('renders Loading component with "inline" className', () => {
		render(<Loading inline />);

		const loadingElement = screen.getByTestId('loading');

		expect(loadingElement).toHaveClass('inline-item inline-item-before');
	});

	it('rendes Loading component with "loading-absolute" className', () => {
		const {container} = render(<Loading absolute />);

		const loadingAnimationChild = container.querySelector(
			'span.loading-animation'
		);

		const loadingElement = screen.getByTestId('loading');

		expect(loadingElement).toBeInTheDocument();

		expect(loadingElement.contains(loadingAnimationChild)).toBe(true);

		expect(loadingAnimationChild).toHaveClass('loading-absolute');
	});

	it('renders Loading component with new style', () => {
		render(<Loading style={{backgroundColor: 'orange'}} />);

		const loadingElement = screen.getByTestId('loading');

		expect(loadingElement).toHaveAttribute(
			'style',
			'background-color: orange;'
		);
	});
});
