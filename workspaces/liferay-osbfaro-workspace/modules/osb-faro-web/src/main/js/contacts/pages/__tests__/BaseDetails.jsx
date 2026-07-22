import * as data from 'test/data';
import BaseDetails from '../BaseDetails';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<MemoryRouter>
		<Provider store={mockStore()}>
			<BaseDetails
				dataSourceFn={() => Promise.resolve(data.mockAccountDetails())}
				groupId='23'
				id='test'
				{...props}
			/>
		</Provider>
	</MemoryRouter>
);

describe('BaseDetails', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render w/o loading', () => {
		const {container} = render(<DefaultComponent />);

		expect(container.querySelector('.loading-animation')).toBeTruthy();
	});

	it('should render w/ ErrorDisplay', async () => {
		const {container, queryByText} = render(
			<DefaultComponent dataSourceFn={() => Promise.reject({})} />
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Reload')).toBeTruthy();
	});
});
