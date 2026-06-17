/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import Integrations from '../../../js/integrations/Integrations';

jest.mock('@liferay/frontend-data-set-web', () => ({
	FrontendDataSet: jest.fn(() => <div data-testid="frontend-data-set" />),
}));

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const randomInt = () => Math.floor(Math.random() * 1_000_000);

const randomString = () => Math.random().toString(36).slice(2, 10);

type IntegrationsProps = React.ComponentProps<typeof Integrations>;

function renderIntegrations(props: Partial<IntegrationsProps> = {}) {
	return render(
		<Integrations
			fdsId={randomString()}
			integrationTypes={[]}
			integrationsURL={`/${randomString()}`}
			items={[]}
			itemsActions={[]}
			views={[]}
			{...props}
		/>
	);
}

beforeEach(() => {
	(FrontendDataSet as jest.Mock).mockClear();
	(openToast as jest.Mock).mockClear();

	(Liferay as any).Util = {
		fetch: jest.fn(),
	};

	delete (window as any).location;

	(window as any).location = {
		assign: jest.fn(),
		href: '',
		reload: jest.fn(),
	};
});

describe('Integrations', () => {
	it('renders the empty state when no items are provided', () => {
		renderIntegrations();

		expect(
			screen.getByText('no-integrations-have-been-added-yet')
		).toBeInTheDocument();
		expect(
			screen.getByText('add-your-first-integration')
		).toBeInTheDocument();

		expect(
			screen.queryByTestId('frontend-data-set')
		).not.toBeInTheDocument();
	});

	it('renders the data set when items are provided', () => {
		renderIntegrations({
			items: [{id: randomString(), name: randomString()}],
		});

		expect(screen.getByTestId('frontend-data-set')).toBeInTheDocument();

		expect(
			screen.queryByText('no-integrations-have-been-added-yet')
		).not.toBeInTheDocument();
	});

	it('renders the Add Integration trigger', () => {
		renderIntegrations();

		expect(
			screen.getByRole('button', {name: /add-integration/})
		).toBeInTheDocument();
	});

	it('renders an enabled Add Integration menu item for an unconfigured type', () => {
		const configurationURL = `/${randomString()}`;

		renderIntegrations({
			integrationTypes: [
				{
					configurationURL,
					disabled: false,
					id: randomString(),
					name: 'Display Name',
				},
			],
		});

		fireEvent.click(screen.getByRole('button', {name: /add-integration/}));

		const link = screen.getByRole('menuitem', {name: 'Display Name'});

		expect(link).toHaveAttribute('href', configurationURL);
	});

	it('renders a disabled Add Integration menu item for a configured type', () => {
		renderIntegrations({
			integrationTypes: [
				{
					configurationURL: `/${randomString()}`,
					disabled: true,
					id: randomString(),
					name: 'Display Name',
				},
			],
		});

		fireEvent.click(screen.getByRole('button', {name: /add-integration/}));

		const item = screen.getByRole('menuitem', {name: 'Display Name'});

		expect(item).not.toHaveAttribute('href');
	});

	it('navigates to the row configuration URL on Edit action click', () => {
		const configurationURL = `/${randomString()}`;

		renderIntegrations({
			items: [{configurationURL, id: randomString()}],
		});

		const fdsProps = (FrontendDataSet as jest.Mock).mock.calls[0][0];

		fdsProps.onActionDropdownItemClick({
			action: {data: {id: 'edit'}},
			itemData: {configurationURL},
		});

		expect(window.location.assign).toHaveBeenCalledWith(configurationURL);
	});

	it('deletes the integration row and reloads on Remove action click', async () => {
		const fetchMock = jest.fn().mockResolvedValue({ok: true});

		(Liferay as any).Util = {fetch: fetchMock};

		const integrationsURL = `/${randomString()}`;
		const itemId = randomInt();

		renderIntegrations({
			integrationsURL,
			items: [{id: itemId}],
		});

		const fdsProps = (FrontendDataSet as jest.Mock).mock.calls[0][0];

		fdsProps.onActionDropdownItemClick({
			action: {data: {id: 'remove'}},
			itemData: {id: itemId},
		});

		await waitFor(() => {
			expect(window.location.reload).toHaveBeenCalled();
		});

		expect(fetchMock).toHaveBeenCalledWith(`${integrationsURL}/${itemId}`, {
			method: 'DELETE',
		});

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'success'})
		);
	});

	it('shows an error toast when the Remove DELETE fails', async () => {
		const fetchMock = jest.fn().mockResolvedValue({ok: false});

		(Liferay as any).Util = {fetch: fetchMock};

		const itemId = randomInt();

		renderIntegrations({items: [{id: itemId}]});

		const fdsProps = (FrontendDataSet as jest.Mock).mock.calls[0][0];

		fdsProps.onActionDropdownItemClick({
			action: {data: {id: 'remove'}},
			itemData: {id: itemId},
		});

		await waitFor(() => {
			expect(openToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			);
		});

		expect(window.location.reload).not.toHaveBeenCalled();
	});
});
