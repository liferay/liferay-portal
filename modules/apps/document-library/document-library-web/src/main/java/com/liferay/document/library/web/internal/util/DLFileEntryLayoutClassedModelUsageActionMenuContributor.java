/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.util.LayoutClassedModelUsageActionMenuContributor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Szalay
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.repository.model.FileEntry",
	service = LayoutClassedModelUsageActionMenuContributor.class
)
public class DLFileEntryLayoutClassedModelUsageActionMenuContributor
	implements LayoutClassedModelUsageActionMenuContributor {

	@Override
	public List<DropdownItem> getLayoutClassedModelUsageActionDropdownItems(
		HttpServletRequest httpServletRequest,
		LayoutClassedModelUsage layoutClassedModelUsage) {

		DLFileEntry dlFileEntry = _dLFileEntryLocalService.fetchDLFileEntry(
			layoutClassedModelUsage.getClassPK());

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				dropdownItem.setHref(
					_getURL(
						dlFileEntry, layoutClassedModelUsage,
						AssetRendererFactory.TYPE_LATEST_APPROVED,
						InfoItemIdentifier.VERSION_LATEST_APPROVED,
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(themeDisplay.getLocale(), "view-in-page"));
			}
		).build();
	}

	private String _getURL(
			DLFileEntry dlFileEntry,
			LayoutClassedModelUsage layoutClassedModelUsage, int previewType,
			String previewVersion, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutURL = null;

		if (layoutClassedModelUsage.getContainerType() ==
				_portal.getClassNameId(FragmentEntryLink.class)) {

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
				layoutURL, "previewType", String.valueOf(previewType));
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewVersion", previewVersion);
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
				"previewType", previewType
			).setParameter(
				"previewVersion", previewVersion
			).buildString();
		}

		String portletURLString = HttpComponentsUtil.addParameters(
			layoutURL, "p_l_back_url", themeDisplay.getURLCurrent(),
			"fileEntryId", dlFileEntry.getFileEntryId());

		return portletURLString + "#portlet_" +
			layoutClassedModelUsage.getContainerKey();
	}

	@Reference
	private DLFileEntryLocalService _dLFileEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}