/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import React, {useId, useState} from 'react';

import {Space} from '../services/getSpaces';

interface SpaceSelectProps {
	onSelectSpace: (space: Space) => void;
	spaces: Space[];
}

const SpaceSelect: React.FC<SpaceSelectProps> = ({onSelectSpace, spaces}) => {
	const [siteId, setSiteId] = useState('');
	const [submitted, setSubmitted] = useState(false);

	const selectId = useId();

	function handleChange(event: React.ChangeEvent<HTMLSelectElement>) {
		const value = event.target.value;

		setSiteId(value);

		const space = spaces.find((space) => String(space.siteId) === value);

		if (!space) {
			return;
		}

		setSubmitted(true);

		onSelectSpace(space);
	}

	return (
		<ClayForm.Group>
			<label htmlFor={selectId}>{Liferay.Language.get('space')}</label>

			<ClaySelectWithOption
				disabled={submitted}
				id={selectId}
				onChange={handleChange}
				options={[
					{
						disabled: true,
						label: Liferay.Language.get('select-a-space'),
						value: '',
					},
					...spaces.map((space) => ({
						label: space.name,
						value: String(space.siteId),
					})),
				]}
				value={siteId}
			/>
		</ClayForm.Group>
	);
};

export default SpaceSelect;
