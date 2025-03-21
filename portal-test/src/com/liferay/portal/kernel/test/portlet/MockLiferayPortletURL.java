/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutablePortletParameters;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletParameters;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;

import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author Cristina González
 */
public class MockLiferayPortletURL implements LiferayPortletURL, RenderURL {

	@Override
	public void addParameterIncludedInPath(String name) {
	}

	@Override
	public void addProperty(String name, String value) {
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
	public String getCacheability() {
		return null;
	}

	@Override
	public String getFragmentIdentifier() {
		return null;
	}

	@Override
	public String getLifecycle() {
		return null;
	}

	@Override
	public String getParameter(String name) {
		String[] parameters = _parameters.get(name);

		if (ArrayUtil.isEmpty(parameters)) {
			return null;
		}

		return parameters[0];
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameters;
	}

	@Override
	public Set<String> getParametersIncludedInPath() {
		return null;
	}

	@Override
	public long getPlid() {
		return 0;
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public PortletMode getPortletMode() {
		return null;
	}

	@Override
	public Set<String> getRemovedParameterNames() {
		return null;
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		return new MockMutableRenderParameters();
	}

	@Override
	public String getResourceID() {
		return null;
	}

	@Override
	public MutableResourceParameters getResourceParameters() {
		return null;
	}

	@Override
	public WindowState getWindowState() {
		return null;
	}

	@Override
	public boolean isAnchor() {
		return false;
	}

	@Override
	public boolean isCopyCurrentRenderParameters() {
		return false;
	}

	@Override
	public boolean isEncrypt() {
		return false;
	}

	@Override
	public boolean isEscapeXml() {
		return false;
	}

	@Override
	public boolean isParameterIncludedInPath(String name) {
		return false;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void removePublicRenderParameter(String name) {
	}

	@Override
	public void setAnchor(boolean anchor) {
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
	}

	@Override
	public void setCacheability(String cacheLevel) {
	}

	@Override
	public void setCopyCurrentRenderParameters(
		boolean copyCurrentRenderParameters) {
	}

	@Override
	public void setDoAsGroupId(long doAsGroupId) {
	}

	@Override
	public void setDoAsUserId(long doAsUserId) {
	}

	@Override
	public void setDoAsUserLanguageId(String doAsUserLanguageId) {
	}

	@Override
	public void setEncrypt(boolean encrypt) {
	}

	@Override
	public void setEscapeXml(boolean escapeXml) {
	}

	@Override
	public void setFragmentIdentifier(String fragment) {
	}

	@Override
	public void setLifecycle(String lifecycle) {
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
		_parameters.put(name, new String[] {value});
	}

	@Override
	public void setParameter(String name, String[] values, boolean append) {
		_parameters.put(name, values);
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		_parameters = parameters;
	}

	@Override
	public void setPlid(long plid) {
	}

	@Override
	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {
	}

	@Override
	public void setProperty(String name, String value) {
	}

	@Override
	public void setRefererGroupId(long refererGroupId) {
	}

	@Override
	public void setRefererPlid(long refererPlid) {
	}

	@Override
	public void setRemovedParameterNames(Set<String> removedParamNames) {
	}

	@Override
	public void setResourceID(String resourceID) {
	}

	@Override
	public void setSecure(boolean secure) throws PortletSecurityException {
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {
	}

	@Override
	public void setWindowStateRestoreCurrentView(
		boolean windowStateRestoreCurrentView) {
	}

	@Override
	public String toString() {
		Set<Map.Entry<String, String[]>> entries = _parameters.entrySet();

		StringBundler sb = new StringBundler();

		if (isSecure()) {
			sb.append("https");
		}
		else {
			sb.append("http");
		}

		sb.append("//localhost/test?");

		for (Map.Entry<String, String[]> entry : entries) {
			String[] values = entry.getValue();

			if (ArrayUtil.isEmpty(values)) {
				continue;
			}

			for (String value : values) {
				if (value == null) {
					continue;
				}

				sb.append(_portletId);
				sb.append("_");
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(value);
				sb.append(";");
			}
		}

		if (!entries.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	@Override
	public void visitReservedParameters(BiConsumer<String, String> biConsumer) {
	}

	@Override
	public void write(Writer writer) throws IOException {
	}

	@Override
	public void write(Writer writer, boolean escapeXML) throws IOException {
	}

	private Map<String, String[]> _parameters = new ConcurrentHashMap<>();
	private String _portletId = "param";

	private class MockMutableRenderParameters
		implements MutableRenderParameters {

		@Override
		public MutablePortletParameters add(
			PortletParameters portletParameters) {

			return this;
		}

		@Override
		public void clear() {
		}

		@Override
		public void clearPrivate() {
		}

		@Override
		public void clearPublic() {
		}

		@Override
		public MutableRenderParameters clone() {
			return new MockMutableRenderParameters();
		}

		@Override
		public Set<String> getNames() {
			return Collections.emptySet();
		}

		@Override
		public String getValue(String name) {
			return null;
		}

		@Override
		public String[] getValues(String name) {
			return new String[0];
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean isPublic(String name) {
			return false;
		}

		@Override
		public boolean removeParameter(String name) {
			return false;
		}

		@Override
		public MutablePortletParameters set(
			PortletParameters portletParameters) {

			return this;
		}

		@Override
		public String setValue(String name, String value) {
			return null;
		}

		@Override
		public String[] setValues(String name, String... values) {
			return new String[0];
		}

		@Override
		public int size() {
			return 0;
		}

	}

}