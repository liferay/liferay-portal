import mockStore from 'test/mock-store';
import React from 'react';
import SearchCard from '../SearchCard';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor
} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockSearchStringListReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/definitions/search']}
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
							mocks={[mockSearchStringListReq()]}
						>
							<SearchCard groupId='23' {...props} />
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/definitions/search/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('SearchCard', () => {
	afterEach(cleanup);

	it('should render', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should have a default uneditable field with value of q', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		render(<WrappedComponent />);

		await waitForLoadingToBeRemoved();

		expect(screen.getByDisplayValue('q')).toBeDisabled();
	});

	it('should remove special characters on fields', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		render(<WrappedComponent />);

		await waitForLoadingToBeRemoved();

		const input = screen.getByDisplayValue('jackson');

		fireEvent.change(input, {target: {value: 'jackson@#!'}});
		fireEvent.blur(input);

		await waitFor(() => expect(input.value).toBe('jackson'));
	});

	it('should remove every character after equals sign', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		render(<WrappedComponent />);

		await waitForLoadingToBeRemoved();

		const input = screen.getByDisplayValue('jackson');

		fireEvent.change(input, {target: {value: 'jackson=testvalue'}});
		fireEvent.blur(input);

		await waitFor(() => expect(input.value).toBe('jackson'));
	});

	it('should render input as disabled when user is not admin', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => false}));

		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		container.querySelectorAll('.query-input input').forEach(el => {
			expect(el).toBeDisabled();
		});
	});

	it('should not render buttons when user is not admin', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => false}));

		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelectorAll('.query-card-root button')
		).toHaveLength(0);
	});
});
