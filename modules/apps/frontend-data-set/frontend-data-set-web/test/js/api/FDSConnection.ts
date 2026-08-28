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

import type {
	FDSConnectionOptions,
	FDSState,
	FDSStateChangeCallback,
} from '@liferay/js-api/data-set';

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

const RESTORED_STATE = {selections: {color: ['Blue', 'Green']}};

describe('FDSConnection filters', () => {
	let atom: Liferay.State.Atom<FDSState>;
	let connection: FDSConnection;
	let onRestore: jest.Mock;
	let onSearch: jest.Mock;
	let onStatus: jest.Mock;

	const readState = () => State.read(atom as never) as unknown as FDSState;

	const offerRestoredConnectionState = (restoredConnectionState: unknown) =>
		State.write(
			atom as never,
			{
				...readState(),
				restoredConnectionState,
			} as never
		);

	const connect = async (
		options: FDSConnectionOptions = {},
		fdsStateChangeCallback: FDSStateChangeCallback = {
			restore: onRestore,
			search: onSearch,
		}
	) => {
		connection = new FDSConnection(
			FDS_NAME,
			fdsStateChangeCallback,
			onStatus,
			options
		);

		await waitFor(() =>
			expect(onStatus).toHaveBeenCalledWith(
				expect.objectContaining({status: 'ready'})
			)
		);
	};

	const connectOwningFilters = () => connect({owns: ['filters', 'search']});

	beforeEach(() => {
		State.__internal__.reset();

		(Liferay.on as jest.Mock).mockReturnValue({detach: jest.fn()});

		atom = State.atom(`${FDS_NAME}_fdsState`, {
			filters: DECLARED_FILTERS,
			search: {query: ''},
		}) as never;

		onRestore = jest.fn();
		onSearch = jest.fn();
		onStatus = jest.fn();

		jest.spyOn(console, 'warn').mockImplementation(() => {});
	});

	afterEach(() => {
		connection?.disconnect();

		(Liferay.on as jest.Mock).mockReset();

		jest.restoreAllMocks();
	});

	it('reports a type error when a connection writes the declared filters', () => {
		const fdsState: FDSState = {

			// @ts-expect-error TS2353: 'filters' does not exist in type 'FDSState'

			filters: [],
			search: {query: ''},
		};

		expect(fdsState.search.query).toBe('');
	});

	it('takes the filtering over as soon as a consumer that owns it connects', async () => {
		await connectOwningFilters();

		expect(readState().connectionFilters).toEqual([]);
	});

	it('leaves the filtering to the data set when a consumer only owns the search', async () => {
		await connect({owns: ['search']});

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('leaves the filtering to the data set when a consumer declares nothing', async () => {
		await connect();

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('applies the filters the consumer sets', async () => {
		await connectOwningFilters();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		expect(readState().connectionFilters).toEqual([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);
	});

	it('replaces the previous set on every call', async () => {
		await connectOwningFilters();

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
		await connectOwningFilters();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		connection.clearFilters();

		expect(readState().connectionFilters).toEqual([]);
	});

	it('ignores a consumer that filters without owning the filtering', async () => {
		await connect({owns: ['search']});

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('hands the filtering back when a consumer that owned it disconnects', async () => {
		await connectOwningFilters();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		connection.disconnect();

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('keeps the declared filters in play when a consumer that never owned the filtering disconnects', async () => {
		await connect({owns: ['search']});

		connection.disconnect();

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('ignores filter changes once disconnected', async () => {
		await connectOwningFilters();

		connection.disconnect();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		expect(readState().connectionFilters).toBeUndefined();
	});

	it('keeps the state a consumer asks it to remember, so the data set can put it in the URL', async () => {
		await connectOwningFilters();

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		expect(readState().connectionState).toEqual({
			selections: {color: ['Blue']},
		});
	});

	it('filters without remembering anything when the consumer passes no state', async () => {
		await connectOwningFilters();

		connection.setFilters([
			{id: 'color', odataFilterString: "color in ('Blue')"},
		]);

		expect(readState().connectionFilters).toEqual([
			{id: 'color', odataFilterString: "color in ('Blue')"},
		]);

		expect(readState().connectionState).toBeUndefined();
	});

	it('hands the state the data set restored to the consumer', async () => {
		offerRestoredConnectionState(RESTORED_STATE);

		await connectOwningFilters();

		expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE);
	});

	it('stops offering the restored state once the consumer has it', async () => {
		offerRestoredConnectionState(RESTORED_STATE);

		await connectOwningFilters();

		expect(readState().restoredConnectionState).toBeUndefined();
	});

	it('takes the filtering over with what the consumer restores, not with nothing', async () => {
		offerRestoredConnectionState(RESTORED_STATE);

		onRestore = jest.fn(() =>
			connection.setFilters(
				[
					{
						id: 'color',
						odataFilterString: "color in ('Blue', 'Green')",
					},
				],
				RESTORED_STATE
			)
		);

		await connectOwningFilters();

		expect(readState().connectionFilters).toEqual([
			{id: 'color', odataFilterString: "color in ('Blue', 'Green')"},
		]);
	});

	it('hands over state the data set restores after the connection is ready', async () => {
		await connectOwningFilters();

		onRestore.mockClear();

		offerRestoredConnectionState(RESTORED_STATE);

		await waitFor(() =>
			expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE)
		);

		expect(readState().restoredConnectionState).toBeUndefined();
	});

	it('hands over an empty restore, so that going back to an unfiltered address clears the filter UI', async () => {
		await connectOwningFilters();

		onRestore.mockClear();

		offerRestoredConnectionState(null);

		await waitFor(() => expect(onRestore).toHaveBeenCalledWith(null));
	});

	it('leaves the restored state alone for a consumer that only owns the search', async () => {
		offerRestoredConnectionState(RESTORED_STATE);

		await connect({owns: ['search']});

		expect(onRestore).not.toHaveBeenCalled();
		expect(readState().restoredConnectionState).toEqual(RESTORED_STATE);
	});

	it('warns and drops the restored state when the consumer cannot take it', async () => {
		offerRestoredConnectionState(RESTORED_STATE);

		await connect({owns: ['filters']}, {search: onSearch});

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining('Dropped the filters restored for')
		);

		expect(readState().restoredConnectionState).toBeUndefined();
	});
});
