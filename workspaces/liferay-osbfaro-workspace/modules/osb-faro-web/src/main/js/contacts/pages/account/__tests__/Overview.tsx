import mockStore from 'test/mock-store';
import Overview from '../Overview';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '456',
		id: 'acc-1',
	}),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: () => ({data: {items: []}, loading: false}),
}));

const mockAccount = {
	accountName: 'IQVIA',
	accountType: 'Prospect',
	annualRevenue: 11359000000,
	country: 'United States',
	id: 'acc-1',
	industry: 'Business Services',
	lifecycleStage: 'ENGAGED',
};

const renderOverview = (props = {}) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<MockedProvider
					addTypename={false}
					mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
				>
					<Overview {...props} />
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	);

describe('Overview', () => {
	afterEach(cleanup);

	it('should render the account firmographics from the account', () => {
		renderOverview({account: mockAccount});

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.getByText('United States')).toBeInTheDocument();
		expect(screen.getByText('11.36B Revenue')).toBeInTheDocument();
		expect(screen.getByText('Business Services')).toBeInTheDocument();
		expect(screen.getByText('Lifecycle: Engaged')).toBeInTheDocument();
		expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
	});

	it('should render no lifecycle label when the account has none', () => {
		renderOverview({account: {...mockAccount, lifecycleStage: null}});

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.queryByText(/Lifecycle/)).not.toBeInTheDocument();
	});

	it('should render no account type label when the account has none', () => {
		renderOverview({account: {...mockAccount, accountType: ''}});

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.queryByText(/Type:/)).not.toBeInTheDocument();
	});

	it('should render the card without an account', () => {
		const {container} = renderOverview();

		expect(screen.getByText('ACCOUNT INFO')).toBeInTheDocument();
		expect(container.querySelectorAll('.label')).toHaveLength(0);
	});

	it('should render the Top Pages card under the engagement section', () => {
		renderOverview({account: mockAccount});

		expect(screen.getByText('ENGAGEMENT SUMMARY')).toBeInTheDocument();
		expect(screen.getByText('TOP PAGES')).toBeInTheDocument();
	});

	it('should render the Top Assets card in a half-width column', () => {
		const {container} = renderOverview({account: mockAccount});

		expect(screen.getByText('TOP ASSETS')).toBeInTheDocument();
		expect(
			container.querySelector('.col-xl-6 .top-assets')
		).toBeInTheDocument();
	});

	it('should render the Top Asset Categories and Tags card beside the Top Assets card', () => {
		const {container} = renderOverview({account: mockAccount});

		expect(
			screen.getByText('TOP ASSET CATEGORIES AND TAGS')
		).toBeInTheDocument();
		expect(
			container.querySelectorAll(
				'.col-xl-6 .top-assets, .col-xl-6 .top-categories-and-tags'
			)
		).toHaveLength(2);
	});

	it('should stretch both engagement cards so they stay aligned', () => {
		const {container} = renderOverview({account: mockAccount});

		expect(
			container.querySelectorAll(
				'.col-xl-6.d-flex.flex-column > .flex-grow-1.top-assets, .col-xl-6.d-flex.flex-column > .flex-grow-1.top-categories-and-tags'
			)
		).toHaveLength(2);
	});
});
