/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormViewFormInstanceRecordDisplayContext;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Leonardo Barros
 */
public class DDMFormAssetRenderer
	extends BaseJSPAssetRenderer<DDMFormInstanceRecord> {

	public DDMFormAssetRenderer(
		DDMFormInstanceRecord ddmFormInstanceRecord,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		ModelResourcePermission<DDMFormInstanceRecord>
			ddmFormInstanceRecordModelResourcePermission,
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion,
		DDMFormInstanceVersionLocalService ddmFormInstanceVersionLocalService,
		DDMFormRenderer ddmFormRenderer,
		DDMFormValuesFactory ddmFormValuesFactory,
		DDMFormValuesMerger ddmFormValuesMerger, Portal portal) {

		_ddmFormInstanceRecord = ddmFormInstanceRecord;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmFormInstanceRecordModelResourcePermission =
			ddmFormInstanceRecordModelResourcePermission;
		_ddmFormInstanceRecordVersion = ddmFormInstanceRecordVersion;
		_ddmFormInstanceVersionLocalService =
			ddmFormInstanceVersionLocalService;
		_ddmFormRenderer = ddmFormRenderer;
		_ddmFormValuesFactory = ddmFormValuesFactory;
		_ddmFormValuesMerger = ddmFormValuesMerger;
		_portal = portal;

		DDMFormInstance ddmFormInstance = null;

		try {
			ddmFormInstance = ddmFormInstanceRecordVersion.getFormInstance();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		_ddmFormInstance = ddmFormInstance;
	}

	@Override
	public DDMFormInstanceRecord getAssetObject() {
		return _ddmFormInstanceRecord;
	}

	@Override
	public AssetRendererFactory<DDMFormInstanceRecord>
		getAssetRendererFactory() {

		return new DDMFormAssetRendererFactory();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return _ddmFormInstance.getAvailableLanguageIds();
	}

	@Override
	public String getClassName() {
		return DDMFormInstanceRecord.class.getName();
	}

	@Override
	public long getClassPK() {
		return _ddmFormInstanceRecord.getFormInstanceRecordId();
	}

	@Override
	public long getGroupId() {
		return _ddmFormInstanceRecord.getGroupId();
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
		return _ddmFormInstanceRecordVersion.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return LanguageUtil.format(
			locale, "form-record-for-form-x", _ddmFormInstance.getName(locale),
			false);
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		HttpServletRequest httpServletRequest =
			liferayPortletRequest.getHttpServletRequest();

		String portletNamespace = DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM;

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, portletNamespace,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/display/edit_form_instance_record.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"defaultLanguageId",
			() -> {
				DDMFormValues ddmFormValues =
					_ddmFormInstanceRecordVersion.getDDMFormValues();

				return LocaleUtil.toLanguageId(
					ddmFormValues.getDefaultLocale());
			}
		).setParameter(
			"formInstanceId", _ddmFormInstanceRecord.getFormInstanceId()
		).setParameter(
			"formInstanceRecordId",
			_ddmFormInstanceRecord.getFormInstanceRecordId()
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
		return _ddmFormInstanceRecord.getUserId();
	}

	@Override
	public String getUserName() {
		return _ddmFormInstanceRecord.getUserName();
	}

	@Override
	public String getUuid() {
		return _ddmFormInstanceRecord.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		try {
			if (_ddmFormInstanceRecordModelResourcePermission.contains(
					permissionChecker, _ddmFormInstanceRecord,
					ActionKeys.UPDATE) ||
				_ddmFormInstanceRecordModelResourcePermission.contains(
					permissionChecker, _ddmFormInstanceRecord,
					DDMActionKeys.ADD_FORM_INSTANCE_RECORD)) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		try {
			return _ddmFormInstanceRecordModelResourcePermission.contains(
				permissionChecker, _ddmFormInstanceRecord, ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_RECORD,
			_ddmFormInstanceRecord);

		DDMFormViewFormInstanceRecordDisplayContext
			ddmFormViewFormInstanceRecordDisplayContext =
				new DDMFormViewFormInstanceRecordDisplayContext(
					httpServletRequest, httpServletResponse,
					_ddmFormInstanceRecordLocalService,
					_ddmFormInstanceVersionLocalService, _ddmFormRenderer,
					_ddmFormValuesFactory, _ddmFormValuesMerger);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			ddmFormViewFormInstanceRecordDisplayContext);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormAssetRenderer.class);

	private final DDMFormInstance _ddmFormInstance;
	private final DDMFormInstanceRecord _ddmFormInstanceRecord;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final ModelResourcePermission<DDMFormInstanceRecord>
		_ddmFormInstanceRecordModelResourcePermission;
	private final DDMFormInstanceRecordVersion _ddmFormInstanceRecordVersion;
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final DDMFormValuesMerger _ddmFormValuesMerger;
	private final Portal _portal;

}