/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.aui.base.BaseWorkflowStatusTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *          com.liferay.portal.workflow.taglib.servlet.taglib.WorkflowStatusTag}
 */
@Deprecated
public class WorkflowStatusTag extends BaseWorkflowStatusTag {

	@Override
	protected String getPage() {
		return "/html/taglib/aui/workflow_status/page.jsp";
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		Object bean = getBean();

		if (bean == null) {
			bean = pageContext.getAttribute("aui:model-context:bean");
		}

		String helpMessage = getHelpMessage();

		if (Validator.isNull(helpMessage) &&
			(getStatus() == WorkflowConstants.STATUS_APPROVED) &&
			Validator.isNotNull(getVersion())) {

			helpMessage = _HELP_MESSAGE_DEFAULT;
		}

		Class<?> model = getModel();

		if (model == null) {
			model = (Class<?>)pageContext.getAttribute(
				"aui:model-context:model");
		}

		setNamespacedAttribute(httpServletRequest, "bean", bean);
		setNamespacedAttribute(httpServletRequest, "helpMessage", helpMessage);
		setNamespacedAttribute(httpServletRequest, "model", model);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _HELP_MESSAGE_DEFAULT =
		"a-new-version-is-created-automatically-if-this-content-is-modified";

}