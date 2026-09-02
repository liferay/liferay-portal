/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import {sub} from 'frontend-js-web';
import React from 'react';

import {PICKER_MESSAGES} from '../../utils/constants';
import {getWorkflowTaskAssetTitle} from '../../utils/getWorkflowTaskAssetTitle';
import {StepGroup} from '../../utils/groupWorkflowTasks';
import {WorkflowTaskItemData} from '../../utils/types';

export default function BulkUpdateWorkflowStateStepGroup({
	deselectedTaskIds,
	getTaskURL,
	onToggleTasks,
	onTransitionChange,
	stepGroup,
	transitionName,
}: {
	deselectedTaskIds: number[];
	getTaskURL: (task: WorkflowTaskItemData) => string;
	onToggleTasks: (taskIds: number[], deselect: boolean) => void;
	onTransitionChange: (transitionName: string) => void;
	stepGroup: StepGroup;
	transitionName?: string;
}) {
	return (
		<div className="lfr-cmp__bulk-update-state-step">
			<ClayList>
				{[
					<ClayList.Header key="header">
						{sub(
							Liferay.Language.get('current-step-x'),
							stepGroup.label
						)}
					</ClayList.Header>,
					...stepGroup.tasks.map((task) => {
						const {id} = task.embedded;
						const taskURL = getTaskURL(task);
						const title = getWorkflowTaskAssetTitle(task);

						return (
							<ClayList.Item flex key={id}>
								<ClayList.ItemField>
									<ClayCheckbox
										aria-label={title}
										checked={
											!deselectedTaskIds.includes(id)
										}
										onChange={() =>
											onToggleTasks(
												[id],
												!deselectedTaskIds.includes(id)
											)
										}
									/>
								</ClayList.ItemField>

								<ClayList.ItemField expand>
									<ClayList.ItemTitle>
										{taskURL ? (
											<ClayLink
												href={taskURL}
												rel="noopener noreferrer"
												target="_blank"
											>
												{title}
											</ClayLink>
										) : (
											title
										)}
									</ClayList.ItemTitle>
								</ClayList.ItemField>
							</ClayList.Item>
						);
					}),
				]}
			</ClayList>

			<div className="lfr-cmp__bulk-update-state-transition">
				{sub(
					Liferay.Language.get('transition-from-x-to'),
					<span
						className="lfr-cmp__bulk-update-state-transition-step"
						key="step"
					>
						{stepGroup.label}
					</span>
				)}

				<Picker<StepGroup['transitions'][number]>
					aria-label={sub(
						Liferay.Language.get('transition-from-x-to'),
						stepGroup.label
					)}
					items={stepGroup.transitions}
					messages={PICKER_MESSAGES}
					onSelectionChange={(key) => onTransitionChange(String(key))}
					placeholder={Liferay.Language.get('select-a-transition')}
					selectedKey={transitionName}
				>
					{(transition) => (
						<Option
							key={transition.name}
							textValue={transition.label}
						>
							{transition.label}
						</Option>
					)}
				</Picker>
			</div>
		</div>
	);
}
