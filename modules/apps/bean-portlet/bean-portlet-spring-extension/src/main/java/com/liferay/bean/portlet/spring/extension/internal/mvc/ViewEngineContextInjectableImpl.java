/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.annotation.ManagedBean;

import jakarta.mvc.Models;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.ws.rs.core.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author Neil Griffin
 */
@ManagedBean
@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRedirect")
public class ViewEngineContextInjectableImpl extends BaseViewEngineContext {

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

	@Autowired
	private Configuration _configuration;

	@Autowired
	private MimeResponse _mimeResponse;

	@Autowired
	private Models _models;

	@Autowired
	private PortletRequest _portletRequest;

}