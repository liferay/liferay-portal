import CampaignRow from '../CampaignRow';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const CAMPAIGN = {
	campaignId: 'c3',
	campaignName: 'Q3 Manufacturing ABM',
	dataSourceType: 'salesforce',
	touches: [
		{
			individualId: 'ind-1',
			individualName: 'Michelle de Rue',
			jobTitle: 'VP of Operations',
			status: 'Registered',
		},
		{
			individualId: null,
			individualName: 'Marcus Vance',
			jobTitle: null,
			status: 'Attended',
		},
	],
};

const expand = (container: HTMLElement) =>
	fireEvent.click(
		container.querySelector('.campaign-row .row-main') as Element
	);

const renderRow = (campaign = CAMPAIGN, individualUrls = {}) =>
	render(
		<ul>
			<CampaignRow campaign={campaign} individualUrls={individualUrls} />
		</ul>
	);

describe('CampaignRow', () => {
	afterEach(cleanup);

	it('shows the campaign name and its data source', () => {
		renderRow();

		expect(screen.getByText('Q3 Manufacturing ABM')).toBeInTheDocument();
		expect(screen.getByText(/salesforce/i)).toBeInTheDocument();
	});

	it('counts the touches it holds', () => {
		const {container} = renderRow();

		expect(container.querySelector('.event-count-pill')).toHaveTextContent(
			'2'
		);
	});

	it('collapses its touches by default', () => {
		const {container} = renderRow();

		expect(container.querySelector('.touch-row')).not.toBeInTheDocument();
	});

	it('reveals one row per touch when expanded', () => {
		const {container} = renderRow();

		expand(container);

		expect(container.querySelectorAll('.touch-row')).toHaveLength(2);
		expect(screen.getByText('Michelle de Rue')).toBeInTheDocument();
		expect(screen.getByText('Marcus Vance')).toBeInTheDocument();
	});

	it('shows each touch job title and raw status', () => {
		const {container} = renderRow();

		expand(container);

		expect(screen.getByText('VP of Operations')).toBeInTheDocument();
		expect(screen.getByText('Registered')).toBeInTheDocument();
		expect(screen.getByText('Attended')).toBeInTheDocument();
	});

	it('omits the job title line when there is none', () => {
		const {container} = renderRow();

		expand(container);

		expect(container.querySelectorAll('.touch-job-title')).toHaveLength(1);
	});

	it('links a touch to its profile only when a url is known', () => {
		const {container} = renderRow(CAMPAIGN, {
			'ind-1': '/individuals/ind-1',
		});

		expand(container);

		expect(
			screen.getByText('Michelle de Rue').closest('a')
		).toHaveAttribute('href', '/individuals/ind-1');
		expect(screen.getByText('Marcus Vance').closest('a')).toBeNull();
	});

	it('collapses again when clicked a second time', () => {
		const {container} = renderRow();

		expand(container);
		expand(container);

		expect(container.querySelector('.touch-row')).not.toBeInTheDocument();
	});
});
