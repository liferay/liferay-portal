/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {
	IBaseFilterState,
	IItemActionsData,
} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useMemo, useState} from 'react';

import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {
	FDS_FILTER_ID,
	NO_VALUE,
	UPCOMING_REVIEW_THRESHOLD_DAYS,
} from '../../../../common/utils/constants';
import dateFormat from '../../../../common/utils/dateFormat';
import toDatePart from '../../../../common/utils/toDatePart';
import {QUICK_FILTER_TYPES} from '../../../quick_filters/constants';
import {QUICK_FILTER_UPDATES} from '../../../quick_filters/quickFilterUpdates';
import {SectionHeader} from '../../common/SectionHeader';
import InteractiveCard, {
	MetricColor,
} from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {
	AssetStatistics,
	FreshnessListType,
	MILLISECONDS_PER_DAY,
} from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';
import {ContentAgeDistribution} from './ContentAgeDistribution';
import {
	GovernanceAssetList,
	getGovernanceAssetListPanelId,
} from './GovernanceAssetList';
import {PercentageOfTotal} from './PercentageOfTotal';

type FreshnessCard = {
	color: MetricColor;
	description: string;
	emptyStateDescription: string;
	emptyStateTitle: string;
	getViewAllFilters: () => Record<string, IBaseFilterState['selectedData']>;
	icon: string;
	itemActionIds: IItemActionsData['id'][];
	listTitle?: string;
	listType: FreshnessListType;
	renderSubtitle: (item: ISearchAssetObjectEntry) => React.ReactNode;
	statKey: keyof AssetStatistics;
	title: string;
	viewAllSortKey: string;
};

const BASE_ITEM_ACTION_IDS: IItemActionsData['id'][] = [
	'actionLink',
	'view-content',
	'view-file',
];

const EXPIRATION_ITEM_ACTION_IDS: IItemActionsData['id'][] = [
	'update-expiration-date',
	...BASE_ITEM_ACTION_IDS,
	'delete',
];

const REVIEW_ITEM_ACTION_IDS: IItemActionsData['id'][] = [
	'update-review-date',
	...BASE_ITEM_ACTION_IDS,
	'delete',
];

function formatDate(value?: string) {
	if (!value) {
		return NO_VALUE;
	}

	return (
		dateFormat(
			{
				day: 'numeric',
				month: 'short',
				timeZone: Liferay.ThemeDisplay.getTimeZone(),
				year: 'numeric',
			},
			value
		) ?? NO_VALUE
	);
}

function getUpcomingReviewsFilters() {
	return {
		[FDS_FILTER_ID.DATE_REVIEW]: {
			exclude: false,
			from: toDatePart(new Date(Date.now())),
			to: toDatePart(
				new Date(
					Date.now() +
						UPCOMING_REVIEW_THRESHOLD_DAYS * MILLISECONDS_PER_DAY
				)
			),
		},
	};
}

function renderExpirationDate(item: ISearchAssetObjectEntry) {
	return sub(
		Liferay.Language.get('expires-on-x'),
		formatDate(item.embedded?.expirationDate)
	);
}

function renderExpiredDate(item: ISearchAssetObjectEntry) {
	return sub(
		Liferay.Language.get('expired-on-x'),
		formatDate(item.embedded?.expirationDate)
	);
}

function renderReviewDate(item: ISearchAssetObjectEntry) {
	return sub(
		Liferay.Language.get('review-due-on-x'),
		formatDate(item.embedded?.reviewDate)
	);
}

