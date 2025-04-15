/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.alloyeditor.web.internal;

import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResources;

import jakarta.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Bradford
 */
@Component(service = PortalWebResources.class)
public class AlloyEditorPortalWebResources implements PortalWebResources {

	@Override
	public String getContextPath() {
		return _servletContext.getContextPath();
	}

	@Override
	public long getLastModified() {
		return _bundle.getLastModified();
	}

	@Override
	public String getResourceType() {
		return PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_ALLOYEDITOR;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();
	}

	private Bundle _bundle;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.editor.alloyeditor.web)"
	)
	private ServletContext _servletContext;

}