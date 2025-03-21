/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.resolver;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.util.ExtensionUtil;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Javier de Arcos
 */
@Provider
public class EntityExtensionHandlerContextResolver
	implements ContextResolver<EntityExtensionHandler> {

	public EntityExtensionHandlerContextResolver(
		ExtensionProviderRegistry extensionProviderRegistry) {

		_extensionProviderRegistry = extensionProviderRegistry;
	}

	@Override
	public EntityExtensionHandler getContext(Class<?> clazz) {
		return ExtensionUtil.getEntityExtensionHandler(
			clazz.getName(), _company.getCompanyId(),
			_extensionProviderRegistry);
	}

	@Context
	private Company _company;

	private final ExtensionProviderRegistry _extensionProviderRegistry;

}