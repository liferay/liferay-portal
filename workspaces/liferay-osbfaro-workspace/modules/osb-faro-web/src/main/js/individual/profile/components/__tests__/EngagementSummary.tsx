import EngagementSummary from '../EngagementSummary';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockSitesTopPagesReq,
	mockPreferenceReq,
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

const renderEngagementSummary = () =>
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
					/>
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	);

describe('EngagementSummary', () => {
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
});
