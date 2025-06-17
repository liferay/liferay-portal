/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.closure.CTClosureFactory;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.BasePersistenceRegistry;
import com.liferay.change.tracking.web.internal.display.context.PublicationsDisplayContext;
import com.liferay.change.tracking.web.internal.display.context.ViewChangesDisplayContext;
import com.liferay.change.tracking.web.internal.helper.PublicationHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portlet.LiferayPortletUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/view_change"
	},
	service = MVCRenderCommand.class
)
public class ViewChangeMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

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

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		try {
			if ((ctCollection == null) ||
				!_ctCollectionModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), ctCollection,
					ActionKeys.VIEW)) {

				return "/publications/view_publications.jsp";
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return "/publications/view_changes.jsp";
		}

		long ctEntryId = ParamUtil.getLong(renderRequest, "ctEntryId");

		long modelClassNameId = 0;
		long modelClassPK = 0;

		if (ctEntryId == 0) {
			modelClassNameId = ParamUtil.getLong(
				renderRequest, "modelClassNameId");
			modelClassPK = ParamUtil.getLong(renderRequest, "modelClassPK");
		}
		else {
			CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(ctEntryId);

			if (ctEntry == null) {
				return "/publications/view_changes.jsp";
			}

			modelClassNameId = ctEntry.getModelClassNameId();
			modelClassPK = ctEntry.getModelClassPK();
		}

		LiferayPortletRequest liferayPortletRequest =
			LiferayPortletUtil.getLiferayPortletRequest(renderRequest);

		DynamicServletRequest dynamicServletRequest =
			(DynamicServletRequest)
				liferayPortletRequest.getHttpServletRequest();

		dynamicServletRequest.setParameter(
			"entry",
			String.valueOf(modelClassNameId) + "-" +
				String.valueOf(modelClassPK));

		ViewChangesDisplayContext viewChangesDisplayContext =
			new ViewChangesDisplayContext(
				activeCtCollectionId, _basePersistenceRegistry,
				_ctClosureFactory, ctCollection, _ctCollectionLocalService,
				_ctDisplayRendererRegistry, _ctEntryLocalService,
				_ctSchemaVersionLocalService, _groupLocalService, _language,
				_portal,
				new PublicationsDisplayContext(
					_ctCollectionLocalService, _ctDisplayRendererRegistry,
					_ctPreferencesLocalService, _ctRemoteLocalService,
					_portal.getHttpServletRequest(renderRequest), _language,
					_publicationHelper, renderRequest, renderResponse),
				_publishSchedulerSnapshot.get(), renderRequest, renderResponse,
				_userLocalService, _workflowInstanceLinkLocalService,
				_workflowTaskManager);

		renderRequest.setAttribute(
			CTWebKeys.VIEW_CHANGES_DISPLAY_CONTEXT, viewChangesDisplayContext);

		return "/publications/view_change.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewChangeMVCRenderCommand.class);

	private static final Snapshot<PublishScheduler> _publishSchedulerSnapshot =
		new Snapshot<>(
			ViewChangeMVCRenderCommand.class, PublishScheduler.class, null,
			true);

	@Reference
	private BasePersistenceRegistry _basePersistenceRegistry;

	@Reference
	private CTClosureFactory _ctClosureFactory;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTRemoteLocalService _ctRemoteLocalService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PublicationHelper _publicationHelper;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}