/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {useContext, useMemo} from 'react';

import StatusLabel from '../../../../common/components/StatusLabel';
import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {
	FDS_FILTER_ID,
	NO_VALUE,
	UPCOMING_REVIEWS_THRESHOLD_MONTHS,
} from '../../../../common/utils/constants';
import dateFormat from '../../../../common/utils/dateFormat';
import toDatePart from '../../../../common/utils/toDatePart';
import {QUICK_FILTER_TYPES} from '../../../quick_filters/constants';
import {QUICK_FILTER_UPDATES} from '../../../quick_filters/quickFilterUpdates';
import {SectionHeader} from '../../common/SectionHeader';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';
import NeedsReviewCard from './NeedsReviewCard';

function formatReviewDate(reviewDate?: string) {
	if (!reviewDate) {
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
			reviewDate
		) ?? NO_VALUE
	);
}

function renderReviewDate(item: ISearchAssetObjectEntry) {
	return formatReviewDate(item.embedded?.reviewDate);
}

function renderExpiringStatus(item: ISearchAssetObjectEntry) {
	const status = item.embedded?.status;

	if (!status) {
		return NO_VALUE;
	}

	return (
		<StatusLabel
			expirationDate={item.embedded?.expirationDate}
			label={status.label}
		/>
	);
}

function getUpcomingReviewsSelectedData() {
	const from = toDatePart(new Date());

	const monthIndex = from.month - 1 + UPCOMING_REVIEWS_THRESHOLD_MONTHS;

	const month = (monthIndex % 12) + 1;
	const year = from.year + Math.floor(monthIndex / 12);

	return {
		exclude: false,
		from,
		to: {
			day: Math.min(from.day, new Date(year, month, 0).getDate()),
			hour: from.hour,
			minute: from.minute,
			month,
			year,
		},
	};
}

export function NeedsReview({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {space} = useContext(GovernanceContext);

	const groupId = space.siteId;

	const {expiringSoonHref, upcomingReviewsHref} = useMemo(() => {
		const spaceFilters = getSpaceFilters(space);

		return {
			expiringSoonHref: getAllSectionHref(
				additionalProps.allSectionFDSName,
				{
					filters: [
						...Object.entries(
							QUICK_FILTER_UPDATES[
								QUICK_FILTER_TYPES.EXPIRING_SOON
							]()
						).map(([id, selectedData]) => ({id, selectedData})),
						...spaceFilters,
					],
				}
			),
			upcomingReviewsHref: getAllSectionHref(
				additionalProps.allSectionFDSName,
				{
					filters: [
						{
							id: FDS_FILTER_ID.DATE_REVIEW,
							selectedData: getUpcomingReviewsSelectedData(),
						},
						...spaceFilters,
					],
					sorts: [{direction: 'asc', key: FDS_FILTER_ID.DATE_REVIEW}],
				}
			),
		};
	}, [additionalProps.allSectionFDSName, space]);

	const title = Liferay.Language.get('needs-review');

	return (
		<div className="mb-3 mt-4">
			<SectionHeader icon="restore" title={title} />

			<ClayLayout.Row aria-label={title} className="mt-3" role="group">
				<ClayLayout.Col md={6}>
					<NeedsReviewCard
						additionalProps={additionalProps}
						apiURL={GovernanceService.getSearchURL(
							additionalProps.upcomingReviewsFilterString,
							'dateReview:asc',
							groupId
						)}
						description={Liferay.Language.get(
							'assets-approaching-their-review-date'
						)}
						emptyLabel={Liferay.Language.get('no-upcoming-reviews')}
						id={additionalProps.upcomingReviewsFDSName}
						renderSubtitle={renderReviewDate}
						title={Liferay.Language.get('upcoming-reviews')}
						viewAllHref={upcomingReviewsHref}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col md={6}>
					<NeedsReviewCard
						additionalProps={additionalProps}
						apiURL={GovernanceService.getSearchURL(
							additionalProps.expiringSoonFilterString,
							'dateExpiration:asc',
							groupId
						)}
						description={Liferay.Language.get(
							'assets-approaching-their-expiration-date'
						)}
						emptyLabel={Liferay.Language.get(
							'no-assets-are-expiring-soon'
						)}
						id={additionalProps.expiringSoonFDSName}
						renderSubtitle={renderExpiringStatus}
						title={Liferay.Language.get('expiring-soon')}
						viewAllHref={expiringSoonHref}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</div>
	);
}
