/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {renderHook} from '@testing-library/react-hooks';
import React from 'react';

import {SWITCH_SIDEBAR_PANEL} from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/types';
import useOnToggleSidebars from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/useOnToggleSidebars';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const INITIAL_STATE = {
	sidebar: {hidden: true},
};

const mockDispatch = jest.fn((a) => {
	if (typeof a === 'function') {
		return a(mockDispatch);
	}
});

const wrapper = ({children, state = INITIAL_STATE}) => (
	<StoreMother.Component dispatch={mockDispatch} getState={() => state}>
		{children}
	</StoreMother.Component>
);

describe('useOnToggleSidebars', () => {
	it('calls dispatch when the sidebar is open', () => {
		const {result} = renderHook(() => useOnToggleSidebars(), {wrapper});

		result.current();

		expect(mockDispatch).toBeCalledWith(
			expect.objectContaining({hidden: false, type: SWITCH_SIDEBAR_PANEL})
		);
	});

	it('calls dispatch when the sidebar is closed', () => {
		const {result} = renderHook(() => useOnToggleSidebars(), {
			initialProps: {
				sidebar: {hidden: true},
			},
			wrapper,
		});

		result.current();

		expect(mockDispatch).toHaveBeenNthCalledWith(
			2,
			expect.objectContaining({hidden: false, type: SWITCH_SIDEBAR_PANEL})
		);
	});
});
