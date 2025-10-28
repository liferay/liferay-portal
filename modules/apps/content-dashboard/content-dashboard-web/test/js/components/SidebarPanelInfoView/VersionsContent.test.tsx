/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {fetch, navigate} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import VersionsContent from '../../../../src/main/resources/META-INF/resources/js/components/SidebarPanelInfoView/VersionsContent';

jest.mock('frontend-js-web', () => ({
	fetch: jest.fn().mockReturnValue({
		json: jest.fn().mockReturnValue({
			versions: [
				{
					actions: [
						{
							icon: 'time',
							label: 'Expire',
							name: 'expire',
							type: 'SUBMIT_FORM',
							url: 'http://localhost:8080/expire-url',
						},
					],
					createDate: 'Thu Sep 29 11:22:34 GMT 2022',
					statusLabel: 'Approved',
					statusStyle: 'success',
					userName: 'Test Test',
					version: '2.1',
				},
				{
					actions: [
						{
							icon: 'time',
							label: 'Expire',
							name: 'expire',
							type: 'SUBMIT_FORM',
							url: 'http://localhost:8080/expire-url',
						},
					],
					changeLog: 'Log text',
					createDate: 'Thu Sep 30 11:22:34 GMT 2022',
					statusLabel: 'Approved',
					statusStyle: 'success',
					userName: 'Test Test',
					version: '2.2',
				},
				{
					actions: [],
					changeLog: 'Another log text',
					createDate: 'Thu Sep 30 12:22:34 GMT 2022',
					statusLabel: 'Expired',
					statusStyle: 'danger',
					userName: 'Test Test',
					version: '2.3',
				},
				{
					actions: [
						{
							icon: 'view',
							label: 'View',
							name: 'view',
							type: 'BLANK',
							url: 'http://localhost:8080/view-url',
						},
						{
							label: 'Compare to...',
							name: 'compare-versions',
							type: 'NAVIGATE',
							url: 'http://localhost:8080/navigate-url',
						},
					],
					changeLog: 'Another log text',
					createDate: 'Thu Sep 30 14:22:34 GMT 2022',
					statusLabel: 'Approved',
					statusStyle: 'success',
					userName: 'Test Test',
					version: '2.4',
				},
			],
			viewVersionsURL: 'http://localhost:8080/view-more-versions',
		}),
		ok: true,
	}),
	navigate: jest.fn(),
	sub: jest.fn(),
}));

window.open = jest.fn();
window.submitForm = jest.fn();

const fetchItemVersionsURL =
	'http://localhost:8080/fetch-manage-collaborators-button-url';

const _getComponent = (props = {}) => {
	return (
		<VersionsContent
			active={false}
			getItemVersionsURL={fetchItemVersionsURL}
			languageTag="en"
			onError={() => {}}
			{...props}
		/>
	);
};

describe('Versions Content component', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		cleanup();
	});

	it('does not call the endpoint on first render and calls it in the following render', async () => {
		const {rerender} = render(_getComponent());

		await waitFor(() => {
			expect(fetch).not.toHaveBeenCalledWith(fetchItemVersionsURL);
		});

		rerender(_getComponent({active: true}));

		await waitFor(() => {
			expect(fetch).toHaveBeenCalledWith(fetchItemVersionsURL);
		});
	});

	it('displays the versions list', async () => {
		const {getByText} = render(_getComponent({active: true}));

		await waitFor(() => {
			expect(getByText('2.1', {exact: false})).toBeInTheDocument();
			expect(getByText('no-change-log')).toBeInTheDocument();
			expect(getByText('2.2', {exact: false})).toBeInTheDocument();
			expect(getByText('Log text')).toBeInTheDocument();
		});
	});

	it('displays View more link if viewVersionsURL is provided', async () => {
		const {container} = render(_getComponent({active: true}));

		await waitFor(() => {
			const link = container.querySelector(
				'a.btn-secondary'
			) as HTMLAnchorElement | null;
			expect(link?.textContent).toBe('view-more');
			expect(link?.href).toBe('http://localhost:8080/view-more-versions');
		});
	});

	it('does not display View more link if no viewVersionsURL is provided', async () => {
		jest.mock('frontend-js-web', () => ({
			fetch: jest.fn().mockReturnValue({
				json: jest.fn().mockReturnValue({versions: []}),
				ok: true,
			}),
			sub: jest.fn(),
		}));

		const {container} = render(_getComponent({active: true}));

		await waitFor(() => {
			const link = container.querySelectorAll('a.btn-secondary');
			expect(link.length).toBe(0);
		});
	});
});

describe('Versions actions', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		cleanup();
	});

	it('are rendered within each version', async () => {
		const {getAllByText, getAllByTitle} = render(
			_getComponent({active: true})
		);

		await waitFor(() => {
			const expireActionButtons: HTMLElement[] = getAllByText('Expire');
			const viewActionButtons: HTMLElement[] = getAllByText('View');
			const compareActionButtons: HTMLElement[] =
				getAllByText('Compare to...');

			expect(getAllByTitle('actions').length).toBe(3);
			expect(expireActionButtons.length).toBe(2);
			expect(viewActionButtons.length).toBe(1);
			expect(compareActionButtons.length).toBe(1);

			const firstExpireButton: HTMLButtonElement =
				expireActionButtons[0].closest('button')!;

			fireEvent(
				firstExpireButton,
				new MouseEvent('click', {
					bubbles: true,
					cancelable: true,
				})
			);

			expect(window.submitForm).toHaveBeenCalledWith(
				undefined,
				'http://localhost:8080/expire-url'
			);

			const viewButton: HTMLButtonElement =
				viewActionButtons[0].closest('button')!;

			fireEvent(
				viewButton,
				new MouseEvent('click', {
					bubbles: true,
					cancelable: true,
				})
			);

			expect(window.open).toHaveBeenCalledWith(
				'http://localhost:8080/view-url',
				'_blank',
				'noopener'
			);

			const compareButton: HTMLButtonElement =
				compareActionButtons[0].closest('button')!;

			fireEvent(
				compareButton,
				new MouseEvent('click', {
					bubbles: true,
					cancelable: true,
				})
			);

			expect(navigate).toHaveBeenCalled();
		});
	});
});

declare global {
	interface Window {
		Liferay: any;
	}
}
