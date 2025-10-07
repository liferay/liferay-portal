/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {
	ViewDashboardContextProvider,
	initialSpace,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/ViewDashboardContext';
import {ExpiredAssetsCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/ExpiredAssetsCard';

const assetsList = [
	{
		href: 'http://fakeurl.com',
		title: 'Understanding Quantum Computing for Beginners',
		usages: 1,
	},
	{
		href: 'http://fakeurl.com',
		title: 'A Guide to Sustainable Energy Solutions',
		usages: 8,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Top 10 Tips for Remote Work Productivity',
		usages: 5,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Biodiversity Loss: Why It Matters to the Corporate Bottom Line',
		usages: 4,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Leadership 4.0: Navigating the Age of Artificial Intelligence',
		usages: 2,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Organizational Culture in Transformation: The End of the Traditional Model?',
		usages: 16,
	},
	{
		href: 'http://fakeurl.com',
		title: 'The Impact of Gen Z on the Future of Corporations',
		usages: 8,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Corporate Sustainability: From Trend to Strategic Imperative',
		usages: 9,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Corporate Resilience: Lessons from the Last Economic Crisis',
		usages: 0,
	},
	{
		href: 'http://fakeurl.com',
		title: 'The War for Talent: How to Attract and Retain the Best Professionals',
		usages: 1,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Open Innovation: Collaborating with Startups to Grow',
		usages: 12,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Stakeholder Capitalism: Purpose Above Profit',
		usages: 32,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Fintechs and the Banking Sector: A Silent Revolution',
		usages: 11,
	},
	{
		href: 'http://fakeurl.com',
		title: 'The Power of Big Data in Decision Making',
		usages: 16,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Cybersecurity as a Competitive Advantage',
		usages: 8,
	},
	{
		href: 'http://fakeurl.com',
		title: 'The Future of Retail: Personalization and Customer Experience',
		usages: 9,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Diversity and Inclusion: More Than a Social Obligation',
		usages: 2,
	},
	{
		href: 'http://fakeurl.com',
		title: 'The Role of CFOs in the Age of Digitalization',
		usages: 6,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Mergers and Acquisitions: Strategies for Sustainable Growth',
		usages: 6,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Beyond Recycling: The Circular Economy in Practice',
		usages: 4,
	},
	{
		href: 'http://fakeurl.com',
		title: 'The Green Supply Chain: A New Era for Logistics',
		usages: 3,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Renewable Economy Tipping Point: From Niche to Mainstream',
		usages: 5,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Water Scarcity: A Business Risk and a Call for Innovation',
		usages: 7,
	},
	{
		href: 'http://fakeurl.com',
		title: 'Sustainable Urban Development: Building the Cities of Tomorrow',
		usages: 8,
	},
];

const mockData = {
	data: {
		items: assetsList,
		totalCount: 3,
	},
	error: null,
};

describe('[CMS Dashboard] ExpiredAssetsCard', () => {
	it('renders correctly', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue(mockData);

		const {getByText} = render(<ExpiredAssetsCard />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(getByText('EXPIRED-ASSETS')).toBeTruthy();
	});

	it('displays the number of usage for each item', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue(mockData);

		const {container} = render(<ExpiredAssetsCard />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		const items = container.querySelectorAll(
			'.cms-dashboard__expired-assets__item'
		);

		for (const [index, item] of items.entries()) {
			if (assetsList[index].usages === 1) {
				expect(item.textContent).toContain('x-usage');
			}
			else {
				expect(item.textContent).toContain('x-usages');
			}
		}
	});

	it('renders 20 items per page by default and paginates correctly', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValueOnce({
			data: {
				items: assetsList.slice(0, 20),
				totalCount: 24,
			},
			error: null,
		});

		const {container} = render(<ExpiredAssetsCard />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		let items = container.querySelectorAll(
			'.cms-dashboard__expired-assets__item'
		);

		expect(items.length).toBe(20);

		expect(screen.getByText('Showing 1 to 20 of 24')).toBeInTheDocument();

		expect(screen.getByText(assetsList[19].title)).toBeInTheDocument();

		expect(
			screen.queryByText(assetsList[20].title)
		).not.toBeInTheDocument();

		jest.spyOn(ApiHelper, 'get').mockResolvedValueOnce({
			data: {
				items: assetsList.slice(20, 24),
				totalCount: 24,
			},
			error: null,
		});

		const nextPageButton = screen.getByRole('button', {
			name: 'Go to the next page, 2',
		});

		fireEvent.click(nextPageButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		items = container.querySelectorAll(
			'.cms-dashboard__expired-assets__item'
		);

		expect(items.length).toBe(4);

		await waitFor(() => {
			expect(
				screen.getByText('Showing 21 to 24 of 24')
			).toBeInTheDocument();
		});

		expect(
			screen.queryByText(assetsList[19].title)
		).not.toBeInTheDocument();

		expect(screen.getByText(assetsList[20].title)).toBeInTheDocument();
	});

	it('renders modal view button for each asset', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue(mockData);

		render(<ExpiredAssetsCard />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		const viewModalButtons = screen.getAllByTestId('view-asset-button');

		expect(viewModalButtons.length).toBe(24);
	});

	it('renders EmptyStateCard when there are no expired assets', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [],
				totalCount: 0,
			},
			error: null,
		});

		render(<ExpiredAssetsCard />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByText('no-assets-expired-yet')).toBeInTheDocument();

		expect(
			screen.getByText('there-are-no-assets-expired-in-the-spaces')
		).toBeInTheDocument();

		const images = screen.getAllByRole('img');

		const emptyStateImage = images.find((image) =>
			image.getAttribute('src')?.includes('cms_empty_state.svg')
		);

		expect(emptyStateImage).toBeInTheDocument();
	});

	it('renders Clear Filters empty state when filters applied but no results', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [],
				totalCount: 0,
			},
			error: null,
		});

		const customValue = {
			filters: {
				language: {label: 'English', value: 'en_US'},
				space: initialSpace,
			},
		};

		render(
			<ViewDashboardContextProvider value={customValue}>
				<ExpiredAssetsCard />
			</ViewDashboardContextProvider>
		);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByText('no-results-found')).toBeInTheDocument();
		expect(
			screen.getByText('sorry,-no-results-were-found')
		).toBeInTheDocument();

		expect(screen.getByText('clear-filters')).toBeInTheDocument();

		const images = screen.getAllByRole('img');
		const emptyStateImage = images.find((image) =>
			image.getAttribute('src')?.includes('search_state.svg')
		);

		expect(emptyStateImage).toBeInTheDocument();
	});
});
