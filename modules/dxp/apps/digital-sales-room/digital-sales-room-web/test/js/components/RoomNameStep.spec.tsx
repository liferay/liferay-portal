/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, renderHook, screen} from '@testing-library/react';
import React, {useState} from 'react';

import {
	IRoomDataContext,
	IRoomStepProps,
} from '../../../src/main/resources/META-INF/resources/js/common/utils/types';
import {RoomContext} from '../../../src/main/resources/META-INF/resources/js/components/RoomInitializer';
import RoomNameStep from '../../../src/main/resources/META-INF/resources/js/components/RoomNameStep';
import {setFieldValue} from '../utils';

let {result: useStateHookResult} = renderHook(() =>
	useState<IRoomDataContext>({
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
			<RoomNameStep
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

describe('RoomNameStep', () => {
	beforeEach(() => {
		({result: useStateHookResult} = renderHook(() =>
			useState({
				errors: {},
				friendlyURL: '',
				roomName: '',
			})
		));
	});

	it('renders the correct UI elements', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		expect(screen.getByTestId('friendlyURLInput')).toBeInTheDocument();
		expect(screen.getByTestId('roomNameInput')).toBeInTheDocument();
		expect(screen.getByTestId('stepLocator')).toBeInTheDocument();
		expect(screen.getByTestId('stepTitle')).toBeInTheDocument();
	});

	it('hints the friendly URL prefix of the current instance', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		expect(
			screen.getByText('http://localhost:8080/web')
		).toBeInTheDocument();
	});

	it('hides header based on the parameter', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
			showHeader: false,
		});

		expect(screen.getByTestId('friendlyURLInput')).toBeInTheDocument();
		expect(screen.getByTestId('roomNameInput')).toBeInTheDocument();
		expect(screen.queryByTestId('stepLocator')).not.toBeInTheDocument();
		expect(screen.queryByTestId('stepTitle')).not.toBeInTheDocument();
	});

	it('validate step', async () => {
		let stepSubmit: Function = function () {};

		const submit = (fun: Function) => {
			stepSubmit = fun();
		};

		const container = renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: submit,
		});

		expect(screen.queryByTestId('roomNameError')).not.toBeInTheDocument();

		await act(async () => {
			expect(await stepSubmit(new CustomEvent('event'))).toBeFalsy();
		});

		container.rerender(
			component({numberOfSteps: 1, setHandleStepSubmit: submit})
		);

		expect(screen.queryByTestId('roomNameError')).toBeInTheDocument();

		await setFieldValue(screen.getByTestId('roomNameInput'), 'roomName');
		container.rerender(
			component({numberOfSteps: 1, setHandleStepSubmit: submit})
		);

		await act(async () => {
			expect(await stepSubmit(new CustomEvent('event'))).toBeTruthy();
		});

		container.rerender(
			component({numberOfSteps: 1, setHandleStepSubmit: submit})
		);

		expect(screen.queryByTestId('roomNameError')).not.toBeInTheDocument();
	});

	it('validate UI interaction', async () => {
		const stepSubmit = jest.fn();

		const container = renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: stepSubmit,
		});

		await setFieldValue(screen.getByTestId('roomNameInput'), 'roomName');
		await setFieldValue(
			screen.getByTestId('friendlyURLInput'),
			'friendlyURL'
		);

		let state = useStateHookResult.current[0];

		expect(state.roomName).not.toBe('');
		expect(state.friendlyURL).not.toBe('');
		expect(state.errors.roomName).toBeNull();

		await setFieldValue(screen.getByTestId('roomNameInput'), '');

		state = useStateHookResult.current[0];

		expect(state.roomName).toBe('');
		expect(state.friendlyURL).not.toBe('');
		expect(state.errors.roomName).not.toBe('');

		container.rerender(
			component({numberOfSteps: 1, setHandleStepSubmit: stepSubmit})
		);

		expect(screen.queryByTestId('roomNameError')).toBeInTheDocument();
	});

	it('disables fields and buttons when loading is true', async () => {
		renderComponent({
			loading: true,
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
		});

		expect(screen.getByTestId('friendlyURLInput')).toBeDisabled();
		expect(screen.getByTestId('roomNameInput')).toBeDisabled();
	});
});
