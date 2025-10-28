/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {App, EPageView} from '../index';

const INITIAL_PROPS = {
	connected: false,
	liferayAnalyticsURL: '',
	token: '',
	wizardMode: false,
};

describe('App', () => {
	it('renders without crashing', () => {
		const {container} = render(<App {...INITIAL_PROPS} />);

		expect(container.firstChild).toHaveClass('analytics-settings-web');
	});

	it('renders wizard view when not connected', () => {
		const {getAllByTestId} = render(<App {...INITIAL_PROPS} wizardMode />);

		expect(getAllByTestId(EPageView.Wizard)).toBeTruthy();
	});

	it('renders default view when connected', () => {
		const {getAllByTestId} = render(
			<App {...INITIAL_PROPS} connected wizardMode={false} />
		);

		expect(getAllByTestId(EPageView.Default)).toBeTruthy();
	});
});
