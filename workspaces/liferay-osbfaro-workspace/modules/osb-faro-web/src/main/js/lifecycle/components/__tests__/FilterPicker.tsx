import * as API from 'shared/api';
import FilterPicker from '../FilterPicker';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {LifecycleContextProvider} from '../../context/LifecycleContext';
import {
	mockListGeometry,
	mockPaginatedFieldValues,
	scrollListToBottom,
} from 'test/infinite-scroll';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '456', groupId: '2000'}),
}));

const fetchFieldValues = API.accounts.fetchFieldValues as jest.Mock;

const renderFilter = (props = {}) =>
	render(
		<LifecycleContextProvider lifecycleId="1">
			<FilterPicker
				entityLabel="Industries"
				fieldMappingFieldName="industry"
				filterKey="industryFilter"
				{...props}
			/>
		</LifecycleContextProvider>
	);

const getTrigger = (name = 'Filter By Industries') =>
	screen.getByRole('combobox', {name});

describe('FilterPicker', () => {
	afterEach(cleanup);

	beforeEach(() => {
		fetchFieldValues.mockReset();
		fetchFieldValues.mockResolvedValue({items: ['Tech', 'Finance']});
	});

	it('should not request the field values until the picker is opened', () => {
		renderFilter();

		expect(fetchFieldValues).not.toHaveBeenCalled();
	});

	it('should render the loading state while the request is pending', async () => {
		let resolveRequest: (value: unknown) => void = () => {};

		fetchFieldValues.mockImplementation(
			() =>
				new Promise((resolve) => {
					resolveRequest = resolve;
				})
		);

		const {container} = renderFilter();

		fireEvent.click(getTrigger());

		await waitFor(() => expect(fetchFieldValues).toHaveBeenCalled());

		expect(container.querySelector('.loading-root')).toBeInTheDocument();

		resolveRequest({items: ['Tech']});

		await waitFor(() =>
			expect(
				container.querySelector('.loading-root')
			).not.toBeInTheDocument()
		);
	});

	it('should render the "all-x" label when the filter is empty', () => {
		renderFilter();

		expect(screen.getByText('All Industries')).toBeInTheDocument();
	});

	it('should expose an accessible name on the filter trigger', () => {
		renderFilter();

		expect(getTrigger()).toBeInTheDocument();
	});

	it('should list the fetched field values as options', async () => {
		renderFilter();

		fireEvent.click(getTrigger());

		expect(
			await screen.findByRole('option', {name: 'Tech'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: /^finance$/i})
		).toBeInTheDocument();
	});

	it('should constrain the dropdown menu width so long values do not overflow', async () => {
		fetchFieldValues.mockResolvedValue({
			items: [
				'A very very very long industry value that would overflow the filter',
			],
		});

		const {baseElement} = renderFilter();

		fireEvent.click(getTrigger());

		// The menu is held back until the options land.

		await waitFor(() =>
			expect(
				baseElement.querySelector('.dropdown-menu')
			).toBeInTheDocument()
		);

		const menu = baseElement.querySelector('.dropdown-menu');

		expect(menu).toHaveStyle({maxWidth: 'none', width: '240px'});
	});

	it('should pass the fieldMappingFieldName through to the request', async () => {
		renderFilter({
			entityLabel: 'Countries',
			fieldMappingFieldName: 'country',
			filterKey: 'countryFilter',
		});

		fireEvent.click(getTrigger('Filter By Countries'));

		await waitFor(() =>
			expect(fetchFieldValues).toHaveBeenCalledWith(
				expect.objectContaining({
					channelId: '456',
					fieldMappingFieldName: 'country',
					groupId: '2000',
				})
			)
		);
	});

	it('should load the next page of field values as the list is scrolled', async () => {
		const restoreListGeometry = mockListGeometry();

		fetchFieldValues.mockImplementation(mockPaginatedFieldValues(100));

		renderFilter();

		fireEvent.click(getTrigger());

		await screen.findByRole('option', {name: 'Value 0'});

		scrollListToBottom();

		expect(
			await screen.findByRole('option', {name: 'Value 20'})
		).toBeInTheDocument();

		expect(fetchFieldValues).toHaveBeenLastCalledWith(
			expect.objectContaining({delta: 20, page: 2, query: ''})
		);

		restoreListGeometry();
	});
});
