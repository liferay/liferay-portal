import mockStore from 'test/mock-store';
import NewRuleModal from '../NewRuleModal';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationPageAssetsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {useModal} from '@clayui/modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const DefaultWrapper = ({children, mocks = []}) => (
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
							mocks={mocks}
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

const TestModal = props => {
	const {observer} = useModal({defaultOpen: true});

	return <NewRuleModal observer={observer} {...props} />;
};

describe('NewRuleModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		render(
			<DefaultWrapper mocks={[mockRecommendationPageAssetsReq([])]}>
				<TestModal />
			</DefaultWrapper>
		);

		await waitForModal();

		await waitForLoadingToBeRemoved(document.body);

		expect(document.body).toMatchSnapshot();
	});
});
