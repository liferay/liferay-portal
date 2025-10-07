/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useState} from 'react';

const TIME_RANGE_TYPES = [
	{
		label: Liferay.Util.sub(Liferay.Language.get('last-x-hours'), ['24']),
		value: '0',
	},
	{
		label: Liferay.Language.get('yesterday') + ` (00:00-23:59)`,
		value: '1',
	},
	{
		label: Liferay.Language.get('last-7-days'),
		value: '7',
	},
	{
		label: Liferay.Util.sub(Liferay.Language.get('last-x-days'), ['28']),
		value: '28',
	},
	{
		label: Liferay.Language.get('last-30-days'),
		value: '30',
	},
];

export default function TimeRangeInput({index, onChange, value}) {
	const [activeDropdown, setActiveDropdown] = useState(false);

	return (
		<ClayInput.GroupItem>
			<label htmlFor={`timeRange${index}`}>
				{Liferay.Language.get('time-range')}

				<ClayTooltipProvider>
					<span
						className="c-ml-2"
						data-tooltip-align="top"
						tabIndex={0}
						title={Liferay.Language.get('time-range-help')}
					>
						<ClayIcon symbol="question-circle-full" />
					</span>
				</ClayTooltipProvider>
			</label>

			<ClayInput.Group>
				<ClayInput.Group>
					<Picker
						active={activeDropdown}
						aria-label={Liferay.Language.get('time-range')}
						id={`timeRange${index}`}
						items={TIME_RANGE_TYPES}
						onActiveChange={setActiveDropdown}
						onSelectionChange={onChange}
						placeholder={Liferay.Util.sub(
							Liferay.Language.get('select-x'),
							Liferay.Language.get('time-range')
						)}
						selectedKey={value || ''}
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</Picker>
				</ClayInput.Group>
			</ClayInput.Group>
		</ClayInput.GroupItem>
	);
}
