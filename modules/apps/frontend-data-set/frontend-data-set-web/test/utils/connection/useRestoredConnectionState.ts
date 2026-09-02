/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import JsonURL from '@jsonurl/jsonurl';
import {renderHook, waitFor} from '@testing-library/react';

import {getConfigParamName} from '../../../src/main/resources/META-INF/resources/utils/configInURL';
import {useRestoredConnectionState} from '../../../src/main/resources/META-INF/resources/utils/connection/useRestoredConnectionState';
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

const OTHER_APP_STATE = {[OTHER_APP_ID]: {selections: {size: ['Big']}}};

// An address that carries state for both, which is what a page holding a
// second consumer leaves behind.

const BOTH_APPS_STATE = {
	...OTHER_APP_STATE,
	[OWNER_APP_ID]: {selections: {color: ['Blue']}},
};

const OWNER_APP_STATE = {[OWNER_APP_ID]: {selections: {color: ['Blue']}}};

describe('useRestoredConnectionState', () => {
	let onGiveUp: jest.Mock;

	const putConnectionStateInURL = (connectionState: unknown) => {
		const searchParams = new URLSearchParams();

		searchParams.set(
			getConfigParamName(FDS_ID),
			JsonURL.stringify(
				{[EConfigInURLKeys.CONNECTION_STATE]: connectionState},
				{AQF: true, noEmptyComposite: true}
			) as string
		);

		window.history.replaceState({}, '', `?${searchParams}`);
	};

	const renderRestore = () =>
		renderHook(
			(props: {
				filteringOwnerAppId: string | undefined;
				restoredConnectionState: unknown;
			}) =>
				useRestoredConnectionState({
					configInURLBehavior: EConfigInURLBehavior.PUSH,
					filteringOwnerAppId: props.filteringOwnerAppId,
					id: FDS_ID,
					onGiveUp,
					restoredConnectionState: props.restoredConnectionState,
				}),
			{
				initialProps: {
					filteringOwnerAppId: undefined as string | undefined,
					restoredConnectionState: undefined as unknown,
				},
			}
		);

	beforeEach(() => {
		onGiveUp = jest.fn();

		putConnectionStateInURL(OTHER_APP_STATE);
	});

	afterEach(() => {
		window.history.replaceState({}, '', '?');
	});

	it('has nothing to wait for when the URL carries no state a connection left', () => {
		window.history.replaceState({}, '', '?');

		expect(renderRestore().result.current.restored).toBe(true);
	});

	// The data set holds its first request for as long as this waits, so
	// waiting on an offer nobody will take is a data set nobody can use.

	it('waits while no connection owns the filtering, since the one that will may still be loading', () => {
		const {rerender, result} = renderRestore();

		expect(result.current.restored).toBe(false);

		rerender({
			filteringOwnerAppId: undefined,
			restoredConnectionState: OTHER_APP_STATE,
		});

		expect(result.current.restored).toBe(false);
		expect(onGiveUp).not.toHaveBeenCalled();
	});

	it('waits while what is offered holds a key for the connection that owns the filtering', () => {
		const {rerender, result} = renderRestore();

		rerender({
			filteringOwnerAppId: OTHER_APP_ID,
			restoredConnectionState: OTHER_APP_STATE,
		});

		expect(result.current.restored).toBe(false);
		expect(onGiveUp).not.toHaveBeenCalled();
	});

	it('has been taken once nothing is left on offer', async () => {
		putConnectionStateInURL(OWNER_APP_STATE);

		const {rerender, result} = renderRestore();

		rerender({
			filteringOwnerAppId: OWNER_APP_ID,
			restoredConnectionState: OWNER_APP_STATE,
		});

		expect(result.current.restored).toBe(false);

		rerender({
			filteringOwnerAppId: OWNER_APP_ID,
			restoredConnectionState: undefined,
		});

		await waitFor(() => expect(result.current.restored).toBe(true));

		expect(onGiveUp).not.toHaveBeenCalled();
	});

	// The owner takes its own key and leaves the rest, so an offer that still
	// holds keys of other apps has been taken all the same. What tells that
	// from an offer the owner never had a key in is the address, which the
	// data set does not write until the wait is over.

	it('has been taken once the key of the owner is gone, though keys of other apps are still on offer', async () => {
		putConnectionStateInURL(BOTH_APPS_STATE);

		const {rerender, result} = renderRestore();

		rerender({
			filteringOwnerAppId: OWNER_APP_ID,
			restoredConnectionState: BOTH_APPS_STATE,
		});

		expect(result.current.restored).toBe(false);

		rerender({
			filteringOwnerAppId: OWNER_APP_ID,
			restoredConnectionState: OTHER_APP_STATE,
		});

		await waitFor(() => expect(result.current.restored).toBe(true));

		expect(onGiveUp).not.toHaveBeenCalled();
	});

	// Nobody else can take it: the connection that owns the filtering is the
	// only one the data set offers what the URL carries to.

	it('gives up as soon as the connection that owns the filtering finds no key of its own', async () => {
		const {rerender, result} = renderRestore();

		rerender({
			filteringOwnerAppId: OWNER_APP_ID,
			restoredConnectionState: OTHER_APP_STATE,
		});

		await waitFor(() => expect(result.current.restored).toBe(true));

		expect(onGiveUp).toHaveBeenCalled();
	});
});
