/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info.item.provider;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=10",
		"item.class.name=com.liferay.commerce.model.CommerceOrderItem"
	},
	service = InfoItemDetailsProvider.class
)
public class CommerceOrderItemInfoItemDetailsProvider
	implements InfoItemDetailsProvider<CommerceOrderItem> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(CommerceOrderItem.class.getName());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		CommerceOrderItem commerceOrderItem) {

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CommerceOrderItem.class.getName(),
				commerceOrderItem.getCommerceOrderItemId()));
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass,
		CommerceOrderItem commerceOrderItem) {

		if (!Objects.equals(
				infoItemIdentifierClass, ClassPKInfoItemIdentifier.class) &&
			!Objects.equals(
				infoItemIdentifierClass, ERCInfoItemIdentifier.class)) {

			return null;
		}

		if (Objects.equals(
				infoItemIdentifierClass, ClassPKInfoItemIdentifier.class)) {

			return new InfoItemDetails(
				getInfoItemClassDetails(),
				new InfoItemReference(
					CommerceOrderItem.class.getName(),
					commerceOrderItem.getCommerceOrderItemId()));
		}

		String scopeExternalReferenceCode = null;

		if (groupId != commerceOrderItem.getGroupId()) {
			Group group = _groupLocalService.fetchGroup(
				commerceOrderItem.getGroupId());

			scopeExternalReferenceCode = group.getExternalReferenceCode();
		}

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CommerceOrderItem.class.getName(),
				new ERCInfoItemIdentifier(
					commerceOrderItem.getExternalReferenceCode(),
					scopeExternalReferenceCode)));
	}

	@Reference
	private GroupLocalService _groupLocalService;

}