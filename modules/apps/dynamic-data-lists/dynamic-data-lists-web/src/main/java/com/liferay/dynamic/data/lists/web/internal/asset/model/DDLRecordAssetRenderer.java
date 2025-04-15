/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.kernel.model.DDMFormValuesReader;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLWebKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.web.internal.asset.DDLRecordDDMFormValuesReader;
import com.liferay.dynamic.data.lists.web.internal.security.permission.resource.DDLRecordSetPermission;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Marcellus Tavares
 * @author Sergio González
 */
public class DDLRecordAssetRenderer extends BaseJSPAssetRenderer<DDLRecord> {

	public DDLRecordAssetRenderer(
		DDLRecord record, DDLRecordVersion recordVersion) {

		_record = record;
		_recordVersion = recordVersion;

		DDMStructure ddmStructure = null;
		DDLRecordSet recordSet = null;

		try {
			recordSet = record.getRecordSet();

			ddmStructure = recordSet.getDDMStructure();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		_ddmStructure = ddmStructure;
		_recordSet = recordSet;
	}

	@Override
	public DDLRecord getAssetObject() {
		return _record;
	}

	@Override
	public AssetRendererFactory<DDLRecord> getAssetRendererFactory() {
		return new DDLRecordAssetRendererFactory();
	}

	@Override
	public String getClassName() {
		return DDLRecord.class.getName();
	}

	@Override
	public long getClassPK() {
		return _record.getRecordId();
	}

	@Override
	public DDMFormValuesReader getDDMFormValuesReader() {
		return new DDLRecordDDMFormValuesReader(_record);
	}

	@Override
	public long getGroupId() {
		return _record.getGroupId();
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
		return _recordVersion.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		String ddmStructureName = _ddmStructure.getName(locale);

		String recordSetName = _recordSet.getName(locale);

		return LanguageUtil.format(
			locale, "new-x-for-list-x",
			new Object[] {ddmStructureName, recordSetName}, false);
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(_record.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_record.jsp"
		).setParameter(
			"recordId", _record.getRecordId()
		).setParameter(
			"version", _recordVersion.getVersion()
		).buildPortletURL();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		return noSuchEntryRedirect;
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		return noSuchEntryRedirect;
	}

	@Override
	public long getUserId() {
		return _recordVersion.getUserId();
	}

	@Override
	public String getUserName() {
		return _recordVersion.getUserName();
	}

	@Override
	public String getUuid() {
		return _record.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return DDLRecordSetPermission.contains(
			permissionChecker, _recordSet, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return DDLRecordSetPermission.contains(
			permissionChecker, _recordSet, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD, _record);
		httpServletRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD_VERSION, _recordVersion);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDLRecordAssetRenderer.class);

	private final DDMStructure _ddmStructure;
	private final DDLRecord _record;
	private final DDLRecordSet _recordSet;
	private final DDLRecordVersion _recordVersion;

}