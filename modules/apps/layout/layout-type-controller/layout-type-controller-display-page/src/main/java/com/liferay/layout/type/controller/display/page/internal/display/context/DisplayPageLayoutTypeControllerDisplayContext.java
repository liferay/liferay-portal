/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.display.page.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Jürgen Kappler
 */
public class DisplayPageLayoutTypeControllerDisplayContext {

	public DisplayPageLayoutTypeControllerDisplayContext(
			HttpServletRequest httpServletRequest,
			InfoItemServiceRegistry infoItemServiceRegistry,
			InfoSearchClassMapperRegistry infoSearchClassMapperRegistry)
		throws Exception {

		_infoItemServiceRegistry = infoItemServiceRegistry;

		long assetEntryId = ParamUtil.getLong(
			httpServletRequest, "assetEntryId");

		Object infoItem = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);
		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if ((assetEntryId > 0) && (infoItem == null) &&
			(infoItemDetails == null)) {

			AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
				assetEntryId);

			String className = infoSearchClassMapperRegistry.getClassName(
				assetEntry.getClassName());

			InfoItemObjectProvider<Object> infoItemObjectProvider =
				(InfoItemObjectProvider<Object>)
					infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemObjectProvider.class, className,
						ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

			InfoItemIdentifier infoItemIdentifier =
				new ClassPKInfoItemIdentifier(assetEntry.getClassPK());

			infoItemIdentifier.setVersion(InfoItemIdentifier.VERSION_LATEST);

			infoItem = infoItemObjectProvider.getInfoItem(infoItemIdentifier);

			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			if (assetRenderer != null) {
				InfoItemDetailsProvider infoItemDetailsProvider =
					infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemDetailsProvider.class, className);

				infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
					assetRenderer.getAssetObject());
			}

			httpServletRequest.setAttribute(
				InfoDisplayWebKeys.INFO_ITEM, infoItem);
			httpServletRequest.setAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY, assetEntry);

			LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
				httpServletRequest, assetEntry.getEntryId());
		}

		_infoItem = infoItem;
		_infoItemDetails = infoItemDetails;
	}

	public AssetRendererFactory<?> getAssetRendererFactory() {
		if (_infoItemDetails == null) {
			return null;
		}

		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(_infoItemDetails.getClassName());
	}

	public boolean hasInfoItem() {
		if ((_infoItem != null) && (_infoItemDetails != null)) {
			return true;
		}

		return false;
	}

	public boolean hasPermission(
			PermissionChecker permissionChecker, String actionId)
		throws Exception {

		if (_infoItemDetails == null) {
			return true;
		}

		InfoItemPermissionProvider infoItemPermissionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemPermissionProvider.class,
				_infoItemDetails.getClassName());

		if (infoItemPermissionProvider != null) {
			return infoItemPermissionProvider.hasPermission(
				permissionChecker, _infoItem, actionId);
		}

		AssetRendererFactory<?> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory != null) {
			InfoItemReference infoItemReference =
				_infoItemDetails.getInfoItemReference();

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
				return false;
			}

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			return assetRendererFactory.hasPermission(
				permissionChecker, classPKInfoItemIdentifier.getClassPK(),
				actionId);
		}

		return true;
	}

	public boolean isForbidden(HttpServletResponse httpServletResponse) {
		if (httpServletResponse.getStatus() ==
				HttpServletResponse.SC_FORBIDDEN) {

			return true;
		}

		return false;
	}

	private final Object _infoItem;
	private final InfoItemDetails _infoItemDetails;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;

}