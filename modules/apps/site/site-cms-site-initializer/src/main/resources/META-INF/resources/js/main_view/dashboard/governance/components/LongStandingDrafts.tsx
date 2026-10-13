/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayLink from '@clayui/link';
import {
	FrontendDataSet,
	IItemActionsData,
} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useMemo} from 'react';

import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {FDS_FILTER_ID} from '../../../../common/utils/constants';
import toDatePart from '../../../../common/utils/toDatePart';
import getDashboardAssetListFDSProps from '../../../props_transformer/getDashboardAssetListFDSProps';
import {QUICK_FILTER_TYPES} from '../../../quick_filters/constants';
import {QUICK_FILTER_UPDATES} from '../../../quick_filters/quickFilterUpdates';
import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {
	LONG_STANDING_DRAFTS_PAGE_SIZE,
	MILLISECONDS_PER_DAY,
} from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';
import {PercentageOfTotal} from './PercentageOfTotal';

const EMPTY_STATE_IMAGE = '/states/cms_empty_state.svg';

const ITEMS_ACTION_IDS: IItemActionsData['id'][] = [
	'actionLink',
	'delete',
	'view-content',
	'view-file',
];

const LIST_ID = 'longStandingDrafts';

const LIST_PANEL_ID = `${LIST_ID}-panel`;

const LIST_TITLE_ID = `${LIST_ID}-title`;

const VIEWS = [
	{
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					actionId: 'actionLink',
					contentRenderer: 'assetRenderer',
					fieldName: 'embedded.title',
					label: Liferay.Language.get('title'),
				},
			],
		},
		thumbnail: 'table',
	},
];

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
			aria-controls={LIST_PANEL_ID}
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
	const {reloadStatistics, space, statistics} = useContext(GovernanceContext);

	const {apiURL, viewAllHref} = useMemo(
		() => ({
			apiURL: GovernanceService.getLongStandingDraftsURL(
				additionalProps.contentProgressFilter,
				space.siteId
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
			space,
		]
	);

	const fdsProps = useMemo(
		() =>
			getDashboardAssetListFDSProps({
				additionalProps,
				apiURL,
				id: LIST_ID,
				itemsActions: additionalProps.fdsActionDropdownItems.filter(
					(action) => ITEMS_ACTION_IDS.includes(action.data?.id)
				),
				onDataChange: reloadStatistics,
				renderSubtitle: renderAuthorAndDaysInDraft,
			}),
		[additionalProps, apiURL, reloadStatistics]
	);

	const count = statistics?.longStandingDraftsCount ?? 0;

	const title = Liferay.Language.get('long-standing-drafts');

	return (
		<section
			aria-labelledby={LIST_TITLE_ID}
			className="border rounded-lg"
			id={LIST_PANEL_ID}
		>
			<div className="pt-3 px-3">
				<div className="align-items-center d-flex justify-content-between mb-2 pb-1">
					<span
						className="font-weight-semi-bold text-4"
						id={LIST_TITLE_ID}
					>
						{title}
					</span>

					<ClayLink
						aria-label={sub(
							Liferay.Language.get('view-all-x'),
							title
						)}
						borderless
						className="font-weight-semi-bold text-3"
						href={viewAllHref}
						small
					>
						{Liferay.Language.get('view-all')}
					</ClayLink>
				</div>

				<span className="text-3 text-secondary">
					{Liferay.Language.get(
						'content-that-has-remained-in-draft-status-for-longer-than-expected'
					)}
				</span>
			</div>

			<div className="cms-fds-fluid cms-long-standing-drafts custom-empty-state">
				<FrontendDataSet
					{...fdsProps}
					emptyState={{
						description: '',
						image: EMPTY_STATE_IMAGE,
						title: Liferay.Language.get(
							'there-are-no-long-standing-drafts'
						),
					}}
					id={LIST_ID}
					showManagementBar={false}
					showPagination={false}
					showSearch={false}
					style="fluid"
					views={VIEWS}
				/>
			</div>

			{count ? (
				<div className="p-3">
					<Text color="secondary" size={3}>
						{sub(
							Liferay.Language.get('showing-x-of-x-items'),
							Math.min(count, LONG_STANDING_DRAFTS_PAGE_SIZE),
							count
						)}
					</Text>
				</div>
			) : null}
		</section>
	);
}
