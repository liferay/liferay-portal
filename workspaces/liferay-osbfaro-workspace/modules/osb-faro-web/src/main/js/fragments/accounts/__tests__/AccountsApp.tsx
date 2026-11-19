jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {Account, AccountActivities, AccountProfile} from '../AccountsApp';

jest.unmock('react-dom');

describe('AccountsApp', () => {
	it('renders the route of Account', () => {
		render(<Account channelId="321" groupId="23" params={{id: '1'}} />);

		expect(
			screen.getByText('contacts/accounts/1/overview')
		).toBeInTheDocument();
	});

	it('renders the route of AccountActivities', () => {
		render(
			<AccountActivities
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText('contacts/accounts/1/activities')
		).toBeInTheDocument();
	});

	it('renders the route of AccountProfile', () => {
		render(
			<AccountProfile channelId="321" groupId="23" params={{id: '1'}} />
		);

		expect(
			screen.getByText('contacts/accounts/1/profile')
		).toBeInTheDocument();
	});
});
