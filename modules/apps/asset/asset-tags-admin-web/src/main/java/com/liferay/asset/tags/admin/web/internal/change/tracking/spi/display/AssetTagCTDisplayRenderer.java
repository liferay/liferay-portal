/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.admin.web.internal.change.tracking.spi.display;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class AssetTagCTDisplayRenderer extends BaseCTDisplayRenderer<AssetTag> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, AssetTag assetTag)
		throws Exception {

		Group group = _groupLocalService.getGroup(assetTag.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_tag.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"tagId", assetTag.getTagId()
		).buildString();
	}

	@Override
	public Class<AssetTag> getModelClass() {
		return AssetTag.class;
	}

	@Override
	public String getTitle(Locale locale, AssetTag assetTag) {
		return assetTag.getName();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<AssetTag> displayBuilder) {
		AssetTag assetTag = displayBuilder.getModel();

		displayBuilder.display(
			"name", assetTag.getName()
		).display(
			"usages",
			_assetTagLocalService.getTagsSize(
				assetTag.getGroupId(), 0, assetTag.getName())
		);
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}