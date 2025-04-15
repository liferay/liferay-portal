/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormDisplayContextUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormGuestUploadFieldUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceStagingUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceSubmissionLimitStatusUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.security.permission.resource.DDMFormInstancePermission;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SessionParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Marcellus Tavares
 */
public class DDMFormDisplayContext {

	public DDMFormDisplayContext(
		DDMFormFieldOptionsFactory ddmFormFieldOptionsFactory,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		DDMFormInstanceRecordService ddmFormInstanceRecordService,
		DDMFormInstanceRecordVersionLocalService
			ddmFormInstanceRecordVersionLocalService,
		DDMFormInstanceService ddmFormInstanceService,
		DDMFormInstanceVersionLocalService ddmFormInstanceVersionLocalService,
		DDMFormRenderer ddmFormRenderer,
		DDMFormValuesFactory ddmFormValuesFactory,
		DDMFormValuesMerger ddmFormValuesMerger,
		DDMFormWebConfiguration ddmFormWebConfiguration,
		DDMStorageAdapterRegistry ddmStorageAdapterRegistry,
		DDMStructureLocalService ddmStructureLocalService,
		GroupLocalService groupLocalService, JSONFactory jsonFactory,
		NPMResolver npmResolver,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		Portal portal, RenderRequest renderRequest,
		RenderResponse renderResponse, RoleLocalService roleLocalService,
		UserLocalService userLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		_ddmFormFieldOptionsFactory = ddmFormFieldOptionsFactory;
		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;
		_ddmFormInstanceLocalService = ddmFormInstanceLocalService;
		_ddmFormInstanceRecordService = ddmFormInstanceRecordService;
		_ddmFormInstanceRecordVersionLocalService =
			ddmFormInstanceRecordVersionLocalService;
		_ddmFormInstanceService = ddmFormInstanceService;
		_ddmFormInstanceVersionLocalService =
			ddmFormInstanceVersionLocalService;
		_ddmFormRenderer = ddmFormRenderer;
		_ddmFormValuesFactory = ddmFormValuesFactory;
		_ddmFormValuesMerger = ddmFormValuesMerger;
		_ddmFormWebConfiguration = ddmFormWebConfiguration;
		_ddmStorageAdapterRegistry = ddmStorageAdapterRegistry;
		_ddmStructureLocalService = ddmStructureLocalService;
		_groupLocalService = groupLocalService;
		_jsonFactory = jsonFactory;
		_npmResolver = npmResolver;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;

		_containerId = "ddmForm".concat(StringUtil.randomString());

		if (Validator.isNotNull(getPortletResource())) {
			return;
		}

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.fetchDDMFormInstance(
				getFormInstanceId());

		if (ddmFormInstance == null) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}
	}

	public int getAutosaveInterval() {
		return _ddmFormWebConfiguration.autosaveInterval() * 60000;
	}

	public String[] getAvailableLanguageIds() throws PortalException {
		ThemeDisplay themeDisplay = getThemeDisplay();

		Set<Locale> siteAvailableLocales = LanguageUtil.getAvailableLocales(
			themeDisplay.getSiteGroupId());

		DDMForm ddmForm = getDDMForm();

		return TransformUtil.transformToArray(
			ddmForm.getAvailableLocales(),
			locale -> {
				if (!siteAvailableLocales.contains(locale)) {
					return null;
				}

				return LanguageUtil.getLanguageId(locale);
			},
			String.class);
	}

	public String getContainerId() {
		return _containerId;
	}

	public String getDataEngineModule() {
		return _npmResolver.resolveModuleName("data-engine-js-components-web");
	}

	public Map<String, Object> getDDMFormContext() throws Exception {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return null;
		}

		boolean maximumSubmissionLimitReached =
			DDMFormGuestUploadFieldUtil.isMaximumSubmissionLimitReached(
				ddmFormInstance, _getHttpServletRequest(),
				_ddmFormWebConfiguration.guestUploadMaximumSubmissions());

		DDMForm ddmForm = getDDMForm(ddmFormInstance);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			if (Objects.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.DOCUMENT_LIBRARY)) {

				if (ddmFormField.isRepeatable()) {
					ddmFormField.setProperty(
						"maximumRepetitions",
						_ddmFormWebConfiguration.
							maximumRepetitionsForUploadFields());
				}

				ddmFormField.setProperty(
					"maximumSubmissionLimitReached",
					maximumSubmissionLimitReached);

				if (Objects.equals(
						ddmFormInstance.getStorageType(), "object")) {

					DDMFormInstanceSettings ddmFormInstanceSettings =
						ddmFormInstance.getSettingsModel();

					ObjectField objectField =
						_objectFieldLocalService.getObjectField(
							GetterUtil.getLong(
								ddmFormInstanceSettings.objectDefinitionId()),
							_getObjectFieldName(ddmFormField));

					ddmFormField.setProperty(
						"objectFieldAcceptedFileExtensions",
						_getObjectFieldAcceptedFileExtensions(
							objectField.getObjectFieldId()));
					ddmFormField.setProperty(
						"objectFieldId", objectField.getObjectFieldId());
				}
			}
			else if (Objects.equals(
						ddmFormField.getType(),
						ObjectDDMFormFieldTypeConstants.OBJECT_RELATIONSHIP) &&
					 Objects.equals(
						 ddmFormInstance.getStorageType(), "object")) {

				ddmFormField.setProperty(
					"objectDefinitionId",
					String.valueOf(
						_getObjectDefinitionId(ddmFormField, ddmFormInstance)));
			}
			else if (StringUtil.equals(
						ddmFormField.getType(),
						DDMFormFieldTypeConstants.SELECT) &&
					 StringUtil.equals(
						 ddmFormField.getDataSourceType(), "data-provider")) {

				DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
					new DDMFormFieldRenderingContext();

				ddmFormFieldRenderingContext.setHttpServletRequest(
					_getHttpServletRequest());
				ddmFormFieldRenderingContext.setLocale(
					getLocale(_getHttpServletRequest(), ddmForm));

				ddmFormField.setDDMFormFieldOptions(
					_ddmFormFieldOptionsFactory.create(
						ddmFormField, ddmFormFieldRenderingContext));
			}
		}

		DDMFormInstanceRecord ddmFormInstanceRecord = getFormInstanceRecord();

		if (ddmFormInstanceRecord != null) {
			return _ddmFormRenderer.getDDMFormTemplateContext(
				ddmForm, getDDMFormLayout(ddmFormInstance),
				createDDMFormRenderingContext(
					ddmForm, ddmFormInstance,
					ddmFormInstanceRecord.
						getLatestFormInstanceRecordVersion()));
		}

		return _ddmFormRenderer.getDDMFormTemplateContext(
			ddmForm, getDDMFormLayout(ddmFormInstance),
			createDDMFormRenderingContext(
				ddmForm, ddmFormInstance,
				_ddmFormInstanceRecordVersionLocalService.
					fetchLatestFormInstanceRecordVersion(
						_getUserId(), getFormInstanceId(),
						getFormInstanceVersion(),
						WorkflowConstants.STATUS_DRAFT)));
	}

	public DDMFormSuccessPageSettings getDDMFormSuccessPageSettings()
		throws PortalException {

		DDMForm ddmForm = getDDMForm();

		return ddmForm.getDDMFormSuccessPageSettings();
	}

	public String getDefaultLanguageId() throws PortalException {
		String languageId = ParamUtil.getString(_renderRequest, "languageId");

		Locale locale = LocaleUtil.fromLanguageId(languageId, true, false);

		DDMForm ddmForm = getDDMForm();

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		if (!availableLocales.contains(locale)) {
			locale = getLocale(_getHttpServletRequest(), ddmForm);
		}

		return LanguageUtil.getLanguageId(locale);
	}

	public DDMFormInstance getFormInstance() {
		if (_ddmFormInstance != null) {
			return _ddmFormInstance;
		}

		try {
			_ddmFormInstance = _ddmFormInstanceLocalService.fetchFormInstance(
				getFormInstanceId());

			if ((_ddmFormInstance != null) && !isPreview()) {
				DDMFormInstanceVersion latestApprovedDDMFormInstanceVersion =
					_getLatestApprovedDDMFormInstanceVersion();

				if (Validator.isNotNull(
						latestApprovedDDMFormInstanceVersion.getSettings())) {

					_ddmFormInstance.setSettings(
						latestApprovedDDMFormInstanceVersion.getSettings());

					DDMStructureVersion ddmStructureVersion =
						latestApprovedDDMFormInstanceVersion.
							getStructureVersion();

					_ddmFormInstance.setStructureId(
						ddmStructureVersion.getStructureId());
				}
			}
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}

		return _ddmFormInstance;
	}

	public long getFormInstanceId() {
		if (_ddmFormInstanceId != 0) {
			return _ddmFormInstanceId;
		}

		String ddmStructureExternalReferenceCode = PrefsParamUtil.getString(
			_renderRequest.getPreferences(), _renderRequest,
			"ddmStructureExternalReferenceCode");

		if (!Validator.isBlank(ddmStructureExternalReferenceCode)) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				PrefsParamUtil.getString(
					_renderRequest.getPreferences(), _renderRequest,
					"groupExternalReferenceCode"),
				themeDisplay.getCompanyId());

			try {
				DDMStructure ddmStructure =
					_ddmStructureLocalService.
						getStructureByExternalReferenceCode(
							ddmStructureExternalReferenceCode,
							group.getGroupId(),
							_portal.getClassNameId(DDMFormInstance.class));

				DDMFormInstance ddmFormInstance =
					_ddmFormInstanceLocalService.getFormInstanceByStructureId(
						ddmStructure.getStructureId());

				_ddmFormInstanceId = ddmFormInstance.getFormInstanceId();

				return _ddmFormInstanceId;
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		_ddmFormInstanceId = PrefsParamUtil.getLong(
			_renderRequest.getPreferences(), _renderRequest, "formInstanceId");

		if (_ddmFormInstanceId == 0) {
			_ddmFormInstanceId = _getFormInstanceIdFromSession();
		}

		return _ddmFormInstanceId;
	}

	public DDMFormInstanceRecord getFormInstanceRecord() {
		if (_ddmFormInstanceRecord != null) {
			return _ddmFormInstanceRecord;
		}

		try {
			_ddmFormInstanceRecord =
				_ddmFormInstanceRecordService.getFormInstanceRecord(
					getFormInstanceRecordId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return _ddmFormInstanceRecord;
	}

	public long getFormInstanceRecordId() {
		if (_ddmFormInstanceRecordId != 0) {
			return _ddmFormInstanceRecordId;
		}

		return PrefsParamUtil.getLong(
			_renderRequest.getPreferences(), _renderRequest,
			"formInstanceRecordId");
	}

	public Map<String, String> getLimitToOneSubmissionPerUserMap()
		throws PortalException {

		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return HashMapBuilder.put(
				"limitToOneSubmissionPerUserBody", StringPool.BLANK
			).put(
				"limitToOneSubmissionPerUserHeader", StringPool.BLANK
			).build();
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		JSONObject limitToOneSubmissionPerUserBodyJSONObject =
			_jsonFactory.createJSONObject(
				ddmFormInstanceSettings.limitToOneSubmissionPerUserBody());

		String limitToOneSubmissionPerUserBody =
			limitToOneSubmissionPerUserBodyJSONObject.getString(
				getDefaultLanguageId());

		JSONObject limitToOneSubmissionPerUserHeaderJSONObject =
			_jsonFactory.createJSONObject(
				ddmFormInstanceSettings.limitToOneSubmissionPerUserHeader());

		String limitToOneSubmissionPerUserHeader =
			limitToOneSubmissionPerUserHeaderJSONObject.getString(
				getDefaultLanguageId());

		if (Validator.isNotNull(limitToOneSubmissionPerUserBody) &&
			Validator.isNotNull(limitToOneSubmissionPerUserHeader)) {

			return HashMapBuilder.put(
				"limitToOneSubmissionPerUserBody",
				limitToOneSubmissionPerUserBody
			).put(
				"limitToOneSubmissionPerUserHeader",
				limitToOneSubmissionPerUserHeader
			).build();
		}

		return HashMapBuilder.put(
			"limitToOneSubmissionPerUserBody",
			LanguageUtil.get(
				_getHttpServletRequest(),
				"you-can-fill-out-this-form-only-once.-contact-the-owner-of-" +
					"the-form-if-you-think-this-is-a-mistake")
		).put(
			"limitToOneSubmissionPerUserHeader",
			LanguageUtil.get(
				_getHttpServletRequest(), "you-have-already-responded")
		).build();
	}

	public String getRedirectURL() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return null;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.redirectURL();
	}

	public String getSubmitLabel() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return StringPool.BLANK;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			ddmFormInstanceSettings.submitLabel());

		String submitLabel = jsonObject.getString(getDefaultLanguageId());

		if (Validator.isNotNull(submitLabel)) {
			return submitLabel;
		}

		ResourceBundle resourceBundle = _getResourceBundle();

		if (StringUtil.equals(ddmFormInstance.getStorageType(), "object")) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					ddmFormInstance.getObjectDefinitionId());

			if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
					objectDefinition.getCompanyId(), 0,
					objectDefinition.getClassName())) {

				return LanguageUtil.get(resourceBundle, "submit-for-workflow");
			}

			return LanguageUtil.get(resourceBundle, "save");
		}

		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				ddmFormInstance.getCompanyId(), ddmFormInstance.getGroupId(),
				DDMFormInstance.class.getName(),
				ddmFormInstance.getFormInstanceId())) {

			DDMFormInstanceRecord ddmFormInstanceRecord =
				getFormInstanceRecord();

			if (ddmFormInstanceRecord != null) {
				DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
					ddmFormInstanceRecord.getLatestFormInstanceRecordVersion();

				if (ddmFormInstanceRecordVersion.getStatus() ==
						WorkflowConstants.STATUS_PENDING) {

					return LanguageUtil.get(resourceBundle, "save");
				}
			}

			return LanguageUtil.get(resourceBundle, "submit-for-workflow");
		}

		return LanguageUtil.get(resourceBundle, "submit-form");
	}

	public String getSuccessPageDescription(Locale locale)
		throws PortalException {

		DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
			getDDMFormSuccessPageSettings();

		LocalizedValue body = ddmFormSuccessPageSettings.getBody();

		return GetterUtil.getString(
			body.getString(locale), body.getString(body.getDefaultLocale()));
	}

	public String getSuccessPageTitle(Locale locale) throws PortalException {
		DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
			getDDMFormSuccessPageSettings();

		LocalizedValue title = ddmFormSuccessPageSettings.getTitle();

		return HtmlUtil.escape(
			GetterUtil.getString(
				title.getString(locale),
				title.getString(title.getDefaultLocale())));
	}

	public boolean hasAddFormInstanceRecordPermission() throws PortalException {
		if (_hasAddFormInstanceRecordPermission != null) {
			return _hasAddFormInstanceRecordPermission;
		}

		_hasAddFormInstanceRecordPermission = false;

		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance != null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_hasAddFormInstanceRecordPermission =
				DDMFormInstancePermission.contains(
					themeDisplay.getPermissionChecker(), ddmFormInstance,
					DDMActionKeys.ADD_FORM_INSTANCE_RECORD);
		}

		return _hasAddFormInstanceRecordPermission;
	}

	public boolean hasValidStorageType(DDMFormInstance ddmFormInstance) {
		try {
			DDMStorageAdapter ddmStorageAdapter =
				_ddmStorageAdapterRegistry.getDDMStorageAdapter(
					ddmFormInstance.getStorageType());

			if (ddmStorageAdapter != null) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	public boolean hasViewPermission() throws PortalException {
		if (_hasViewPermission != null) {
			return _hasViewPermission;
		}

		_hasViewPermission = false;

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.fetchFormInstance(getFormInstanceId());

		if (ddmFormInstance != null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_hasViewPermission = DDMFormInstancePermission.contains(
				themeDisplay.getPermissionChecker(), ddmFormInstance,
				ActionKeys.VIEW);
		}

		return _hasViewPermission;
	}

	public boolean isAutosaveEnabled() throws PortalException {
		if (_autosaveEnabled != null) {
			return _autosaveEnabled;
		}

		if (isGuestUser()) {
			_autosaveEnabled = Boolean.FALSE;
		}
		else {
			DDMFormInstance ddmFormInstance = getFormInstance();

			DDMFormInstanceSettings ddmFormInstanceSettings =
				ddmFormInstance.getSettingsModel();

			_autosaveEnabled =
				ddmFormInstanceSettings.autosaveEnabled() &&
				(getAutosaveInterval() > 0);
		}

		return _autosaveEnabled;
	}

	public boolean isDisplayChartAsTable() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return false;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.displayChartAsTable();
	}

	public boolean isFormAvailable() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if ((ddmFormInstance == null) || !isFormPublished()) {
			return false;
		}

		if (!isFormShared() && isSharedURL()) {
			return false;
		}

		Group group = _groupLocalService.getGroup(ddmFormInstance.getGroupId());

		Group scopeGroup = _groupLocalService.getGroup(
			_portal.getScopeGroupId(_renderRequest));

		if ((group != null) && (scopeGroup != null) && group.isStagingGroup() &&
			!scopeGroup.isStagingGroup()) {

			return false;
		}

		if ((group != null) && group.isStagedRemotely()) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			Role role = _roleLocalService.getRole(
				themeDisplay.getCompanyId(), RoleConstants.ADMINISTRATOR);

			List<User> users = _userLocalService.getRoleUsers(role.getRoleId());

			if (!DDMFormInstanceStagingUtil.isFormInstancePublishedToRemoteLive(
					group, users.get(0), ddmFormInstance.getUuid())) {

				return false;
			}
		}

		return true;
	}

	public boolean isFormShared() {
		PortletSession portletSession = _renderRequest.getPortletSession(false);

		if (portletSession != null) {
			return SessionParamUtil.getBoolean(_renderRequest, "shared");
		}

		return ParamUtil.getBoolean(_renderRequest, "shared");
	}

	public boolean isLoggedUser() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.isSignedIn();
	}

	public boolean isPreview() throws PortalException {
		ThemeDisplay themeDisplay = getThemeDisplay();

		if (ParamUtil.getBoolean(_renderRequest, "preview") &&
			DDMFormInstancePermission.contains(
				themeDisplay.getPermissionChecker(), getFormInstanceId(),
				ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	public boolean isPropagateLanguageSelection() {
		return _ddmFormWebConfiguration.propagateLanguageSelection();
	}

	public boolean isRememberMe() {
		String rememberMe = CookiesManagerUtil.getCookieValue(
			CookiesConstants.NAME_REMEMBER_ME, _getHttpServletRequest());

		if ((rememberMe != null) && rememberMe.equals("true")) {
			return true;
		}

		return false;
	}

	public Boolean isRequireAuthentication() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		Layout layout = themeDisplay.getLayout();

		if (ddmFormInstanceSettings.requireAuthentication() &&
			!layout.isPrivateLayout() && !themeDisplay.isSignedIn()) {

			return true;
		}

		return false;
	}

	public boolean isSharedURL() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		String urlCurrent = themeDisplay.getURLCurrent();

		if (urlCurrent.contains("shared") &&
			urlCurrent.contains(String.valueOf(getFormInstanceId()))) {

			return true;
		}

		return false;
	}

	public boolean isShowConfigurationIcon() throws PortalException {
		if (_showConfigurationIcon != null) {
			return _showConfigurationIcon;
		}

		String layoutMode = ParamUtil.getString(
			PortalUtil.getOriginalServletRequest(_getHttpServletRequest()),
			"p_l_mode", Constants.VIEW);

		if (isPreview() || (isSharedURL() && isFormShared()) ||
			layoutMode.equals(Constants.EDIT)) {

			_showConfigurationIcon = false;

			return _showConfigurationIcon;
		}

		ThemeDisplay themeDisplay = getThemeDisplay();

		_showConfigurationIcon = PortletPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			getPortletId(), ActionKeys.CONFIGURATION);

		return _showConfigurationIcon;
	}

	public boolean isShowPartialResultsToRespondents() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return false;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.showPartialResultsToRespondents();
	}

	public boolean isShowSubmitButton() {
		return !ParamUtil.getBoolean(_renderRequest, "preview");
	}

	public boolean isShowSuccessPage() throws PortalException {
		if (!SessionErrors.isEmpty(_renderRequest) ||
			!SessionMessages.contains(
				_renderRequest, "formInstanceRecordAdded") ||
			Validator.isNotNull(getRedirectURL())) {

			return false;
		}

		DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
			getDDMFormSuccessPageSettings();

		return ddmFormSuccessPageSettings.isEnabled();
	}

	public boolean isSubmissionLimitReached() throws PortalException {
		return DDMFormInstanceSubmissionLimitStatusUtil.
			isSubmissionLimitReached(
				getFormInstance(), _ddmFormInstanceRecordVersionLocalService,
				getUser());
	}

	protected DDMFormRenderingContext createDDMFormRenderingContext(
			DDMForm ddmForm, DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion)
		throws PortalException {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		if (ddmFormInstanceRecordVersion != null) {
			ddmFormRenderingContext.addProperty(
				"ddmFormInstanceRecordId",
				ddmFormInstanceRecordVersion.getFormInstanceRecordId());
		}

		ddmFormRenderingContext.addProperty(
			"showPartialResultsToRespondents",
			isShowPartialResultsToRespondents());

		String redirectURL = ParamUtil.getString(_renderRequest, "redirect");

		if (Validator.isNotNull(redirectURL)) {
			ddmFormRenderingContext.setCancelLabel(
				LanguageUtil.get(ddmForm.getDefaultLocale(), "cancel"));
		}

		ddmFormRenderingContext.setContainerId(_containerId);
		ddmFormRenderingContext.setDDMFormInstanceId(
			ddmFormInstance.getFormInstanceId());

		DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(
			_renderRequest, ddmForm);

		if (ddmFormInstanceRecordVersion != null) {
			ddmFormValues = _ddmFormValuesMerger.merge(
				ddmForm, ddmFormInstanceRecordVersion.getDDMFormValues(),
				ddmFormValues);
		}

		ddmFormRenderingContext.setDDMFormValues(ddmFormValues);

		ddmFormRenderingContext.setGroupId(ddmFormInstance.getGroupId());
		ddmFormRenderingContext.setHttpServletRequest(_getHttpServletRequest());
		ddmFormRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(_renderResponse));
		ddmFormRenderingContext.setLocale(
			getLocale(_getHttpServletRequest(), ddmForm));
		ddmFormRenderingContext.setPortletNamespace(
			_renderResponse.getNamespace());

		if (!hasAddFormInstanceRecordPermission() ||
			!hasValidStorageType(ddmFormInstance)) {

			ddmFormRenderingContext.setReadOnly(true);
		}

		if (Validator.isNotNull(redirectURL)) {
			ddmFormRenderingContext.setRedirectURL(redirectURL);
		}

		ddmFormRenderingContext.setSharedURL(isSharedURL());

		if (Validator.isNull(redirectURL)) {
			ddmFormRenderingContext.setShowCancelButton(false);
		}

		ddmFormRenderingContext.setShowSubmitButton(isShowSubmitButton());
		ddmFormRenderingContext.setSubmitLabel(getSubmitLabel());
		ddmFormRenderingContext.setViewMode(true);

		return ddmFormRenderingContext;
	}

	protected DDMForm getDDMForm() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return ddmStructure.getDDMForm();
	}

	protected DDMForm getDDMForm(DDMFormInstance ddmFormInstance)
		throws PortalException {

		DDMForm ddmForm = null;

		if (isPreview()) {
			DDMStructure ddmStructure = ddmFormInstance.getStructure();

			DDMStructureVersion latestStructureVersion =
				ddmStructure.getLatestStructureVersion();

			ddmForm = latestStructureVersion.getDDMForm();
		}
		else {
			DDMFormInstanceVersion latestDDMFormInstanceVersion =
				_getLatestApprovedDDMFormInstanceVersion();

			DDMStructureVersion ddmStructureVersion =
				latestDDMFormInstanceVersion.getStructureVersion();

			ddmForm = ddmStructureVersion.getDDMForm();
		}

		DDMFormDisplayContextUtil.addCaptchaDDMFormField(
			ddmForm, ddmFormInstance.getSettingsModel(), _renderRequest);

		return ddmForm;
	}

	protected DDMFormLayout getDDMFormLayout(DDMFormInstance ddmFormInstance)
		throws PortalException {

		DDMFormLayout ddmFormLayout = null;

		if (isPreview()) {
			DDMStructure ddmStructure = ddmFormInstance.getStructure();

			DDMStructureVersion latestStructureVersion =
				ddmStructure.getLatestStructureVersion();

			ddmFormLayout = latestStructureVersion.getDDMFormLayout();
		}
		else {
			DDMFormInstanceVersion latestDDMFormInstanceVersion =
				_getLatestApprovedDDMFormInstanceVersion();

			DDMStructureVersion ddmStructureVersion =
				latestDDMFormInstanceVersion.getStructureVersion();

			ddmFormLayout = ddmStructureVersion.getDDMFormLayout();
		}

		DDMFormDisplayContextUtil.addCaptchaDDMFormLayoutRow(
			ddmFormInstance.getSettingsModel(), ddmFormLayout);

		return ddmFormLayout;
	}

	protected String getFormInstanceVersion() {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return "1.0";
		}

		return ddmFormInstance.getVersion();
	}

	protected Locale getLocale(
		HttpServletRequest httpServletRequest, DDMForm ddmForm) {

		String defaultLanguageId = ParamUtil.getString(
			httpServletRequest, "defaultLanguageId");

		if (Validator.isNotNull(defaultLanguageId)) {
			return LocaleUtil.fromLanguageId(defaultLanguageId);
		}

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		Locale locale = LocaleUtil.fromLanguageId(
			LanguageUtil.getLanguageId(httpServletRequest));

		if (availableLocales.contains(locale)) {
			return locale;
		}

		return ddmForm.getDefaultLocale();
	}

	protected String getPortletId() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getId();
	}

	protected String getPortletResource() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getPortletResource();
	}

	protected ThemeDisplay getThemeDisplay() {
		return (ThemeDisplay)_renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
	}

	protected User getUser() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		return themeDisplay.getUser();
	}

	protected boolean isFormPublished() throws PortalException {
		DDMFormInstance ddmFormInstance = getFormInstance();

		if (ddmFormInstance == null) {
			return false;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.published();
	}

	protected boolean isGuestUser() {
		User user = getUser();

		return user.isGuestUser();
	}

	private long _getFormInstanceIdFromSession() {
		PortletSession portletSession = _renderRequest.getPortletSession();

		return GetterUtil.getLong(
			portletSession.getAttribute("ddmFormInstanceId"));
	}

	private HttpServletRequest _getHttpServletRequest() {
		return PortalUtil.getHttpServletRequest(_renderRequest);
	}

	private DDMFormInstanceVersion _getLatestApprovedDDMFormInstanceVersion()
		throws PortalException {

		if (_latestDDMFormInstanceVersion != null) {
			return _latestDDMFormInstanceVersion;
		}

		_latestDDMFormInstanceVersion =
			_ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
				_ddmFormInstance.getFormInstanceId(),
				WorkflowConstants.STATUS_APPROVED);

		return _latestDDMFormInstanceVersion;
	}

	private long _getObjectDefinitionId(
			DDMFormField ddmFormField, DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			GetterUtil.getLong(ddmFormInstanceSettings.objectDefinitionId()),
			_getObjectFieldName(ddmFormField));

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectFieldId2(
					objectField.getObjectFieldId());

		return objectRelationship.getObjectDefinitionId1();
	}

	private String _getObjectFieldAcceptedFileExtensions(long objectFieldId) {
		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectFieldId, "acceptedFileExtensions");

		return objectFieldSetting.getValue();
	}

	private String _getObjectFieldName(DDMFormField ddmFormField) {
		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray(
				(String)ddmFormField.getProperty("objectFieldName"));

			return jsonArray.getString(0);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	private ResourceBundle _getResourceBundle() {
		ResourceBundle portalResourceBundle = _portal.getResourceBundle(
			LocaleThreadLocal.getThemeDisplayLocale());

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", LocaleThreadLocal.getThemeDisplayLocale(),
			getClass());

		return new AggregateResourceBundle(
			moduleResourceBundle, portalResourceBundle);
	}

	private long _getUserId() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		return themeDisplay.getUserId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormDisplayContext.class);

	private Boolean _autosaveEnabled;
	private final String _containerId;
	private final DDMFormFieldOptionsFactory _ddmFormFieldOptionsFactory;
	private final DDMFormFieldTypeServicesRegistry
		_ddmFormFieldTypeServicesRegistry;
	private DDMFormInstance _ddmFormInstance;
	private long _ddmFormInstanceId;
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService;
	private DDMFormInstanceRecord _ddmFormInstanceRecord;
	private long _ddmFormInstanceRecordId;
	private final DDMFormInstanceRecordService _ddmFormInstanceRecordService;
	private final DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;
	private final DDMFormInstanceService _ddmFormInstanceService;
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final DDMFormValuesMerger _ddmFormValuesMerger;
	private final DDMFormWebConfiguration _ddmFormWebConfiguration;
	private final DDMStorageAdapterRegistry _ddmStorageAdapterRegistry;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final GroupLocalService _groupLocalService;
	private Boolean _hasAddFormInstanceRecordPermission;
	private Boolean _hasViewPermission;
	private final JSONFactory _jsonFactory;
	private DDMFormInstanceVersion _latestDDMFormInstanceVersion;
	private final NPMResolver _npmResolver;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final RoleLocalService _roleLocalService;
	private Boolean _showConfigurationIcon;
	private final UserLocalService _userLocalService;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}