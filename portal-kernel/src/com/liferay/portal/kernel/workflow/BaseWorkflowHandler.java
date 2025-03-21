/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

/**
 * @author Bruno Farache
 * @author Marcellus Tavares
 * @author Juan Fernández
 * @author Julio Camarero
 * @author Jorge Ferrer
 */
public abstract class BaseWorkflowHandler<T> implements WorkflowHandler<T> {

	@Override
	public AssetRenderer<T> getAssetRenderer(long classPK)
		throws PortalException {

		AssetRendererFactory<T> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory != null) {
			return assetRendererFactory.getAssetRenderer(
				classPK, AssetRendererFactory.TYPE_LATEST);
		}

		return null;
	}

	@Override
	public AssetRendererFactory<T> getAssetRendererFactory() {
		return (AssetRendererFactory<T>)
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				getClassName());
	}

	@Override
	public String getIconCssClass() {
		AssetRendererFactory<?> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory != null) {
			return assetRendererFactory.getIconCssClass();
		}

		return StringPool.BLANK;
	}

	@Override
	public String getNotificationLink(
			long workflowTaskId, ServiceContext serviceContext)
		throws PortalException {

		try {
			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			if (!StringUtil.matches(layout.getFriendlyURL(), "/manage")) {
				layout = LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
					layout.getGroupId(), false, "/manage");
			}

			if (layout == null) {
				layout = LayoutLocalServiceUtil.fetchLayout(
					themeDisplay.getPlid());
			}

			return PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					serviceContext.getRequest(), PortletKeys.MY_WORKFLOW_TASK,
					layout.getPlid(), PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/edit_workflow_task.jsp"
			).setBackURL(
				themeDisplay.getURLCurrent()
			).setParameter(
				"workflowTaskId", String.valueOf(workflowTaskId)
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public String getSummary(
		long classPK, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		try {
			AssetRenderer<?> assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.getSummary(
					portletRequest, portletResponse);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public String getTitle(long classPK, Locale locale) {
		try {
			AssetRenderer<?> assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.getTitle(locale);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public PortletURL getURLEdit(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		try {
			AssetRenderer<?> assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.getURLEdit(
					liferayPortletRequest, liferayPortletResponse);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public PortletURL getURLViewDiffs(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		try {
			AssetRenderer<?> assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.getURLViewDiffs(
					liferayPortletRequest, liferayPortletResponse);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public String getURLViewInContext(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		try {
			AssetRenderer<?> assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.getURLViewInContext(
					liferayPortletRequest, liferayPortletResponse,
					noSuchEntryRedirect);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		return WorkflowDefinitionLinkLocalServiceUtil.
			fetchWorkflowDefinitionLink(
				companyId, groupId, getClassName(), 0, 0);
	}

	@Override
	public boolean include(
		long classPK, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String template) {

		try {
			AssetRenderer<?> assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.include(
					httpServletRequest, httpServletResponse, template);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return false;
	}

	@Override
	public boolean isAssetTypeSearchable() {
		return _ASSET_TYPE_SEARCHABLE;
	}

	@Override
	public boolean isScopeable() {
		return _SCOPEABLE;
	}

	@Override
	public boolean isVisible() {
		return _VISIBLE;
	}

	@Override
	public void startWorkflowInstance(
			long companyId, long groupId, long userId, long classPK, T model,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		WorkflowInstanceLinkLocalServiceUtil.startWorkflowInstance(
			companyId, groupId, userId, getClassName(), classPK,
			workflowContext);
	}

	private static final boolean _ASSET_TYPE_SEARCHABLE = true;

	private static final boolean _SCOPEABLE = true;

	private static final boolean _VISIBLE = true;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseWorkflowHandler.class);

}