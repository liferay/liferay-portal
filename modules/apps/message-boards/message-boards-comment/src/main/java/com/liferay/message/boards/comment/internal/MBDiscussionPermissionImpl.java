/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.comment.internal;

import com.liferay.comment.configuration.CommentGroupServiceConfiguration;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.comment.BaseDiscussionPermission;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermissionUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Sergio González
 */
@Component(service = DiscussionPermission.class)
public class MBDiscussionPermissionImpl extends BaseDiscussionPermission {

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, long companyId, long groupId,
		String className, long classPK) {

		return hasPermission(
			permissionChecker, companyId, groupId, className, classPK,
			ActionKeys.ADD_DISCUSSION);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, Comment comment,
			String actionId)
		throws PortalException {

		if (comment instanceof MBCommentImpl) {
			MBCommentImpl mbCommentImpl = (MBCommentImpl)comment;

			MBMessage mbMessage = mbCommentImpl.getMessage();

			return _hasPermission(permissionChecker, mbMessage, actionId);
		}

		return hasPermission(
			permissionChecker, comment.getCommentId(), actionId);
	}

	@Override
	public boolean hasPermission(
		PermissionChecker permissionChecker, long companyId, long groupId,
		String className, long classPK, String actionId) {

		if (_mbBanLocalService.hasBan(groupId, permissionChecker.getUserId())) {
			return false;
		}

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			className);

		if (!resourceActions.contains(actionId)) {
			return true;
		}

		Boolean hasPermission = ModelResourcePermissionUtil.contains(
			permissionChecker, groupId, className, classPK, actionId);

		if (hasPermission != null) {
			return hasPermission.booleanValue();
		}

		return permissionChecker.hasPermission(
			groupId, className, classPK, actionId);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long commentId,
			String actionId)
		throws PortalException {

		return _hasPermission(
			permissionChecker, _mbMessageLocalService.getMessage(commentId),
			actionId);
	}

	@Override
	public boolean hasSubscribePermission(
		PermissionChecker permissionChecker, long companyId, long groupId,
		String className, long classPK) {

		return hasViewPermission(
			permissionChecker, companyId, groupId, className, classPK);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, long companyId, long groupId,
		String className, long classPK) {

		return hasPermission(
			permissionChecker, companyId, groupId, className, classPK,
			ActionKeys.VIEW);
	}

	private int _getDepotEntryType(MBMessage message) {
		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, message.getClassName());

		if (infoItemObjectProvider == null) {
			return DepotConstants.TYPE_ANY;
		}

		try {
			Object infoItem = infoItemObjectProvider.getInfoItem(
				new ClassPKInfoItemIdentifier(message.getClassPK()));

			if (!(infoItem instanceof GroupedModel)) {
				return DepotConstants.TYPE_ANY;
			}

			GroupedModel groupedModel = (GroupedModel)infoItem;

			DepotEntry depotEntry =
				_depotEntryLocalService.fetchGroupDepotEntry(
					groupedModel.getGroupId());

			if (depotEntry == null) {
				return DepotConstants.TYPE_ANY;
			}

			return depotEntry.getType();
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}

			return DepotConstants.TYPE_ANY;
		}
	}

	private boolean _hasPermission(
			PermissionChecker permissionChecker, MBMessage message,
			String actionId)
		throws ConfigurationException {

		String className = message.getClassName();

		if (className.equals(WorkflowInstance.class.getName())) {
			return permissionChecker.hasPermission(
				message.getGroupId(), PortletKeys.WORKFLOW_DEFINITION,
				message.getGroupId(), ActionKeys.VIEW);
		}

		CommentGroupServiceConfiguration commentGroupServiceConfiguration =
			_configurationProvider.getGroupConfiguration(
				CommentGroupServiceConfiguration.class, message.getCompanyId(),
				message.getGroupId());

		int depotEntryType = _getDepotEntryType(message);

		if ((commentGroupServiceConfiguration.alwaysEditableByOwner() ||
			 (depotEntryType == DepotConstants.TYPE_PROJECT) ||
			 (depotEntryType == DepotConstants.TYPE_SPACE)) &&
			(permissionChecker.getUserId() == message.getUserId())) {

			return true;
		}

		if ((depotEntryType == DepotConstants.TYPE_PROJECT) &&
			actionId.equals(ActionKeys.UPDATE_DISCUSSION)) {

			return false;
		}

		if (message.isPending()) {
			Boolean hasPermission = WorkflowPermissionUtil.hasPermission(
				permissionChecker, message.getGroupId(),
				message.getWorkflowClassName(), message.getMessageId(),
				actionId);

			if (hasPermission != null) {
				return hasPermission.booleanValue();
			}
		}

		return hasPermission(
			permissionChecker, message.getCompanyId(), message.getGroupId(),
			className, message.getClassPK(), actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBDiscussionPermissionImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

}