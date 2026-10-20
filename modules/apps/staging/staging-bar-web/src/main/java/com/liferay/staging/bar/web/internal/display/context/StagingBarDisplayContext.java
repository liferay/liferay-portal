/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutRevisionConstants;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutRevisionLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class StagingBarDisplayContext {

	public StagingBarDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, Layout layout) {

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_liferayPortletResponse = liferayPortletResponse;
		_layout = layout;
	}

	public List<DropdownItem> getDropdownItems(
		Layout layout, LayoutRevision layoutRevision, boolean hasWorkflowTask,
		LayoutSetBranch layoutSetBranch) {

		UnsafeConsumer<DropdownItem, Exception>
			reviewHistoryDropdownItemUnsafeConsumer =
				_getReviewHistoryDropdownItemUnsafeConsumer(layoutRevision);

		return DropdownItemListBuilder.add(
			_viewLayoutSetBranchesDropdownItemUnsafeConsumer()
		).add(
			() -> !layoutRevision.isIncomplete() && !layout.isTypeContent(),
			_viewLayoutBranchesDropdownItemUnsafeConsumer(layoutSetBranch)
		).add(
			() -> !layoutRevision.isIncomplete() && !layout.isTypeContent(),
			_viewHistoryDropdownItemUnsafeConsumer(layoutRevision)
		).add(
			() ->
				!hasWorkflowTask && !layout.isTypeContent() &&
				(reviewHistoryDropdownItemUnsafeConsumer != null),
			reviewHistoryDropdownItemUnsafeConsumer
		).build();
	}

	public LayoutRevision updateLayoutRevision(LayoutRevision layoutRevision) {
		if ((layoutRevision == null) || layoutRevision.isApproved() ||
			_layout.isDraftLayout() || !_layout.isTypeContent()) {

			return layoutRevision;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		try {
			layoutRevision =
				LayoutRevisionLocalServiceUtil.updateLayoutRevision(
					_themeDisplay.getUserId(),
					layoutRevision.getLayoutRevisionId(),
					layoutRevision.getLayoutBranchId(),
					layoutRevision.getName(), layoutRevision.getTitle(),
					layoutRevision.getDescription(),
					layoutRevision.getKeywords(), layoutRevision.getRobots(),
					layoutRevision.getTypeSettings(),
					layoutRevision.getIconImage(),
					layoutRevision.getIconImageId(),
					layoutRevision.getThemeId(),
					layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
					serviceContext);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return layoutRevision;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getReviewHistoryDropdownItemUnsafeConsumer(
			LayoutRevision layoutRevision) {

		if (!layoutRevision.isMajor() &&
			(layoutRevision.getParentLayoutRevisionId() !=
				LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID)) {

			return _undoDropdownItemUnsafeConsumer(layoutRevision);
		}

		if (layoutRevision.hasChildren()) {
			List<LayoutRevision> childLayoutRevisions =
				layoutRevision.getChildren();

			LayoutRevision firstChildLayoutRevision = childLayoutRevisions.get(
				0);

			if (firstChildLayoutRevision.isInactive()) {
				return _redoDropdownItemUnsafeConsumer(
					firstChildLayoutRevision);
			}
		}

		return null;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_redoDropdownItemUnsafeConsumer(
			LayoutRevision firstChildLayoutRevision) {

		return dropdownItem -> {
			dropdownItem.putData("action", "redo");
			dropdownItem.putData(
				"layoutRevisionId",
				String.valueOf(firstChildLayoutRevision.getLayoutRevisionId()));
			dropdownItem.putData(
				"layoutSetBranchId",
				String.valueOf(
					firstChildLayoutRevision.getLayoutSetBranchId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_themeDisplay.getLocale(), "redo"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_undoDropdownItemUnsafeConsumer(LayoutRevision layoutRevision) {

		return dropdownItem -> {
			dropdownItem.putData("action", "undo");
			dropdownItem.putData(
				"layoutRevisionId",
				String.valueOf(layoutRevision.getLayoutRevisionId()));
			dropdownItem.putData(
				"layoutSetBranchId",
				String.valueOf(layoutRevision.getLayoutSetBranchId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_themeDisplay.getLocale(), "undo"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_viewHistoryDropdownItemUnsafeConsumer(LayoutRevision layoutRevision) {

		return dropdownItem -> {
			dropdownItem.putData("action", "viewHistory");
			dropdownItem.putData(
				"viewHistoryURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/view_layout_revisions.jsp"
				).setParameter(
					"layoutSetBranchId", layoutRevision.getLayoutSetBranchId()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_themeDisplay.getLocale(), "history[record]"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_viewLayoutBranchesDropdownItemUnsafeConsumer(
			LayoutSetBranch layoutSetBranch) {

		return dropdownItem -> {
			dropdownItem.putData("action", "viewLayoutBranches");
			dropdownItem.putData(
				"viewLayoutBranchesURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/staging_bar/view_layout_branches"
				).setParameter(
					"layoutSetBranchId", layoutSetBranch.getLayoutSetBranchId()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_themeDisplay.getLocale(), "page-variation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_viewLayoutSetBranchesDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "viewLayoutSetBranches");
			dropdownItem.putData(
				"viewLayoutSetBranchesURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/staging_bar/view_layout_set_branches"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(
					_themeDisplay.getLocale(), "site-pages-variation"));
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingBarDisplayContext.class);

	private final Layout _layout;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}