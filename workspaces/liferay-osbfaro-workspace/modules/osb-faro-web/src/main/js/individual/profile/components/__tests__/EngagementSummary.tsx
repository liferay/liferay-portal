import EngagementSummary from '../EngagementSummary';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockPreferenceReq,
	mockSitesTopPagesReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	assets: {
		fetchIndividualTopAssets: jest.fn(() => Promise.resolve({items: []})),
	},
	categories: {
		fetchIndividualTopCategories: jest.fn(() =>
			Promise.resolve({items: []})
		),
	},
	tags: {
		fetchIndividualTopTags: jest.fn(() => Promise.resolve({items: []})),
	},
}));

const INDIVIDUAL_ID = 'ind-1';

const renderEngagementSummary = (
	props: {loading?: boolean; showEmptyState?: boolean} = {}
) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<MockedProvider
					addTypename={false}
					mocks={[
						mockTimeRangeReq(),
						mockPreferenceReq(),
						mockSitesTopPagesReq({
							individualId: INDIVIDUAL_ID,
						}),
					]}
				>
					<EngagementSummary
						channelId="123"
						groupId="456"
						individualId={INDIVIDUAL_ID}
						individualName="Jane Doe"
						{...props}
					>
						<div>{'No Individuals Data Synced'}</div>
					</EngagementSummary>
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	);

describe('EngagementSummary', () => {
	beforeEach(jest.clearAllMocks);

	afterEach(cleanup);

	it('should render the section header', () => {
		renderEngagementSummary();

		expect(screen.getByText('ENGAGEMENT SUMMARY')).toBeInTheDocument();
	});

	it('should render the three engagement cards', () => {
		renderEngagementSummary();

		expect(screen.getByText('TOP PAGES')).toBeInTheDocument();
		expect(screen.getByText('TOP ASSETS')).toBeInTheDocument();
		expect(
			screen.getByText('TOP ASSET CATEGORIES AND TAGS')
		).toBeInTheDocument();
	});

	describe('without a data source', () => {
		it('should replace the cards with the placeholder', () => {
			renderEngagementSummary({showEmptyState: true});

			expect(
				screen.getByText('No Individuals Data Synced')
			).toBeInTheDocument();
			expect(screen.queryByText('TOP PAGES')).toBeNull();
			expect(screen.queryByText('TOP ASSETS')).toBeNull();
			expect(
				screen.queryByText('TOP ASSET CATEGORIES AND TAGS')
			).toBeNull();
		});

		it('should keep the section header', () => {
			renderEngagementSummary({showEmptyState: true});

			expect(screen.getByText('ENGAGEMENT SUMMARY')).toBeInTheDocument();
		});

		it('should request nothing', () => {
			const API = jest.requireMock('shared/api');

			renderEngagementSummary({showEmptyState: true});

			expect(API.assets.fetchIndividualTopAssets).not.toHaveBeenCalled();
			expect(
				API.categories.fetchIndividualTopCategories
			).not.toHaveBeenCalled();
			expect(API.tags.fetchIndividualTopTags).not.toHaveBeenCalled();
		});

		it('should render the cards while the data sources are still loading', () => {
			renderEngagementSummary({loading: true, showEmptyState: true});

			expect(screen.getByText('TOP PAGES')).toBeInTheDocument();
			expect(screen.queryByText('No Individuals Data Synced')).toBeNull();
		});
	});
});
