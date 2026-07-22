import * as API from 'shared/api';
import CSVPreviewModal from '../CSVPreviewModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {noop} from 'lodash';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const mockGroupId = '23';

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
		const {container} = render(
			<Wrapper>
				<MockedProvider
					cache={
						new InMemoryCache({
							addTypename: false
						})
					}
					mocks={[]}
				>
					<CSVPreviewModal
						fileName='test'
						groupId={mockGroupId}
						id='test'
						name='test'
						onClose={noop}
					/>
				</MockedProvider>
			</Wrapper>
		);

		expect(container).toMatchSnapshot();
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
					<CSVPreviewModal
						fileName='test'
						groupId={mockGroupId}
						id='test'
						name='CSV Test Title'
						onClose={noop}
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
