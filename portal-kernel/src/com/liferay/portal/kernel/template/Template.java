/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.template;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.Map;
import java.util.function.Supplier;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tina Tian
 */
@ProviderType
public interface Template extends Map<String, Object> {

	public void prepare(HttpServletRequest httpServletRequest);

	public void prepareTaglib(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void processTemplate(Writer writer) throws TemplateException;

	public void processTemplate(
			Writer writer,
			Supplier<TemplateResource> errorTemplateResourceSupplier)
		throws TemplateException;

}