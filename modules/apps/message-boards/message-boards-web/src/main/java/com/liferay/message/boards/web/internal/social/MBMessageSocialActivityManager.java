/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.social;

import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBDiscussionLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.social.BaseSocialActivityManager;
import com.liferay.portal.kernel.social.SocialActivityManager;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.service.SocialActivityLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBMessage",
	service = SocialActivityManager.class
)
public class MBMessageSocialActivityManager
	extends BaseSocialActivityManager<MBMessage> {

	@Override
	public void deleteActivities(MBMessage message) throws PortalException {
		_deleteDiscussionSocialActivities(message.getClassName(), message);
	}

	@Override
	protected SocialActivityLocalService getSocialActivityLocalService() {
		return _socialActivityLocalService;
	}

	private void _deleteDiscussionSocialActivities(
			String className, MBMessage message)
		throws PortalException {

		MBDiscussion discussion = _mbDiscussionLocalService.getThreadDiscussion(
			message.getThreadId());

		long classNameId = _classNameLocalService.getClassNameId(className);

		if (discussion.getClassNameId() != classNameId) {
			return;
		}

		List<SocialActivity> socialActivities =
			_socialActivityLocalService.getActivities(
				0, className, discussion.getClassPK(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (SocialActivity socialActivity : socialActivities) {
			if (Validator.isNull(socialActivity.getExtraData())) {
				continue;
			}

			JSONObject extraDataJSONObject = _jsonFactory.createJSONObject(
				socialActivity.getExtraData());

			long extraDataMessageId = extraDataJSONObject.getLong("messageId");

			if (message.getMessageId() != extraDataMessageId) {
				continue;
			}

			_socialActivityLocalService.deleteActivity(
				socialActivity.getActivityId());
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MBDiscussionLocalService _mbDiscussionLocalService;

	@Reference
	private SocialActivityLocalService _socialActivityLocalService;

}