/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Nilton Vieira
 */
@RequestMapping("/object/action/permission")
@RestController
public class ObjectActionPermissionRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject jsonObject = new JSONObject(json);

		_updateObjectEntryPermissions(
			new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/object-admin/v1.0/object-definitions/" +
							jsonObject.getLong("objectDefinitionId")
					).build(
					).toUri())),
			jsonObject.getLong("classPK"));

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private JSONArray _filterObjectEntryPermissionsJSONArray(
		JSONArray currentObjectEntryPermissionsJSONArray,
		JSONArray newObjectEntryPermissionsJSONArray) {

		List<String> roleNames = new ArrayList<>();

		for (int i = 0; i < newObjectEntryPermissionsJSONArray.length(); i++) {
			JSONObject jsonObject =
				newObjectEntryPermissionsJSONArray.getJSONObject(i);

			JSONArray actionIdsJSONArray = jsonObject.getJSONArray("actionIds");

			List<Object> actionIds = actionIdsJSONArray.toList();

			actionIds.removeIf(
				actionId -> !_allowedActionIds.contains(actionId));

			jsonObject.put("actionIds", actionIds);

			roleNames.add(jsonObject.getString("roleName"));
		}

		for (int i = 0; i < currentObjectEntryPermissionsJSONArray.length();
			 i++) {

			JSONObject jsonObject =
				currentObjectEntryPermissionsJSONArray.getJSONObject(i);

			if (!roleNames.contains(jsonObject.getString("roleName"))) {
				newObjectEntryPermissionsJSONArray.put(
					new JSONObject(
					).put(
						"actionIds", new JSONArray()
					).put(
						"roleName", jsonObject.getString("roleName")
					));
			}
		}

		return newObjectEntryPermissionsJSONArray;
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-learn-etc-spring-boot-oahs");
	}

	private JSONArray _getObjectEntryPermissionsJSONArray(
		long objectEntryId, String restContextPath) {

		return new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					StringBundler.concat(
						restContextPath, "/", objectEntryId, "/permissions")
				).build(
				).toUri())
		).getJSONArray(
			"items"
		);
	}

	private Map<Long, List<Object>> _getRelatedObjectEntries(
		JSONArray jsonArray, long objectEntryId, String restContextPath) {

		Map<Long, List<Object>> map = new HashMap<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject1 = jsonArray.getJSONObject(i);

			if (_excludedObjectDefinitionsExternalReferenceCodes.contains(
					jsonObject1.getString(
						"objectDefinitionExternalReferenceCode2"))) {

				continue;
			}

			JSONObject jsonObject2 = new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromPath(
						StringBundler.concat(
							restContextPath, "/", objectEntryId, "/",
							jsonObject1.getString("name"))
					).queryParam(
						"fields", "id"
					).queryParam(
						"pageSize", 500
					).build(
					).toUri()));

			map.put(
				jsonObject1.getLong("objectDefinitionId2"),
				jsonObject2.getJSONArray(
					"items"
				).toList());
		}

		return map;
	}

	private void _updateObjectEntryPermissions(
		JSONObject jsonObject, long objectEntryId) {

		for (Map.Entry<Long, List<Object>> entry :
				_getRelatedObjectEntries(
					jsonObject.getJSONArray("objectRelationships"),
					objectEntryId, jsonObject.getString("restContextPath")
				).entrySet()) {

			JSONObject objectDefinitionJSONObject = new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/object-admin/v1.0/object-definitions/" +
							entry.getKey()
					).build(
					).toUri()));

			for (Object object : entry.getValue()) {
				Map<String, Object> map = (Map<String, Object>)object;

				put(
					_getAuthorization(),
					_filterObjectEntryPermissionsJSONArray(
						_getObjectEntryPermissionsJSONArray(
							GetterUtil.getLong(map.get("id")),
							objectDefinitionJSONObject.getString(
								"restContextPath")),
						_getObjectEntryPermissionsJSONArray(
							objectEntryId,
							jsonObject.getString("restContextPath"))
					).toString(),
					UriComponentsBuilder.fromPath(
						StringBundler.concat(
							objectDefinitionJSONObject.getString(
								"restContextPath"),
							"/", GetterUtil.getLong(map.get("id")),
							"/permissions")
					).build(
					).toUri());

				_updateObjectEntryPermissions(
					objectDefinitionJSONObject,
					GetterUtil.getLong(map.get("id")));
			}
		}
	}

	private static final List<String> _allowedActionIds = Arrays.asList(
		"DELETE", "PERMISSIONS", "UPDATE", "VIEW");
	private static final List<String>
		_excludedObjectDefinitionsExternalReferenceCodes = Arrays.asList(
			"T4T14_ENROLLMENTS", "T4T14_QUIZ_ANSWER", "T4T14_QUIZ_QUESTION");

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}