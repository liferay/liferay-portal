/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useContext, useState} from 'react';

import {deepEqual} from '../utils/checkDeepEqual';

type PreviewItem = {
	data: {
		className?: string;
		classNameId?: string;
		classPK?: string;
		externalReferenceCode?: string;
		title?: string;
	};
	label: string;
};

type PreviewItemState = {
	recentItemList: PreviewItem[];
	selectedItem: PreviewItem | null;
};

const MAX_RECENT_ITEMS = 100;

const INITIAL_STATE: PreviewItemState = {
	recentItemList: [],
	selectedItem: null,
};

const SelectedItemStateContext = React.createContext(INITIAL_STATE);

const SelectedItemDispatchContext = React.createContext<
	React.Dispatch<React.SetStateAction<PreviewItemState>>
>(() => {});

function itemsAreEqual(itemA: PreviewItem, itemB: PreviewItem) {
	return deepEqual(itemA, itemB);
}

export function DisplayPagePreviewItemContextProvider({
	children,
}: {
	children: React.ReactNode;
}) {
	const [state, setState] = useState(() => INITIAL_STATE);

	return (
		<SelectedItemDispatchContext.Provider value={setState}>
			<SelectedItemStateContext.Provider value={state}>
				{children}
			</SelectedItemStateContext.Provider>
		</SelectedItemDispatchContext.Provider>
	);
}

export function useDisplayPagePreviewItem() {
	return useContext(SelectedItemStateContext).selectedItem;
}

export function useDisplayPageRecentPreviewItemList() {
	return useContext(SelectedItemStateContext).recentItemList;
}

export function useSelectDisplayPagePreviewItem() {
	const setState = useContext(SelectedItemDispatchContext);

	return useCallback(
		(selectedItem: PreviewItem | null) =>
			setState(({recentItemList}) => {
				let nextRecentItemList = recentItemList;

				if (
					selectedItem &&
					!nextRecentItemList.some((item) =>
						itemsAreEqual(selectedItem, item)
					)
				) {
					nextRecentItemList = [
						selectedItem,
						...recentItemList,
					].slice(0, MAX_RECENT_ITEMS);
				}

				return {
					recentItemList: nextRecentItemList,
					selectedItem,
				};
			}),
		[setState]
	);
}
