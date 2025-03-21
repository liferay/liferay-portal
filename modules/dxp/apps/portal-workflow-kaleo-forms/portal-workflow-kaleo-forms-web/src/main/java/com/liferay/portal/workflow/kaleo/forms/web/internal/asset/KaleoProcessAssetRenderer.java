/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.asset;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.dynamic.data.lists.constants.DDLWebKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsActionKeys;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsWebKeys;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;
import com.liferay.portal.workflow.kaleo.forms.service.KaleoProcessLinkLocalService;
import com.liferay.portal.workflow.kaleo.forms.service.permission.KaleoProcessPermission;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Inácio Nery
 */
public class KaleoProcessAssetRenderer
	extends BaseJSPAssetRenderer<KaleoProcess> {

	public KaleoProcessAssetRenderer(
		KaleoProcess kaleoProcess, DDLRecord ddlRecord,
		DDLRecordVersion ddlRecordVersion) {

		_kaleoProcess = kaleoProcess;
		_ddlRecord = ddlRecord;
		_ddlRecordVersion = ddlRecordVersion;
	}

	@Override
	public KaleoProcess getAssetObject() {
		return _kaleoProcess;
	}

	@Override
	public AssetRendererFactory<KaleoProcess> getAssetRendererFactory() {
		return new KaleoProcessAssetRendererFactory();
	}

	@Override
	public String getClassName() {
		return DDLRecord.class.getName();
	}

	@Override
	public long getClassPK() {
		return _ddlRecord.getRecordId();
	}

	@Override
	public long getGroupId() {
		return _kaleoProcess.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/asset/full_content.jsp";
		}

		return null;
	}

	@Override
	public int getStatus() {
		return _ddlRecordVersion.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		String kaleoProcessName = StringPool.BLANK;

		try {
			kaleoProcessName = _kaleoProcess.getName(locale);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return kaleoProcessName;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/admin/edit_record.jsp"
		).setParameter(
			"ddlRecordId", _ddlRecord.getRecordId()
		).setParameter(
			"kaleoProcessId", _kaleoProcess.getKaleoProcessId()
		).buildPortletURL();
	}

	@Override
	public long getUserId() {
		return _kaleoProcess.getUserId();
	}

	@Override
	public String getUserName() {
		return _kaleoProcess.getUserName();
	}

	@Override
	public String getUuid() {
		return _kaleoProcess.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker) {
		return KaleoProcessPermission.contains(
			permissionChecker, _kaleoProcess,
			KaleoFormsActionKeys.COMPLETE_FORM);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		return KaleoProcessPermission.contains(
			permissionChecker, _kaleoProcess,
			KaleoFormsActionKeys.COMPLETE_FORM);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD, _ddlRecord);
		httpServletRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD_VERSION, _ddlRecordVersion);
		httpServletRequest.setAttribute(
			KaleoFormsWebKeys.KALEO_PROCESS, _kaleoProcess);

		KaleoProcessLink kaleoProcessLink = _fetchKaleoProcessLink(
			httpServletRequest);

		httpServletRequest.setAttribute(
			KaleoFormsWebKeys.KALEO_PROCESS_LINK, kaleoProcessLink);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	protected void setKaleoProcessLinkLocalService(
		KaleoProcessLinkLocalService kaleoProcessLinkLocalService) {

		_kaKaleoProcessLinkLocalService = kaleoProcessLinkLocalService;
	}

	private KaleoProcessLink _fetchKaleoProcessLink(
			HttpServletRequest httpServletRequest)
		throws Exception {

		KaleoProcessLink kaleoProcessLink = null;

		WorkflowTask workflowTask = _getWorkflowTask(httpServletRequest);

		if (workflowTask != null) {
			kaleoProcessLink =
				_kaKaleoProcessLinkLocalService.fetchKaleoProcessLink(
					_kaleoProcess.getKaleoProcessId(), workflowTask.getName());
		}

		return kaleoProcessLink;
	}

	private WorkflowTask _getWorkflowTask(HttpServletRequest httpServletRequest)
		throws Exception {

		WorkflowTask workflowTask = null;

		long workflowTaskId = ParamUtil.getLong(
			httpServletRequest, "workflowTaskId");

		if (workflowTaskId > 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			workflowTask = WorkflowTaskManagerUtil.getWorkflowTask(
				themeDisplay.getCompanyId(), workflowTaskId);
		}

		return workflowTask;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoProcessAssetRenderer.class);

	private final DDLRecord _ddlRecord;
	private final DDLRecordVersion _ddlRecordVersion;
	private KaleoProcessLinkLocalService _kaKaleoProcessLinkLocalService;
	private final KaleoProcess _kaleoProcess;

}