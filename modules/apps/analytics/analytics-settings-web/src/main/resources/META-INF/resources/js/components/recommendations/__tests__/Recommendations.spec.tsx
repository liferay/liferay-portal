/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {loadingElement} from '../../../utils/__tests__/helpers';
import {JobId, JobStatus} from '../../../utils/api';
import Recommendations from '../Recommendations';

const mockStatusScenarios = [

	// Jobs with status enabled

	{
		expect: {
			statusLabel: 'enabled',
			toggleChecked: true,
			toggleDisabled: false,
		},
		mockedResponse: {
			contentRecommenderMostPopularItems: {
				enabled: true,
				status: JobStatus.Enabled,
			},
			contentRecommenderUserPersonalization: {
				enabled: true,
				status: JobStatus.Enabled,
			},
		},
		status: JobStatus.Enabled,
	},

	// Jobs with status disabled

	{
		expect: {
			statusLabel: 'disabled',
			toggleChecked: false,
			toggleDisabled: false,
		},
		mockedResponse: {
			contentRecommenderMostPopularItems: {
				enabled: false,
				status: JobStatus.Disabled,
			},
			contentRecommenderUserPersonalization: {
				enabled: false,
				status: JobStatus.Disabled,
			},
		},
		status: JobStatus.Disabled,
	},

	// Jobs with status configuring

	{
		expect: {
			statusLabel: 'configuring',
			toggleChecked: true,
			toggleDisabled: true,
		},
		mockedResponse: {
			contentRecommenderMostPopularItems: {
				enabled: true,
				status: JobStatus.Configuring,
			},
			contentRecommenderUserPersonalization: {
				enabled: true,
				status: JobStatus.Configuring,
			},
		},
		status: JobStatus.Configuring,
	},
];

describe('Recommendations', () => {
	it('renders recommendations table', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({
				contentRecommenderMostPopularItems: {
					enabled: true,
					status: JobStatus.Enabled,
				},
				contentRecommenderUserPersonalization: {
					enabled: true,
					status: JobStatus.Enabled,
				},
			})
		);

		const {getAllByText, getByText} = render(<Recommendations />);

		await loadingElement();

		expect(getByText('most-popular-content')).toBeInTheDocument();
		expect(
			getByText(
				'recommends-content-based-on-popularity-among-all-users-without-considering-individual-user-behavior'
			)
		).toBeInTheDocument();

		expect(
			getByText(
				'recommends-content-based-on-individual-users-preferences-and-past-behavior'
			)
		).toBeInTheDocument();
		expect(
			getByText(
				'recommends-content-based-on-popularity-among-all-users-without-considering-individual-user-behavior'
			)
		).toBeInTheDocument();

		expect(getAllByText('content')).toHaveLength(2);
	});

	mockStatusScenarios.forEach(
		({
			expect: {statusLabel, toggleChecked, toggleDisabled},
			mockedResponse,
			status,
		}) => {
			it(`renders recommendations table with status ${status}, toggle checked ${toggleChecked} and disabled ${toggleDisabled}`, async () => {
				fetch.mockResponseOnce(JSON.stringify(mockedResponse));

				const {getAllByText, getByTestId} = render(<Recommendations />);

				await loadingElement();

				const contentRecommendationRow = getByTestId(
					JobId.ContentRecommenderMostPopularItems
				);

				const userPersonalizationRow = getByTestId(
					JobId.ContentRecommenderUserPersonalization
				);

				expect(userPersonalizationRow).toBeInTheDocument();
				expect(contentRecommendationRow).toBeInTheDocument();

				const contentRecommendationToggle =
					contentRecommendationRow.querySelector(
						'#toggle .toggle-switch-check'
					);

				const userPersonalizationToggle =
					contentRecommendationRow.querySelector(
						'#toggle .toggle-switch-check'
					);

				if (toggleDisabled) {
					expect(contentRecommendationToggle).toBeDisabled();
					expect(userPersonalizationToggle).toBeDisabled();
				}
				else {
					expect(contentRecommendationToggle).toBeEnabled();
					expect(userPersonalizationToggle).toBeEnabled();
				}

				if (toggleChecked) {
					expect(contentRecommendationToggle).toBeChecked();
					expect(userPersonalizationToggle).toBeChecked();
				}
				else {
					expect(contentRecommendationToggle).not.toBeChecked();
					expect(userPersonalizationToggle).not.toBeChecked();
				}

				expect(getAllByText(statusLabel)).toHaveLength(2);
			});
		}
	);
});
