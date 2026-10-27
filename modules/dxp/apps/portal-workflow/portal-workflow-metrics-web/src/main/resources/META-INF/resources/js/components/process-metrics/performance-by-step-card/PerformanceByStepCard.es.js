/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClayPanel from '@clayui/panel';
import React, {useMemo} from 'react';

import PanelHeaderWithOptions from '../../../shared/components/panel-header-with-options/PanelHeaderWithOptions.es';
import PromisesResolver from '../../../shared/components/promises-resolver/PromisesResolver.es';
import {useFetch} from '../../../shared/hooks/useFetch.es';
import {useFilter} from '../../../shared/hooks/useFilter.es';
import ProcessVersionFilter from '../../filter/ProcessVersionFilter.es';
import TimeRangeFilter from '../../filter/TimeRangeFilter.es';
import {
	getDefaultEndDate,
	getDefaultStartDate,
	getTimeRangeParams,
} from '../../filter/util/timeRangeUtil.es';
import {Body, Footer} from './PerformanceByStepCardBody.es';

function Header({disableFilters, prefixKey, processId}) {
	return (
		<PanelHeaderWithOptions
			className="tabs-panel-header"
			description={Liferay.Language.get(
				'performance-by-step-description'
			)}
			title={Liferay.Language.get('performance-by-step')}
		>
			<ClayLayout.ContentCol className="m-0 management-bar management-bar-light navbar">
				<div className="navbar-nav">
					<ProcessVersionFilter
						disabled={disableFilters}
						options={{
							hideControl: true,
							multiple: false,
							withAllVersions: true,
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

function PerformanceByStepCard({routeParams}) {
	const {processId} = routeParams;
	const filterKeys = ['processVersion', 'timeRange'];
	const prefixKey = 'step';
	const prefixKeys = [prefixKey];

	const {
		filterValues: {
			stepDateEnd,
			stepDateStart,
			stepProcessVersion: [version] = ['allVersions'],
			stepTimeRange: [key] = [],
		},
		filtersError,
	} = useFilter({
		filterKeys,
		prefixKeys,
	});

	const processVersion = version !== 'allVersions' ? [version] : undefined;
	const timeRange = useMemo(
		() =>
			getTimeRangeParams(
				stepDateStart ?? getDefaultStartDate(),
				stepDateEnd ?? getDefaultEndDate()
			),
		[stepDateEnd, stepDateStart]
	);

	const {data, fetchData} = useFetch({
		params: {
			completed: true,
			page: 1,
			pageSize: 10,
			processVersion,
			sort: 'durationAvg:desc',
			...timeRange,
		},
		url: `/processes/${processId}/nodes/metrics`,
	});

	const promises = useMemo(() => {
		if (timeRange.dateEnd && timeRange.dateStart) {
			return [fetchData()];
		}

		return [new Promise((_, reject) => reject(filtersError))];

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [filtersError, routeParams, timeRange.dateEnd, timeRange.dateStart]);

	return (
		<ClayPanel className="mt-4 tabs-card" displayType="secondary">
			<PromisesResolver promises={promises}>
				<PerformanceByStepCard.Header
					disableFilters={filtersError}
					prefixKey={prefixKey}
					processId={processId}
					totalCount={data?.totalCount}
				/>

				<PerformanceByStepCard.Body {...data} />

				{data?.totalCount > 0 && (
					<PerformanceByStepCard.Footer
						processId={processId}
						processVersion={processVersion}
						timeRange={{key, ...timeRange}}
						totalCount={data?.totalCount}
					/>
				)}
			</PromisesResolver>
		</ClayPanel>
	);
}

PerformanceByStepCard.Body = Body;
PerformanceByStepCard.Footer = Footer;
PerformanceByStepCard.Header = Header;

export default PerformanceByStepCard;
