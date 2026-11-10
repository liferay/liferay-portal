import * as API from 'shared/api';
import CampaignDropdown from '../CampaignDropdown';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {MemoryRouter, Route, Routes} from 'react-router-dom';

jest.unmock('react-dom');

const searchCampaignNames = API.campaigns.searchCampaignNames as jest.Mock;

const Wrapper = ({children}: {children: React.ReactNode}) => (
	<MemoryRouter initialEntries={['/workspace/123/456/individuals']}>
		<Routes>
			<Route
				element={children}
				path="/workspace/:groupId/:channelId/individuals"
			/>
		</Routes>
	</MemoryRouter>
);

// Opening the picker is what triggers the campaigns request.

const openPicker = () =>
	fireEvent.click(
		screen.getByRole('combobox', {name: 'Filter By Campaigns'})
	);

describe('CampaignDropdown', () => {
	afterEach(cleanup);

	beforeEach(() => {
		searchCampaignNames.mockReset();
		searchCampaignNames.mockReturnValue(
			Promise.resolve({
				items: [
					{id: '1', name: 'Spring Sale'},
					{id: '2', name: 'Black Friday'},
				],
				totalCount: 2,
			})
		);
	});

	it('should render with "All Campaigns" as the default value', () => {
		render(
			<Wrapper>
				<CampaignDropdown onFilterChange={jest.fn()} />
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Campaigns'})
		).toHaveTextContent('All Campaigns');
	});

	it('should list the campaigns of the channel when opened', async () => {
		render(
			<Wrapper>
				<CampaignDropdown onFilterChange={jest.fn()} />
			</Wrapper>
		);

		openPicker();

		await waitFor(() =>
			expect(searchCampaignNames).toHaveBeenCalledWith(
				expect.objectContaining({
					channelId: '456',
					groupId: '123',
					page: 1,
				})
			)
		);

		expect(
			await screen.findByRole('option', {name: 'Spring Sale'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Black Friday'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'All Campaigns'})
		).toBeInTheDocument();
	});

	it('should call onFilterChange with the selected campaign', async () => {
		const onFilterChange = jest.fn();

		render(
			<Wrapper>
				<CampaignDropdown onFilterChange={onFilterChange} />
			</Wrapper>
		);

		openPicker();

		fireEvent.click(
			await screen.findByRole('option', {name: 'Spring Sale'})
		);

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining({id: '1', name: 'Spring Sale'})
		);
	});

	it('should show the campaign the URL carries', () => {
		render(
			<Wrapper>
				<CampaignDropdown
					initialCampaignId="1"
					initialCampaignName="Spring Sale"
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Campaigns'})
		).toHaveTextContent('Spring Sale');
	});

	it('should fall back to the raw id when the name is missing', () => {
		render(
			<Wrapper>
				<CampaignDropdown
					initialCampaignId="1"
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Campaigns'})
		).toHaveTextContent('1');
	});
});
