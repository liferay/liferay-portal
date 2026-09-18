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
} from '@liferay/js-api/data-set/connection';

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

const OTHER_FDS_NAME = 'otherTestDataSet';

const CONNECTION_ID = 'sampleCustomElement';

const OTHER_CONNECTION_ID = 'otherCustomElement';

const CUSTOM_CONFIG = {selections: {color: ['Blue', 'Green']}};

// What the URL holds is keyed by connection, and what a consumer is handed
// back is only its own slice.

const CUSTOM_CONFIGS = {[CONNECTION_ID]: CUSTOM_CONFIG};

describe('FDSConnection filters', () => {
	let atom: Liferay.State.Atom<FDSState>;
	let connection: FDSConnection;
	let connections: Array<FDSConnection>;
	let onApply: jest.Mock;
	let onSearch: jest.Mock;
	let onStatus: jest.Mock;
	let openToast: jest.Mock;

	const createFDSAtom = (fdsName: string) =>
		State.atom(`${fdsName}_fdsState`, {
			filters: DECLARED_FILTERS,
			search: {query: ''},
		}) as never as Liferay.State.Atom<FDSState>;

	const readState = (fdsAtom: Liferay.State.Atom<FDSState> = atom) =>
		State.read(fdsAtom as never) as unknown as FDSState;

	const offerCustomConfigs = (
		offeredCustomConfigs: Record<string, unknown> | null
	) =>
		State.write(
			atom as never,
			{
				...readState(),
				offeredCustomConfigs,
			} as never
		);

	const connect = async (
		options: FDSConnectionOptions = {},
		fdsStateChangeCallback: FDSStateChangeCallback = {
			apply: onApply,
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
		const onSecondApply = jest.fn();
		const onSecondStatus = jest.fn();

		const secondConnection = await connect(
			{appId: OTHER_CONNECTION_ID, owns: ['filters', 'search']},
			{apply: onSecondApply, search: onSearch},
			onSecondStatus,
			settledStatus
		);

		return {onSecondApply, onSecondStatus, secondConnection};
	};

	beforeEach(() => {
		State.__internal__.reset();

		(Liferay.on as jest.Mock).mockReturnValue({detach: jest.fn()});

		atom = createFDSAtom(FDS_NAME);

		connections = [];
		onApply = jest.fn();
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

		expect(readState().filteringOwnerAppId).toBe(CONNECTION_ID);

		expect(readState().connectionFilters).toBeUndefined();
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
		expect(readState().appliedCustomConfigs).toBeUndefined();
	});

	// Only one consumer can own the filtering today, so no second key can be
	// in play for it to leave behind. Seeding one keeps releasing honest for
	// the day it can, and matches what taking an offered key already does.

	it('takes only its own config out when a consumer that owned the filtering disconnects', async () => {
		await connectOwningFilters();

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		const otherCustomConfig = {selections: {size: ['Big']}};

		State.write(
			atom as never,
			{
				...readState(),
				appliedCustomConfigs: {
					...readState().appliedCustomConfigs,
					[OTHER_CONNECTION_ID]: otherCustomConfig,
				},
			} as never
		);

		connection.disconnect();

		expect(readState().appliedCustomConfigs).toEqual({
			[OTHER_CONNECTION_ID]: otherCustomConfig,
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

	it('keeps the config a consumer asks it to remember under the id of its connection, so the data set can put it in the URL', async () => {
		await connectOwningFilters();

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		expect(readState().appliedCustomConfigs).toEqual({
			[CONNECTION_ID]: {selections: {color: ['Blue']}},
		});
	});

	it('filters without remembering anything when the consumer passes no config', async () => {
		await connectOwningFilters();

		connection.setFilters([
			{id: 'color', odataFilterString: "color in ('Blue')"},
		]);

		expect(readState().connectionFilters).toEqual([
			{id: 'color', odataFilterString: "color in ('Blue')"},
		]);

		expect(readState().appliedCustomConfigs).toBeUndefined();
	});

	// The state of the data set holds one key per connection and the data set
	// writes the URL from the whole map, so what another connection asked to
	// have remembered survives this one filtering.

	it('keeps the configs of other connections when a consumer remembers its own', async () => {
		await connectOwningFilters();

		const otherCustomConfig = {selections: {size: ['Big']}};

		State.write(
			atom as never,
			{
				...readState(),
				appliedCustomConfigs: {
					[OTHER_CONNECTION_ID]: otherCustomConfig,
				},
			} as never
		);

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		expect(readState().appliedCustomConfigs).toEqual({
			[CONNECTION_ID]: {selections: {color: ['Blue']}},
			[OTHER_CONNECTION_ID]: otherCustomConfig,
		});
	});

	it('takes only its own config out when a consumer filters without remembering anything', async () => {
		await connectOwningFilters();

		const otherCustomConfig = {selections: {size: ['Big']}};

		connection.setFilters(
			[{id: 'color', odataFilterString: "color in ('Blue')"}],
			{selections: {color: ['Blue']}}
		);

		State.write(
			atom as never,
			{
				...readState(),
				appliedCustomConfigs: {
					...readState().appliedCustomConfigs,
					[OTHER_CONNECTION_ID]: otherCustomConfig,
				},
			} as never
		);

		connection.setFilters([
			{id: 'color', odataFilterString: "color in ('Green')"},
		]);

		expect(readState().appliedCustomConfigs).toEqual({
			[OTHER_CONNECTION_ID]: otherCustomConfig,
		});
	});

	it('hands the config the data set offers to the consumer', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connectOwningFilters();

		expect(onApply).toHaveBeenCalledWith(CUSTOM_CONFIG);
	});

	it('stops offering the config once the consumer has it', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connectOwningFilters();

		expect(readState().offeredCustomConfigs).toBeUndefined();
	});

	it('takes the filtering over with what the consumer applies, not with nothing', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		onApply = jest.fn(() =>
			connection.setFilters(
				[
					{
						id: 'color',
						odataFilterString: "color in ('Blue', 'Green')",
					},
				],
				CUSTOM_CONFIG
			)
		);

		await connectOwningFilters();

		expect(readState().connectionFilters).toEqual([
			{id: 'color', odataFilterString: "color in ('Blue', 'Green')"},
		]);
	});

	it('hands over a config the data set offers after the connection is ready', async () => {
		await connectOwningFilters();

		onApply.mockClear();

		offerCustomConfigs(CUSTOM_CONFIGS);

		await waitFor(() =>
			expect(onApply).toHaveBeenCalledWith(CUSTOM_CONFIG)
		);

		expect(readState().offeredCustomConfigs).toBeUndefined();
	});

	it('hands over an empty config, so that going back to an unfiltered address clears the filter UI', async () => {
		await connectOwningFilters();

		onApply.mockClear();

		offerCustomConfigs(null);

		await waitFor(() => expect(onApply).toHaveBeenCalledWith(null));
	});

	it('leaves the offered config alone for a consumer that only owns the search', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connect({owns: ['search']});

		expect(onApply).not.toHaveBeenCalled();
		expect(readState().offeredCustomConfigs).toEqual(CUSTOM_CONFIGS);
	});

	it('warns and drops the offered config when the consumer cannot take it', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connect(
			{appId: CONNECTION_ID, owns: ['filters']},
			{search: onSearch}
		);

		expect(console.warn).toHaveBeenCalledWith(
			expect.anything(),
			expect.anything(),
			expect.anything(),
			expect.stringContaining('Dropped the custom config offered for')
		);

		expect(readState().offeredCustomConfigs).toBeUndefined();
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

		expect(readState().connectionFilters).toBeUndefined();

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

		expect(readState().filteringOwnerAppId).toBe(CONNECTION_ID);
	});

	it('leaves the config the data set offers to the consumer that owns the filtering', async () => {
		await connectOwningFilters();

		const {onSecondApply} = await connectSecondOwningFilters();

		offerCustomConfigs(CUSTOM_CONFIGS);

		await waitFor(() =>
			expect(onApply).toHaveBeenCalledWith(CUSTOM_CONFIG)
		);

		expect(onSecondApply).not.toHaveBeenCalled();
	});

	it('leaves the config the URL carries to the consumer that owns the filtering', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connectOwningFilters();

		const {onSecondApply} = await connectSecondOwningFilters();

		expect(onApply).toHaveBeenCalledWith(CUSTOM_CONFIG);
		expect(onSecondApply).not.toHaveBeenCalled();
	});

	it('hands a consumer its own slice of what the URL carries', async () => {
		offerCustomConfigs({
			[CONNECTION_ID]: CUSTOM_CONFIG,
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});

		await connectOwningFilters();

		expect(onApply).toHaveBeenCalledWith(CUSTOM_CONFIG);
	});

	it('leaves the keys of other connections on offer when it takes its own', async () => {
		const otherCustomConfig = {selections: {size: ['Big']}};

		offerCustomConfigs({
			[CONNECTION_ID]: CUSTOM_CONFIG,
			[OTHER_CONNECTION_ID]: otherCustomConfig,
		});

		await connectOwningFilters();

		expect(onApply).toHaveBeenCalledWith(CUSTOM_CONFIG);

		expect(readState().offeredCustomConfigs).toEqual({
			[OTHER_CONNECTION_ID]: otherCustomConfig,
		});
	});

	it('stops offering anything once the last key has been taken', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connectOwningFilters();

		expect(readState().offeredCustomConfigs).toBeUndefined();
	});

	it('says nothing to a consumer when what is left on offer holds no key of its own', async () => {
		await connectOwningFilters();

		onApply.mockClear();

		offerCustomConfigs({
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});

		// Waiting on the search callback, which every write reaches, proves
		// the write was seen rather than merely not handled yet.

		await waitFor(() => expect(onSearch).toHaveBeenCalled());

		expect(onApply).not.toHaveBeenCalled();

		expect(readState().offeredCustomConfigs).toEqual({
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});
	});

	it('hands over an empty config when the URL carries nothing for this connection', async () => {
		offerCustomConfigs({
			[OTHER_CONNECTION_ID]: {selections: {size: ['Big']}},
		});

		await connectOwningFilters();

		expect(onApply).toHaveBeenCalledWith(null);
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

	it('leaves the config the URL carries alone for a consumer refused for want of an appId', async () => {
		offerCustomConfigs(CUSTOM_CONFIGS);

		await connect(
			{owns: ['filters', 'search']},
			undefined,
			onStatus,
			'refused'
		);

		expect(onApply).not.toHaveBeenCalled();
		expect(readState().offeredCustomConfigs).toEqual(CUSTOM_CONFIGS);
	});

	it('grants the filtering to a consumer that connects once the owner is gone', async () => {
		await connectOwningFilters();

		connection.disconnect();

		const {onSecondStatus} = await connectSecondOwningFilters('ready');

		expect(onSecondStatus).toHaveBeenCalledWith(
			expect.objectContaining({status: 'ready'})
		);

		expect(readState().filteringOwnerAppId).toBe(OTHER_CONNECTION_ID);
	});

	// What says the filtering is owned is the claim of its owner, not the
	// filters it applies: a consumer may own the filtering and filter by
	// nothing, and the filters it applied may outlive its connection.

	it('says which app owns the filtering in the state of the data set, and stops saying it once that app is gone', async () => {
		await connectOwningFilters();

		expect(readState().filteringOwnerAppId).toBe(CONNECTION_ID);

		connection.disconnect();

		expect(readState().filteringOwnerAppId).toBeUndefined();
	});

	// Another copy of this module on the page keeps its own owners out of
	// reach, so what a connection of this one goes by is the claim in the
	// state of the data set.

	it('refuses the filtering to a consumer when the state of the data set says another app owns it', async () => {
		State.write(
			atom as never,
			{...readState(), filteringOwnerAppId: OTHER_CONNECTION_ID} as never
		);

		await connect(
			{appId: CONNECTION_ID, owns: ['filters', 'search']},
			undefined,
			onStatus,
			'refused'
		);

		expect(onStatus).toHaveBeenCalledWith(
			expect.objectContaining({status: 'refused'})
		);
	});

	// Filters left behind are not the new owner's, so taking the filtering
	// over drops them rather than leaving the data set filtered by a
	// connection that no longer owns it.

	it('grants the filtering to a consumer when the state of the data set carries filters that no owner claims, and drops them', async () => {
		State.write(
			atom as never,
			{
				...readState(),
				connectionFilters: [
					{id: 'stale', odataFilterString: "color eq 'Blue'"},
				],
			} as never
		);

		await connectOwningFilters();

		expect(readState().connectionFilters).toBeUndefined();
	});

	// What is granted is the filtering of one data set, so an app that
	// filters two of them is not asking twice for the same thing.

	it('grants one app the filtering of two data sets', async () => {
		const otherFDSAtom = createFDSAtom(OTHER_FDS_NAME);
		const onOtherFDSStatus = jest.fn();

		await connectOwningFilters();

		const otherFDSConnection = new FDSConnection(
			OTHER_FDS_NAME,
			{search: onSearch},
			onOtherFDSStatus,
			{appId: CONNECTION_ID, owns: ['filters', 'search']}
		);

		connections.push(otherFDSConnection);

		await waitFor(() =>
			expect(onOtherFDSStatus).toHaveBeenCalledWith(
				expect.objectContaining({status: 'ready'})
			)
		);

		otherFDSConnection.setFilters([
			{id: 'other', odataFilterString: "author eq 'joe'"},
		]);

		expect(readState(otherFDSAtom).connectionFilters).toEqual([
			{id: 'other', odataFilterString: "author eq 'joe'"},
		]);

		expect(readState().filteringOwnerAppId).toBe(CONNECTION_ID);

		expect(readState().connectionFilters).toBeUndefined();
	});
});
