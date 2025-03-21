/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.helper;

import com.liferay.commerce.frontend.helper.HeaderHelper;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.util.CommerceWorkflowedModelHelper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;

import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = HeaderHelper.class)
public class HeaderHelperImpl implements HeaderHelper {

	@Override
	public WorkflowTask getReviewWorkflowTask(
			long companyId, long userId, long beanId, String className)
		throws PortalException {

		String[] assetTypes = null;

		if (Validator.isNotNull(className)) {
			assetTypes = new String[] {className};
		}

		List<WorkflowTask> workflowTasks = _workflowTaskManager.search(
			companyId, userId, null, new String[] {"review"}, assetTypes,
			new Long[] {beanId}, null, null, null, null, false, null, null,
			null, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (workflowTasks.size() == 1) {
			return workflowTasks.get(0);
		}

		return null;
	}

	@Override
	public List<HeaderActionModel> getWorkflowTransitionHeaderActionModels(
			long userId, long companyId, String className, long beanId,
			PortletURL transitionPortletURL)
		throws PortalException {

		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		List<ObjectValuePair<Long, String>> workflowTransitionObjectValuePairs =
			_commerceWorkflowedModelHelper.getWorkflowTransitions(
				userId, companyId, className, beanId);

		HeaderActionModel headerActionModel;

		for (ObjectValuePair<Long, String> workflowTransitionObjectValuePair :
				workflowTransitionObjectValuePairs) {

			String transitionName =
				workflowTransitionObjectValuePair.getValue();

			transitionPortletURL.setParameter("transitionName", transitionName);

			transitionPortletURL.setParameter(
				"workflowTaskId",
				String.valueOf(workflowTransitionObjectValuePair.getKey()));

			String additionalClasses = null;

			if (transitionName.equals("approve")) {
				additionalClasses = "btn-primary";
			}

			headerActionModel = new HeaderActionModel(
				additionalClasses, null, transitionPortletURL.toString(), null,
				transitionName);

			headerActionModels.add(headerActionModel);
		}

		return headerActionModels;
	}

	@Reference
	private CommerceWorkflowedModelHelper _commerceWorkflowedModelHelper;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}