/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import PageSpeedCharts from '../../../js/pagespeed/PageSpeedCharts';

jest.mock('frontend-js-web', () => ({
	fetch: jest.fn(),
	sub: (template: string, ...args: string[]) =>
		template.replace(/\{(\d+)\}/g, (_match, index) => args[Number(index)]),
}));

const {fetch} = jest.requireMock('frontend-js-web');

const COMPLETE_RESULT = {
	accessibilityScore: 95,
	bestPracticesScore: 88,
	dateCreated: '2026-06-29T12:00:00Z',
	dateModified: '2026-06-29T12:00:00Z',
	pagesErrored: 0,
	pagesScanned: 10,
	pagesTotal: 10,
	performanceScore: 75,
	seoScore: 100,
};

describe('PageSpeedCharts', () => {
	beforeEach(() => {
		fetch.mockReset();
		fetch.mockResolvedValue({json: () => ({items: []}), ok: true});
	});

	it('shows the empty state when no result is available', async () => {
		render(<PageSpeedCharts />);

		await waitFor(() =>
			expect(
				screen.getByText('no-data-available-yet')
			).toBeInTheDocument()
		);
	});

	it('renders a gauge for performance, accessibility, best practices, and SEO', () => {
		render(<PageSpeedCharts initialResult={COMPLETE_RESULT} />);

		expect(screen.getByText('performance')).toBeInTheDocument();
		expect(screen.getByText('accessibility')).toBeInTheDocument();
		expect(screen.getByText('best-practices')).toBeInTheDocument();
		expect(screen.getByText('seo')).toBeInTheDocument();
	});

	it('shows the title above the gauges', () => {
		render(<PageSpeedCharts initialResult={COMPLETE_RESULT} />);

		expect(
			screen.getAllByText('google-pagespeed-metrics')[0]
		).toBeInTheDocument();
	});

	it('shows a formatted last-checked timestamp when dateCreated is present', () => {
		render(<PageSpeedCharts initialResult={COMPLETE_RESULT} />);

		expect(screen.getByText(/last-checked-x/)).toBeInTheDocument();
	});

	it('does not poll when the initial result is complete', async () => {
		render(<PageSpeedCharts initialResult={COMPLETE_RESULT} />);

		await new Promise((resolve) => setTimeout(resolve, 50));

		expect(fetch).not.toHaveBeenCalled();
	});

	it('polls for updates when the initial result is in progress', async () => {
		const inProgressResult = {
			...COMPLETE_RESULT,
			pagesScanned: 2,
			pagesTotal: 10,
		};

		fetch.mockResolvedValueOnce({
			json: () => ({items: [COMPLETE_RESULT]}),
			ok: true,
		});

		render(<PageSpeedCharts initialResult={inProgressResult} />);

		await waitFor(() => expect(fetch).toHaveBeenCalled());

		expect(fetch.mock.calls[0][0]).toContain(
			'/o/seo-studio/pagespeed-results'
		);
	});
});
