/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PersonalMenuEntryHelper;
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.PortletCategoryConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.AdministratorControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.OmniadminControlPanelEntry;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletTitleComparator;
import com.liferay.portal.util.WebAppPool;
import com.liferay.product.navigation.personal.menu.BasePersonalMenuEntry;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Evan Thibodeau
 */
public class EditRolePermissionsNavigationDisplayContext {

	public EditRolePermissionsNavigationDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse,
		Role role, Boolean accountRoleGroupScope) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
		_role = role;
		_accountRoleGroupScope = accountRoleGroupScope;

		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);
		_personalMenuEntryHelper =
			(PersonalMenuEntryHelper)httpServletRequest.getAttribute(
				ApplicationListWebKeys.PERSONAL_MENU_ENTRY_HELPER);
		_locale = httpServletRequest.getLocale();
		_servletContext = httpServletRequest.getServletContext();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() {
		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.convertValue(
			_getTopLevelNavigationItem(), Map.class);
	}

	private NavigationItem _getApplicationsNavigationItem() {
		Set<String> hiddenPortletIds = Collections.emptySet();

		PortletCategory portletCategory = (PortletCategory)WebAppPool.get(
			_themeDisplay.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		PortletCategory hiddenPortletCategory = portletCategory.getCategory(
			PortletCategoryConstants.NAME_HIDDEN);

		if (hiddenPortletCategory != null) {
			hiddenPortletIds = hiddenPortletCategory.getPortletIds();
		}

		List<NavigationItem> navigationItems = new ArrayList<>();

		boolean includeSystemPortlets = false;

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets(
			_themeDisplay.getCompanyId(), includeSystemPortlets, false);

		portlets = ListUtil.sort(
			portlets, new PortletTitleComparator(_servletContext, _locale));

		for (Portlet portlet : portlets) {
			String portletId = portlet.getPortletId();

			if (Validator.isNull(portletId) ||
				hiddenPortletIds.contains(portletId)) {

				continue;
			}

			navigationItems.add(
				NavigationItem.create(
					PortalUtil.getPortletLongTitle(
						portlet, _servletContext, _locale),
					_getPortletResourceNavigationItemConsumer(portletId)));
		}

		return NavigationItem.create(
			LanguageUtil.get(_locale, "applications"),
			navigationItem -> navigationItem.addNavigationItems(
				navigationItems));
	}

	private String _getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		return _backURL;
	}

	private String _getEditPermissionsResourceURL(String portletResource) {
		return ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setMVCPath(
			"/view_resources.jsp"
		).setCMD(
			Constants.EDIT
		).setBackURL(
			_getBackURL()
		).setPortletResource(
			portletResource
		).setTabs2(
			"roles"
		).setParameter(
			"accountRoleGroupScope", _accountRoleGroupScope
		).setParameter(
			"roleId", _role.getRoleId()
		).setParameter(
			"p_p_isolated", "true"
		).buildString();
	}

	private List<NavigationItem> _getMarketplaceNavigationItems() {
		return TransformUtil.transform(
			_panelAppRegistry.getPanelApps(PanelCategoryKeys.MARKETPLACE),
			panelApp -> {
				Portlet panelAppPortlet =
					PortletLocalServiceUtil.getPortletById(
						_themeDisplay.getCompanyId(), panelApp.getPortletId());

				return NavigationItem.create(
					PortalUtil.getPortletLongTitle(
						panelAppPortlet, _servletContext, _locale),
					_getPortletResourceNavigationItemConsumer(
						panelAppPortlet.getPortletId()));
			});
	}

	private List<NavigationItem> _getObjectsNavigationItems() {
		List<NavigationItem> navigationItems = new ArrayList<>();

		for (ObjectDefinition objectDefinition :
				ObjectDefinitionLocalServiceUtil.getObjectDefinitions(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (objectDefinition.isApproved() &&
				!objectDefinition.isRootDescendantNode() &&
				!objectDefinition.isUnmodifiableSystemObject() &&
				Validator.isNull(objectDefinition.getPanelCategoryKey())) {

				NavigationItem navigationItem = NavigationItem.create(
					objectDefinition.getLabel(_locale),
					_getPortletResourceNavigationItemConsumer(
						objectDefinition.getPortletId()));

				navigationItem.setId(
					"object_" + objectDefinition.getObjectDefinitionId());

				navigationItems.add(navigationItem);
			}
		}

		return navigationItems;
	}

	private NavigationItem _getPanelCategoryNavigationItem(
		PanelCategory panelCategory, String[] excludedPanelAppKeys) {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			panelCategory);

		if (panelApps.isEmpty()) {
			return null;
		}

		List<NavigationItem> navigationItems = new ArrayList<>();

		for (PanelApp panelApp : panelApps) {
			Portlet panelAppPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), panelApp.getPortletId());

			String controlPanelEntryClassName =
				panelAppPortlet.getControlPanelEntryClass();
			ControlPanelEntry controlPanelEntry =
				panelAppPortlet.getControlPanelEntryInstance();

			if (Objects.equals(
					controlPanelEntryClassName,
					AdministratorControlPanelEntry.class.getName()) ||
				Objects.equals(
					controlPanelEntryClassName,
					OmniadminControlPanelEntry.class.getName()) ||
				AdministratorControlPanelEntry.class.isAssignableFrom(
					controlPanelEntry.getClass()) ||
				OmniadminControlPanelEntry.class.isAssignableFrom(
					controlPanelEntry.getClass()) ||
				ArrayUtil.contains(
					excludedPanelAppKeys, panelApp.getPortletId())) {

				continue;
			}

			navigationItems.add(
				NavigationItem.create(
					PortalUtil.getPortletLongTitle(
						panelAppPortlet, _servletContext, _locale),
					_getPortletResourceNavigationItemConsumer(
						panelAppPortlet.getPortletId())));
		}

		return NavigationItem.create(
			panelCategory.getLabel(_locale),
			navigationItem -> navigationItem.addNavigationItems(
				navigationItems));
	}

	private List<NavigationItem> _getPanelCategoryNavigationItems(
		String panelCategoryKey) {

		List<NavigationItem> navigationItems = new ArrayList<>();

		for (PanelCategory panelCategory :
				PanelCategoryRegistryUtil.getChildPanelCategories(
					panelCategoryKey)) {

			NavigationItem panelCategoryNavigationItem =
				_getPanelCategoryNavigationItem(panelCategory, new String[0]);

			if (panelCategoryNavigationItem != null) {
				navigationItems.add(panelCategoryNavigationItem);
			}
		}

		return navigationItems;
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	private Consumer<NavigationItem> _getPortletResourceNavigationItemConsumer(
		String portletResource) {

		return navigationItem -> {
			navigationItem.put(
				"resourceURL", _getEditPermissionsResourceURL(portletResource));
			navigationItem.setActive(_portletResource.equals(portletResource));
		};
	}

	private List<NavigationItem>
		_getSiteAdministrationPanelCategoryNavigationItems() {

		List<NavigationItem> navigationItems = new ArrayList<>();

		for (PanelCategory panelCategory :
				PanelCategoryRegistryUtil.getChildPanelCategories(
					PanelCategoryKeys.SITE_ADMINISTRATION)) {

			NavigationItem navigationItem =
				_getUnfilteredPanelCategoryNavigationItem(panelCategory);

			if (navigationItem != null) {
				navigationItems.add(navigationItem);
			}
		}

		return navigationItems;
	}

	private NavigationItem _getSummaryNavigationItem() {
		return NavigationItem.create(
			LanguageUtil.get(_locale, "summary"),
			navigationItem -> {
				navigationItem.put("className", "mb-4");
				navigationItem.put("ignoreFilter", true);
				navigationItem.put(
					"resourceURL",
					ResourceURLBuilder.createResourceURL(
						_renderResponse
					).setMVCPath(
						"/view_resources.jsp"
					).setCMD(
						Constants.VIEW
					).setBackURL(
						_getBackURL()
					).setTabs1(
						"roles"
					).setParameter(
						"accountRoleGroupScope", _accountRoleGroupScope
					).setParameter(
						"roleId", _role.getRoleId()
					).setParameter(
						"p_p_isolated", "true"
					).buildString());
				navigationItem.setActive(
					Validator.isNull(_getPortletResource()));
			});
	}

	private NavigationItem _getTopLevelNavigationItem() {
		NavigationItem topLevelNavigationItem = new NavigationItem(null);

		topLevelNavigationItem.addNavigationItems(_getSummaryNavigationItem());

		int roleType = _role.getType();

		if (roleType == RoleConstants.TYPE_ORGANIZATION) {
			topLevelNavigationItem.addNavigationItems(
				_getUsersAndOrganizationsNavigationItem());
		}
		else if (roleType == RoleConstants.TYPE_REGULAR) {
			topLevelNavigationItem.addNavigationItems(
				NavigationItem.create(
					LanguageUtil.get(_locale, "control-panel"),
					navigationItem -> {
						navigationItem.addNavigationItems(
							NavigationItem.create(
								LanguageUtil.get(
									_locale, "general-permissions"),
								_getPortletResourceNavigationItemConsumer(
									PortletKeys.PORTAL)));

						navigationItem.addNavigationItems(
							_getPanelCategoryNavigationItems(
								PanelCategoryKeys.CONTROL_PANEL));

						navigationItem.setInitialExpanded(true);
					}));

			topLevelNavigationItem.addNavigationItems(
				NavigationItem.create(
					LanguageUtil.get(_locale, "commerce"),
					navigationItem -> {
						navigationItem.addNavigationItems(
							_getPanelCategoryNavigationItems(
								PanelCategoryKeys.COMMERCE));
						navigationItem.setInitialExpanded(true);
					}));

			topLevelNavigationItem.addNavigationItems(
				NavigationItem.create(
					LanguageUtil.get(_locale, "marketplace"),
					navigationItem -> {
						navigationItem.addNavigationItems(
							_getMarketplaceNavigationItems());
						navigationItem.setInitialExpanded(true);
					}));

			topLevelNavigationItem.addNavigationItems(
				NavigationItem.create(
					LanguageUtil.get(_locale, "applications-menu"),
					navigationItem -> {
						navigationItem.addNavigationItems(
							_getPanelCategoryNavigationItems(
								PanelCategoryKeys.
									APPLICATIONS_MENU_APPLICATIONS));
						navigationItem.setInitialExpanded(true);
					}));

			List<NavigationItem> navigationItems = _getObjectsNavigationItems();

			if (!navigationItems.isEmpty()) {
				topLevelNavigationItem.addNavigationItems(
					NavigationItem.create(
						LanguageUtil.get(_locale, "objects"),
						navigationItem -> {
							navigationItem.addNavigationItems(navigationItems);
							navigationItem.setInitialExpanded(true);
						}));
			}
		}

		if (!_accountRoleGroupScope) {
			String[] excludedPanelAppKeys =
				(String[])_httpServletRequest.getAttribute(
					RolesAdminWebKeys.EXCLUDED_PANEL_APP_KEYS);

			for (String panelCategoryKey :
					(String[])_httpServletRequest.getAttribute(
						RolesAdminWebKeys.PANEL_CATEGORY_KEYS)) {

				NavigationItem panelCategoryNavigationItem =
					_getPanelCategoryNavigationItem(
						PanelCategoryRegistryUtil.getPanelCategory(
							panelCategoryKey),
						excludedPanelAppKeys);

				if (panelCategoryNavigationItem != null) {
					topLevelNavigationItem.addNavigationItems(
						panelCategoryNavigationItem);
				}
			}
		}

		topLevelNavigationItem.addNavigationItems(
			NavigationItem.create(
				LanguageUtil.get(
					_locale, "site-and-asset-library-administration"),
				navigationItem -> {
					navigationItem.addNavigationItems(
						_getSiteAdministrationPanelCategoryNavigationItems());
					navigationItem.addNavigationItems(
						_getApplicationsNavigationItem());
				}));

		if (roleType == RoleConstants.TYPE_REGULAR) {
			topLevelNavigationItem.addNavigationItems(
				NavigationItem.create(
					LanguageUtil.get(_locale, "user"),
					navigationItem -> navigationItem.addNavigationItems(
						_getUserNavigationItems())));

			List<PanelCategory> panelCategories = new ArrayList<>();

			panelCategories.addAll(
				PanelCategoryRegistryUtil.getChildPanelCategories(
					PanelCategoryKeys.APPLICATIONS_MENU));
			panelCategories.addAll(
				PanelCategoryRegistryUtil.getChildPanelCategories(
					PanelCategoryKeys.ROOT));

			for (PanelCategory panelCategory : panelCategories) {
				if (ListUtil.isNotEmpty(
						_panelAppRegistry.getPanelApps(panelCategory))) {

					NavigationItem panelCategoryNavigationItem =
						_getUnfilteredPanelCategoryNavigationItem(
							panelCategory);

					if (panelCategoryNavigationItem != null) {
						topLevelNavigationItem.addNavigationItems(
							panelCategoryNavigationItem);
					}
				}
			}
		}

		return topLevelNavigationItem;
	}

	private NavigationItem _getUnfilteredPanelCategoryNavigationItem(
		PanelCategory panelCategory) {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			panelCategory);

		if (panelApps.isEmpty()) {
			return null;
		}

		return NavigationItem.create(
			panelCategory.getLabel(_locale),
			navigationItem -> {
				for (PanelApp panelApp : panelApps) {
					Portlet panelAppPortlet =
						PortletLocalServiceUtil.getPortletById(
							_themeDisplay.getCompanyId(),
							panelApp.getPortletId());

					navigationItem.addNavigationItems(
						NavigationItem.create(
							PortalUtil.getPortletLongTitle(
								panelAppPortlet, _servletContext, _locale),
							_getPortletResourceNavigationItemConsumer(
								panelAppPortlet.getPortletId())));
				}
			});
	}

	private List<NavigationItem> _getUserNavigationItems() {
		List<NavigationItem> navigationItems = new ArrayList<>();

		for (BasePersonalMenuEntry basePersonalMenuEntry :
				_personalMenuEntryHelper.getBasePersonalMenuEntries()) {

			Portlet personalPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(),
				basePersonalMenuEntry.getPortletId());

			navigationItems.add(
				NavigationItem.create(
					PortalUtil.getPortletLongTitle(
						personalPortlet, _servletContext, _locale),
					_getPortletResourceNavigationItemConsumer(
						personalPortlet.getPortletId())));
		}

		return navigationItems;
	}

	private NavigationItem _getUsersAndOrganizationsNavigationItem() {
		Portlet usersAdminPortlet = PortletLocalServiceUtil.getPortletById(
			_themeDisplay.getCompanyId(),
			PortletProviderUtil.getPortletId(
				User.class.getName(), PortletProvider.Action.VIEW));

		return NavigationItem.create(
			PortalUtil.getPortletLongTitle(
				usersAdminPortlet, _servletContext, _locale),
			_getPortletResourceNavigationItemConsumer(
				usersAdminPortlet.getPortletId()));
	}

	private final Boolean _accountRoleGroupScope;
	private String _backURL;
	private final HttpServletRequest _httpServletRequest;
	private final Locale _locale;
	private final PanelAppRegistry _panelAppRegistry;
	private final PersonalMenuEntryHelper _personalMenuEntryHelper;
	private String _portletResource;
	private final RenderResponse _renderResponse;
	private final Role _role;
	private final ServletContext _servletContext;
	private final ThemeDisplay _themeDisplay;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private static class NavigationItem {

		public static NavigationItem create(
			String label, Consumer<NavigationItem> navigationItemConsumer) {

			NavigationItem navigationItem = new NavigationItem(label);

			navigationItemConsumer.accept(navigationItem);

			if (navigationItem.id == null) {
				navigationItem.setId(
					"NAVIGATION_ITEM" + SecureRandomUtil.nextLong());
			}

			return navigationItem;
		}

		public NavigationItem(String label) {
			this.label = label;
		}

		public void addNavigationItems(
			Collection<NavigationItem> navigationItems) {

			this.navigationItems.addAll(navigationItems);
		}

		public void addNavigationItems(NavigationItem... navigationItems) {
			addNavigationItems(Arrays.asList(navigationItems));
		}

		public void put(String key, Object value) {
			properties.put(key, value);
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setInitialExpanded(boolean initialExpanded) {
			this.initialExpanded = initialExpanded;
		}

		@JsonProperty
		protected boolean active;

		@JsonProperty
		protected String id;

		@JsonProperty
		protected boolean initialExpanded;

		@JsonProperty
		protected final String label;

		@JsonProperty("items")
		protected List<NavigationItem> navigationItems = new ArrayList<>();

		@JsonAnyGetter
		protected final Map<String, Object> properties = new HashMap<>();

	}

}