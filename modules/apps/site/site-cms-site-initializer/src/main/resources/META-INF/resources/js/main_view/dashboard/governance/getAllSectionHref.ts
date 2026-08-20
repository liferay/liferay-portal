/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBaseFilterState,
	TSort,
	getConfigParamName,
	serializeFDSConfig,
} from '@liferay/frontend-data-set-web';

import {FDS_FILTER_ID} from '../../../common/utils/constants';
import {SpaceOption} from '../common/SpacePicker';
import getCMSSectionURL from './getCMSSectionURL';

type FDSConfigFilter = {
	id: string;
	selectedData: IBaseFilterState['selectedData'];
};

export function getSpaceFilters(space: SpaceOption): FDSConfigFilter[] {
	if (!space.siteId) {
		return [];
	}

	return [
		{
			id: FDS_FILTER_ID.SCOPE_GROUP_ID,
			selectedData: {
				exclude: false,
				selectedItems: [{label: space.label, value: space.siteId}],
			},
		},
	];
}

export default function getAllSectionHref(
	fdsName: string,
	config: {filters?: FDSConfigFilter[]; sorts?: TSort[]}
) {
	const searchParams = new URLSearchParams({
		[getConfigParamName(fdsName)]: serializeFDSConfig(config),
	});

	return `${getCMSSectionURL('all')}?${searchParams}`;
}
