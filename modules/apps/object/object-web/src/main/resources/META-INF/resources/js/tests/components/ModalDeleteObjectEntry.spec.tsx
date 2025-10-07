/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
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
	const {Liferay: originalLiferay} = window;

	beforeAll(() => {
		window['Liferay'] = {
			...originalLiferay,

			detach: (name, fn): void => {
				window.removeEventListener(name as string, fn as EventListener);
			},
			fire: (name, payload) => {
				const event = document.createEvent('CustomEvent');

				event.initCustomEvent(name);

				if (payload) {
					Object.keys(payload).forEach((key: string) => {
						(event as any)[key] = payload[key];
					});
				}

				window.dispatchEvent(event);
			},

			on: (name, fn) => {
				if (fn) {
					window.addEventListener(
						name as string,
						fn as EventListener
					);
				}

				return {
					detach: () => {
						if (fn) {
							window.removeEventListener(
								name as string,
								fn as EventListener
							);
						}

						return 0;
					},
				};
			},
		};
	});

	afterAll(() => {
		cleanup();

		window.Liferay = originalLiferay;

		jest.resetAllMocks();
	});

	it('can disable the delete button after clicking on it', async () => {
		const {findByText} = render(
			<ModalDeleteObjectEntry byExternalReferenceCodePath="/test" />
		);

		Liferay.fire('openModalDeleteObjectEntry', {
			objectEntry: MOCK_OBJECT_ENTRY,
		});

		const deleteButton = await findByText('delete');

		expect(deleteButton).not.toBeDisabled();

		userEvent.click(deleteButton);

		expect(deleteButton).toBeDisabled();
	});
});
