/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class UserDisplayTerms extends DisplayTerms {

	public static final String EMAIL_ADDRESS = "emailAddress";

	public static final String FIRST_NAME = "firstName";

	public static final String LAST_NAME = "lastName";

	public static final String MIDDLE_NAME = "middleName";

	public static final String ORGANIZATION_ID = "organizationId";

	public static final String ROLE_ID = "roleId";

	public static final String SCREEN_NAME = "screenName";

	public static final String STATUS = "status";

	public static final String USER_GROUP_ID = "userGroupId";

	public UserDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		String statusString = ParamUtil.getString(portletRequest, STATUS);

		if (Validator.isNotNull(statusString)) {
			status = GetterUtil.getInteger(statusString);
		}

		emailAddress = ParamUtil.getString(portletRequest, EMAIL_ADDRESS);
		firstName = ParamUtil.getString(portletRequest, FIRST_NAME);
		lastName = ParamUtil.getString(portletRequest, LAST_NAME);
		middleName = ParamUtil.getString(portletRequest, MIDDLE_NAME);
		organizationId = ParamUtil.getLong(portletRequest, ORGANIZATION_ID);
		roleId = ParamUtil.getLong(portletRequest, ROLE_ID);
		screenName = ParamUtil.getString(portletRequest, SCREEN_NAME);
		userGroupId = ParamUtil.getLong(portletRequest, USER_GROUP_ID);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public long getRoleId() {
		return roleId;
	}

	public String getScreenName() {
		return screenName;
	}

	public int getStatus() {
		return status;
	}

	public long getUserGroupId() {
		return userGroupId;
	}

	public boolean isActive() {
		if (status == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}

		return false;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	protected String emailAddress;
	protected String firstName;
	protected String lastName;
	protected String middleName;
	protected long organizationId;
	protected long roleId;
	protected String screenName;
	protected int status;
	protected long userGroupId;

}