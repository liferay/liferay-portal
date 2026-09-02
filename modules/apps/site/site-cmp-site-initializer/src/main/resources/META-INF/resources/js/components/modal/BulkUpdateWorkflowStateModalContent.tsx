/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBadge from '@clayui/badge';
import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useMemo} from 'react';

import {
	getStepGroupKey,
	getWorkflowKey,
	getWorkflowTaskIds,
} from '../../utils/bulkUpdateWorkflowState';
import {openCMPModal} from '../../utils/openCMPModal';
import {WorkflowTaskItemData} from '../../utils/types';
import BulkUpdateWorkflowStateCommentModalContent from './BulkUpdateWorkflowStateCommentModalContent';
import BulkUpdateWorkflowStateStepGroup from './BulkUpdateWorkflowStateStepGroup';
import useBulkUpdateWorkflowState from './hooks/useBulkUpdateWorkflowState';

import './BulkUpdateWorkflowStateModalContent.scss';

type SelectedData = {
	items?: WorkflowTaskItemData[];
};

const getTaskCountLabel = (taskCount: number) =>
	taskCount === 1
		? Liferay.Language.get('one-task')
		: sub(Liferay.Language.get('x-tasks'), String(taskCount));

export default function BulkUpdateWorkflowStateModalContent({
	closeModal,
	getTaskURL,
	loadData,
	selectedData,
}: {
	closeModal: () => void;
	getTaskURL: (task: WorkflowTaskItemData) => string;
	loadData: () => void;
	selectedData: SelectedData;
}) {
	const items = useMemo(() => selectedData.items ?? [], [selectedData]);

	const {
		changeTransitions,
		deselectedTaskIds,
		isCollapsed,
		selectTransitionName,
		toggleCollapsed,
		toggleTasks,
		transitionNames,
		workflowGroups,
	} = useBulkUpdateWorkflowState(items);

	const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (!changeTransitions.length) {
			return;
		}

		closeModal();

		openCMPModal({
			center: true,
			contentComponent: ({
				closeModal: closeCommentModal,
			}: {
				closeModal: () => void;
			}) => (
				<BulkUpdateWorkflowStateCommentModalContent
					changeTransitions={changeTransitions}
					closeModal={closeCommentModal}
					loadData={loadData}
				/>
			),
			size: 'md',
		});
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('bulk-update-state')}
			</ClayModal.Header>

			<ClayModal.Body className="lfr-cmp__bulk-update-state-body">
				<p className="lfr-cmp__bulk-update-state-description">
					{Liferay.Language.get(
						'choose-a-transition-for-each-task-group'
					)}
				</p>

				{workflowGroups.map((workflowGroup) => {
					const {workflowDefinitionName} = workflowGroup;

					const workflowKey = getWorkflowKey(workflowGroup);
					const workflowTaskIds = getWorkflowTaskIds(workflowGroup);

					const selectedTaskIds = workflowTaskIds.filter(
						(taskId) => !deselectedTaskIds.includes(taskId)
					);

					const allTasksSelected =
						selectedTaskIds.length === workflowTaskIds.length;
					const collapsed = isCollapsed(workflowKey);
					return (
						<div
							aria-label={workflowDefinitionName}
							className="lfr-cmp__bulk-update-state-workflow"
							key={workflowKey}
							role="group"
						>
							<div className="lfr-cmp__bulk-update-state-workflow-header">
								<ClayCheckbox
									aria-label={workflowDefinitionName}
									checked={!!selectedTaskIds.length}
									indeterminate={
										!!selectedTaskIds.length &&
										!allTasksSelected
									}
									onChange={() =>
										toggleTasks(
											workflowTaskIds,
											allTasksSelected
										)
									}
								/>

								<span className="lfr-cmp__bulk-update-state-workflow-name">
									{workflowDefinitionName}
								</span>

								<ClayBadge
									className="lfr-cmp__bulk-update-state-workflow-badge"
									displayType="secondary"
									label={getTaskCountLabel(
										workflowGroup.taskCount
									)}
								/>

								<ClayButton
									aria-expanded={!collapsed}
									className="lfr-cmp__bulk-update-state-workflow-toggle"
									displayType="unstyled"
									onClick={() => toggleCollapsed(workflowKey)}
								>
									<ClayIcon
										symbol={
											collapsed
												? 'angle-down'
												: 'angle-up'
										}
									/>
								</ClayButton>
							</div>

							{!collapsed &&
								workflowGroup.stepGroups.map((stepGroup) => {
									const stepGroupKey = getStepGroupKey(
										workflowGroup,
										stepGroup
									);

									return (
										<BulkUpdateWorkflowStateStepGroup
											deselectedTaskIds={
												deselectedTaskIds
											}
											getTaskURL={getTaskURL}
											key={stepGroupKey}
											onToggleTasks={toggleTasks}
											onTransitionChange={(
												transitionName
											) =>
												selectTransitionName(
													stepGroupKey,
													transitionName
												)
											}
											stepGroup={stepGroup}
											transitionName={
												transitionNames[stepGroupKey]
											}
										/>
									);
								})}
						</div>
					);
				})}
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

						<ClayButton
							disabled={!changeTransitions.length}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('update-state')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
