/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	RangeSelectors,
	TrendClassification,
} from '@liferay/analytics-reports-js-components-web';
import {
	render,
	screen,
	waitForElementToBeRemoved,
	within,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {
	ContentAndFilesCard,
	IMetricsProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/ContentAndFilesCard';

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

		const trendIcon = within(trendParent).getByRole('presentation', {
			name: 'caret-top',
		});
		expect(trendIcon).toBeInTheDocument();
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

		const trendIcon = within(trendParent).getByRole('presentation', {
			name: 'caret-bottom',
		});
		expect(trendIcon).toBeInTheDocument();
	});
});
