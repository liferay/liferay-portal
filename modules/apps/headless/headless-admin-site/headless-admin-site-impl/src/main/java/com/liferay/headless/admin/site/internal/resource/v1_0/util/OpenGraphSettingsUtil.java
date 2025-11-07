/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Locale;
import java.util.Map;

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
			return new OpenGraphSettings();
		}

		return new OpenGraphSettings() {
			{
				setDescription_i18n(
					() -> {
						Map<Locale, String> map =
							layoutSEOEntry.getOpenGraphDescriptionMap();

						if (MapUtil.isEmpty(map)) {
							return null;
						}

						return LocalizedMapUtil.getI18nMap(map);
					});
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
									() -> ItemScopeUtil.getItemScope(
										fileEntry.getGroupId(),
										layout.getGroupId()));
							}
						};
					});
				setImageAlt_i18n(
					() -> {
						Map<Locale, String> map =
							layoutSEOEntry.getOpenGraphImageAltMap();

						if (MapUtil.isEmpty(map)) {
							return null;
						}

						return LocalizedMapUtil.getI18nMap(map);
					});
				setTitle_i18n(
					() -> {
						Map<Locale, String> map =
							layoutSEOEntry.getOpenGraphTitleMap();

						if (MapUtil.isEmpty(map)) {
							return null;
						}

						return LocalizedMapUtil.getI18nMap(map);
					});
			}
		};
	}

}