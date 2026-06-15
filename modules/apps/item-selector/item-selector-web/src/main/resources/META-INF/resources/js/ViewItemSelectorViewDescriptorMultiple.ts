/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getOpener} from 'frontend-js-web';

export interface Props {
	itemSelectorReturnType: string;
	itemSelectorSelectedEvent: string;
	namespace: string;
}

export default function ({
	itemSelectorReturnType,
	itemSelectorSelectedEvent,
	namespace,
}: Props) {
	const searchContainer = Liferay.SearchContainer.get(`${namespace}entries`);
	const containerId = `${namespace}entries`;

	const GLOBAL_KEY = `${namespace}__idToJsonMap`;
	const idToJsonMap: Map<string, string> =
		(window as any)[GLOBAL_KEY] || new Map<string, string>();
	(window as any)[GLOBAL_KEY] = idToJsonMap;

	const getContainer = (): HTMLElement | null =>
		document.getElementById(containerId);

	const getIdFromJsonString = (
		jsonStr: string | undefined | null
	): string | null => {
		if (!jsonStr) {return null;}
		try {
			const obj = JSON.parse(jsonStr);
			const id = obj?.assetEntryId ?? obj?.classPK;

			return id != null ? String(id) : null;
		}
		catch {
			return null;
		}
	};

	const indexVisibleRows = () => {
		const container = getContainer();
		if (!container) {return;}

		const rows = container.querySelectorAll<HTMLElement>(
			'li[data-value], tr[data-value], dd[data-value]'
		);

		rows.forEach((row) => {
			const jsonStr = row.dataset.value;
			const id = getIdFromJsonString(jsonStr);
			if (id && jsonStr) {
				idToJsonMap.set(id, jsonStr);
			}
		});
	};

	const getSelectedIds = (): string[] => {
		const nodeList = searchContainer.select.getAllSelectedElements();
		const ids: string[] = [];

		nodeList.each((node: any) => {
			const v = node.get('value');
			if (v != null) {ids.push(String(v));}
		});

		return Array.from(new Set(ids));
	};

	const buildPayload = (): string[] => {
		indexVisibleRows();
		const ids = getSelectedIds();

		return ids.map((id) => idToJsonMap.get(id) ?? id);
	};

	const searchContainerOnHandler = searchContainer.on('rowToggled', () => {
		const payload = buildPayload();

		getOpener().Liferay.fire(itemSelectorSelectedEvent, {
			data: {
				returnType: itemSelectorReturnType,
				value: payload,
			},
		});
	});

	return {
		dispose() {
			searchContainerOnHandler.detach();
		},
	};
}
