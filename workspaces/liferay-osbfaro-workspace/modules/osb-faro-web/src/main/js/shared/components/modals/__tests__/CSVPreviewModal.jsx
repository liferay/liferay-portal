import * as API from 'shared/api';
import CSVPreviewModal from '../CSVPreviewModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {useModal} from '@clayui/modal';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const mockGroupId = '23';

const TestModal = props => {
	const {observer} = useModal({defaultOpen: true});

	return <CSVPreviewModal observer={observer} {...props} />;
};

const Wrapper = ({children}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23']}>
			<Routes>
				<Route element={children} path='/workspace/:groupId' />
			</Routes>
		</MemoryRouter>
	</Provider>
);

describe('CSVPreviewModal', () => {
	beforeEach(() => {
		API.dataSource.fetchFieldValues.mockReturnValue(Promise.resolve([]));
	});

	afterEach(cleanup);

	it('should render', async () => {
		render(
			<Wrapper>
				<MockedProvider
					cache={
						new InMemoryCache({
							addTypename: false
						})
					}
					mocks={[]}
				>
					<TestModal
						fileName='test'
						groupId={mockGroupId}
						id='test'
						name='test'
					/>
				</MockedProvider>
			</Wrapper>
		);

		await waitForModal();

		expect(document.body).toMatchSnapshot();
	});

	it('should render with a title on the heading', async () => {
		const {getByText} = render(
			<Wrapper>
				<MockedProvider
					cache={
						new InMemoryCache({
							addTypename: false
						})
					}
					mocks={[]}
				>
					<TestModal
						fileName='test'
						groupId={mockGroupId}
						id='test'
						name='CSV Test Title'
					/>
				</MockedProvider>
			</Wrapper>
		);

		await waitFor(() =>
			expect(
				getByText(/Data Preview "CSV Test Title"/i)
			).toBeInTheDocument()
		);
	});
});
