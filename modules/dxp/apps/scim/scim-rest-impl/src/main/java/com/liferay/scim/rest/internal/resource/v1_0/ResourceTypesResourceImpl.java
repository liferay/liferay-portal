/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.scim.rest.internal.util.ScimUtil;
import com.liferay.scim.rest.resource.v1_0.ResourceTypesResource;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

/**
 * @author Alvaro Saugar
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/resource-types.properties",
	scope = ServiceScope.PROTOTYPE, service = ResourceTypesResource.class
)
public class ResourceTypesResourceImpl extends BaseResourceTypesResourceImpl {

	@Override
	public Object getV2ResourceTypeById(String id) throws Exception {
		return ScimUtil.buildResponse(_getSCIMResponse(id));
	}

	@Override
	public Object getV2ResourceTypes() throws Exception {
		return getV2ResourceTypeById(null);
	}

	private String _getResourceTypeJSON(String id)
		throws AbstractCharonException {

		if (_resourceTypeFileNames.containsKey(id)) {
			JSONObject resourceTypeJSONObject = _read(
				_resourceTypeFileNames.get(id));

			return resourceTypeJSONObject.toString();
		}

		throw new NotFoundException(
			"No resource type found with resource type ID " + id);
	}

	private String _getResourceTypesJSON() throws AbstractCharonException {
		return JSONUtil.put(
			"itemsPerPage", 2
		).put(
			"Resources",
			() -> {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				for (Map.Entry<String, String> entry :
						_resourceTypeFileNames.entrySet()) {

					jsonArray.put(_read(entry.getValue()));
				}

				return jsonArray;
			}
		).put(
			"schemas",
			JSONUtil.put("urn:ietf:params:scim:api:messages:2.0:ListResponse")
		).put(
			"startIndex", 1
		).put(
			"totalResults", _resourceTypeFileNames.size()
		).toString();
	}

	private SCIMResponse _getSCIMResponse(String id) {
		try {
			ScimUtil.getScimClientOAuth2ApplicationConfiguration(
				contextCompany.getCompanyId(), _configurationAdmin);

			if (Validator.isNull(id)) {
				return new SCIMResponse(
					ResponseCodeConstants.CODE_OK, _getResourceTypesJSON(),
					ScimUtil.getHeaders(SCIMConstants.RESOURCE_TYPE_ENDPOINT));
			}

			String resourceTypeJSON = _getResourceTypeJSON(id);

			if (Validator.isNull(resourceTypeJSON)) {
				throw new NotFoundException(
					"No resource type found with resourceType ID " + id);
			}

			return new SCIMResponse(
				ResponseCodeConstants.CODE_OK, resourceTypeJSON,
				ScimUtil.getHeaders(SCIMConstants.RESOURCE_TYPE_ENDPOINT));
		}
		catch (AbstractCharonException abstractCharonException) {
			return AbstractResourceManager.encodeSCIMException(
				abstractCharonException);
		}
		catch (Exception exception) {
			if (exception instanceof ConflictException) {
				return AbstractResourceManager.encodeSCIMException(
					(ConflictException)exception);
			}

			throw exception;
		}
	}

	private JSONObject _read(String fileName) throws InternalErrorException {
		try {
			Bundle bundle = FrameworkUtil.getBundle(
				ResourceTypesResourceImpl.class);

			JSONObject resourceTypeJSONObject = _jsonFactory.createJSONObject(
				URLUtil.toString(
					bundle.getResource("META-INF/resource-types/" + fileName)));

			JSONObject metaJSONObject = resourceTypeJSONObject.getJSONObject(
				"meta");

			String resourceEndpointURL =
				AbstractResourceManager.getResourceEndpointURL(
					SCIMConstants.RESOURCE_TYPE_ENDPOINT);

			metaJSONObject.put(
				"location",
				resourceEndpointURL + metaJSONObject.getString("location"));

			resourceTypeJSONObject.put(
				"schemas",
				JSONUtil.put(
					"urn:ietf:params:scim:schemas:core:2.0:ResourceType"));

			return resourceTypeJSONObject;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new InternalErrorException("Unable to read " + fileName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceTypesResourceImpl.class);

	private static final Map<String, String> _resourceTypeFileNames = Map.of(
		"Group", "group.json", "User", "user.json");

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private JSONFactory _jsonFactory;

}