/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import {
	render,
	screen,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {IMetricsProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/ContentAndFilesCard';
import {ContentCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/ContentCard';

describe('[CMS Dashboard] Components: ContentCard', () => {
	beforeEach(() => {
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

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockedResponse,
			error: null,
		});
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly', async () => {
		render(<ContentCard />);

		const Title = screen.getByText('CONTENT');
		expect(Title).toBeInTheDocument();

		const Description = screen.getByText(
			'this-metric-calculates-the-total-amount-of-content-assets-created-in-your-spaces'
		);
		expect(Description).toBeInTheDocument();

		const [RangeSelectorDropdown] = screen.getAllByRole('button');

		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const MainMetric = screen.getByText('x-new-content-items');
		expect(MainMetric).toBeInTheDocument();

		const Comparison = screen.getByText('x-vs-previous-period');
		expect(Comparison).toBeInTheDocument();

		const VocabulariesBreakdown = screen.getByText('vocabularies');
		expect(VocabulariesBreakdown).toBeInTheDocument();

		const CategoriesBreakdown = screen.getByText('categories');
		expect(CategoriesBreakdown).toBeInTheDocument();

		const TagsBreakdown = screen.getByText('tags');
		expect(TagsBreakdown).toBeInTheDocument();
	});
});
