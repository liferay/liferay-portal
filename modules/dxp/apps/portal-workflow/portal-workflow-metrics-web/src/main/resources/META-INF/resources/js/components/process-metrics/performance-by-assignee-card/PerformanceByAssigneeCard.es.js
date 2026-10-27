/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClayPanel from '@clayui/panel';
import React, {useMemo} from 'react';

import PanelHeaderWithOptions from '../../../shared/components/panel-header-with-options/PanelHeaderWithOptions.es';
import PromisesResolver from '../../../shared/components/promises-resolver/PromisesResolver.es';
import {useFilter} from '../../../shared/hooks/useFilter.es';
import {usePost} from '../../../shared/hooks/usePost.es';
import ProcessStepFilter from '../../filter/ProcessStepFilter.es';
import TimeRangeFilter from '../../filter/TimeRangeFilter.es';
import {
	getDefaultEndDate,
	getDefaultStartDate,
	getTimeRangeParams,
} from '../../filter/util/timeRangeUtil.es';
import {Body, Footer} from './PerformanceByAssigneeCardBody.es';

function Header({disableFilters, prefixKey, processId}) {
	return (
		<PanelHeaderWithOptions
			className="tabs-panel-header"
			description={Liferay.Language.get(
				'performance-by-assignee-description'
			)}
			title={Liferay.Language.get('performance-by-assignee')}
		>
			<ClayLayout.ContentCol className="m-0 management-bar management-bar-light navbar">
				<div className="navbar-nav">
					<ProcessStepFilter
						disabled={disableFilters}
						options={{
							hideControl: true,
							multiple: false,
							withAllSteps: true,
							withSelectionTitle: true,
						}}
						prefixKey={prefixKey}
						processId={processId}
					/>

					<TimeRangeFilter
						className="pl-3"
						disabled={disableFilters}
						prefixKey={prefixKey}
					/>
				</div>
			</ClayLayout.ContentCol>
		</PanelHeaderWithOptions>
	);
}

function PerformanceByAssigneeCard({routeParams}) {
	const {processId} = routeParams;
	const filterKeys = ['processStep', 'timeRange'];
	const prefixKey = 'assignee';
	const prefixKeys = [prefixKey];

	const {
		filterValues: {
			assigneeDateEnd,
			assigneeDateStart,
			assigneeTaskNames: [taskName] = ['allSteps'],
			assigneeTimeRange: [key] = [],
		},
		filtersError,
	} = useFilter({
		filterKeys,
		prefixKeys,
	});

	const taskNames = taskName !== 'allSteps' ? [taskName] : undefined;
	const timeRange = useMemo(
		() =>
			getTimeRangeParams(
				assigneeDateStart ?? getDefaultStartDate(),
				assigneeDateEnd ?? getDefaultEndDate()
			),
		[assigneeDateEnd, assigneeDateStart]
	);

	const {data, postData} = usePost({
		body: {
			completed: true,
			taskNames,
			...timeRange,
		},
		params: {
			page: 1,
			pageSize: 10,
			sort: 'durationTaskAvg:desc',
		},
		url: `/processes/${processId}/assignees/metrics`,
	});

	const promises = useMemo(() => {
		if (timeRange.dateEnd && timeRange.dateStart) {
			return [postData()];
		}

		return [new Promise((_, reject) => reject(filtersError))];

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [filtersError, routeParams, timeRange.dateEnd, timeRange.dateStart]);

	return (
		<ClayPanel className="mt-4 tabs-card" displayType="secondary">
			<PromisesResolver promises={promises}>
				<PerformanceByAssigneeCard.Header
					disableFilters={filtersError}
					prefixKey={prefixKey}
					{...routeParams}
				/>

				<PerformanceByAssigneeCard.Body
					{...data}
					filtered={!!taskNames}
				/>

				<PerformanceByAssigneeCard.Footer
					processStep={taskName}
					timeRange={{key, ...timeRange}}
					totalCount={data?.totalCount}
					{...routeParams}
				/>
			</PromisesResolver>
		</ClayPanel>
	);
}

PerformanceByAssigneeCard.Body = Body;
PerformanceByAssigneeCard.Footer = Footer;
PerformanceByAssigneeCard.Header = Header;

export default PerformanceByAssigneeCard;
