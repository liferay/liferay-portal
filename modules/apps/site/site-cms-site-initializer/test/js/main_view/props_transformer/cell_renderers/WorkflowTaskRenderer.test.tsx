/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import mockSub from '../../../../../../../frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/sub';
import {WorkflowTask} from '../../../../../src/main/resources/META-INF/resources/js/common/types/WorkflowTask';
import WorkflowTaskRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/WorkflowTaskRenderer';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual<typeof import('frontend-js-web')>('frontend-js-web'),
	sub: mockSub,
}));

describe('WorkflowTaskRenderer', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the task sentence from the translated language key', () => {
		jest.spyOn(Liferay.Language, 'get').mockImplementation((key) =>
			key === 'x-sent-you-x-for-x-in-the-workflow'
				? '{0} te envió {1} para {2} en el flujo de trabajo.'
				: key
		);

		const itemData: WorkflowTask = {
			assignedDate: '2026-09-30T10:00:00Z',
			assigneePerson: {id: 1, name: 'Test User'},
			auditUser: 'Test User',
			auditUserImageURL: '',
			completed: false,
			dateDue: '',
			id: '1',
			myWorkflowTasksURL:
				'http://www.test.com/myWorkflowTasks?p_p_id=com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet',
			name: 'review',
			objectReviewed: {
				assetTitle: 'Test Content',
				assetType: 'Basic Web Content',
				id: 2,
			},
			workflowLogs: [],
		};

		const {container} = render(
			<WorkflowTaskRenderer itemData={itemData} />
		);

		expect(container.querySelector('.list-group-text')).toHaveTextContent(
			'Test User te envió Test Content para review en el flujo de trabajo.'
		);

		const link = screen.getByRole('link', {name: 'Test Content'});

		expect(link).toHaveAttribute(
			'href',
			expect.stringContaining('workflowTaskId=1')
		);
	});
});
