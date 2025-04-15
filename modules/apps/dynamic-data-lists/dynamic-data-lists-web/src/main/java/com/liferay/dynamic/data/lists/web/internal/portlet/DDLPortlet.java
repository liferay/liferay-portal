/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLWebKeys;
import com.liferay.dynamic.data.lists.exception.NoSuchRecordException;
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
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PortletPreferencesException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
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
 * @author Marcellus Tavares
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-dynamic-data-lists",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portal-javascript=/o/dynamic-data-mapping-web/js/legacy/custom_fields.js",
		"com.liferay.portlet.header-portal-javascript=/o/dynamic-data-mapping-web/js/legacy/main.js",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.icon=/icons/dynamic_data_lists.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Dynamic Data Lists",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DDLPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		super.processAction(actionRequest, actionResponse);

		_setCloseRedirect(actionRequest);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-196935")) {
			return;
		}

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
		catch (NoSuchRecordException | NoSuchRecordSetException exception) {

			// Let this slide because the user can manually input an record set
			// key for a new record set that does not yet exist

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());
		}

		super.render(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddlWebConfiguration = ConfigurableUtil.createConfigurable(
			DDLWebConfiguration.class, properties);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchRecordException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchRecordSetException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				PortletPreferencesException.MustBeStrict.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
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

		long recordSetId = ParamUtil.getLong(renderRequest, "recordSetId");

		DDLRecordSet recordSet = null;

		if (recordSetId > 0) {
			recordSet = _ddlRecordSetService.getRecordSet(recordSetId);
		}

		DDLRecord record = (DDLRecord)renderRequest.getAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD);

		if ((recordSet == null) && (record != null)) {
			recordSet = record.getRecordSet();
		}

		renderRequest.setAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD_SET, recordSet);
	}

	private void _setCloseRedirect(ActionRequest actionRequest) {
		String closeRedirect = ParamUtil.getString(
			actionRequest, "closeRedirect");

		if (Validator.isNull(closeRedirect)) {
			return;
		}

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_CLOSE_REDIRECT,
			closeRedirect);
	}

	private static final Log _log = LogFactoryUtil.getLog(DDLPortlet.class);

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
	private Portal _portal;

}