/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, waitFor} from '@testing-library/react';
import React from 'react';

import ModalSchedulePublication from '../../../object_entries/object_entry/ModalSchedulePublication';

describe('ModalSchedulePublication', () => {
	it('opens the modal only in the widget that fired the event', async () => {
		render(
			<>
				<ModalSchedulePublication
					hiddenScheduleValues={{}}
					portletNamespace="_portletNamespace1_"
					submitRef=""
					value=""
				/>

				<ModalSchedulePublication
					hiddenScheduleValues={{}}
					portletNamespace="_portletNamespace2_"
					submitRef=""
					value=""
				/>
			</>
		);

		await act(async () => {
			Liferay.fire('_portletNamespace1_openModalSchedulePublication');
		});

		expect(document.querySelectorAll('.modal-content')).toHaveLength(1);

		await waitFor(() =>
			expect(
				document.getElementById('_portletNamespace1_displayDate')
			).toBeInTheDocument()
		);
	});
});
