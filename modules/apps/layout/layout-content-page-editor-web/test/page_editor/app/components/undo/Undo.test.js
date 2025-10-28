/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import Undo from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/Undo';
import useUndoRedoActions from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/useUndoRedoActions';
import StoreMother from '../../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const mockDispatch = jest.fn((a) => {
	if (typeof a === 'function') {
		return a(mockDispatch);
	}
});

const INITIAL_STATE = {
	redoHistory: [
		{
			itemId: 'item-0',
			segmentsExperienceId: 0,
		},
	],
	undoHistory: [
		{
			itemId: 'item-1',
			segmentsExperienceId: 0,
		},
		{
			itemId: 'item-2',
			segmentsExperienceId: 0,
		},
	],
};

function renderUndoComponent({state = INITIAL_STATE} = {}) {
	return render(
		<StoreMother.Component dispatch={mockDispatch} getState={() => state}>
			<Undo />
		</StoreMother.Component>
	);
}

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/useUndoRedoActions',
	() => {
		const undoRedo = {onRedo: jest.fn(), onUndo: jest.fn()};

		return () => undoRedo;
	}
);

describe('Undo', () => {
	it('renders Undo component and makes sure that the buttons has aria-label and title', () => {
		renderUndoComponent();

		['undo', 'redo'].forEach((action) => {
			expect(screen.getByLabelText(action)).toBeInTheDocument();
			expect(screen.getByTitle(action)).toBeInTheDocument();
		});
	});

	it('calls onUndo when the Undo button is pressed', async () => {
		renderUndoComponent();

		const {onUndo} = useUndoRedoActions();

		await userEvent.click(screen.getByTitle('undo'));

		expect(onUndo).toBeCalled();
	});

	it('calls onRedo when the Redo button is pressed', async () => {
		renderUndoComponent();

		const {onRedo} = useUndoRedoActions();

		await userEvent.click(screen.getByTitle('redo'));

		expect(onRedo).toBeCalled();
	});

	it('disables the Undo button when there are no undo history items', () => {
		renderUndoComponent({state: {...INITIAL_STATE, undoHistory: []}});

		expect(screen.getByTitle('undo')).toBeDisabled();
	});

	it('disables the Redo button when there are no redo history items', () => {
		renderUndoComponent({state: {...INITIAL_STATE, redoHistory: []}});

		expect(screen.getByTitle('redo')).toBeDisabled();
	});
});
