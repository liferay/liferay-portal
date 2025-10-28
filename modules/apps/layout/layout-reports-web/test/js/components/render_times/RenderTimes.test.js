/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import React from 'react';

import RenderTimes from '../../../../src/main/resources/META-INF/resources/js/components/render_times/RenderTimes';

import '@testing-library/jest-dom';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	fetch: () => Promise.resolve({json: () => ({})}),
}));

const mockSegmentsExperiences = [
	{
		active: true,
		segmentsEntryId: '0',
		segmentsEntryName: 'Anyone',
		segmentsExperienceId: '33590',
		segmentsExperienceName: 'Experience Default',
		statusLabel: 'Active',
		url: 'url',
	},
];

const mockSelectedSegmentsExperience = {
	active: true,
	segmentsEntryId: '0',
	segmentsEntryName: 'Anyone',
	segmentsExperienceId: '33590',
	segmentsExperienceName: 'Experience Default',
	statusLabel: 'Active',
	url: 'url',
};

const renderRenderTimes = ({segments = mockSegmentsExperiences} = {}) =>
	render(
		<RenderTimes
			segmentsExperiences={segments}
			selectedSegmentsExperience={mockSelectedSegmentsExperience}
			url="url"
		/>
	);

describe('RenderTimes', () => {
	it('does not render the experience selector when there is only the default experience', async () => {
		await act(async () => renderRenderTimes());

		expect(
			screen.queryByText('Experience Default')
		).not.toBeInTheDocument();
	});

	it('renders experience selector if there is more than one experience', async () => {
		const newSegments = [
			...mockSegmentsExperiences,
			{
				active: true,
				segmentsEntryId: '0',
				segmentsEntryName: 'Anyone',
				segmentsExperienceId: '33591',
				segmentsExperienceName: 'Experience1',
				statusLabel: 'Inactive',
				url: 'url',
			},
		];

		await act(async () => renderRenderTimes({segments: newSegments}));

		expect(screen.getByText('Experience Default')).toBeInTheDocument();
	});
});
