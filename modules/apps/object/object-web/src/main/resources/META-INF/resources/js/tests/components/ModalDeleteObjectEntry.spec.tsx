/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ModalDeleteObjectEntry from '../../components/ModalDeleteObjectEntry';

const MOCK_OBJECT_ENTRY = {
	externalReferenceCode: 'test',
	id: 1,
	objectDefinitionId: 1,
	objectEntry: null,
};

describe('ModalDeleteObjectEntry', () => {
	it('can disable the delete button after clicking on it', async () => {
		const {findByText} = render(
			<ModalDeleteObjectEntry
				byExternalReferenceCodePath="/test"
				portletNamespace="_portletNamespace_"
			/>
		);

		Liferay.fire('_portletNamespace_openModalDeleteObjectEntry', {
			objectEntry: MOCK_OBJECT_ENTRY,
		});

		const deleteButton = await findByText('delete');

		expect(deleteButton).not.toBeDisabled();

		userEvent.click(deleteButton);

		expect(deleteButton).toBeDisabled();
	});

	it('opens the modal only in the widget that fired the event', async () => {
		render(
			<>
				<ModalDeleteObjectEntry
					byExternalReferenceCodePath="/test1"
					portletNamespace="_portletNamespace1_"
				/>

				<ModalDeleteObjectEntry
					byExternalReferenceCodePath="/test2"
					portletNamespace="_portletNamespace2_"
				/>
			</>
		);

		await act(async () => {
			Liferay.fire('_portletNamespace1_openModalDeleteObjectEntry', {
				objectEntry: MOCK_OBJECT_ENTRY,
			});
		});

		expect(document.querySelectorAll('.modal-content')).toHaveLength(1);
	});
});
