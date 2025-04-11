/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getDefaultFieldsShape, updateFields} from './formsHelper';

class DDMFormHandler {
	constructor({
		DDMFormInstance,
		accountId = 0,
		channelId = 0,
		cpDefinitionId,
		namespace,
		quantity,
	}) {
		const {
			account: {accountId: contextAccountId},
			commerceChannelId: contextCommerceChannelId,
			currency: {currencyCode},
		} = Liferay.CommerceContext;

		this.DDMFormInstance = DDMFormInstance;
		this.accountId = accountId || contextAccountId;
		this.channelId = channelId || contextCommerceChannelId;
		this.currencyCode = currencyCode;
		this.fields = getDefaultFieldsShape(
			DDMFormInstance.reactComponentRef.current.toJSON()
		);
		this.namespace = namespace;
		this.productId = cpDefinitionId;
		this.quantity = quantity;

		this._attachFormListener();
	}

	_attachFormListener() {
		this.DDMFormInstance.unstable_onEvent(
			({payload: field, type: eventName}) => {
				if (eventName === 'field_change') {
					this.fields = updateFields(this.fields, field);

					Liferay.fire('product-option-upload-update', {
						key: field.fieldInstance.fieldName,
						value: field.value,
					});
				}
			}
		);
	}
}

export default DDMFormHandler;
