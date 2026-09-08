/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import viewsReducer, {
	EViewsActionTypes,
} from '../../src/main/resources/META-INF/resources/views/viewsReducer';

describe('viewsReducer preferences', () => {
	describe('UPDATE_USER_PREFERENCES', () => {
		it('sets the initial data set snapshot ERC preference', () => {
			const state = {userPreferences: null};

			const nextState = viewsReducer(state, {
				type: EViewsActionTypes.UPDATE_USER_PREFERENCES,
				value: {userPreferences: {initialDataSetSnapshotERC: 'erc-1'}},
			});

			expect(nextState.userPreferences).toEqual({
				initialDataSetSnapshotERC: 'erc-1',
			});
		});

		it('replaces a previously set initial data set snapshot ERC preference', () => {
			const state = {
				userPreferences: {initialDataSetSnapshotERC: 'erc-1'},
			};

			const nextState = viewsReducer(state, {
				type: EViewsActionTypes.UPDATE_USER_PREFERENCES,
				value: {userPreferences: {initialDataSetSnapshotERC: 'erc-2'}},
			});

			expect(nextState.userPreferences).toEqual({
				initialDataSetSnapshotERC: 'erc-2',
			});
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
			userPreferences: {initialDataSetSnapshotERC: 'erc-1'},
		};

		it('clears the initial data set snapshot ERC preference when snapshot is deleted', () => {
			const nextState = viewsReducer(baseState, {
				type: EViewsActionTypes.DELETE_SNAPSHOT,
				value: {snapshotERC: 'erc-1'},
			});

			expect(nextState.userPreferences).toEqual({
				initialDataSetSnapshotERC: null,
			});
		});

		it('keeps the initial data set snapshot ERC preference when a different snapshot is deleted', () => {
			const nextState = viewsReducer(baseState, {
				type: EViewsActionTypes.DELETE_SNAPSHOT,
				value: {snapshotERC: 'erc-2'},
			});

			expect(nextState.userPreferences).toEqual({
				initialDataSetSnapshotERC: 'erc-1',
			});
		});
	});
});
