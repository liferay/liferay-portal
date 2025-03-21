/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.nested.portlets.web.internal.portlet;

import com.liferay.nested.portlets.web.internal.constants.NestedPortletsPortletKeys;
import com.liferay.nested.portlets.web.internal.constants.NestedPortletsWebKeys;
import com.liferay.nested.portlets.web.internal.display.context.NestedPortletsDisplayContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTemplateConstants;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutTemplateLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-nested-portlets",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=1",
		"com.liferay.portlet.single-page-application=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Nested Portlets",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + NestedPortletsPortletKeys.NESTED_PORTLETS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class NestedPortletsPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String layoutTemplateId = StringPool.BLANK;

		try {
			NestedPortletsDisplayContext nestedPortletsDisplayContext =
				new NestedPortletsDisplayContext(
					_portal.getHttpServletRequest(renderRequest));

			layoutTemplateId =
				nestedPortletsDisplayContext.getLayoutTemplateId();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		String templateId = StringPool.BLANK;
		String templateContent = StringPool.BLANK;

		Map<String, String> columnIds = new HashMap<>();

		if (Validator.isNotNull(layoutTemplateId)) {
			Theme theme = themeDisplay.getTheme();

			LayoutTemplate layoutTemplate =
				_layoutTemplateLocalService.getLayoutTemplate(
					layoutTemplateId, false, theme.getThemeId());

			if (layoutTemplate != null) {
				String content = layoutTemplate.getContent();

				Matcher processColumnMatcher = _processColumnPattern.matcher(
					content);

				while (processColumnMatcher.find()) {
					String columnId = processColumnMatcher.group(2);

					if (Validator.isNotNull(columnId)) {
						columnIds.put(
							columnId,
							renderResponse.getNamespace() +
								StringPool.UNDERLINE + columnId);
					}
				}

				processColumnMatcher.reset();

				templateId = StringBundler.concat(
					theme.getThemeId(),
					LayoutTemplateConstants.CUSTOM_SEPARATOR,
					renderResponse.getNamespace(), layoutTemplateId);

				content = processColumnMatcher.replaceAll(
					"$1" + renderResponse.getNamespace() + "_$2$3");

				Matcher columnIdMatcher = _columnIdPattern.matcher(content);

				templateContent = columnIdMatcher.replaceAll(
					"$1" + renderResponse.getNamespace() + "_$2$3");
			}
		}

		_checkLayout(themeDisplay.getLayout(), columnIds.values());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		renderRequest.setAttribute(
			NestedPortletsWebKeys.TEMPLATE_ID + portletDisplay.getId(),
			templateId);
		renderRequest.setAttribute(
			NestedPortletsWebKeys.TEMPLATE_CONTENT + portletDisplay.getId(),
			templateContent);

		Map<String, Object> vmVariables =
			(Map<String, Object>)renderRequest.getAttribute(
				WebKeys.FTL_VARIABLES + portletDisplay.getId());

		if (vmVariables != null) {
			vmVariables.putAll(columnIds);
		}
		else {
			renderRequest.setAttribute(
				WebKeys.FTL_VARIABLES + portletDisplay.getId(), columnIds);
		}

		super.include(viewTemplate, renderRequest, renderResponse);
	}

	private void _checkLayout(Layout layout, Collection<String> columnIds) {
		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String[] layoutColumnIds = StringUtil.split(
			typeSettingsUnicodeProperties.get(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS));

		boolean updateColumnIds = false;

		for (String columnId : columnIds) {
			String portletIds = typeSettingsUnicodeProperties.getProperty(
				columnId);

			if (Validator.isNotNull(portletIds) &&
				!ArrayUtil.contains(layoutColumnIds, columnId)) {

				layoutColumnIds = ArrayUtil.append(layoutColumnIds, columnId);

				updateColumnIds = true;
			}
		}

		if (updateColumnIds) {
			typeSettingsUnicodeProperties.setProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS,
				StringUtil.merge(layoutColumnIds));

			layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

			try {
				_layoutLocalService.updateLayout(
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getLayoutId(), layout.getTypeSettings());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NestedPortletsPortlet.class);

	private static final Pattern _columnIdPattern = Pattern.compile(
		"([<].*?id=[\"'])([^ ]*?)([\"'].*?[>])", Pattern.DOTALL);
	private static final Pattern _processColumnPattern = Pattern.compile(
		"(processColumn[(]\")(.*?)(\"(?:, *\"(?:.*?)\")?[)])", Pattern.DOTALL);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutTemplateLocalService _layoutTemplateLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.nested.portlets.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}