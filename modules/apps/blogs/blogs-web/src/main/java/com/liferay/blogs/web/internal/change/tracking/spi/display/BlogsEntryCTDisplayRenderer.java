/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.change.tracking.spi.display;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.util.BlogsEntryUtil;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class BlogsEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<BlogsEntry> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, BlogsEntry blogsEntry)
		throws Exception {

		Group group = _groupLocalService.getGroup(blogsEntry.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, BlogsPortletKeys.BLOGS, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/blogs/edit_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"entryId", blogsEntry.getEntryId()
		).buildString();
	}

	@Override
	public Class<BlogsEntry> getModelClass() {
		return BlogsEntry.class;
	}

	@Override
	public String getTitle(Locale locale, BlogsEntry blogsEntry) {
		return blogsEntry.getTitle();
	}

	@Override
	public String renderPreview(DisplayContext<BlogsEntry> displayContext)
		throws Exception {

		BlogsEntry blogsEntry = displayContext.getModel();

		return blogsEntry.getContent();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<BlogsEntry> displayBuilder) {
		BlogsEntry blogsEntry = displayBuilder.getModel();

		displayBuilder.display(
			"title",
			() -> {
				ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
					displayBuilder.getLocale(), getClass());

				return BlogsEntryUtil.getDisplayTitle(
					resourceBundle, blogsEntry);
			}
		).display(
			"author", blogsEntry.getUserName()
		).display(
			"create-date", blogsEntry.getCreateDate()
		).display(
			"display-date", blogsEntry.getDisplayDate()
		).display(
			"status", blogsEntry.getStatus()
		).display(
			"views",
			() -> {
				AssetEntry assetEntry = _assetEntryLocalService.getEntry(
					BlogsEntry.class.getName(), blogsEntry.getEntryId());

				return String.valueOf(assetEntry.getViewCount());
			}
		);
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}