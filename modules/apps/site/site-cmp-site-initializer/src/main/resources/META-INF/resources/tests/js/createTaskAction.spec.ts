/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import SelectProjectModalContent from '../../js/components/modal/SelectProjectModalContent';
import createTaskAction from '../../js/components/props_transformer/actions/createTaskAction';
import {
	mockCloseModal,
	mockOpenModal,
} from '../../tests/js/__mocks__/frontend-js-components-web';
import {mockNavigate} from '../../tests/js/__mocks__/frontend-js-web';

const mockSelectProjectModalContent = SelectProjectModalContent as jest.Mock;

jest.mock('../../js/components/modal/SelectProjectModalContent', () =>
	jest.fn()
);

describe('createTaskAction', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	describe('modal path', () => {
		it('opens modal when redirect is not provided', () => {
			createTaskAction({
				addProjectURL: '/add-project',
				addTaskURL: '/add-task',
				projectObjectDefinitionId: 123,
			});

			expect(mockNavigate).not.toHaveBeenCalled();
			expect(mockOpenModal).toHaveBeenCalledTimes(1);

			const openModalConfig = mockOpenModal.mock.calls[0][0];

			openModalConfig.contentComponent({closeModal: mockCloseModal});

			expect(mockSelectProjectModalContent).toHaveBeenCalledWith({
				addProjectURL: '/add-project',
				addTaskURL: '/add-task',
				closeModal: mockCloseModal,
			});
		});
	});

	describe('redirect path', () => {
		it('navigates to redirect URL', () => {
			createTaskAction({
				addProjectURL: '/add-project',
				addTaskURL: '/add-task',
				projectObjectDefinitionId: 123,
				redirect: 'http://localhost/redirect-url',
			});

			expect(mockNavigate).toHaveBeenCalledWith('/redirect-url');
			expect(mockOpenModal).not.toHaveBeenCalled();
		});
	});
});
