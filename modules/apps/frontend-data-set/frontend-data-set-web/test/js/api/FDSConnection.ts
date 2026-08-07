/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

// The connection reaches the data set state through the global registry the
// portal installs, so the tests run against the real implementation rather
// than a stand-in.

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import State from '../../../../../frontend-js/frontend-js-state-web/src/main/resources/META-INF/resources/main/State';
import {FDSConnection} from '../../../src/main/resources/META-INF/resources/js/api/FDSConnection';

import type {FDSState} from '@liferay/js-api/data-set';

const FDS_NAME = 'testDataSet';

const DECLARED_FILTERS = [
	{
		active: true,
		id: 'color',
		label: 'Color',
		odataFilterString: "color in ('Blue', 'Green')",
		type: 'selection',
	},
];

describe('FDSConnection filters', () => {
	let atom: Liferay.State.Atom<FDSState>;
	let connection: FDSConnection;
	let onSearch: jest.Mock;
	let onStatus: jest.Mock;

	const readState = () => State.read(atom as never) as unknown as FDSState;

	const connect = async () => {
		connection = new FDSConnection(FDS_NAME, {search: onSearch}, onStatus);

		await waitFor(() =>
			expect(onStatus).toHaveBeenCalledWith(
				expect.objectContaining({status: 'ready'})
			)
		);
	};

	beforeEach(() => {
		State.__internal__.reset();

		(Liferay.on as jest.Mock).mockReturnValue({detach: jest.fn()});

		atom = State.atom(`${FDS_NAME}_fdsState`, {
			filters: DECLARED_FILTERS,
			search: {query: ''},
		}) as never;

		onSearch = jest.fn();
		onStatus = jest.fn();
	});

	afterEach(() => {
		connection?.disconnect();

		(Liferay.on as jest.Mock).mockReset();
	});

	it('reports a type error when a connection writes the declared filters', () => {
		const fdsState: FDSState = {

			// @ts-expect-error TS2353: 'filters' does not exist in type 'FDSState'

			filters: [],
			search: {query: ''},
		};

		expect(fdsState.search.query).toBe('');
	});

	it('takes the filtering over when the consumer sets its own filters', async () => {
		await connect();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		expect(readState().connectionFilters).toEqual([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);
	});

	it('replaces the previous set on every call', async () => {
		await connect();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		connection.setFilters([
			{id: 'other', odataFilterString: "author eq 'joe'"},
		]);

		expect(readState().connectionFilters).toEqual([
			{id: 'other', odataFilterString: "author eq 'joe'"},
		]);
	});

	it('filters nothing when the consumer clears its filters', async () => {
		await connect();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		connection.clearFilters();

		expect(readState().connectionFilters).toEqual([]);
	});

	it('leaves nothing applied when a consumer that filtered disconnects', async () => {
		await connect();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		connection.disconnect();

		expect(readState().connectionFilters).toEqual([]);
	});

	it('keeps the declared filters in play when a consumer that never filtered disconnects', async () => {
		await connect();

		connection.disconnect();

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('ignores filter changes once disconnected', async () => {
		await connect();

		connection.disconnect();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		expect(readState().connectionFilters).toBeUndefined();
	});
});
