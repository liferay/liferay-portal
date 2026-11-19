/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.service;

import com.liferay.commerce.helper.CommerceRoleHelper;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ServiceWrapper.class)
public class CommerceReturnsRolesObjectDefinitionLocalServiceWrapper
	extends ObjectDefinitionLocalServiceWrapper {

	@Override
	public ObjectDefinition publishSystemObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException {

		ObjectDefinition objectDefinition = super.publishSystemObjectDefinition(
			userId, objectDefinitionId);

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_COMMERCE_RETURN") &&
			!Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_COMMERCE_RETURN_ITEM")) {

			return objectDefinition;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(objectDefinition.getCompanyId());

		_commerceRoleHelper.checkCommerceReturnsRoles(serviceContext);

		return objectDefinition;
	}

	@Reference
	private CommerceRoleHelper _commerceRoleHelper;

}