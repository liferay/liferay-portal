/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import viewsReducer, {
	EViewsActionTypes,
} from '../../src/main/resources/META-INF/resources/views/viewsReducer';

describe('viewsReducer startup snapshot', () => {
	describe('ADD_OR_UPDATE_STARTUP_SNAPSHOT', () => {
		it('sets the startup snapshot', () => {
			const state = {startupSnapshot: null};

			const nextState = viewsReducer(state, {
				type: EViewsActionTypes.ADD_OR_UPDATE_STARTUP_SNAPSHOT,
				value: {startupSnapshot: {erc: 'erc-1'}},
			});

			expect(nextState.startupSnapshot).toEqual({erc: 'erc-1'});
		});

		it('replaces a previously set startup snapshot', () => {
			const state = {startupSnapshot: {erc: 'erc-1'}};

			const nextState = viewsReducer(state, {
				type: EViewsActionTypes.ADD_OR_UPDATE_STARTUP_SNAPSHOT,
				value: {startupSnapshot: {erc: 'erc-2'}},
			});

			expect(nextState.startupSnapshot).toEqual({erc: 'erc-2'});
		});
	});

	describe('DELETE_SNAPSHOT', () => {
		const baseState = {
			defaultSnapshot: {},
			snapshots: [
				{
					headerVisible: false,
					items: [{erc: 'erc-1'}, {erc: 'erc-2'}],
				},
			],
			startupSnapshot: {erc: 'erc-1'},
		};

		it('clears the startup snapshot when it is deleted', () => {
			const nextState = viewsReducer(baseState, {
				type: EViewsActionTypes.DELETE_SNAPSHOT,
				value: {snapshotERC: 'erc-1'},
			});

			expect(nextState.startupSnapshot).toBeNull();
		});

		it('keeps the startup snapshot when a different view is deleted', () => {
			const nextState = viewsReducer(baseState, {
				type: EViewsActionTypes.DELETE_SNAPSHOT,
				value: {snapshotERC: 'erc-2'},
			});

			expect(nextState.startupSnapshot).toEqual({erc: 'erc-1'});
		});
	});
});
