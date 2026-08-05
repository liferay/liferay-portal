/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {
	getConfigParamName,
	serializeFDSConfig,
} from '@liferay/frontend-data-set-web';
import React, {useContext, useMemo} from 'react';

import StatusLabel from '../../../../common/components/StatusLabel';
import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {
	EXPIRING_SOON_THRESHOLD_DAYS,
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../common/utils/constants';
import dateFormat from '../../../../common/utils/dateFormat';
import toDatePart from '../../../../common/utils/toDatePart';
import {SectionHeader} from '../../common/SectionHeader';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService from '../GovernanceService';
import {GovernanceAdditionalProps} from '../types';
import NeedsReviewCard from './NeedsReviewCard';

const NO_VALUE = '--';

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
	const to = new Date();

	to.setMonth(to.getMonth() + 1);

	return {exclude: false, from: toDatePart(new Date()), to: toDatePart(to)};
}

function getAllSectionHref(
	fdsName: string,
	config: {filters?: Array<Object>; sorts?: Array<Object>}
) {
	const searchParams = new URLSearchParams({
		[getConfigParamName(fdsName)]: serializeFDSConfig(config),
	});

	return `${Liferay.ThemeDisplay.getPathFriendlyURLPublic()}/cms/all?${searchParams}`;
}

function getDateWindowSelectedData() {
	const from = new Date();

	const to = new Date();

	to.setDate(from.getDate() + EXPIRING_SOON_THRESHOLD_DAYS);

	return {exclude: false, from: toDatePart(from), to: toDatePart(to)};
}

export function NeedsReview({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {space} = useContext(GovernanceContext);

	const spaceId = space.value === 'all' ? undefined : space.value;

	const {expiringSoonHref, upcomingReviewsHref} = useMemo(
		() => ({
			expiringSoonHref: getAllSectionHref(
				additionalProps.allSectionFDSName,
				{
					filters: [
						{
							id: FDS_FILTER_ID.STATUS,
							selectedData: {
								exclude: false,
								selectedItems: [
									{value: WORKFLOW_STATUS.APPROVED},
								],
							},
						},
						{
							id: FDS_FILTER_ID.DATE_EXPIRATION,
							selectedData: getDateWindowSelectedData(),
						},
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
					],
					sorts: [{direction: 'asc', key: FDS_FILTER_ID.DATE_REVIEW}],
				}
			),
		}),
		[additionalProps.allSectionFDSName]
	);

	const title = Liferay.Language.get('needs-review');

	return (
		<div className="mb-4 mt-4">
			<SectionHeader icon="restore" title={title} />

			<ClayLayout.Row aria-label={title} className="mt-3" role="group">
				<ClayLayout.Col md={6}>
					<NeedsReviewCard
						additionalProps={additionalProps}
						apiURL={GovernanceService.getSearchURL(
							additionalProps.upcomingReviewsFilterString,
							'dateReview:asc',
							spaceId
						)}
						description={Liferay.Language.get(
							'assets-approaching-their-review-date'
						)}
						emptyLabel={Liferay.Language.get('no-upcoming-reviews')}
						id="cmsGovernanceUpcomingReviews"
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
							spaceId
						)}
						description={Liferay.Language.get(
							'assets-approaching-their-expiration-date'
						)}
						emptyLabel={Liferay.Language.get(
							'no-assets-are-expiring-soon'
						)}
						id="cmsGovernanceExpiringSoon"
						renderSubtitle={renderExpiringStatus}
						title={Liferay.Language.get('expiring-soon')}
						viewAllHref={expiringSoonHref}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</div>
	);
}
