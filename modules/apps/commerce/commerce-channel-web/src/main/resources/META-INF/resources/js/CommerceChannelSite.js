/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {delegate} from 'frontend-js-web';

import getSitesItemSelectorProps from './item_selector/getSitesItemSelectorProps';

export default function ({
	commerceChannelId,
	portletNamespace,
	removeCommerceChannelSiteIcon,
	searchContainerId,
	site,
}) {
	let currentSite = site;

	Liferay.componentReady(`${portletNamespace}${searchContainerId}`).then(
		(searchContainer) => {
			const searchContainerContentBox = searchContainer.get('contentBox');

			const searchContainerNode = searchContainerContentBox.getDOMNode();

			const selectSiteButton = document.getElementById(
				`${portletNamespace}selectSite`
			);

			if (selectSiteButton) {
				selectSiteButton.addEventListener('click', (event) => {
					event.preventDefault();

					openItemSelectorModal({
						...getSitesItemSelectorProps({
							commerceChannelId,
							portletNamespace,
						}),
						items: currentSite ? [currentSite] : [],
						onItemsChange: ([selectedSite]) => {
							if (!selectedSite) {
								return;
							}

							const links =
								searchContainerNode.querySelectorAll(
									'.modify-link'
								);

							links.forEach((link) => {
								searchContainer.deleteRow(
									link.closest('tr'),
									link.dataset.rowid
								);
							});

							const rowColumns = [];

							rowColumns.push(
								Liferay.Util.escapeHTML(selectedSite.name)
							);
							rowColumns.push(
								`<a class="float-right modify-link" data-rowId="${selectedSite.id}" href="javascript:void(0);">${removeCommerceChannelSiteIcon}</a>`
							);

							const siteGroupInput = document.getElementById(
								`${portletNamespace}siteGroupId`
							);

							if (siteGroupInput) {
								siteGroupInput.value = selectedSite.id;
							}

							searchContainer.addRow(
								rowColumns,
								String(selectedSite.id)
							);

							searchContainer.updateDataStore();

							currentSite = selectedSite;
						},
					});
				});
			}

			delegate(searchContainerNode, 'click', '.modify-link', (event) => {
				const link = event.delegateTarget;

				const row = link.closest('tr');

				searchContainer.deleteRow(row, link.dataset.rowid);

				const siteGroupInput = document.getElementById(
					`${portletNamespace}siteGroupId`
				);

				if (siteGroupInput) {
					siteGroupInput.value = 0;
				}

				currentSite = null;
			});
		}
	);
}
