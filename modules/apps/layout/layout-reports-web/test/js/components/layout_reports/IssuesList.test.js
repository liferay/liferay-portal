/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import IssuesList from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/IssuesList';
import {StoreContextProvider} from '../../../../src/main/resources/META-INF/resources/js/context/StoreContext';
import loadIssues from '../../../../src/main/resources/META-INF/resources/js/utils/loadIssues';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/utils/loadIssues',
	() => jest.fn(() => () => {})
);

const mockLayoutReportsIssues = [
	{
		details: [
			{
				key: 'missing-input-alt-attributes',
				title: 'Missing Input ALT Attributes',
				total: '4',
			},
			{
				key: 'missing-video-caption',
				title: 'Missing Video Caption',
				total: '5',
			},
		],
		key: 'accessibility',
		title: 'Accessibility',
		total: '17',
	},
	{
		details: [],
		key: 'seo',
		title: 'SEO',
		total: '123',
	},
];

const mockLayoutReportsIssuesNoSEO = [
	{
		details: [],
		key: 'accessibility',
		title: 'Accessibility',
		total: '17',
	},
	{
		details: [],
		key: 'seo',
		title: 'SEO',
		total: '0',
	},
];

const mockLayoutReportsIssuesNoAccessibilityNoSEO = [
	{
		key: 'accessibility',
		title: 'Accessibility',
		total: '0',
	},
	{
		key: 'seo',
		title: 'SEO',
		total: '0',
	},
];

const mockLayoutReportsIssuesOver100SEO = [
	{
		details: [],
		key: 'accessibility',
		title: 'Accessibility',
		total: '0',
	},
	{
		details: [],
		key: 'seo',
		title: 'SEO',
		total: '101',
	},
];

const mockLayoutReportsIssuesSEODetails = [
	{
		details: [],
		key: 'accessibility',
		title: 'Accessibility',
		total: '0',
	},
	{
		details: [
			{
				description: '<p>Meta description</p>',
				key: 'missing-meta-description',
				tips: '<p>Meta description can be added</p>',
				title: 'Missing Meta Description',
				total: '1',
			},
			{
				description: '<p>Check with no failing elements</p>',
				key: 'check-with-no-failing-elements',
				tips: '<p>No failing elements</p>',
				title: 'No failing elements',
				total: '0',
			},
		],
		key: 'seo',
		title: 'SEO',
		total: '1',
	},
];

const mockPageURLs = [
	{languageId: 'en-US', title: 'English', url: 'English URL'},
	{languageId: 'es-ES', title: 'Español', url: 'URL en Español'},
];

const defaultLanguageId = 'en-US';

const renderIssuesList = ({
	languageId = defaultLanguageId,
	layoutReportsIssues,
	loading = false,
}) => {
	return render(
		<StoreContextProvider
			value={{
				data: {
					defaultLanguageId,
					imagesPath: 'imagesPath',
					layoutReportsIssues: {
						[defaultLanguageId]: {issues: layoutReportsIssues},
					},
					pageURLs: mockPageURLs,
				},
				languageId,
				loading,
			}}
		>
			<IssuesList />
		</StoreContextProvider>
	);
};

describe('IssuesList', () => {
	it('renders accessibility and seo sections with issues count', () => {
		const {getByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssues,
		});

		expect(getByText('Accessibility')).toBeInTheDocument();
		expect(getByText('17')).toBeInTheDocument();
		expect(getByText('SEO')).toBeInTheDocument();
		expect(getByText('+100')).toBeInTheDocument();
	});

	it('renders no seo or accesibility issues message', () => {
		const {getByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssuesNoSEO,
		});

		expect(getByText('SEO')).toBeInTheDocument();
		expect(getByText('0')).toBeInTheDocument();
		expect(getByText('there-are-no-x-related-issues')).toBeInTheDocument();
	});

	it('renders no issues message (no seo and accessibility issues)', () => {
		const {getAllByText, getByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssuesNoAccessibilityNoSEO,
		});

		expect(getByText('Accessibility')).toBeInTheDocument();
		expect(getByText('SEO')).toBeInTheDocument();
		expect(getAllByText('0').length).toBe(2);
		expect(getByText('your-page-has-no-issues')).toBeInTheDocument();
	});

	it('render list of accesibility issues with correct number of failing elements', () => {
		const {getAllByText, getByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssues,
		});

		expect(getByText('Missing Input ALT Attributes')).toBeInTheDocument();
		expect(getByText('Missing Video Caption')).toBeInTheDocument();
		expect(getAllByText('4').length).toBe(1);
		expect(getAllByText('5').length).toBe(1);
	});

	it('show +100 if a section has more than 100 issues', () => {
		const {getByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssuesOver100SEO,
		});

		expect(getByText('+100')).toBeInTheDocument();
	});

	it('renders SEO section open and show checks with failing elements', () => {
		const {getAllByText, getByText, queryByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssuesSEODetails,
		});

		expect(getByText('Missing Meta Description')).toBeInTheDocument();
		expect(getAllByText('1').length).toBe(2);

		expect(queryByText('No failing elements')).not.toBeInTheDocument();
	});

	it('renders loading progress bar, title, URL and language when issues are loading', () => {
		const {getByText} = renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssuesSEODetails,
			loading: true,
		});

		expect(
			getByText('connecting-with-google-pagespeed')
		).toBeInTheDocument();
	});

	it('renders no issues loaded view when there are no issues for the selected language', () => {
		const {getByText} = renderIssuesList({
			languageId: 'es-ES',
			layoutReportsIssues: mockLayoutReportsIssuesSEODetails,
		});

		expect(
			getByText(
				"launch-a-page-audit-to-check-issues-that-impact-on-your-page's-accesibility-and-seo"
			)
		).toBeInTheDocument();
	});

	it('calls loadIssues when clicking launch button in no issues loaded view', () => {
		const {getByText} = renderIssuesList({
			languageId: 'es-ES',
			layoutReportsIssues: mockLayoutReportsIssuesSEODetails,
		});

		const button = getByText('launch');

		userEvent.click(button);

		expect(loadIssues).toBeCalled();
	});

	it('does not show launch button when there is cached data', () => {
		renderIssuesList({
			layoutReportsIssues: mockLayoutReportsIssuesSEODetails,
		});

		expect(screen.queryByText('launch')).not.toBeInTheDocument();
	});

	it('shows launch button when there is no cached data', () => {
		renderIssuesList({
			languageId: 'es-ES',
			layoutReportsIssues: mockLayoutReportsIssuesSEODetails,
		});

		expect(screen.getByText('launch')).toBeInTheDocument();
	});
});
