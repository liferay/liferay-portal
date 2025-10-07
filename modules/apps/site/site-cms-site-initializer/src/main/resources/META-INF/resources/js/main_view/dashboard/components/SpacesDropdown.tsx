/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import {ViewDashboardContext, initialSpace} from '../ViewDashboardContext';
import {FilterDropdown} from './FilterDropdown';

type Space = {
	label: string;
	value: string;
};

const PATH = '/o/headless-asset-library/v1.0/asset-libraries';

const SpacesDropdown: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => {
	const {
		changeSpace,
		filters: {space},
	} = useContext(ViewDashboardContext);

	const [dropdownActive, setDropdownActive] = useState(false);

	const [spaces, setSpaces] = useState<Space[]>([initialSpace]);
	const [loading, setLoading] = useState(false);

	const fetchSpaces = async (keywords: string = '') => {
		const queryParams = buildQueryString({
			keywords,
		});
		const endpoint = `${PATH}${queryParams}`;

		const {data, error} = await ApiHelper.get<{
			items: {id: string; name: string}[];
		}>(endpoint);

		if (data) {
			return data.items.map(({id, name}) => ({
				label: name,
				value: String(id),
			}));
		}

		if (error) {
			console.error(error);
		}

		return [];
	};

	return (
		<FilterDropdown
			active={dropdownActive}
			borderless={false}
			className={className}
			filterByValue="spaces"
			icon="box-container"
			items={spaces}
			loading={loading}
			onActiveChange={() => setDropdownActive(!dropdownActive)}
			onSearch={async (value) => {
				setLoading(true);

				const spaces = await fetchSpaces(value);

				setSpaces(value ? spaces : [initialSpace, ...spaces]);

				setLoading(false);
			}}
			onSelectItem={(item) => {
				changeSpace(item);

				setDropdownActive(false);
			}}
			onTrigger={async () => {
				setLoading(true);

				const spaces = await fetchSpaces();

				setSpaces([initialSpace, ...spaces]);

				setLoading(false);
			}}
			selectedItem={space}
			title={Liferay.Language.get('filter-by-spaces')}
		/>
	);
};

export {SpacesDropdown};
