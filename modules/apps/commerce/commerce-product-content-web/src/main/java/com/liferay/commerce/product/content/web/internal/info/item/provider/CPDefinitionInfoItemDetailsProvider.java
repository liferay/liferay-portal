/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Alec Sloan
 */
@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=10",
		"item.class.name=com.liferay.commerce.product.model.CPDefinition"
	},
	service = InfoItemDetailsProvider.class
)
public class CPDefinitionInfoItemDetailsProvider
	implements InfoItemDetailsProvider<CPDefinition> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(CPDefinition.class.getName());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(CPDefinition cpDefinition) {
		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CPDefinition.class.getName(),
				cpDefinition.getCPDefinitionId()));
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass,
		CPDefinition cpDefinition) {

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
					CPDefinition.class.getName(),
					cpDefinition.getCPDefinitionId()));
		}

		String scopeExternalReferenceCode = null;

		if (groupId != cpDefinition.getGroupId()) {
			Group group = _groupLocalService.fetchGroup(
				cpDefinition.getGroupId());

			scopeExternalReferenceCode = group.getExternalReferenceCode();
		}

		try {
			CProduct cProduct = cpDefinition.getCProduct();

			return new InfoItemDetails(
				getInfoItemClassDetails(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ERCInfoItemIdentifier(
						cProduct.getExternalReferenceCode(),
						scopeExternalReferenceCode)));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionInfoItemDetailsProvider.class);

	@Reference
	private GroupLocalService _groupLocalService;

}