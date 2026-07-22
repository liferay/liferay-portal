import HelpWidgetModal from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/settings']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider addTypename={false}>
							<HelpWidgetModal onClose={jest.fn()} {...props} />
						</MockedProvider>
					}
					path={`${Routes.SETTINGS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('HelpWidgetModal', () => {
	afterEach(cleanup);

	it('Should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
