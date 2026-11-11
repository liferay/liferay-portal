/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.taglib.internal.display.context;

import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.taglib.internal.constants.WorkflowStatusConstants;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Feliphe Marinho
 */
public class WorkflowStatusDisplayContext {

	public Map<String, Object> getData(
		HttpServletRequest httpServletRequest, Locale locale) {

		boolean showInstanceTracker = _isShowInstanceTracker(
			httpServletRequest);

		ResourceBundle resourceBundle = TagResourceBundleUtil.getResourceBundle(
			httpServletRequest, locale);

		return HashMapBuilder.<String, Object>put(
			"id", HtmlUtil.escape(_getId(httpServletRequest))
		).put(
			"idLabel", LanguageUtil.get(resourceBundle, "id")
		).put(
			"instanceId",
			() -> {
				if (!showInstanceTracker) {
					return null;
				}

				return _getInstanceId(httpServletRequest);
			}
		).put(
			"showInstanceTracker", showInstanceTracker
		).put(
			"showStatusLabel", _isShowStatusLabel(httpServletRequest)
		).put(
			"statusLabel", LanguageUtil.get(resourceBundle, "status")
		).put(
			"statusMessage",
			HtmlUtil.escape(
				LanguageUtil.get(
					resourceBundle, getStatusMessage(httpServletRequest)))
		).put(
			"statusStyle",
			WorkflowConstants.getStatusStyle(getStatus(httpServletRequest))
		).put(
			"version", _getVersion(httpServletRequest)
		).put(
			"versionLabel", LanguageUtil.get(resourceBundle, "version")
		).build();
	}

	protected Integer getStatus(HttpServletRequest httpServletRequest) {
		Object bean = _getBean(httpServletRequest);

		if (bean instanceof WorkflowableComment workflowableComment) {
			return workflowableComment.getStatus();
		}

		if (bean instanceof WorkflowedModel workflowedModel) {
			return workflowedModel.getStatus();
		}

		return GetterUtil.getInteger(
			_getAttribute("status", httpServletRequest));
	}

	protected String getStatusMessage(HttpServletRequest httpServletRequest) {
		if (Validator.isNotNull(
				GetterUtil.getString(
					_getAttribute("statusMessage", httpServletRequest)))) {

			return GetterUtil.getString(
				_getAttribute("statusMessage", httpServletRequest));
		}

		return WorkflowConstants.getStatusLabel(getStatus(httpServletRequest));
	}

	private Object _getAttribute(
		String attribute, HttpServletRequest httpServletRequest) {

		return httpServletRequest.getAttribute(
			WorkflowStatusConstants.ATTRIBUTE_NAMESPACE + attribute);
	}

	private Object _getBean(HttpServletRequest httpServletRequest) {
		return _getAttribute("bean", httpServletRequest);
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

	private String _getId(HttpServletRequest httpServletRequest) {
		return GetterUtil.getString(_getAttribute("id", httpServletRequest));
	}

	private Long _getInstanceId(HttpServletRequest httpServletRequest) {
		Object bean = _getBean(httpServletRequest);
		Class<?> modelClass = _getModelClass(httpServletRequest);

		if ((bean == null) || (modelClass == null)) {
			return null;
		}

		try {
			WorkflowInstanceLink workflowInstanceLink =
				WorkflowInstanceLinkLocalServiceUtil.getWorkflowInstanceLink(
					_getCompanyId(bean), _getGroupId(bean),
					modelClass.getName(), _getClassPK(bean));

			return workflowInstanceLink.getWorkflowInstanceId();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private Class<?> _getModelClass(HttpServletRequest httpServletRequest) {
		return (Class<?>)_getAttribute("modelClass", httpServletRequest);
	}

	private String _getVersion(HttpServletRequest httpServletRequest) {
		return GetterUtil.getString(
			_getAttribute("version", httpServletRequest));
	}

	private boolean _isShowInstanceTracker(
		HttpServletRequest httpServletRequest) {

		return GetterUtil.getBoolean(
			_getAttribute("showInstanceTracker", httpServletRequest), true);
	}

	private boolean _isShowStatusLabel(HttpServletRequest httpServletRequest) {
		return GetterUtil.getBoolean(
			_getAttribute("showStatusLabel", httpServletRequest), true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowStatusDisplayContext.class);

}