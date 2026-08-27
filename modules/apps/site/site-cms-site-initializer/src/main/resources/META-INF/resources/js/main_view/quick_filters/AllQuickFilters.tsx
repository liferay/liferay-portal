/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import {IBaseFilterState, IFDSState} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classNames from 'classnames';
import {fetch} from 'frontend-js-web';
import React, {ComponentProps, useCallback, useEffect, useState} from 'react';

import {
	CMSSiteInitializerFDSNames,
	FDS_EVENT_DISPLAY_UPDATED,
	FDS_FILTER_ID,
} from '../../common/utils/constants';
import {allFDSAtom} from './atoms';
import {QUICK_FILTER_TYPES, QuickFilterType} from './constants';
import {QUICK_FILTER_UPDATES} from './quickFilterUpdates';

import './AllQuickFilters.scss';

interface QuickFilterCounts {
	expired: number;
	expiringSoon: number;
	inDraft: number;
	reviewDateOverdue: number;
	total: number;
}

type QuickFilterSelectedData = {
	exclude?: boolean;
	from?: {day: number; month: number; year: number} | null;
	selectedItems?: Array<{value: number}>;
	to?: {day: number; month: number; year: number} | null;
};

function isSameDateBound(
	actualBound?: QuickFilterSelectedData['from'],
	expectedBound?: QuickFilterSelectedData['from']
) {
	if (!actualBound || !expectedBound) {
		return !actualBound && !expectedBound;
	}

	return (
		actualBound.day === expectedBound.day &&
		actualBound.month === expectedBound.month &&
		actualBound.year === expectedBound.year
	);
}

function matchesSelectedData(
	actualData?: IBaseFilterState['selectedData'],
	expectedData?: IBaseFilterState['selectedData']
) {
	if (!actualData || !expectedData) {
		return false;
	}

	const actual = actualData as QuickFilterSelectedData;
	const expected = expectedData as QuickFilterSelectedData;

	if (Boolean(actual.exclude) !== Boolean(expected.exclude)) {
		return false;
	}

	if (expected.selectedItems) {
		const actualValues =
			actual.selectedItems?.map(({value}) => value) ?? [];
		const expectedValues = expected.selectedItems.map(({value}) => value);

		return (
			actualValues.length === expectedValues.length &&
			expectedValues.every((value) => actualValues.includes(value))
		);
	}

	return (
		isSameDateBound(actual.from, expected.from) &&
		isSameDateBound(actual.to, expected.to)
	);
}

function getActiveQuickFilter(filters: readonly IBaseFilterState[] = []) {
	const activeFilters = filters.filter(
		({active, id}) => active && id !== FDS_FILTER_ID.SCOPE_GROUP_ID
	);

	if (!activeFilters.length) {
		return null;
	}

	for (const quickFilterType of Object.values(QUICK_FILTER_TYPES)) {
		const filterUpdates = QUICK_FILTER_UPDATES[quickFilterType]();

		const filterIds = Object.keys(filterUpdates);

		if (
			activeFilters.length === filterIds.length &&
			activeFilters.every((filter) =>
				matchesSelectedData(
					filter.selectedData,
					filterUpdates[filter.id]
				)
			)
		) {
			return quickFilterType;
		}
	}

	return null;
}

function clearedFilter(filter: IBaseFilterState): IBaseFilterState {
	return {
		...filter,
		active: false,
		odataFilterString: undefined,
		selectedData: undefined,
	};
}

function QuickFilterButton({
	active,
	count,
	displayType,
	icon,
	label,
	onClick,
}: {
	active: boolean;
	count: number;
	displayType: ComponentProps<typeof ClaySticker>['displayType'];
	icon: string;
	label: string;
	onClick: () => void;
}) {
	return (
		<ClayButton
			aria-pressed={active}
			className={classNames('quick-filter-button', {active})}
			displayType="secondary"
			onClick={onClick}
		>
			<div className="align-items-center d-flex">
				<ClaySticker
					className="rounded"
					displayType={displayType}
					size="lg"
				>
					<ClayIcon symbol={icon} />
				</ClaySticker>

				<div className="ml-2">
					<div className="text-dark">{count}</div>

					<div className="text-3 text-secondary text-weight-normal">
						{label}
					</div>
				</div>
			</div>
		</ClayButton>
	);
}

export default function AllQuickFilters({
	freeTier = false,
}: {
	freeTier?: boolean;
}) {
	const [allFDSState, setAllFDSState] =
		useLiferayState<IFDSState>(allFDSAtom);

	const activeQuickFilter = getActiveQuickFilter(allFDSState.filters);

	const [counts, setCounts] = useState<QuickFilterCounts>({
		expired: 0,
		expiringSoon: 0,
		inDraft: 0,
		reviewDateOverdue: 0,
		total: 0,
	});

	const fetchCounts = useCallback(() => {
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
	}, []);

	useEffect(() => {
		if (freeTier) {
			return;
		}

		fetchCounts();

		const handleDisplayUpdated = (event?: {id?: string}) => {
			if (event?.id?.endsWith(CMSSiteInitializerFDSNames.ALL_SECTION)) {
				fetchCounts();
			}
		};

		Liferay.on(FDS_EVENT_DISPLAY_UPDATED, handleDisplayUpdated);

		return () => {
			Liferay.detach(FDS_EVENT_DISPLAY_UPDATED, handleDisplayUpdated);
		};
	}, [fetchCounts, freeTier]);

	const applyQuickFilter = useCallback(
		(quickFilterType: QuickFilterType) => {
			const filterUpdates = QUICK_FILTER_UPDATES[quickFilterType]();

			setAllFDSState({
				...allFDSState,
				filters: allFDSState.filters.map((filter: IBaseFilterState) => {
					const selectedData = filterUpdates[filter.id];

					if (selectedData) {
						return {
							...filter,
							active: true,
							selectedData,
						};
					}

					return clearedFilter(filter);
				}),
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
