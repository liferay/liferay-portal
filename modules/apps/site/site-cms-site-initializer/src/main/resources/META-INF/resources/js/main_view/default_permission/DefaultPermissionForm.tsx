/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayButtonWithIcon} from '@clayui/button';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayManagementToolbar from '@clayui/management-toolbar';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayTable from '@clayui/table';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {ChangeEvent, useCallback, useEffect, useState} from 'react';

import {
	CheckedRoleActions,
	DefaultPermissionFormProps,
} from './DefaultPermissionTypes';

export default function DefaultPermissionForm({
	actions,
	disabled,
	infoBoxMessage,
	onChange,
	roles,
	selectedRole,
	singleRoleMode,
	values,
}: DefaultPermissionFormProps & {
	selectedRole?: string;
	singleRoleMode?: boolean;
}) {
	const [data, setData] = useState<CheckedRoleActions>({});
	const [filteredRoles, setFilteredRoles] = useState(roles);
	const [pagination, setPagination] = useState({
		currentPage: 1,
		pageSize: 20,
	});
	const [search, setSearch] = useState('');

	useEffect(() => {
		const formattedData: CheckedRoleActions = {};

		for (const [role, actions] of Object.entries(values || {})) {
			actions.map((action) => {
				formattedData[`${role}#${action}`] = true;
			});
		}

		setData(formattedData);
	}, [values]);

	useEffect(() => {
		setFilteredRoles(
			roles.filter((role) => {
				if (singleRoleMode) {
					if (!selectedRole) {
						return false;
					}

					return role.key === selectedRole;
				}

				return role.name.toLowerCase().includes(search.toLowerCase());
			})
		);
	}, [roles, search, selectedRole, singleRoleMode]);

	const handleChange = useCallback(
		(event: ChangeEvent<HTMLInputElement>) => {
			const key = event.currentTarget.value;

			const newData = {...data, [key]: !data[key]};

			setData(newData);

			if (onChange) {
				onChange(newData);
			}
		},
		[data, onChange]
	);

	const handlePaginationDeltaChange = useCallback((value: any) => {
		setPagination((prevState) => ({
			...prevState,
			pageSize: value,
		}));
	}, []);

	const handlePaginationPageChange = useCallback((value: any) => {
		setPagination((prevState) => ({
			...prevState,
			currentPage: value,
		}));
	}, []);

	return (
		<>
			{!singleRoleMode && (
				<ClayManagementToolbar className="toolbar-search">
					<ClayManagementToolbar.Search>
						<ClayInput.Group>
							<ClayInput.GroupItem>
								<ClayInput
									aria-label="search"
									className="form-control input-group-inset input-group-inset-after"
									data-testid="input-search"
									disabled={disabled}
									onChange={({target: {value}}) => {
										setSearch(value);
									}}
									placeholder={Liferay.Language.get(
										'search-for'
									)}
									type="text"
									value={search}
								/>

								<ClayInput.GroupInsetItem after tag="span">
									<ClayButtonWithIcon
										aria-label="Search"
										disabled={disabled}
										displayType="unstyled"
										symbol="search"
										type="button"
									/>
								</ClayInput.GroupInsetItem>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayManagementToolbar.Search>
				</ClayManagementToolbar>
			)}

			{infoBoxMessage ? (
				<ClayAlert
					className="info-box-message"
					data-testid="info-box-message"
					displayType="info"
					role={null}
					title="Info"
					variant="stripe"
				>
					{infoBoxMessage}
				</ClayAlert>
			) : (
				<></>
			)}

			{!!filteredRoles.length && (
				<div className="border-top pb-3 pl-4 pr-4 pt-4">
					<ClayTable responsive={true}>
						<ClayTable.Head>
							<>
								<ClayTable.Cell
									className="role-name-column"
									id="0_0"
								>
									<></>
								</ClayTable.Cell>

								{actions.map((action) => {
									return (
										<ClayTable.Cell
											align="center"
											className="text-nowrap"
											data-testid={`head-cell-${action.key}`}
											key={`0_${action.key}`}
										>
											{action.label}
										</ClayTable.Cell>
									);
								})}
							</>
						</ClayTable.Head>

						<ClayTable.Body>
							{filteredRoles
								.slice(
									(pagination.currentPage - 1) *
										pagination.pageSize,
									pagination.currentPage * pagination.pageSize
								)
								.map((role) => {
									return (
										<ClayTable.Row key={role.key}>
											<>
												<ClayTable.Cell
													className="role-name-column text-nowrap"
													data-testid={`row-cell-${role.key}`}
													key={`${role.key}_0`}
												>
													<>
														<ClayTooltipProvider>
															<span
																className="mr-2"
																title={
																	role.type ===
																	'1'
																		? Liferay.Language.get(
																				'regular-role'
																			)
																		: Liferay.Language.get(
																				'space-role'
																			)
																}
															>
																<ClayIcon
																	data-testid={`row-cell-icon-${role.key}`}
																	symbol={
																		role.type ===
																		'1'
																			? 'user'
																			: 'globe'
																	}
																/>
															</span>
														</ClayTooltipProvider>

														{role.name}
													</>
												</ClayTable.Cell>

												{actions.map((action) => {
													return (
														<ClayTable.Cell
															align="center"
															data-testid={`row-cell-${role.key}_${action.key}`}
															key={`${role.key}_${action.key}`}
														>
															<ClayCheckbox
																aria-label={Liferay.Util.sub(
																	Liferay.Language.get(
																		'give-x-permission-to-users-with-the-x-role'
																	),
																	[
																		action.label,
																		role.name,
																	]
																)}
																checked={
																	data[
																		`${role.key}#${action.key}`
																	]
																}
																data-testid={`row-checkbox-${role.key}_${action.key}`}
																disabled={
																	disabled ||
																	(action.guestUnsupported &&
																		role.key ===
																			'Guest') ||
																	(
																		role.actions ||
																		[]
																	).includes(
																		action.key
																	)
																}
																inline
																key={`${role.key}_${action.key}`}
																onChange={
																	handleChange
																}
																value={`${role.key}#${action.key}`}
															/>
														</ClayTable.Cell>
													);
												})}
											</>
										</ClayTable.Row>
									);
								})}
						</ClayTable.Body>
					</ClayTable>

					{!singleRoleMode && (
						<div className="data-set-pagination-wrapper">
							<ClayPaginationBarWithBasicItems
								activeDelta={pagination.pageSize}
								deltas={[20, 40, 60].map((size) => ({
									label: size,
								}))}
								ellipsisBuffer={3}
								onActiveChange={handlePaginationPageChange}
								onDeltaChange={handlePaginationDeltaChange}
								totalItems={filteredRoles.length}
							/>
						</div>
					)}
				</div>
			)}
		</>
	);
}
