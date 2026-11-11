/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.instance.tracker.web.internal.url.provider;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.instance.tracker.url.provider.WorkflowInstanceTrackerURLProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = WorkflowInstanceTrackerURLProvider.class)
public class WorkflowInstanceTrackerURLProviderImpl
	implements WorkflowInstanceTrackerURLProvider {

	@Override
	public String getURL(
		Object bean, HttpServletRequest httpServletRequest, Class<?> modelClass,
		boolean useDialog) {

		String portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null,
				WorkflowPortletKeys.WORKFLOW_INSTANCE_TRACKER, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setParameter(
			"instanceId",
			() -> {
				try {
					WorkflowInstanceLink workflowInstanceLink =
						_workflowInstanceLinkLocalService.
							getWorkflowInstanceLink(
								_getCompanyId(bean), _getGroupId(bean),
								modelClass.getName(), _getClassPK(bean));

					return workflowInstanceLink.getWorkflowInstanceId();
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}

				return null;
			}
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();

		if (useDialog) {
			return StringBundler.concat(
				"javascript:",
				"Liferay.Util.openModal({iframeBodyCssClass: '', title: '",
				UnicodeLanguageUtil.get(httpServletRequest, "track-workflow"),
				"', url: '", portletURL, "'});;");
		}

		return portletURL;
	}

	private long _getClassPK(Object bean) {
		if (bean instanceof ClassedModel classedModel) {
			return GetterUtil.getLong(classedModel.getPrimaryKeyObj());
		}

		if (bean instanceof WorkflowableComment workflowableComment) {
			return workflowableComment.getPrimaryKey();
		}

		return 0;
	}

	private long _getCompanyId(Object bean) {
		if (bean instanceof AuditedModel auditedModel) {
			return auditedModel.getCompanyId();
		}

		if (bean instanceof ShardedModel shardedModel) {
			return shardedModel.getCompanyId();
		}

		if (bean instanceof WorkflowableComment workflowableComment) {
			return workflowableComment.getCompanyId();
		}

		return 0;
	}

	private long _getGroupId(Object bean) {
		if (bean instanceof GroupedModel groupedModel) {
			return groupedModel.getGroupId();
		}

		if (bean instanceof WorkflowableComment workflowableComment) {
			return workflowableComment.getGroupId();
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowInstanceTrackerURLProviderImpl.class);

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}