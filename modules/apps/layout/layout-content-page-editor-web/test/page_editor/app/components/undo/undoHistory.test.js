/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import UndoHistory from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/UndoHistory';
import {UNDO_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/undoTypes';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import multipleUndo from '../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/multipleUndo';

const mockDispatch = jest.fn((a) => {
	if (typeof a === 'function') {
		return a(mockDispatch);
	}
});

const mockState = {
	availableSegmentsExperiences: {
		0: {
			name: 'Experience 0',
			segmentsExperienceId: '0',
		},
	},
	redoHistory: [
		{
			actionId: 0,
			itemId: 'item-0',
			itemName: 'Item 0',
			layoutData: {},
			segmentsExperienceId: '0',
			type: 'ADD_ITEM',
		},
		{
			actionId: 1,
			itemId: 'item-1',
			itemName: 'Item 1',
			layoutData: {},
			segmentsExperienceId: '0',
			type: 'MOVE_ITEM',
		},
	],
	undoHistory: [
		{
			actionId: 2,
			itemId: 'item-2',
			itemName: 'Item 2',
			layoutData: {},
			segmentsExperienceId: '0',
			type: 'UPDATE_EDITABLE_VALUES',
		},
		{
			actionId: 3,
			itemId: 'item-3',
			itemName: 'Item 3',
			layoutData: {},
			segmentsExperienceId: '0',
			type: 'DELETE_ITEM',
		},
	],
};

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/getActionLabel',
	() => jest.fn((action) => action.itemName)
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/multipleUndo',
	() => jest.fn(() => () => Promise.resolve())
);

function renderUndoHistory() {
	return render(
		<StoreAPIContextProvider
			dispatch={mockDispatch}
			getState={() => mockState}
		>
			<UndoHistory />
		</StoreAPIContextProvider>
	);
}

describe('UndoHistory', () => {
	afterEach(() => {
		multipleUndo.mockClear();
	});

	it('shows all redo and undo history items in the list', () => {
		const {getByText} = renderUndoHistory();

		mockState.redoHistory
			.concat(mockState.undoHistory)
			.forEach((historyItem) =>
				expect(getByText(historyItem.itemName)).toBeInTheDocument()
			);
	});

	it('calls multipleUndo with the correct number of actions', async () => {
		const {getByText} = renderUndoHistory();

		const redoHistory = [...mockState.redoHistory].reverse();

		for (let i = 0; i < redoHistory.length; i++) {
			const redoItem = redoHistory[i];

			const button = getByText(redoItem.itemName);

			if (!button.disabled) {
				await act(async () => userEvent.click(button));

				expect(multipleUndo).toBeCalledWith(
					expect.objectContaining({
						numberOfActions: mockState.redoHistory.length - i,
						type: UNDO_TYPES.redo,
					})
				);
			}
		}

		const undoHistory = mockState.undoHistory;

		for (let j = 0; j < undoHistory.length; j++) {
			const undoItem = undoHistory[j];

			const button = getByText(undoItem.itemName);

			if (!button.disabled) {
				await act(async () => userEvent.click(button));

				expect(multipleUndo).toBeCalledWith(
					expect.objectContaining({
						numberOfActions: j,
						type: UNDO_TYPES.undo,
					})
				);
			}
		}
	});
});
