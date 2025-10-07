/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.stream.hub;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.client.dto.v1_0.Status;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectActionResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mahmoud Hussein Tayem
 */
@RequestMapping("/stream-hub-configurations")
@RestController
public class StreamHubConfigurationRestController extends BaseRestController {

	@PostMapping("/configure")
	public ResponseEntity<String> postConfigure(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		JSONObject configurationJSONObject = _getConfigurationJSONObject(json);

		long objectDefinitionId = configurationJSONObject.getLong(
			"objectDefinitionId");

		_unconfigure(objectDefinitionId);

		_configure(objectDefinitionId, _getTypes(configurationJSONObject));

		return ResponseEntity.ok("");
	}

	@PostMapping("/unconfigure")
	public ResponseEntity<String> postUnconfigure(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		JSONObject configurationJSONObject = _getConfigurationJSONObject(json);

		long objectDefinitionId = configurationJSONObject.getLong(
			"objectDefinitionId");

		_unconfigure(objectDefinitionId);

		return ResponseEntity.ok("");
	}

	private void _configure(long objectDefinitionId, List<String> types)
		throws Exception {

		ObjectActionResource objectActionResource = _getObjectActionResource();

		List<ObjectAction> objectActions = new ArrayList<>();

		for (String type : types) {
			ObjectAction objectAction = _getObjectAction(
				objectDefinitionId, type);

			if (objectAction == null) {
				continue;
			}

			objectActions.add(objectAction);
		}

		objectActionResource.postObjectDefinitionObjectActionBatch(
			objectDefinitionId, "", objectActions);
	}

	private JSONObject _getConfigurationJSONObject(String json) {
		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
			"objectEntry");

		JSONObject valuesJSONObject = objectEntryJSONObject.getJSONObject(
			"values");

		return new JSONObject(valuesJSONObject.get("configuration"));
	}

	private ObjectAction _getObjectAction(
		long objectDefinitionId, String type) {

		if (!type.equals("onAfterAdd") && !type.equals("onAfterDelete") &&
			!type.equals("onAfterUpdate") && !type.equals("standalone")) {

			return null;
		}

		ObjectAction objectAction = new ObjectAction();

		objectAction.setActive(() -> true);

		if (type.equals("standalone")) {
			objectAction.setErrorMessage(
				() -> Map.of(
					"en_US",
					"Error while executing Event Streaming for Standalone"));
		}

		objectAction.setExternalReferenceCode(
			() -> new StringBuilder(
			).append(
				"T4L9_STREAM_HUB_EVENT_"
			).append(
				objectDefinitionId
			).append(
				"_"
			).append(
				type
			).toString());
		objectAction.setLabel(
			() -> {
				String value = null;

				String prefix = "Stream Events - ";

				if (type.equals("onAfterAdd")) {
					value = prefix + "On Add";
				}
				else if (type.equals("onAfterDelete")) {
					value = prefix + "On Delete";
				}
				else if (type.equals("onAfterUpdate")) {
					value = prefix + "On Update";
				}
				else {
					value = prefix + "Standalone";
				}

				return Map.of("en_US", value);
			});
		objectAction.setName(() -> "stream" + objectDefinitionId + type);
		objectAction.setObjectActionExecutorKey(
			() -> {
				String prefix =
					"function#liferay-streamhub-etc-spring-boot-object-action-";

				if (type.equals("onAfterAdd")) {
					return prefix + "1";
				}
				else if (type.equals("onAfterDelete")) {
					return prefix + "3";
				}
				else if (type.equals("onAfterUpdate")) {
					return prefix + "2";
				}

				return prefix + "4";
			});
		objectAction.setObjectActionTriggerKey(() -> type);
		objectAction.setParameters(Collections::emptyMap);
		objectAction.setStatus(
			() -> new Status() {
				{
					setCode(() -> 0);
					setLabel(() -> "Never Ran");
				}
			});

		return objectAction;
	}

	private ObjectActionResource _getObjectActionResource() {
		return ObjectActionResource.builder(
		).header(
			"Authorization",
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-streamhub-etc-spring-boot-oahs")
		).endpoint(
			_lxcDXPMainDomain, _lxcDXPServerProtocol
		).build();
	}

	private List<String> _getTypes(JSONObject configurationJSONObject) {
		List<String> types = new ArrayList<>();

		JSONArray objectActionsJSONArray = configurationJSONObject.getJSONArray(
			"objectAction");

		for (int i = 0; i < objectActionsJSONArray.length(); i++) {
			JSONObject objectActionJSONObject =
				objectActionsJSONArray.getJSONObject(i);

			types.add(objectActionJSONObject.getString("type"));
		}

		return types;
	}

	private void _unconfigure(long objectDefinitionId) throws Exception {
		ObjectActionResource objectActionResource = _getObjectActionResource();

		Page<ObjectAction> page =
			objectActionResource.getObjectDefinitionObjectActionsPage(
				objectDefinitionId, "", Pagination.of(0, 0), "");

		for (ObjectAction objectAction : page.getItems()) {
			String externalReferenceCode =
				objectAction.getExternalReferenceCode();

			if (externalReferenceCode.startsWith("T4L9_STREAM_HUB_EVENT_")) {
				objectActionResource.deleteObjectAction(objectAction.getId());
			}
		}
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${com.liferay.lxc.dxp.mainDomain}")
	private String _lxcDXPMainDomain;

	@Value("${com.liferay.lxc.dxp.server.protocol}")
	private String _lxcDXPServerProtocol;

}