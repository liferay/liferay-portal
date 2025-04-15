/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet;

import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextFactory;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormAdminDisplayContext;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormAdminFieldSetDisplayContext;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializer;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRegistry;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration",
	property = {
		"com.liferay.portlet.autopropagated-parameters=currentTab",
		"com.liferay.portlet.css-class-wrapper=portlet-forms-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/admin/css/main.css",
		"com.liferay.portlet.icon=/admin/icons/form.png",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Forms",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/admin/",
		"jakarta.portlet.init-param.view-template=/admin/view.jsp",
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DDMFormAdminPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			setRenderRequestAttributes(renderRequest, renderResponse);
		}
		catch (Exception exception) {
			if (isSessionErrorException(exception)) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}

				SessionErrors.add(renderRequest, exception.getClass());
			}
			else {
				_log.error(exception);

				throw new PortletException(exception);
			}
		}

		hideDefaultErrorMessage(renderRequest);

		super.render(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddmFormWebConfiguration = ConfigurableUtil.createConfigurable(
			DDMFormWebConfiguration.class, properties);
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if ((throwable instanceof SystemException) ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	protected void setRenderRequestAttributes(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		String currentTab = ParamUtil.getString(
			renderRequest, "currentTab", "forms");

		if (currentTab.equals("element-set")) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new DDMFormAdminFieldSetDisplayContext(
					renderRequest, renderResponse,
					_ddmFormBuilderContextFactory,
					_ddmFormBuilderSettingsRetriever,
					_ddmFormContextToDDMFormValues,
					_ddmFormFieldTypeServicesRegistry,
					_ddmFormFieldTypesSerializer, _ddmFormInstanceLocalService,
					_ddmFormInstanceRecordLocalService,
					_ddmFormInstanceRecordWriterRegistry,
					_ddmFormInstanceService,
					_ddmFormInstanceVersionLocalService, _ddmFormRenderer,
					_ddmFormTemplateContextFactory, _ddmFormValuesFactory,
					_ddmFormValuesMerger, _ddmFormWebConfiguration,
					_ddmStorageAdapterRegistry, _ddmStructureLocalService,
					_ddmStructureService, _jsonFactory, _npmResolver,
					_objectDefinitionLocalService, _portal));

			CTTimelineUtil.setClassName(renderRequest, DDMStructure.class);
		}
		else {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new DDMFormAdminDisplayContext(
					renderRequest, renderResponse,
					_ddmFormBuilderContextFactory,
					_ddmFormBuilderSettingsRetriever,
					_ddmFormContextToDDMFormValues,
					_ddmFormFieldTypeServicesRegistry,
					_ddmFormFieldTypesSerializer, _ddmFormInstanceLocalService,
					_ddmFormInstanceRecordLocalService,
					_ddmFormInstanceRecordWriterRegistry,
					_ddmFormInstanceService,
					_ddmFormInstanceVersionLocalService, _ddmFormRenderer,
					_ddmFormTemplateContextFactory, _ddmFormValuesFactory,
					_ddmFormValuesMerger, _ddmFormWebConfiguration,
					_ddmStorageAdapterRegistry, _ddmStructureLocalService,
					_ddmStructureService, _jsonFactory, _npmResolver,
					_objectDefinitionLocalService, _portal));

			CTTimelineUtil.setClassName(renderRequest, DDMFormInstance.class);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormAdminPortlet.class);

	@Reference
	private DDMFormBuilderContextFactory _ddmFormBuilderContextFactory;

	@Reference
	private DDMFormBuilderSettingsRetriever _ddmFormBuilderSettingsRetriever;

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=formValues)"
	)
	private DDMFormContextDeserializer<DDMFormValues>
		_ddmFormContextToDDMFormValues;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference(target = "(ddm.form.field.types.serializer.type=json)")
	private DDMFormFieldTypesSerializer _ddmFormFieldTypesSerializer;

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private DDMFormInstanceRecordWriterRegistry
		_ddmFormInstanceRecordWriterRegistry;

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesMerger _ddmFormValuesMerger;

	private volatile DDMFormWebConfiguration _ddmFormWebConfiguration;

	@Reference
	private DDMStorageAdapterRegistry _ddmStorageAdapterRegistry;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private NPMResolver _npmResolver;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.dynamic.data.mapping.form.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}