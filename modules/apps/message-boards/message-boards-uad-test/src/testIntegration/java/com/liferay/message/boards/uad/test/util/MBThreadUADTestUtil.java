/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.uad.test.util;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class MBThreadUADTestUtil {

	public static MBThread addMBThread(
			MBCategoryLocalService mbCategoryLocalService,
			MBMessageLocalService mbMessageLocalService,
			MBThreadLocalService mbThreadLocalService, long userId)
		throws Exception {

		MBCategory mbCategory = mbCategoryLocalService.addCategory(
			null, userId, 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		return addMBThread(
			mbMessageLocalService, mbThreadLocalService, userId,
			mbCategory.getCategoryId());
	}

	public static MBThread addMBThread(
			MBMessageLocalService mbMessageLocalService,
			MBThreadLocalService mbThreadLocalService, long userId,
			long parentMBCategoryId)
		throws Exception {

		MBMessage mbMessage = mbMessageLocalService.addMessage(
			userId, RandomTestUtil.randomString(), TestPropsValues.getGroupId(),
			parentMBCategoryId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		return mbThreadLocalService.getMBThread(mbMessage.getThreadId());
	}

	public static MBThread addMBThreadWithStatusByUserId(
			MBCategoryLocalService mbCategoryLocalService,
			MBMessageLocalService mbMessageLocalService,
			MBThreadLocalService mbThreadLocalService, long userId,
			long statusByUserId)
		throws Exception {

		MBThread mbThread = addMBThread(
			mbCategoryLocalService, mbMessageLocalService, mbThreadLocalService,
			userId);

		return mbThreadLocalService.updateStatus(
			statusByUserId, mbThread.getThreadId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	public static void cleanUpDependencies(
			MBCategoryLocalService mbCategoryLocalService,
			List<MBThread> mbThreads)
		throws Exception {

		for (MBThread mbThread : mbThreads) {
			long mbCategoryId = mbThread.getCategoryId();

			if (mbCategoryId ==
					MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

				continue;
			}

			mbCategoryLocalService.deleteCategory(mbCategoryId);
		}
	}

}