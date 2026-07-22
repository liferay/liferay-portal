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
	CMPTask,
	ProjectAssetLink,
} from '../services/ProjectLinkService';
import ProjectCard from './ProjectCard';

import '../../../css/components/LinkedProjects.scss';

type LinkedProjectsProps = {
	assetKeywords?: string[];
	cmpProjectAssetRelationshipObjectDefinitionId?: number | null;
	cmpProjectObjectDefinitionId?: number | null;
	cmpTaskObjectDefinitionId?: number | null;
	entryClassName?: string;
	entryExternalReferenceCode?: string;
	entryGroupExternalReferenceCode?: string;
	projectViewURL?: string;
	taskViewURL?: string;
};

/**
 * Links a CMP asset to one or more CMP projects. Selecting a project from the
 * picker links it, and removing a card unlinks it; both are auto-saved. Shared
 * by the content editor's Projects side panel and the content list's info
 * panel.
 */
export default function LinkedProjects({
	assetKeywords,
	cmpProjectAssetRelationshipObjectDefinitionId,
	cmpProjectObjectDefinitionId,
	cmpTaskObjectDefinitionId,
	entryClassName,
	entryExternalReferenceCode,
	entryGroupExternalReferenceCode,
	projectViewURL,
	taskViewURL,
}: LinkedProjectsProps) {
	const [expandedProjectIds, setExpandedProjectIds] = useState<Set<number>>(
		new Set()
	);
	const [links, setLinks] = useState<ProjectAssetLink[]>([]);
	const [projects, setProjects] = useState<CMPProject[]>([]);
	const [tasksByProjectId, setTasksByProjectId] = useState<{
		[projectId: number]: CMPTask[];
	}>({});

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

		ProjectLinkService.getProjectAssetLinks({
			cmpProjectAssetRelationshipObjectDefinitionId,
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
		cmpProjectAssetRelationshipObjectDefinitionId,
		entryClassName,
		entryExternalReferenceCode,
		entryGroupExternalReferenceCode,
		isMounted,
	]);

	useEffect(() => {
		const controller = new AbortController();

		ProjectLinkService.getLinkedTasks({
			assetKeywords,
			cmpTaskObjectDefinitionId,
			signal: controller.signal,
		}).then(({data, error}) => {
			if (!isMounted()) {
				return;
			}

			if (data) {
				setTasksByProjectId(data);
			}
			else if (error) {
				openToast({message: error, type: 'danger'});
			}
		});

		return () => controller.abort();
	}, [assetKeywords, cmpTaskObjectDefinitionId, isMounted]);

	const linkedProjects = useMemo(() => {
		const projectsById = new Map(
			projects.map((project) => [project.id, project])
		);

		const joinedProjects: CMPProject[] = [];

		links.forEach((link) => {
			const project = projectsById.get(link.projectId);

			if (project) {
				joinedProjects.push({...project, linkId: link.id});
			}
		});

		return joinedProjects;
	}, [links, projects]);

	const selectableProjects = useMemo(() => {
		const linkedProjectIds = new Set(links.map(({projectId}) => projectId));

		return projects.filter(({id}) => !linkedProjectIds.has(id));
	}, [links, projects]);

	const linkProject = async (project: CMPProject) => {
		setLinks((previous) => [...previous, {projectId: project.id}]);

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
				previous.filter(({projectId}) => projectId !== project.id)
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
				link.projectId === project.id ? {...link, id: data.id} : link
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
			previous.filter(({projectId}) => projectId !== project.id)
		);

		const {error} = await ProjectLinkService.unlinkProject({linkId});

		if (!isMounted()) {
			return;
		}

		if (error) {
			setLinks((previous) => [
				...previous,
				{id: linkId, projectId: project.id},
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

	const toggleTasks = (project: CMPProject) => {
		setExpandedProjectIds((previous) => {
			const expandedIds = new Set(previous);

			if (expandedIds.has(project.id)) {
				expandedIds.delete(project.id);
			}
			else {
				expandedIds.add(project.id);
			}

			return expandedIds;
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

			<div className="cms-linked-projects-list">
				{linkedProjects.map((project) => (
					<ProjectCard
						expanded={expandedProjectIds.has(project.id)}
						key={project.id}
						onRemove={() => unlinkProject(project)}
						onToggleTasks={() => toggleTasks(project)}
						project={project}
						projectViewURL={projectViewURL}
						taskViewURL={taskViewURL}
						tasks={tasksByProjectId[project.id] ?? []}
					/>
				))}
			</div>
		</div>
	);
}
