/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Body, Cell, Head, Row, Table, Text} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React, {useMemo, useState} from 'react';

/**
 * Returns a new permissions object with the particular actionId at
 * a certain index toggled. Called when clicking on a checkbox in the
 * permissions table.
 *
 * @param permissions
 * @param actionId
 * @param index
 * @returns Updated permissions array
 */
const toggleActionIdAtIndex = (permissions, actionId, index) => {
	const newPermissions = permissions.slice();

	newPermissions[index].actionIds = permissions[index].actionIds.includes(
		actionId
	)
		? permissions[index].actionIds.filter((item) => item !== actionId)
		: [...permissions[index].actionIds, actionId];

	return newPermissions;
};

function PublicationsPermissionsTable({

	/*
	 * Actions to be listed as the row/column entries.
	 */
	actions = [
		{
			id: 'DELETE',
			label: Liferay.Language.get('delete'),
		},
		{
			id: 'PERMISSIONS',
			label: Liferay.Language.get('permissions'),
		},
		{
			id: 'INVITE_USERS',
			label: Liferay.Language.get('invite-users'),
		},
		{
			id: 'UPDATE',
			label: Liferay.Language.get('update'),
		},
		{
			id: 'PUBLISH',
			label: Liferay.Language.get('publish'),
		},
		{
			id: 'VIEW',
			label: Liferay.Language.get('view'),
		},
	],

	/*
	 * Default state for permissions. Sets the checkboxes initially selected.
	 */
	defaultPermissions,

	/*
	 * Checkboxes to be disabled for the guest role.
	 */
	guestUnsupportedActionIds = [
		'DELETE',
		'PERMISSIONS',
		'INVITE_USERS',
		'UPDATE',
		'PUBLISH',
		'VIEW',
	],

	/*
	 * Callback for when permissions gets changed.
	 */
	onChange = () => {},

	/*
	 * Roles to be listed as the row/column entries.
	 * `name` is used to compare with `roleName` for equality.
	 */
	roles,
	...otherProps
}) {
	const initialPermissions = useMemo(() => {
		const roleIds = Object.keys(defaultPermissions);

		return roles.map((role, index) => {
			const roleId = roleIds[index];
			const actionIds = defaultPermissions[roleId] || [];

			return {
				actionIds,
				roleId,
				roleName: role.name,
			};
		});
	}, [roles, defaultPermissions]);

	const [permissions, setPermissions] = useState(initialPermissions);

	const _handleChangePermissions = (newPermissions) => {
		setPermissions(newPermissions);
		onChange(newPermissions);
	};

	const _handleChangeAction = (actionId, index) => {
		_handleChangePermissions(
			toggleActionIdAtIndex(permissions, actionId, index)
		);
	};

	const _isActionChecked = (actionId, index) => {
		return permissions[index].actionIds.includes(actionId);
	};

	const _isActionDisabled = (actionId, roleName) => {
		if (roleName === 'Guest') {
			return guestUnsupportedActionIds.includes(actionId);
		}

		if (roleName === 'Owner' && actionId === 'VIEW') {
			return true;
		}

		return false;
	};

	return roles.length ? (
		<div className="p-3">
			<Table
				className="table table-striped"
				columnsVisibility={false}
				{...otherProps}
			>
				<>
					<Head
						items={[
							{
								id: 'role',
								label: Liferay.Language.get('role'),
							},
							...actions,
						]}
					>
						{(column) => (
							<Cell key={column.id}>{column.label}</Cell>
						)}
					</Head>

					<Body>
						{roles.map(
							({label: roleLabel, name: roleName}, index) => (
								<Row className="role-row" key={roleName}>
									<Cell>
										<ClayIcon
											style={{marginRight: '8px'}}
											symbol="user"
										/>

										<Text size={3} weight="semi-bold">
											{roleLabel}
										</Text>
									</Cell>

									{actions.map(({id: actionId}) => (
										<Cell
											key={`${roleName.replace(' ', '')}-${actionId}`}
										>
											<ClayCheckbox
												aria-label={Liferay.Util.sub(
													'give-x-permission-to-users-with-the-x-role',
													[actionId, roleName]
												)}
												checked={_isActionChecked(
													actionId,
													index
												)}
												disabled={_isActionDisabled(
													actionId,
													roleName
												)}
												onChange={() =>
													_handleChangeAction(
														actionId,
														index
													)
												}
											/>
										</Cell>
									))}
								</Row>
							)
						)}
					</Body>
				</>
			</Table>
		</div>
	) : (
		<ClayEmptyState
			className="text-center"
			description={Liferay.Language.get('no-roles-were-found')}
			title=""
		/>
	);
}

export default PublicationsPermissionsTable;
