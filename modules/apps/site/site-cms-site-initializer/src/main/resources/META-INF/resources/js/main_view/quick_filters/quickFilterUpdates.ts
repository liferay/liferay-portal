/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IBaseFilterState} from '@liferay/frontend-data-set-web';

import {
	EXPIRING_SOON_THRESHOLD_DAYS,
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../common/utils/constants';
import toDatePart from '../../common/utils/toDatePart';
import {QUICK_FILTER_TYPES, QuickFilterType} from './constants';

type FilterUpdates = Record<string, IBaseFilterState['selectedData']>;

export function getStatusSelectedData(
	selectedItems: {label: string; value: number}[]
) {
	return {exclude: false, selectedItems};
}

export const QUICK_FILTER_UPDATES: Record<
	QuickFilterType,
	() => FilterUpdates
> = {
	[QUICK_FILTER_TYPES.EXPIRED]: () => ({
		[FDS_FILTER_ID.STATUS]: getStatusSelectedData([
			{
				label: Liferay.Language.get('expired'),
				value: WORKFLOW_STATUS.EXPIRED,
			},
		]),
	}),
	[QUICK_FILTER_TYPES.EXPIRING_SOON]: () => {
		const to = new Date();

		to.setDate(to.getDate() + EXPIRING_SOON_THRESHOLD_DAYS);

		return {
			[FDS_FILTER_ID.DATE_EXPIRATION]: {
				exclude: false,
				from: toDatePart(new Date()),
				to: toDatePart(to),
			},
			[FDS_FILTER_ID.STATUS]: getStatusSelectedData([
				{
					label: Liferay.Language.get('approved'),
					value: WORKFLOW_STATUS.APPROVED,
				},
			]),
		};
	},
	[QUICK_FILTER_TYPES.IN_DRAFT]: () => ({
		[FDS_FILTER_ID.STATUS]: getStatusSelectedData([
			{
				label: Liferay.Language.get('draft'),
				value: WORKFLOW_STATUS.DRAFT,
			},
		]),
	}),
	[QUICK_FILTER_TYPES.REVIEW_DATE_OVERDUE]: () => ({
		[FDS_FILTER_ID.DATE_REVIEW]: {
			exclude: false,
			from: null,
			to: toDatePart(new Date()),
		},
	}),
};
