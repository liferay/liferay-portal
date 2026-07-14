/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useEffect, useState} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import PickerTrigger from './PickerTrigger';

export type SpaceOption = {
	externalReferenceCode?: string;
	label: string;
	value: string;
};

export const initialSpace: SpaceOption = {
	label: Liferay.Language.get('all-spaces'),
	value: 'all',
};

interface ISpacePicker extends React.HTMLAttributes<HTMLElement> {
	className?: string;
	onSelectSpace: (space: SpaceOption) => void;
	selectedSpace: SpaceOption;
}

const SpacePicker: React.FC<ISpacePicker> = ({
	className,
	onSelectSpace,
	selectedSpace,
}) => {
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
				const space = spaces.find(({value}) => value === String(key));

				if (space) {
					onSelectSpace(space);
				}
			}}
			searchable
			selectedKey={selectedSpace.value}
			triggerClassName={className}
			triggerIcon="box-container"
		>
			{(item: SpaceOption) => (
				<Option key={item.value}>{item.label}</Option>
			)}
		</Picker>
	);
};

export {SpacePicker};
