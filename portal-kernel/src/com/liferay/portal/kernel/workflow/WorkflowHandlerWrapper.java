/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class WorkflowHandlerWrapper<T> implements WorkflowHandler<T> {

	public WorkflowHandlerWrapper(WorkflowHandler<T> workflowHandler) {
		_workflowHandler = workflowHandler;
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(long classPK)
		throws PortalException {

		return _workflowHandler.getAssetRenderer(classPK);
	}

	@Override
	public AssetRendererFactory<T> getAssetRendererFactory() {
		return _workflowHandler.getAssetRendererFactory();
	}

	@Override
	public String getClassName() {
		return _workflowHandler.getClassName();
	}

	@Override
	public long getDiscussionClassPK(
		Map<String, Serializable> workflowContext) {

		return _workflowHandler.getDiscussionClassPK(workflowContext);
	}

	@Override
	public String getIconCssClass() {
		return _workflowHandler.getIconCssClass();
	}

	@Override
	public String getNotificationLink(
			long workflowTaskId, ServiceContext serviceContext)
		throws PortalException {

		return _workflowHandler.getNotificationLink(
			workflowTaskId, serviceContext);
	}

	@Override
	public String getSummary(
		long classPK, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		return _workflowHandler.getSummary(
			classPK, portletRequest, portletResponse);
	}

	@Override
	public String getTitle(long classPK, Locale locale) {
		return _workflowHandler.getTitle(classPK, locale);
	}

	@Override
	public String getType(Locale locale) {
		return _workflowHandler.getType(locale);
	}

	@Override
	public PortletURL getURLEdit(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return _workflowHandler.getURLEdit(
			classPK, liferayPortletRequest, liferayPortletResponse);
	}

	@Override
	public PortletURL getURLViewDiffs(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return _workflowHandler.getURLViewDiffs(
			classPK, liferayPortletRequest, liferayPortletResponse);
	}

	@Override
	public String getURLViewInContext(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		return _workflowHandler.getURLViewInContext(
			classPK, liferayPortletRequest, liferayPortletResponse,
			noSuchEntryRedirect);
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		return _workflowHandler.getWorkflowDefinitionLink(
			companyId, groupId, classPK);
	}

	@Override
	public boolean include(
		long classPK, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String template) {

		return _workflowHandler.include(
			classPK, httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isAssetTypeSearchable() {
		return _workflowHandler.isAssetTypeSearchable();
	}

	@Override
	public boolean isCommentable() {
		return _workflowHandler.isCommentable();
	}

	@Override
	public boolean isScopeable() {
		return _workflowHandler.isScopeable();
	}

	@Override
	public boolean isVisible() {
		return _workflowHandler.isVisible();
	}

	@Override
	public boolean isVisible(Group group) {
		return _workflowHandler.isVisible(group);
	}

	@Override
	public void startWorkflowInstance(
			long companyId, long groupId, long userId, long classPK, T model,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		_workflowHandler.startWorkflowInstance(
			companyId, groupId, userId, classPK, model, workflowContext);
	}

	@Override
	public T updateStatus(int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		return _workflowHandler.updateStatus(status, workflowContext);
	}

	@Override
	public T updateStatus(
			T model, int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		return _workflowHandler.updateStatus(model, status, workflowContext);
	}

	private final WorkflowHandler<T> _workflowHandler;

}