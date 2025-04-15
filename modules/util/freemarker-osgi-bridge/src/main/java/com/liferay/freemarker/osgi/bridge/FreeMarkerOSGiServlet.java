/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.freemarker.osgi.bridge;

import com.liferay.freemarker.osgi.bridge.internal.BundleTemplateLoader;

import freemarker.cache.TemplateLoader;

import freemarker.ext.jakarta.servlet.FreemarkerServlet;

import org.osgi.framework.FrameworkUtil;

/**
 * @author Carlos Sierra Andrés
 */
public class FreeMarkerOSGiServlet extends FreemarkerServlet {

	@Override
	protected TemplateLoader createTemplateLoader(String templatePath) {
		return new BundleTemplateLoader(FrameworkUtil.getBundle(getClass()));
	}

}