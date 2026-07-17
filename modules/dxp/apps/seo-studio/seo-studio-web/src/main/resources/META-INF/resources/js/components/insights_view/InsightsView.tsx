/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useMemo} from 'react';

import {InsightsViewContext} from './InsightsViewContext';
import CategoryCellRenderer from './cell_renderers/CategoryCellRenderer';
import ImpactCellRenderer from './cell_renderers/ImpactCellRenderer';
import InsightNameCellRenderer from './cell_renderers/InsightNameCellRenderer';

import './InsightsView.scss';

export default function InsightsView({
	apiURL,
	emptyState,
	fdsActionDropdownItems,
	fdsId,
	filters,
	onSelectInsight,
	views,
}: {
	apiURL: string;
	emptyState: Record<string, unknown>;
	fdsActionDropdownItems: any[];
	fdsId: string;
	filters: any[];
	onSelectInsight: (externalReferenceCode: string) => void;
	views: any[];
}) {
	const contextValue = useMemo(
		() => ({
			selectInsight: onSelectInsight,
		}),
		[onSelectInsight]
	);

	return (
		<InsightsViewContext.Provider value={contextValue}>
			<div className="seo-studio-insights-section">
				<h3>{Liferay.Language.get('insights')}</h3>

				<FrontendDataSet
					apiURL={apiURL}
					appURL={`${Liferay.ThemeDisplay.getPortalURL()}/o/frontend-data-set-taglib/app`}
					customRenderers={{
						tableCell: [
							{
								component: CategoryCellRenderer,
								name: 'categoryCellRenderer',
								type: 'internal',
							},
							{
								component: ImpactCellRenderer,
								name: 'impactCellRenderer',
								type: 'internal',
							},
							{
								component: InsightNameCellRenderer,
								name: 'insightNameCellRenderer',
								type: 'internal',
							},
						],
					}}
					emptyState={emptyState}
					filters={filters}
					id={fdsId}
					itemsActions={fdsActionDropdownItems}
					pagination={{initialDelta: 10}}
					showManagementBar
					showPagination
					showSearch={false}
					uniformActionsDisplay
					views={views}
				/>
			</div>
		</InsightsViewContext.Provider>
	);
}
