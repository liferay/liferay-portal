/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayCheckbox} from '@clayui/form';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useCallback, useMemo, useState} from 'react';

import {Role, RoleExternalReferenceCode} from './types';

interface MembersPermissionSelectProps {
	defaultRole?: Role;
	disabled?: boolean;
	onChange: (selectedRoles: string[]) => void;
	roleNames?: Partial<Record<RoleExternalReferenceCode, string>>;
	roles: Role[];
	selectedRoles: string[];
}

export function MembersPermissionSelect({
	defaultRole,
	disabled = false,
	onChange,
	roleNames,
	roles = [],
	selectedRoles,
}: MembersPermissionSelectProps) {
	const [active, setActive] = useState(false);

	const getRoleName = useCallback(
		(role: Role) =>
			roleNames?.[
				role.externalReferenceCode as RoleExternalReferenceCode
			] ?? role.name,
		[roleNames]
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

	const {tooltipText, triggerText} = useMemo(() => {
		const allSelectedRoleNames = roles
			.filter(({name}) => selectedRoles.includes(name))
			.map((role) => getRoleName(role));

		const maxVisibleRoles = 2;
		const tooltip = allSelectedRoleNames.join(', ');

		if (allSelectedRoleNames.length > maxVisibleRoles) {
			const remaining = allSelectedRoleNames.length - maxVisibleRoles;

			const trigger = `${allSelectedRoleNames
				.slice(0, maxVisibleRoles)
				.join(', ')}, +${remaining}`;

			return {tooltipText: tooltip, triggerText: trigger};
		}

		return {tooltipText: tooltip, triggerText: tooltip};
	}, [getRoleName, roles, selectedRoles]);

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={3}
			onActiveChange={setActive}
			trigger={
				<ClayButton
					borderless
					className="align-items-center d-flex"
					disabled={disabled}
					displayType="secondary"
					size="xs"
				>
					<ClayTooltipProvider>
						<span
							className="permission-select-trigger-text text-truncate"
							data-tooltip-align="top"
							title={tooltipText}
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
							disabled={
								role.externalReferenceCode ===
								defaultRole?.externalReferenceCode
							}
							label={getRoleName(role)}
							onChange={() => handleCheckboxChange(role.name)}
						/>
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
