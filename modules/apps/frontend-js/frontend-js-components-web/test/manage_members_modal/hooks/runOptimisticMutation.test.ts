/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ActionTypes} from '../../../src/main/resources/META-INF/resources/manage_members_modal/hooks/membersReducer';
import {runOptimisticMutation} from '../../../src/main/resources/META-INF/resources/manage_members_modal/hooks/runOptimisticMutation';
import {MemberType} from '../../../src/main/resources/META-INF/resources/manage_members_modal/types';
import openToast from '../../../src/main/resources/META-INF/resources/toast/openToast';

jest.mock(
	'../../../src/main/resources/META-INF/resources/toast/openToast',
	() => ({
		__esModule: true,
		default: jest.fn(),
	})
);

const XSS_NAME = '<img src=x onerror=alert(document.domain)>';

const SUCCESS_MESSAGE = 'User {0} successfully added to design library.';

const ERROR_MESSAGE = 'Failed to add user {0} to design library.';

function buildMutation(error: string | null) {
	return {
		errorMessage: ERROR_MESSAGE,
		name: XSS_NAME,
		optimisticAction: {
			payload: {item: {id: '1', roles: []}, type: MemberType.USERS},
			type: ActionTypes.AddMemberSuccess,
		},
		performMutation: () => Promise.resolve({error}),
		rollbackAction: {
			payload: {id: '1', type: MemberType.USERS},
			type: ActionTypes.AddMemberFailure,
		},
		successMessage: SUCCESS_MESSAGE,
	} as any;
}

function getToastElement() {
	const [{message}] = (openToast as jest.Mock).mock.calls[0];

	const container = document.createElement('div');

	container.innerHTML = message;

	return container;
}

describe('runOptimisticMutation', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('displays the member name as plain text in the success toast', async () => {
		await runOptimisticMutation(jest.fn(), buildMutation(null));

		const container = getToastElement();

		expect(container.querySelector('img')).toBeNull();
		expect(container.textContent).toContain(XSS_NAME);
	});

	it('displays the member name as plain text in the error toast', async () => {
		await runOptimisticMutation(jest.fn(), buildMutation('error'));

		const container = getToastElement();

		expect(container.querySelector('img')).toBeNull();
		expect(container.textContent).toContain(XSS_NAME);
	});

	it('displays no success toast when there is no success message', async () => {
		await runOptimisticMutation(jest.fn(), {
			...buildMutation(null),
			successMessage: undefined,
		});

		expect(openToast).not.toHaveBeenCalled();
	});
});
