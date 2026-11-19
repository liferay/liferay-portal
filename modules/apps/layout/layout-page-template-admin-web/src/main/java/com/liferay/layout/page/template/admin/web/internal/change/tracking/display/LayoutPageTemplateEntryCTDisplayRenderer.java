/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.change.tracking.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutPageTemplateEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<LayoutPageTemplateEntry> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			LayoutPrototype layoutPrototype =
				_layoutPrototypeLocalService.fetchLayoutPrototype(
					layoutPageTemplateEntry.getLayoutPrototypeId());

			if (layoutPrototype == null) {
				return null;
			}

			Group layoutPrototypeGroup = layoutPrototype.getGroup();

			return HttpComponentsUtil.addParameters(
				layoutPrototypeGroup.getDisplayURL(themeDisplay, true),
				"p_l_back_url", themeDisplay.getURLCurrent());
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		return HttpComponentsUtil.addParameters(
			PortalUtil.getLayoutFullURL(
				layout.fetchDraftLayout(), themeDisplay),
			"p_l_back_url", themeDisplay.getURLCurrent(), "p_l_mode",
			Constants.EDIT);
	}

	@Override
	public Class<LayoutPageTemplateEntry> getModelClass() {
		return LayoutPageTemplateEntry.class;
	}

	@Override
	public String getTitle(
			Locale locale, LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		return layoutPageTemplateEntry.getName();
	}

	@Override
	public String renderPreview(
			DisplayContext<LayoutPageTemplateEntry> displayContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			displayContext.getModel();

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Layout previewLayout = layout;

		if (layout.isTypeContent() || layoutPageTemplateEntry.isDraft()) {
			previewLayout = layout.fetchDraftLayout();
		}

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.BASIC) {

			String friendlyURL = HttpComponentsUtil.addParameter(
				_portal.getLayoutFriendlyURL(previewLayout, themeDisplay),
				"p_l_mode", "preview");

			friendlyURL = HttpComponentsUtil.addParameter(
				friendlyURL, "previewCTCollectionId",
				layout.getCtCollectionId());

			return StringBundler.concat(
				"<iframe frameborder=\"0\" onload=\"this.style.height = ",
				"(this.contentWindow.document.body.scrollHeight+20) + 'px';\" ",
				"src=\"", friendlyURL, "\" width=\"100%\"></iframe>");
		}

		String url = themeDisplay.getPathMain() + "/portal/get_page_preview";

		if (layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE) {

			url = HttpComponentsUtil.addParameter(
				url, "p_l_id", previewLayout.getPlid());
		}

		url = HttpComponentsUtil.addParameter(url, "p_l_mode", "preview");
		url = HttpComponentsUtil.addParameter(url, "p_p_state", "undefined");

		String languageId = LocaleUtil.toLanguageId(displayContext.getLocale());

		url = HttpComponentsUtil.addParameter(url, "languageId", languageId);
		url = HttpComponentsUtil.addParameter(
			url, "previewCTCollectionId", layout.getCtCollectionId());

		if ((layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) ||
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			url = HttpComponentsUtil.addParameter(
				url, "selPlid", previewLayout.getPlid());
		}

		return StringBundler.concat(
			"<iframe frameborder=\"0\" onload=\"this.style.height = ",
			"(this.contentWindow.document.body.scrollHeight+20) + 'px';\" ",
			"src=\"", url, "\" width=\"100%\"></iframe>");
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<LayoutPageTemplateEntry> displayBuilder) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			displayBuilder.getModel();

		displayBuilder.display(
			"name", layoutPageTemplateEntry.getName()
		).display(
			"created-by",
			() -> {
				String userName = layoutPageTemplateEntry.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", layoutPageTemplateEntry.getCreateDate()
		).display(
			"last-modified", layoutPageTemplateEntry.getModifiedDate()
		).display(
			"site",
			() -> {
				Group group = _groupLocalService.fetchGroup(
					layoutPageTemplateEntry.getGroupId());

				return group.getName(displayBuilder.getLocale());
			}
		).display(
			"last-publish-date", layoutPageTemplateEntry.getLastPublishDate()
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private Portal _portal;

}