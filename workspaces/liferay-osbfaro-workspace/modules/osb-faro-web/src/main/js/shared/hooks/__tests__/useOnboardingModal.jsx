import mockStore, {mockStoreData} from 'test/mock-store';
import React from 'react';
import SitesDashboardQuery from 'shared/queries/SitesDashboardQuery';
import {InMemoryCache, useQuery} from '@apollo/client';
import {mockDataSourcesReq} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {OnboardingContext} from 'shared/context/onboarding';
import {Provider} from 'react-redux';
import {renderHook, waitFor} from '@testing-library/react';
import {useOnboardingModal} from 'shared/hooks/useOnboardingModal';

jest.mock('shared/components/modals/onboarding-modal', () => ({
	__esModule: true,
	...jest.requireActual('shared/components/modals/onboarding-modal'),
	default: ({groupId}) => <div data-testid="onboarding-modal">{groupId}</div>,
}));

const getModal = result => result.current.modal;

const renderUseOnboardingModal = ({
	mocks = [mockDataSourcesReq()],
	onboardingTriggered = false,
	setOnboardingTriggered = jest.fn(),
	store = mockStore(),
} = {}) =>
	renderHook(
		() => {
			const modal = useOnboardingModal({groupId: '23'});

			return {
				modal,
				...useQuery(SitesDashboardQuery, {variables: {type: null}}),
			};
		},
		{
			wrapper: ({children}) => (
				<Provider store={store}>
					<OnboardingContext.Provider
						value={{onboardingTriggered, setOnboardingTriggered}}
					>
						<MockedProvider
							cache={new InMemoryCache({addTypename: false})}
							mocks={mocks}
						>
							{children}
						</MockedProvider>
					</OnboardingContext.Provider>
				</Provider>
			),
		}
	);

describe('useOnboardingModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		window.sessionStorage.clear();
	});

	it('returns the onboarding modal when the workspace has no data sources', async () => {
		const setOnboardingTriggered = jest.fn();

		const {result} = renderUseOnboardingModal({setOnboardingTriggered});

		await waitFor(() => expect(getModal(result)).not.toBeNull());

		expect(getModal(result).props.groupId).toBe('23');

		expect(setOnboardingTriggered).toHaveBeenCalled();
	});

	it('does not return the onboarding modal when the workspace has data sources', async () => {
		const setOnboardingTriggered = jest.fn();

		const {result} = renderUseOnboardingModal({
			mocks: [
				mockDataSourcesReq([
					{
						__typename: 'DataSource',
						id: '123',
						name: 'foo datasource',
						url: 'foo.url',
					},
				]),
			],
			setOnboardingTriggered,
		});

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(getModal(result)).toBeNull();
		expect(setOnboardingTriggered).not.toHaveBeenCalled();
	});

	it('does not return the onboarding modal for non-admin users', async () => {
		const {result} = renderUseOnboardingModal({
			store: mockStore(mockStoreData.setIn(['currentUser', 'data'], '24')),
		});

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(getModal(result)).toBeNull();
	});

	it('does not return the onboarding modal twice', async () => {
		const {result} = renderUseOnboardingModal({onboardingTriggered: true});

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(getModal(result)).toBeNull();
	});

	it('resumes the onboarding a previous page of the workspace left open', async () => {
		const onboardingState = {dxpConnected: true, groupId: '23', step: 3};

		window.sessionStorage.setItem(
			'onboardingState',
			JSON.stringify(onboardingState)
		);

		const {result} = renderUseOnboardingModal({onboardingTriggered: true});

		await waitFor(() => expect(getModal(result)).not.toBeNull());

		expect(getModal(result).props.initialState).toEqual(onboardingState);
	});

	it('does not resume the onboarding of another workspace', async () => {
		window.sessionStorage.setItem(
			'onboardingState',
			JSON.stringify({dxpConnected: true, groupId: '24', step: 3})
		);

		const {result} = renderUseOnboardingModal({onboardingTriggered: true});

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(getModal(result)).toBeNull();
	});
});
