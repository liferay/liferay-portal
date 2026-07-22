jest.mock('shared/actions/modals', () => ({
	actionTypes: {},
	close: jest.fn(),
	modalTypes: {},
	open: jest.fn(() => ({meta: {}, payload: {}, type: 'open'}))
}));

import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import NoPropertiesAvailable from '../NoPropertiesAvailable';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {getImmutableMock, mockMemberUser, mockUser} from 'test/data';
import {open} from 'shared/actions/modals';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';
import {User} from 'shared/util/records';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<NoPropertiesAvailable
				currentUser={getImmutableMock(User, mockUser)}
				groupId='123123'
				{...props}
			/>
		</MemoryRouter>
	</Provider>
);

describe('NoPropertiesAvailable', () => {
	afterEach(cleanup);

	it('should render', async () => {
		API.dataSource.search.mockReturnValueOnce(Promise.resolve({total: 0}));

		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should not display the "Start" button to trigger onboarding if the user is not an AC admin', () => {
		const {queryByRole} = render(
			<DefaultComponent
				currentUser={getImmutableMock(User, mockMemberUser)}
			/>
		);

		jest.runAllTimers();

		expect(queryByRole('button')).toBeNull();
	});

	it('should call open on "Start" click', async () => {
		API.dataSource.search.mockReturnValueOnce(Promise.resolve({total: 0}));

		const {container, queryByText} = render(
			<DefaultComponent open={open} />
		);

		expect(open).not.toBeCalled();

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(queryByText('Start'));

		expect(open).toBeCalled();
	});
});
