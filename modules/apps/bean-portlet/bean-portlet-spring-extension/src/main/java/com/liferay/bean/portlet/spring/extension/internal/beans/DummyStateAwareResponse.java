/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.WindowState;

import java.io.Serializable;

import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author Neil Griffin
 */
public class DummyStateAwareResponse
	extends DummyPortletResponse implements StateAwareResponse {

	public static final StateAwareResponse INSTANCE =
		new DummyStateAwareResponse();

	@Override
	public PortletMode getPortletMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Map<String, String[]> getRenderParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WindowState getWindowState() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void removePublicRenderParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEvent(QName qName, Serializable serializable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEvent(String name, Serializable serializable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPortletMode(PortletMode portletMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRenderParameter(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRenderParameter(String name, String... values) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRenderParameters(Map<String, String[]> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWindowState(WindowState windowState) {
		throw new UnsupportedOperationException();
	}

}