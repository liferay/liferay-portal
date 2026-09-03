import Campaigns from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {MemoryRouter} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(() => ({data: undefined, loading: false})),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
	}),
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
});
