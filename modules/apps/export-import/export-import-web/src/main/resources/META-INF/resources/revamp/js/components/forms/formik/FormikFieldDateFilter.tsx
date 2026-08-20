/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useField, useFormikContext} from 'formik';
import React from 'react';

import DateFilter, {DateFilterValues, Range} from '../../date_filter';

interface FormikFieldDateFilterProps {
	itemsCount?: number;
	lastPublishDate?: string;
	name: string;
	onApplyFilter?: (dateFilterValues: DateFilterValues) => void;
}

export function FormikFieldDateFilter({
	itemsCount,
	lastPublishDate,
	name,
	onApplyFilter,
}: FormikFieldDateFilterProps) {
	const [field, , helpers] = useField<DateFilterValues>(name);
	const {setFieldTouched} = useFormikContext();

	return (
		<DateFilter
			appliedValue={field.value ?? {range: Range.All}}
			itemsCount={itemsCount}
			lastPublishDate={lastPublishDate}
			onApplyFilter={(dateFilterValues) => {
				helpers.setValue(dateFilterValues);
				setFieldTouched(name, true, false);
				onApplyFilter?.(dateFilterValues);
			}}
		/>
	);
}
