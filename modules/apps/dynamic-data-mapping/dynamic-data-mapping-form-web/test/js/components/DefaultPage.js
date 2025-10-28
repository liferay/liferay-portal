/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import DefaultPage from '../../../src/main/resources/META-INF/resources/js/components/DefaultPage';

describe('DefaultPage', () => {
	afterEach(cleanup);

	it('shows the form title, form description, page description and page title', () => {
		const {getByText} = render(
			<DefaultPage
				formDescription="Form description"
				formTitle="Form title"
				pageDescription="Page description"
				pageTitle="Page title"
			/>
		);

		expect(getByText('Form title')).toBeInTheDocument();
		expect(getByText('Form description')).toBeInTheDocument();
		expect(getByText('Page description')).toBeInTheDocument();
		expect(getByText('Page title')).toBeInTheDocument();
	});

	it('shows submit again button when the show submit again flag is enabled', () => {
		const {getByText} = render(<DefaultPage showSubmitAgainButton />);

		expect(getByText('submit-again')).toBeInTheDocument();
	});

	it('hides submit again button when the show submit again flag is disabled', () => {
		const {queryByText} = render(<DefaultPage />);

		expect(queryByText('submit-again')).not.toBeInTheDocument();
	});

	it('hides partial results button if no url is provided', () => {
		const {queryByText} = render(
			<DefaultPage showPartialResultsToRespondents />
		);

		expect(
			queryByText('preview-existing-submissions')
		).not.toBeInTheDocument();
	});

	it('shows partial results button when Show partial results to respondents is enabled', () => {
		const {queryByText} = render(
			<DefaultPage
				formReportDataURL="http://liferay.com/"
				showPartialResultsToRespondents
			/>
		);

		expect(queryByText('preview-existing-submissions')).toBeInTheDocument();
	});

	it('hides partial results button when Show partial results to respondents is disabled', () => {
		const {queryByText} = render(<DefaultPage />);

		expect(
			queryByText('preview-existing-submissions')
		).not.toBeInTheDocument();
	});
});
