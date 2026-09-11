/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useContext} from 'react';

import FrontendDataSetContext from '../FrontendDataSetContext';
import ViewsContext from '../views/ViewsContext';
import {IRecentlyVisitedItem} from './recentlyVisited';
import {recordVisit} from './recordVisit';

/**
 * Returns the function a component calls to remember where a row led, reading
 * the Data Set it belongs to and the field its view names rows by off the
 * contexts it is rendered in.
 *
 * A Data Set records the links it renders itself, so a view that draws its own
 * link in a cell renderer takes the user away without the row ever reaching the
 * history the search suggestions read. Such a renderer calls this instead.
 */
export function useRecordVisit() {
	const {id, searchSuggestionsEnabled} = useContext(FrontendDataSetContext);

	const [{activeView}]: any = useContext(ViewsContext);

	return useCallback(
		(
			itemData: any,
			{href, label}: Partial<IRecentlyVisitedItem> = {}
		): void =>
			recordVisit({
				accessibleNameField: activeView?.schema?.accessibleNameField,
				fdsName: id,
				href,
				itemData,
				label,
				searchSuggestionsEnabled,
			}),
		[activeView, id, searchSuggestionsEnabled]
	);
}
