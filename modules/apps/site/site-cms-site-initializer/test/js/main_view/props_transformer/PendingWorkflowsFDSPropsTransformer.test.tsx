/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import {getPendingWorkflowTask} from '../../../../src/main/resources/META-INF/resources/js/common/services/WorkflowService';
import {openActionNotAllowedModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openActionNotAllowedModal';
import {openCMSModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import {displayErrorToast} from '../../../../src/main/resources/META-INF/resources/js/common/utils/toastUtil';
import AssignToModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/home/modal/AssignToModalContent';
import PendingWorkflowsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/PendingWorkflowsFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	FDS_EVENT: {UPDATE_DISPLAY: 'updateDisplay'},
	findAction: jest.fn(),
	replaceTokens: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	dateUtils: {
		fromNow: jest.fn(() => '5 minutes ago'),
	},
	sub: jest.fn((key, ...args) => {
		let result = key;

		args.forEach((arg, i) => {
			result = result.replace(`{${i}}`, arg);
		});

		return result;
	}),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/WorkflowService',
	() => ({getPendingWorkflowTask: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openActionNotAllowedModal',
	() => ({openActionNotAllowedModal: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/toastUtil',
	() => ({displayErrorToast: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/home/modal/AssignToModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/home/modal/UpdateDueDateModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SimpleActionLinkRenderer',
	() => ({__esModule: true, default: () => null})
);

const mockLiferayLanguageGet = jest.fn((key) => {
	const languageMap: {[key: string]: string} = {
		'modified-x': 'Modified {0}',
		'modified-x-by-x': 'Modified {0} by {1}',
	};

	return languageMap[key] || key;
});

(global as any).Liferay = {
	...(global as any).Liferay,
	Language: {get: mockLiferayLanguageGet},
};

function renderTitle(itemData: any) {
	const {customRenderers} = PendingWorkflowsFDSPropsTransformer({
		additionalProps: {},
	} as any);

	const TitleRenderer = (customRenderers.listSection[0] as any).component;

	return render(
		<TitleRenderer actions={[]} itemData={itemData} value="Pending demo" />
	);
}

describe('PendingWorkflowsFDSPropsTransformer', () => {
	it('renders the modification info for an item with a creator', () => {
		renderTitle({
			dateModified: '2026-08-10T10:00:00Z',
			embedded: {creator: {name: 'Test User'}},
		});

		expect(
			screen.getByText('Modified 5 minutes ago by Test User')
		).toBeInTheDocument();
	});

	it('renders the modification info for an item whose creator is missing', () => {
		renderTitle({
			dateModified: '2026-08-10T10:00:00Z',
			embedded: {},
		});

		expect(screen.getByText('Modified 5 minutes ago')).toBeInTheDocument();
	});

	it('renders a dash for an item without a modification date', () => {
		renderTitle({embedded: {creator: {name: 'Test User'}}});

		expect(screen.getByText('--')).toBeInTheDocument();
	});
});

const ITEM = {
	embedded: {id: 42},
	entryClassName: 'com.liferay.object.model.ObjectEntry',
};

const WORKFLOW_TASK = {dateDue: '2099-12-31T10:00:00Z', id: 7};

function clickAction(id: string) {
	const {onActionDropdownItemClick} = PendingWorkflowsFDSPropsTransformer({
		additionalProps: {},
	} as any);

	const event = {preventDefault: jest.fn()};

	onActionDropdownItemClick({
		action: {data: {id}},
		event,
		itemData: ITEM,
	} as any);

	return event;
}

describe('PendingWorkflowsFDSPropsTransformer row actions', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(getPendingWorkflowTask as jest.Mock).mockResolvedValue(WORKFLOW_TASK);
	});

	it('resolves the live workflow task before opening the assign to me modal', async () => {
		clickAction('assign-to-me');

		await waitFor(() => expect(openCMSModal).toHaveBeenCalledTimes(1));

		expect(getPendingWorkflowTask).toHaveBeenCalledWith({
			assetClassName: ITEM.entryClassName,
			assetPrimaryKey: ITEM.embedded.id,
		});

		const {contentComponent} = (openCMSModal as jest.Mock).mock.calls[0][0];

		render(contentComponent({closeModal: jest.fn()}));

		expect(AssignToModalContent).toHaveBeenCalledWith(
			expect.objectContaining({assignable: false, workflowTaskId: 7}),
			expect.anything()
		);
	});

	it('marks the task as assignable when assigning to another user', async () => {
		clickAction('assign-to');

		await waitFor(() => expect(openCMSModal).toHaveBeenCalledTimes(1));

		const {contentComponent} = (openCMSModal as jest.Mock).mock.calls[0][0];

		render(contentComponent({closeModal: jest.fn()}));

		expect(AssignToModalContent).toHaveBeenCalledWith(
			expect.objectContaining({assignable: true, workflowTaskId: 7}),
			expect.anything()
		);
	});

	it('warns the user when the task no longer exists', async () => {
		(getPendingWorkflowTask as jest.Mock).mockResolvedValue(null);

		clickAction('update-due-date');

		await waitFor(() =>
			expect(openActionNotAllowedModal).toHaveBeenCalledTimes(1)
		);

		expect(openCMSModal).not.toHaveBeenCalled();
	});

	it('surfaces an error toast when the task cannot be resolved', async () => {
		(getPendingWorkflowTask as jest.Mock).mockRejectedValue(
			new Error('Request failed')
		);

		clickAction('update-due-date');

		await waitFor(() =>
			expect(displayErrorToast).toHaveBeenCalledWith('Request failed')
		);

		expect(openCMSModal).not.toHaveBeenCalled();
	});

	it('leaves the edit action to its own link', async () => {
		const event = clickAction('edit');

		expect(event.preventDefault).not.toHaveBeenCalled();
		expect(getPendingWorkflowTask).not.toHaveBeenCalled();
	});
});
