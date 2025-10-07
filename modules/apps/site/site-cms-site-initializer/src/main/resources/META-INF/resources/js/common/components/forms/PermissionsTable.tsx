/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Body, Cell, Head, Row, Table, Text} from '@clayui/core';
import {ClayCheckbox, ClaySelect} from '@clayui/form';
import React, {useMemo, useState} from 'react';

interface IActionItem {
	id: string;
	label: string;
}

interface IRoleItem {
	label: string;
	name: string;
}

export interface IPermissionItem {
	actionIds: string[];
	roleName: string;
}

/**
 * Returns a new permissions object with the particular actionId at
 * a certain index toggled. Called when clicking on a checkbox in the
 * permissions table.
 *
 * @param permissions
 * @param actionId
 * @param index
 * @returns IPermissionItem[]
 */
const toggleActionIdAtIndex = (
	permissions: IPermissionItem[],
	actionId: string,
	index: number
): IPermissionItem[] => {
	const newPermissions = permissions.slice();

	newPermissions[index].actionIds = permissions[index].actionIds.includes(
		actionId
	)
		? permissions[index].actionIds.filter((item) => item !== actionId)
		: [...permissions[index].actionIds, actionId];

	return newPermissions;
};

/**
 * Returns a new permissions object with all the actionIds enabled
 * for the listed roles, while the rest of the roles have the actionIds
 * disabled. Called when selecting from the 'Viewable By' dropdown.
 *
 * @param permissions
 * @param roleNames
 * @param actionIdsToEnable
 * @returns IPermissionItem[]
 */
const enableActionIdsForRoles = (
	permissions: IPermissionItem[],
	roleNamesToEnable: string[],
	actionIdsToEnable: string[]
): IPermissionItem[] => {
	return permissions.map(({actionIds, roleName}) => {
		const actionIdsNotEnabled = actionIdsToEnable.filter(
			(id) => !actionIds.includes(id)
		);

		return {
			actionIds: roleNamesToEnable.includes(roleName)
				? [...actionIds, ...actionIdsNotEnabled]
				: actionIds.filter((item) => !actionIdsToEnable.includes(item)),
			roleName,
		};
	});
};

