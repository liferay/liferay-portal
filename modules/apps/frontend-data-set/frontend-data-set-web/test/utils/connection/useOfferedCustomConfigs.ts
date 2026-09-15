/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import JsonURL from '@jsonurl/jsonurl';
import {renderHook, waitFor} from '@testing-library/react';

import {getConfigParamName} from '../../../src/main/resources/META-INF/resources/utils/configInURL';
import {useOfferedCustomConfigs} from '../../../src/main/resources/META-INF/resources/utils/connection/useOfferedCustomConfigs';
import {
	EConfigInURLBehavior,
	EConfigInURLKeys,
} from '../../../src/main/resources/META-INF/resources/utils/types';

const FDS_ID = 'testDataSet';

const OTHER_APP_ID = 'otherCustomElement';

const OWNER_APP_ID = 'sampleCustomElement';

// What the URL carries is filed under the app id of the connection that left
// it, so this is an address written by an app other than the one that owns
// the filtering below.

const OTHER_APP_CONFIGS = {[OTHER_APP_ID]: {selections: {size: ['Big']}}};

// An address that carries configs for both, which is what a page holding a
// second consumer leaves behind.

const BOTH_APPS_CONFIGS = {
	...OTHER_APP_CONFIGS,
	[OWNER_APP_ID]: {selections: {color: ['Blue']}},
};

const OWNER_APP_CONFIGS = {[OWNER_APP_ID]: {selections: {color: ['Blue']}}};

describe('useOfferedCustomConfigs', () => {
	let onGiveUp: jest.Mock;

	const putCustomConfigsInURL = (customConfigs: unknown) => {
		const searchParams = new URLSearchParams();

		searchParams.set(
			getConfigParamName(FDS_ID),
			JsonURL.stringify(
				{[EConfigInURLKeys.CUSTOM_CONFIGS]: customConfigs},
				{AQF: true, noEmptyComposite: true}
			) as string
		);

		window.history.replaceState({}, '', `?${searchParams}`);
	};

	const renderOfferedCustomConfigs = () =>
		renderHook(
			(props: {
				customConfigsOffered: boolean;
				filteringOwnerAppId: string | undefined;
				offeredCustomConfigs: unknown;
			}) =>
				useOfferedCustomConfigs({
					configInURLBehavior: EConfigInURLBehavior.PUSH,
					customConfigsOffered: props.customConfigsOffered,
					filteringOwnerAppId: props.filteringOwnerAppId,
					id: FDS_ID,
					offeredCustomConfigs: props.offeredCustomConfigs,
					onGiveUp,
				}),
			{
				initialProps: {
					customConfigsOffered: false,
					filteringOwnerAppId: undefined as string | undefined,
					offeredCustomConfigs: undefined as unknown,
				},
			}
		);

	beforeEach(() => {
		onGiveUp = jest.fn();

		putCustomConfigsInURL(OTHER_APP_CONFIGS);
	});

	afterEach(() => {
		window.history.replaceState({}, '', '?');
	});

	it('has nothing to wait for when the URL carries no config a connection left', () => {
		window.history.replaceState({}, '', '?');

		expect(renderOfferedCustomConfigs().result.current.settled).toBe(true);
	});

	// The data set holds its first request for as long as this waits, so
	// waiting on an offer nobody will take is a data set nobody can use.

	it('waits while no connection owns the filtering, since the one that will may still be loading', () => {
		const {rerender, result} = renderOfferedCustomConfigs();

		expect(result.current.settled).toBe(false);

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: undefined,
			offeredCustomConfigs: OTHER_APP_CONFIGS,
		});

		expect(result.current.settled).toBe(false);
		expect(onGiveUp).not.toHaveBeenCalled();
	});

	it('waits while what is offered holds a key for the connection that owns the filtering', () => {
		const {rerender, result} = renderOfferedCustomConfigs();

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OTHER_APP_ID,
			offeredCustomConfigs: OTHER_APP_CONFIGS,
		});

		expect(result.current.settled).toBe(false);
		expect(onGiveUp).not.toHaveBeenCalled();
	});

	it('has been taken once nothing is left on offer', async () => {
		putCustomConfigsInURL(OWNER_APP_CONFIGS);

		const {rerender, result} = renderOfferedCustomConfigs();

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OWNER_APP_ID,
			offeredCustomConfigs: OWNER_APP_CONFIGS,
		});

		expect(result.current.settled).toBe(false);

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OWNER_APP_ID,
			offeredCustomConfigs: undefined,
		});

		await waitFor(() => expect(result.current.settled).toBe(true));

		expect(onGiveUp).not.toHaveBeenCalled();
	});

	// The owner takes its own key and leaves the rest, so an offer that still
	// holds keys of other apps has been taken all the same. What tells that
	// from an offer the owner never had a key in is the address, which the
	// data set does not write until the wait is over.

	it('has been taken once the key of the owner is gone, though keys of other apps are still on offer', async () => {
		putCustomConfigsInURL(BOTH_APPS_CONFIGS);

		const {rerender, result} = renderOfferedCustomConfigs();

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OWNER_APP_ID,
			offeredCustomConfigs: BOTH_APPS_CONFIGS,
		});

		expect(result.current.settled).toBe(false);

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OWNER_APP_ID,
			offeredCustomConfigs: OTHER_APP_CONFIGS,
		});

		await waitFor(() => expect(result.current.settled).toBe(true));

		expect(onGiveUp).not.toHaveBeenCalled();
	});

	// A connection that is already listening when the offer lands takes its
	// key in the same turn, so the offer is never in place for a render to
	// see: what is read before it is made and what is read after it is taken
	// are the same nothing. Only the data set having said it offered tells
	// the two apart, which is what a page Liferay swaps in underneath rather
	// than loads depends on, since the connection is there first.

	it('has been taken when the offer was made and taken between renders', async () => {
		putCustomConfigsInURL(OWNER_APP_CONFIGS);

		const {rerender, result} = renderOfferedCustomConfigs();

		expect(result.current.settled).toBe(false);

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OWNER_APP_ID,
			offeredCustomConfigs: undefined,
		});

		await waitFor(() => expect(result.current.settled).toBe(true));

		expect(onGiveUp).not.toHaveBeenCalled();
	});

	// Nobody else can take it: the connection that owns the filtering is the
	// only one the data set offers what the URL carries to.

	it('gives up as soon as the connection that owns the filtering finds no key of its own', async () => {
		const {rerender, result} = renderOfferedCustomConfigs();

		rerender({
			customConfigsOffered: true,
			filteringOwnerAppId: OWNER_APP_ID,
			offeredCustomConfigs: OTHER_APP_CONFIGS,
		});

		await waitFor(() => expect(result.current.settled).toBe(true));

		expect(onGiveUp).toHaveBeenCalled();
	});
});
