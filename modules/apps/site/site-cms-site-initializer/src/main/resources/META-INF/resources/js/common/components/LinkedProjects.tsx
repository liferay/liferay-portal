/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import ProjectLinkService, {
	CMPProject,
	ProjectLink,
} from '../services/ProjectLinkService';
import ProjectCard from './ProjectCard';

import '../../../css/components/LinkedProjects.scss';

type LinkedProjectsProps = {
	cmpProjectLinkObjectDefinitionId?: number | null;
	cmpProjectObjectDefinitionId?: number | null;
	entryClassName?: string;
	entryExternalReferenceCode?: string;
	entryGroupExternalReferenceCode?: string;
	projectViewURL?: string;
};

/**
 * Links a CMP asset to one or more CMP projects. Selecting a project from the
 * picker links it, and removing a card unlinks it; both are auto-saved. Shared
 * by the content editor's Projects side panel and the content list's info
 * panel.
 */
export default function LinkedProjects({
	cmpProjectLinkObjectDefinitionId,
	cmpProjectObjectDefinitionId,
	entryClassName,
	entryExternalReferenceCode,
	entryGroupExternalReferenceCode,
	projectViewURL,
}: LinkedProjectsProps) {
	const [links, setLinks] = useState<ProjectLink[]>([]);
	const [projects, setProjects] = useState<CMPProject[]>([]);

	const isMounted = useIsMounted();

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
				openToast({message: error, type: 'danger'});
			}
		});

		return () => controller.abort();
	}, [cmpProjectObjectDefinitionId, isMounted]);

	useEffect(() => {
		const controller = new AbortController();

		ProjectLinkService.getProjectLinks({
			cmpProjectLinkObjectDefinitionId,
			entryClassName,
			entryExternalReferenceCode,
			entryGroupExternalReferenceCode,
			signal: controller.signal,
		}).then(({data, error}) => {
			if (!isMounted()) {
				return;
			}

			if (data) {
				setLinks(data);
			}
			else if (error) {
				openToast({message: error, type: 'danger'});
			}
		});

		return () => controller.abort();
	}, [
		cmpProjectLinkObjectDefinitionId,
		entryClassName,
		entryExternalReferenceCode,
		entryGroupExternalReferenceCode,
		isMounted,
	]);

	const linkedProjects = useMemo(() => {
		const projectsById = new Map(
			projects.map((project) => [project.id, project])
		);

		const joinedProjects: CMPProject[] = [];

		links.forEach((link) => {
			const project = projectsById.get(link.cmpProjectObjectEntryId);

			if (project) {
				joinedProjects.push({...project, linkId: link.id});
			}
		});

		return joinedProjects;
	}, [links, projects]);

	const selectableProjects = useMemo(() => {
		const linkedProjectIds = new Set(
			links.map(({cmpProjectObjectEntryId}) => cmpProjectObjectEntryId)
		);

		return projects.filter(({id}) => !linkedProjectIds.has(id));
	}, [links, projects]);

	const linkProject = async (project: CMPProject) => {
		setLinks((previous) => [
			...previous,
			{cmpProjectObjectEntryId: project.id},
		]);

		const {data, error} = await ProjectLinkService.linkProject({
			entryClassName,
			entryExternalReferenceCode,
			entryGroupExternalReferenceCode,
			project,
		});

		if (!isMounted()) {
			return;
		}

		if (error || !data) {
			setLinks((previous) =>
				previous.filter(
					({cmpProjectObjectEntryId}) =>
						cmpProjectObjectEntryId !== project.id
				)
			);

			openToast({
				message:
					error ||
					Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			return;
		}

		setLinks((previous) =>
			previous.map((link) =>
				link.cmpProjectObjectEntryId === project.id
					? {...link, id: data.id}
					: link
			)
		);

		openToast({
			message: Liferay.Language.get(
				'your-request-completed-successfully'
			),
			type: 'success',
		});
	};

	const unlinkProject = async (project: CMPProject) => {
		if (project.linkId === undefined) {
			return;
		}

		const linkId = project.linkId;

		setLinks((previous) =>
			previous.filter(
				({cmpProjectObjectEntryId}) =>
					cmpProjectObjectEntryId !== project.id
			)
		);

		const {error} = await ProjectLinkService.unlinkProject({linkId});

		if (!isMounted()) {
			return;
		}

		if (error) {
			setLinks((previous) => [
				...previous,
				{cmpProjectObjectEntryId: project.id, id: linkId},
			]);

			openToast({message: error, type: 'danger'});

			return;
		}

		openToast({
			message: Liferay.Language.get(
				'your-request-completed-successfully'
			),
			type: 'success',
		});
	};

	return (
		<div className="cms-linked-projects">
			<Picker<CMPProject>
				aria-label={Liferay.Language.get('projects')}
				items={selectableProjects}
				onSelectionChange={(key) => {
					const project = selectableProjects.find(
						({id}) => id === Number(key)
					);

					if (project) {
						linkProject(project);
					}
				}}
				placeholder={Liferay.Language.get('search-or-select-a-project')}
				selectedKey=""
			>
				{(project) => (
					<Option key={project.id} textValue={project.title}>
						{project.title}
					</Option>
				)}
			</Picker>

			{linkedProjects.length ? (
				<div className="cms-linked-projects-list">
					{linkedProjects.map((project) => (
						<ProjectCard
							key={project.id}
							onRemove={() => unlinkProject(project)}
							project={project}
							projectViewURL={projectViewURL}
						/>
					))}
				</div>
			) : null}
		</div>
	);
}
