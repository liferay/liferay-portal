/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ProjectLinkService, {
	CMPProject,
} from '../../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';
import {IBulkActionFDSData} from '../../../../../src/main/resources/META-INF/resources/js/common/types/BulkActionTask';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import AddAssetsToProjectModalContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/projects/modal/AddAssetsToProjectModalContent';

const mockTriggerAssetBulkAction = jest.fn();

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetBulkAction',
	() => ({
		triggerAssetBulkAction: (dto: unknown) =>
			mockTriggerAssetBulkAction(dto),
	})
);

const GOV_DIGITAL: CMPProject = {
	id: 1,
	scopeKey: 'PROJECT-1',
	title: 'GOV Digital',
};

const TECH_LEADERS: CMPProject = {
	id: 2,
	scopeKey: 'PROJECT-2',
	title: 'TechLeaders Summit',
};

const SELECTED_DATA = {
	items: [
		{entryClassName: 'com.example.Content', id: 11},
		{entryClassName: 'com.example.Content', id: 12},
	],
	selectAll: false,
} as unknown as IBulkActionFDSData;

function mockProjects(projects: CMPProject[]) {
	jest.spyOn(ProjectLinkService, 'getProjects').mockResolvedValue({
		data: projects,
		error: null,
	});
}

function renderModal({closeModal = jest.fn()} = {}) {
	render(
		<AddAssetsToProjectModalContent
			apiURL="/o/bulk"
			closeModal={closeModal}
			cmpProjectObjectDefinitionId={42}
			cmpProjectViewURL="/project"
			selectedData={SELECTED_DATA}
		/>
	);

	return {closeModal};
}

async function selectProject(title: string) {
	await userEvent.click(
		await screen.findByRole('combobox', {name: 'select-project'})
	);

	await userEvent.click(await screen.findByRole('option', {name: title}));
}

describe('AddAssetsToProjectModalContent', () => {
	afterEach(() => {
		jest.clearAllMocks();
		jest.restoreAllMocks();
	});

	it('disables confirm until a project is selected', async () => {
		mockProjects([GOV_DIGITAL]);

		renderModal();

		const confirmButton = await screen.findByRole('button', {
			name: 'confirm',
		});

		expect(confirmButton).toBeDisabled();

		await selectProject('GOV Digital');

		expect(confirmButton).toBeEnabled();
	});

	it('drops folders from the bulk payload', async () => {
		mockProjects([GOV_DIGITAL]);

		render(
			<AddAssetsToProjectModalContent
				apiURL="/o/bulk"
				closeModal={jest.fn()}
				cmpProjectObjectDefinitionId={42}
				selectedData={
					{
						items: [
							{
								entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
								id: 10,
							},
							{entryClassName: 'com.example.Content', id: 11},
						],
						selectAll: false,
					} as unknown as IBulkActionFDSData
				}
			/>
		);

		await selectProject('GOV Digital');

		await userEvent.click(screen.getByRole('button', {name: 'confirm'}));

		expect(mockTriggerAssetBulkAction).toHaveBeenCalledWith(
			expect.objectContaining({
				selectedData: expect.objectContaining({
					items: [{entryClassName: 'com.example.Content', id: 11}],
				}),
			})
		);
	});

	it('removes a selected project card', async () => {
		mockProjects([GOV_DIGITAL]);

		renderModal();

		await selectProject('GOV Digital');

		expect(
			await screen.findByRole('link', {name: 'GOV Digital'})
		).toBeInTheDocument();

		await userEvent.click(screen.getByLabelText('remove'));

		expect(
			screen.queryByRole('link', {name: 'GOV Digital'})
		).not.toBeInTheDocument();

		expect(screen.getByRole('button', {name: 'confirm'})).toBeDisabled();
	});

	it('shows the empty state when there are no projects', async () => {
		mockProjects([]);

		renderModal();

		expect(await screen.findByText('no-projects-yet')).toBeInTheDocument();

		expect(
			screen.queryByRole('combobox', {name: 'select-project'})
		).not.toBeInTheDocument();
	});

	it('starts the bulk task with the selected project scope keys', async () => {
		mockProjects([GOV_DIGITAL, TECH_LEADERS]);

		const {closeModal} = renderModal();

		await selectProject('GOV Digital');
		await selectProject('TechLeaders Summit');

		await userEvent.click(screen.getByRole('button', {name: 'confirm'}));

		expect(mockTriggerAssetBulkAction).toHaveBeenCalledWith(
			expect.objectContaining({
				additionalData: {
					targetName: 'GOV Digital, TechLeaders Summit',
				},
				keyValues: {projectScopeKeys: ['PROJECT-1', 'PROJECT-2']},
				type: 'AddObjectToProjectBulkSelectionAction',
			})
		);

		const [dto] = mockTriggerAssetBulkAction.mock.calls[0];

		dto.onCreateSuccess({data: {}, error: null});

		await waitFor(() => expect(closeModal).toHaveBeenCalled());
	});
});
