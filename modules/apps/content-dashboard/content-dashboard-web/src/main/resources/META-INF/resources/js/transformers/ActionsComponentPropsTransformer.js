/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import SidebarPanel from '../components/SidebarPanel';
import SidebarPanelInfoView from '../components/SidebarPanelInfoView/SidebarPanelInfoView';
import SidebarPanelMetricsView from '../components/SidebarPanelMetricsView';
import {
	handlePanelStateFromSession,
	handleSessionOnSidebarOpen,
} from './panelStateHandler';

const ACTIVE_ROW_CSS_CLASS = 'table-active';

const deselectAllRows = (portletNamespace) => {
	const activeRows = document.querySelectorAll(
		`#${portletNamespace}contentSearchContainer tr.${ACTIVE_ROW_CSS_CLASS}`
	);

	activeRows.forEach((row) => row.classList.remove(ACTIVE_ROW_CSS_CLASS));
};

const getRow = (portletNamespace, rowId) =>
	document.querySelector(
		`#${portletNamespace}contentSearchContainer [data-rowid="${rowId}"]`
	);

const selectRow = (portletNamespace, rowId) => {
	deselectAllRows(portletNamespace);

	const currentRow = getRow(portletNamespace, rowId);

	if (!currentRow) {
		return;
	}

	currentRow.classList.add(ACTIVE_ROW_CSS_CLASS);
};

const showSidebar = ({
	View,
	contentPerformanceDataFetchURL,
	fetchURL,
	portletNamespace,
	singlePageApplicationEnabled,
}) => {
	const id = `${portletNamespace}sidebar`;

	const sidebarPanel = Liferay.component(id);

	if (!sidebarPanel) {
		const container = document.body.appendChild(
			document.createElement('div')
		);

		render(
			SidebarPanel,
			{
				contentPerformanceDataFetchURL,
				fetchURL,
				onClose: () => {
					Liferay.component(id).close();

					deselectAllRows(portletNamespace);
				},
				ref: (element) => {
					Liferay.component(id, element);
				},
				singlePageApplicationEnabled,
				viewComponent: View,
			},
			container
		);
	}
	else {
		sidebarPanel.open(
			{contentPerformanceDataFetchURL, url: fetchURL},
			View
		);
	}
};

const actions = {
	showInfo({
		contentPerformanceDataFetchURL,
		fetchURL,
		panelState,
		portletNamespace,
		rowId,
		selectedItemRowId,
		singlePageApplicationEnabled,
	}) {
		selectRow(portletNamespace, rowId);

		if (singlePageApplicationEnabled) {
			handleSessionOnSidebarOpen({
				fetchURL,
				panelState,
				rowId,
				selectedItemRowId,
			});
		}

		showSidebar({
			View: SidebarPanelInfoView,
			contentPerformanceDataFetchURL,
			fetchURL,
			portletNamespace,
			singlePageApplicationEnabled,
		});
	},
	showMetrics({fetchURL, portletNamespace, rowId}) {
		selectRow(portletNamespace, rowId);
		showSidebar({
			View: SidebarPanelMetricsView,
			fetchURL,
			portletNamespace,
		});
	},
};

export {selectRow, showSidebar};

export default function propsTransformer({
	additionalProps,
	items,
	portletNamespace,
	...otherProps
}) {
	const {
		panelState,
		selectedItemRowId,
		singlePageApplicationEnabled = !!Liferay.SPA,
	} = additionalProps;

	if (singlePageApplicationEnabled) {
		handlePanelStateFromSession(additionalProps);
	}

	return {
		...otherProps,
		items: items.map((item) => {
			return {
				...item,
				onClick(event) {
					const action = item.data?.action;

					if (action) {
						event.preventDefault();

						actions[action]({
							contentPerformanceDataFetchURL:
								item.data.contentPerformanceDataFetchURL,
							fetchURL: item.data.fetchURL,
							panelState,
							portletNamespace,
							rowId: item.data.classPK,
							selectedItemRowId,
							singlePageApplicationEnabled,
						});
					}
				},
			};
		}),
	};
}
