/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.moderation.internal.instance.lifecycle;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class
	AddMBModerationWorkflowDefinitionInitialRequestPortalInstanceLifecycleListener
		extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(null);

		try {
			int workflowDefinitionsCount =
				_workflowDefinitionManager.getWorkflowDefinitionsCount(
					companyId,
					WorkflowDefinitionConstants.
						NAME_MESSAGE_BOARDS_USER_STATS_MODERATION);

			if (workflowDefinitionsCount > 0) {
				return;
			}

			long guestUserId = _userLocalService.getGuestUserId(companyId);
			Company company = _companyLocalService.getCompany(companyId);
			String content = StringUtil.read(
				AddMBModerationWorkflowDefinitionInitialRequestPortalInstanceLifecycleListener.class,
				"dependencies" +
					"/message-boards-moderation-workflow-definition.xml");

			_workflowDefinitionManager.deployWorkflowDefinition(
				null, companyId, guestUserId,
				_localization.getXml(
					_getTitleMap(companyId),
					_language.getLanguageId(company.getLocale()), "title"),
				WorkflowDefinitionConstants.
					NAME_MESSAGE_BOARDS_USER_STATS_MODERATION,
				MBMessage.class.getName(), content.getBytes());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private Map<String, String> _getTitleMap(long companyId) {
		Map<String, String> titleMap = new HashMap<>();

		for (Locale locale : _language.getCompanyAvailableLocales(companyId)) {
			titleMap.put(
				_language.getLanguageId(locale),
				_language.get(
					locale,
					WorkflowDefinitionConstants.
						NAME_MESSAGE_BOARDS_USER_STATS_MODERATION));
		}

		return titleMap;
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}