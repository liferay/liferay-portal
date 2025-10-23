/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext} from 'react';

export interface IViewsContext {
	activeCustomViewId: null | string;
	activeView: any;
	customViews: any;
	customViewsEnabled: boolean;
	filters: Array<any>;
	modifiedFields: any;
	paginationDelta: any;
	sorts: Array<any>;
	viewUpdated: boolean;
	views: Array<any>;
	visibleFieldNames: any;
}

export type TViewsContextDispatch = ({
	type,
	value,
}: {
	type: string;
	value: any;
}) => void;

const ViewsContext = createContext<[IViewsContext, any]>([
	{
		activeCustomViewId: null,
		activeView: null,
		customViews: {},
		customViewsEnabled: false,
		filters: [],
		modifiedFields: {},
		paginationDelta: null,
		sorts: [],
		viewUpdated: false,
		views: [],
		visibleFieldNames: {},
	},
	() => {},
]);

export default ViewsContext;
