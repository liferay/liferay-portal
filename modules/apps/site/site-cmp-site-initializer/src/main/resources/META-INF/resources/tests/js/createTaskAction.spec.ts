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
				projectObjectDefinitionId: 123,
			});
		});
	});

	describe('redirect path', () => {
		afterEach(() => {
			window.history.replaceState({}, '', '/');
		});

		it('navigates to redirect URL', () => {
			createTaskAction({
				addProjectURL: '/add-project',
				addTaskURL: '/add-task',
				projectObjectDefinitionId: 123,
				redirect: 'http://localhost/redirect-url',
			});

			expect(mockNavigate).toHaveBeenCalledWith(
				'http://localhost/redirect-url'
			);
			expect(mockOpenModal).not.toHaveBeenCalled();
		});

		it('repoints the redirect parameter to the current location', () => {
			window.history.replaceState(
				{},
				'',
				'/web/cms/tasks?PROJECT_TASKS_fdsConfig=(view:kanban)'
			);

			createTaskAction({
				addProjectURL: '/add-project',
				addTaskURL: '/add-task',
				projectObjectDefinitionId: 123,
				redirect:
					'http://localhost/web/cms/add_task?projectId=42&redirect=http://localhost/web/cms/tasks',
			});

			expect(mockOpenModal).not.toHaveBeenCalled();
			expect(mockNavigate).toHaveBeenCalledTimes(1);

			const url = new URL(
				mockNavigate.mock.calls[0][0],
				window.location.origin
			);

			expect(url.pathname).toBe('/web/cms/add_task');
			expect(url.searchParams.get('projectId')).toBe('42');
			expect(url.searchParams.get('redirect')).toBe(window.location.href);
		});
	});
});
