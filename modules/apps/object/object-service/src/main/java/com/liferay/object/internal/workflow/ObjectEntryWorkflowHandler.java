/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.workflow;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

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
		ObjectEntryLocalService objectEntryLocalService) {

		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getTitle(long classPK, Locale locale) {
		try {
			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				classPK);

			String titleValue = objectEntry.getTitleValue();

			if(Validator.isXml(titleValue)){
				String localizedValue =
					LocalizationUtil.getLocalization(titleValue, locale.getLanguage());
				return localizedValue;
			}
			else{
				return titleValue;
			}
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

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryWorkflowHandler.class);

	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;

}