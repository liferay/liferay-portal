/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {CommerceServiceProvider} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';

import getAccountGroupsItemSelectorProps from './item_selector/getAccountGroupsItemSelectorProps';
import getChannelsItemSelectorProps from './item_selector/getChannelsItemSelectorProps';

async function updateProduct({
	dataSetId,
	fieldName,
	fieldValueName,
	filterFieldName,
	ids,
	productId,
}) {
	const AdminCatalogResource = CommerceServiceProvider.AdminCatalogAPI('v1');

	try {
		await AdminCatalogResource.updateProduct(productId, {
			[fieldName]: ids.map((id) => ({[fieldValueName]: id})),
			[filterFieldName]: true,
		});
	}
	catch (error) {
		openToast({
			message: Liferay.Language.get('an-unexpected-error-occurred'),
			title: Liferay.Language.get('error'),
			type: 'danger',
		});

		return false;
	}

	openToast({
		message: Liferay.Language.get('your-request-completed-successfully'),
		type: 'success',
	});

	Liferay.fire(FDS_EVENT.UPDATE_DISPLAY, {id: dataSetId});

	return true;
}

export default function ({
	accountGroupDataSetId,
	accountGroupIds,
	channelDataSetId,
	commerceChannelIds,
	namespace,
	productId,
}) {
	let assignedAccountGroupIds = accountGroupIds;
	let assignedChannelIds = commerceChannelIds;

	const eventHandlers = [
		Liferay.on(`${namespace}selectCommerceAccountGroup`, () => {
			openItemSelectorModal({
				...getAccountGroupsItemSelectorProps({
					excludedAccountGroupIds: assignedAccountGroupIds,
					portletNamespace: namespace,
				}),
				items: [],
				onItemsChange: async (accountGroups) => {
					if (!accountGroups.length) {
						return;
					}

					const ids = [
						...assignedAccountGroupIds,
						...accountGroups.map((accountGroup) => accountGroup.id),
					];

					const updated = await updateProduct({
						dataSetId: accountGroupDataSetId,
						fieldName: 'productAccountGroups',
						fieldValueName: 'accountGroupId',
						filterFieldName: 'productAccountGroupFilter',
						ids,
						productId,
					});

					if (updated) {
						assignedAccountGroupIds = ids;
					}
				},
			});
		}),
		Liferay.on(`${namespace}selectCommerceChannel`, () => {
			openItemSelectorModal({
				...getChannelsItemSelectorProps({
					excludedChannelIds: assignedChannelIds,
					portletNamespace: namespace,
				}),
				items: [],
				onItemsChange: async (channels) => {
					if (!channels.length) {
						return;
					}

					const ids = [
						...assignedChannelIds,
						...channels.map((channel) => channel.id),
					];

					const updated = await updateProduct({
						dataSetId: channelDataSetId,
						fieldName: 'productChannels',
						fieldValueName: 'channelId',
						filterFieldName: 'productChannelFilter',
						ids,
						productId,
					});

					if (updated) {
						assignedChannelIds = ids;
					}
				},
			});
		}),
	];

	Liferay.on('destroyPortlet', () => {
		eventHandlers.forEach((eventHandler) => {
			eventHandler.detach();
		});
	});
}
