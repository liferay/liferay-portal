/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.helper;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.util.constants.LayoutClassedModelUsageConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = LayoutClassedModelUsagesHelper.class)
public class LayoutClassedModelUsagesHelper {

	public String getName(
		LayoutClassedModelUsage layoutClassedModelUsage, Locale locale) {

		if (layoutClassedModelUsage.getType() ==
				LayoutClassedModelUsageConstants.TYPE_LAYOUT) {

			Layout layout = _layoutLocalService.fetchLayout(
				layoutClassedModelUsage.getPlid());

			if (layout == null) {
				return StringPool.BLANK;
			}

			if (!layout.isDraftLayout()) {
				return layout.getName(locale);
			}

			return StringBundler.concat(
				layout.getName(locale), " (", _language.get(locale, "draft"),
				")");
		}

		long plid = layoutClassedModelUsage.getPlid();

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return StringPool.BLANK;
		}

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry == null) {
			return StringPool.BLANK;
		}

		if (!layout.isDraftLayout()) {
			return layoutPageTemplateEntry.getName();
		}

		return StringBundler.concat(
			layoutPageTemplateEntry.getName(), " (",
			_language.get(locale, "draft"), ")");
	}

	public String getPreviewURL(
			LayoutClassedModelUsage layoutClassedModelUsage,
			HttpServletRequest httpServletRequest)
		throws Exception {

		String layoutURL = null;

		if (layoutClassedModelUsage.getContainerType() ==
				_portal.getClassNameId(FragmentEntryLink.class)) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			layoutURL = _portal.getLayoutFriendlyURL(
				_layoutLocalService.fetchLayout(
					layoutClassedModelUsage.getPlid()),
				themeDisplay);

			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewClassNameId",
				String.valueOf(layoutClassedModelUsage.getClassNameId()));
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewClassPK",
				String.valueOf(layoutClassedModelUsage.getClassPK()));
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewType",
				String.valueOf(AssetRendererFactory.TYPE_LATEST));
		}
		else {
			layoutURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest,
					layoutClassedModelUsage.getContainerKey(),
					layoutClassedModelUsage.getPlid(),
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"previewClassNameId", layoutClassedModelUsage.getClassNameId()
			).setParameter(
				"previewClassPK", layoutClassedModelUsage.getClassPK()
			).setParameter(
				"previewType", AssetRendererFactory.TYPE_LATEST
			).buildString();
		}

		String portletURLString = HttpComponentsUtil.addParameter(
			layoutURL, "p_l_mode", Constants.PREVIEW);

		return portletURLString + "#portlet_" +
			layoutClassedModelUsage.getContainerKey();
	}

	public String getTypeLabel(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		if (layoutClassedModelUsage.getType() ==
				LayoutClassedModelUsageConstants.TYPE_DISPLAY_PAGE_TEMPLATE) {

			return "display-page-template";
		}

		if (layoutClassedModelUsage.getType() ==
				LayoutClassedModelUsageConstants.TYPE_LAYOUT) {

			return "page";
		}

		return "page-template";
	}

	public boolean isShowPreview(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		if (layoutClassedModelUsage.getType() ==
				LayoutClassedModelUsageConstants.TYPE_LAYOUT) {

			return true;
		}

		if ((layoutClassedModelUsage.getType() ==
				LayoutClassedModelUsageConstants.TYPE_DISPLAY_PAGE_TEMPLATE) ||
			(layoutClassedModelUsage.getType() !=
				LayoutClassedModelUsageConstants.TYPE_PAGE_TEMPLATE)) {

			return false;
		}

		long plid = layoutClassedModelUsage.getPlid();

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return false;
		}

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if ((layoutPageTemplateEntry == null) ||
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return false;
		}

		return true;
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

}