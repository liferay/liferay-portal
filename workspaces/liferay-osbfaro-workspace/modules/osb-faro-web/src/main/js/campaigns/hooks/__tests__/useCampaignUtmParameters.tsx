import * as API from 'shared/api';
import React from 'react';
import {act, renderHook, waitFor} from '@testing-library/react';
import {useCampaignUtmParameters} from '../useCampaignUtmParameters';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

const fetchCampaignAcquisitionParameters = API.campaigns
	.fetchCampaignAcquisitionParameters as jest.Mock;

const parameter = (name: string, standard: boolean, value: string) => ({
	fieldName: null,
	name,
	standard,
	value,
});

const renderUseCampaignUtmParameters = (initialEntry = '/') =>
	renderHook(
		({campaignId}: {campaignId: string}) =>
			useCampaignUtmParameters({
				campaignId,
				channelId: '123',
				groupId: '23',
			}),
		{
			initialProps: {campaignId: '7'},
			wrapper: ({children}) => (
				<MemoryRouter initialEntries={[initialEntry]}>
					{children}
				</MemoryRouter>
			),
		}
	);

describe('useCampaignUtmParameters', () => {
	beforeEach(() => {
		fetchCampaignAcquisitionParameters.mockReset();
		fetchCampaignAcquisitionParameters.mockResolvedValue([]);
	});

	it('should ask for the campaign it names, within the selected range', async () => {
		renderUseCampaignUtmParameters('/?rangeKey=7');

		await waitFor(() =>
			expect(fetchCampaignAcquisitionParameters).toHaveBeenCalledWith({
				channelId: '123',
				groupId: '23',
				id: '7',
				rangeQueryValues: {rangeKey: '7'},
			})
		);
	});

	it('should send a custom range as its bounds alone', async () => {
		renderUseCampaignUtmParameters(
			'/?rangeEnd=2026-10-31&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);

		await waitFor(() =>
			expect(fetchCampaignAcquisitionParameters).toHaveBeenCalledWith(
				expect.objectContaining({
					rangeQueryValues: {
						rangeEnd: '2026-10-31',
						rangeStart: '2026-10-01',
					},
				})
			)
		);
	});

	it('should group the fetched values for the card', async () => {
		fetchCampaignAcquisitionParameters.mockResolvedValue([
			parameter('utm_region', false, 'emea'),
			parameter('utm_source', true, 'google'),
		]);

		const {result} = renderUseCampaignUtmParameters();

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current.customParameters).toEqual([
			{name: 'utm_region', values: ['emea']},
		]);
		expect(result.current.utmParameters).toEqual({utmSource: ['google']});
	});

	it('should refetch with the current variables, not the first ones', async () => {
		const {rerender, result} = renderUseCampaignUtmParameters();

		await waitFor(() =>
			expect(fetchCampaignAcquisitionParameters).toHaveBeenCalledTimes(1)
		);

		rerender({campaignId: '8'});

		await waitFor(() =>
			expect(fetchCampaignAcquisitionParameters).toHaveBeenCalledTimes(2)
		);

		act(() => result.current.refetch());

		await waitFor(() =>
			expect(fetchCampaignAcquisitionParameters).toHaveBeenCalledTimes(3)
		);

		expect(fetchCampaignAcquisitionParameters).toHaveBeenLastCalledWith(
			expect.objectContaining({id: '8'})
		);
	});
});
