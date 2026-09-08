import CampaignDetail from '../CampaignDetail';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {MemoryRouter} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

let params: Record<string, string> = {};

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => params,
}));

const renderCampaignDetail = () =>
	render(
		<Provider store={mockStore()}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<MemoryRouter>
					<CampaignDetail />
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

describe('CampaignDetail', () => {
	beforeEach(() => {
		params = {channelId: '123', groupId: '23', id: '7'};
	});

	it('should title the screen with the campaign the route names', () => {
		const {getAllByText} = renderCampaignDetail();

		// The name is both the page title and the last breadcrumb.

		expect(
			getAllByText('Multi-Cloud Solutions Guide').length
		).toBeGreaterThan(0);
	});

	it('should trail the breadcrumb back through Campaigns', () => {
		const {container} = renderCampaignDetail();

		const hrefs = Array.from(
			container.querySelectorAll('.breadcrumb a'),
			(anchor) => anchor.getAttribute('href')
		);

		expect(hrefs).toContain('/workspace/23/123/campaigns');
	});

	it('should render nothing for a campaign the route does not name', () => {
		params = {channelId: '123', groupId: '23', id: 'nope'};

		const {queryByText} = renderCampaignDetail();

		expect(queryByText('Multi-Cloud Solutions Guide')).toBeNull();
	});
});
