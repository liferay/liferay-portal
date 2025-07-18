/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	render,
	screen,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import {
	ContentAndFilesCard,
	IMetricsProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/ContentAndFilesCard';
import {RangeSelectors} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/RangeSelectorsDropdown';
import {TrendClassification} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/utils/metrics';

const mockedResponse: IMetricsProps = {
	categoriesCount: 10,
	tagsCount: 10,
	totalCount: 30,
	trend: {
		classification: TrendClassification.Neutral,
		percentage: 100.0,
	},
	vocabulariesCount: 10,
};

const WrappedComponent = () => (
	<ContentAndFilesCard
		endpointURL="/o/analytics-cms-rest/v1.0/content-overview"
		rangeSelector={{
			rangeEnd: '',
			rangeKey: RangeSelectors.Last7Days,
			rangeStart: '',
		}}
		title={(totalCount) => {
			return `${totalCount} new content items`;
		}}
	/>
);

describe('[CMS Dashboard] Components: ContentAndFilesCard', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly with given props', async () => {
		global.fetch = jest.fn().mockResolvedValue({
			json: () => Promise.resolve(mockedResponse),
			ok: true,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const Title = screen.getByText('30 new content items');
		expect(Title).toBeInTheDocument();

		const Trend = screen.getByText('x-vs-previous-period');
		expect(Trend).toBeInTheDocument();

		const VocabulariesBreakdown = screen.getByText('vocabularies');
		expect(VocabulariesBreakdown).toBeInTheDocument();

		const CategoriesBreakdown = screen.getByText('categories');
		expect(CategoriesBreakdown).toBeInTheDocument();

		const TagsBreakdown = screen.getByText('tags');
		expect(TagsBreakdown).toBeInTheDocument();
	});

	it('renders correctly with POSITIVE trend', async () => {
		global.fetch = jest.fn().mockResolvedValue({
			json: () =>
				Promise.resolve({
					...mockedResponse,
					trend: {
						classification: TrendClassification.Positive,
						percentage: -42,
					},
				}),
			ok: true,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const Trend = screen.getByText('42%').parentElement;

		expect(Trend).toBeInTheDocument();
		expect(Trend).toHaveTextContent('42%');
		expect(Trend).toHaveClass('text-success');
	});

	it('renders correctly with NEGATIVE trend', async () => {
		global.fetch = jest.fn().mockResolvedValue({
			json: () =>
				Promise.resolve({
					...mockedResponse,
					trend: {
						classification: TrendClassification.Negative,
						percentage: 42,
					},
				}),
			ok: true,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const Trend = screen.getByText('42%').parentElement;
		expect(Trend).toBeInTheDocument();
		expect(Trend).toHaveTextContent('42%');
		expect(Trend).toHaveClass('text-danger');
	});
});
