/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.delivery.dto.v1_0.CustomMetaTag;
import com.liferay.headless.delivery.dto.v1_0.PageSettings;
import com.liferay.headless.delivery.dto.v1_0.SitePageNavigationMenuSettings;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
public class PageSettingsUtil {

	public static PageSettings getPageSettings(
		DLAppService dlAppService, DLURLHelper dlURLHelper,
		DTOConverterContext dtoConverterContext,
		LayoutSEOEntryLocalService layoutSEOEntryLocalService, Layout layout) {

		return new PageSettings() {
			{
				setCustomMetaTags(
					() -> _getCustomMetaTags(
						layout, layoutSEOEntryLocalService));
				setHiddenFromNavigation(layout::isHidden);
				setOpenGraphSettings(
					() -> OpenGraphSettingsUtil.getOpenGraphSettings(
						dlAppService, dlURLHelper, dtoConverterContext,
						layoutSEOEntryLocalService, layout));
				setSeoSettings(
					() -> SEOSettingsUtil.getSeoSettings(
						dtoConverterContext, layoutSEOEntryLocalService,
						layout));
				setSitePageNavigationMenuSettings(
					() -> _toSitePageNavigationMenuSettings(
						layout.getTypeSettingsProperties()));
			}
		};
	}

	private static CustomMetaTag[] _getCustomMetaTags(
		Layout layout, LayoutSEOEntryLocalService layoutSEOEntryLocalService) {

		LayoutSEOEntry layoutSEOEntry =
			layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		if (layoutSEOEntry == null) {
			return null;
		}

		List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags =
			layoutSEOEntryLocalService.getLayoutSEOEntryCustomMetaTags(
				layoutSEOEntry.getGroupId(),
				layoutSEOEntry.getLayoutSEOEntryId());

		List<CustomMetaTag> customMetaTags = new ArrayList<>();

		for (LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag :
				layoutSEOEntryCustomMetaTags) {

			customMetaTags.add(
				new CustomMetaTag() {
					{
						setKey(layoutSEOEntryCustomMetaTag::getProperty);
						setValue(
							() -> layoutSEOEntryCustomMetaTag.getContent(
								LocaleUtil.ROOT));
						setValue_i18n(
							() -> LocalizedMapUtil.getI18nMap(
								layoutSEOEntryCustomMetaTag.getContentMap()));
					}
				});
		}

		return customMetaTags.toArray(new CustomMetaTag[0]);
	}

	private static SitePageNavigationMenuSettings
		_toSitePageNavigationMenuSettings(UnicodeProperties unicodeProperties) {

		String queryStringPropertyValue = unicodeProperties.getProperty(
			LayoutTypePortletConstants.QUERY_STRING);
		String targetPropertyValue = unicodeProperties.getProperty(
			LayoutTypePortletConstants.TARGET);
		String targetTypePropertyValue = unicodeProperties.getProperty(
			"targetType");

		if ((queryStringPropertyValue == null) &&
			(targetPropertyValue == null) &&
			(targetTypePropertyValue == null)) {

			return null;
		}

		return new SitePageNavigationMenuSettings() {
			{
				setQueryString(() -> queryStringPropertyValue);
				setTarget(() -> targetPropertyValue);
				setTargetType(
					() -> {
						if (targetTypePropertyValue == null) {
							return null;
						}

						if (Objects.equals(
								targetTypePropertyValue, "useNewTab")) {

							return TargetType.NEW_TAB;
						}

						return TargetType.SPECIFIC_FRAME;
					});
			}
		};
	}

}