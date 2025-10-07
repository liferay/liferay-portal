/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DatePicker from '@clayui/date-picker';
import {useEffect, useState} from 'react';

type DateRangeProps = {
	label?: string;
	onChange: (event: any, setValue: any) => void;
	value: string;
};

const DateRange: React.FC<DateRangeProps> = ({label, onChange, value}) => {
	const [currentDate, setCurrentDate] = useState(value);

	useEffect(() => {
		setCurrentDate(value);
	}, [value]);

	return (
		<div>
			{label && <label>{label}</label>}
			<DatePicker
				onChange={(event: string) => onChange(event, setCurrentDate)}
				placeholder="YYYY-MM-DD - YYYY-MM-DD"
				range
				value={currentDate}
				years={{end: new Date().getFullYear(), start: 2020}}
			/>
		</div>
	);
};

export default DateRange;
