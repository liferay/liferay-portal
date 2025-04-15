/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.social;

import com.liferay.microblogs.constants.MicroblogsEntryConstants;
import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.MicroblogsEntryLocalService;
import com.liferay.microblogs.web.internal.util.MicroblogsWebUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan Lee
 */
@Component(
	property = "jakarta.portlet.name=" + MicroblogsPortletKeys.MICROBLOGS,
	service = SocialActivityInterpreter.class
)
public class MicroblogsActivityInterpreter
	extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getBody(
		SocialActivity activity, ServiceContext serviceContext) {

		return getUserName(activity.getUserId(), serviceContext);
	}

	@Override
	protected String getLink(
		SocialActivity activity, ServiceContext serviceContext) {

		return StringPool.BLANK;
	}

	@Override
	protected String getTitle(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		MicroblogsEntry microblogsEntry =
			_microblogsEntryLocalService.getMicroblogsEntry(
				activity.getClassPK());

		if (activity.getReceiverUserId() > 0) {
			String receiverUserName = getUserName(
				activity.getReceiverUserId(), serviceContext);

			if (microblogsEntry.getType() ==
					MicroblogsEntryConstants.TYPE_REPLY) {

				sb.append("@");
				sb.append(receiverUserName);
				sb.append(": ");
			}
			else if (microblogsEntry.getType() ==
						MicroblogsEntryConstants.TYPE_REPOST) {

				sb.append(serviceContext.translate("reposted-from"));
				sb.append(" ");
				sb.append(receiverUserName);
				sb.append(": ");
			}
		}

		sb.append(
			MicroblogsWebUtil.getProcessedContent(
				HtmlUtil.replaceNewLine(microblogsEntry.getContent()),
				serviceContext));

		return sb.toString();
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		return _microblogsEntryModelResourcePermission.contains(
			permissionChecker, activity.getClassPK(), ActionKeys.VIEW);
	}

	private static final String[] _CLASS_NAMES = {
		MicroblogsEntry.class.getName()
	};

	@Reference
	private MicroblogsEntryLocalService _microblogsEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.microblogs.model.MicroblogsEntry)"
	)
	private ModelResourcePermission<MicroblogsEntry>
		_microblogsEntryModelResourcePermission;

}