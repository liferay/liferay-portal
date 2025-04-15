/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormDisplayContext;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceSubmissionLimitStatusUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.util.DDMLayoutUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.application-type=widget",
		"com.liferay.portlet.css-class-wrapper=portlet-forms-display",
		"com.liferay.portlet.display-category=category.collaboration",
		"com.liferay.portlet.friendly-url-mapping=form",
		"com.liferay.portlet.header-portlet-css=/admin/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Dynamic Data Mapping Form",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/display/",
		"jakarta.portlet.init-param.view-template=/display/view.jsp",
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DDMFormPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		try {
			super.processAction(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			_portal.copyRequestParameters(actionRequest, actionResponse);

			Throwable throwable = _getRootCauseThrowable(exception);

			hideDefaultErrorMessage(actionRequest);

			if (throwable instanceof DDMFormValuesValidationException) {
				if (throwable instanceof
						DDMFormValuesValidationException.MustSetValidValue ||
					throwable instanceof
						DDMFormValuesValidationException.RequiredValue) {

					SessionErrors.add(
						actionRequest, throwable.getClass(), throwable);
				}
				else {
					SessionErrors.add(
						actionRequest, DDMFormValuesValidationException.class);
				}
			}
			else {
				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			if (DDMLayoutUtil.isSharedLayout(themeDisplay)) {
				_saveParametersInSession(actionRequest);
			}
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			setRenderRequestAttributes(renderRequest, renderResponse);

			DDMFormDisplayContext ddmFormDisplayContext =
				(DDMFormDisplayContext)renderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT);

			if (ddmFormDisplayContext.isFormShared()) {
				_saveRefererGroupIdInRequest(
					renderRequest, ddmFormDisplayContext);
			}

			if ((DDMFormInstanceSubmissionLimitStatusUtil.
					isLimitToOneSubmissionPerUser(
						ddmFormDisplayContext.getFormInstance()) &&
				 !ddmFormDisplayContext.isLoggedUser()) ||
				(ddmFormDisplayContext.isRequireAuthentication() &&
				 ddmFormDisplayContext.isSharedURL())) {

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.sendRedirect(
					StringBundler.concat(
						_portal.getPathMain(), "/portal/login?redirect=",
						URLCodec.encodeURL(
							_portal.getCurrentURL(renderRequest))));

				return;
			}
		}
		catch (Exception exception) {
			if (isSessionErrorException(exception)) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}

				hideDefaultErrorMessage(renderRequest);

				SessionErrors.add(renderRequest, exception.getClass());
			}
			else {
				throw new PortletException(exception);
			}
		}

		super.render(renderRequest, renderResponse);
	}

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(
			_ALIAS, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		return false;
	}

	protected void setRenderRequestAttributes(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		DDMFormDisplayContext ddmFormDisplayContext = new DDMFormDisplayContext(
			_ddmFormFieldOptionsFactory, _ddmFormFieldTypeServicesRegistry,
			_ddmFormInstanceLocalService, _ddmFormInstanceRecordService,
			_ddmFormInstanceRecordVersionLocalService, _ddmFormInstanceService,
			_ddmFormInstanceVersionLocalService, _ddmFormRenderer,
			_ddmFormValuesFactory, _ddmFormValuesMerger,
			_configurationProvider.getCompanyConfiguration(
				DDMFormWebConfiguration.class,
				CompanyThreadLocal.getCompanyId()),
			_ddmStorageAdapterRegistry, _ddmStructureLocalService,
			_groupLocalService, _jsonFactory, _npmResolver,
			_objectDefinitionLocalService, _objectFieldLocalService,
			_objectFieldSettingLocalService, _objectRelationshipLocalService,
			_portal, renderRequest, renderResponse, _roleLocalService,
			_userLocalService, _workflowDefinitionLinkLocalService);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, ddmFormDisplayContext);
	}

	private Throwable _getRootCauseThrowable(Throwable throwable) {
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}

		return throwable;
	}

	private void _saveParametersInSession(ActionRequest actionRequest) {
		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");

		if (formInstanceId > 0) {
			PortletSession portletSession = actionRequest.getPortletSession();

			portletSession.setAttribute("formInstanceId", formInstanceId);
			portletSession.setAttribute("shared", Boolean.TRUE);
		}
	}

	private void _saveRefererGroupIdInRequest(
		RenderRequest renderRequest,
		DDMFormDisplayContext ddmFormDisplayContext) {

		DDMFormInstance ddmFormInstance =
			ddmFormDisplayContext.getFormInstance();

		if (ddmFormInstance != null) {
			renderRequest.setAttribute(
				DDMFormWebKeys.REFERER_GROUP_ID, ddmFormInstance.getGroupId());
		}
	}

	private static final String _ALIAS = "form";

	private static final Log _log = LogFactoryUtil.getLog(DDMFormPortlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DDMFormFieldOptionsFactory _ddmFormFieldOptionsFactory;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesMerger _ddmFormValuesMerger;

	@Reference
	private DDMStorageAdapterRegistry _ddmStorageAdapterRegistry;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private NPMResolver _npmResolver;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.dynamic.data.mapping.form.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}