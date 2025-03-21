/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.context.ViewConflictsDisplayContext;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/view_conflicts"
	},
	service = MVCRenderCommand.class
)
public class ViewConflictsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.fetchCTPreferences(
				themeDisplay.getCompanyId(), themeDisplay.getUserId());

		long activeCtCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;

		if (ctPreferences != null) {
			activeCtCollectionId = ctPreferences.getCtCollectionId();
		}

		long ctCollectionId = ParamUtil.getLong(
			renderRequest, "ctCollectionId");

		try {
			CTCollection ctCollection =
				_ctCollectionLocalService.getCTCollection(ctCollectionId);

			if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				String redirect = ParamUtil.getString(
					renderRequest, "redirect");

				if (Validator.isNull(redirect)) {
					redirect = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCRenderCommandName(
						"/change_tracking/view_changes"
					).setParameter(
						"ctCollectionId", ctCollectionId
					).buildString();
				}

				httpServletResponse.sendRedirect(redirect);
			}

			Map<Long, List<ConflictInfo>> conflictInfoMap = null;

			boolean hasUnapprovedChanges = false;

			if (_ctCollectionLocalService.hasUnapprovedChanges(
					ctCollectionId)) {

				hasUnapprovedChanges = true;
			}

			if (!hasUnapprovedChanges ||
				_ctSettingsConfigurationHelper.isUnapprovedChangesAllowed(
					themeDisplay.getCompanyId())) {

				conflictInfoMap = _ctCollectionLocalService.checkConflicts(
					ctCollection);
			}

			renderRequest.setAttribute(
				CTWebKeys.VIEW_CONFLICTS_DISPLAY_CONTEXT,
				new ViewConflictsDisplayContext(
					activeCtCollectionId, conflictInfoMap, ctCollection,
					_ctCollectionLocalService, _ctDisplayRendererRegistry,
					_ctEntryLocalService, _ctSettingsConfigurationHelper,
					hasUnapprovedChanges, _language, _portal, renderRequest,
					renderResponse));

			return "/publications/view_conflicts.jsp";
		}
		catch (IOException | PortalException exception) {
			throw new PortletException(exception);
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}