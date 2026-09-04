import Campaigns from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {MemoryRouter} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
	}),
}));

jest.mock('../../components/CampaignsDataSet', () => ({
	__esModule: true,
	default: () => <div data-testid="campaigns-data-set" />,
}));

jest.mock('../../components/OverviewSection', () => ({
	__esModule: true,
	default: () => <div data-testid="overview-section" />,
}));

const renderCampaigns = () =>
	render(
		<Provider store={mockStore()}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<MemoryRouter>
					<Campaigns />
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

describe('Campaigns', () => {
	it('should render the page title', () => {
		const {getByText} = renderCampaigns();

		expect(getByText('Campaigns')).toBeTruthy();
	});

	it('should render the channel breadcrumb', () => {
		const {container} = renderCampaigns();

		expect(container.querySelector('.breadcrumb')).toBeTruthy();
	});

	it('should render the overview section above the table', () => {
		const {getByTestId} = renderCampaigns();

		const overview = getByTestId('overview-section');
		const table = getByTestId('campaigns-data-set');

		expect(overview).toBeTruthy();
		expect(
			overview.compareDocumentPosition(table) &
				Node.DOCUMENT_POSITION_FOLLOWING
		).toBeTruthy();
	});

	it('should render the campaigns table', () => {
		const {getByTestId} = renderCampaigns();

		expect(getByTestId('campaigns-data-set')).toBeTruthy();
	});

	it('should state that the section covers all time, as secondary text', () => {
		const {getByText} = renderCampaigns();

		const allTime = getByText('All Time');

		expect(allTime).toBeTruthy();
		expect(allTime).toHaveClass('text-secondary');
	});
});
