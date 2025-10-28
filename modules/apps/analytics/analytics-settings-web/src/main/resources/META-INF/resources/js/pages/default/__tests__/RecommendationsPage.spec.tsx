/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import fetch from 'jest-fetch-mock';

import {JobId, JobStatus} from '../../../utils/api';
import RecommendationsPage from '../RecommendationsPage';

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

describe('RecommendationsPage', () => {
	it('renders page title and description', async () => {
		fetch.mockResponseOnce(JSON.stringify(response));

		await act(async () => {
			const {getByText} = render(
				<RecommendationsPage title="Recommendations" />
			);

			const title = screen.getByText('Recommendations');
			const description = screen.getByText(
				'content-recommendations-personalize-user-experiences-by-suggesting-relevant-items-based-on-user-behavior-and-preferences'
			);

			expect(title).toBeInTheDocument();
			expect(description).toBeInTheDocument();

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
