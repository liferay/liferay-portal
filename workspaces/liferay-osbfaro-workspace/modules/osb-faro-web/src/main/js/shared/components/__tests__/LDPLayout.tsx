jest.mock('shared/pages/ErrorPage', () => () => 'ErrorPage');

import LDPLayout, {LDPElement} from '../LDPLayout';
import mockStore, {mockStoreData, mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';

const renderAtLifecycle = (storeData = mockStoreData) =>
	render(
		<Provider store={mockStore(storeData)}>
			<MemoryRouter initialEntries={['/workspace/23/1/lifecycle']}>
				<RouterRoutes>
					<Route path="workspace/:groupId/*">
						<Route element={<LDPLayout />}>
							<Route
								element={
									<LDPElement fallback="standard page">
										{'LDP page'}
									</LDPElement>
								}
								path=":channelId?/lifecycle"
							/>
						</Route>
					</Route>
				</RouterRoutes>
			</MemoryRouter>
		</Provider>
	);

describe('LDPLayout', () => {
	it('renders the page on LDP plans', () => {
		renderAtLifecycle(mockStoreDataLDP);

		expect(screen.getByText('LDP page')).toBeInTheDocument();
	});

	it('renders the error page outside LDP plans', () => {
		renderAtLifecycle();

		expect(screen.getByText('ErrorPage')).toBeInTheDocument();
		expect(screen.queryByText('standard page')).not.toBeInTheDocument();
	});
});

describe('LDPElement', () => {
	const renderElement = (storeData = mockStoreData) =>
		render(
			<Provider store={mockStore(storeData)}>
				<MemoryRouter initialEntries={['/workspace/23']}>
					<RouterRoutes>
						<Route
							element={
								<LDPElement fallback="standard page">
									{'LDP page'}
								</LDPElement>
							}
							path="workspace/:groupId"
						/>
					</RouterRoutes>
				</MemoryRouter>
			</Provider>
		);

	it('renders its children on LDP plans', () => {
		renderElement(mockStoreDataLDP);

		expect(screen.getByText('LDP page')).toBeInTheDocument();
	});

	it('renders the fallback outside LDP plans', () => {
		renderElement();

		expect(screen.getByText('standard page')).toBeInTheDocument();
	});
});
