/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render} from '@testing-library/react';
import React, {useEffect} from 'react';

import {MULTI_SELECT_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/multiSelectTypes';
import {
	ControlsProvider,
	useActivateMultiSelect,
	useMultiSelectTypeRef,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';

const observed: Array<string | null> = [];

function Consumer() {
	const activateMultiSelect = useActivateMultiSelect();
	const multiSelectTypeRef = useMultiSelectTypeRef();

	useEffect(() => {
		const onClick = () => {
			observed.push(multiSelectTypeRef.current);

			activateMultiSelect(null);

			observed.push(multiSelectTypeRef.current);
		};

		const onKeydown = () => {
			activateMultiSelect(MULTI_SELECT_TYPES.range);

			observed.push(multiSelectTypeRef.current);
		};

		window.addEventListener('click', onClick, true);
		window.addEventListener('keydown', onKeydown, true);

		return () => {
			window.removeEventListener('click', onClick, true);
			window.removeEventListener('keydown', onKeydown, true);
		};
	}, [activateMultiSelect, multiSelectTypeRef]);

	return null;
}

describe('MultiSelectProvider', () => {
	it('keeps the multi select ref deferred when activating and clears it within the event that deactivates it', () => {
		render(
			<ControlsProvider>
				<Consumer />
			</ControlsProvider>
		);

		act(() => {
			window.dispatchEvent(new KeyboardEvent('keydown', {key: 'Shift'}));
		});

		act(() => {
			window.dispatchEvent(new MouseEvent('click'));
		});

		expect(observed).toEqual([null, MULTI_SELECT_TYPES.range, null]);
	});
});
