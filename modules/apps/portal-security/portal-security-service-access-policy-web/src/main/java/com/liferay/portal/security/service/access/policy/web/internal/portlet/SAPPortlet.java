/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.web.internal.portlet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionMapping;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionsManager;
import com.liferay.portal.security.service.access.policy.service.SAPEntryService;
import com.liferay.portal.security.service.access.policy.web.internal.constants.SAPPortletKeys;
import com.liferay.portal.security.service.access.policy.web.internal.constants.SAPWebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portal-security-service-access-policy-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"jakarta.portlet.display-name=Service Access Policy",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=Service Access Policy",
		"jakarta.portlet.info.short-title=Service Access Policy",
		"jakarta.portlet.info.title=Service Access Policy",
		"jakarta.portlet.init-param.clear-request-parameters=true",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SAPPortletKeys.SERVICE_ACCESS_POLICY,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SAPPortlet extends MVCPortlet {

	public void deleteSAPEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long sapEntryId = ParamUtil.getLong(actionRequest, "sapEntryId");

		_sapEntryService.deleteSAPEntry(sapEntryId);
	}

	public void getActionMethodNames(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		PrintWriter printWriter = resourceResponse.getWriter();

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		String contextName = ParamUtil.getString(
			resourceRequest, "contextName");

		Map<String, Set<JSONWebServiceActionMapping>>
			jsonWebServiceActionMappingsMap =
				_getServiceJSONWebServiceActionMappingsMap(contextName);

		String serviceClassName = ParamUtil.getString(
			resourceRequest, "serviceClassName");

		Set<JSONWebServiceActionMapping> jsonWebServiceActionMappingsSet =
			jsonWebServiceActionMappingsMap.get(serviceClassName);

		for (JSONWebServiceActionMapping jsonWebServiceActionMapping :
				jsonWebServiceActionMappingsSet) {

			JSONObject jsonObject = JSONUtil.put(
				"actionMethodName",
				() -> {
					Method method =
						jsonWebServiceActionMapping.getActionMethod();

					return method.getName();
				});

			jsonArray.put(jsonObject);
		}

		printWriter.write(jsonArray.toString());
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String mvcPath = ParamUtil.getString(renderRequest, "mvcPath");

		if (mvcPath.equals("/edit_entry.jsp")) {
			renderRequest.setAttribute(
				SAPWebKeys.SERVICE_CLASS_NAMES_TO_CONTEXT_NAMES,
				_getServiceClassNamesToContextNamesJSONArray());
		}

		super.render(renderRequest, renderResponse);
	}

	public void updateSAPEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long sapEntryId = ParamUtil.getLong(actionRequest, "sapEntryId");

		String allowedServiceSignatures = ParamUtil.getString(
			actionRequest, "allowedServiceSignatures");
		boolean defaultSAPEntry = ParamUtil.getBoolean(
			actionRequest, "defaultSAPEntry");
		boolean enabled = ParamUtil.getBoolean(actionRequest, "enabled");
		String name = ParamUtil.getString(actionRequest, "name");
		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		if (sapEntryId > 0) {
			_sapEntryService.updateSAPEntry(
				sapEntryId, allowedServiceSignatures, defaultSAPEntry, enabled,
				name, titleMap, serviceContext);
		}
		else {
			_sapEntryService.addSAPEntry(
				allowedServiceSignatures, defaultSAPEntry, enabled, name,
				titleMap, serviceContext);
		}
	}

	private JSONArray _getServiceClassNamesToContextNamesJSONArray() {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Set<String> contextNames =
			_jsonWebServiceActionsManager.getContextNames();

		for (String contextName : contextNames) {
			Map<String, Set<JSONWebServiceActionMapping>>
				jsonWebServiceActionMappingsMap =
					_getServiceJSONWebServiceActionMappingsMap(contextName);

			for (Map.Entry<String, Set<JSONWebServiceActionMapping>> entry :
					jsonWebServiceActionMappingsMap.entrySet()) {

				jsonArray.put(
					JSONUtil.put(
						"contextName",
						() -> {
							Set<JSONWebServiceActionMapping>
								jsonWebServiceActionMappingsSet =
									entry.getValue();

							Iterator<JSONWebServiceActionMapping> iterator =
								jsonWebServiceActionMappingsSet.iterator();

							JSONWebServiceActionMapping
								firstJSONWebServiceActionMapping =
									iterator.next();

							return firstJSONWebServiceActionMapping.
								getContextName();
						}
					).put(
						"serviceClassName", entry.getKey()
					));
			}
		}

		return jsonArray;
	}

	private Map<String, Set<JSONWebServiceActionMapping>>
		_getServiceJSONWebServiceActionMappingsMap(String contextName) {

		Map<String, Set<JSONWebServiceActionMapping>>
			jsonWebServiceActionMappingsMap = new LinkedHashMap<>();

		List<JSONWebServiceActionMapping> jsonWebServiceActionMappings =
			_jsonWebServiceActionsManager.getJSONWebServiceActionMappings(
				contextName);

		for (JSONWebServiceActionMapping jsonWebServiceActionMapping :
				jsonWebServiceActionMappings) {

			Object actionObject = jsonWebServiceActionMapping.getActionObject();

			Class<?> serviceClass = actionObject.getClass();

			Class<?>[] serviceInterfaces = serviceClass.getInterfaces();

			for (Class<?> serviceInterface : serviceInterfaces) {
				Annotation[] declaredAnnotations =
					serviceInterface.getDeclaredAnnotations();

				for (Annotation declaredAnnotation : declaredAnnotations) {
					if (!(declaredAnnotation instanceof AccessControlled)) {
						continue;
					}

					String serviceClassName = serviceInterface.getName();

					Set<JSONWebServiceActionMapping>
						jsonWebServiceActionMappingsSet =
							jsonWebServiceActionMappingsMap.get(
								serviceClassName);

					if (jsonWebServiceActionMappingsSet == null) {
						jsonWebServiceActionMappingsSet = new LinkedHashSet<>();

						jsonWebServiceActionMappingsMap.put(
							serviceClassName, jsonWebServiceActionMappingsSet);
					}

					jsonWebServiceActionMappingsSet.add(
						jsonWebServiceActionMapping);
				}
			}
		}

		return jsonWebServiceActionMappingsMap;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private JSONWebServiceActionsManager _jsonWebServiceActionsManager;

	@Reference
	private Localization _localization;

	@Reference
	private SAPEntryService _sapEntryService;

}