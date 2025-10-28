/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.NavigationMenu;
import com.liferay.headless.delivery.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.NavigationMenuEntityModel;
import com.liferay.headless.delivery.resource.v1_0.NavigationMenuResource;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.site.navigation.constants.SiteNavigationActionKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuService;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuItemOrderComparator;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/navigation-menu.properties",
	scope = ServiceScope.PROTOTYPE, service = NavigationMenuResource.class
)
@Deprecated
public class NavigationMenuResourceImpl extends BaseNavigationMenuResourceImpl {

	@Override
	public void deleteNavigationMenu(Long navigationMenuId) throws Exception {
		_siteNavigationMenuService.deleteSiteNavigationMenu(navigationMenuId);
	}

	@Override
	public void deleteSiteNavigationMenuByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		_siteNavigationMenuService.deleteSiteNavigationMenu(
			externalReferenceCode, siteId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public NavigationMenu getNavigationMenu(Long navigationMenuId)
		throws Exception {

		return _toNavigationMenu(
			_siteNavigationMenuService.fetchSiteNavigationMenu(
				navigationMenuId));
	}

	@Override
	public NavigationMenu getSiteNavigationMenuByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _toNavigationMenu(
			_siteNavigationMenuService.
				getSiteNavigationMenuByExternalReferenceCode(
					externalReferenceCode, siteId));
	}

