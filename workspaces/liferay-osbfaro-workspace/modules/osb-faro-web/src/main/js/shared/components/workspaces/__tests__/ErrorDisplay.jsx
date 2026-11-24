import mockStore from 'test/mock-store';
import React from 'react';
import WorkspacesErrorDisplay from '../ErrorDisplay';
import {BrowserRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {User} from 'shared/util/records';

describe('WorkspacesErrorDisplay', () => {
	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<WorkspacesErrorDisplay
						currentUser={
							new User({emailAddress: 'test@liferay.com'})
						}
					/>
				</BrowserRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
