/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.service;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Ryan Schuhler
 */
@Component
public class VersionListTypeService extends BaseService {

	@Scheduled(cron = "${liferay.customer.version.list.type.cron}")
	public void scheduled() throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Updating list type definitions");
		}

		JSONArray releasesJSONArray = new JSONArray(
			get(
				StringPool.BLANK,
				UriComponentsBuilder.fromUriString(
					_liferayCustomerVersionListTypeReleasesURL
				).build(
				).toUri()));

		Map<String, List<String>> versionsMap = _getVersionsMap(
			releasesJSONArray);

		List<String> dxpMajorVersionsMap = versionsMap.get("dxpMajor");

		_updateListTypeDefinition(
			_liferayCustomerVersionListTypeDXPMajorERC, "DXP Major Version",
			dxpMajorVersionsMap);

		List<String> dxpMinorVersionsMap = versionsMap.get("dxpMinor");

		_updateListTypeDefinition(
			_liferayCustomerVersionListTypeDXPMinorERC, "DXP Minor Version",
			dxpMinorVersionsMap);

		List<String> dxpMinorVersionsAndPortalMajorVersionsMap =
			new ArrayList<>();

		dxpMinorVersionsAndPortalMajorVersionsMap.addAll(dxpMinorVersionsMap);

		Collections.addAll(
			dxpMinorVersionsAndPortalMajorVersionsMap,
			_liferayCustomerVersionListTypePortalMajorVersions.split(
				StringPool.COMMA));

		_updateListTypeDefinition(
			_liferayCustomerVersionListTypeDXPMinorPortalMajorERC,
			"DXP Minor Version and Portal Major Version",
			dxpMinorVersionsAndPortalMajorVersionsMap);
	}

	private void _addVersion(
		String key, String version, Map<String, List<String>> versionsMap) {

		List<String> versions = versionsMap.get(key);

		if ((versions != null) && !versions.contains(version)) {
			versions.add(version);

			Collections.sort(versions);
		}
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-customer-etc-spring-boot-oahs");
	}

	private Map<String, List<String>> _getVersionsMap(
		JSONArray releasesJSONArray) {

		Map<String, List<String>> versionsMap =
			HashMapBuilder.<String, List<String>>put(
				"dxpMajor", new ArrayList<>()
			).put(
				"dxpMinor", new ArrayList<>()
			).build();

		for (int i = 0; i < releasesJSONArray.length(); i++) {
			JSONObject releaseJSONObject = releasesJSONArray.getJSONObject(i);

			String product = releaseJSONObject.getString("product");
			String productGroupVersion = releaseJSONObject.getString(
				"productGroupVersion");

			if (Validator.isNull(product) ||
				Validator.isNull(productGroupVersion) ||
				!product.equals("dxp")) {

				continue;
			}

			String productMajorVersion = releaseJSONObject.optString(
				"productMajorVersion", null);

			if (Validator.isNull(productMajorVersion)) {
				productMajorVersion =
					StringUtil.toUpperCase(product) + StringPool.SPACE +
						StringUtil.toUpperCase(productGroupVersion);
			}

			_addVersion(product + "Major", productMajorVersion, versionsMap);

			String productVersion = releaseJSONObject.getString(
				"productVersion");

			_addVersion(product + "Minor", productVersion, versionsMap);
		}

		return versionsMap;
	}

	private void _updateListTypeDefinition(
			String externalReferenceCode, String name, List<String> values)
		throws Exception {

		JSONObject listTypeDefinitionJSONObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-list-type/v1.0/list-type-definitions" +
						"/by-external-reference-code/" + externalReferenceCode
				).build(
				).toUri()));

		JSONArray listTypeEntriesJSONArray = new JSONArray();

		for (String value : values) {
			if (Validator.isNull(value)) {
				continue;
			}

			listTypeEntriesJSONArray.put(
				new JSONObject(
				).put(
					"externalReferenceCode",
					value.toUpperCase(
					).replaceAll(
						"[^A-Z0-9]", "_"
					)
				).put(
					"key",
					value.toLowerCase(
					).replaceAll(
						"[^a-z0-9]", ""
					)
				).put(
					"name", value
				).put(
					"name_i18n",
					new JSONObject(
					).put(
						"en-US", value
					)
				));
		}

		put(
			_getAuthorization(),
			new JSONObject(
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"listTypeEntries", listTypeEntriesJSONArray
			).put(
				"name", name
			).put(
				"name_i18n",
				new JSONObject(
				).put(
					"en-US", name
				)
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/headless-admin-list-type/v1.0/list-type-definitions/" +
					listTypeDefinitionJSONObject.getInt("id")
			).build(
			).toUri());

		if (_log.isInfoEnabled()) {
			_log.info("Updated list type definition " + externalReferenceCode);
		}
	}

	private static final Log _log = LogFactory.getLog(
		VersionListTypeService.class);

	@Value("${liferay.customer.version.list.type.dxp.major.erc}")
	private String _liferayCustomerVersionListTypeDXPMajorERC;

	@Value("${liferay.customer.version.list.type.dxp.minor.erc}")
	private String _liferayCustomerVersionListTypeDXPMinorERC;

	@Value("${liferay.customer.version.list.type.dxp.minor.portal.major.erc}")
	private String _liferayCustomerVersionListTypeDXPMinorPortalMajorERC;

	@Value("${liferay.customer.version.list.type.portal.major.versions}")
	private String _liferayCustomerVersionListTypePortalMajorVersions;

	@Value("${liferay.customer.version.list.type.releases.url}")
	private String _liferayCustomerVersionListTypeReleasesURL;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}