/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {renderHook} from '@testing-library/react-hooks';
import React from 'react';

import useUndoRedoActions from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/useUndoRedoActions';
import redo from '../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/redo';
import undo from '../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/undo';
import StoreMother from '../../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/redo',
	() => jest.fn(() => 'redo')
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/undo',
	() => jest.fn(() => 'undo')
);

const mockDispatch = jest.fn((a) => {
	if (typeof a === 'function') {
		return a(mockDispatch);
	}
});

const wrapper = ({children}) => (
	<StoreMother.Component dispatch={mockDispatch} getState={() => {}}>
		{children}
	</StoreMother.Component>
);

describe('useUndoRedoActions', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('calls dispatch method with redo() when onRedo is called', () => {
		const {result} = renderHook(() => useUndoRedoActions(), {
			wrapper,
		});

		renderHook(() => result.current.onRedo(), {
			wrapper,
		});

		expect(mockDispatch).toHaveBeenNthCalledWith(1, redo());
	});

	it('calls dispatch method with undo() when onUndo is called', () => {
		const {result} = renderHook(() => useUndoRedoActions(), {
			wrapper,
		});

		renderHook(() => result.current.onUndo(), {
			wrapper,
		});

		expect(mockDispatch).toHaveBeenNthCalledWith(1, undo());
	});
});
