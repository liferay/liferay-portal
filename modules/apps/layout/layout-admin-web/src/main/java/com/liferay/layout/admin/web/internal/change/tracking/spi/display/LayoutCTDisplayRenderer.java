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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		String currentURL = _portal.getCurrentURL(httpServletRequest);

		if (layout.isTypeContent()) {
			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					layout.fetchDraftLayout(), themeDisplay),
				"p_l_back_url", currentURL, "p_l_mode", Constants.EDIT);
		}

		if (layout.isTypePortlet() &&
			!Objects.equals(
				layout.getType(), LayoutConstants.TYPE_FULL_PAGE_APPLICATION)) {

			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(layout, themeDisplay),
				"p_l_back_url", currentURL);
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setParameter(
			"groupId", layout.getGroupId()
		).setParameter(
			"privateLayout", layout.isPrivateLayout()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
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
	public boolean isShowPreviewDiff() {
		return FeatureFlagManagerUtil.isEnabled(
			CompanyThreadLocal.getCompanyId(), "LPD-84028");
	}

	@Override
	public String renderPreview(DisplayContext<Layout> displayContext)
		throws Exception {

		Layout layout = displayContext.getModel();

		if (layout.isTypeURL()) {
			return null;
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				layout.getCompanyId(), "LPD-84028")) {

			return _renderIframePreview(displayContext, layout);
		}

		return _renderInlinePreview(displayContext, layout);
	}

	@Override
	public String renderPreviewStyles(DisplayContext<Layout> displayContext)
		throws Exception {

		Layout layout = displayContext.getModel();

		if (!FeatureFlagManagerUtil.isEnabled(
				layout.getCompanyId(), "LPD-84028")) {

			return null;
		}

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		String stylesContent = (String)httpServletRequest.getAttribute(
			_STYLES_CONTENT);

		httpServletRequest.removeAttribute(_STYLES_CONTENT);

		return stylesContent;
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
				StyleBookEntry styleBookEntry =
					StyleBookEntryProviderUtil.getStyleBookEntry(layout);

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

	private String _extractHeadStyles(String html) {
		Matcher headMatcher = _headPattern.matcher(html);

		if (!headMatcher.find()) {
			return StringPool.BLANK;
		}

		String headContent = headMatcher.group(1);

		StringBundler sb = new StringBundler();

		Matcher tagMatcher = _styleAndLinkPattern.matcher(headContent);

		while (tagMatcher.find()) {
			sb.append(tagMatcher.group());
		}

		return sb.toString();
	}

	private String _normalizePreview(String html) {
		String normalized = HtmlUtil.stripBetween(html, "script");

		normalized = _analyticsCollectionPattern.matcher(
			normalized
		).replaceAll(
			StringPool.BLANK
		);

		normalized = _fragmentIdPattern.matcher(
			normalized
		).replaceAll(
			StringPool.BLANK
		);

		return normalized;
	}

	private String _renderIframePreview(
			DisplayContext<Layout> displayContext, Layout layout)
		throws Exception {

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout previewLayout = layout;

		if (layout.isDenied() || layout.isPending()) {
			previewLayout = layout.fetchDraftLayout();
		}

		String languageId = LocaleUtil.toLanguageId(displayContext.getLocale());

		String url = HttpComponentsUtil.addParameters(
			themeDisplay.getPathMain() + "/portal/get_page_preview",
			"languageId", languageId, "p_l_mode", Constants.PREVIEW,
			"p_p_state", "undefined", "previewCTCollectionId",
			layout.getCtCollectionId());

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId");

		if (segmentsExperienceId > 0) {
			url = HttpComponentsUtil.addParameter(
				url, "segmentsExperienceId", segmentsExperienceId);
		}

		url = HttpComponentsUtil.addParameter(
			url, "selPlid", previewLayout.getPlid());

		url = HttpComponentsUtil.addParameter(
			url, "showUserLocaleOptionsMessage", "false");

		return StringBundler.concat(
			"<iframe frameborder=\"0\" onload=\"this.style.height = ",
			"(this.contentWindow.document.body.scrollHeight+20) + 'px';\" ",
			"src=\"", url, "\" width=\"100%\"></iframe>");
	}

	private String _renderInlinePreview(
			DisplayContext<Layout> displayContext, Layout layout)
		throws Exception {

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		Layout previewLayout = layout;

		if (layout.isDenied() || layout.isPending()) {
			previewLayout = layout.fetchDraftLayout();
		}

		ThemeDisplay currentThemeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ThemeDisplay themeDisplay = (ThemeDisplay)currentThemeDisplay.clone();

		themeDisplay.setLayout(previewLayout);

		LayoutSet layoutSet = previewLayout.getLayoutSet();

		themeDisplay.setLocale(displayContext.getLocale());

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());
		themeDisplay.setPlid(previewLayout.getPlid());
		themeDisplay.setScopeGroupId(previewLayout.getGroupId());
		themeDisplay.setSignedIn(false);
		themeDisplay.setSiteGroupId(previewLayout.getGroupId());

		User guestUser = _userLocalService.getGuestUser(
			themeDisplay.getCompanyId());

		themeDisplay.setUser(guestUser);

		long[] currentSegmentsExperienceIds = GetterUtil.getLongValues(
			httpServletRequest.getAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));
		Layout currentLayout = (Layout)httpServletRequest.getAttribute(
			WebKeys.LAYOUT);
		Object currentOutputData = httpServletRequest.getAttribute(
			WebKeys.OUTPUT_DATA);
		boolean currentPortletDecorate = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(WebKeys.PORTLET_DECORATE));

		try {
			long segmentsExperienceId = ParamUtil.getLong(
				httpServletRequest, "segmentsExperienceId");

			if (segmentsExperienceId <= 0) {
				segmentsExperienceId =
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(
							previewLayout.getPlid());
			}

			httpServletRequest.setAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
				new long[] {segmentsExperienceId});

			httpServletRequest.setAttribute(WebKeys.LAYOUT, previewLayout);
			httpServletRequest.setAttribute(WebKeys.OUTPUT_DATA, null);
			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DECORATE, Boolean.FALSE);
			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ServiceContext clonedServiceContext =
				(ServiceContext)serviceContext.clone();

			clonedServiceContext.setPlid(previewLayout.getPlid());
			clonedServiceContext.setScopeGroupId(previewLayout.getGroupId());

			ServiceContextThreadLocal.pushServiceContext(clonedServiceContext);

			try {
				previewLayout.includeLayoutContent(
					httpServletRequest,
					displayContext.getHttpServletResponse());

				StringBundler sb =
					(StringBundler)httpServletRequest.getAttribute(
						WebKeys.LAYOUT_CONTENT);

				if (sb == null) {
					return null;
				}

				String themeHtml = ThemeUtil.include(
					ServletContextPool.get(_portal.getServletContextName()),
					httpServletRequest, displayContext.getHttpServletResponse(),
					"portal_normal.ftl", previewLayout.getTheme(), false);

				httpServletRequest.setAttribute(
					_STYLES_CONTENT, _extractHeadStyles(themeHtml));

				return _normalizePreview(sb.toString());
			}
			finally {
				ServiceContextThreadLocal.popServiceContext();
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to render preview for layout " +
						previewLayout.getPlid(),
					exception);
			}

			return null;
		}
		finally {
			httpServletRequest.setAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
				currentSegmentsExperienceIds);
			httpServletRequest.setAttribute(WebKeys.LAYOUT, currentLayout);
			httpServletRequest.setAttribute(
				WebKeys.OUTPUT_DATA, currentOutputData);
			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DECORATE, currentPortletDecorate);
			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, currentThemeDisplay);
		}
	}

	private static final String _STYLES_CONTENT =
		LayoutCTDisplayRenderer.class + "#STYLES_CONTENT";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutCTDisplayRenderer.class);

	private static final Pattern _analyticsCollectionPattern = Pattern.compile(
		"data-analytics-targetable-collection=\"[^\"]*\"");
	private static final Pattern _fragmentIdPattern = Pattern.compile(
		"id=\"fragment-[a-f0-9-]+\"");
	private static final Pattern _headPattern = Pattern.compile(
		"<head[^>]*>([\\s\\S]*?)</head>", Pattern.CASE_INSENSITIVE);
	private static final Pattern _styleAndLinkPattern = Pattern.compile(
		"<(?:link[^>]*rel=[\"']stylesheet[\"'][^>]*>|" +
			"style[^>]*>[\\s\\S]*?</style>)",
		Pattern.CASE_INSENSITIVE);

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}