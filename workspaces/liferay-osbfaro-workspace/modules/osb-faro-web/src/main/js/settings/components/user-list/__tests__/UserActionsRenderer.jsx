import React from 'react';
import UserActionsRenderer from '../UserActionsRenderer';
import {render} from '@testing-library/react';
import {User} from 'shared/util/records';

jest.unmock('react-dom');

describe('UserActionsRenderer', () => {
	it('should render', () => {
		const {container} = render(
			<UserActionsRenderer currentUserId={1} data={new User({id: 2})} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with save and cancel buttons because editing is true', () => {
		const {findByText} = render(
			<UserActionsRenderer
				currentUserId={1}
				data={new User({id: 2})}
				editing
			/>
		);

		expect(findByText(/^save$/i)).toBeTruthy();
		expect(findByText(/^cancel$/i)).toBeTruthy();
	});

	it('should render without row actions for the current user', () => {
		const {queryAllByRole} = render(
			<UserActionsRenderer
				currentUserId={1}
				data={new User({id: 1})}
				editing
			/>
		);

		expect(queryAllByRole('button').length).toBe(0);
	});
});
