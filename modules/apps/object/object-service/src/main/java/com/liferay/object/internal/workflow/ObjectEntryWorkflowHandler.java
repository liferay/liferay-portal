/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.workflow;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntryWorkflowHandler
	extends BaseWorkflowHandler<ObjectEntry> {

	public ObjectEntryWorkflowHandler(
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
	}

	@Override
	public AssetRenderer<ObjectEntry> getAssetRenderer(long classPK)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			classPK);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		AssetRendererFactory<ObjectEntry> objectEntryAssetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				objectDefinition.getClassName());

		return objectEntryAssetRendererFactory.getAssetRenderer(classPK);
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public long getEntryClassPK(
			long companyId, HttpServletRequest httpServletRequest,
			WorkflowTask workflowTask)
		throws PortalException {

		long assetEntryClassPK = ParamUtil.getLong(
			httpServletRequest, "assetEntryClassPK");

		if (assetEntryClassPK > 0) {
			return assetEntryClassPK;
		}

		return super.getEntryClassPK(
			companyId, httpServletRequest, workflowTask);
	}

	@Override
	public String getTitle(long classPK, Locale locale) {
		try {
			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				classPK);

			return objectEntry.getTitleValue();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getType(Locale locale) {
		return _objectDefinition.getLabel(locale);
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				companyId, groupId, ObjectEntryFolder.class.getName(),
				_getObjectEntryFolderId(classPK),
				ObjectDefinitionConstants.OBJECT_DEFINITION_ID_ALL, true);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		return super.getWorkflowDefinitionLink(companyId, groupId, classPK);
	}

	@Override
	public boolean isVisible(Group group) {
		if (group.isSite() &&
			!Objects.equals(
				ObjectDefinitionConstants.SCOPE_SITE,
				_objectDefinition.getScope())) {

			return false;
		}

		return true;
	}

	@Override
	public ObjectEntry updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		return _objectEntryLocalService.updateStatus(
			userId, classPK, status, serviceContext);
	}

	@Override
	public ObjectEntry updateStatus(
			ObjectEntry objectEntry, int status,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		return _objectEntryLocalService.updateStatus(
			userId, objectEntry, status, serviceContext);
	}

	private long _getObjectEntryFolderId(long classPK) throws PortalException {
		Long objectEntryFolderId =
			ObjectEntryThreadLocal.getObjectEntryFolderId();

		if (objectEntryFolderId != null) {
			return objectEntryFolderId;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			classPK);

		return objectEntry.getObjectEntryFolderId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryWorkflowHandler.class);

	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}