/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import Toolbar from '../../src/main/resources/META-INF/resources/js/style-book-editor/Toolbar';
import {
	StyleBookEditorContextProvider,
	useFrontendTokensValues,
} from '../../src/main/resources/META-INF/resources/js/style-book-editor/contexts/StyleBookEditorContext';
import saveDraft from '../../src/main/resources/META-INF/resources/js/style-book-editor/saveDraft';

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/saveDraft',
	() => jest.fn(() => Promise.resolve())
);

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/config',
	() => ({
		config: {
			pending: false,
			portletNamespace: 'portletNamespace',
			publishURL: 'publishURL',
			redirectURL: 'redirectURL',
		},
	})
);

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, arg) => langKey.replace('-x', ` ${arg}`)),
}));

const STATE = {
	frontendTokensValues: {
		brandColor2: {
			value: {
				cssVariableMapping: 'brand-color-2',
				value: 'black',
			},
		},
		testColor1: {
			value: {
				cssVariableMapping: 'test-color-1',
				value: 'white',
			},
		},
	},

	redoHistory: [
		{
			label: 'Brand Color 2',
			name: 'brandColor2',
			value: {
				cssVariableMapping: 'brand-color-2',
				value: 'yellow',
			},
		},
		{
			label: 'Test Color 1',
			name: 'testColor1',
			value: {
				cssVariableMapping: 'test-color-1',
				value: 'red',
			},
		},
	],
	undoHistory: [
		{
			label: 'Brand Color 2',
			name: 'brandColor2',
			value: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
		},
		{
			label: 'Test Color 1',
			name: 'testColor1',
			value: {
				cssVariableMapping: 'test-color-1',
				value: 'blue',
			},
		},
	],
};

const TestComponent = ({frontendTokensValuesRef}) => {
	const frontendTokensValues = useFrontendTokensValues();

	frontendTokensValuesRef.current = frontendTokensValues;

	return null;
};

const renderComponent = () => {
	const frontendTokensValuesRef = React.createRef();

	render(
		<StyleBookEditorContextProvider
			initialState={{
				draftStatus: 'saved',
				frontendTokensValues: STATE.frontendTokensValues,
				previewLayout: {},
				previewLayoutType: 'lalala',
				redoHistory: STATE.redoHistory,
				undoHistory: STATE.undoHistory,
			}}
		>
			<TestComponent frontendTokensValuesRef={frontendTokensValuesRef} />

			<Toolbar />
		</StyleBookEditorContextProvider>
	);

	return frontendTokensValuesRef;
};

describe('Toolbar', () => {
	beforeEach(() => {
		saveDraft.mockClear();
	});

	it('renders Toolbar component', async () => {
		renderComponent();

		expect(screen.getByLabelText('publish')).toBeInTheDocument();
	});

	it('Clicking on undo, reset the last change', async () => {
		const frontendTokensValuesRef = renderComponent();

		await act(async () => {
			userEvent.click(screen.getByLabelText('undo'));
		});

		expect(saveDraft).toBeCalledWith({
			...STATE.frontendTokensValues,
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
		});

		expect(screen.getByText('saved')).toBeInTheDocument();

		expect(frontendTokensValuesRef.current).toMatchObject({
			...STATE.frontendTokensValues,
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
		});
	});

	it('Clicking on redo, re-apply the last change', async () => {
		const frontendTokensValuesRef = renderComponent();

		await act(async () => {
			userEvent.click(screen.getByLabelText('redo'));
		});

		expect(saveDraft).toBeCalledWith({
			...STATE.frontendTokensValues,
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'yellow',
			},
		});

		expect(screen.getByText('saved')).toBeInTheDocument();

		expect(frontendTokensValuesRef.current).toMatchObject({
			...STATE.frontendTokensValues,
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'yellow',
			},
		});
	});

	it('Undo button is disabled when there is no more undos', async () => {
		const frontendTokensValuesRef = renderComponent();

		const undoButton = screen.getByLabelText('undo');

		await act(async () => {
			userEvent.click(undoButton);
		});

		await act(async () => {
			userEvent.click(undoButton);
		});

		expect(saveDraft).nthCalledWith(2, {
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
			testColor1: {
				cssVariableMapping: 'test-color-1',
				value: 'blue',
			},
		});

		expect(frontendTokensValuesRef.current).toMatchObject({
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
			testColor1: {
				cssVariableMapping: 'test-color-1',
				value: 'blue',
			},
		});

		expect(undoButton).toBeDisabled();
	});

	it('Undo button is disabled when there is no more redos', async () => {
		const frontendTokensValuesRef = renderComponent();

		const redoButton = screen.getByLabelText('redo');

		await act(async () => {
			userEvent.click(redoButton);
		});

		await act(async () => {
			userEvent.click(redoButton);
		});

		expect(saveDraft).nthCalledWith(2, {
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'yellow',
			},
			testColor1: {
				cssVariableMapping: 'test-color-1',
				value: 'red',
			},
		});

		expect(frontendTokensValuesRef.current).toMatchObject({
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'yellow',
			},
			testColor1: {
				cssVariableMapping: 'test-color-1',
				value: 'red',
			},
		});

		expect(redoButton).toBeDisabled();
	});

	it('Undo all button reset all changes', async () => {
		const frontendTokensValuesRef = renderComponent();

		await act(async () => {
			userEvent.click(screen.getByText('undo-all'));
		});

		expect(saveDraft).toBeCalledWith({
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
			testColor1: {
				cssVariableMapping: 'test-color-1',
				value: 'blue',
			},
		});

		expect(frontendTokensValuesRef.current).toMatchObject({
			brandColor2: {
				cssVariableMapping: 'brand-color-2',
				value: 'gray',
			},
			testColor1: {
				cssVariableMapping: 'test-color-1',
				value: 'blue',
			},
		});
	});
});
