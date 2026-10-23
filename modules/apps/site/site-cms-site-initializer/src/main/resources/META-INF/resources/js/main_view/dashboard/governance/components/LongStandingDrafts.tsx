/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IItemActionsData} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useMemo} from 'react';

import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {FDS_FILTER_ID} from '../../../../common/utils/constants';
import toDatePart from '../../../../common/utils/toDatePart';
import {QUICK_FILTER_TYPES} from '../../../quick_filters/constants';
import {QUICK_FILTER_UPDATES} from '../../../quick_filters/quickFilterUpdates';
import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {MILLISECONDS_PER_DAY} from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';
import {
	GovernanceAssetList,
	getGovernanceAssetListPanelId,
} from './GovernanceAssetList';
import {PercentageOfTotal} from './PercentageOfTotal';

const ITEM_ACTION_IDS: IItemActionsData['id'][] = [
	'actionLink',
	'view-content',
	'view-file',
	'delete',
];

const LIST_ID = 'longStandingDrafts';

function renderAuthorAndDaysInDraft(item: ISearchAssetObjectEntry) {
	const daysInDraft = Math.floor(
		(Date.now() - new Date(item.dateModified).getTime()) /
			MILLISECONDS_PER_DAY
	);

	const creatorName = item.embedded?.creator?.name;
	const daysInDraftLabel = sub(
		Liferay.Language.get('x-days-in-draft'),
		daysInDraft
	);

	return creatorName
		? `${creatorName} · ${daysInDraftLabel}`
		: daysInDraftLabel;
}

export function LongStandingDraftsCard({
	expanded,
	onClick,
}: {
	expanded: boolean;
	onClick: () => void;
}) {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);

	const count = statistics?.longStandingDraftsCount ?? 0;

	return (
		<InteractiveCard
			active={expanded}
			aria-controls={getGovernanceAssetListPanelId(LIST_ID)}
			aria-expanded={expanded}
			color="purple"
			description={Liferay.Language.get(
				'content-that-has-remained-in-draft-status-for-longer-than-expected'
			)}
			icon="pencil"
			loading={loadingStatistics}
			onClick={onClick}
			title={Liferay.Language.get('long-standing-drafts')}
			value={count}
		>
			<PercentageOfTotal
				count={count}
				template={Liferay.Language.get('x-of-x-assets')}
				total={statistics?.totalCount ?? 0}
			/>
		</InteractiveCard>
	);
}

export function LongStandingDraftsList({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {groupIds, space, statistics} = useContext(GovernanceContext);

	const {apiURL, viewAllHref} = useMemo(
		() => ({
			apiURL: GovernanceService.getLongStandingDraftsURL(
				additionalProps.contentProgressFilter,
				groupIds
			),
			viewAllHref: getAllSectionHref(additionalProps.allSectionFDSName, {
				filters: [
					...Object.entries(
						QUICK_FILTER_UPDATES[QUICK_FILTER_TYPES.IN_DRAFT]()
					).map(([id, selectedData]) => ({id, selectedData})),
					{
						id: FDS_FILTER_ID.DATE_MODIFIED,
						selectedData: {
							exclude: false,
							from: null,
							to: toDatePart(
								GovernanceService.getLongStandingDraftsThresholdDate()
							),
						},
					},
					...getSpaceFilters(space),
				],
				sorts: [{direction: 'asc', key: FDS_FILTER_ID.DATE_MODIFIED}],
			}),
		}),
		[
			additionalProps.allSectionFDSName,
			additionalProps.contentProgressFilter,
			groupIds,
			space,
		]
	);

	return (
		<GovernanceAssetList
			additionalProps={additionalProps}
			apiURL={apiURL}
			count={statistics?.longStandingDraftsCount ?? 0}
			description={Liferay.Language.get(
				'content-that-has-remained-in-draft-status-for-longer-than-expected'
			)}
			emptyStateDescription={Liferay.Language.get(
				'no-content-has-stayed-in-draft-longer-than-expected'
			)}
			emptyStateTitle={Liferay.Language.get('no-long-standing-drafts')}
			id={LIST_ID}
			itemActionIds={ITEM_ACTION_IDS}
			renderSubtitle={renderAuthorAndDaysInDraft}
			title={Liferay.Language.get('long-standing-drafts')}
			viewAllHref={viewAllHref}
		/>
	);
}
