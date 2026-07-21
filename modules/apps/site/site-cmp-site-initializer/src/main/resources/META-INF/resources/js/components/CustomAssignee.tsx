/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Assignee,
	AssigneeValue,
} from '@liferay/object-dynamic-data-mapping-form-field-type';
import React, {useState} from 'react';

import AssigneeTrigger from './AssigneeTrigger';

import './AssigneeTrigger.scss';

interface ICustomAssignee {
	label?: string;
	name?: string;
	onChange?: (value: AssigneeValue | {}) => void;
	readOnly?: boolean;
	searchURL?: string;
	showLabel?: boolean;
	triggerClassName?: string;
	usersOnly?: boolean;
	value: AssigneeValue | {} | null;
}

export default function CustomAssignee({
	label,
	name,
	onChange,
	readOnly,
	searchURL,
	showLabel = true,
	triggerClassName,
	usersOnly = false,
	value: initialValue,
}: ICustomAssignee) {
	const [value, setValue] = useState<AssigneeValue | null | {}>(initialValue);

	function getValue(
		usersOnly: boolean,
		value: AssigneeValue | null | {}
	): string {
		if (!usersOnly) {
			return JSON.stringify(value ?? {});
		}

		if (!value || !('id' in value)) {
			return '0';
		}

		return String(value.id);
	}

	return (
		<>
			<Assignee
				label={label ?? Liferay.Language.get('assignee')}
				name=""
				onChange={async (event: {
					target: {value: AssigneeValue | {}};
				}) => {
					setValue(event.target.value);
					onChange?.(event.target.value);
				}}
				readOnly={readOnly}
				searchURL={
					searchURL ??
					Liferay.ThemeDisplay.getPortalURL() +
						'/o/headless-cmp/v1.0/task-assignees/'
				}
				showLabel={showLabel}
				triggerClassName={triggerClassName}
				triggerComponent={AssigneeTrigger}
				value={value}
				visible={true}
			/>
			<input
				name={name}
				type="hidden"
				value={getValue(usersOnly, value)}
			/>
		</>
	);
}
