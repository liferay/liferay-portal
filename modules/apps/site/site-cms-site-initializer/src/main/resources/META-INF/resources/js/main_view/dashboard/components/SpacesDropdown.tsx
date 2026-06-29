/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useContext, useEffect, useState} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import {DashboardsContext, initialSpace} from '../DashboardsContext';
import PickerTrigger from './PickerTrigger';

type SpaceOption = {
	externalReferenceCode?: string;
	label: string;
	value: string;
};

const SpacesDropdown: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => {
	const {
		changeSpace,
		filters: {space},
	} = useContext(DashboardsContext);

	const [spaces, setSpaces] = useState<SpaceOption[]>([initialSpace]);

	useEffect(() => {
		const fetchSpaces = async () => {
			const spaces = await SpaceService.getSpaces();

			setSpaces([
				initialSpace,
				...spaces.map(({externalReferenceCode, id, name}) => ({
					externalReferenceCode,
					label: name,
					value: String(id),
				})),
			]);
		};

		fetchSpaces();
	}, []);

	return (
		<Picker
			aria-label={Liferay.Language.get('filter-by-spaces')}
			as={PickerTrigger}
			filterKey="label"
			items={spaces}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const selectedSpace = spaces.find(
					({value}) => value === String(key)
				);

				if (selectedSpace) {
					changeSpace(selectedSpace);
				}
			}}
			searchable
			selectedKey={space.value}
			triggerClassName={className}
			triggerIcon="box-container"
		>
			{(item: SpaceOption) => (
				<Option key={item.value}>{item.label}</Option>
			)}
		</Picker>
	);
};

export {SpacesDropdown};
