import {Routes, toRoute} from 'shared/util/router';
import {buildHeaderSubtitle} from '../utils';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('buildHeaderSubtitle', () => {
	const context = {channelId: '123', groupId: 'liferay.com'};

	afterEach(cleanup);

	it('links the account name to its account page when an account id exists', () => {
		const individual = {
			accountName: 'Acme Corporation',
			accounts: [{accountName: 'Acme Corporation', id: 'acc-1'}],
			lastSessionCountry: 'Brazil',
			properties: {email: 'jane@acme.com'},
		};

		const {getByRole} = render(buildHeaderSubtitle(individual, context));

		const link = getByRole('link', {name: 'Acme Corporation'});

		expect(link.getAttribute('href')).toBe(
			toRoute(Routes.CONTACTS_ACCOUNT, {...context, id: 'acc-1'})
		);
	});

	it('renders the account name as plain text when there is no account id', () => {
		const individual = {
			accountName: 'Acme Corporation',
			accounts: [],
			lastSessionCountry: 'Brazil',
			properties: {email: 'jane@acme.com'},
		};

		const {container, queryByRole} = render(
			buildHeaderSubtitle(individual, context)
		);

		expect(queryByRole('link')).toBeNull();
		expect(container.textContent).toContain('Acme Corporation');
	});
});
