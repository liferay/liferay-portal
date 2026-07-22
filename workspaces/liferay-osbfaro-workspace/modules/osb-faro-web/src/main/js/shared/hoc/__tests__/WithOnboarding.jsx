import 'test/mock-modal';
import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import withOnboarding from '../WithOnboarding';
import {cleanup, render, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockDataSourcesReq} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {mockMemberUser} from 'test/data';
import {OnboardingContext} from 'shared/context/onboarding';
import {open} from 'shared/actions/modals';
import {Provider} from 'react-redux';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const WrappedComponent = withOnboarding(() => (
	<div>{'wrapped component text'}</div>
));

const defaultContext = {
	onboardingTriggered: false,
	setOnboardingTriggered: jest.fn()
};

const Wrapper = ({
	children,
	mocks = [mockDataSourcesReq()],
	onboardingContext = defaultContext,
	store = mockStore()
}) => (
	<Provider store={store}>
		<MemoryRouter initialEntries={['/']}>
			<RouterRoutes>
				<Route
					element={
						<OnboardingContext.Provider value={onboardingContext}>
							<MockedProvider
								cache={new InMemoryCache({addTypename: false})}
								mocks={mocks}
							>
								{children}
							</MockedProvider>
						</OnboardingContext.Provider>
					}
					path='/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('WithOnboarding', () => {
	afterEach(() => {
		cleanup();
		jest.clearAllMocks();
	});

	it('should render the wrapped component', async () => {
		const {container} = render(
			<Wrapper
				mocks={[
					mockDataSourcesReq([
						{
							__typename: 'DataSource',
							id: '123',
							name: 'foo datasource',
							url: 'foo.url'
						}
					])
				]}
			>
				<WrappedComponent />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.textContent).toBe('wrapped component text');
	});

	it('should trigger the onboarding modal if there are no dxp datasources', async () => {
		const {container} = render(
			<Wrapper>
				<WrappedComponent />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		await waitFor(() => expect(open).toBeCalled());
	});

	it('should not trigger the onboarding modal for non-admin users', async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(mockMemberUser('23'))
		);

		const {container} = render(
			<Wrapper>
				<WrappedComponent />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(open).not.toBeCalled();
	});

	it('should not trigger the onboarding modal if it has already been triggered', async () => {
		const {container} = render(
			<Wrapper
				onboardingContext={{
					...defaultContext,
					onboardingTriggered: true
				}}
			>
				<WrappedComponent />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(open).not.toBeCalled();
	});

	it('should not trigger the onboarding modal when a prop happens to match a field in the SitesDashboardQuery', async () => {
		const {container} = render(
			<Wrapper mocks={[mockDataSourcesReq([], {type: 'DYNAMIC'})]}>
				<WrappedComponent type='DYNAMIC' />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(open).not.toBeCalled();
	});
});
