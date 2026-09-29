/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {Dispatch, SetStateAction} from 'react';

import {AnalyticsFilters, TAnalyticsFilter} from '../types';

interface IProps {
	additionalProps?: Record<string, any>;
	filters: TAnalyticsFilter | Dispatch<SetStateAction<TAnalyticsFilter>>;
	filtersJSONString: string | null;
	interactable: boolean;
	persisted: boolean;
	setValue: (filter: TAnalyticsFilter) => void;
}

export default function AnalyticsFiltersToolbar(props: IProps) {
	return props.interactable ? (
		<div className="analytics-filters-toolbar d-flex mt-4">
			{Object.entries(props.filters).map(([key, filter], index) =>
				filter.active && key !== AnalyticsFilters.ROOM ? (
					<filter.component
						{...props.additionalProps}
						filter={filter}
						key={index}
						setValue={props.setValue}
					/>
				) : null
			)}
		</div>
	) : null;
}
