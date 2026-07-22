/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayLabel from '@clayui/label';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import ProjectLinkService, {
	CMPProject,
	CMPTask,
	ProjectAssetLink,
} from '../services/ProjectLinkService';
import dateFormat from '../utils/dateFormat';

import '../../../css/components/LinkedProjects.scss';

type LinkedProjectCardProps = {
	expanded: boolean;
	onToggleTasks: () => void;
	onUnlink: () => void;
	project: CMPProject;
	projectViewURL?: string;
	taskViewURL?: string;
	tasks: CMPTask[];
};

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

const DUE_DATE_FORMAT = {
	day: 'numeric',
	month: 'short',
	year: 'numeric',
};

const STATE_DISPLAY_TYPE: {
	[key: string]: React.ComponentProps<typeof ClayLabel>['displayType'];
} = {
	blocked: 'danger',
	done: 'success',
	inProgress: 'info',
	notStarted: 'secondary',
	overdue: 'warning',
};

/**
 * Derives the badge shown on a project card. "Overdue" is not a stored state:
 * it is computed from the due date whenever the project is not yet done.
 */
function getStatus(project: CMPProject): {key: string; name: string} | null {
	const isOverdue =
		Boolean(project.dueDate) &&
		project.state?.key !== 'done' &&
		project.dueDate!.slice(0, 10) < new Date().toISOString().slice(0, 10);

	if (isOverdue) {
		return {key: 'overdue', name: Liferay.Language.get('overdue')};
	}

	return project.state ?? null;
}

function LinkedProjectCard({
	expanded,
	onToggleTasks,
	onUnlink,
	project,
	projectViewURL,
	taskViewURL,
	tasks,
}: LinkedProjectCardProps) {
	const status = getStatus(project);

	const projectURL = projectViewURL
		? `${projectViewURL}${project.id}`
		: undefined;

	const hasTasks = !!tasks.length;

	return (
		<div className="cms-linked-projects-card">
			<div className="cms-linked-projects-card-title">
				{projectURL ? (
					<a
						className="cms-linked-projects-card-name"
						href={projectURL}
					>
						{project.title}
					</a>
				) : (
					<span className="cms-linked-projects-card-name">
						{project.title}
					</span>
				)}

				{project.dueDate ? (
					<div className="cms-linked-projects-card-due-date">
						{Liferay.Util.sub(
							Liferay.Language.get('due-date-x'),
							dateFormat(DUE_DATE_FORMAT, project.dueDate) ?? ''
						)}
					</div>
				) : null}

				{status ? (
					<ClayLabel
						className="cms-linked-projects-card-status"
						displayType={
							STATE_DISPLAY_TYPE[status.key] ?? 'secondary'
						}
						inverse
					>
						{status.name}
					</ClayLabel>
				) : null}

				{hasTasks && expanded ? (
					<ul className="cms-linked-projects-tasks">
						{tasks.map((task) => {
							const taskURL = taskViewURL
								? `${taskViewURL}${task.id}`
								: undefined;

							return (
								<li key={task.id}>
									{taskURL ? (
										<a href={taskURL}>{task.title}</a>
									) : (
										task.title
									)}
								</li>
							);
						})}
					</ul>
				) : null}
			</div>

			<div className="cms-linked-projects-card-actions">
				{hasTasks ? (
					<ClayButtonWithIcon
						aria-label={
							expanded
								? Liferay.Language.get('collapse')
								: Liferay.Language.get('expand')
						}
						borderless
						displayType="secondary"
						onClick={onToggleTasks}
						size="sm"
						symbol={expanded ? 'angle-down' : 'angle-right'}
						title={
							expanded
								? Liferay.Language.get('collapse')
								: Liferay.Language.get('expand')
						}
					/>
				) : null}

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('remove')}
					borderless
					displayType="secondary"
					onClick={onUnlink}
					size="sm"
					symbol="times-circle"
					title={Liferay.Language.get('remove')}
				/>
			</div>
		</div>
	);
}

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
					<LinkedProjectCard
						expanded={expandedProjectIds.has(project.id)}
						key={project.id}
						onToggleTasks={() => toggleTasks(project)}
						onUnlink={() => unlinkProject(project)}
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
