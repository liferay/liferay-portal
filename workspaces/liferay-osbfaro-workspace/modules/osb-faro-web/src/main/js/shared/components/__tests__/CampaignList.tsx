import CampaignList from '../CampaignList';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';

jest.unmock('react-dom');

const buildCampaigns = (total: number) =>
	Array.from({length: total}, (unused, index) => ({
		campaignId: `c${index}`,
		campaignName: `Campaign ${index}`,
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: `Person ${index}`,
				jobTitle: 'Plant Manager',
				status: 'Attended',
			},
		],
	}));

describe('CampaignList', () => {
	afterEach(cleanup);

	it('shows only the first page of campaigns', () => {
		const {container} = render(
			<CampaignList campaigns={buildCampaigns(9)} />
		);

		expect(container.querySelectorAll('.campaign-row')).toHaveLength(8);
		expect(screen.getByText('Campaign 0')).toBeInTheDocument();
		expect(screen.queryByText('Campaign 8')).not.toBeInTheDocument();
	});

	it('counts every campaign in the pager, not just the page', () => {
		const {container} = render(
			<CampaignList campaigns={buildCampaigns(9)} />
		);

		expect(
			container.querySelector('.pagination-results')
		).toHaveTextContent('9');
	});

	it('shows the remaining campaigns on the next page', () => {
		const {container} = render(
			<CampaignList campaigns={buildCampaigns(9)} />
		);

		const pager = container.querySelector(
			'.pagination-bar-root'
		) as HTMLElement;

		fireEvent.click(within(pager).getByText('2'));

		expect(container.querySelectorAll('.campaign-row')).toHaveLength(1);
		expect(screen.getByText('Campaign 8')).toBeInTheDocument();
	});

	it('leaves the pager out when a day holds no campaigns', () => {
		const {container} = render(<CampaignList campaigns={[]} />);

		expect(
			container.querySelector('.campaign-row')
		).not.toBeInTheDocument();
		expect(
			container.querySelector('.pagination-bar-root')
		).not.toBeInTheDocument();
	});
});
