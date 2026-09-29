/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayNavigationBar from '@clayui/navigation-bar';
import {navigate} from 'frontend-js-web';
import React from 'react';

import useAnalyticsFilters from '../../../common/hooks/useAnalyticsFilters';
import {
	AnalyticsFilters,
	IAnalyticsRoomFilter,
	TAnalyticsFilter,
} from '../types';
import {BASE_URL} from '../utils/constants';
import AnalyticsFiltersToolbar from './AnalyticsFiltersToolbar';
import RoomAnalyticsFilter from './filters/RoomAnalyticsFilter';

interface IProps {
	activeTab: string;
	filterSettings: {
		disabled: boolean;
		interactable: boolean;
		persisted: boolean;
	};
	filtersJSONString: string;
	groupIds: string[];
	isAnalyticsEnabled: boolean;
}

export default function Navigation({
	activeTab,
	filterSettings,
	filtersJSONString,
	groupIds,
	isAnalyticsEnabled,
}: IProps) {
	const [filters, setFilter] = useAnalyticsFilters(
		filtersJSONString,
		filterSettings.persisted
	);

	return (
		<>
			<div className="dsr-analytics-navigation">
				<div className="align-items-center d-flex justify-content-between">
					<div className="c-gap-2 d-flex">
						<h2 className="font-weight-semi-bold mb-0 text-7 text-dark">
							{Liferay.Language.get('analytics')}
						</h2>
					</div>

					{isAnalyticsEnabled && (
						<RoomAnalyticsFilter
							filter={
								filters[
									AnalyticsFilters.ROOM
								] as IAnalyticsRoomFilter
							}
							groupIds={groupIds}
							setValue={setFilter}
						/>
					)}
				</div>

				<ClayNavigationBar
					aria-label={Liferay.Language.get('navigation')}
					fluidSize={false}
					triggerLabel={activeTab}
				>
					<ClayNavigationBar.Item
						active={activeTab.includes('overview')}
						key={Liferay.Language.get('overview')}
					>
						<ClayButton
							onClick={() =>
								navigate(`${BASE_URL}/view-overview`)
							}
						>
							{Liferay.Language.get('overview')}
						</ClayButton>
					</ClayNavigationBar.Item>

					<ClayNavigationBar.Item
						active={activeTab.includes('timeline')}
						key={Liferay.Language.get('timeline')}
					>
						<ClayButton
							onClick={() =>
								navigate(`${BASE_URL}/view-timeline`)
							}
						>
							{Liferay.Language.get('timeline')}
						</ClayButton>
					</ClayNavigationBar.Item>
				</ClayNavigationBar>
			</div>

			{filterSettings.disabled || !isAnalyticsEnabled ? null : (
				<AnalyticsFiltersToolbar
					{...filterSettings}
					filters={filters as TAnalyticsFilter}
					filtersJSONString={filtersJSONString}
					setValue={setFilter as (filter: TAnalyticsFilter) => void}
				/>
			)}
		</>
	);
}
