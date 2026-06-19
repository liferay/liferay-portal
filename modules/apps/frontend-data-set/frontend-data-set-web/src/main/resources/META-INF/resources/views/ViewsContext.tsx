/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext} from 'react';

export interface IViewsContext {
	activeSnapshotERC: null | string;
	activeView: any;
	defaultSnapshot?: any;
	groupedFilters: Array<any>;
	modifiedFields: any;
	paginationDelta: any;
	snapshotUpdated: boolean;
	snapshots: Array<ISnapshots>;
	snapshotsEnabled: boolean;
	sorts: Array<any>;
	startupViewDataSetSnapshotERC: null | string;
	views: Array<any>;
	visibleFieldNames: any;
}

export interface ISnapshot {
	configuration?: any;
	erc: string;
	id?: number;
	label: string;
}

export interface ISnapshots {
	headerVisible: boolean;
	items: Array<ISnapshot>;
	label?: string;
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
		groupedFilters: [],
		modifiedFields: {},
		paginationDelta: null,
		snapshotUpdated: false,
		snapshots: [],
		snapshotsEnabled: false,
		sorts: [],
		startupViewDataSetSnapshotERC: null,
		views: [],
		visibleFieldNames: {},
	},
	() => {},
]);

export default ViewsContext;
