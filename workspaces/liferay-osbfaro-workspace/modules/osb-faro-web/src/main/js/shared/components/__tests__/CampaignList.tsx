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
		origin: 'SALESFORCE',
		touches: [
			{
				individualId: null as string | null,
				individualName: `Person ${index}`,
				jobTitle: 'Plant Manager',
				status: 'Attended',
			},
		],
		touchesCount: 1,
	}));

const renderList = (props = {}) =>
	render(
		<CampaignList
			campaigns={buildCampaigns(8)}
			onDeltaChange={() => {}}
			onPageChange={() => {}}
			page={1}
			selectedDelta={8}
			totalItems={9}
			{...props}
		/>
	);

describe('CampaignList', () => {
	afterEach(cleanup);

	it('renders the page it was given without slicing it', () => {
		const {container} = renderList();

		expect(container.querySelectorAll('.campaign-row')).toHaveLength(8);
		expect(screen.getByText('Campaign 0')).toBeInTheDocument();
	});

	it('counts the day total in the pager, not the page it holds', () => {
		const {container} = renderList();

		expect(
			container.querySelector('.pagination-results')
		).toHaveTextContent('9');
	});

	it('asks for another page rather than paging in place', () => {
		const onPageChange = jest.fn();

		const {container} = renderList({onPageChange});

		const pager = container.querySelector(
			'.pagination-bar-root'
		) as HTMLElement;

		fireEvent.click(within(pager).getByText('2'));

		expect(onPageChange).toHaveBeenCalledWith(2);
		expect(container.querySelectorAll('.campaign-row')).toHaveLength(8);
	});

	it('counts what it pages as campaign entries', () => {
		const {container} = renderList();

		expect(
			container.querySelector('.pagination-results')
		).toHaveTextContent(/campaign entr/i);
	});

	it('links a touch whose individual it was given a route for', () => {
		const campaigns = buildCampaigns(1);

		campaigns[0].touches[0].individualId = 'ind-1';

		const {container} = renderList({
			campaigns,
			individualUrls: {
				'ind-1': '/workspace/liferay.com/1/individuals/ind-1',
			},
			totalItems: 1,
		});

		fireEvent.click(
			container.querySelector('.campaign-row .row-main') as HTMLElement
		);

		expect(screen.getByText('Person 0').closest('a')).toHaveAttribute(
			'href',
			'/workspace/liferay.com/1/individuals/ind-1'
		);
	});

	it('links each campaign it was given a route for', () => {
		renderList({
			campaigns: buildCampaigns(2),
			campaignUrls: {c0: '/campaigns/c0'},
			totalItems: 2,
		});

		expect(screen.getByText('Campaign 0').closest('a')).toHaveAttribute(
			'href',
			'/campaigns/c0'
		);
		expect(screen.getByText('Campaign 1').closest('a')).toBeNull();
	});

	it('leaves the pager out when a day holds no campaigns', () => {
		const {container} = renderList({campaigns: [], totalItems: 0});

		expect(
			container.querySelector('.campaign-row')
		).not.toBeInTheDocument();
		expect(
			container.querySelector('.pagination-bar-root')
		).not.toBeInTheDocument();
	});
});
