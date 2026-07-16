/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	RangeSelectors,
	TrendClassification,
} from '@liferay/analytics-reports-js-components-web';
import {
	render,
	screen,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {
	ContentAndFilesCard,
	IMetricsProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/inventory/components/ContentAndFilesCard';

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

const mockedResponseSingularValues: IMetricsProps = {
	categoriesCount: 1,
	tagsCount: 1,
	totalCount: 1,
	trend: {
		classification: TrendClassification.Neutral,
		percentage: 100.0,
	},
	vocabulariesCount: 1,
};

const WrappedComponent = () => (
	<ContentAndFilesCard
		endpointURL="/o/analytics-cms-rest/v1.0/content-overview"
		rangeSelector={{
			rangeEnd: '',
			rangeKey: RangeSelectors.Last7Days,
			rangeStart: '',
		}}
		title={(totalCount) =>
			totalCount === 1
				? `1 new content item`
				: `${totalCount} new content items`
		}
	/>
);

describe('[CMS Dashboard] Components: ContentAndFilesCard', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly with given props', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockedResponse,
			error: null,
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
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				...mockedResponse,
				trend: {
					classification: TrendClassification.Positive,
					percentage: 42,
				},
			},
			error: null,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const trendParent = screen.getByText('42%')
			.parentElement as HTMLElement;
		expect(trendParent).toBeInTheDocument();
		expect(trendParent).toHaveTextContent('42%');
		expect(trendParent).toHaveClass('text-success');

		expect(
			trendParent.querySelector('.lexicon-icon-caret-top')
		).toBeInTheDocument();
	});

	it('renders correctly with NEGATIVE trend', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				...mockedResponse,
				trend: {
					classification: TrendClassification.Negative,
					percentage: -42,
				},
			},
			error: null,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const trendParent = screen.getByText('42%')
			.parentElement as HTMLElement;
		expect(trendParent).toBeInTheDocument();
		expect(trendParent).toHaveTextContent('42%');
		expect(trendParent).toHaveClass('text-danger');

		expect(
			trendParent.querySelector('.lexicon-icon-caret-bottom')
		).toBeInTheDocument();
	});

	it('formats percentage to two decimal places correctly', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				...mockedResponse,
				trend: {
					classification: TrendClassification.Positive,
					percentage: 3.14159265,
				},
			},
			error: null,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const percentageText = screen.getByText('3.14%');
		expect(percentageText).toBeInTheDocument();
	});

	it('renders correctly with singular values', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockedResponseSingularValues,
			error: null,
		});

		render(<WrappedComponent />);

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const title = screen.getByText('1 new content item');
		expect(title).toBeInTheDocument();

		const trend = screen.getByText('x-vs-previous-period');
		expect(trend).toBeInTheDocument();

		const vocabulariesBreakdown = screen.getByText('vocabulary');
		expect(vocabulariesBreakdown).toBeInTheDocument();

		const categoriesBreakdown = screen.getByText('category');
		expect(categoriesBreakdown).toBeInTheDocument();

		const tagsBreakdown = screen.getByText('tag');
		expect(tagsBreakdown).toBeInTheDocument();
	});
});
