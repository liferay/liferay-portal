/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.NavigationMenu;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuItemOrderComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = DTOConverter.class)
public class NavigationMenuDTOConverter
	implements DTOConverter<SiteNavigationMenu, NavigationMenu> {

	@Override
	public String getContentType() {
		return NavigationMenu.class.getSimpleName();
	}

	@Override
	public NavigationMenu toDTO(
			DTOConverterContext dtoConverterContext,
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		return new NavigationMenu() {
			{
				setActions(dtoConverterContext::getActions);
				setAuto(siteNavigationMenu::getAuto);
				setCreator(
					() -> CreatorUtil.toCreator(
						siteNavigationMenu.getUserId(),
						siteNavigationMenu.getUserName()));
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

						return TransformUtil.transformToArray(
							siteNavigationMenuItemsMap.getOrDefault(
								0L, new ArrayList<>()),
							siteNavigationMenuItem -> {
								DefaultDTOConverterContext
									defaultDTOConverterContext =
										new DefaultDTOConverterContext(
											dtoConverterContext.
												isAcceptAllLanguages(),
											null, _dtoConverterRegistry,
											siteNavigationMenuItem.
												getSiteNavigationMenuItemId(),
											dtoConverterContext.getLocale(),
											dtoConverterContext.getUriInfo(),
											dtoConverterContext.getUser());

								defaultDTOConverterContext.setAttribute(
									"siteNavigationMenuItemsMap",
									siteNavigationMenuItemsMap);

								return _navigationMenuItemDTOConverter.toDTO(
									defaultDTOConverterContext,
									siteNavigationMenuItem);
							},
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
				setSiteExternalReferenceCode(
					() -> {
						Group group = _groupLocalService.fetchGroup(
							siteNavigationMenu.getGroupId());

						if (group != null) {
							return group.getExternalReferenceCode();
						}

						return StringPool.BLANK;
					});
			}
		};
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

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.NavigationMenuItemDTOConverter)"
	)
	private DTOConverter<SiteNavigationMenuItem, NavigationMenuItem>
		_navigationMenuItemDTOConverter;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

}