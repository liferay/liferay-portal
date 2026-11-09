import * as API from 'shared/api';
import CampaignUtmParameters from '../CampaignUtmParameters';
import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

const fetchCampaignAcquisitionParameters = API.campaigns
	.fetchCampaignAcquisitionParameters as jest.Mock;

const renderComponent = () =>
	render(
		<MemoryRouter>
			<CampaignUtmParameters
				campaignId="7"
				channelId="123"
				groupId="23"
			/>
		</MemoryRouter>
	);

describe('CampaignUtmParameters', () => {
	beforeEach(() => {
		fetchCampaignAcquisitionParameters.mockReset();
		fetchCampaignAcquisitionParameters.mockResolvedValue([]);
	});

	it('should render the fetched parameters in the card', async () => {
		fetchCampaignAcquisitionParameters.mockResolvedValue([
			{
				fieldName: null,
				name: 'utm_region',
				standard: false,
				value: 'emea',
			},
			{
				fieldName: null,
				name: 'utm_source',
				standard: true,
				value: 'google',
			},
		]);

		renderComponent();

		fireEvent.click(
			await screen.findByRole('button', {name: /UTM Parameters/})
		);

		expect(screen.getByText('google')).toBeInTheDocument();
		expect(screen.getByText('utm_region')).toBeInTheDocument();
		expect(screen.getByText('emea')).toBeInTheDocument();
	});

	it('should load the parameters again from the error state', async () => {
		fetchCampaignAcquisitionParameters.mockRejectedValueOnce(
			new Error('Network error')
		);

		renderComponent();

		fireEvent.click(await screen.findByRole('button', {name: 'Reload'}));

		expect(
			await screen.findByRole('button', {name: /UTM Parameters/})
		).toBeInTheDocument();
		expect(fetchCampaignAcquisitionParameters).toHaveBeenCalledTimes(2);
	});
});
