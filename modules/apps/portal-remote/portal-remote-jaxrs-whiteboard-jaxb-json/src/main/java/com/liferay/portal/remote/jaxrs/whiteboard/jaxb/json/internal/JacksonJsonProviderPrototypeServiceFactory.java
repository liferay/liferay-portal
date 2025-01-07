/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.jaxb.json.internal;

import com.fasterxml.jackson.jakarta.rs.cfg.Annotations;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class JacksonJsonProviderPrototypeServiceFactory
	implements PrototypeServiceFactory<JacksonJsonProvider> {

	@Override
	public JacksonJsonProvider getService(
		Bundle bundle,
		ServiceRegistration<JacksonJsonProvider> serviceRegistration) {

		return new JacksonJsonProvider(
			Annotations.JACKSON, Annotations.JAKARTA_XML_BIND);
	}

	@Override
	public void ungetService(
		Bundle bundle,
		ServiceRegistration<JacksonJsonProvider> serviceRegistration,
		JacksonJsonProvider jacksonJsonProvider) {
	}

}