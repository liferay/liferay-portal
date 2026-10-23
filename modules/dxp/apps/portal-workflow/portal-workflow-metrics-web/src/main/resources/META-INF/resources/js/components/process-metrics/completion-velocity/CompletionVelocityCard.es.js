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
import TimeRangeFilter from '../../filter/TimeRangeFilter.es';
import VelocityUnitFilter from '../../filter/VelocityUnitFilter.es';
import {getTimeRangeParams} from '../../filter/util/timeRangeUtil.es';
import {getVelocityUnits} from '../../filter/util/velocityUnitUtil.es';
import Body from './CompletionVelocityCardBody.es';

const getDefaultStartDate = () => {
	const date = new Date();

	date.setDate(date.getDate() - 29);
	date.setHours(0, 0, 0, 0);

	let newDate = date.toISOString();

	newDate = newDate.split('.')[0] + 'Z';

	return newDate;
};

const getDefaultEndDate = () => {
	const date = new Date();

	let newDate = date.toISOString();

	newDate = newDate.split('.')[0] + 'Z';

	return newDate;
};

function CompletionVelocityCard({routeParams}) {
	const {processId} = routeParams;
	const filterKeys = ['timeRange', 'velocityUnit'];
	const prefixKey = 'completion';
	const prefixKeys = [prefixKey];

	const {
		filterValues: {
			completionDateEnd,
			completionDateStart,
			completionVelocityUnit: [velocity] = [],
		},
		filtersError,
	} = useFilter({filterKeys, prefixKeys});

	const timeRange = useMemo(
		() =>
			getTimeRangeParams(
				completionDateStart ?? getDefaultStartDate(),
				completionDateEnd ?? getDefaultEndDate()
			),
		[completionDateEnd, completionDateStart]
	);

	const velocityUnits = useMemo(
		() => getVelocityUnits(timeRange),
		[timeRange]
	);

	const defaultUnit =
		velocityUnits.find((unit) => unit.defaultVelocityUnit) || {};

	const velocityUnit =
		velocityUnits.find((unit) => unit.key === velocity) || defaultUnit;

	const {key: unit} = velocityUnit;

	const {data, fetchData} = useFetch({
		params: {
			...timeRange,
			unit,
		},
		url: `/processes/${processId}/histograms/metrics`,
	});

	const promises = useMemo(() => {
		if (timeRange.dateEnd && timeRange.dateStart && unit) {
			return [fetchData()];
		}

		return [new Promise((_, reject) => reject(filtersError))];

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [
		filtersError,
		timeRange.dateEnd,
		timeRange.dateStart,
		routeParams,
		unit,
	]);

	return (
		<PromisesResolver promises={promises}>
			<ClayPanel className="mt-4" displayType="secondary">
				<CompletionVelocityCard.Header
					disableFilters={filtersError}
					prefixKey={prefixKey}
					timeRange={timeRange}
				/>

				<CompletionVelocityCard.Body
					data={data}
					timeRange={timeRange}
					velocityUnit={velocityUnit}
				/>
			</ClayPanel>
		</PromisesResolver>
	);
}

function Header({disableFilters, prefixKey, timeRange}) {
	return (
		<PanelHeaderWithOptions
			className="pb-0 tabs-panel-header"
			description={Liferay.Language.get(
				'completion-velocity-description'
			)}
			title={Liferay.Language.get('completion-velocity')}
		>
			<ClayLayout.ContentCol className="m-0 management-bar management-bar-light navbar">
				<div className="navbar-nav">
					<TimeRangeFilter
						disabled={disableFilters}
						prefixKey={prefixKey}
					/>

					<VelocityUnitFilter
						className="pl-3"
						disabled={disableFilters}
						prefixKey={prefixKey}
						timeRange={timeRange}
					/>
				</div>
			</ClayLayout.ContentCol>
		</PanelHeaderWithOptions>
	);
}

CompletionVelocityCard.Header = Header;
CompletionVelocityCard.Body = Body;

export default CompletionVelocityCard;
