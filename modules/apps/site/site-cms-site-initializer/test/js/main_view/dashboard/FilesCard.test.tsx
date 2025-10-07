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
import {FilesCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/FilesCard';

describe('[CMS Dashboard] Components: FilesCard', () => {
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
		render(<FilesCard />);

		const Title = screen.getByText('FILES');
		expect(Title).toBeInTheDocument();

		const Description = screen.getByText(
			'this-metric-calculates-the-total-amount-of-files-created-in-your-spaces'
		);
		expect(Description).toBeInTheDocument();

		const [RangeSelectorDropdown] = screen.getAllByRole('button');

		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		await waitForElementToBeRemoved(
			screen.getByTestId('loading-animation')
		);

		const MainMetric = screen.getByText('x-new-files');
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
