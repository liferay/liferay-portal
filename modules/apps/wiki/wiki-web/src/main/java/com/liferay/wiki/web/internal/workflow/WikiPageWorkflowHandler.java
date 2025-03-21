/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.workflow;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Julio Camarero
 */
@Component(
	property = "model.class.name=com.liferay.wiki.model.WikiPage",
	service = WorkflowHandler.class
)
public class WikiPageWorkflowHandler extends BaseWorkflowHandler<WikiPage> {

	@Override
	public void contributeWorkflowContext(
		Map<String, Serializable> workflowContext) {

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		if (serviceContext.getRequest() == null) {
			return;
		}

		PortletURL portletURL = null;

		if (serviceContext.getPlid() == LayoutConstants.DEFAULT_PLID) {
			portletURL = _portal.getControlPanelPortletURL(
				serviceContext.getRequest(), WikiPortletKeys.WIKI_ADMIN,
				PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = _portletURLFactory.create(
				serviceContext.getRequest(), WikiPortletKeys.WIKI,
				serviceContext.getPlid(), PortletRequest.RENDER_PHASE);
		}

		serviceContext.setAttribute("baseDiffsURL", portletURL.toString());
	}

	@Override
	public String getClassName() {
		return WikiPage.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public boolean isVisible(Group group) {
		return FeatureFlagManagerUtil.isEnabled(
			group.getCompanyId(), "LPD-35013");
	}

	@Override
	public WikiPage updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		WikiPage page = _wikiPageLocalService.getPageByPageId(classPK);

		return updateStatus(page, status, workflowContext);
	}

	@Override
	public WikiPage updateStatus(
			WikiPage page, int status,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		return _wikiPageLocalService.updateStatus(
			userId, page, status, serviceContext, workflowContext);
	}

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}