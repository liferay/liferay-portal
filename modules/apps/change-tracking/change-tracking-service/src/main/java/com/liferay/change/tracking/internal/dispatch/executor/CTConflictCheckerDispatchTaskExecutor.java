/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.dispatch.executor;

import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.internal.notification.CTUserNotificationSender;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + CTConflictCheckerDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + CTConflictCheckerDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class CTConflictCheckerDispatchTaskExecutor
	extends BaseDispatchTaskExecutor {

	public static final String KEY = "scheduled-publications-conflict-checks";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_ctCollectionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"status", WorkflowConstants.STATUS_SCHEDULED)));
		actionableDynamicQuery.setCompanyId(dispatchTrigger.getCompanyId());
		actionableDynamicQuery.setPerformActionMethod(
			(CTCollection ctCollection) -> {
				Map<Long, List<ConflictInfo>> conflictMap =
					_ctCollectionLocalService.checkConflicts(ctCollection);

				if (conflictMap.isEmpty()) {
					return;
				}

				_ctUserNotificationSender.sendUserNotificationEvents(
					JSONUtil.put(
						"ctCollectionId", ctCollection.getCtCollectionId()
					).put(
						"ctCollectionName",
						HtmlUtil.escape(ctCollection.getName())
					).put(
						"notificationType",
						UserNotificationDefinition.
							NOTIFICATION_TYPE_REVIEW_ENTRY
					).put(
						"scheduled", true
					).put(
						"showConflicts", true
					),
					_ctUserNotificationSender.getPublicationRoleUserIds(
						ctCollection, true, PublicationRoleConstants.NAME_ADMIN,
						PublicationRoleConstants.NAME_EDITOR,
						PublicationRoleConstants.NAME_PUBLISHER));
			});

		actionableDynamicQuery.performActions();
	}

	@Override
	public String getName() {
		return KEY;
	}

	@Override
	public boolean isHiddenInUI() {
		return true;
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTUserNotificationSender _ctUserNotificationSender;

}