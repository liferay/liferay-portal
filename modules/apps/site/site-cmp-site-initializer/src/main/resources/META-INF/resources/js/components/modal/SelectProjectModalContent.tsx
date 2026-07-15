/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker, Text} from '@clayui/core';
import {FetchPolicy, useResource} from '@clayui/data-provider';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {useState} from 'react';

import './SelectProjectModalContent.scss';

export interface Project {
	embedded: {
		id: number;
		scopeId: number;
		title: string;
	};
}

export default function SelectProjectModalContent({
	addProjectURL,
	addTaskURL,
	closeModal,
	projectObjectDefinitionId,
}: {
	addProjectURL: string;
	addTaskURL: string;
	closeModal: () => void;
	projectObjectDefinitionId: number;
}) {
	const [errorMessage, setErrorMessage] = useState<string>('');
	const [selectedProject, setSelectedProject] = useState<Project | null>();

	const {
		resource,
	}: {
		resource: {
			items: Project[];
		};
	} = useResource({
		fetchOptions: {
			credentials: 'include',
			headers: new Headers({'x-csrf-token': Liferay.authToken}),
			method: 'GET',
		},
		fetchPolicy: FetchPolicy.CacheFirst,
		link: `${Liferay.ThemeDisplay.getPortalURL()}/o/search/v1.0/search?emptySearch=true&filter=objectDefinitionId eq ${projectObjectDefinitionId}&nestedFields=embedded`,
	});

	const projects: Project[] = resource?.items?.length
		? resource.items
		: [
				{
					embedded: {
						id: -1,
						scopeId: -1,
						title: Liferay.Language.get('new-project'),
					},
				},
			];

	const handleSave = () => {
		if (!selectedProject) {
			setErrorMessage(Liferay.Language.get('this-field-is-required'));

			return;
		}

		const url = new URL(addTaskURL);

		url.searchParams.set(
			'projectGroupId',
			String(selectedProject.embedded.scopeId)
		);
		url.searchParams.set('projectId', String(selectedProject.embedded.id));
		url.searchParams.set('redirect', window.location.href);

		navigate(url);
	};

	return (
		<div className="lfr-cmp__select-project-modal">
			<ClayModal.Header>
				{Liferay.Language.get('new-task')}
			</ClayModal.Header>

			<ClayModal.Body>
				<FieldBase
					errorMessage={errorMessage}
					label={Liferay.Language.get('project')}
					required
				>
					<Picker<Project>
						aria-label={Liferay.Language.get('project')}
						items={projects}
						messages={{
							itemDescribedby: Liferay.Language.get(
								'you-are-currently-on-a-text-element,-inside-of-a-list-box'
							),
							itemSelected: Liferay.Language.get('x-selected'),
							scrollToBottomAriaLabel:
								Liferay.Language.get('scroll-to-bottom'),
							scrollToTopAriaLabel:
								Liferay.Language.get('scroll-to-top'),
						}}
						onSelectionChange={(key) => {
							const project = projects.find(
								({embedded: {id}}) =>
									id === Number(key) && id !== -1
							);

							if (project) {
								setErrorMessage('');
								setSelectedProject(project);
							}
						}}
						placeholder={Liferay.Language.get('select-a-project')}
					>
						{({embedded: {id, title}}) => (
							<Option key={id} textValue={title}>
								{id === -1 ? (
									<div className="lfr-cmp__select-project-modal-new-project">
										<Text
											as="p"
											color="secondary"
											size={3}
											weight="normal"
										>
											<span>
												{Liferay.Language.get(
													'no-projects-created'
												)}
											</span>
										</Text>

										<ClayButton
											className="lfr-cmp__select-project-modal-new-project-btn"
											displayType="secondary"
											onClick={() =>
												navigate(new URL(addProjectURL))
											}
										>
											<span>
												{Liferay.Language.get(
													'new-project'
												)}
											</span>

											<ClayIcon symbol="shortcut" />
										</ClayButton>
									</div>
								) : (
									<span>{title}</span>
								)}
							</Option>
						)}
					</Picker>
				</FieldBase>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" onClick={handleSave}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</div>
	);
}
