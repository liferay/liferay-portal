import * as data from 'test/data';
import Details from '../Details';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Individual} from 'shared/util/records';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('IndividualDetails', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<MemoryRouter>
				<Provider store={mockStore()}>
					<Details
						groupId='23'
						id='test'
						individual={data.getImmutableMock(
							Individual,
							data.mockIndividual
						)}
					/>
				</Provider>
			</MemoryRouter>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
