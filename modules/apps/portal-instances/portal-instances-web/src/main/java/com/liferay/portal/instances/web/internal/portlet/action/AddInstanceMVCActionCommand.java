/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.batch.engine.jaxrs.uri.BatchEngineUriInfo;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceResource;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Víctor Galán Grande
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES,
		"mvc.command.name=/portal_instances/add_instance"
	},
	service = MVCActionCommand.class
)
public class AddInstanceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_validateAdmin(actionRequest);
			_validateCompany(actionRequest);

			_addPortalInstance(actionRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			jsonObject.put(
				"error",
				_language.get(
					actionRequest.getLocale(), _getErrorMessageKey(exception)));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private void _addPortalInstance(ActionRequest actionRequest)
		throws Exception {

		PortalInstanceResource portalInstanceResource =
			_componentServiceObjects.getService();

		try {
			portalInstanceResource.setContextAcceptLanguage(
				_getAcceptLanguage(actionRequest));
			portalInstanceResource.setContextCompany(
				_portal.getCompany(actionRequest));
			portalInstanceResource.setContextHttpServletRequest(
				_getHttpServletRequest(actionRequest));
			portalInstanceResource.setContextUriInfo(
				new BatchEngineUriInfo.Builder(
				).build());
			portalInstanceResource.setContextUser(
				_portal.getUser(actionRequest));
			portalInstanceResource.setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResourceFactory.create());

			portalInstanceResource.postPortalInstanceBatch(
				null,
				Collections.singletonList(
					_getPortalInstanceMap(actionRequest)));
		}
		finally {
			_componentServiceObjects.ungetService(portalInstanceResource);
		}
	}

	private AcceptLanguage _getAcceptLanguage(ActionRequest actionRequest) {
		Locale locale = _portal.getLocale(actionRequest);

		return new AcceptLanguage() {

			@Override
			public List<Locale> getLocales() {
				return Collections.singletonList(locale);
			}

			@Override
			public String getPreferredLanguageId() {
				return LocaleUtil.toLanguageId(locale);
			}

			@Override
			public Locale getPreferredLocale() {
				return locale;
			}

		};
	}

	private Map<String, String> _getAdminMap(ActionRequest actionRequest) {
		String defaultAdminEmailAddress = ParamUtil.getString(
			actionRequest, "defaultAdminEmailAddress");

		if (Validator.isNull(defaultAdminEmailAddress)) {
			return null;
		}

		return HashMapBuilder.put(
			"emailAddress", defaultAdminEmailAddress
		).put(
			"familyName",
			ParamUtil.getString(actionRequest, "defaultAdminLastName")
		).put(
			"givenName",
			ParamUtil.getString(actionRequest, "defaultAdminFirstName")
		).put(
			"middleName",
			ParamUtil.getString(actionRequest, "defaultAdminMiddleName")
		).put(
			"password",
			ParamUtil.getString(actionRequest, "defaultAdminPassword")
		).put(
			"screenName",
			ParamUtil.getString(actionRequest, "defaultAdminScreenName")
		).build();
	}

	private String _getErrorMessageKey(Exception exception) {
		if (exception instanceof CompanyMaxUsersException) {
			return "please-enter-a-valid-max-users";
		}

		if (exception instanceof CompanyMxException) {
			return "please-enter-a-valid-mail-domain";
		}

		if (exception instanceof CompanyVirtualHostException) {
			return "please-enter-a-valid-virtual-host";
		}

		if (exception instanceof CompanyWebIdException) {
			return "please-enter-a-valid-web-id";
		}

		if (exception instanceof UserEmailAddressException) {
			return "please-enter-a-valid-email-address";
		}

		if (exception instanceof UserPasswordException) {
			return "please-enter-a-valid-password";
		}

		if (exception instanceof UserScreenNameException) {
			return "please-enter-a-valid-screen-name";
		}

		return "an-unexpected-error-occurred";
	}

	private HttpServletRequest _getHttpServletRequest(
		ActionRequest actionRequest) {

		return new HttpServletRequestWrapper(
			_portal.getHttpServletRequest(actionRequest)) {

			@Override
			public String getHeader(String name) {
				if (StringUtil.equalsIgnoreCase(
						name, HttpHeaders.CONTENT_TYPE)) {

					return ContentTypes.APPLICATION_JSON;
				}

				return super.getHeader(name);
			}

		};
	}

	private Map<String, Object> _getPortalInstanceMap(
		ActionRequest actionRequest) {

		return HashMapBuilder.<String, Object>put(
			"active", ParamUtil.getBoolean(actionRequest, "active")
		).put(
			"admin", () -> _getAdminMap(actionRequest)
		).put(
			"domain", ParamUtil.getString(actionRequest, "mx")
		).put(
			"maxUsers", ParamUtil.getInteger(actionRequest, "maxUsers")
		).put(
			"portalInstanceId", ParamUtil.getString(actionRequest, "webId")
		).put(
			"siteInitializerKey", () -> _getSiteInitializerKey(actionRequest)
		).put(
			"virtualHost", ParamUtil.getString(actionRequest, "virtualHostname")
		).build();
	}

	private String _getSiteInitializerKey(ActionRequest actionRequest) {
		String siteInitializerKey = ParamUtil.getString(
			actionRequest, "siteInitializerKey");

		if (Validator.isNull(siteInitializerKey)) {
			return null;
		}

		return siteInitializerKey;
	}

	private void _validateAdmin(ActionRequest actionRequest)
		throws PortalException {

		if (Validator.isNotNull(PropsValues.DEFAULT_ADMIN_PASSWORD)) {
			return;
		}

		if (Validator.isNull(
				ParamUtil.getString(
					actionRequest, "defaultAdminEmailAddress"))) {

			throw new UserEmailAddressException.MustNotBeNull();
		}

		if (Validator.isNull(
				ParamUtil.getString(actionRequest, "defaultAdminPassword"))) {

			throw new UserPasswordException.MustNotBeNull(0);
		}

		if (Validator.isNull(
				ParamUtil.getString(actionRequest, "defaultAdminScreenName"))) {

			throw new UserScreenNameException.MustNotBeNull();
		}
	}

	private void _validateCompany(ActionRequest actionRequest)
		throws PortalException {

		_companyLocalService.validateCompany(
			ParamUtil.getString(actionRequest, "webId"),
			ParamUtil.getString(actionRequest, "virtualHostname"),
			ParamUtil.getString(actionRequest, "mx"),
			ParamUtil.getInteger(actionRequest, "maxUsers"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddInstanceMVCActionCommand.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PortalInstanceResource>
		_componentServiceObjects;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory;

}