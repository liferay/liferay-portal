/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.extension;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;
import jakarta.portlet.annotations.PortletSerializable;

import java.io.Writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author Neil Griffin
 */
public class CSRFLiferayPortletURL implements LiferayPortletURL {

	public CSRFLiferayPortletURL(String portletId) {
		_portletId = portletId;
	}

	@Override
	public void addParameterIncludedInPath(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Appendable append(Appendable appendable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Appendable append(Appendable appendable, boolean escapeXML) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCacheability() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.ACTION_PHASE;
	}

	@Override
	public String getParameter(String name) {
		return (String)ArrayUtil.getValue(_parameters.get(name), 0);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameters;
	}

	@Override
	public Set<String> getParametersIncludedInPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getPlid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public PortletMode getPortletMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getRemovedParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getResourceID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MutableResourceParameters getResourceParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WindowState getWindowState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAnchor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCopyCurrentRenderParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEncrypt() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEscapeXml() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isParameterIncludedInPath(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removePublicRenderParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAnchor(boolean anchor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCacheability(String cacheLevel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCopyCurrentRenderParameters(
		boolean copyCurrentRenderParameters) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void setDoAsGroupId(long doAsGroupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDoAsUserId(long doAsUserId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDoAsUserLanguageId(String doAsUserLanguageId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEncrypt(boolean encrypt) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEscapeXml(boolean escapeXml) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLifecycle(String lifecycle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParameter(String name, String value) {
		_parameters.put(name, new String[] {value});
	}

	@Override
	public void setParameter(String name, String... values) {
		_parameters.put(name, values);
	}

	@Override
	public void setParameter(String name, String value, boolean append) {
		String[] existingValues = _parameters.get(name);

		if (existingValues != null) {
			_parameters.put(name, ArrayUtil.append(existingValues, value));
		}
		else {
			_parameters.put(name, new String[] {value});
		}
	}

	@Override
	public void setParameter(String name, String[] values, boolean append) {
		String[] existingValues = _parameters.get(name);

		if (existingValues != null) {
			_parameters.put(name, ArrayUtil.append(existingValues, values));
		}
		else {
			_parameters.put(name, values);
		}
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		_parameters = parameters;
	}

	@Override
	public void setPlid(long plid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	@Override
	public void setPortletMode(PortletMode portletMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setProperty(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRefererGroupId(long refererGroupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRefererPlid(long refererPlid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRemovedParameterNames(Set<String> removedParamNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResourceID(String resourceID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSecure(boolean secure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWindowState(WindowState windowState) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWindowStateRestoreCurrentView(
		boolean windowStateRestoreCurrentView) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void visitReservedParameters(BiConsumer<String, String> biConsumer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(Writer writer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(Writer writer, boolean escapeXML) {
		throw new UnsupportedOperationException();
	}

	private Map<String, String[]> _parameters = new HashMap<>();
	private String _portletId;

}