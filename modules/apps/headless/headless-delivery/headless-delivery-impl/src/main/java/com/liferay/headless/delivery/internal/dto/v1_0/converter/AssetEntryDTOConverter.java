/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.headless.delivery.dto.v1_0.AssetEntry;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(
	property = "dto.class.name=com.liferay.asset.kernel.model.AssetEntry",
	service = DTOConverter.class
)
public class AssetEntryDTOConverter
	implements DTOConverter
		<com.liferay.asset.kernel.model.AssetEntry, AssetEntry> {

	@Override
	public String getContentType() {
		return AssetEntry.class.getSimpleName();
	}

	@Override
	public AssetEntry toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.asset.kernel.model.AssetEntry serviceBuilderAssetEntry) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				serviceBuilderAssetEntry.getClassName());
		Group group = _groupLocalService.fetchGroup(
			serviceBuilderAssetEntry.getGroupId());
		Locale locale = dtoConverterContext.getLocale();

		return new AssetEntry() {
			{
				setAssetEntryId(serviceBuilderAssetEntry::getEntryId);
				setAssetType(
					() -> {
						if (assetRendererFactory == null) {
							return null;
						}

						if (!assetRendererFactory.isSupportsClassTypes()) {
							return assetRendererFactory.getTypeName(
								locale,
								serviceBuilderAssetEntry.getClassTypeId());
						}

						ClassTypeReader classTypeReader =
							assetRendererFactory.getClassTypeReader();

						ClassType classType = classTypeReader.getClassType(
							serviceBuilderAssetEntry.getClassTypeId(), locale);

						return classType.getName();
					});
				setClassName(serviceBuilderAssetEntry::getClassName);
				setClassNameId(serviceBuilderAssetEntry::getClassNameId);
				setClassPK(serviceBuilderAssetEntry::getClassPK);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(
							serviceBuilderAssetEntry.getUserId())));
				setDateModified(serviceBuilderAssetEntry::getModifiedDate);
				setDescription(
					() -> serviceBuilderAssetEntry.getDescription(locale));
				setGroupDescriptiveName(
					() -> {
						if (group == null) {
							return null;
						}

						return group.getDescriptiveName(locale);
					});
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> {
							Collection<Permission> permissions =
								PermissionUtil.getPermissions(
									serviceBuilderAssetEntry.getCompanyId(),
									_resourceActionLocalService.
										getResourceActions(
											serviceBuilderAssetEntry.
												getClassName()),
									serviceBuilderAssetEntry.getClassPK(),
									serviceBuilderAssetEntry.getClassName(),
									new String[] {RoleConstants.GUEST});

							return permissions.toArray(new Permission[0]);
						}));
				setStatus(
					() -> {
						AssetRenderer<?> assetRenderer =
							serviceBuilderAssetEntry.getAssetRenderer();

						if (assetRenderer == null) {
							return null;
						}

						return assetRenderer.getStatus();
					});
				setTitle(() -> serviceBuilderAssetEntry.getTitle(locale));
			}
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}