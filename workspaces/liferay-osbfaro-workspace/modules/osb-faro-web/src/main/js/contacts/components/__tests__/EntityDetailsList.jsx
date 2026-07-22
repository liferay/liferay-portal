import * as data from 'test/data';
import EntityDetailsList from '../EntityDetailsList';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	groupId: '23',
	title: 'Test Test'
};

const DefaultComponent = props => (
	<MemoryRouter
		initialEntries={[
			'/workspace/23/321321/contacts/accounts/123123/interests/test'
		]}
	>
		<RouterRoutes>
			<Route
				element={
					<EntityDetailsList {...defaultProps} {...props} />
				}
				path={`${Routes.CONTACTS_ACCOUNT_INTEREST_DETAILS}/*`}
			/>
		</RouterRoutes>
	</MemoryRouter>
);

describe('EntityDetailsList', () => {
	it('should render', async () => {
		const {container} = render(
			<DefaultComponent
				demographicsIMap={fromJS(data.mockAccountDetails())}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should filter results by query', async () => {
		const {container, getByPlaceholderText} = render(
			<DefaultComponent
				demographicsIMap={fromJS(data.mockAccountDetails())}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		fireEvent.change(getByPlaceholderText('Search'), {
			target: {value: 'Agriculture'}
		});

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.subnav-tbar .tbar-item')
		).toHaveTextContent('1 Result for "Agriculture"');

		expect(container.querySelectorAll('table > tbody').length).toBe(1);
	});
});
