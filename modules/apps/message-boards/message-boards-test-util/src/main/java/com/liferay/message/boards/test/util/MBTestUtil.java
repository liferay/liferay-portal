/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.test.util;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 * @author Daniel Kocsis
 */
public class MBTestUtil {

	public static MBMessage addMessage(
			long groupId, long userId, long categoryId, String subject,
			String body, ServiceContext serviceContext)
		throws PortalException {

		return addMessage(
			userId, RandomTestUtil.randomString(), groupId, categoryId, subject,
			body, serviceContext);
	}

	public static MBMessage addMessage(
			long groupId, long userId, String subject, String body)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		return addMessage(
			groupId, userId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			subject, body, serviceContext);
	}

	public static MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, ServiceContext serviceContext)
		throws PortalException {

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();

		return MBMessageLocalServiceUtil.addMessage(
			userId, userName, groupId, categoryId, 0, 0, subject, body,
			MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs, false, 0.0,
			false, serviceContext);
	}

	public static MBMessage addMessage(String subject, String body)
		throws PortalException {

		return addMessage(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(), subject,
			body);
	}

	public static MBMessage addMessageWithWorkflow(
			long groupId, long categoryId, String subject, String body,
			boolean approved, ServiceContext serviceContext)
		throws Exception {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(true);

			serviceContext = (ServiceContext)serviceContext.clone();

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			MBMessage message = addMessage(
				groupId, serviceContext.getUserId(), categoryId, subject, body,
				serviceContext);

			if (approved) {
				return updateStatus(message, serviceContext);
			}

			return message;
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	public static List<ObjectValuePair<String, InputStream>> getInputStreamOVPs(
		String fileName, Class<?> clazz, String keywords) {

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			new ArrayList<>(1);

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		ObjectValuePair<String, InputStream> inputStreamOVP = null;

		if (Validator.isBlank(keywords)) {
			inputStreamOVP = new ObjectValuePair<>(fileName, inputStream);
		}
		else {
			inputStreamOVP = new ObjectValuePair<>(keywords, inputStream);
		}

		inputStreamOVPs.add(inputStreamOVP);

		return inputStreamOVPs;
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

	protected static MBMessage updateStatus(
			MBMessage message, ServiceContext serviceContext)
		throws Exception {

		return MBMessageLocalServiceUtil.updateStatus(
			message.getUserId(), message.getMessageId(),
			WorkflowConstants.STATUS_APPROVED, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_URL, "http://localhost"
			).build());
	}

}