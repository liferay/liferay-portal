/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutCTDisplayRenderer extends BaseCTDisplayRenderer<Layout> {

	@Override
	public String[] getAvailableLanguageIds(Layout layout) {
		return layout.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(Layout layout) {
		return layout.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Layout layout)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_layoutPermission.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.UPDATE) ||
			layout.isSystem()) {

			return null;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).buildPortletURL();

		String currentURL = _portal.getCurrentURL(httpServletRequest);

		portletURL.setParameter("redirect", currentURL);
		portletURL.setParameter("backURL", currentURL);

		portletURL.setParameter("groupId", String.valueOf(layout.getGroupId()));
		portletURL.setParameter("selPlid", String.valueOf(layout.getPlid()));
		portletURL.setParameter(
			"privateLayout", String.valueOf(layout.isPrivateLayout()));

		return portletURL.toString();
	}

	@Override
	public Class<Layout> getModelClass() {
		return Layout.class;
	}

	@Override
	public String getTitle(Locale locale, Layout layout) {
		return layout.getName(locale);
	}

	@Override
	public boolean isHideable(Layout layout) {
		if (layout.isDraftLayout() &&
			(layout.getStatus() == WorkflowConstants.STATUS_DRAFT)) {

			return false;
		}

		return layout.isSystem();
	}

	@Override
	public String renderPreview(DisplayContext<Layout> displayContext)
		throws Exception {

		Layout layout = displayContext.getModel();

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout previewLayout = layout;

		if (layout.isDenied() || layout.isPending()) {
			previewLayout = layout.fetchDraftLayout();
		}

		String url = HttpComponentsUtil.addParameter(
			themeDisplay.getPathMain() + "/portal/update_language", "p_l_id",
			previewLayout.getPlid());

		String redirect = HttpComponentsUtil.addParameter(
			_portal.getLayoutFriendlyURL(previewLayout, themeDisplay),
			"p_l_mode", "preview");

		redirect = HttpComponentsUtil.addParameter(
			redirect, "previewCTCollectionId", layout.getCtCollectionId());

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId");

		if (segmentsExperienceId > 0) {
			redirect = HttpComponentsUtil.addParameter(
				redirect, "segmentsExperienceId", segmentsExperienceId);
		}

		url = HttpComponentsUtil.addParameter(url, "redirect", redirect);

		String languageId = LocaleUtil.toLanguageId(displayContext.getLocale());

		url = HttpComponentsUtil.addParameter(url, "languageId", languageId);

		url = HttpComponentsUtil.addParameter(url, "persistState", "false");
		url = HttpComponentsUtil.addParameter(
			url, "previewCTCollectionId", layout.getCtCollectionId());
		url = HttpComponentsUtil.addParameter(
			url, "showUserLocaleOptionsMessage", "false");

		return StringBundler.concat(
			"<iframe frameborder=\"0\" onload=\"this.style.height = ",
			"(this.contentWindow.document.body.scrollHeight+20) + 'px';\" ",
			"src=\"", url, "\" width=\"100%\"></iframe>");
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Layout> displayBuilder) {
		Layout layout = displayBuilder.getModel();

		displayBuilder.display(
			"name", layout.getName(displayBuilder.getLocale())
		).display(
			"title", layout.getTitle()
		).display(
			"description", layout.getDescription(displayBuilder.getLocale())
		).display(
			"friendly-url", layout.getFriendlyURL()
		).display(
			"created-by",
			() -> {
				String userName = layout.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", layout.getCreateDate()
		).display(
			"last-modified", layout.getModifiedDate()
		).display(
			"site",
			() -> {
				Group group = layout.getGroup();

				return group.getName(displayBuilder.getLocale());
			}
		).display(
			"theme",
			() -> {
				Theme theme = layout.getTheme();

				return theme.getName();
			}
		).display(
			"color-scheme",
			() -> {
				ColorScheme colorScheme = layout.getColorScheme();

				return colorScheme.getName();
			}
		).display(
			"style-book",
			() -> {
				long styleBookEntryId = layout.getStyleBookEntryId();

				if (styleBookEntryId <= 0) {
					return null;
				}

				StyleBookEntry styleBookEntry =
					_styleBookEntryLocalService.fetchStyleBookEntry(
						layout.getStyleBookEntryId());

				if (styleBookEntry == null) {
					return null;
				}

				return styleBookEntry.getName();
			}
		).display(
			"type", layout.getType()
		).display(
			"type-settings", layout.getTypeSettings()
		).display(
			"css", layout.getCss()
		).display(
			"keywords", layout.getKeywords()
		).display(
			"robots", layout.getRobots()
		).display(
			"hidden", layout.isHidden()
		).display(
			"system", layout.isSystem()
		).display(
			"publish-date", layout.getPublishDate()
		).display(
			"last-publish-date", layout.getLastPublishDate()
		).display(
			"priority", layout.getPriority()
		);
	}

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}