function PermissionsTable({

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
			id: 'UPDATE',
			label: Liferay.Language.get('update'),
		},
		{
			id: 'VIEW',
			label: Liferay.Language.get('view'),
		},
	],

	/*
	 * Classname to apply extra styles to the table.
	 */
	className,

	/*
	 * Default group role that is between Guest and Owner, featured
	 * in the Viewable By options and should be included as one of the roles
	 * in "roles" (example: Organization Member, Power User,
	 * Site Member).
	 */
	defaultGroupRole = 'Site Member',

	/*
	 * Default state for permissions. Sets the checkboxes initially selected.
	 */
	defaultPermissions = [
		{
			actionIds: ['VIEW'],
			roleName: 'Guest',
		},
		{
			actionIds: ['VIEW'],
			roleName: 'Site Member',
		},
	],

	/*
	 * Checkboxes to be disabled for the guest role.
	 */
	guestUnsupportedActionIds = ['UPDATE'],

	/*
	 * Callback for when permissions gets changed.
	 */
	onChange = () => {},

	/*
	 * Configures whether actions or roles are displayed horizontally in the
	 * table header.
	 * `false` = Horizontal display, Actions are listed in the table header.
	 * `true` = Vertical display: Roles are listed in the table header.
	 */
	reverse = false,

	/*
	 * Roles to be listed as the row/column entries.
	 * `name` is used to compare with `roleName` for equality.
	 */
	roles = [
		{
			label: Liferay.Language.get('guest'),
			name: 'Guest',
		},
		{
			label: Liferay.Language.get('site-member'),
			name: 'Site Member',
		},
	],
	...otherProps
}: {
	actions?: IActionItem[];
	className?: string;
	defaultGroupRole?: string;
	defaultPermissions?: IPermissionItem[];
	guestUnsupportedActionIds?: string[];
	onChange?: Function;
	reverse?: boolean;
	roles?: IRoleItem[];
}) {
	const initialPermissions = useMemo(
		() =>
			roles.map(({name}) => ({
				actionIds:
					defaultPermissions.find(({roleName}) => name === roleName)
						?.actionIds || [],
				roleName: name,
			})),
		[roles, defaultPermissions]
	);
	const supportedActionIds: string[] = useMemo(
		() => actions.map(({id}) => id),
		[actions]
	);
	const supportedRoleNames: string[] = useMemo(
		() => roles.map(({name}) => name),
		[roles]
	);

	const [permissions, setPermissions] =
		useState<IPermissionItem[]>(initialPermissions);
	const [viewableBy, setViewableBy] = useState<string>(
		initialPermissions.every(({actionIds}) => actionIds.includes('VIEW')) // All "view" permissions enabled -> Guest
			? 'Guest'
			: initialPermissions.every(
						({actionIds}) => !actionIds.includes('VIEW')
				  ) // No "view" permissions enabled -> Owner
				? 'Owner'
				: defaultGroupRole // Some "view" permissions enabled -> Default Group Role
	);

	const viewableByOptions = [
		{
			label: `${Liferay.Language.get('anyone')} (${Liferay.Util.sub(Liferay.Language.get('x-role'), Liferay.Language.get('guest'))})`,
			value: 'Guest',
		},
		{
			label: roles.find(({name}) => name === defaultGroupRole)?.label,
			value: defaultGroupRole,
		},
		{
			label: Liferay.Language.get('Owner'),
			value: 'Owner',
		},
	];

	const _handleChangePermissions = (newPermissions: IPermissionItem[]) => {
		setPermissions(newPermissions);
		onChange(newPermissions);
	};

	const _handleChangeAction = (actionId: string, index: number) => {
		_handleChangePermissions(
			toggleActionIdAtIndex(permissions, actionId, index)
		);
	};

	const _handleChangeViewableBy = (event: any) => {
		setViewableBy(event.target.value);

		const actionIdsToEnable = supportedActionIds.includes('DOWNLOAD')
			? ['VIEW', 'DOWNLOAD']
			: ['VIEW'];

		const roleNamesToEnable =
			event.target.value === 'Guest'
				? supportedRoleNames
				: event.target.value === 'Owner'
					? []
					: [defaultGroupRole];

		_handleChangePermissions(
			enableActionIdsForRoles(
				permissions,
				roleNamesToEnable,
				actionIdsToEnable
			)
		);
	};

	const _isActionChecked = (actionId: string, index: number) => {
		return permissions[index].actionIds.includes(actionId);
	};

	const _isActionDisabled = (actionId: string, roleName: string) => {
		if (actionId === 'VIEW') {
			return true;
		}

		if (roleName === 'Guest') {
			return guestUnsupportedActionIds.includes(actionId);
		}

		return false;
	};

	return (
		<>
			<p>
				<label htmlFor="viewableBy">
					{supportedActionIds.includes('DOWNLOAD')
						? Liferay.Language.get('viewable-and-downloadable-by')
						: Liferay.Language.get('viewable-by')}
				</label>

				<ClaySelect
					aria-label={Liferay.Language.get('viewable-by')}
					id="viewableBy"
					onChange={_handleChangeViewableBy}
					value={viewableBy}
				>
					{viewableByOptions.map((item) => (
						<ClaySelect.Option
							key={item.value}
							label={item.label}
							value={item.value}
						/>
					))}
				</ClaySelect>
			</p>

			<Table
				className={className}
				columnsVisibility={false}
				striped={false}
				{...otherProps}
			>
				{reverse ? (
					<>
						<Head
							items={[
								{
									label: Liferay.Language.get('action'),
									name: 'action',
								},
								...roles,
							]}
						>
							{(column) => (
								<Cell key={column.name}>{column.label}</Cell>
							)}
						</Head>

						<Body>
							{actions.map(
								({id: actionId, label: actionLabel}) => (
									<Row key={actionId}>
										<Cell>
											<Text size={3} weight="semi-bold">
												{actionLabel}
											</Text>
										</Cell>

										{roles.map(
											({name: roleName}, index) => (
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
											)
										)}
									</Row>
								)
							)}
						</Body>
					</>
				) : (
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
									<Row key={roleName}>
										<Cell>
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
				)}
			</Table>
		</>
	);
}

export default PermissionsTable;
