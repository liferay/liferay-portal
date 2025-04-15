/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLWebKeys;
import com.liferay.dynamic.data.lists.exception.NoSuchRecordSetException;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.dynamic.data.lists.util.DDL;
import com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfiguration;
import com.liferay.dynamic.data.lists.web.internal.display.context.DDLDisplayContext;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PortletPreferencesException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-dynamic-data-lists-display",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portal-javascript=/o/dynamic-data-mapping-web/js/legacy/custom_fields.js",
		"com.liferay.portlet.header-portal-javascript=/o/dynamic-data-mapping-web/js/legacy/main.js",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Dynamic Data Lists Display",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view_selected_record_set.jsp",
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DDLDisplayPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			setDDLRecordRequestAttribute(renderRequest);

			setDDLRecordSetRequestAttribute(renderRequest);

			DDLDisplayContext ddlDisplayContext = new DDLDisplayContext(
				renderRequest, renderResponse, _ddl, _ddlRecordSetLocalService,
				_ddlWebConfiguration, _ddmPermissionSupport,
				_ddmStorageEngineManager, _ddmTemplateLocalService);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, ddlDisplayContext);
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
	protected void activate(Map<String, Object> properties) {
		modified(properties);

		_portletRegistry.registerAlias(
			_ALIAS, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchRecordSetException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				PortletPreferencesException.MustBeStrict.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include(templatePath + "error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof NoSuchRecordSetException ||
			throwable instanceof PortletPreferencesException ||
			throwable instanceof PrincipalException) {

			return true;
		}

		return false;
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_ddlWebConfiguration = ConfigurableUtil.createConfigurable(
			DDLWebConfiguration.class, properties);
	}

	protected void setDDLRecordRequestAttribute(RenderRequest renderRequest)
		throws PortalException {

		long recordId = ParamUtil.getLong(renderRequest, "recordId");

		DDLRecord record = null;

		if (recordId > 0) {
			record = _ddlRecordService.getRecord(recordId);
		}

		renderRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD, record);
	}

	protected void setDDLRecordSetRequestAttribute(RenderRequest renderRequest)
		throws PortalException {

		long recordSetId = PrefsParamUtil.getLong(
			renderRequest.getPreferences(), renderRequest, "recordSetId");

		DDLRecordSet recordSet = null;

		if (recordSetId > 0) {
			recordSet = _ddlRecordSetService.getRecordSet(recordSetId);
		}

		renderRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD_SET, recordSet);
	}

	private static final String _ALIAS = "dynamic-data-list";

	private static final Log _log = LogFactoryUtil.getLog(
		DDLDisplayPortlet.class);

	@Reference
	private DDL _ddl;

	@Reference
	private DDLRecordService _ddlRecordService;

	@Reference
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Reference
	private DDLRecordSetService _ddlRecordSetService;

	private volatile DDLWebConfiguration _ddlWebConfiguration;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

}