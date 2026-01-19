/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import classNames from 'classnames';
import React, {useId} from 'react';

export enum SelectOptions {
	USERS = 'users',
	GROUPS = 'groups',
}

export interface SpaceMembersSelectOptionsProps {
	className?: string;
	onSelectChange?: (value: SelectOptions) => void;
	selectValue: SelectOptions;
	children: React.ReactNode;
	label?: string;
}

export function SpaceMembersSelectOptions(
	props: SpaceMembersSelectOptionsProps
) {
	const {children, className, label, onSelectChange, selectValue} = props;
	const selectId = useId();

	return (
		<ClayForm.Group
			className={classNames('space-members-input-with-select', className)}
		>
			{label && (
				<label className="d-block" htmlFor={selectId}>
					{label}
				</label>
			)}

			<ClayInput.Group>
				<ClayInput.GroupItem prepend shrink>
					<ClaySelectWithOption
						className="font-weight-semi-bold form-control form-control-select-secondary rounded-left"
						id={selectId}
						onChange={(event) => {
							onSelectChange?.(
								event.target.value as SelectOptions
							);
						}}
						options={[
							{
								label: Liferay.Language.get('users'),
								value: 'users',
							},
							{
								label: Liferay.Language.get('groups'),
								value: 'groups',
							},
						]}
						value={selectValue}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append>{children}</ClayInput.GroupItem>
			</ClayInput.Group>
		</ClayForm.Group>
	);
}
