/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.mvc.Models;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.ws.rs.core.Configuration;

/**
 * @author Neil Griffin
 */
public class ViewEngineContextImpl extends BaseViewEngineContext {

	public ViewEngineContextImpl(
		Configuration configuration, MimeResponse mimeResponse, Models models,
		PortletRequest portletRequest) {

		_configuration = configuration;
		_mimeResponse = mimeResponse;
		_models = models;
		_portletRequest = portletRequest;
	}

	@Override
	public Configuration getConfiguration() {
		return _configuration;
	}

	@Override
	public Models getModels() {
		return _models;
	}

	@Override
	protected MimeResponse getMimeResponse() {
		return _mimeResponse;
	}

	@Override
	protected PortletRequest getPortletRequest() {
		return _portletRequest;
	}

	private final Configuration _configuration;
	private final MimeResponse _mimeResponse;
	private final Models _models;
	private final PortletRequest _portletRequest;

}