/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.info.item.provider;

import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
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
 * @author Mahmoud Azzam
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=10",
		"item.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry"
	},
	service = InfoItemDetailsProvider.class
)
public class CSDiagramEntryInfoItemDetailsProvider
	implements InfoItemDetailsProvider<CSDiagramEntry> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(CSDiagramEntry.class.getName());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(CSDiagramEntry csDiagramEntry) {
		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CSDiagramEntry.class.getName(),
				csDiagramEntry.getCSDiagramEntryId()));
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass,
		CSDiagramEntry csDiagramEntry) {

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
					CSDiagramEntry.class.getName(),
					csDiagramEntry.getCSDiagramEntryId()));
		}

		String scopeExternalReferenceCode = null;

		CProduct cProduct = _cProductLocalService.fetchCProduct(
			csDiagramEntry.getCProductId());

		if ((cProduct != null) && (groupId != cProduct.getGroupId())) {
			Group group = _groupLocalService.fetchGroup(cProduct.getGroupId());

			scopeExternalReferenceCode = group.getExternalReferenceCode();
		}

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CSDiagramEntry.class.getName(),
				new ERCInfoItemIdentifier(
					csDiagramEntry.getExternalReferenceCode(),
					scopeExternalReferenceCode)));
	}

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}