/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ScopeUtil;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
public class OpenGraphSettingsUtil {

	public static OpenGraphSettings getOpenGraphSettings(
		DLAppService dlAppService,
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
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						layoutSEOEntry.getOpenGraphDescriptionMap()));
				setImage(
					() -> {
						long openGraphImageFileEntryId =
							layoutSEOEntry.getOpenGraphImageFileEntryId();

						if (openGraphImageFileEntryId == 0) {
							return null;
						}

						FileEntry fileEntry = dlAppService.getFileEntry(
							openGraphImageFileEntryId);

						return new ItemExternalReference() {
							{
								setClassName(FileEntry.class::getName);
								setExternalReferenceCode(
									fileEntry::getExternalReferenceCode);
								setScope(
									() -> ScopeUtil.getScope(
										layout.getGroupId(),
										fileEntry.getGroupId()));
							}
						};
					});
				setImageAlt_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						layoutSEOEntry.getOpenGraphImageAltMap()));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						layoutSEOEntry.getOpenGraphTitleMap()));
			}
		};
	}

}