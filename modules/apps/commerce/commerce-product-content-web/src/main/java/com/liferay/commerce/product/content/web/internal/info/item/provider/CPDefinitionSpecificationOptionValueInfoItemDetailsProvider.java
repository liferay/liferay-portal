/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider;

import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
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
		"item.class.name=com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue"
	},
	service = InfoItemDetailsProvider.class
)
public class CPDefinitionSpecificationOptionValueInfoItemDetailsProvider
	implements InfoItemDetailsProvider<CPDefinitionSpecificationOptionValue> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(
			CPDefinitionSpecificationOptionValue.class.getName());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue) {

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CPDefinitionSpecificationOptionValue.class.getName(),
				cpDefinitionSpecificationOptionValue.
					getCPDefinitionSpecificationOptionValueId()));
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass,
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue) {

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
					CPDefinitionSpecificationOptionValue.class.getName(),
					cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId()));
		}

		String scopeExternalReferenceCode = null;

		if (groupId != cpDefinitionSpecificationOptionValue.getGroupId()) {
			Group group = _groupLocalService.fetchGroup(
				cpDefinitionSpecificationOptionValue.getGroupId());

			scopeExternalReferenceCode = group.getExternalReferenceCode();
		}

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				CPDefinitionSpecificationOptionValue.class.getName(),
				new ERCInfoItemIdentifier(
					cpDefinitionSpecificationOptionValue.
						getExternalReferenceCode(),
					scopeExternalReferenceCode)));
	}

	@Reference
	private GroupLocalService _groupLocalService;

}