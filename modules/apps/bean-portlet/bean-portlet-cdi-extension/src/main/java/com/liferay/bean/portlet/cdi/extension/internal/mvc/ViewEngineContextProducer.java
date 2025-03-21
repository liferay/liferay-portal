/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import jakarta.mvc.Models;
import jakarta.mvc.engine.ViewEngineContext;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.ws.rs.core.Configuration;

/**
 * @author Neil Griffin
 */
@ApplicationScoped
public class ViewEngineContextProducer {

	@Dependent
	@Produces
	public ViewEngineContext getViewEngineContext(
		Configuration configuration, MimeResponse mimeResponse, Models models,
		PortletRequest portletRequest) {

		return new ViewEngineContextImpl(
			configuration, portletRequest.getLocale(), mimeResponse, models,
			portletRequest);
	}

}