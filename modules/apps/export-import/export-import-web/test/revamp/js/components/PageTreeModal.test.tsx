/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import PageTreeModal from '../../../../src/main/resources/META-INF/resources/revamp/js/components/PageTreeModal';
import {mockPageTreeItems} from '../mocks/mockPageTreeItems';

jest.mock('staging-taglib', () => ({
	PagesTree: require('../mocks/MockPagesTree').MockPagesTree,
}));

const renderModal = (
	props: Partial<React.ComponentProps<typeof PageTreeModal>>
) =>
	render(
		<PageTreeModal
			groupId={20121}
			initialSelectedIds={[]}
			onClose={jest.fn()}
			onSubmit={jest.fn()}
			pageSize={20}
			privateLayout={false}
			{...props}
		/>
	);

describe('PageTreeModal', () => {
	beforeEach(() => {
		fetch.resetMocks();
	});

	it('titles the dialog for the export page unless told otherwise', async () => {
		fetch.mockResponse('[]');

		renderModal({});

		expect(await screen.findByText('pages-to-export')).toBeInTheDocument();
	});

	it('titles the dialog with the given title', async () => {
		fetch.mockResponse('[]');

		renderModal({title: 'pages-to-publish'});

		expect(await screen.findByText('pages-to-publish')).toBeInTheDocument();
	});

	it('emits the selected subset when not every page is checked', async () => {
		fetch
			.mockResponseOnce('[]')
			.mockResponseOnce('[]')
			.mockResponseOnce(
				JSON.stringify({
					hasMoreElements: false,
					items: mockPageTreeItems,
				})
			);

		const onSubmit = jest.fn();

		renderModal({initialSelectedIds: ['1'], onSubmit, privateLayout: true});

		await screen.findByLabelText('page-1');

		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		expect(onSubmit).toHaveBeenCalledWith({
			layoutIds: [1],
			privateLayout: true,
		});
	});

	it('emits no layoutIds when every page is checked', async () => {
		fetch
			.mockResponseOnce('[]')
			.mockResponseOnce('[]')
			.mockResponseOnce(
				JSON.stringify({
					hasMoreElements: false,
					items: mockPageTreeItems,
				})
			);

		const onSubmit = jest.fn();

		renderModal({initialSelectedIds: ['1', '2'], onSubmit});

		await screen.findByLabelText('page-1');

		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		expect(onSubmit).toHaveBeenCalledWith({privateLayout: false});
	});

	it('seeds the tree from the server when opened in the all state', async () => {
		fetch
			.mockResponseOnce('[]')
			.mockResponseOnce(JSON.stringify([1, 2]))
			.mockResponseOnce(
				JSON.stringify({
					hasMoreElements: false,
					items: mockPageTreeItems,
				})
			);

		const onSubmit = jest.fn();

		renderModal({initialAll: true, onSubmit});

		await screen.findByLabelText('page-1');

		const layoutCheckCall = fetch.mock.calls.find(([, init]) =>
			init?.body?.toString().includes('cmd=layoutCheck')
		);

		expect(layoutCheckCall?.[1]?.body?.toString()).toContain(
			'groupId=20121'
		);

		expect(screen.getByLabelText('page-1')).toBeChecked();
		expect(screen.getByLabelText('page-2')).toBeChecked();

		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		expect(onSubmit).toHaveBeenCalledWith({privateLayout: false});
	});
});
