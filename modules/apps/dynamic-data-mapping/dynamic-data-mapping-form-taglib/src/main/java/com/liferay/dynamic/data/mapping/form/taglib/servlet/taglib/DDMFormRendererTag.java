/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.taglib.internal.security.permission.DDMFormInstancePermission;
import com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib.base.BaseDDMFormRendererTag;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Pedro Queiroz
 * @author Rafael Praxedes
 */
public class DDMFormRendererTag extends BaseDDMFormRendererTag {

	@Override
	public int doStartTag() throws JspException {
		int result = super.doStartTag();

		HttpServletRequest httpServletRequest = getRequest();

		setNamespacedAttribute(
			httpServletRequest, "ddmFormHTML", getDDMFormHTML());
		setNamespacedAttribute(
			httpServletRequest, "ddmFormInstance", getDDMFormInstance());
		setNamespacedAttribute(
			httpServletRequest, "formAvailable", isFormAvailable());
		setNamespacedAttribute(
			httpServletRequest, "hasAddFormInstanceRecordPermission",
			hasAddFormInstanceRecordPermission());
		setNamespacedAttribute(
			httpServletRequest, "hasViewFormInstancePermission",
			hasViewFormInstancePermission());
		setNamespacedAttribute(
			httpServletRequest, "languageId",
			LocaleUtil.toLanguageId(
				getLocale(httpServletRequest, getDDMForm())));
		setNamespacedAttribute(
			httpServletRequest, "redirectURL", getRedirectURL());
		setNamespacedAttribute(
			httpServletRequest, "resourceBundle",
			getResourceBundle(getLocale(httpServletRequest, getDDMForm())));

		return result;
	}

	protected DDMFormRenderingContext createDDMFormRenderingContext(
		DDMForm ddmForm) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		ddmFormRenderingContext.setContainerId(
			"form_" + StringUtil.randomString());
		ddmFormRenderingContext.setGroupId(ddmFormInstance.getGroupId());

