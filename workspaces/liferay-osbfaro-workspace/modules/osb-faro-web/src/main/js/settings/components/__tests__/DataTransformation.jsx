import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataTransformation, processFieldMappings} from '../DataTransformation';
import {fromJS} from 'immutable';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockFieldMapping, mockMapping} from 'test/data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	groupId: '23',
	id: '123',
	onSubmit: jest.fn()
};

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/data-source/123']}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							cache={
								new InMemoryCache({
									addTypename: false,
									freezeResults: false
								})
							}
						>
							<DataTransformation {...defaultProps} {...props} />
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/data-source/:id/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('processFieldMappings', () => {
	it('should return fieldMappings', () => {
		const foo = 'foo';
		const bar = 'bar';
		const baz = 'baz';

		const inputValue = fromJS([
			{source: {name: foo}, suggestion: {}},
			{source: {name: bar}, suggestion: {}},
			{source: {name: baz}, suggestion: {}}
		]);

		const result = processFieldMappings(inputValue);

		expect(result.length).toEqual(3);

		expect(result).toMatchSnapshot();
	});
});

describe('DataTransformation', () => {
	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ the done button enabled', async () => {
		API.dataSource.fetchMappings.mockReturnValue(
			Promise.resolve([
				mockMapping('Matched Field', {
					suggestions: [mockFieldMapping()]
				})
			])
		);

		const {getByText} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(getByText('Done')).not.toBeDisabled();
	});

	it('should render w/ the done button disabled if there are duplicate CSV field mappings', async () => {
		API.dataSource.fetchMappings.mockReturnValue(
			Promise.resolve([
				mockMapping('Has default match 1', {
					suggestions: [
						mockFieldMapping(null, {name: 'foo'}),
						mockFieldMapping(null, {name: 'jack', value: 'dupe'})
					]
					// eslint-disable-next-line comma-dangle
				}),
				mockMapping('Has default match 2', {
					suggestions: [
						mockFieldMapping(null, {name: 'bar'}),
						mockFieldMapping(null, {name: 'jack', value: 'dupe'})
					]
					// eslint-disable-next-line comma-dangle
				}),
				mockMapping('No default match')
			])
		);

		const {getByText} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(getByText('Done')).toBeDisabled();
	});
});
