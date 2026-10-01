/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.delivery.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.delivery.dto.v1_0.util.ContentDocumentUtil;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
public class OpenGraphSettingsUtil {

	public static OpenGraphSettings getOpenGraphSettings(
		DLAppService dlAppService, DLURLHelper dlURLHelper,
		DTOConverterContext dtoConverterContext,
		LayoutSEOEntryLocalService layoutSEOEntryLocalService, Layout layout) {

		LayoutSEOEntry layoutSEOEntry =
			layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		if (layoutSEOEntry == null) {
			return null;
		}

		return new OpenGraphSettings() {
			{
				setDescription(
					() -> layoutSEOEntry.getOpenGraphDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						layoutSEOEntry.getOpenGraphDescriptionMap()));
				setImage(
					() -> {
						if (Validator.isNull(
								layoutSEOEntry.
									getOpenGraphImageFileEntryERC())) {

							return null;
						}

						return ContentDocumentUtil.toContentDocument(
							dlURLHelper,
							"openGraphSettings.contentFieldValue.image",
							dlAppService.getFileEntryByExternalReferenceCode(
								layoutSEOEntry.getOpenGraphImageFileEntryERC(),
								layoutSEOEntry.
									getOpenGraphImageFileEntryGroupId()),
							dtoConverterContext.getUriInfo());
					});
				setImageAlt(
					() -> layoutSEOEntry.getOpenGraphImageAlt(
						dtoConverterContext.getLocale()));
				setImageAlt_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						layoutSEOEntry.getOpenGraphImageAltMap()));
				setTitle(
					() -> layoutSEOEntry.getOpenGraphTitle(
						dtoConverterContext.getLocale()));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						layoutSEOEntry.getOpenGraphTitleMap()));
			}
		};
	}

}