	@Override
	public Page<NavigationMenu> getSiteNavigationMenusPage(
			Long siteId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU,
					"postSiteNavigationMenu",
					SiteNavigationConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU,
					"postSiteNavigationMenuBatch",
					SiteNavigationConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteNavigationMenuBatch",
					SiteNavigationConstants.RESOURCE_NAME, null)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putNavigationMenuBatch",
					SiteNavigationConstants.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
			},
			filter, SiteNavigationMenu.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});
			},
			sorts,
			document -> _toNavigationMenu(
				_siteNavigationMenuService.fetchSiteNavigationMenu(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public NavigationMenu postSiteNavigationMenu(
			Long siteId, NavigationMenu navigationMenu)
		throws Exception {

		return _addNavigationMenu(
			navigationMenu.getExternalReferenceCode(), siteId, navigationMenu);
	}

	@Override
	public NavigationMenu putNavigationMenu(
			Long navigationMenuId, NavigationMenu navigationMenu)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuService.fetchSiteNavigationMenu(
				navigationMenuId);

		return _updateNavigationMenu(navigationMenu, siteNavigationMenu);
	}

	@Override
	public NavigationMenu putSiteNavigationMenuByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			NavigationMenu navigationMenu)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					externalReferenceCode, siteId);

		if (siteNavigationMenu != null) {
			return _updateNavigationMenu(navigationMenu, siteNavigationMenu);
		}

		return _addNavigationMenu(
			externalReferenceCode, siteId, navigationMenu);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuService.fetchSiteNavigationMenu((Long)id);

		return siteNavigationMenu.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return SiteNavigationConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return SiteNavigationMenu.class.getName();
	}

	private NavigationMenu _addNavigationMenu(
			String externalReferenceCode, Long siteId,
			NavigationMenu navigationMenu)
		throws Exception {

		int type = SiteNavigationConstants.TYPE_DEFAULT;

		NavigationMenu.NavigationType navigationType =
			navigationMenu.getNavigationType();

		if (navigationType != null) {
			type = navigationType.ordinal() + 1;
		}

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuService.addSiteNavigationMenu(
				externalReferenceCode, siteId, navigationMenu.getName(), type,
				true,
				ServiceContextBuilder.create(
					siteId, contextHttpServletRequest, null
				).permissions(
					ModelPermissionsUtil.toModelPermissions(
						contextCompany.getCompanyId(),
						navigationMenu.getPermissions(),
						GetterUtil.getLong(navigationMenu.getId()),
						SiteNavigationMenu.class.getName(),
						_resourceActionLocalService,
						_resourcePermissionLocalService, _roleLocalService)
				).build());

		_createNavigationMenuItems(
			navigationMenu.getNavigationMenuItems(), 0, siteId,
			siteNavigationMenu.getSiteNavigationMenuId());

		return _toNavigationMenu(siteNavigationMenu);
	}

	private void _createNavigationMenuItem(
			NavigationMenuItem navigationMenuItem, long parentNavigationMenuId,
			long siteId, long siteNavigationMenuId)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.putAll(
			navigationMenuItem.getTypeSettings()
		).build();

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemService.addSiteNavigationMenuItem(
				null, siteId, siteNavigationMenuId, parentNavigationMenuId,
				navigationMenuItem.getType(), unicodeProperties.toString(),
				ServiceContextBuilder.create(
					siteId, contextHttpServletRequest, null
				).expandoBridgeAttributes(
					CustomFieldsUtil.toMap(
						SiteNavigationMenuItem.class.getName(),
						contextCompany.getCompanyId(),
						navigationMenuItem.getCustomFields(),
						contextAcceptLanguage.getPreferredLocale())
				).build());

		_createNavigationMenuItems(
			navigationMenuItem.getNavigationMenuItems(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId(), siteId,
			siteNavigationMenuId);
	}

	private void _createNavigationMenuItems(
			NavigationMenuItem[] navigationMenuItems,
			long parentNavigationMenuId, long siteId, long siteNavigationMenuId)
		throws Exception {

		if (navigationMenuItems == null) {
			return;
		}

		for (NavigationMenuItem navigationMenuItem : navigationMenuItems) {
			_createNavigationMenuItem(
				navigationMenuItem, parentNavigationMenuId, siteId,
				siteNavigationMenuId);
		}
	}

	private Layout _getLayout(SiteNavigationMenuItem siteNavigationMenuItem) {
		UnicodeProperties unicodeProperties = _getUnicodeProperties(
			siteNavigationMenuItem);

		String layoutUuid = unicodeProperties.get("layoutUuid");
		boolean privateLayout = GetterUtil.getBoolean(
			unicodeProperties.get("privateLayout"));

		return _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, siteNavigationMenuItem.getGroupId(), privateLayout);
	}

	private Locale _getLocaleFromProperty(Map.Entry<String, String> property) {
		return LocaleUtil.fromLanguageId(
			StringUtil.removeSubstring(property.getKey(), "name_"));
	}

	private Map<Locale, String> _getLocalizedNamesFromProperties(
		UnicodeProperties unicodeProperties) {

		if (unicodeProperties == null) {
			return new HashMap<>();
		}

		Map<Locale, String> properties = new HashMap<>();

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			if (!_isNameProperty(entry)) {
				continue;
			}

			properties.put(_getLocaleFromProperty(entry), entry.getValue());
		}

		return properties;
	}

	private String _getName(
			Layout layout, String type, UnicodeProperties unicodeProperties,
			boolean useCustomName)
		throws JSONException {

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());
		String preferredLanguageId =
			contextAcceptLanguage.getPreferredLanguageId();

		if (StringUtil.equals(type, "page")) {
			if (!useCustomName && (layout == null)) {
				return null;
			}

			if (!useCustomName && (layout != null)) {
				return layout.getName(
					contextAcceptLanguage.getPreferredLocale());
			}

			if (!useCustomName) {
				return null;
			}

			return unicodeProperties.getProperty(
				"name_" + preferredLanguageId,
				unicodeProperties.getProperty("name_" + defaultLanguageId));
		}

		if (useCustomName) {
			JSONObject customNameJSONObject = _jsonFactory.createJSONObject(
				unicodeProperties.getProperty("localizedNames"));

			return customNameJSONObject.getString(
				preferredLanguageId,
				customNameJSONObject.getString(defaultLanguageId));
		}

		if (StringUtil.equals(type, "navigationMenu") ||
			StringUtil.equals(type, "url")) {

			return unicodeProperties.getProperty(
				"name_" + preferredLanguageId,
				unicodeProperties.getProperty("name_" + defaultLanguageId));
		}

		return unicodeProperties.getProperty("title");
	}

	private Map<Long, List<SiteNavigationMenuItem>>
		_getSiteNavigationMenuItemsMap(
			List<SiteNavigationMenuItem> siteNavigationMenuItems) {

		Map<Long, List<SiteNavigationMenuItem>> siteNavigationMenuItemsMap =
			new HashMap<>();

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			long parentSiteNavigationMenuItemId =
				siteNavigationMenuItem.getParentSiteNavigationMenuItemId();

			if (siteNavigationMenuItemsMap.containsKey(
					parentSiteNavigationMenuItemId)) {

				continue;
			}

			for (SiteNavigationMenuItem childSiteNavigationMenuItem :
					siteNavigationMenuItems) {

				if (parentSiteNavigationMenuItemId !=
						childSiteNavigationMenuItem.
							getParentSiteNavigationMenuItemId()) {

					continue;
				}

				List<SiteNavigationMenuItem> parentSiteNavigationMenuItems =
					siteNavigationMenuItemsMap.getOrDefault(
						parentSiteNavigationMenuItemId, new ArrayList<>());

				parentSiteNavigationMenuItems.add(childSiteNavigationMenuItem);

				siteNavigationMenuItemsMap.put(
					parentSiteNavigationMenuItemId,
					parentSiteNavigationMenuItems);
			}
		}

		return siteNavigationMenuItemsMap;
	}

	private UnicodeProperties _getUnicodeProperties(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		if (siteNavigationMenuItem == null) {
			return null;
		}

		return UnicodePropertiesBuilder.fastLoad(
			siteNavigationMenuItem.getTypeSettings()
		).build();
	}

	private boolean _isNameProperty(Map.Entry<String, String> entry) {
		String key = entry.getKey();

		return key.startsWith("name_");
	}

	private NavigationMenu _toNavigationMenu(
		SiteNavigationMenu siteNavigationMenu) {

		return new NavigationMenu() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						addAction(
							ActionKeys.DELETE, siteNavigationMenu,
							"deleteNavigationMenu")
					).put(
						"replace",
						addAction(
							ActionKeys.UPDATE, siteNavigationMenu,
							"putNavigationMenu")
					).build());
				setCreator(
					() -> CreatorUtil.toCreator(
						new DefaultDTOConverterContext(
							null, null, null, contextUriInfo, null),
						_portal,
						_userLocalService.fetchUser(
							siteNavigationMenu.getUserId())));
				setDateCreated(siteNavigationMenu::getCreateDate);
				setDateModified(siteNavigationMenu::getModifiedDate);
				setExternalReferenceCode(
					siteNavigationMenu::getExternalReferenceCode);
				setId(siteNavigationMenu::getSiteNavigationMenuId);
				setName(siteNavigationMenu::getName);
				setNavigationMenuItems(
					() -> {
						Map<Long, List<SiteNavigationMenuItem>>
							siteNavigationMenuItemsMap =
								_getSiteNavigationMenuItemsMap(
									_siteNavigationMenuItemService.
										getSiteNavigationMenuItems(
											siteNavigationMenu.
												getSiteNavigationMenuId(),
											SiteNavigationMenuItemOrderComparator.
												getInstance(true)));

						return transformToArray(
							siteNavigationMenuItemsMap.getOrDefault(
								0L, new ArrayList<>()),
							siteNavigationMenuItem -> _toNavigationMenuItem(
								siteNavigationMenuItem,
								siteNavigationMenuItemsMap),
							NavigationMenuItem.class);
					});
				setNavigationType(
					() -> {
						if (siteNavigationMenu.getType() == 0) {
							return null;
						}

						return NavigationType.values()
							[siteNavigationMenu.getType() - 1];
					});
				setPermissions(() -> _toPermissions(siteNavigationMenu));
				setSiteId(siteNavigationMenu::getGroupId);
			}
		};
	}

	private NavigationMenuItem _toNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem,
		Map<Long, List<SiteNavigationMenuItem>> siteNavigationMenuItemsMap) {

		Layout layout = _getLayout(siteNavigationMenuItem);

		UnicodeProperties unicodeProperties = _getUnicodeProperties(
			siteNavigationMenuItem);

		final String navigationMenuItemType = _toType(
			siteNavigationMenuItem.getType());

		return new NavigationMenuItem() {
			{
				setAvailableLanguages(
					() -> {
						Map<Locale, String> localizedMap =
							_getLocalizedNamesFromProperties(unicodeProperties);

						Set<Locale> locales = localizedMap.keySet();

						return LocaleUtil.toW3cLanguageIds(
							locales.toArray(new Locale[localizedMap.size()]));
					});
				setCreator(
					() -> CreatorUtil.toCreator(
						new DefaultDTOConverterContext(
							null, null, null, contextUriInfo, null),
						_portal,
						_userLocalService.fetchUser(
							siteNavigationMenuItem.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						contextAcceptLanguage.isAcceptAllLanguages(),
						SiteNavigationMenuItem.class.getName(),
						siteNavigationMenuItem.getSiteNavigationMenuItemId(),
						siteNavigationMenuItem.getCompanyId(),
						contextAcceptLanguage.getPreferredLocale()));
				setDateCreated(siteNavigationMenuItem::getCreateDate);
				setDateModified(siteNavigationMenuItem::getModifiedDate);
				setId(siteNavigationMenuItem::getSiteNavigationMenuItemId);
				setName(
					() -> _getName(
						layout, navigationMenuItemType, unicodeProperties,
						getUseCustomName()));
				setName_i18n(
					() -> {
						if (!contextAcceptLanguage.isAcceptAllLanguages()) {
							return null;
						}

						Map<Locale, String> localizedNames =
							_getLocalizedNamesFromProperties(unicodeProperties);

						if ((!useCustomName || localizedNames.isEmpty()) &&
							(layout != null)) {

							localizedNames = layout.getNameMap();
						}

						return LocalizedMapUtil.getI18nMap(localizedNames);
					});
				setNavigationMenuItems(
					() -> transformToArray(
						siteNavigationMenuItemsMap.getOrDefault(
							siteNavigationMenuItem.
								getSiteNavigationMenuItemId(),
							new ArrayList<>()),
						item -> _toNavigationMenuItem(
							item, siteNavigationMenuItemsMap),
						NavigationMenuItem.class));
				setType(siteNavigationMenuItem::getType);
				setTypeSettings(() -> unicodeProperties);
				setUseCustomName(
					() -> Boolean.valueOf(
						unicodeProperties.getProperty("useCustomName")));
			}
		};
	}

	private Permission[] _toPermissions(SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		return NestedFieldsSupplier.supply(
			"permissions",
			nestedFieldNames -> {
				_permissionService.checkPermission(
					siteNavigationMenu.getGroupId(),
					siteNavigationMenu.getModelClassName(),
					siteNavigationMenu.getSiteNavigationMenuId());

				Collection<Permission> permissions =
					PermissionUtil.getPermissions(
						siteNavigationMenu.getCompanyId(),
						_resourceActionLocalService.getResourceActions(
							siteNavigationMenu.getModelClassName()),
						siteNavigationMenu.getSiteNavigationMenuId(),
						siteNavigationMenu.getModelClassName(), null);

				return permissions.toArray(new Permission[0]);
			});
	}

	private String _toType(String type) {
		if (type.equals("layout")) {
			return "page";
		}
		else if (type.equals("node")) {
			return "navigationMenu";
		}
		else if (type.equals(FileEntry.class.getName())) {
			return DLFileEntry.class.getName();
		}

		return type;
	}

	private NavigationMenu _updateNavigationMenu(
			NavigationMenu navigationMenu,
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		_updateNavigationMenuItems(
			navigationMenu.getNavigationMenuItems(), 0,
			siteNavigationMenu.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId());

		ServiceContext serviceContext = ServiceContextBuilder.create(
			siteNavigationMenu.getGroupId(), contextHttpServletRequest, null
		).permissions(
			ModelPermissionsUtil.toModelPermissions(
				contextCompany.getCompanyId(), navigationMenu.getPermissions(),
				GetterUtil.getLong(navigationMenu.getId()),
				SiteNavigationMenu.class.getName(), _resourceActionLocalService,
				_resourcePermissionLocalService, _roleLocalService)
		).build();

		NavigationMenu.NavigationType navigationType =
			navigationMenu.getNavigationType();

		if (navigationType != null) {
			_siteNavigationMenuService.updateSiteNavigationMenu(
				siteNavigationMenu.getSiteNavigationMenuId(),
				navigationType.ordinal() + 1, true, serviceContext);
		}

		return _toNavigationMenu(
			_siteNavigationMenuService.updateSiteNavigationMenu(
				siteNavigationMenu.getSiteNavigationMenuId(),
				navigationMenu.getName(), serviceContext));
	}

	private void _updateNavigationMenuItems(
			NavigationMenuItem[] navigationMenuItems,
			long parentSiteNavigationMenuItemId, Long siteId,
			long siteNavigationMenuId)
		throws Exception {

		List<SiteNavigationMenuItem> siteNavigationMenuItems = new ArrayList<>(
			_siteNavigationMenuItemService.getSiteNavigationMenuItems(
				siteNavigationMenuId, parentSiteNavigationMenuItemId));

		if (navigationMenuItems != null) {
			for (NavigationMenuItem navigationMenuItem : navigationMenuItems) {
				Long navigationMenuItemId = navigationMenuItem.getId();

				SiteNavigationMenuItem siteNavigationMenuItem = null;

				for (SiteNavigationMenuItem curSiteNavigationMenuItem :
						siteNavigationMenuItems) {

					if (Objects.equals(
							navigationMenuItemId,
							curSiteNavigationMenuItem.
								getSiteNavigationMenuItemId())) {

						siteNavigationMenuItem = curSiteNavigationMenuItem;

						break;
					}
				}

				if (siteNavigationMenuItem != null) {
					SiteNavigationMenuItem updatedSiteNavigationMenuItem =
						_siteNavigationMenuItemService.
							updateSiteNavigationMenuItem(
								navigationMenuItemId,
								String.valueOf(
									navigationMenuItem.getTypeSettings()),
								ServiceContextBuilder.create(
									siteId, contextHttpServletRequest, null
								).expandoBridgeAttributes(
									CustomFieldsUtil.toMap(
										SiteNavigationMenuItem.class.getName(),
										contextCompany.getCompanyId(),
										navigationMenuItem.getCustomFields(),
										contextAcceptLanguage.
											getPreferredLocale())
								).build());

					_updateNavigationMenuItems(
						navigationMenuItem.getNavigationMenuItems(),
						updatedSiteNavigationMenuItem.
							getSiteNavigationMenuItemId(),
						siteId, siteNavigationMenuId);

					siteNavigationMenuItems.remove(siteNavigationMenuItem);
				}
				else {
					_createNavigationMenuItem(
						navigationMenuItem, parentSiteNavigationMenuItemId,
						siteId, siteNavigationMenuId);
				}
			}
		}

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			_siteNavigationMenuItemService.deleteSiteNavigationMenuItem(
				siteNavigationMenuItem.getSiteNavigationMenuItemId(), true);
		}
	}

	private static final EntityModel _entityModel =
		new NavigationMenuEntityModel();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Reference
	private SiteNavigationMenuService _siteNavigationMenuService;

	@Reference
	private UserLocalService _userLocalService;

}