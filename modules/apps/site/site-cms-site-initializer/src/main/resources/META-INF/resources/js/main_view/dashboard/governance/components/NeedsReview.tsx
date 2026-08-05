/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {useContext} from 'react';

import StatusLabel from '../../../../common/components/StatusLabel';
import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import dateFormat from '../../../../common/utils/dateFormat';
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

export function NeedsReview({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {space} = useContext(GovernanceContext);

	const spaceId = space.value === 'all' ? undefined : space.value;

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
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</div>
	);
}
