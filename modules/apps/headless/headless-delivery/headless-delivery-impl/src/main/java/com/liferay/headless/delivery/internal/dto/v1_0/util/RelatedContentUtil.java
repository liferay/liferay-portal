/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.RelatedContent;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.Locale;

/**
 * @author Javier Gamarra
 */
public class RelatedContentUtil {

	public static RelatedContent[] toRelatedContents(
		AssetEntryLocalService assetEntryLocalService,
		AssetLinkLocalService assetLinkLocalService,
		DTOConverterRegistry dtoConverterRegistry, String className,
		long classPK, Locale locale) {

		AssetEntry assetEntry = assetEntryLocalService.fetchEntry(
			className, classPK);

		if (assetEntry == null) {
			return null;
		}

		return TransformUtil.transformToArray(
			assetLinkLocalService.getDirectLinks(assetEntry.getEntryId()),
			assetLink -> _toRelatedContent(
				assetEntryLocalService.getEntry(assetLink.getEntryId2()),
				dtoConverterRegistry, locale),
			RelatedContent.class);
	}

	private static RelatedContent _toRelatedContent(
			AssetEntry assetEntry, DTOConverterRegistry dtoConverterRegistry,
			Locale locale)
		throws PortalException {

		if (assetEntry == null) {
			return null;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntry.getClassName());

		if (assetRendererFactory == null) {
			return null;
		}

		AssetRenderer<?> assetRenderer = null;

		try {
			assetRenderer = assetRendererFactory.getAssetRenderer(
				assetEntry.getClassPK());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return null;
		}

		if ((assetRenderer == null) ||
			!assetRenderer.hasViewPermission(
				PermissionThreadLocal.getPermissionChecker())) {

			return null;
		}

		return new RelatedContent() {
			{
				setContentType(
					() -> {
						DTOConverter<?, ?> dtoConverter =
							dtoConverterRegistry.getDTOConverter(
								assetEntry.getClassName());

						if (dtoConverter == null) {
							return assetEntry.getClassName();
						}

						return dtoConverter.getContentType();
					});
				setId(assetEntry::getClassPK);
				setTitle(() -> assetEntry.getTitle(locale));
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RelatedContentUtil.class);

}