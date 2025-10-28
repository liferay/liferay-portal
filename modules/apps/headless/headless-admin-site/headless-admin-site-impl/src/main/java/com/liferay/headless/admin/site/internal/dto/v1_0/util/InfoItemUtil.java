/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Lourdes Fernández Besada
 */
public class InfoItemUtil {

	public static ClassPKInfoItemIdentifier getClassPKInfoItemIdentifier(
		String className, String externalReferenceCode,
		InfoItemServiceRegistry infoItemServiceRegistry, Scope scope,
		long scopeGroupId) {

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		if ((infoItemObjectProvider == null) ||
			(infoItemDetailsProvider == null)) {

			return null;
		}

		try {
			Object infoItem = infoItemObjectProvider.getInfoItem(
				scopeGroupId,
				new ERCInfoItemIdentifier(
					externalReferenceCode,
					ItemScopeUtil.getItemScopeExternalReferenceCode(
						scope, scopeGroupId)));

			InfoItemDetails infoItemDetails =
				infoItemDetailsProvider.getInfoItemDetails(
					scopeGroupId, ClassPKInfoItemIdentifier.class, infoItem);

			if (infoItemDetails == null) {
				return null;
			}

			InfoItemReference infoItemReference =
				infoItemDetails.getInfoItemReference();

			if (infoItemReference == null) {
				return null;
			}

			return (ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			LogUtil.logOptionalReference(
				className, externalReferenceCode, scope, scopeGroupId);
		}

		return null;
	}

	public static ERCInfoItemIdentifier getERCInfoItemIdentifier(
		String className, long classPK,
		InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId) {

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className,
				ERCInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		if ((infoItemObjectProvider == null) ||
			(infoItemDetailsProvider == null)) {

			return null;
		}

		try {
			Object infoItem = infoItemObjectProvider.getInfoItem(
				scopeGroupId, new ClassPKInfoItemIdentifier(classPK));

			InfoItemDetails infoItemDetails =
				infoItemDetailsProvider.getInfoItemDetails(
					scopeGroupId, ERCInfoItemIdentifier.class, infoItem);

			if (infoItemDetails == null) {
				return null;
			}

			InfoItemReference infoItemReference =
				infoItemDetails.getInfoItemReference();

			if (infoItemReference == null) {
				return null;
			}

			return (ERCInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return null;
	}

	public static JSONObject getMappedItemJSONObject(
		String className, String externalReferenceCode, String fieldKey,
		InfoItemServiceRegistry infoItemServiceRegistry, Scope scope,
		long scopeGroupId) {

		return JSONUtil.put(
			"className", className
		).put(
			"classNameId", PortalUtil.getClassNameId(className)
		).put(
			"classPK",
			() -> {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					getClassPKInfoItemIdentifier(
						className, externalReferenceCode,
						infoItemServiceRegistry, scope, scopeGroupId);

				if (classPKInfoItemIdentifier == null) {
					return null;
				}

				return classPKInfoItemIdentifier.getClassPK();
			}
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"fieldId", fieldKey
		).put(
			"scopeExternalReferenceCode",
			() -> ItemScopeUtil.getItemScopeExternalReferenceCode(
				scope, scopeGroupId)
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(InfoItemUtil.class);

}