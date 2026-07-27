import FilterPicker from '../FilterPicker';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {LifecycleContextProvider} from '../../context/LifecycleContext';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '456', groupId: '2000'}),
}));

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

describe('FilterPicker', () => {
	afterEach(cleanup);

	it('should render the loading state while the request is pending', () => {
		const useRequest = require('shared/hooks/useRequest');
		useRequest.useRequest = jest.fn(() => ({loading: true}));

		const {container} = renderFilter();

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
	});

	it('should render the "all-x" label when the filter is empty', () => {
		const useRequest = require('shared/hooks/useRequest');
		useRequest.useRequest = jest.fn(() => ({
			data: {items: ['Tech', 'Finance']},
			loading: false,
		}));

		const {getByText} = renderFilter();

		expect(getByText('All Industries')).toBeInTheDocument();
	});

	it('should expose an accessible name on the filter trigger', () => {
		const useRequest = require('shared/hooks/useRequest');
		useRequest.useRequest = jest.fn(() => ({
			data: {items: ['Tech', 'Finance']},
			loading: false,
		}));

		const {getByRole} = renderFilter();

		expect(
			getByRole('combobox', {name: 'Filter By Industries'})
		).toBeInTheDocument();
	});

	it('should constrain the dropdown menu width so long values do not overflow', () => {
		const useRequest = require('shared/hooks/useRequest');

		useRequest.useRequest = jest.fn(() => ({
			data: {
				items: [
					'A very very very long industry value that would overflow the filter',
				],
			},
			loading: false,
		}));

		const {baseElement, getByRole} = renderFilter();

		fireEvent.click(getByRole('combobox', {name: 'Filter By Industries'}));

		const menu = baseElement.querySelector('.dropdown-menu');

		expect(menu).toBeInTheDocument();
		expect(menu).toHaveStyle({maxWidth: 'none', width: '240px'});
	});

	it('should pass the fieldMappingFieldName through to the request', () => {
		const useRequest = require('shared/hooks/useRequest');
		const spy = jest.fn(() => ({data: {items: []}, loading: false}));
		useRequest.useRequest = spy;

		renderFilter({
			entityLabel: 'Countries',
			fieldMappingFieldName: 'country',
			filterKey: 'countryFilter',
		});

		expect(spy).toHaveBeenCalledWith(
			expect.objectContaining({
				variables: expect.objectContaining({
					channelId: '456',
					fieldMappingFieldName: 'country',
					groupId: '2000',
				}),
			})
		);
	});
});
