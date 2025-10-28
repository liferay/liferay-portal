/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';

import {JobId, JobStatus} from '../../..//utils/api';
import RecommendationsStep from '../RecommendationsStep';

const response = {
	[JobId.ContentRecommenderMostPopularItems]: {
		enabled: true,
		status: JobStatus.Enabled,
	},
	[JobId.ContentRecommenderUserPersonalization]: {
		enabled: true,
		status: JobStatus.Enabled,
	},
};

describe('Recommendations Step', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('render RecommendationsStep without crashing', async () => {
		await act(async () => {
			fetch.mockResponseOnce(JSON.stringify(response));

			const {container, getByText} = render(
				<RecommendationsStep
					onCancel={() => {}}
					onChangeStep={() => {}}
				/>
			);

			const recommendationsStepTitle = getByText('recommendations');

			const recommendationsStepDescription = getByText(
				'content-recommendations-personalize-user-experiences-by-suggesting-relevant-items-based-on-user-behavior-and-preferences'
			);

			expect(recommendationsStepTitle).toBeInTheDocument();

			expect(recommendationsStepDescription).toBeInTheDocument();

			expect(container.firstChild).toHaveClass('sheet');

			const linkToDocumentation = getByText(
				'learn-more-about-recommendations'
			);

			expect(linkToDocumentation).toBeInTheDocument();
			expect(linkToDocumentation).toHaveAttribute(
				'href',
				'https://learn.liferay.com/w/analytics-cloud/getting-started/connecting-liferay-dxp-to-analytics-cloud'
			);
		});
	});
});
