jest.mock(
	'shared/components/workspaces/SuccessDisplay',
	() => () => 'SuccessDisplay'
);

import * as API from 'shared/api';
import checkProjectState from '../CheckProjectState';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const DefaultWrapper = ({children}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/']}>
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
							{children}
						</MockedProvider>
					}
					path='/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

const TestComponent = () => <div>{'component body'}</div>;

describe('SuccessDisplayIf', () => {
	afterEach(cleanup);

	it('should render the WrappedComponent', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='23' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the SuccessDisplay component if the project is not ready', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='24' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render a scheduled maintenance error page if the project state is "maintenance"', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='25' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render an unavailable error page if the project state is "unavailable"', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='26' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render an unavailable error page if the project state is "scheduled"', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='27' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render activating display if project state is "activating"', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='28' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render activating display if project state is "deactivated"', () => {
		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='29' />
			</DefaultWrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render an error page if there was an error fetching the project', async () => {
		API.projects.fetch.mockReturnValue(
			Promise.reject({message: 'foo rejection from server'})
		);

		const WrappedComponent = checkProjectState(TestComponent);

		const {container} = render(
			<DefaultWrapper>
				<WrappedComponent groupId='23' />
			</DefaultWrapper>
		);

		await waitFor(() => {});

		expect(container).toMatchSnapshot();
	});
});
