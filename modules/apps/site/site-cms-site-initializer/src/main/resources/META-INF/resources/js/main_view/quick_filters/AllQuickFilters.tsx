/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {IFDSState} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useState} from 'react';

import useRefreshOnFDSDisplayUpdated from '../../common/hooks/useRefreshOnFDSDisplayUpdated';
import {
	CMSSiteInitializerFDSNames,
	FDS_FILTER_ID,
} from '../../common/utils/constants';
import QuickFilterButton from './QuickFilterButton';
import {allFDSAtom} from './atoms';
import {QUICK_FILTER_TYPES, QuickFilterType} from './constants';
import {QUICK_FILTER_UPDATES} from './quickFilterUpdates';
import {applyQuickFilterUpdates, getActiveQuickFilter} from './utils';

interface QuickFilterCounts {
	expired: number;
	expiringSoon: number;
	inDraft: number;
	reviewDateOverdue: number;
	total: number;
}

export default function AllQuickFilters({
	freeTier = false,
}: {
	freeTier?: boolean;
}) {
	const [allFDSState, setAllFDSState] =
		useLiferayState<IFDSState>(allFDSAtom);

	const activeQuickFilter = getActiveQuickFilter(
		allFDSState.filters,
		QUICK_FILTER_UPDATES,
		[FDS_FILTER_ID.SCOPE_GROUP_ID]
	);

	const [counts, setCounts] = useState<QuickFilterCounts>({
		expired: 0,
		expiringSoon: 0,
		inDraft: 0,
		reviewDateOverdue: 0,
		total: 0,
	});

	const fetchCounts = useCallback(() => {
		if (freeTier) {
			return;
		}

		fetch('/o/headless-cms/v1.0/asset-statistics', {
			headers: {
				Accept: 'application/json',
			},
		})
			.then((response) => {
				if (!response.ok) {
					throw new Error(`HTTP ${response.status}`);
				}

				return response.json();
			})
			.then((data) => {
				setCounts({
					expired: data.expiredCount ?? 0,
					expiringSoon: data.expiringSoonCount ?? 0,
					inDraft: data.inDraftCount ?? 0,
					reviewDateOverdue: data.reviewDateOverdueCount ?? 0,
					total: data.totalCount ?? 0,
				});
			})
			.catch((error) => {
				console.error('Failed to fetch asset statistics', error);
			});
	}, [freeTier]);

	useRefreshOnFDSDisplayUpdated(
		CMSSiteInitializerFDSNames.ALL_SECTION,
		fetchCounts
	);

	const applyQuickFilter = useCallback(
		(quickFilterType: QuickFilterType) => {
			setAllFDSState({
				...allFDSState,
				filters: applyQuickFilterUpdates(
					allFDSState.filters,
					QUICK_FILTER_UPDATES[quickFilterType]()
				),
			});
		},
		[allFDSState, setAllFDSState]
	);

	if (counts.total === 0) {
		return null;
	}

	return (
		<div className="all-quick-filters-container">
			<ClayLayout.ContainerFluid
				className="c-pb-4 c-pt-2 c-px-4"
				size={false}
			>
				<ClayLayout.Row>
					<ClayLayout.Col className="c-px-2" size={3}>
						<QuickFilterButton
							active={
								activeQuickFilter ===
								QUICK_FILTER_TYPES.IN_DRAFT
							}
							count={counts.inDraft}
							displayType="secondary"
							icon="pencil"
							label={Liferay.Language.get('in-draft')}
							onClick={() =>
								applyQuickFilter(QUICK_FILTER_TYPES.IN_DRAFT)
							}
						/>
					</ClayLayout.Col>

					<ClayLayout.Col className="c-px-2" size={3}>
						<QuickFilterButton
							active={
								activeQuickFilter ===
								QUICK_FILTER_TYPES.EXPIRING_SOON
							}
							count={counts.expiringSoon}
							displayType="warning"
							icon="flag-full"
							label={Liferay.Language.get('expiring-soon')}
							onClick={() =>
								applyQuickFilter(
									QUICK_FILTER_TYPES.EXPIRING_SOON
								)
							}
						/>
					</ClayLayout.Col>

					<ClayLayout.Col className="c-px-2" size={3}>
						<QuickFilterButton
							active={
								activeQuickFilter === QUICK_FILTER_TYPES.EXPIRED
							}
							count={counts.expired}
							displayType="danger"
							icon="warning-full"
							label={Liferay.Language.get('expired')}
							onClick={() =>
								applyQuickFilter(QUICK_FILTER_TYPES.EXPIRED)
							}
						/>
					</ClayLayout.Col>

					<ClayLayout.Col className="c-px-2" size={3}>
						<QuickFilterButton
							active={
								activeQuickFilter ===
								QUICK_FILTER_TYPES.REVIEW_DATE_OVERDUE
							}
							count={counts.reviewDateOverdue}
							displayType="info"
							icon="date-time"
							label={Liferay.Language.get('review-date-overdue')}
							onClick={() =>
								applyQuickFilter(
									QUICK_FILTER_TYPES.REVIEW_DATE_OVERDUE
								)
							}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>
			</ClayLayout.ContainerFluid>
		</div>
	);
}
