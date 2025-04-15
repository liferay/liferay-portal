/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Bárbara Cabrera
 */
public class KBConfigurationDisplayContext {

	public KBConfigurationDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				_themeDisplay.getScopeGroup(), 0, 0)
		).buildString();
	}

	public Map<String, String> getEmailDefinitionTerms(
			KBGroupServiceConfiguration kbGroupServiceConfiguration,
			PortletDisplay portletDisplay, ResourceBundle resourceBundle)
		throws PortalException {

		return LinkedHashMapBuilder.put(
			"[$ARTICLE_ATTACHMENTS$]",
			LanguageUtil.get(
				resourceBundle, "the-article-attachments-file-names")
		).put(
			"[$ARTICLE_CONTENT$]",
			LanguageUtil.get(resourceBundle, "the-article-content")
		).put(
			"[$ARTICLE_CONTENT_DIFF$]",
			LanguageUtil.get(resourceBundle, "the-article-content-diff")
		).put(
			"[$ARTICLE_TITLE$]",
			LanguageUtil.get(resourceBundle, "the-article-title")
		).put(
			"[$ARTICLE_TITLE_DIFF$]",
			LanguageUtil.get(resourceBundle, "the-article-title-diff")
		).put(
			"[$ARTICLE_URL$]",
			LanguageUtil.get(resourceBundle, "the-article-url")
		).put(
			"[$ARTICLE_USER_ADDRESS$]",
			LanguageUtil.get(
				resourceBundle,
				"the-email-address-of-the-user-who-added-the-article")
		).put(
			"[$ARTICLE_USER_NAME$]",
			LanguageUtil.get(resourceBundle, "the-user-who-added-the-article")
		).put(
			"[$ARTICLE_VERSION$]",
			LanguageUtil.get(resourceBundle, "the-article-version")
		).put(
			"[$CATEGORY_TITLE$]",
			LanguageUtil.get(resourceBundle, "category.kb")
		).put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				resourceBundle, "the-company-id-associated-with-the-article")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				resourceBundle, "the-company-mx-associated-with-the-article")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				resourceBundle, "the-company-name-associated-with-the-article")
		).put(
			"[$FROM_ADDRESS$]",
			HtmlUtil.escape(kbGroupServiceConfiguration.emailFromAddress())
		).put(
			"[$FROM_NAME$]",
			HtmlUtil.escape(kbGroupServiceConfiguration.emailFromName())
		).put(
			"[$PORTAL_URL$]", PortalUtil.getPortalURL(_themeDisplay)
		).put(
			"[$PORTLET_NAME$]", HtmlUtil.escape(portletDisplay.getTitle())
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				resourceBundle, "the-site-name-associated-with-the-article")
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				resourceBundle, "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(resourceBundle, "the-name-of-the-email-recipient")
		).build();
	}

	public Map<String, String> getEmailSuggestionDefinitionTerms(
		ResourceBundle resourceBundle) {

		return LinkedHashMapBuilder.put(
			"[$ARTICLE_CONTENT$]",
			LanguageUtil.get(resourceBundle, "the-article-content")
		).put(
			"[$ARTICLE_TITLE$]",
			LanguageUtil.get(resourceBundle, "the-article-title")
		).put(
			"[$ARTICLE_URL$]",
			LanguageUtil.get(resourceBundle, "the-article-url")
		).put(
			"[$COMMENT_CONTENT$]",
			LanguageUtil.get(resourceBundle, "the-comment-content")
		).put(
			"[$COMMENT_CREATE_DATE$]",
			LanguageUtil.get(resourceBundle, "the-comment-create-date")
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				resourceBundle, "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(resourceBundle, "the-name-of-the-email-recipient")
		).build();
	}

	public String getNavigation() {
		if (Validator.isNotNull(_navigation)) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_renderRequest, "navigation", "email-from");

		if (PortalUtil.isRSSFeedsEnabled()) {
			_navigation = ParamUtil.getString(
				_renderRequest, "navigation", "rss");
		}

		return _navigation;
	}

	public VerticalNavItemList getNotificationsVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			_getVerticalNavItemUnsafeConsumer("email-from")
		).add(
			_getVerticalNavItemUnsafeConsumer("article-added-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("article-updated-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("article-review-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("article-expired-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("suggestion-received-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("suggestion-in-progress-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("suggestion-resolved-email")
		).build();
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setActionName(
			"editConfiguration"
		).setMVCPath(
			"/edit_configuration.jsp"
		).setPortletResource(
			ParamUtil.getString(_httpServletRequest, "portletResource")
		).setParameter(
			"portletConfiguration", Boolean.TRUE
		).setParameter(
			"settingsScope",
			PortletPreferencesFactoryConstants.SETTINGS_SCOPE_PORTLET_INSTANCE
		).buildPortletURL();

		return _portletURL;
	}

	public PortletURL getRedirect() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setNavigation(
			getNavigation()
		).buildPortletURL();
	}

	public VerticalNavItemList getSettingsVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			_getVerticalNavItemUnsafeConsumer("rss")
		).build();
	}

	public String getSubtitle() {
		if (Objects.equals(getNavigation(), "rss")) {
			return LanguageUtil.get(_httpServletRequest, "rss-subscription");
		}

		return LanguageUtil.get(_httpServletRequest, "email");
	}

	public String getTitle() {
		return LanguageUtil.get(_httpServletRequest, getNavigation());
	}

	private UnsafeConsumer<VerticalNavItem, Exception>
		_getVerticalNavItemUnsafeConsumer(String key) {

		return verticalNavItem -> {
			verticalNavItem.setActive(Objects.equals(getNavigation(), key));
			verticalNavItem.setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setNavigation(
					key
				).buildString());

			String name = LanguageUtil.get(_httpServletRequest, key);

			verticalNavItem.setId(name);
			verticalNavItem.setLabel(name);
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private String _navigation;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}