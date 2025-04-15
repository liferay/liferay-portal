/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.util.BlogsEntryAssetEntryUtil;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_AGGREGATOR,
		"mvc.command.name=/blogs/view_entry"
	},
	service = MVCRenderCommand.class
)
public class ViewEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long assetCategoryId = ParamUtil.getLong(renderRequest, "categoryId");
		String assetCategoryName = ParamUtil.getString(renderRequest, "tag");

		if ((assetCategoryId > 0) || Validator.isNotNull(assetCategoryName)) {
			return "/blogs/view.jsp";
		}

		try {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);

			BlogsEntry entry = ActionUtil.getEntry(renderRequest);

			CTTimelineUtil.setCTTimelineKeys(
				renderRequest, BlogsEntry.class, entry.getPrimaryKey());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			if (!Objects.equals(
					portletDisplay.getPortletName(),
					BlogsPortletKeys.BLOGS_ADMIN)) {

				_assetEntryService.incrementViewCounter(
					BlogsEntryAssetEntryUtil.getAssetEntry(
						httpServletRequest, entry));
			}

			String assetDisplayPageFriendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						BlogsEntry.class.getName(),
						new ClassPKInfoItemIdentifier(entry.getEntryId())),
					themeDisplay);

			if (assetDisplayPageFriendlyURL != null) {
				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.sendRedirect(assetDisplayPageFriendlyURL);

				return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
			}

			FriendlyURLEntry mainFriendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					BlogsEntry.class, entry.getEntryId());

			boolean redirectToLastFriendlyURL = ParamUtil.getBoolean(
				renderRequest, "redirectToLastFriendlyURL", true);
			String urlTitle = ParamUtil.getString(renderRequest, "urlTitle");

			if (redirectToLastFriendlyURL && Validator.isNotNull(urlTitle) &&
				!urlTitle.equals(mainFriendlyURLEntry.getUrlTitle())) {

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.sendRedirect(
					PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCRenderCommandName(
						"/blogs/view_entry"
					).setParameter(
						"urlTitle", mainFriendlyURLEntry.getUrlTitle()
					).buildString());

				return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
			}

			httpServletRequest.setAttribute(WebKeys.BLOGS_ENTRY, entry);

			if (PropsValues.BLOGS_PINGBACK_ENABLED && (entry != null) &&
				entry.isAllowPingbacks()) {

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.addHeader(
					"X-Pingback",
					_portal.getPortalURL(renderRequest) + "/xmlrpc/pingback");
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/blogs/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/blogs/view_entry.jsp";
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetEntryService _assetEntryService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Portal _portal;

}