/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.security.permission;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.propagator.BasePermissionPropagator;
import com.liferay.portal.kernel.security.permission.propagator.PermissionPropagator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kenneth Chang
 * @author Hugo Huijser
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN
	},
	service = PermissionPropagator.class
)
public class MBPermissionPropagatorImpl extends BasePermissionPropagator {

	@Override
	public void propagateRolePermissions(
			ActionRequest actionRequest, String className, String primKey,
			long[] roleIds)
		throws PortalException {

		if (className.equals(MBCategory.class.getName())) {
			_propagateCategoryRolePermissions(
				actionRequest, className, primKey, roleIds);
		}
		else if (className.equals(MBMessage.class.getName())) {
			long messageId = GetterUtil.getLong(primKey);

			MBMessage message = _mbMessageLocalService.getMessage(messageId);

			if (message.isRoot()) {
				_propagateThreadRolePermissions(
					actionRequest, className, messageId, message.getThreadId(),
					roleIds);
			}
		}
		else if (className.equals("com.liferay.message.boards")) {
			_propagateMBRolePermissions(
				actionRequest, className, primKey, roleIds);
		}
	}

	private void _propagateCategoryRolePermissions(
			ActionRequest actionRequest, String className, long primaryKey,
			long categoryId, long[] roleIds)
		throws PortalException {

		for (long roleId : roleIds) {
			propagateRolePermissions(
				actionRequest, roleId, className, primaryKey,
				MBCategory.class.getName(), categoryId);
		}
	}

	private void _propagateCategoryRolePermissions(
			ActionRequest actionRequest, String className, String primKey,
			long[] roleIds)
		throws PortalException {

		long categoryId = GetterUtil.getLong(primKey);

		MBCategory category = _mbCategoryLocalService.getCategory(categoryId);

		List<Object> categoriesAndThreads =
			_mbCategoryLocalService.getCategoriesAndThreads(
				category.getGroupId(), categoryId);

		for (Object categoryOrThread : categoriesAndThreads) {
			if (categoryOrThread instanceof MBThread) {
				MBThread thread = (MBThread)categoryOrThread;

				List<MBMessage> messages =
					_mbMessageLocalService.getThreadMessages(
						thread.getThreadId(), WorkflowConstants.STATUS_ANY);

				for (MBMessage message : messages) {
					_propagateMessageRolePermissions(
						actionRequest, className, categoryId,
						message.getMessageId(), roleIds);
				}
			}
			else {
				category = (MBCategory)categoryOrThread;

				List<Long> categoryIds = new ArrayList<>();

				categoryIds.add(category.getCategoryId());

				categoryIds = _mbCategoryLocalService.getSubcategoryIds(
					categoryIds, category.getGroupId(),
					category.getCategoryId());

				for (final long addCategoryId : categoryIds) {
					_propagateCategoryRolePermissions(
						actionRequest, className, categoryId, addCategoryId,
						roleIds);

					ActionableDynamicQuery actionableDynamicQuery =
						_mbMessageLocalService.getActionableDynamicQuery();

					actionableDynamicQuery.setAddCriteriaMethod(
						dynamicQuery -> {
							Property categoryIdProperty =
								PropertyFactoryUtil.forName("categoryId");

							dynamicQuery.add(
								categoryIdProperty.eq(addCategoryId));
						});
					actionableDynamicQuery.setGroupId(category.getGroupId());
					actionableDynamicQuery.setPerformActionMethod(
						(MBMessage message) -> _propagateMessageRolePermissions(
							actionRequest, className, categoryId,
							message.getMessageId(), roleIds));

					actionableDynamicQuery.performActions();
				}
			}
		}
	}

	private void _propagateMBRolePermissions(
			ActionRequest actionRequest, String className, String primKey,
			long[] roleIds)
		throws PortalException {

		long groupId = GetterUtil.getLong(primKey);

		List<MBCategory> categories = _mbCategoryLocalService.getCategories(
			groupId);

		for (MBCategory category : categories) {
			_propagateCategoryRolePermissions(
				actionRequest, className, groupId, category.getCategoryId(),
				roleIds);
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_mbMessageLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setGroupId(groupId);
		actionableDynamicQuery.setPerformActionMethod(
			(MBMessage message) -> _propagateMessageRolePermissions(
				actionRequest, className, groupId, message.getMessageId(),
				roleIds));

		actionableDynamicQuery.performActions();
	}

	private void _propagateMessageRolePermissions(
			ActionRequest actionRequest, String className, long primaryKey,
			long messageId, long[] roleIds)
		throws PortalException {

		for (long roleId : roleIds) {
			propagateRolePermissions(
				actionRequest, roleId, className, primaryKey,
				MBMessage.class.getName(), messageId);
		}
	}

	private void _propagateThreadRolePermissions(
			ActionRequest actionRequest, String className, long messageId,
			long threadId, long[] roleIds)
		throws PortalException {

		List<MBMessage> messages = _mbMessageLocalService.getThreadMessages(
			threadId, WorkflowConstants.STATUS_ANY);

		for (MBMessage message : messages) {
			_propagateMessageRolePermissions(
				actionRequest, className, messageId, message.getMessageId(),
				roleIds);
		}
	}

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

}