/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenu;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.NavigationMenuEntityModel;
import com.liferay.headless.admin.site.resource.v1_0.NavigationMenuResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.constants.SiteNavigationActionKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/navigation-menu.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = NavigationMenuResource.class
)
public class NavigationMenuResourceImpl
	extends BaseNavigationMenuResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<NavigationMenu> {

	@Override
	public void create(
			Collection<NavigationMenu> navigationMenus,
			Map<String, Serializable> parameters)
		throws Exception {

		if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
			return;
		}

		super.create(navigationMenus, parameters);
	}

	@Override
	public void deleteSiteNavigationMenu(
			String siteExternalReferenceCode,
			String navigationMenuExternalReferenceCode)
		throws Exception {

		_siteNavigationMenuService.deleteSiteNavigationMenu(
			navigationMenuExternalReferenceCode,
			GroupUtil.getGroupId(
				true, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<SiteNavigationMenu>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return NavigationMenuResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "navigation-menus";
			}

			@Override
			public Class<SiteNavigationMenu> getModelClass() {
				return SiteNavigationMenu.class;
			}

			@Override
			public String getPortletId() {
				return SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_SITE_BUILDER;
			}

			@Override
			public boolean isStagingSupported() {
				return true;
			}

		};
	}

	@Override
	protected NavigationMenu doGetSiteNavigationMenu(
			String siteExternalReferenceCode,
			String navigationMenuExternalReferenceCode)
		throws Exception {

		return _toNavigationMenu(
			_siteNavigationMenuService.
				getSiteNavigationMenuByExternalReferenceCode(
					navigationMenuExternalReferenceCode,
					GroupUtil.getGroupId(
						true, true, contextCompany.getCompanyId(),
						siteExternalReferenceCode)));
	}

	@Override
	protected Page<NavigationMenu> doGetSiteNavigationMenusPage(
			String siteExternalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU,
					"postSiteNavigationMenu",
					SiteNavigationConstants.RESOURCE_NAME, groupId)
			).put(
				"createBatch",
				addAction(
					SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU,
					"postSiteNavigationMenuBatch",
					SiteNavigationConstants.RESOURCE_NAME, groupId)
			).build(),
			booleanQuery -> {
			},
			filter, SiteNavigationMenu.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _toNavigationMenu(
				_siteNavigationMenuService.fetchSiteNavigationMenu(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected NavigationMenu doPostSiteNavigationMenu(
			String siteExternalReferenceCode, NavigationMenu navigationMenu)
		throws Exception {

		return _addNavigationMenu(
			navigationMenu.getExternalReferenceCode(),
			GroupUtil.getGroupId(
				true, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			navigationMenu);
	}

	@Override
	protected NavigationMenu doPutSiteNavigationMenu(
			String siteExternalReferenceCode,
			String navigationMenuExternalReferenceCode,
			NavigationMenu navigationMenu)
		throws Exception {

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					navigationMenuExternalReferenceCode, groupId);

		if (siteNavigationMenu != null) {
			return _updateNavigationMenu(navigationMenu, siteNavigationMenu);
		}

		return _addNavigationMenu(
			navigationMenuExternalReferenceCode, groupId, navigationMenu);
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuService.
				getSiteNavigationMenuByExternalReferenceCode(
					externalReferenceCode,
					getPermissionCheckerGroupId(groupExternalReferenceCode));

		return siteNavigationMenu.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
		String groupExternalReferenceCode, String externalReferenceCode) {

		return SiteNavigationMenu.class.getName();
	}

	private NavigationMenu _addNavigationMenu(
			String externalReferenceCode, long groupId,
			NavigationMenu navigationMenu)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuService.addSiteNavigationMenu(
				externalReferenceCode, groupId, navigationMenu.getName(),
				_getNavigationMenuType(navigationMenu),
				_isAuto(navigationMenu.getAuto()),
				ServiceContextBuilder.create(
					groupId, contextHttpServletRequest, null
				).build());

		_createNavigationMenuItems(
			groupId, navigationMenu.getNavigationMenuItems(), 0,
			siteNavigationMenu.getSiteNavigationMenuId());

		return _toNavigationMenu(siteNavigationMenu);
	}

	private SiteNavigationMenuItem _createNavigationMenuItem(
			long groupId, NavigationMenuItem navigationMenuItem,
			long parentNavigationMenuId, long siteNavigationMenuId)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.fastLoad(
			_getTypeSettings(navigationMenuItem)
		).build();

		if (!_hasModel(groupId, navigationMenuItem, unicodeProperties)) {
			throw new IllegalArgumentException(
				"Unable to find navigation menu item with external reference " +
					"code " + navigationMenuItem.getExternalReferenceCode());
		}

		return _siteNavigationMenuItemService.addSiteNavigationMenuItem(
			navigationMenuItem.getExternalReferenceCode(), groupId,
			siteNavigationMenuId, parentNavigationMenuId,
			navigationMenuItem.getType(), _getTypeSettings(navigationMenuItem),
			ServiceContextBuilder.create(
				groupId, contextHttpServletRequest, null
			).expandoBridgeAttributes(
				CustomFieldsUtil.toMap(
					SiteNavigationMenuItem.class.getName(),
					contextCompany.getCompanyId(),
					navigationMenuItem.getCustomFields(),
					contextAcceptLanguage.getPreferredLocale())
			).build());
	}

	private void _createNavigationMenuItems(
			long groupId, NavigationMenuItem[] navigationMenuItems,
			long parentNavigationMenuId, long siteNavigationMenuId)
		throws Exception {

		if (navigationMenuItems == null) {
			return;
		}

		for (NavigationMenuItem navigationMenuItem : navigationMenuItems) {
			SiteNavigationMenuItem siteNavigationMenuItem =
				_createNavigationMenuItem(
					groupId, navigationMenuItem, parentNavigationMenuId,
					siteNavigationMenuId);

			_createNavigationMenuItems(
				groupId, navigationMenuItem.getNavigationMenuItems(),
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				siteNavigationMenuId);
		}
	}

	private void _deleteNavigationMenuItems(
			long siteNavigationMenuId, Set<Long> siteNavigationMenuItemIds)
		throws Exception {

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				_siteNavigationMenuItemService.getSiteNavigationMenuItems(
					siteNavigationMenuId)) {

			if (siteNavigationMenuItemIds.contains(
					siteNavigationMenuItem.getSiteNavigationMenuItemId())) {

				continue;
			}

			long parentSiteNavigationMenuItemId =
				siteNavigationMenuItem.getParentSiteNavigationMenuItemId();

			if ((parentSiteNavigationMenuItemId != 0) &&
				!siteNavigationMenuItemIds.contains(
					parentSiteNavigationMenuItemId)) {

				continue;
			}

			_siteNavigationMenuItemService.deleteSiteNavigationMenuItem(
				siteNavigationMenuItem.getSiteNavigationMenuItemId(), true);
		}
	}

	private SiteNavigationMenuItem _fetchSiteNavigationMenuItem(
		long groupId, NavigationMenuItem navigationMenuItem,
		long siteNavigationMenuId) {

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					navigationMenuItem.getExternalReferenceCode(), groupId);

		if ((siteNavigationMenuItem == null) ||
			(siteNavigationMenuItem.getSiteNavigationMenuId() !=
				siteNavigationMenuId)) {

			return null;
		}

		return siteNavigationMenuItem;
	}

	private String _getLocalizedNamesFromI18nMap(
		NavigationMenuItem navigationMenuItem) {

		JSONObject localizedNamesJSONObject = _jsonFactory.createJSONObject();

		Map<String, String> nameI18n = navigationMenuItem.getName_i18n();

		if ((nameI18n == null) || nameI18n.isEmpty()) {
			localizedNamesJSONObject.put(
				navigationMenuItem.getDefaultLanguageId(),
				navigationMenuItem.getName());

			return localizedNamesJSONObject.toString();
		}

		for (Map.Entry<String, String> entry : nameI18n.entrySet()) {
			localizedNamesJSONObject.put(
				String.valueOf(LocaleUtil.fromLanguageId(entry.getKey())),
				entry.getValue());
		}

		return localizedNamesJSONObject.toString();
	}

	private Map<String, String> _getLocalizedPropertiesMapFromI18nMap(
		NavigationMenuItem navigationMenuItem) {

		Map<String, String> localizedPropertyMap = new HashMap<>();

		Map<String, String> nameI18n = navigationMenuItem.getName_i18n();

		if ((nameI18n == null) || nameI18n.isEmpty()) {
			localizedPropertyMap.put(
				"name_" + navigationMenuItem.getDefaultLanguageId(),
				navigationMenuItem.getName());

			return localizedPropertyMap;
		}

		for (Map.Entry<String, String> entry : nameI18n.entrySet()) {
			localizedPropertyMap.put(
				"name_" + LocaleUtil.fromLanguageId(entry.getKey()),
				nameI18n.get(entry.getKey()));
		}

		return localizedPropertyMap;
	}

	private int _getNavigationMenuType(NavigationMenu navigationMenu) {
		int type = SiteNavigationConstants.TYPE_DEFAULT;

		NavigationMenu.NavigationType navigationType =
			navigationMenu.getNavigationType();

		if (navigationType != null) {
			type = navigationType.ordinal() + 1;
		}

		return type;
	}

	private String _getSettingProperty(Object settings, String key) {
		if (settings instanceof Map<?, ?> settingsMap) {
			return String.valueOf(settingsMap.get(key));
		}

		return null;
	}

	private String _getTypeSettings(NavigationMenuItem navigationMenuItem) {
		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).put(
			"defaultLanguageId", navigationMenuItem.getDefaultLanguageId()
		).put(
			"displayIcon", navigationMenuItem.getDisplayIcon()
		).put(
			"useCustomName",
			() -> {
				if (navigationMenuItem.getUseCustomName() != null) {
					return String.valueOf(
						navigationMenuItem.getUseCustomName());
				}

				return null;
			}
		).build();

		String localizedNames = _getLocalizedNamesFromI18nMap(
			navigationMenuItem);

		Map<String, String> localizedPropertyMap =
			_getLocalizedPropertiesMapFromI18nMap(navigationMenuItem);

		Object navigationMenuItemSettings =
			navigationMenuItem.getNavigationMenuItemSettings();

		String type = navigationMenuItem.getType();

		if (Objects.equals(
				type, SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY)) {

			String className = _getSettingProperty(
				navigationMenuItemSettings, "className");

			if (className != null) {
				unicodeProperties.put("className", className);
			}

			String externalReferenceCode = _getSettingProperty(
				navigationMenuItemSettings, "externalReferenceCode");

			if (externalReferenceCode != null) {
				unicodeProperties.put(
					"externalReferenceCode", externalReferenceCode);
			}

			unicodeProperties.put("localizedNames", localizedNames);

			String scopeExternalReferenceCode = _getSettingProperty(
				navigationMenuItemSettings, "scopeExternalReferenceCode");

			if (scopeExternalReferenceCode != null) {
				unicodeProperties.put(
					"scopeExternalReferenceCode", scopeExternalReferenceCode);
			}

			String showAssetVocabularyLevel = _getSettingProperty(
				navigationMenuItemSettings, "showAssetVocabularyLevel");

			if (showAssetVocabularyLevel != null) {
				unicodeProperties.put(
					"showAssetVocabularyLevel", showAssetVocabularyLevel);
			}

			String title = _getSettingProperty(
				navigationMenuItemSettings, "title");

			if (title != null) {
				unicodeProperties.put("title", title);
			}

			if (_getSettingProperty(navigationMenuItemSettings, "type") !=
					null) {

				unicodeProperties.put(
					"type",
					_getSettingProperty(navigationMenuItemSettings, "type"));
			}
		}
		else if (Objects.equals(
					type, SiteNavigationMenuItemTypeConstants.LAYOUT)) {

			String externalReferenceCode = _getSettingProperty(
				navigationMenuItemSettings, "externalReferenceCode");

			if (externalReferenceCode != null) {
				unicodeProperties.put(
					"externalReferenceCode", externalReferenceCode);
			}

			String privatePage = _getSettingProperty(
				navigationMenuItemSettings, "privatePage");

			if (privatePage != null) {
				unicodeProperties.put("privateLayout", privatePage);
			}

			String title = _getSettingProperty(
				navigationMenuItemSettings, "title");

			if (title != null) {
				unicodeProperties.put("title", title);
			}

			unicodeProperties.putAll(localizedPropertyMap);
		}
		else if (Objects.equals(
					type, SiteNavigationMenuItemTypeConstants.NODE)) {

			unicodeProperties.putAll(localizedPropertyMap);
		}
		else if (Objects.equals(
					type, SiteNavigationMenuItemTypeConstants.URL)) {

			if (_getSettingProperty(navigationMenuItemSettings, "url") !=
					null) {

				unicodeProperties.put(
					"url",
					_getSettingProperty(navigationMenuItemSettings, "url"));
			}

			if (_getSettingProperty(navigationMenuItemSettings, "useNewTab") !=
					null) {

				unicodeProperties.put(
					"useNewTab",
					String.valueOf(
						_getSettingProperty(
							navigationMenuItemSettings, "useNewTab")));
			}

			unicodeProperties.putAll(localizedPropertyMap);
		}
		else {
			String className = _getSettingProperty(
				navigationMenuItemSettings, "className");

			if (className != null) {
				unicodeProperties.put("className", className);
			}

			String externalReferenceCode = _getSettingProperty(
				navigationMenuItemSettings, "externalReferenceCode");

			if (externalReferenceCode != null) {
				unicodeProperties.put(
					"externalReferenceCode", externalReferenceCode);
			}

			unicodeProperties.put("localizedNames", localizedNames);

			String scopeExternalReferenceCode = _getSettingProperty(
				navigationMenuItemSettings, "scopeExternalReferenceCode");

			if (scopeExternalReferenceCode != null) {
				unicodeProperties.put(
					"scopeExternalReferenceCode", scopeExternalReferenceCode);
			}

			if (_getSettingProperty(navigationMenuItemSettings, "title") !=
					null) {

				unicodeProperties.put(
					"title",
					_getSettingProperty(navigationMenuItemSettings, "title"));
			}

			if (_getSettingProperty(navigationMenuItemSettings, "type") !=
					null) {

				unicodeProperties.put(
					"type",
					_getSettingProperty(navigationMenuItemSettings, "type"));
			}
		}

		return unicodeProperties.toString();
	}

	private boolean _hasModel(
			long groupId, NavigationMenuItem navigationMenuItem,
			UnicodeProperties unicodeProperties)
		throws Exception {

		if (BatchEngineThreadLocal.isBatchImportInProcess()) {
			return true;
		}

		String navigationMenuItemType = navigationMenuItem.getType();

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				navigationMenuItemType);

		return siteNavigationMenuItemType.hasModel(
			contextCompany.getCompanyId(), groupId, unicodeProperties);
	}

	private boolean _isAuto(Boolean auto) {
		if (auto == null) {
			return true;
		}

		return auto;
	}

	private NavigationMenu _toNavigationMenu(
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		return _navigationMenuDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, siteNavigationMenu,
						"deleteSiteNavigationMenu")
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, siteNavigationMenu,
						"putSiteNavigationMenu")
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				siteNavigationMenu.getSiteNavigationMenuId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			siteNavigationMenu);
	}

	private NavigationMenu _updateNavigationMenu(
			NavigationMenu navigationMenu,
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		Set<Long> siteNavigationMenuItemIds = new HashSet<>();

		_updateNavigationMenuItems(
			siteNavigationMenu.getGroupId(),
			navigationMenu.getNavigationMenuItems(), 0,
			siteNavigationMenu.getSiteNavigationMenuId(),
			siteNavigationMenuItemIds);

		_deleteNavigationMenuItems(
			siteNavigationMenu.getSiteNavigationMenuId(),
			siteNavigationMenuItemIds);

		ServiceContext serviceContext = ServiceContextBuilder.create(
			siteNavigationMenu.getGroupId(), contextHttpServletRequest, null
		).build();

		_siteNavigationMenuService.updateSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId(),
			_getNavigationMenuType(navigationMenu),
			_isAuto(navigationMenu.getAuto()), serviceContext);

		return _toNavigationMenu(
			_siteNavigationMenuService.updateSiteNavigationMenu(
				siteNavigationMenu.getSiteNavigationMenuId(),
				navigationMenu.getName(), serviceContext));
	}

	private void _updateNavigationMenuItems(
			long groupId, NavigationMenuItem[] navigationMenuItems,
			long parentSiteNavigationMenuItemId, long siteNavigationMenuId,
			Set<Long> siteNavigationMenuItemIds)
		throws Exception {

		if (navigationMenuItems == null) {
			return;
		}

		for (int i = 0; i < navigationMenuItems.length; i++) {
			NavigationMenuItem navigationMenuItem = navigationMenuItems[i];

			SiteNavigationMenuItem siteNavigationMenuItem =
				_fetchSiteNavigationMenuItem(
					groupId, navigationMenuItem, siteNavigationMenuId);

			if (siteNavigationMenuItem != null) {
				UnicodeProperties unicodeProperties =
					UnicodePropertiesBuilder.fastLoad(
						_getTypeSettings(navigationMenuItem)
					).build();

				if (!_hasModel(
						groupId, navigationMenuItem, unicodeProperties)) {

					throw new IllegalArgumentException(
						"Unable to find navigation menu item with external " +
							"reference code " +
								navigationMenuItem.getExternalReferenceCode());
				}

				siteNavigationMenuItem =
					_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
						siteNavigationMenuItem.getSiteNavigationMenuItemId(),
						_getTypeSettings(navigationMenuItem),
						ServiceContextBuilder.create(
							groupId, contextHttpServletRequest, null
						).expandoBridgeAttributes(
							CustomFieldsUtil.toMap(
								SiteNavigationMenuItem.class.getName(),
								contextCompany.getCompanyId(),
								navigationMenuItem.getCustomFields(),
								contextAcceptLanguage.getPreferredLocale())
						).build());
			}
			else {
				siteNavigationMenuItem = _createNavigationMenuItem(
					groupId, navigationMenuItem, parentSiteNavigationMenuItemId,
					siteNavigationMenuId);
			}

			if ((siteNavigationMenuItem.getParentSiteNavigationMenuItemId() !=
					parentSiteNavigationMenuItemId) ||
				(siteNavigationMenuItem.getOrder() != i)) {

				_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
					siteNavigationMenuItem.getSiteNavigationMenuItemId(),
					parentSiteNavigationMenuItemId, i);
			}

			siteNavigationMenuItemIds.add(
				siteNavigationMenuItem.getSiteNavigationMenuItemId());

			_updateNavigationMenuItems(
				groupId, navigationMenuItem.getNavigationMenuItems(),
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				siteNavigationMenuId, siteNavigationMenuItemIds);
		}
	}

	private static final EntityModel _entityModel =
		new NavigationMenuEntityModel();

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.NavigationMenuDTOConverter)"
	)
	private DTOConverter<SiteNavigationMenu, NavigationMenu>
		_navigationMenuDTOConverter;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Reference
	private SiteNavigationMenuService _siteNavigationMenuService;

}