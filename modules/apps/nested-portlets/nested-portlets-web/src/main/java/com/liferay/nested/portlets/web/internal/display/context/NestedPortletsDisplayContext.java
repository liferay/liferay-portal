/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.nested.portlets.web.internal.display.context;

import com.liferay.nested.portlets.web.internal.configuration.NestedPortletsPortletInstanceConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.plugin.PluginUtil;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.List;
import java.util.TreeMap;

/**
 * @author Juergen Kappler
 */
public class NestedPortletsDisplayContext {

	public NestedPortletsDisplayContext(HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;

		_nestedPortletsPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				NestedPortletsPortletInstanceConfiguration.class,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
	}

	/**
	 * @see com.liferay.portal.util.PortalImpl#getOriginalServletRequest
	 */
	public HttpServletRequest getLastForwardHttpServletRequest() {
		HttpServletRequest currentHttpServletRequest = _httpServletRequest;
		HttpServletRequestWrapper currentRequestWrapper = null;
		HttpServletRequest originalHttpServletRequest = null;
		HttpServletRequest nextHttpServletRequest = null;

		while (currentHttpServletRequest instanceof HttpServletRequestWrapper) {
			if (currentHttpServletRequest instanceof
					PersistentHttpServletRequestWrapper) {

				PersistentHttpServletRequestWrapper
					persistentHttpServletRequestWrapper =
						(PersistentHttpServletRequestWrapper)
							currentHttpServletRequest;

				persistentHttpServletRequestWrapper =
					persistentHttpServletRequestWrapper.clone();

				if (originalHttpServletRequest == null) {
					originalHttpServletRequest =
						persistentHttpServletRequestWrapper.clone();
				}

				if (currentRequestWrapper != null) {
					currentRequestWrapper.setRequest(
						persistentHttpServletRequestWrapper);
				}

				currentRequestWrapper = persistentHttpServletRequestWrapper;
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)currentHttpServletRequest;

			nextHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();

			if ((currentHttpServletRequest.getDispatcherType() ==
					DispatcherType.FORWARD) &&
				(nextHttpServletRequest.getDispatcherType() ==
					DispatcherType.REQUEST)) {

				break;
			}

			currentHttpServletRequest = nextHttpServletRequest;
		}

		if ((currentRequestWrapper != null) &&
			!_isVirtualHostRequest(nextHttpServletRequest)) {

			currentRequestWrapper.setRequest(currentHttpServletRequest);
		}

		if (originalHttpServletRequest != null) {
			return originalHttpServletRequest;
		}

		return currentHttpServletRequest;
	}

	public String getLayoutTemplateId() {
		if (_layoutTemplateId != null) {
			return _layoutTemplateId;
		}

		_layoutTemplateId =
			_nestedPortletsPortletInstanceConfiguration.layoutTemplateId();

		return _layoutTemplateId;
	}

	public List<LayoutTemplate> getLayoutTemplates() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<LayoutTemplate> layoutTemplates =
			LayoutTemplateLocalServiceUtil.getLayoutTemplates(
				themeDisplay.getThemeId());

		layoutTemplates = PluginUtil.restrictPlugins(
			layoutTemplates, themeDisplay.getUser());

		List<String> unsupportedLayoutTemplateIds =
			_getUnsupportedLayoutTemplateIds();

		return ListUtil.filter(
			layoutTemplates,
			layoutTemplate -> !unsupportedLayoutTemplateIds.contains(
				layoutTemplate.getLayoutTemplateId()));
	}

	private List<String> _getUnsupportedLayoutTemplateIds() {
		return ListUtil.fromArray(
			_nestedPortletsPortletInstanceConfiguration.
				layoutTemplatesUnsupported());
	}

	private boolean _isVirtualHostRequest(
		HttpServletRequest httpServletRequest) {

		LayoutSet layoutSet = (LayoutSet)httpServletRequest.getAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET);

		if (layoutSet != null) {
			TreeMap<String, String> virtualHostnames =
				layoutSet.getVirtualHostnames();

			if (!virtualHostnames.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _layoutTemplateId;
	private final NestedPortletsPortletInstanceConfiguration
		_nestedPortletsPortletInstanceConfiguration;

}