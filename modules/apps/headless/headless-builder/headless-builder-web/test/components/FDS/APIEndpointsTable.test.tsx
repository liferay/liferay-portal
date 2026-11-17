/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import APIEndpointsTable from '../../../src/main/resources/META-INF/resources/js/components/FDS/APIEndpointsTable';

let mockScopeKey = 'company';

jest.mock('@liferay/frontend-data-set-web', () => {
	const {createElement} = jest.requireActual('react');

	return {
		FrontendDataSet: ({
			onActionDropdownItemClick,
		}: {
			onActionDropdownItemClick: (item: object) => void;
		}) =>
			createElement(
				'button',
				{
					onClick: () =>
						onActionDropdownItemClick({
							action: {id: 'copyEndpointURL'},
							itemData: {
								path: '/my-endpoint',
								scope: {key: mockScopeKey},
							},
							loadData: () => {},
						}),
				},
				'copy-url'
			),
	};
});

describe('APIEndpointsTable', () => {
	const renderComponent = () =>
		render(
			<APIEndpointsTable
				apiApplicationBaseURL="my-app"
				apiURLPaths={
					{endpoints: '/o/headless-builder/endpoints'} as APIURLPaths
				}
				basePath="/o/c/"
				currentAPIApplicationId="1"
				portletId="portletId"
				readOnly={false}
				setMainEndpointNav={jest.fn()}
			/>
		);

	afterEach(() => {
		jest.restoreAllMocks();

		mockScopeKey = 'company';
	});

	it('alerts when the URL of the endpoint cannot be copied', async () => {
		const user = userEvent.setup();

		jest.spyOn(navigator.clipboard, 'writeText').mockRejectedValue(
			new Error()
		);

		renderComponent();

		await user.click(screen.getByText('copy-url'));

		expect(
			await screen.findByText('failed-to-copy-to-clipboard')
		).toBeInTheDocument();
	});

	it('confirms that the URL of the endpoint was copied', async () => {
		const user = userEvent.setup();

		renderComponent();

		await user.click(screen.getByText('copy-url'));

		expect(
			await screen.findByText('copied-to-clipboard')
		).toBeInTheDocument();

		await waitFor(async () =>
			expect(await navigator.clipboard.readText()).toBe(
				`${window.location.origin}/o/c/my-app/my-endpoint`
			)
		);
	});

	it('copies the URL of a site scoped endpoint', async () => {
		const user = userEvent.setup();

		mockScopeKey = 'group';

		renderComponent();

		await user.click(screen.getByText('copy-url'));

		await waitFor(async () =>
			expect(await navigator.clipboard.readText()).toBe(
				`${window.location.origin}/o/c/my-app/scopes/group/my-endpoint`
			)
		);
	});
});
