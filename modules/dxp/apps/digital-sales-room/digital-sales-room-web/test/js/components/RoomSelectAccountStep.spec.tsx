/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import fetchMock from 'fetch-mock';

import '@testing-library/jest-dom';
import {
	cleanup,
	render,
	renderHook,
	screen,
	waitFor,
} from '@testing-library/react';
import React, {useState} from 'react';
import ResizeObserver from 'resize-observer-polyfill';

import RoomService from '../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import {
	IRoomDataContext,
	IRoomStepProps,
} from '../../../src/main/resources/META-INF/resources/js/common/utils/types';
import {RoomContext} from '../../../src/main/resources/META-INF/resources/js/components/RoomInitializer';
import RoomSelectAccountStep from '../../../src/main/resources/META-INF/resources/js/components/RoomSelectAccountStep';
import {setFieldValue} from '../utils';

global.ResizeObserver = ResizeObserver;

let {result: useStateHookResult} = renderHook(() =>
	useState<IRoomDataContext>({
		accountId: 0,
		errors: {},
		friendlyURL: '',
		roomName: '',
	})
);

const component = ({
	loading = false,
	numberOfSteps = 1,
	setHandleStepSubmit,
	showHeader = true,
}: IRoomStepProps & {loading?: boolean}) => {
	return (
		<RoomContext.Provider
			value={{
				dataContext: useStateHookResult.current[0],
				loading,
				setDataContext: useStateHookResult.current[1],
			}}
		>
			<RoomSelectAccountStep
				numberOfSteps={numberOfSteps}
				setHandleStepSubmit={setHandleStepSubmit}
				showHeader={showHeader}
			/>
		</RoomContext.Provider>
	);
};

const renderComponent = ({
	loading = false,
	numberOfSteps = 1,
	setHandleStepSubmit,
	showHeader = true,
}: IRoomStepProps & {loading?: boolean}) => {
	return render(
		component({
			loading,
			numberOfSteps,
			setHandleStepSubmit,
			showHeader,
		})
	);
};

describe('RoomSelectAccountStep', () => {
	beforeEach(() => {
		({result: useStateHookResult} = renderHook(() =>
			useState<IRoomDataContext>({
				accountId: 0,
				errors: {},
				friendlyURL: '',
				roomName: '',
			})
		));
	});

	afterEach(() => {
		fetchMock.hardReset();
		jest.clearAllMocks();

		cleanup();
	});

	it('renders the correct UI elements', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		expect(screen.getByTestId('selectAccountInput')).toBeInTheDocument();
		expect(screen.getByTestId('stepLocator')).toBeInTheDocument();
		expect(screen.getByTestId('stepTitle')).toBeInTheDocument();
	});

	it('hides header based on the parameter', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
			showHeader: false,
		});

		expect(screen.getByTestId('selectAccountInput')).toBeInTheDocument();
		expect(screen.queryByTestId('stepLocator')).not.toBeInTheDocument();
		expect(screen.queryByTestId('stepTitle')).not.toBeInTheDocument();
	});

	it('loads accounts', async () => {
		fetchMock.mockGlobal();

		const spyOnGetAccounts = jest.spyOn(RoomService, 'getAccounts');

		fetchMock.get(/headless-admin-user\/.*\/accounts.*/i, () => {
			return {
				items: [
					{
						id: 100,
						name: 'account1',
					},
					{
						id: 101,
						name: 'account2',
					},
				],
			};
		});

		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		expect(spyOnGetAccounts).toHaveBeenCalledTimes(1);

		await setFieldValue(screen.getByTestId('selectAccountInput'), 'ac');

		await waitFor(() => {
			expect(spyOnGetAccounts).toHaveBeenLastCalledWith('ac');
		});

		expect(
			screen.getByRole('option', {name: 'A account1'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'A account2'})
		).toBeInTheDocument();

		screen.getByRole('option', {name: 'A account1'}).click();

		await waitFor(() => {
			expect(spyOnGetAccounts).toHaveBeenLastCalledWith('account1');
		});

		let state = useStateHookResult.current[0];

		expect(state.accountId).toBe(100);
		expect(state.accountName).toBe('account1');

		await setFieldValue(screen.getByTestId('selectAccountInput'), '');

		await waitFor(() => {
			expect(spyOnGetAccounts).toHaveBeenLastCalledWith('');
		});

		state = useStateHookResult.current[0];

		expect(state.accountId).toBe(0);
		expect(state.accountName).toBe('');
	});

	it('validate UI interaction', async () => {
		fetchMock.mockGlobal();

		fetchMock.get(/headless-admin-user\/.*\/accounts.*/i, () => {
			return {
				items: [
					{
						id: 100,
						name: 'account1',
					},
					{
						id: 101,
						name: 'account2',
					},
				],
			};
		});

		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		await setFieldValue(screen.getByTestId('selectAccountInput'), 'ac');

		screen.getByRole('option', {name: 'A account1'}).click();

		await waitFor(() => {
			const state = useStateHookResult.current[0];

			expect(state.accountId).toBe(100);
			expect(state.accountName).toBe('account1');
		});
	});

	it('disables form fields when loading is true', async () => {
		renderComponent({
			loading: true,
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		expect(screen.getByTestId('selectAccountInput')).toBeDisabled();
	});
});
