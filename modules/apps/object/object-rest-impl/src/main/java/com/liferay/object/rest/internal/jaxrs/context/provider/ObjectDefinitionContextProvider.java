/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.context.provider;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.internal.deployer.ObjectDefinitionDeployerImpl;
import com.liferay.object.rest.internal.jaxrs.context.provider.util.ObjectContextProviderUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Javier Gamarra
 */
@Provider
public class ObjectDefinitionContextProvider
	implements ContextProvider<ObjectDefinition> {

	public ObjectDefinitionContextProvider(
		ObjectDefinitionDeployerImpl objectDefinitionDeployerImpl,
		Portal portal) {

		_objectDefinitionDeployerImpl = objectDefinitionDeployerImpl;
		_portal = portal;
	}

	@Override
	public ObjectDefinition createContext(Message message) {
		return ObjectContextProviderUtil.getObjectDefinition(
			message, _objectDefinitionDeployerImpl, _portal);
	}

	private final ObjectDefinitionDeployerImpl _objectDefinitionDeployerImpl;
	private final Portal _portal;

}