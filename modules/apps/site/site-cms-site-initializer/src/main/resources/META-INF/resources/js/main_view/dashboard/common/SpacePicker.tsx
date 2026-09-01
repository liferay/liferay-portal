/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useEffect, useMemo, useState} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import PickerTrigger from './PickerTrigger';

export type SpaceOption = {
	externalReferenceCode?: string;
	label: string;
	siteId?: number;
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
	spaceIds?: string[];
}

const SpacePicker: React.FC<ISpacePicker> = ({
	className,
	onSelectSpace,
	selectedSpace,
	spaceIds,
}) => {
	const [spaces, setSpaces] = useState<SpaceOption[]>([initialSpace]);

	useEffect(() => {
		const fetchSpaces = async () => {
			const spaces = await SpaceService.getSpaces();

			setSpaces([
				initialSpace,
				...spaces.map(({externalReferenceCode, id, name, siteId}) => ({
					externalReferenceCode,
					label: name,
					siteId,
					value: String(id),
				})),
			]);
		};

		fetchSpaces();
	}, []);

	const options = useMemo(
		() =>
			spaceIds
				? spaces.filter(
						({value}) =>
							value === initialSpace.value ||
							spaceIds.includes(value)
					)
				: spaces,
		[spaceIds, spaces]
	);

	return (
		<Picker
			aria-label={Liferay.Language.get('filter-by-spaces')}
			as={PickerTrigger}
			filterKey="label"
			items={options}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const space = options.find(({value}) => value === String(key));

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
