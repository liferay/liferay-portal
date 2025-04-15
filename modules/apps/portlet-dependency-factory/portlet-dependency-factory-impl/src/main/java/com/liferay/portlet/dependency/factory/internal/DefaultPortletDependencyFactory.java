/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.dependency.factory.internal;

import com.liferay.portal.kernel.model.portlet.PortletDependency;
import com.liferay.portal.kernel.model.portlet.PortletDependencyFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.portlet.PortletRequest;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Neil Griffin
 */
@Component(
	property = Constants.SERVICE_RANKING + ":Integer=" + Integer.MIN_VALUE,
	service = PortletDependencyFactory.class
)
public class DefaultPortletDependencyFactory
	implements PortletDependencyFactory {

	@Override
	public PortletDependency createPortletDependency(
		String name, String scope, String version) {

		return new PortletDependencyImpl(name, scope, version, null, null);
	}

	@Override
	public PortletDependency createPortletDependency(
		String name, String scope, String version, String markup,
		PortletRequest portletRequest) {

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				_portal.getHttpServletRequest(portletRequest));

		return new PortletDependencyImpl(
			name, scope, version, markup, absolutePortalURLBuilder);
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private Portal _portal;

}