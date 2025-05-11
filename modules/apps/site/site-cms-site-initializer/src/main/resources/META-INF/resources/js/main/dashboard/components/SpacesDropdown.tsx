/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {ViewDashboardContext} from '../ViewDashboardContext';
import {FilterDropdown} from './FilterDropdown';

const spaces = [
	{
		label: Liferay.Language.get('all-spaces'),
		value: 'all',
	},
	{
		label: Liferay.Language.get('space-02'),
		value: 'space02',
	},
];

const SpacesDropdown: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => {
	const {
		changeSpaceDropdown,
		filters: {spaceId},
	} = useContext(ViewDashboardContext);

	return (
		<FilterDropdown
			active={spaceId}
			borderless={false}
			className={className}
			filterByValue="spaces"
			icon="box-container"
			items={spaces}
			onSelectItem={(space) => changeSpaceDropdown(space.value)}
			triggerLabel={
				spaces.find(({value}) => value === spaceId)?.label ?? ''
			}
		/>
	);
};

export {SpacesDropdown};
