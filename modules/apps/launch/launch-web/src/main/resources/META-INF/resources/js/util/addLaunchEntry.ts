/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal, openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';

import {
	createLaunchEntry,
	listLaunchEntries,
	resolveLaunchEntry,
} from '../api/launches';
import getSelectedItemData from './getSelectedItemData';

export interface LaunchEntryTypeProps {
	className: string;
	itemSelectorURL: string;
	label: string;
}

export default function addLaunchEntry({
	itemSelectedEventName,
	launchEntryType,
	launchSetId,
	onAdd,
	portletNamespace,
	resolveLaunchEntryURL,
}: {
	itemSelectedEventName: string;
	launchEntryType: LaunchEntryTypeProps;
	launchSetId: number;
	onAdd: () => void;
	portletNamespace: string;
	resolveLaunchEntryURL: string;
}) {
	openSelectionModal({
		onSelect: (selectedItem: unknown) => {
			resolveLaunchEntry({
				className: launchEntryType.className,
				portletNamespace,
				resourceURL: resolveLaunchEntryURL,
				selectedItemData: getSelectedItemData(selectedItem),
			})
				.then(async ({baseVersionKey, classPK}) => {
					const launchEntries = await listLaunchEntries({
						className: launchEntryType.className,
						classPK,
						launchSetId,
					});

					if (launchEntries.length) {
						throw new Error(
							Liferay.Language.get(
								'this-item-is-already-in-the-launch'
							)
						);
					}

					return createLaunchEntry({
						baseVersionKey,
						className: launchEntryType.className,
						classPK,
						launchSetId,
					});
				})
				.then(() => onAdd())
				.catch((exception: Error) =>
					openToast({
						message: escapeHTML(exception.message),
						type: 'danger',
					})
				);
		},
		selectEventName: itemSelectedEventName,
		title: launchEntryType.label,
		url: launchEntryType.itemSelectorURL,
	});
}
