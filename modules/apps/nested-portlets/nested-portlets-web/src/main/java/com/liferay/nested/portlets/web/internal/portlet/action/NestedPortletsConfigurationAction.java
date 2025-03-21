/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.nested.portlets.web.internal.portlet.action;

import com.liferay.nested.portlets.web.internal.constants.NestedPortletsPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutTemplateLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Peter Fellwock
 */
@Component(
	property = "jakarta.portlet.name=" + NestedPortletsPortletKeys.NESTED_PORTLETS,
	service = ConfigurationAction.class
)
public class NestedPortletsConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String layoutTemplateId = getParameter(
			actionRequest, "layoutTemplateId");
		String oldLayoutTemplateId = ParamUtil.getString(
			actionRequest, "oldLayoutTemplateId");

		if (!oldLayoutTemplateId.equals(layoutTemplateId)) {
			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			_reorganizeNestedColumns(
				actionRequest, portletResource, layoutTemplateId,
				oldLayoutTemplateId);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private List<String> _getColumnNames(String content, String portletId) {
		Matcher matcher = _pattern.matcher(content);

		Set<String> columnIds = new HashSet<>();

		while (matcher.find()) {
			if (Validator.isNotNull(matcher.group(1))) {
				columnIds.add(matcher.group(1));
			}
		}

		Set<String> columnNames = new LinkedHashSet<>();

		for (String columnId : columnIds) {
			if (!columnId.contains(portletId)) {
				columnNames.add(
					_portal.getPortletNamespace(portletId) +
						StringPool.UNDERLINE + columnId);
			}
		}

		return new ArrayList<>(columnNames);
	}

	private void _reorganizeNestedColumns(
			ActionRequest actionRequest, String portletResource,
			String newLayoutTemplateId, String oldLayoutTemplateId)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();
		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		Theme theme = themeDisplay.getTheme();

		LayoutTemplate oldLayoutTemplate =
			_layoutTemplateLocalService.getLayoutTemplate(
				oldLayoutTemplateId, false, theme.getThemeId());

		if (oldLayoutTemplate != null) {
			LayoutTemplate newLayoutTemplate =
				_layoutTemplateLocalService.getLayoutTemplate(
					newLayoutTemplateId, false, theme.getThemeId());

			List<String> newColumns = _getColumnNames(
				newLayoutTemplate.getContent(), portletResource);

			List<String> oldColumns = _getColumnNames(
				oldLayoutTemplate.getContent(), portletResource);

			layoutTypePortlet.reorganizePortlets(newColumns, oldColumns);
		}

		layoutTypePortlet.setStateMax(StringPool.BLANK);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	private static final Pattern _pattern = Pattern.compile(
		"processColumn[(]\"(.*?)\"(?:, *\"(?:.*?)\")?[)]", Pattern.DOTALL);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutTemplateLocalService _layoutTemplateLocalService;

	@Reference
	private Portal _portal;

}