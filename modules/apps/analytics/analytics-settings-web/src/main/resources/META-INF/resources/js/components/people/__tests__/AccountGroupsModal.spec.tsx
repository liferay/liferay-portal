/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import '@testing-library/jest-dom';
import {useModal} from '@clayui/modal';
import {act, cleanup, render, screen, waitFor} from '@testing-library/react';
import React from 'react';
import ReactDOM from 'react-dom';

import {TEmptyState} from '../../table/StateRenderer';
import {TTableRequestParams} from '../../table/types';
import Modal from '../Modal';
import {EPeople} from '../People';

const responseWithData = {
	actions: {},
	facets: [],
	items: [
		{
			id: 10101,
			name: 'account groups test',
			selected: false,
		},
		{
			id: 21210,
			name: 'account groups test 2',
			selected: false,
		},
	],
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: 2,
};

const responseWithEmptyState = {
	actions: {},
	facets: [],
	items: [],
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: 0,
};

interface IComponentWithDataProps {
	requestFn: (params: TTableRequestParams) => Promise<any>;
}

interface IComponentWithEmptyStateProps {
	requestFn: (params: TTableRequestParams) => Promise<any>;
}

const ComponentWithData: React.FC<
	{children?: React.ReactNode | undefined} & IComponentWithDataProps
> = ({requestFn}) => {
	const {observer} = useModal({onClose: () => {}});

	const emptyState: TEmptyState = {
		contentRenderer: () => <></>,
		description: 'Empty State Description',
		noResultsTitle: 'Empty State No Results Title',
		title: 'Empty State Title',
	};

	return (
		<Modal
			columns={[
				{
					expanded: true,
					id: 'name',
					label: Liferay.Language.get('account-group'),
				},
			]}
			emptyState={emptyState}
			name={EPeople.UserGroupIds}
			observer={observer}
			onCloseModal={() => {}}
			requestFn={requestFn}
			syncAllAccounts
			syncAllContacts
			syncedIds={{
				syncedAccountGroupIds: [''],
				syncedOrganizationIds: [''],
				syncedUserGroupIds: [''],
			}}
			title="Assign Modal Title"
		/>
	);
};

const ComponentWithEmptyState: React.FC<
	{children?: React.ReactNode | undefined} & IComponentWithEmptyStateProps
> = ({requestFn}) => {
	const {observer} = useModal({onClose: () => {}});

	const emptyState: TEmptyState = {
		contentRenderer: () => <></>,
		description: 'Empty State Description',
		noResultsTitle: 'Empty State No Results Title',
		title: 'There are no account groups',
	};

	return (
		<Modal
			columns={[]}
			emptyState={emptyState}
			name={EPeople.UserGroupIds}
			observer={observer}
			onCloseModal={() => {}}
			requestFn={requestFn}
			syncAllAccounts
			syncAllContacts
			syncedIds={{
				syncedAccountGroupIds: [''],
				syncedOrganizationIds: [''],
				syncedUserGroupIds: [''],
			}}
			title="Add Account Groups"
		/>
	);
};

describe('Account Groups Modal', () => {
	beforeAll(() => {

		// @ts-ignore

		ReactDOM.createPortal = jest.fn((element) => {
			return element;
		});
		jest.useFakeTimers();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	afterEach(() => {
		jest.clearAllTimers();
		jest.restoreAllMocks();
		cleanup();
	});

	it('renders Account Groups modal without crashing it', async () => {
		fetch.mockResponse(JSON.stringify(responseWithData));

		await act(async () => {
			render(
				<ComponentWithData requestFn={async () => responseWithData} />
			);

			jest.runAllTimers();

			await waitFor(() => screen.getByText('account groups test'));

			await waitFor(() => screen.getByText('account groups test 2'));
		});

		const modalContent = document.querySelector('.modal-content');

		const tableColumnText = screen.getByText('account groups test');

		const tableColumnText2 = screen.getByText('account groups test 2');

		expect(modalContent).toBeInTheDocument();

		expect(tableColumnText).toBeInTheDocument();

		expect(tableColumnText2).toBeInTheDocument();
	});

	it('renders User Groups Modal with Empty State without crashing it', async () => {
		fetch.mockResponse(JSON.stringify(responseWithEmptyState));

		await act(async () => {
			render(
				<ComponentWithEmptyState
					requestFn={async () => responseWithEmptyState}
				/>
			);
			jest.runAllTimers();
		});

		const accountGroupsTitle = screen.getByText('Add Account Groups');

		const emptyStateTitle = screen.getByText('There are no account groups');

		const emptyStateDescription = screen.getByText(
			'Empty State Description'
		);

		expect(accountGroupsTitle).toBeInTheDocument();

		expect(emptyStateTitle).toBeInTheDocument();

		expect(emptyStateDescription).toBeInTheDocument();
	});
});
