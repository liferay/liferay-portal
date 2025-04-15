/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.social;

import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialRelationConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Hai Yu
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = "jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER,
	service = SocialActivityInterpreter.class
)
@Deprecated
public class ContactsCenterActivityInterpreter
	extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getLink(
		SocialActivity activity, ServiceContext serviceContext) {

		return getUserName(activity.getReceiverUserId(), serviceContext);
	}

	@Override
	protected Object[] getTitleArguments(
		String groupName, SocialActivity activity, String link, String title,
		ServiceContext serviceContext) {

		if (activity.getType() != SocialRelationConstants.TYPE_BI_CONNECTION) {
			return new Object[0];
		}

		String creatorUserName = getUserName(
			activity.getUserId(), serviceContext);
		String receiverUserName = getUserName(
			activity.getReceiverUserId(), serviceContext);

		return new Object[] {creatorUserName, receiverUserName};
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		if (activity.getType() == SocialRelationConstants.TYPE_BI_CONNECTION) {
			return "activity-social-networking-summary-add-connection";
		}

		return StringPool.BLANK;
	}

	@Override
	protected boolean hasPermissions(
		PermissionChecker permissionChecker, SocialActivity activity,
		String actionId, ServiceContext serviceContext) {

		return true;
	}

	private static final String[] _CLASS_NAMES = {User.class.getName()};

}