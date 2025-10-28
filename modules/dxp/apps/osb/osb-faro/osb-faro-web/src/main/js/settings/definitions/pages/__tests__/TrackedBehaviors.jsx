import mockStore from 'test/mock-store';
import React from 'react';
import ReactDOM from 'react-dom';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';
import {TrackedBehaviors} from '../TrackedBehaviors';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

ReactDOM.createPortal = jest.fn();

describe('TrackedBehaviorsList', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<TrackedBehaviors groupId='23' />
				</StaticRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
