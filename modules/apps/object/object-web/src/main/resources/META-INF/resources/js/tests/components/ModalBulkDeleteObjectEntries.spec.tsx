/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import React from 'react';

import ModalBulkDeleteObjectEntries from '../../components/ModalBulkDeleteObjectEntries';

const MOCK_OBJECT_DEFINITION = {
	objectDefinitionId: '1',
	scope: 'company' as const,
};

const MOCK_SELECTED_DATA = {
	items: [{}],
	selectAll: false,
};

describe('ModalBulkDeleteObjectEntries', () => {
	it('opens the modal only in the widget that fired the event', async () => {
		render(
			<>
				<ModalBulkDeleteObjectEntries
					objectDefinition={MOCK_OBJECT_DEFINITION}
					portletNamespace="_portletNamespace1_"
				/>

				<ModalBulkDeleteObjectEntries
					objectDefinition={MOCK_OBJECT_DEFINITION}
					portletNamespace="_portletNamespace2_"
				/>
			</>
		);

		await act(async () => {
			Liferay.fire(
				'_portletNamespace1_openModalBulkDeleteObjectEntries',
				{
					selectedData: MOCK_SELECTED_DATA,
				}
			);
		});

		expect(document.querySelectorAll('.modal-content')).toHaveLength(1);
	});
});
