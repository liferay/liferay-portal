/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import {DateRenderer} from '@liferay/frontend-data-set-web';
import {AssigneeValue} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {displayErrorToast} from '@liferay/site-cms-site-initializer';
import React, {useState} from 'react';

import {patchTaskById} from '../../utils/api';
import {
	displayAssignSuccessToast,
	displayStateSuccessToast,
} from '../../utils/toastUtil';
import CustomAssignee from '../CustomAssignee';
import {UPDATE_HISTORY} from '../History';
import InfoSummary from '../InfoSummary';
import StateSelector, {State} from '../StateSelector';

import '../AssigneeTrigger.scss';

interface TaskInfoSummaryProps {
	assignTo: AssigneeValue;
	cmpTaskObjectEntryId: string;
	dueDate: string;
	hasUpdatePermission: boolean;
	initialState: string;
	states: State[];
	tags: string[];
	title: string;
}

export default function TaskInfoSummary({
	assignTo,
	cmpTaskObjectEntryId,
	dueDate,
	hasUpdatePermission,
	initialState,
	states,
	tags,
	title,
}: TaskInfoSummaryProps) {
	const [selectedStateKey, setSelectedStateKey] = useState(initialState);
	const [stateSelectorDisabled, setStateSelectorDisabled] = useState(false);

	return (
		<InfoSummary
			defaultOpen={true}
			items={[
				{
					label: Liferay.Language.get('state'),
					value: (
						<StateSelector
							disabled={
								!hasUpdatePermission || stateSelectorDisabled
							}
							onChange={async (key: string) => {
								setStateSelectorDisabled(true);

								const {error} = await patchTaskById({
									body: {state: key},
									taskId: cmpTaskObjectEntryId,
								});

								if (!error) {
									setSelectedStateKey(key);

									displayStateSuccessToast();

									Liferay.fire(UPDATE_HISTORY);
								}
								else {
									displayErrorToast(error);
								}

								setStateSelectorDisabled(false);
							}}
							selectedKey={selectedStateKey}
							small
							states={states}
						/>
					),
				},
				{
					label: Liferay.Language.get('assignee'),
					value: (
						<CustomAssignee
							onChange={async (value: AssigneeValue | {}) => {
								const {error} = await patchTaskById({
									body: {assignTo: value},
									taskId: cmpTaskObjectEntryId,
								});

								if (!error) {
									displayAssignSuccessToast(
										title,
										(value as AssigneeValue).name
									);

									Liferay.fire(UPDATE_HISTORY);
								}
								else {
									displayErrorToast(error);
								}
							}}
							readOnly={!hasUpdatePermission}
							showLabel={false}
							value={assignTo}
						/>
					),
				},
				{
					label: Liferay.Language.get('due-date'),
					value: DateRenderer({value: dueDate}) ?? '',
				},
				{
					label: Liferay.Language.get('tags'),
					value: (
						<div>
							{tags.map((tag) => (
								<Label key={tag}>{tag}</Label>
							))}
						</div>
					),
				},
			]}
		/>
	);
}
