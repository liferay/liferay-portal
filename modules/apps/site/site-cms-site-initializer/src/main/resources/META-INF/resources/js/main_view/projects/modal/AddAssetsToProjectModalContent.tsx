/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayModal from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import React, {useEffect, useMemo, useState} from 'react';

import ProjectCard from '../../../common/components/ProjectCard';
import ProjectLinkService, {
	CMPProject,
} from '../../../common/services/ProjectLinkService';
import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import {triggerAssetBulkAction} from '../../props_transformer/actions/triggerAssetBulkAction';

type AddAssetsToProjectModalContentProps = {
	apiURL?: string;
	closeModal: () => void;
	cmpProjectObjectDefinitionId: number | null;
	cmpProjectViewURL?: string;
	selectedData: IBulkActionFDSData;
};

/**
 * Links every selected content list asset to one or more CMP projects in a
 * single bulk task. Picking a project adds it to a removable card list;
 * confirming starts the bulk task, and the completion toast names the
 * projects the assets were added to.
 */
export default function AddAssetsToProjectModalContent({
	apiURL,
	closeModal,
	cmpProjectObjectDefinitionId,
	cmpProjectViewURL,
	selectedData: selected,
}: AddAssetsToProjectModalContentProps) {
	const [loading, setLoading] = useState(true);
	const [projects, setProjects] = useState<CMPProject[]>([]);
	const [selectedProjects, setSelectedProjects] = useState<CMPProject[]>([]);
	const [submitting, setSubmitting] = useState(false);

	const isMounted = useIsMounted();

	// Folders can appear in the list selection but cannot be linked to a
	// project, so they are dropped from the bulk payload.

	const selectedData = useMemo(
		() => ({
			...selected,
			items:
				selected?.items?.filter(
					({entryClassName}) =>
						entryClassName !== OBJECT_ENTRY_FOLDER_CLASS_NAME
				) || [],
		}),
		[selected]
	);

	useEffect(() => {
		const controller = new AbortController();

		ProjectLinkService.getProjects({
			cmpProjectObjectDefinitionId,
			signal: controller.signal,
		}).then(({data, error}) => {
			if (!isMounted()) {
				return;
			}

			if (data) {
				setProjects(data);
			}
			else if (error) {
				displayErrorToast(error);
			}

			setLoading(false);
		});

		return () => controller.abort();
	}, [cmpProjectObjectDefinitionId, isMounted]);

	const selectableProjects = useMemo(() => {
		const selectedProjectIds = new Set(selectedProjects.map(({id}) => id));

		return projects.filter(({id}) => !selectedProjectIds.has(id));
	}, [projects, selectedProjects]);

	const confirm = () => {
		setSubmitting(true);

		triggerAssetBulkAction<'AddObjectToProjectBulkSelectionAction'>({
			additionalData: {
				targetName: selectedProjects.map(({title}) => title).join(', '),
			},
			apiURL,
			keyValues: {
				projectScopeKeys: selectedProjects
					.map(({scopeKey}) => scopeKey)
					.filter((scopeKey): scopeKey is string =>
						Boolean(scopeKey)
					),
			},
			onCreateError: (response) => {
				setSubmitting(false);

				displayErrorToast(response.error ?? undefined);
			},
			onCreateSuccess: (response) => {
				if (response.error) {
					setSubmitting(false);

					displayErrorToast(response.error);

					return;
				}

				closeModal();
			},
			overrideDefaultErrorToast: true,
			selectedData,
			type: 'AddObjectToProjectBulkSelectionAction',
		});
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-assets-to-project')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					{selectedData.selectAll
						? Liferay.Language.get(
								'choose-the-projects-you-would-like-to-add-these-assets-to'
							)
						: Liferay.Language.get(
								'choose-the-projects-you-would-like-to-add-this-asset-to'
							)}
				</p>

				{!loading && !projects.length ? (
					<p className="mb-0 text-secondary">
						{Liferay.Language.get('no-projects-yet')}
					</p>
				) : (
					<>
						<label>{Liferay.Language.get('select-project')}</label>

						<Picker<CMPProject>
							aria-label={Liferay.Language.get('select-project')}
							items={selectableProjects}
							onSelectionChange={(key) => {
								const project = selectableProjects.find(
									({id}) => id === Number(key)
								);

								if (project) {
									setSelectedProjects((previous) => [
										...previous,
										project,
									]);
								}
							}}
							placeholder={Liferay.Language.get(
								'search-or-select-a-project'
							)}
							selectedKey=""
						>
							{(project) => (
								<Option
									key={project.id}
									textValue={project.title}
								>
									{project.title}
								</Option>
							)}
						</Picker>

						<div className="cms-linked-projects-list">
							{selectedProjects.map((project) => (
								<ProjectCard
									key={project.id}
									onRemove={() =>
										setSelectedProjects((previous) =>
											previous.filter(
												({id}) => id !== project.id
											)
										)
									}
									project={project}
									projectViewURL={cmpProjectViewURL}
								/>
							))}
						</div>
					</>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!selectedProjects.length || submitting}
							displayType="primary"
							onClick={confirm}
							type="button"
						>
							{Liferay.Language.get('confirm')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
