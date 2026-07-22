/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayLabel from '@clayui/label';
import React from 'react';

import {CMPProject, CMPTask} from '../services/ProjectLinkService';
import dateFormat from '../utils/dateFormat';

import '../../../css/components/LinkedProjects.scss';

type ProjectCardProps = {
	expanded?: boolean;
	onRemove: () => void;
	onToggleTasks?: () => void;
	project: CMPProject;
	projectViewURL?: string;
	taskViewURL?: string;
	tasks?: CMPTask[];
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

/**
 * A CMP project card: title, due date, and status badge, with a remove button
 * and an optional expandable task list. Shared by the LinkedProjects panels
 * and the bulk add-assets-to-project modal.
 */
export default function ProjectCard({
	expanded = false,
	onRemove,
	onToggleTasks,
	project,
	projectViewURL,
	taskViewURL,
	tasks = [],
}: ProjectCardProps) {
	const status = getStatus(project);

	const projectURL = projectViewURL
		? `${projectViewURL}/${project.id}`
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
								? `${taskViewURL}/${task.id}`
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
				{hasTasks && onToggleTasks ? (
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
					onClick={onRemove}
					size="sm"
					symbol="times-circle"
					title={Liferay.Language.get('remove')}
				/>
			</div>
		</div>
	);
}
