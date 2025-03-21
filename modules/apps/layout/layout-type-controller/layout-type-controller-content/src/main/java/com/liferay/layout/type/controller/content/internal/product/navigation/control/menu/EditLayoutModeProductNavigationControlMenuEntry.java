/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.product.navigation.control.menu;

import com.liferay.exportimport.kernel.staging.LayoutStaging;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.security.permission.resource.LayoutContentModelResourcePermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.staging.StagingGroupHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=50"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class EditLayoutModeProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public String getIcon(HttpServletRequest httpServletRequest) {
		return "pencil";
	}

	@Override
	public String getIconCssClass(HttpServletRequest httpServletRequest) {
		return "icon-monospaced";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "edit");
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			Layout layout = themeDisplay.getLayout();

			if (layout.isDraftLayout()) {
				return _getRedirect(
					httpServletRequest,
					_portal.getLayoutFullURL(
						_layoutLocalService.getLayout(layout.getClassPK()),
						themeDisplay),
					layout, themeDisplay);
			}

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout == null) {
				UnicodeProperties unicodeProperties =
					layout.getTypeSettingsProperties();

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(httpServletRequest);

				draftLayout = _layoutLocalService.addLayout(
					null, layout.getUserId(), layout.getGroupId(),
					layout.isPrivateLayout(), layout.getParentLayoutId(),
					_portal.getClassNameId(Layout.class), layout.getPlid(),
					layout.getNameMap(), layout.getTitleMap(),
					layout.getDescriptionMap(), layout.getKeywordsMap(),
					layout.getRobotsMap(), layout.getType(),
					unicodeProperties.toString(), true, true,
					Collections.emptyMap(), layout.getMasterLayoutPlid(),
					serviceContext);

				draftLayout = _layoutLocalService.copyLayoutContent(
					layout, draftLayout);

				_layoutLocalService.updateStatus(
					draftLayout.getUserId(), draftLayout.getPlid(),
					WorkflowConstants.STATUS_APPROVED, serviceContext);
			}

			return _getRedirect(
				httpServletRequest,
				_portal.getLayoutFullURL(draftLayout, themeDisplay), layout,
				themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		String mode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (Objects.equals(mode, Constants.EDIT)) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long scopeGroupId = themeDisplay.getScopeGroupId();

		if (_stagingGroupHelper.isLocalLiveGroup(scopeGroupId) ||
			_stagingGroupHelper.isRemoteLiveGroup(scopeGroupId)) {

			return false;
		}

		Layout layout = themeDisplay.getLayout();

		LayoutRevision layoutRevision = _layoutStaging.getLayoutRevision(
			layout);

		if ((layoutRevision != null) && layoutRevision.isIncomplete()) {
			return false;
		}

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		LayoutTypeController layoutTypeController =
			layoutTypePortlet.getLayoutTypeController();

		if (layoutTypeController.isFullPageDisplayable()) {
			return false;
		}

		String className = (String)httpServletRequest.getAttribute(
			ContentPageEditorWebKeys.CLASS_NAME);

		if (Objects.equals(
				className, LayoutPageTemplateEntry.class.getName()) ||
			(layout instanceof VirtualLayout) || !layout.isLayoutUpdateable() ||
			!layout.isTypeContent()) {

			return false;
		}

		if (layout.isSystem() && layout.isTypeContent()) {
			layout = _layoutLocalService.getLayout(layout.getClassPK());
		}

		if (_layoutPermission.containsLayoutUpdatePermission(
				themeDisplay.getPermissionChecker(), layout) ||
			_modelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), layout.getPlid(),
				ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	private String _addSegmentsExperienceId(
		HttpServletRequest httpServletRequest, Layout layout, String url) {

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId", -1);

		if (segmentsExperienceId != -1) {
			SegmentsExperience publishedSegmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					segmentsExperienceId);

			Layout draftLayout = layout.fetchDraftLayout();

			SegmentsExperience draftSegmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					draftLayout.getGroupId(),
					publishedSegmentsExperience.getSegmentsExperienceKey(),
					draftLayout.getPlid());

			if ((draftSegmentsExperience != null) &&
				(draftLayout.getPlid() == draftSegmentsExperience.getPlid())) {

				return HttpComponentsUtil.setParameter(
					url, "segmentsExperienceId",
					draftSegmentsExperience.getSegmentsExperienceId());
			}
		}

		return url;
	}

	private String _getRedirect(
			HttpServletRequest httpServletRequest, String fullLayoutURL,
			Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		String redirect = HttpComponentsUtil.addParameters(
			fullLayoutURL, "p_l_back_url",
			_addSegmentsExperienceId(
				httpServletRequest, layout,
				_portal.getLayoutFullURL(layout, themeDisplay)),
			"p_l_back_url_title", layout.getName(themeDisplay.getLocale()),
			"p_l_mode", Constants.EDIT);

		return _addSegmentsExperienceId(httpServletRequest, layout, redirect);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditLayoutModeProductNavigationControlMenuEntry.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutStaging _layoutStaging;

	@Reference
	private LayoutContentModelResourcePermission _modelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}