const FRESHNESS_CARDS: FreshnessCard[] = [
	{
		color: 'red',
		description: Liferay.Language.get(
			'these-are-assets-whose-expiration-date-has-already-passed'
		),
		emptyStateDescription: Liferay.Language.get(
			'none-of-your-assets-have-passed-their-expiration-date'
		),
		emptyStateTitle: Liferay.Language.get('no-expired-assets'),
		getViewAllFilters: QUICK_FILTER_UPDATES[QUICK_FILTER_TYPES.EXPIRED],
		icon: 'warning-full',
		itemActionIds: EXPIRATION_ITEM_ACTION_IDS,
		listTitle: Liferay.Language.get('expired-assets'),
		listType: 'expiredAssets',
		renderSubtitle: renderExpiredDate,
		statKey: 'expiredCount',
		title: Liferay.Language.get('expired'),
		viewAllSortKey: FDS_FILTER_ID.DATE_EXPIRATION,
	},
	{
		color: 'orange',
		description: Liferay.Language.get(
			'these-are-approved-assets-about-to-reach-their-expiration-date'
		),
		emptyStateDescription: Liferay.Language.get(
			'no-approved-assets-are-about-to-reach-their-expiration-date'
		),
		emptyStateTitle: Liferay.Language.get('nothing-expiring-soon'),
		getViewAllFilters:
			QUICK_FILTER_UPDATES[QUICK_FILTER_TYPES.EXPIRING_SOON],
		icon: 'flag-full',
		itemActionIds: EXPIRATION_ITEM_ACTION_IDS,
		listType: 'expiringSoon',
		renderSubtitle: renderExpirationDate,
		statKey: 'expiringSoonCount',
		title: Liferay.Language.get('expiring-soon'),
		viewAllSortKey: FDS_FILTER_ID.DATE_EXPIRATION,
	},
	{
		color: 'cyan',
		description: Liferay.Language.get(
			'these-are-assets-whose-review-date-has-already-passed'
		),
		emptyStateDescription: Liferay.Language.get(
			'all-review-dates-are-up-to-date'
		),
		emptyStateTitle: Liferay.Language.get('no-overdue-reviews'),
		getViewAllFilters:
			QUICK_FILTER_UPDATES[QUICK_FILTER_TYPES.REVIEW_DATE_OVERDUE],
		icon: 'date-time',
		itemActionIds: REVIEW_ITEM_ACTION_IDS,
		listType: 'overdueReviews',
		renderSubtitle: renderReviewDate,
		statKey: 'reviewDateOverdueCount',
		title: Liferay.Language.get('overdue-reviews'),
		viewAllSortKey: FDS_FILTER_ID.DATE_REVIEW,
	},
	{
		color: 'yellow',
		description: Liferay.Language.get(
			'these-are-assets-approaching-their-review-date-soon'
		),
		emptyStateDescription: Liferay.Language.get(
			'no-assets-are-approaching-their-review-date'
		),
		emptyStateTitle: Liferay.Language.get('no-upcoming-reviews'),
		getViewAllFilters: getUpcomingReviewsFilters,
		icon: 'date-time',
		itemActionIds: REVIEW_ITEM_ACTION_IDS,
		listType: 'upcomingReviews',
		renderSubtitle: renderReviewDate,
		statKey: 'upcomingReviewCount',
		title: Liferay.Language.get('upcoming-reviews'),
		viewAllSortKey: FDS_FILTER_ID.DATE_REVIEW,
	},
];

function FreshnessList({
	additionalProps,
	card,
}: {
	additionalProps: GovernanceAdditionalProps;
	card: FreshnessCard;
}) {
	const {space, statistics} = useContext(GovernanceContext);

	const {apiURL, viewAllHref} = useMemo(
		() => ({
			apiURL: GovernanceService.getFreshnessListURL(
				card.listType,
				additionalProps.contentProgressFilter,
				space.siteId
			),
			viewAllHref: getAllSectionHref(additionalProps.allSectionFDSName, {
				filters: [
					...Object.entries(card.getViewAllFilters()).map(
						([id, selectedData]) => ({id, selectedData})
					),
					...getSpaceFilters(space),
				],
				sorts: [{direction: 'asc', key: card.viewAllSortKey}],
			}),
		}),
		[additionalProps, card, space]
	);

	return (
		<GovernanceAssetList
			additionalProps={additionalProps}
			apiURL={apiURL}
			count={statistics?.[card.statKey] ?? 0}
			description={card.description}
			emptyStateDescription={card.emptyStateDescription}
			emptyStateTitle={card.emptyStateTitle}
			id={card.listType}
			itemActionIds={card.itemActionIds}
			renderSubtitle={card.renderSubtitle}
			title={card.listTitle ?? card.title}
			viewAllHref={viewAllHref}
		/>
	);
}

export function Freshness({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);
	const [expandedCard, setExpandedCard] = useState<FreshnessCard | null>(
		null
	);

	const title = Liferay.Language.get('freshness');

	const total = statistics?.totalCount ?? 0;

	return (
		<div aria-label={title} className="pt-4" role="group">
			<SectionHeader icon="date-time" title={title} />

			<ClayLayout.Row className="mt-4">
				<ClayLayout.Col className="mb-4" lg={6}>
					<ContentAgeDistribution additionalProps={additionalProps} />
				</ClayLayout.Col>

				<ClayLayout.Col lg={6}>
					<ClayLayout.Row className="h-100">
						{FRESHNESS_CARDS.map((card) => {
							const count = statistics?.[card.statKey] ?? 0;
							const expanded = card === expandedCard;

							return (
								<ClayLayout.Col
									className="mb-4"
									key={card.listType}
									md={6}
								>
									<InteractiveCard
										active={expanded}
										aria-controls={getGovernanceAssetListPanelId(
											card.listType
										)}
										aria-expanded={expanded}
										color={card.color}
										description={card.description}
										icon={card.icon}
										loading={loadingStatistics}
										onClick={() =>
											setExpandedCard(
												expanded ? null : card
											)
										}
										title={card.title}
										value={count}
									>
										<PercentageOfTotal
											count={count}
											template={Liferay.Language.get(
												'x-of-x-assets'
											)}
											total={total}
										/>
									</InteractiveCard>
								</ClayLayout.Col>
							);
						})}
					</ClayLayout.Row>
				</ClayLayout.Col>
			</ClayLayout.Row>

			{expandedCard ? (
				<ClayLayout.Row>
					<ClayLayout.Col className="mb-4" size={12}>
						<FreshnessList
							additionalProps={additionalProps}
							card={expandedCard}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>
			) : null}
		</div>
	);
}
