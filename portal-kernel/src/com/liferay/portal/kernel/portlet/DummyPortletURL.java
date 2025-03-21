/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.StringPool;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.annotations.PortletSerializable;

import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class DummyPortletURL implements PortletURL {

	public static DummyPortletURL getInstance() {
		return _dummyPortletURL;
	}

	@Override
	public void addProperty(String key, String value) {
	}

	@Override
	public Appendable append(Appendable appendable) throws IOException {
		return null;
	}

	@Override
	public Appendable append(Appendable appendable, boolean escapeXML)
		throws IOException {

		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.emptyMap();
	}

	@Override
	public PortletMode getPortletMode() {
		return PortletMode.VIEW;
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		return null;
	}

	@Override
	public WindowState getWindowState() {
		return WindowState.NORMAL;
	}

	@Override
	public void removePublicRenderParameter(String name) {
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
	}

	@Override
	public void setParameter(String name, String value) {
	}

	@Override
	public void setParameter(String name, String[] values) {
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
	}

	@Override
	public void setPortletMode(PortletMode portletMode) {
	}

	@Override
	public void setProperty(String key, String value) {
	}

	@Override
	public void setSecure(boolean secure) {
	}

	@Override
	public void setWindowState(WindowState windowState) {
	}

	@Override
	public String toString() {
		return StringPool.BLANK;
	}

	@Override
	public void write(Writer writer) {
	}

	@Override
	public void write(Writer writer, boolean escapeXML) {
	}

	private DummyPortletURL() {
	}

	private static final DummyPortletURL _dummyPortletURL =
		new DummyPortletURL();

}