		HttpServletRequest httpServletRequest = getRequest();

		ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);

		RenderResponse renderResponse = getRenderResponse();

		ddmFormRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(renderResponse));

		ddmFormRenderingContext.setLocale(
			getLocale(httpServletRequest, ddmForm));
		ddmFormRenderingContext.setViewMode(true);

		setDDMFormValues(ddmFormRenderingContext, ddmForm);
		setPortletNamespace(ddmFormRenderingContext, renderResponse);
		setReadOnly(ddmFormRenderingContext);
		setSubmitButton(ddmFormRenderingContext, ddmFormInstance);

		return ddmFormRenderingContext;
	}

	protected DDMForm getDDMForm() {
		DDMForm ddmForm = null;

		try {
			DDMFormInstance ddmFormInstance = getDDMFormInstance();

			if (ddmFormInstance == null) {
				return ddmForm;
			}

			DDMFormInstanceVersion latestDDMFormInstanceVersion =
				DDMFormInstanceVersionLocalServiceUtil.
					getLatestFormInstanceVersion(
						ddmFormInstance.getFormInstanceId(),
						WorkflowConstants.STATUS_APPROVED);

			DDMStructureVersion ddmStructureVersion =
				latestDDMFormInstanceVersion.getStructureVersion();

			ddmForm = ddmStructureVersion.getDDMForm();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return ddmForm;
	}

	protected String getDDMFormHTML() {
		String ddmFormHTML = StringPool.BLANK;

		DDMForm ddmForm = getDDMForm();
		DDMFormLayout ddmFormLayout = getDDMFormLayout();

		if ((ddmForm == null) || (ddmFormLayout == null)) {
			return ddmFormHTML;
		}

		try {
			DDMFormRenderer ddmFormRenderer = _ddmFormRendererSnapshot.get();

			ddmFormHTML = ddmFormRenderer.render(
				ddmForm, ddmFormLayout, createDDMFormRenderingContext(ddmForm));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return ddmFormHTML;
	}

	protected DDMFormInstance getDDMFormInstance() {
		long ddmFormInstanceId = 0;

		if (getDdmFormInstanceRecordVersionId() != null) {
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
				DDMFormInstanceRecordVersionLocalServiceUtil.
					fetchDDMFormInstanceRecordVersion(
						getDdmFormInstanceRecordVersionId());

			ddmFormInstanceId =
				ddmFormInstanceRecordVersion.getFormInstanceId();
		}
		else if (getDdmFormInstanceRecordId() != null) {
			DDMFormInstanceRecord ddmFormInstanceRecord =
				DDMFormInstanceRecordLocalServiceUtil.
					fetchDDMFormInstanceRecord(getDdmFormInstanceRecordId());

			ddmFormInstanceId = ddmFormInstanceRecord.getFormInstanceId();
		}
		else if (getDdmFormInstanceVersionId() != null) {
			DDMFormInstanceVersion ddmFormInstanceVersion =
				DDMFormInstanceVersionLocalServiceUtil.
					fetchDDMFormInstanceVersion(getDdmFormInstanceVersionId());

			ddmFormInstanceId = ddmFormInstanceVersion.getFormInstanceId();
		}
		else if (getDdmFormInstanceId() != null) {
			ddmFormInstanceId = getDdmFormInstanceId();
		}

		return DDMFormInstanceLocalServiceUtil.fetchFormInstance(
			ddmFormInstanceId);
	}

	protected DDMFormLayout getDDMFormLayout() {
		DDMFormLayout ddmFormLayout = null;

		try {
			DDMFormInstance ddmFormInstance = getDDMFormInstance();

			if (ddmFormInstance == null) {
				return ddmFormLayout;
			}

			DDMFormInstanceVersion latestDDMFormInstanceVersion =
				DDMFormInstanceVersionLocalServiceUtil.
					getLatestFormInstanceVersion(
						ddmFormInstance.getFormInstanceId(),
						WorkflowConstants.STATUS_APPROVED);

			DDMStructureVersion ddmStructureVersion =
				latestDDMFormInstanceVersion.getStructureVersion();

			ddmFormLayout = ddmStructureVersion.getDDMFormLayout();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return ddmFormLayout;
	}

	protected Locale getLocale(
		HttpServletRequest httpServletRequest, DDMForm ddmForm) {

		Locale locale = LocaleUtil.fromLanguageId(
			LanguageUtil.getLanguageId(httpServletRequest));

		if (ddmForm == null) {
			return locale;
		}

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		if (availableLocales.contains(locale)) {
			return locale;
		}

		return ddmForm.getDefaultLocale();
	}

	protected String getRedirectURL() {
		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance == null) {
			return StringPool.BLANK;
		}

		try {
			DDMFormInstanceSettings ddmFormInstanceSettings =
				ddmFormInstance.getSettingsModel();

			return ddmFormInstanceSettings.redirectURL();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	protected RenderResponse getRenderResponse() {
		HttpServletRequest httpServletRequest = getRequest();

		return (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundleLoader portalResourceBundleLoader =
			ResourceBundleLoaderUtil.getPortalResourceBundleLoader();

		ResourceBundle portalResourceBundle =
			portalResourceBundleLoader.loadResourceBundle(locale);

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			moduleResourceBundle, portalResourceBundle);
	}

	protected String getSubmitLabel(
		DDMFormInstance ddmFormInstance, Locale locale) {

		boolean workflowEnabled = hasWorkflowEnabled(
			ddmFormInstance, getThemeDisplay());

		ResourceBundle resourceBundle = getResourceBundle(locale);

		if (workflowEnabled) {
			return LanguageUtil.get(resourceBundle, "submit-for-workflow");
		}

		return LanguageUtil.get(resourceBundle, "submit-form");
	}

	protected ThemeDisplay getThemeDisplay() {
		HttpServletRequest httpServletRequest = getRequest();

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	protected boolean hasAddFormInstanceRecordPermission() {
		boolean hasAddFormInstanceRecordPermission = true;

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance != null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			try {
				hasAddFormInstanceRecordPermission =
					DDMFormInstancePermission.contains(
						themeDisplay.getPermissionChecker(), ddmFormInstance,
						DDMActionKeys.ADD_FORM_INSTANCE_RECORD);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return hasAddFormInstanceRecordPermission;
	}

	protected boolean hasViewFormInstancePermission() {
		boolean hasViewFormInstancePermission = true;

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance != null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			try {
				hasViewFormInstancePermission =
					DDMFormInstancePermission.contains(
						themeDisplay.getPermissionChecker(), ddmFormInstance,
						ActionKeys.VIEW);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return hasViewFormInstancePermission;
	}

	protected boolean hasWorkflowEnabled(
		DDMFormInstance ddmFormInstance, ThemeDisplay themeDisplay) {

		return WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
			themeDisplay.getCompanyId(), ddmFormInstance.getGroupId(),
			DDMFormInstance.class.getName(),
			ddmFormInstance.getFormInstanceId());
	}

	protected boolean isFormAvailable() {
		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance != null) {
			Group group = GroupLocalServiceUtil.fetchGroup(
				ddmFormInstance.getGroupId());

			if (((group != null) && group.isStagingGroup()) ||
				!hasViewFormInstancePermission()) {

				return false;
			}

			return true;
		}

		return false;
	}

	protected void setDDMFormValues(
		DDMFormRenderingContext ddmFormRenderingContext, DDMForm ddmForm) {

		DDMFormValuesFactory ddmFormValuesFactory =
			_ddmFormValuesFactorySnapshot.get();

		DDMFormValues ddmFormValues = ddmFormValuesFactory.create(
			getRequest(), ddmForm);

		try {
			if (getDdmFormInstanceRecordVersionId() != null) {
				DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
					DDMFormInstanceRecordVersionLocalServiceUtil.
						fetchDDMFormInstanceRecordVersion(
							getDdmFormInstanceRecordVersionId());

				if (ddmFormInstanceRecordVersion != null) {
					DDMFormValuesMerger ddmFormValuesMerger =
						_ddmFormValuesMergerSnapshot.get();

					ddmFormValues = ddmFormValuesMerger.merge(
						ddmFormInstanceRecordVersion.getDDMFormValues(),
						ddmFormValues);
				}
			}
			else if (getDdmFormInstanceRecordId() != null) {
				DDMFormInstanceRecord ddmFormInstanceRecord =
					DDMFormInstanceRecordLocalServiceUtil.
						fetchDDMFormInstanceRecord(
							getDdmFormInstanceRecordId());

				if (ddmFormInstanceRecord != null) {
					DDMFormValuesMerger ddmFormValuesMerger =
						_ddmFormValuesMergerSnapshot.get();

					ddmFormValues = ddmFormValuesMerger.merge(
						ddmFormInstanceRecord.getDDMFormValues(),
						ddmFormValues);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		ddmFormRenderingContext.setDDMFormValues(ddmFormValues);
	}

	protected void setPortletNamespace(
		DDMFormRenderingContext ddmFormRenderingContext,
		RenderResponse renderResponse) {

		String namespace = getNamespace();

		if (Validator.isNull(namespace)) {
			namespace = renderResponse.getNamespace();
		}

		ddmFormRenderingContext.setPortletNamespace(namespace);
	}

	protected void setReadOnly(
		DDMFormRenderingContext ddmFormRenderingContext) {

		if (!hasAddFormInstanceRecordPermission()) {
			ddmFormRenderingContext.setReadOnly(true);
		}
	}

	protected void setSubmitButton(
		DDMFormRenderingContext ddmFormRenderingContext,
		DDMFormInstance ddmFormInstance) {

		if (GetterUtil.getBoolean(getShowSubmitButton())) {
			ddmFormRenderingContext.setShowSubmitButton(true);
			ddmFormRenderingContext.setSubmitLabel(
				getSubmitLabel(
					ddmFormInstance, ddmFormRenderingContext.getLocale()));
		}
		else {
			ddmFormRenderingContext.setShowSubmitButton(false);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormRendererTag.class);

	private static final Snapshot<DDMFormRenderer> _ddmFormRendererSnapshot =
		new Snapshot<>(DDMFormRendererTag.class, DDMFormRenderer.class);
	private static final Snapshot<DDMFormValuesFactory>
		_ddmFormValuesFactorySnapshot = new Snapshot<>(
			DDMFormRendererTag.class, DDMFormValuesFactory.class);
	private static final Snapshot<DDMFormValuesMerger>
		_ddmFormValuesMergerSnapshot = new Snapshot<>(
			DDMFormRendererTag.class, DDMFormValuesMerger.class);

}