/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.web.internal.helper.SelectedUserHelper;
import com.liferay.user.associated.data.web.internal.helper.UADApplicationSummaryHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/erase_personal_data"
	},
	service = MVCActionCommand.class
)
public class ErasePersonalDataMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		User selectedUser = _selectedUserHelper.getSelectedUser(actionRequest);

		if (selectedUser.isActive()) {
			_userGroupLocalService.clearUserUserGroups(
				selectedUser.getUserId());

			selectedUser = _userLocalService.updateStatus(
				selectedUser, WorkflowConstants.STATUS_INACTIVE,
				new ServiceContext());

			Group group = selectedUser.getGroup();

			group.setActive(true);

			_groupLocalService.updateGroup(group);
		}
		else {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
		}

		LiferayPortletURL redirectURL = PortletURLFactoryUtil.create(
			actionRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.RENDER_PHASE);

		redirectURL.setParameter(
			"p_u_i_d", String.valueOf(selectedUser.getUserId()));

		String mvcRenderCommandName = "/user_associated_data/review_uad_data";

		int totalReviewableUADEntitiesCount =
			_uadApplicationSummaryHelper.getTotalReviewableUADEntitiesCount(
				selectedUser.getUserId());

		if (totalReviewableUADEntitiesCount == 0) {
			int totalNonreviewableUADEntitiesCount =
				_uadApplicationSummaryHelper.
					getTotalNonreviewableUADEntitiesCount(
						selectedUser.getUserId());

			if (totalNonreviewableUADEntitiesCount == 0) {
				mvcRenderCommandName =
					"/user_associated_data/completed_data_erasure";
			}
			else {
				mvcRenderCommandName =
					"/user_associated_data/anonymize_nonreviewable_uad_data";
			}
		}

		redirectURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);

		actionResponse.sendRedirect(redirectURL.toString());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SelectedUserHelper _selectedUserHelper;

	@Reference
	private UADApplicationSummaryHelper _uadApplicationSummaryHelper;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}