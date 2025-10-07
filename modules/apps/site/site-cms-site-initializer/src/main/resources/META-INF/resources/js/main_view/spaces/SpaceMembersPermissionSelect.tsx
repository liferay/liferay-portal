/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayCheckbox} from '@clayui/form';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useCallback, useMemo, useState} from 'react';

import {Role} from '../../common/types/Role';

export const SPACE_MEMBER_ROLE_NAME = 'Asset Library Member';
export const HIDDEN_MEMBER_ROLES = ['Asset Library Owner', 'CMS Consumer'];

interface SpaceMembersPermissionSelectProps {
	onChange: (selectedRoles: string[]) => void;
	roles: Role[];
	selectedRoles: string[];
}

export function SpaceMembersPermissionSelect({
	onChange,
	roles: rawRoles = [],
	selectedRoles,
}: SpaceMembersPermissionSelectProps) {
	const [active, setActive] = useState(false);
	const roles = useMemo(
		() =>
			rawRoles.filter((role) => !HIDDEN_MEMBER_ROLES.includes(role.name)),
		[rawRoles]
	);

	const getRoleName = useCallback(
		(roleName: string) => {
			const currentLang = Liferay.ThemeDisplay.getBCP47LanguageId();
			const roleFound = roles.find((role) => role.name === roleName);

			return roleFound?.name_i18n[currentLang] || roleFound?.name;
		},
		[roles]
	);

	const handleCheckboxChange = useCallback(
		(roleName: string) => {
			const newSelectedRoles = selectedRoles.includes(roleName)
				? selectedRoles.filter((name) => name !== roleName)
				: [...selectedRoles, roleName];

			onChange(newSelectedRoles);
		},
		[onChange, selectedRoles]
	);

	const triggerText = useMemo(
		() =>
			roles
				.filter((role) => selectedRoles.includes(role.name))
				.map((role) => getRoleName(role.name))
				.join(', '),
		[getRoleName, roles, selectedRoles]
	);

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={3}
			onActiveChange={setActive}
			trigger={
				<ClayButton
					borderless
					className="align-items-center d-flex"
					displayType="secondary"
					size="xs"
				>
					<ClayTooltipProvider>
						<span
							className="permission-select-trigger-text text-truncate"
							data-tooltip-align="top"
							title={triggerText}
						>
							{triggerText}
						</span>
					</ClayTooltipProvider>
				</ClayButton>
			}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				{roles.map((role) => (
					<ClayDropDown.Item key={role.id}>
						<ClayCheckbox
							checked={selectedRoles.includes(role.name)}
							disabled={role.name === SPACE_MEMBER_ROLE_NAME}
							label={getRoleName(role.name)}
							onChange={() => handleCheckboxChange(role.name)}
						/>
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
