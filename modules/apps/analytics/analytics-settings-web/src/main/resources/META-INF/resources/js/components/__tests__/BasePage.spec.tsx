/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import BasePage from '../BasePage';

const ChildComponent = () => <p>I am a child component</p>;

const FooterChildComponent = () => <p>I am a footer child component</p>;

describe('BasePage', () => {
	it('renders BasePage component without crashing', () => {
		render(
			<BasePage description="test description" title="test title">
				<ChildComponent />
			</BasePage>
		);

		expect(screen.getByText(/test title/i)).toBeInTheDocument();

		expect(screen.getByText(/test description/i)).toBeInTheDocument();

		expect(screen.getByText(/I am a child component/i)).toBeInTheDocument();
	});

	it('renders BasePageFooter component without crashing', () => {
		render(
			<BasePage.Footer>
				<FooterChildComponent />
			</BasePage.Footer>
		);

		expect(
			screen.getByText(/I am a footer child component/i)
		).toBeInTheDocument();
	});
});
