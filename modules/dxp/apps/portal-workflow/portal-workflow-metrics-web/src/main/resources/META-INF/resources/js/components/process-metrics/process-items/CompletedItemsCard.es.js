/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {useFilter} from '../../../shared/hooks/useFilter.es';
import TimeRangeFilter from '../../filter/TimeRangeFilter.es';
import {
	getDefaultEndDate,
	getDefaultStartDate,
	getTimeRangeParams,
} from '../../filter/util/timeRangeUtil.es';
import ProcessItemsCard from './ProcessItemsCard.es';

const CompletedItemsCard = ({routeParams}) => {
	const filterKeys = ['timeRange'];
	const prefixKey = 'completed';
	const prefixKeys = [prefixKey];

	const {
		filterValues: {
			completionDateEnd,
			completionDateStart,
			completionTimeRange: [key] = [],
		},
		filtersError,
	} = useFilter({filterKeys, prefixKeys});

	const timeRange = getTimeRangeParams(
		completionDateStart ?? getDefaultStartDate(),
		completionDateEnd ?? getDefaultEndDate()
	);

	return (
		<ProcessItemsCard
			completed={true}
			description={Liferay.Language.get('completed-items-description')}
			timeRange={{key, ...timeRange}}
			title={Liferay.Language.get('completed-items')}
			{...routeParams}
		>
			<TimeRangeFilter disabled={filtersError} prefixKey={prefixKey} />
		</ProcessItemsCard>
	);
};

export default CompletedItemsCard;
