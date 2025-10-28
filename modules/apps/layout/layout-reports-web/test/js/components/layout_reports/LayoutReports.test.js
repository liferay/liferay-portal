/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import LayoutReports from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/LayoutReports';
import {StoreContextProvider} from '../../../../src/main/resources/META-INF/resources/js/context/StoreContext';
import loadIssues from '../../../../src/main/resources/META-INF/resources/js/utils/loadIssues';
import {layoutReportsIssues, pageURLs} from '../../mocks';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/utils/loadIssues',
	() => jest.fn(() => () => {})
);

const getLayoutReportsComponent = ({
	error = null,
	layoutReportsIssues = null,
	loading = false,
	privateLayout = false,
	selectedItem = null,
	validConnection = true,
} = {}) => {
	return (
		<StoreContextProvider
			value={{
				data: {
					defaultLanguageId: 'en-US',
					layoutReportsIssues,
					pageURLs,
					privateLayout,
					validConnection,
				},
				error,
				languageId: 'en-US',
				loading,
				selectedItem,
			}}
		>
			<LayoutReports />
		</StoreContextProvider>
	);
};

const languageSelectorIsInTheDocument = ({fn, useNot = false}) => {
	['Home', 'en-US', 'http://localhost:8080'].forEach((str) => {

		// eslint-disable-next-line @liferay/expect-assert
		const expected = useNot ? expect(fn(str)).not : expect(fn(str));

		expected.toBeInTheDocument();
	});
};

describe('LayoutReports renders proper component', () => {
	afterEach(cleanup);

	it('Renders error component if and error code 500 is received', () => {
		const {getByText} = render(
			getLayoutReportsComponent({
				error: {
					code: 500,
				},
			})
		);

		expect(getByText('this-page-cannot-be-audited')).toBeInTheDocument();
		expect(getByText('show-details')).toBeInTheDocument();

		languageSelectorIsInTheDocument({fn: getByText});
	});

	it('Renders error component without the extended information if a private page is received', () => {
		const {getByText, queryByText} = render(
			getLayoutReportsComponent({
				privateLayout: true,
			})
		);

		expect(getByText('this-page-cannot-be-audited')).toBeInTheDocument();
		expect(queryByText('show-details')).not.toBeInTheDocument();

		languageSelectorIsInTheDocument({fn: getByText});
	});

	it('Renders not-configured component when a valid connection is not provided, without the page lang selector', () => {
		const {getByText, queryByText} = render(
			getLayoutReportsComponent({
				validConnection: false,
			})
		);

		expect(
			getByText(
				"check-issues-that-impact-on-your-page's-accessibility-and-seo"
			)
		).toBeInTheDocument();

		languageSelectorIsInTheDocument({fn: queryByText, useNot: true});
	});

	it('Renders issues list if available', () => {
		const {getByText} = render(
			getLayoutReportsComponent({layoutReportsIssues})
		);

		const alert = document.querySelector('.alert');

		expect(alert).toBeInTheDocument();
		expect(
			getByText('July 5, 2021 12:09 PM', {exact: false})
		).toBeInTheDocument();

		languageSelectorIsInTheDocument({fn: getByText});
	});

	it('renders language selector button disabled when issues are loading', () => {
		const {getByText} = render(getLayoutReportsComponent({loading: true}));

		const button = getByText('en-US').parentElement;

		expect(button.disabled).toBe(true);
	});

	it('shows an alert with last load date and a button to reload when there is cached data', () => {
		render(getLayoutReportsComponent({layoutReportsIssues}));

		expect(
			screen.getByText('showing-data-from-July 5, 2021 12:09 PM')
		).toBeInTheDocument();

		expect(screen.getByText('relaunch-to-update-data')).toBeInTheDocument();
	});

	it('calls loadIssues when clicking relaunch button', () => {
		render(getLayoutReportsComponent({layoutReportsIssues}));

		const button = screen.getByText('relaunch-to-update-data');

		userEvent.click(button);

		expect(loadIssues).toBeCalled();
	});

	it('does not render relaunch button if it is private page', () => {
		render(
			getLayoutReportsComponent({
				layoutReportsIssues,
				privateLayout: true,
			})
		);

		expect(
			screen.queryByText('relaunch-to-update-data')
		).not.toBeInTheDocument();
	});

	it('does not render relaunch button if key is not configured', () => {
		render(
			getLayoutReportsComponent({
				layoutReportsIssues,
				validConnection: false,
			})
		);

		expect(
			screen.queryByText('relaunch-to-update-data')
		).not.toBeInTheDocument();
	});
});
