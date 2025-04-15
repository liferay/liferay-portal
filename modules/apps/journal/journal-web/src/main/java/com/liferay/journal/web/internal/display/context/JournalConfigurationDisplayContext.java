/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorReturnType;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.configuration.JournalGroupServiceConfiguration;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.util.DDMStructureUtil;
import com.liferay.journal.web.internal.util.JournalUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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

/**
 * @author Jürgen Kappler
 */
public class JournalConfigurationDisplayContext {

	public JournalConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		JournalGroupServiceConfiguration journalGroupServiceConfiguration,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_journalGroupServiceConfiguration = journalGroupServiceConfiguration;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				JournalPortletKeys.JOURNAL, _themeDisplay.getScopeGroup(), 0, 0)
		).buildString();
	}

	public Map<String, String> getEmailDefinitionTerms() {
		if (_emailDefinitionTerms != null) {
			return _emailDefinitionTerms;
		}

		_emailDefinitionTerms = JournalUtil.getEmailDefinitionTerms(
			_renderRequest, getEmailFromAddress(), getEmailFromName());

		return _emailDefinitionTerms;
	}

	public String getEmailFromAddress() {
		if (Validator.isNotNull(_emailFromAddress)) {
			return _emailFromAddress;
		}

		_emailFromAddress = ParamUtil.getString(
			_httpServletRequest, "preferences--emailFromAddress--",
			_journalGroupServiceConfiguration.emailFromAddress());

		return _emailFromAddress;
	}

	public String getEmailFromName() {
		if (Validator.isNotNull(_emailFromName)) {
			return _emailFromName;
		}

		_emailFromName = ParamUtil.getString(
			_httpServletRequest, "preferences--emailFromName--",
			_journalGroupServiceConfiguration.emailFromName());

		return _emailFromName;
	}

	public JSONArray getHighlightedDDMStructuresJSONArray() throws Exception {
		return JSONUtil.toJSONArray(
			DDMStructureUtil.getHighlightedDDMStructures(_themeDisplay),
			ddmStructure -> JSONUtil.put(
				"ddmStructureId", String.valueOf(ddmStructure.getStructureId())
			).put(
				"name", ddmStructure.getName(_themeDisplay.getLocale())
			).put(
				"scope",
				() -> {
					Group group = GroupLocalServiceUtil.fetchGroup(
						ddmStructure.getGroupId());

					if (group == null) {
						return StringPool.BLANK;
					}

					return LanguageUtil.get(
						_themeDisplay.getLocale(),
						group.getScopeLabel(_themeDisplay));
				}
			));
	}

	public String getNavigation() {
		if (Validator.isNotNull(_navigation)) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_renderRequest, "navigation", "structures");

		return _navigation;
	}

	public VerticalNavItemList getNotificationsVerticalNavItemList() {
		VerticalNavItemList verticalNavItemList =
			VerticalNavItemListBuilder.add(
				_getVerticalNavItemUnsafeConsumer("email-from")
			).add(
				_getVerticalNavItemUnsafeConsumer("web-content-added")
			).add(
				_getVerticalNavItemUnsafeConsumer("web-content-expired")
			).add(
				_getVerticalNavItemUnsafeConsumer(
					"web-content-moved-from-folder")
			).add(
				_getVerticalNavItemUnsafeConsumer("web-content-moved-to-folder")
			).add(
				_getVerticalNavItemUnsafeConsumer("web-content-review")
			).add(
				_getVerticalNavItemUnsafeConsumer("web-content-updated")
			).build();

		if (JournalUtil.hasWorkflowDefinitionsLinks(_themeDisplay)) {
			verticalNavItemList.add(
				_getVerticalNavItemUnsafeConsumer(
					"web-content-approval-denied"));
			verticalNavItemList.add(
				_getVerticalNavItemUnsafeConsumer(
					"web-content-approval-granted"));
			verticalNavItemList.add(
				_getVerticalNavItemUnsafeConsumer(
					"web-content-approval-requested"));
		}

		return verticalNavItemList;
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

	public String getSelectDDMStructureURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_renderRequest);

		DDMStructureItemSelectorCriterion ddmStructureItemSelectorCriterion =
			new DDMStructureItemSelectorCriterion();

		ddmStructureItemSelectorCriterion.setClassNameId(
			PortalUtil.getClassNameId(JournalArticle.class));
		ddmStructureItemSelectorCriterion.setMultiSelection(true);
		ddmStructureItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DDMStructureItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_renderResponse.getNamespace() + "selectDDMStructure",
				ddmStructureItemSelectorCriterion));
	}

	public VerticalNavItemList getSettingsVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			_getVerticalNavItemUnsafeConsumer("structures")
		).build();
	}

	public String getSubtitle() {
		if (Objects.equals(getNavigation(), "structures")) {
			return LanguageUtil.get(_httpServletRequest, "highlighted");
		}

		return LanguageUtil.get(_httpServletRequest, "email");
	}

	public String getTitle() {
		return LanguageUtil.get(_httpServletRequest, getNavigation());
	}

	private UnsafeConsumer<VerticalNavItem, Exception>
		_getVerticalNavItemUnsafeConsumer(String key) {

		return verticalNavItem -> {
			String name = LanguageUtil.get(_httpServletRequest, key);

			verticalNavItem.setActive(Objects.equals(getNavigation(), key));
			verticalNavItem.setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setNavigation(
					key
				).buildString());
			verticalNavItem.setId(name);
			verticalNavItem.setLabel(name);
		};
	}

	private Map<String, String> _emailDefinitionTerms;
	private String _emailFromAddress;
	private String _emailFromName;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final JournalGroupServiceConfiguration
		_journalGroupServiceConfiguration;
	private String _navigation;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}