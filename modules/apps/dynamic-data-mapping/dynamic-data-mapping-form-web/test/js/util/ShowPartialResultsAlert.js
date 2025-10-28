/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ShowPartialResultsAlert from '../../../src/main/resources/META-INF/resources/js/util/ShowPartialResultsAlert';

describe('Show Partial Results Alert', () => {
	afterEach(() => {
		cleanup();
	});

	it('shows the alert without dismiss button when showPartialResultsToRespondents is enabled and dismissible is false', () => {
		const {queryByText} = render(
			<ShowPartialResultsAlert
				dismissible={false}
				showPartialResultsToRespondents={true}
			/>
		);

		expect(queryByText('understood')).not.toBeInTheDocument();
		expect(
			queryByText('respondents-can-see-all-submitted-form-data')
		).toBeInTheDocument();
	});

	it('shows the alert with a dismissible button', () => {
		const {queryByText} = render(
			<ShowPartialResultsAlert
				dismissible={true}
				showPartialResultsToRespondents={true}
			/>
		);

		expect(queryByText('understood')).toBeInTheDocument();
	});

	it('hides the alert when clicking on Understood button', () => {
		const {container, queryByText} = render(
			<ShowPartialResultsAlert
				dismissible={true}
				showPartialResultsToRespondents={true}
			/>
		);

		const button = queryByText('understood');

		userEvent.click(button);

		const hiddenAlert = container.querySelector(
			'.lfr-ddm__show-partial-results-alert--hidden'
		);

		expect(hiddenAlert).toBeInTheDocument();
	});
});
