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
	FDSConnectionStatus,
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

const CONNECTION_ID = 'sampleCustomElement';

const OTHER_CONNECTION_ID = 'otherCustomElement';

const RESTORED_STATE = {selections: {color: ['Blue', 'Green']}};

// What the URL holds is keyed by connection, and what a consumer is handed
// back is only its own slice.

const RESTORED_STATE_MAP = {[CONNECTION_ID]: RESTORED_STATE};

describe('FDSConnection filters', () => {
	let atom: Liferay.State.Atom<FDSState>;
	let connection: FDSConnection;
	let connections: Array<FDSConnection>;
	let onRestore: jest.Mock;
	let onSearch: jest.Mock;
	let onStatus: jest.Mock;
	let openToast: jest.Mock;

	const readState = () => State.read(atom as never) as unknown as FDSState;

	const offerRestoredConnectionState = (
		restoredConnectionState: Record<string, unknown> | null
	) =>
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
		},
		onFDSConnectionInfoChange: jest.Mock = onStatus,
		settledStatus: FDSConnectionStatus = 'ready'
	) => {
		connection = new FDSConnection(
			FDS_NAME,
			fdsStateChangeCallback,
			onFDSConnectionInfoChange,
			options
		);

		connections.push(connection);

		await waitFor(() =>
			expect(onFDSConnectionInfoChange).toHaveBeenCalledWith(
				expect.objectContaining({status: settledStatus})
			)
		);

		return connection;
	};

	const connectOwningFilters = () =>
		connect({appId: CONNECTION_ID, owns: ['filters', 'search']});

	// A second consumer asking for the filtering the first one already holds,
	// reported through its own callback so that the two do not mix.

	const connectSecondOwningFilters = async (
		settledStatus: FDSConnectionStatus = 'refused'
	) => {
		const onSecondRestore = jest.fn();
		const onSecondStatus = jest.fn();

		const secondConnection = await connect(
			{appId: OTHER_CONNECTION_ID, owns: ['filters', 'search']},
			{restore: onSecondRestore, search: onSearch},
			onSecondStatus,
			settledStatus
		);

		return {onSecondRestore, onSecondStatus, secondConnection};
	};

	beforeEach(() => {
		State.__internal__.reset();

		(Liferay.on as jest.Mock).mockReturnValue({detach: jest.fn()});

		atom = State.atom(`${FDS_NAME}_fdsState`, {
			filters: DECLARED_FILTERS,
			search: {query: ''},
		}) as never;

		connections = [];
		onRestore = jest.fn();
		onSearch = jest.fn();
		onStatus = jest.fn();
		openToast = jest.fn();

		// What a refused consumer reports to the person looking at the page
		// goes through the portal's own toast, so the test stands in for it.

		Liferay.Util = {...Liferay.Util, openToast} as never;
		Liferay.Language = {
			...Liferay.Language,
			get: (key: string) => key,
		} as never;

		jest.spyOn(console, 'error').mockImplementation(() => {});
		jest.spyOn(console, 'warn').mockImplementation(() => {});
	});

	afterEach(() => {
		connections.forEach((openConnection) => openConnection.disconnect());

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
		expect(readState().connectionState).toBeUndefined();
	});

	// Only one consumer can own the filtering today, so no second key can be
	// in play for it to leave behind. Seeding one keeps releasing honest for
	// the day it can, and matches what taking a restored key already does.

	it('takes only its own state out when a consumer that owned the filtering disconnects', async () => {
		await connectOwningFilters();

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		const otherConnectionState = {selections: {size: ['Big']}};

		State.write(
			atom as never,
			{
				...readState(),
				connectionState: {
					...readState().connectionState,
					[OTHER_CONNECTION_ID]: otherConnectionState,
				},
			} as never
		);

		connection.disconnect();

		expect(readState().connectionState).toEqual({
			[OTHER_CONNECTION_ID]: otherConnectionState,
		});
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

	it('keeps the state a consumer asks it to remember under the id of its connection, so the data set can put it in the URL', async () => {
		await connectOwningFilters();

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		expect(readState().connectionState).toEqual({
			[CONNECTION_ID]: {selections: {color: ['Blue']}},
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
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connectOwningFilters();

		expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE);
	});

	it('stops offering the restored state once the consumer has it', async () => {
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connectOwningFilters();

		expect(readState().restoredConnectionState).toBeUndefined();
	});

	it('takes the filtering over with what the consumer restores, not with nothing', async () => {
		offerRestoredConnectionState(RESTORED_STATE_MAP);

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

		offerRestoredConnectionState(RESTORED_STATE_MAP);

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
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connect({owns: ['search']});

		expect(onRestore).not.toHaveBeenCalled();
		expect(readState().restoredConnectionState).toEqual(RESTORED_STATE_MAP);
	});

	it('warns and drops the restored state when the consumer cannot take it', async () => {
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connect(
			{appId: CONNECTION_ID, owns: ['filters']},
			{search: onSearch}
		);

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining('Dropped the filters restored for')
		);

		expect(readState().restoredConnectionState).toBeUndefined();
	});

	it('refuses the filtering to a second consumer that asks for it', async () => {
		await connectOwningFilters();

		const {onSecondStatus} = await connectSecondOwningFilters();

		expect(onSecondStatus).toHaveBeenCalledWith(
			expect.objectContaining({status: 'refused'})
		);
	});

	// A consumer that gates its controls on the ready status then offers none
	// it cannot back up, which is what keeps a client extension free of code
	// for this case.

	it('never reports itself ready to a consumer it refused the filtering to', async () => {
		await connectOwningFilters();

		const {onSecondStatus} = await connectSecondOwningFilters();

		expect(onSecondStatus).not.toHaveBeenCalledWith(
			expect.objectContaining({status: 'ready'})
		);
	});

	it('reports itself ready to a consumer that only ever wanted the search', async () => {
		await connectOwningFilters();

		const onSearchOnlyStatus = jest.fn();

		await connect(
			{owns: ['search']},
			{search: onSearch},
			onSearchOnlyStatus
		);

		expect(onSearchOnlyStatus).toHaveBeenCalledWith(
			expect.objectContaining({status: 'ready'})
		);
	});

	it('warns in the console when the filtering is already taken', async () => {
		await connectOwningFilters();

		await connectSecondOwningFilters();

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining('Refused the filtering of')
		);
	});

	it('warns on the page when the filtering is already taken, since the console is not where anyone is looking', async () => {
		await connectOwningFilters();

		await connectSecondOwningFilters();

		expect(openToast).toHaveBeenCalledWith({
			message: 'another-widget-is-already-filtering-this-data-set',
			type: 'warning',
		});
	});

	it('says nothing on the page to the consumer that owns the filtering', async () => {
		await connectOwningFilters();

		expect(openToast).not.toHaveBeenCalled();
	});

	it('says nothing on the page when the filtering is refused for want of an appId, which no user can act on', async () => {
		await connect(
			{owns: ['filters', 'search']},
			undefined,
			onStatus,
			'refused'
		);

		expect(openToast).not.toHaveBeenCalled();

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining(
				'connect with an appId to own the filtering'
			)
		);
	});

	it('keeps the filters of the consumer that owns them when a refused one connects', async () => {
		await connectOwningFilters();

		connection.setFilters([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);

		await connectSecondOwningFilters();

		expect(readState().connectionFilters).toEqual([
			{id: 'custom', odataFilterString: "status eq 'draft'"},
		]);
	});

	it('ignores a refused consumer that filters anyway, and says why', async () => {
		await connectOwningFilters();

		const {secondConnection} = await connectSecondOwningFilters();

		secondConnection.setFilters([
			{id: 'other', odataFilterString: "author eq 'joe'"},
		]);

		expect(readState().connectionFilters).toEqual([]);

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining('another connection owns the filtering')
		);
	});

	it('leaves the filtering taken over when a refused consumer disconnects', async () => {
		await connectOwningFilters();

		const {secondConnection} = await connectSecondOwningFilters();

		secondConnection.disconnect();

		expect(readState().connectionFilters).toEqual([]);
	});

	it('leaves the state the data set restores to the consumer that owns the filtering', async () => {
		await connectOwningFilters();

		const {onSecondRestore} = await connectSecondOwningFilters();

		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await waitFor(() =>
			expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE)
		);

		expect(onSecondRestore).not.toHaveBeenCalled();
	});

	it('leaves the state the URL carries to the consumer that owns the filtering', async () => {
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connectOwningFilters();

		const {onSecondRestore} = await connectSecondOwningFilters();

		expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE);
		expect(onSecondRestore).not.toHaveBeenCalled();
	});

	it('hands a consumer its own slice of what the URL carries', async () => {
		offerRestoredConnectionState({
			[CONNECTION_ID]: RESTORED_STATE,
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});

		await connectOwningFilters();

		expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE);
	});

	it('leaves the keys of other connections on offer when it takes its own', async () => {
		const otherConnectionState = {selections: {size: ['Big']}};

		offerRestoredConnectionState({
			[CONNECTION_ID]: RESTORED_STATE,
			[OTHER_CONNECTION_ID]: otherConnectionState,
		});

		await connectOwningFilters();

		expect(onRestore).toHaveBeenCalledWith(RESTORED_STATE);

		expect(readState().restoredConnectionState).toEqual({
			[OTHER_CONNECTION_ID]: otherConnectionState,
		});
	});

	it('stops offering anything once the last key has been taken', async () => {
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connectOwningFilters();

		expect(readState().restoredConnectionState).toBeUndefined();
	});

	it('says nothing to a consumer when what is left on offer holds no key of its own', async () => {
		await connectOwningFilters();

		onRestore.mockClear();

		offerRestoredConnectionState({
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});

		// Waiting on the search callback, which every write reaches, proves
		// the write was seen rather than merely not handled yet.

		await waitFor(() => expect(onSearch).toHaveBeenCalled());

		expect(onRestore).not.toHaveBeenCalled();

		expect(readState().restoredConnectionState).toEqual({
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});
	});

	it('hands over an empty restore when the URL carries nothing for this connection', async () => {
		offerRestoredConnectionState({
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});

		await connectOwningFilters();

		expect(onRestore).toHaveBeenCalledWith(null);
	});

	it('refuses the filtering to a consumer that connects without an appId', async () => {
		await connect(
			{owns: ['filters', 'search']},
			undefined,
			onStatus,
			'refused'
		);

		expect(onStatus).toHaveBeenCalledWith(
			expect.objectContaining({status: 'refused'})
		);

		expect(readState().connectionFilters).toBeUndefined();

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining(
				'connect with an appId to own the filtering'
			)
		);
	});

	it('leaves the state the URL carries alone for a consumer refused for want of an appId', async () => {
		offerRestoredConnectionState(RESTORED_STATE_MAP);

		await connect(
			{owns: ['filters', 'search']},
			undefined,
			onStatus,
			'refused'
		);

		expect(onRestore).not.toHaveBeenCalled();
		expect(readState().restoredConnectionState).toEqual(RESTORED_STATE_MAP);
	});

	it('grants the filtering to a consumer that connects once the owner is gone', async () => {
		await connectOwningFilters();

		connection.disconnect();

		const {onSecondStatus} = await connectSecondOwningFilters('ready');

		expect(onSecondStatus).toHaveBeenCalledWith(
			expect.objectContaining({status: 'ready'})
		);

		expect(readState().connectionFilters).toEqual([]);
	});
});
