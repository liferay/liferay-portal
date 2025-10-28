/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import UndoHistory from '../../src/main/resources/META-INF/resources/js/style-book-editor/UndoHistory';
import {StyleBookEditorContextProvider} from '../../src/main/resources/META-INF/resources/js/style-book-editor/contexts/StyleBookEditorContext';

const STATE = {
	redoHistory: [
		{
			label: 'Brand Color 2',
			name: 'testColor2',
			value: {
				cssVariableMapping: 'test-color-2',
				value: '#DDDDDD',
			},
		},
		{
			label: 'Test Color 1',
			name: 'testColor1',
			value: {
				cssVariableMapping: 'test-color-1',
				value: '#111111',
			},
		},
	],
	undoHistory: [
		{
			label: 'Test Color 3',
			name: 'testColor3',
			value: {
				cssVariableMapping: 'test-color-3',
				value: '#2e5aac',
			},
		},
		{
			label: 'Test Color 4',
			name: 'testColor4',
			value: {
				cssVariableMapping: 'test-color-4',
				value: '#6b6c7e',
			},
		},
		{
			label: 'Test Color 5',
			name: 'testColor5',
			value: {
				cssVariableMapping: 'test-color-5',
				value: '#BBBBBB',
			},
		},
	],
};

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, arg) => langKey.replace('-x', ` ${arg}`)),
}));

function renderUndoHistory() {
	return render(
		<StyleBookEditorContextProvider
			initialState={{
				draftStatus: 'saved',
				frontendTokensValues: {},
				previewLayout: {},
				previewLayoutType: 'lalala',
				redoHistory: STATE.redoHistory,
				undoHistory: STATE.undoHistory,
			}}
		>
			<UndoHistory />
		</StyleBookEditorContextProvider>
	);
}

describe('UndoHistory', () => {
	it('shows all redo and undo history items in the list', () => {
		const {getByText} = renderUndoHistory();

		STATE.redoHistory
			.concat(STATE.undoHistory)
			.forEach(({label}) =>
				expect(getByText(`update ${label}`)).toBeInTheDocument()
			);
	});
});
