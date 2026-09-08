import * as API from 'shared/api';
import CampaignDetail from '../CampaignDetail';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {MemoryRouter} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {render, waitFor} from '@testing-library/react';

jest.unmock('react-dom');

let params: Record<string, string> = {};

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => params,
}));

const fetchCampaign = API.campaigns.fetchCampaign as jest.Mock;

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
		fetchCampaign.mockReset();
		fetchCampaign.mockResolvedValue({
			campaignName: 'Multi-Cloud Solutions Guide',
			id: '7',
		});

		params = {channelId: '123', groupId: '23', id: '7'};
	});

	it('should ask the endpoint for the campaign the route names', async () => {
		renderCampaignDetail();

		await waitFor(() =>
			expect(fetchCampaign).toHaveBeenCalledWith(
				expect.objectContaining({
					channelId: '123',
					groupId: '23',
					id: '7',
				})
			)
		);
	});

	it('should title the screen with the campaign it fetched', async () => {
		const {findAllByText} = renderCampaignDetail();

		// The name is both the page title and the last breadcrumb.

		expect(
			(await findAllByText('Multi-Cloud Solutions Guide')).length
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

	it('should render no name while the campaign is still in flight', () => {
		const {queryByText} = renderCampaignDetail();

		// Asserted before awaiting, so this is the first paint. A placeholder
		// would appear twice, since the title doubles as the breadcrumb.

		expect(queryByText('Multi-Cloud Solutions Guide')).toBeNull();
	});
});
