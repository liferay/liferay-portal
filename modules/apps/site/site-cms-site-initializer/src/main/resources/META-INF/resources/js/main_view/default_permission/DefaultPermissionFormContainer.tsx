/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import React, {useCallback, useEffect, useState} from 'react';

import DefaultPermissionForm from './DefaultPermissionForm';
import {
	Action,
	AssetRoleSelectedActions,
	AssetType,
	CheckedRoleActions,
	DefaultAssetTypes,
	DefaultPermissionFormContainerProps,
	RoleSelectedActions,
} from './DefaultPermissionTypes';

const DEFAULT_ASSET_TYPES: Array<AssetType> = [
	{
		key: DefaultAssetTypes.OBJECT_ENTRY_FOLDERS,
		label: Liferay.Language.get('default-folder-permissions'),
	},
	{
		key: DefaultAssetTypes.L_CONTENTS,
		label: Liferay.Language.get('default-content-permissions'),
	},
	{
		key: DefaultAssetTypes.L_FILES,
		label: Liferay.Language.get('default-file-permissions'),
	},
];

export default function DefaultPermissionFormContainer({
	actions,
	disabled,
	infoBoxMessage,
	onChange,
	roles,
	types,
	values,
}: DefaultPermissionFormContainerProps) {
	const [activeIndex, setActiveIndex] = useState(0);
	const [activeActions, setActiveActions] = useState<Action[]>([]);
	const [activeValues, setActiveValues] = useState({});
	const [data, setData] = useState<AssetRoleSelectedActions>(values || {});
	const [tabs, setTabs] = useState<AssetType[]>(DEFAULT_ASSET_TYPES);

	const handlePermissionsChange = useCallback(
		(checkedRoleActions: CheckedRoleActions) => {
			const roleSelectedActions: RoleSelectedActions = {};

			for (const [key, value] of Object.entries(checkedRoleActions)) {
				if (!value) {
					continue;
				}

				const lastIndex = key.lastIndexOf('#');

				const roleKey = key.slice(0, lastIndex);
				const action = key.slice(lastIndex + 1);

				const existingData = roleSelectedActions[roleKey] || [];

				existingData.push(action);

				roleSelectedActions[roleKey] = existingData;
			}

			const newData = {
				...data,
				[tabs[activeIndex].key]: roleSelectedActions,
			};

			setData(newData);

			if (onChange) {
				onChange(newData);
			}
		},
		[activeIndex, data, onChange, tabs]
	);

	useEffect(() => {
		if (tabs && tabs.length) {
			setActiveActions(actions[tabs[activeIndex]?.key]);
			setActiveValues((data || {})[tabs[activeIndex]?.key]);
		}
	}, [actions, activeIndex, data, tabs]);

	useEffect(() => {
		setTabs(types || DEFAULT_ASSET_TYPES);
	}, [types]);

	useEffect(() => {
		setData(values || {});
	}, [values]);

	return (
		<>
			<ClayNavigationBar
				className="toolbar-tabs"
				triggerLabel={String(activeIndex)}
			>
				{tabs.map((tab, index) => {
					return (
						<ClayNavigationBar.Item
							active={index === activeIndex}
							key={`tab-${tab.key}`}
						>
							<ClayLink
								data-testid={`tab-${tab.key}`}
								onClick={(event) => {
									event.preventDefault();

									if (disabled) {
										return;
									}

									setActiveIndex(index);
								}}
								role="tab"
							>
								{tab.label}
							</ClayLink>
						</ClayNavigationBar.Item>
					);
				})}
			</ClayNavigationBar>

			<div className="border-bottom">
				<DefaultPermissionForm
					actions={activeActions}
					disabled={disabled}
					infoBoxMessage={infoBoxMessage}
					onChange={handlePermissionsChange}
					roles={roles}
					values={activeValues}
				/>
			</div>
		</>
	);
}
