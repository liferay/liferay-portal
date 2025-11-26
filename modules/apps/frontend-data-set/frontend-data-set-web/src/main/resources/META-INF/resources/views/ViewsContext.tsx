/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext} from 'react';

export interface IViewsContext {
	activeSnapshotERC: null | string;
	activeView: any;
	defaultView?: any;
	filters: Array<any>;
	modifiedFields: any;
	paginationDelta: any;
	snapshots: Array<ISnapshot>;
	snapshotsEnabled: boolean;
	sorts: Array<any>;
	viewUpdated: boolean;
	views: Array<any>;
	visibleFieldNames: any;
}

export interface ISnapshot {
	configuration?: any;
	erc: string;
	label: string;
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
		activeSnapshotERC: null,
		activeView: null,
		filters: [],
		modifiedFields: {},
		paginationDelta: null,
		snapshots: [],
		snapshotsEnabled: false,
		sorts: [],
		viewUpdated: false,
		views: [],
		visibleFieldNames: {},
	},
	() => {},
]);

export default ViewsContext;
