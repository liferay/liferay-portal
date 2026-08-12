/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {IItemsActions} from '@liferay/frontend-data-set-web';

import type {DashboardAssetListAdditionalProps} from '../../props_transformer/getDashboardAssetListFDSProps';

export type GovernanceAdditionalProps = DashboardAssetListAdditionalProps & {
	allSectionFDSName: string;
	contentProgressFilter: string;
	expiringSoonFDSName: string;
	expiringSoonFilterString: string;
	fdsActionDropdownItems: IItemsActions[];
	upcomingReviewsFDSName: string;
	upcomingReviewsFilterString: string;
};
