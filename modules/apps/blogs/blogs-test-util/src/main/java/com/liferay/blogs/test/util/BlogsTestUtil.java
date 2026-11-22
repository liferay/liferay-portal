/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.test.util;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;

/**
 * @author Zsolt Berentey
 */
public class BlogsTestUtil {

	public static BlogsEntry addEntry(
			long userId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		return BlogsEntryLocalServiceUtil.addEntry(
			userId, title, StringPool.BLANK, StringPool.BLANK, content,
			new Date(), true, true, new String[0], StringPool.BLANK, null, null,
			serviceContext);
	}

	public static BlogsEntry addEntryWithWorkflow(
			long userId, String title, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(true);

			Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
				2012, 1, 1);

			serviceContext = (ServiceContext)serviceContext.clone();

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			BlogsEntry entry = BlogsEntryLocalServiceUtil.addEntry(
				userId, title, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				displayCalendar.getTime(), true, true, new String[0],
				StringPool.BLANK, null, null, serviceContext);

			if (approved) {
				return updateStatus(entry, serviceContext);
			}

			return entry;
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	public static void assertEquals(
		BlogsEntry expectedEntry, BlogsEntry actualEntry) {

		Assert.assertEquals(expectedEntry.getUserId(), actualEntry.getUserId());
		Assert.assertEquals(expectedEntry.getTitle(), actualEntry.getTitle());
		Assert.assertEquals(
			expectedEntry.getDescription(), actualEntry.getDescription());
		Assert.assertEquals(
			expectedEntry.getContent(), actualEntry.getContent());
		Assert.assertEquals(
			expectedEntry.getDisplayDate(), actualEntry.getDisplayDate());
		Assert.assertEquals(
			expectedEntry.isAllowPingbacks(), actualEntry.isAllowPingbacks());
		Assert.assertEquals(
			expectedEntry.isAllowTrackbacks(), actualEntry.isAllowTrackbacks());
		Assert.assertEquals(
			expectedEntry.isSmallImage(), actualEntry.isSmallImage());
		Assert.assertEquals(
			expectedEntry.getCoverImageFileEntryId(),
			actualEntry.getCoverImageFileEntryId());
	}

	public static String getTempBlogsEntryAttachmentFileEntryImgTag(
		long dataImageId, String url) {

		return StringBundler.concat(
			"<img ", EditorConstants.ATTRIBUTE_DATA_IMAGE_ID, "=\"",
			dataImageId, "\" src=\"", url, "\"/>");
	}

	public static void populateNotificationsServiceContext(
			ServiceContext serviceContext, String command)
		throws Exception {

		serviceContext.setAttribute("entryURL", "http://localhost");

		if (Validator.isNotNull(command)) {
			serviceContext.setCommand(command);
		}

		serviceContext.setLayoutFullURL("http://localhost");
	}

	protected static BlogsEntry updateStatus(
			BlogsEntry entry, ServiceContext serviceContext)
		throws Exception {

		return BlogsEntryLocalServiceUtil.updateStatus(
			entry.getUserId(), entry.getEntryId(),
			WorkflowConstants.STATUS_APPROVED, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_URL, "http://localhost"
			).put(
				WorkflowConstants.CONTEXT_USER_PORTRAIT_URL, "http://localhost"
			).put(
				WorkflowConstants.CONTEXT_USER_URL, "http://localhost"
			).build());
	}

}