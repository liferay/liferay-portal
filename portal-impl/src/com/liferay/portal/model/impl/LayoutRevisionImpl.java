/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutRevisionLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutRevisionImpl extends LayoutRevisionBaseImpl {

	@Override
	public String getBreadcrumb(Locale locale) throws PortalException {
		List<Layout> layouts = _getAncestors();

		StringBundler sb = new StringBundler((4 * layouts.size()) + 5);

		Group group = getGroup();

		sb.append(group.getLayoutRootNodeName(isPrivateLayout(), locale));

		sb.append(StringPool.SPACE);
		sb.append(StringPool.GREATER_THAN);
		sb.append(StringPool.SPACE);

		Collections.reverse(layouts);

		for (Layout layout : layouts) {
			sb.append(HtmlUtil.escape(_getLayoutName(layout, locale)));
			sb.append(StringPool.SPACE);
			sb.append(StringPool.GREATER_THAN);
			sb.append(StringPool.SPACE);
		}

		sb.append(HtmlUtil.escape(getName(locale)));

		return sb.toString();
	}

	@Override
	public List<LayoutRevision> getChildren() {
		return LayoutRevisionLocalServiceUtil.getChildLayoutRevisions(
			getLayoutSetBranchId(), getLayoutRevisionId(), getPlid());
	}

	@Override
	public ColorScheme getColorScheme() throws PortalException {
		if (isInheritLookAndFeel()) {
			return getLayoutSet().getColorScheme();
		}

		return ThemeLocalServiceUtil.getColorScheme(
			getCompanyId(), getTheme().getThemeId(), getColorSchemeId());
	}

	@Override
	public String getCssText() throws PortalException {
		if (isInheritLookAndFeel()) {
			return getLayoutSet().getCss();
		}

		return getCss();
	}

	@Override
	public Group getGroup() {
		try {
			return GroupLocalServiceUtil.getGroup(getGroupId());
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public String getHTMLTitle(Locale locale) {
		String localeLanguageId = LocaleUtil.toLanguageId(locale);

		return getHTMLTitle(localeLanguageId);
	}

	@Override
	public String getHTMLTitle(String localeLanguageId) {
		String htmlTitle = getTitle(localeLanguageId);

		if (Validator.isNull(htmlTitle)) {
			htmlTitle = getName(localeLanguageId);
		}

		return htmlTitle;
	}

	@Override
	public boolean getIconImage() {
		if (getIconImageId() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public LayoutBranch getLayoutBranch() throws PortalException {
		return LayoutBranchLocalServiceUtil.getLayoutBranch(
			getLayoutBranchId());
	}

	@Override
	public LayoutSet getLayoutSet() throws PortalException {
		return LayoutSetLocalServiceUtil.getLayoutSet(
			getGroupId(), isPrivateLayout());
	}

	@Override
	public String getRegularURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String url = PortalUtil.getLayoutURL(
			LayoutLocalServiceUtil.getLayout(getPlid()), themeDisplay);

		if (!CookiesManagerUtil.hasSessionId(httpServletRequest) &&
			(url.startsWith(PortalUtil.getPortalURL(httpServletRequest)) ||
			 url.startsWith(StringPool.SLASH))) {

			HttpSession httpSession = httpServletRequest.getSession();

			url = PortalUtil.getURLWithSessionId(url, httpSession.getId());
		}

		return url;
	}

	@Override
	public String getTarget() {
		String target = getTypeSettingsProperty("target");

		if (Validator.isNull(target)) {
			return StringPool.BLANK;
		}

		return "target=\"" + HtmlUtil.escapeAttribute(target) + "\"";
	}

	@Override
	public Theme getTheme() throws PortalException {
		if (isInheritLookAndFeel()) {
			return getLayoutSet().getTheme();
		}

		return ThemeLocalServiceUtil.getTheme(getCompanyId(), getThemeId());
	}

	@Override
	public String getThemeSetting(String key, String device) {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		String value = typeSettingsUnicodeProperties.getProperty(
			ThemeSettingImpl.namespaceProperty(device, key));

		if (value != null) {
			return value;
		}

		if (!isInheritLookAndFeel()) {
			try {
				Theme theme = getTheme();

				return theme.getSetting(key);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		try {
			LayoutSet layoutSet = getLayoutSet();

			value = layoutSet.getThemeSetting(key, device);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return value;
	}

	@Override
	public String getTypeSettings() {
		if (_typeSettingsUnicodeProperties == null) {
			return super.getTypeSettings();
		}

		return _typeSettingsUnicodeProperties.toString();
	}

	@Override
	public UnicodeProperties getTypeSettingsProperties() {
		if (_typeSettingsUnicodeProperties == null) {
			_typeSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				super.getTypeSettings()
			).build();
		}

		return _typeSettingsUnicodeProperties;
	}

	@Override
	public String getTypeSettingsProperty(String key) {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return typeSettingsUnicodeProperties.getProperty(key);
	}

	@Override
	public String getTypeSettingsProperty(String key, String defaultValue) {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return typeSettingsUnicodeProperties.getProperty(key, defaultValue);
	}

	@Override
	public boolean hasChildren() {
		if (!getChildren().isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isContentDisplayPage() {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		String defaultAssetPublisherPortletId =
			typeSettingsUnicodeProperties.getProperty(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID);

		if (Validator.isNotNull(defaultAssetPublisherPortletId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isCustomizable() throws PortalException {
		Layout layout = LayoutLocalServiceUtil.getLayout(getPlid());

		if (!layout.isTypePortlet()) {
			return false;
		}

		if (GetterUtil.getBoolean(
				getTypeSettingsProperty(LayoutConstants.CUSTOMIZABLE_LAYOUT))) {

			return true;
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (layoutTypePortlet.isCustomizable()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isIconImage() {
		return getIconImage();
	}

	@Override
	public boolean isInheritLookAndFeel() {
		if (Validator.isNull(getThemeId()) ||
			Validator.isNull(getColorSchemeId())) {

			return true;
		}

		return false;
	}

	@Override
	public void setTypeSettings(String typeSettings) {
		_typeSettingsUnicodeProperties = null;

		super.setTypeSettings(typeSettings);
	}

	@Override
	public void setTypeSettingsProperties(
		UnicodeProperties typeSettingsUnicodeProperties) {

		_typeSettingsUnicodeProperties = typeSettingsUnicodeProperties;

		super.setTypeSettings(_typeSettingsUnicodeProperties.toString());
	}

	private List<Layout> _getAncestors() throws PortalException {
		Layout layout = LayoutLocalServiceUtil.getLayout(getPlid());

		return layout.getAncestors();
	}

	private String _getLayoutName(Layout layout, Locale locale) {
		LayoutRevision layoutRevision =
			LayoutRevisionLocalServiceUtil.fetchLatestLayoutRevision(
				getLayoutSetBranchId(), layout.getPlid());

		if (layoutRevision == null) {
			return layout.getName(locale);
		}

		return layoutRevision.getName(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutRevisionImpl.class);

	private UnicodeProperties _typeSettingsUnicodeProperties;

}