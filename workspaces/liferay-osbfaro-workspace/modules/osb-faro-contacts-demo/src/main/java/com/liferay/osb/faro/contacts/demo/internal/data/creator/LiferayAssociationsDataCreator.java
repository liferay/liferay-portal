/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal.data.creator;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Kong
 */
public class LiferayAssociationsDataCreator extends DataCreator {

	public LiferayAssociationsDataCreator(
		FaroProject faroProject, String dataSourceId,
		DataCreator... dataCreators) {

		super(null, faroProject, null, "associations");

		this.dataSourceId = dataSourceId;

		Collections.addAll(_dataCreators, dataCreators);
	}

	@Override
	protected void addData(List<Map<String, Object>> objects) {
		Http.Options options = new Http.Options();

		options.setHeaders(
			HashMapBuilder.put(
				"Content-Type", ContentTypes.APPLICATION_JSON
			).put(
				"OSB-Asah-Data-Source-ID", dataSourceId
			).put(
				"OSB-Asah-Project-ID", faroProject.getProjectId()
			).build());

		options.setLocation(
			FaroPropsValues.OSB_ASAH_PUBLISHER_URL + "/dxp-entities");
		options.setPost(true);

		try {
			options.setBody(
				_objectMapper.writeValueAsString(objects),
				ContentTypes.APPLICATION_JSON, StandardCharsets.UTF_8.name());

			HttpUtil.URLtoString(options);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	protected Map<String, Object> doCreate(Object[] params) {
		Map<String, Object> objectMap = new HashMap<>();

		DataCreator dataCreator = _dataCreators.get(
			random.nextInt(_dataCreators.size()));

		Map<String, Object> associatedObject = dataCreator.getRandom();

		if (dataCreator instanceof LiferayOrganizationsDataCreator) {
			objectMap.put(
				"classPK",
				associatedObject.get(dataCreator.getClassPKFieldName()));
		}
		else {
			Map<String, Object> fields =
				(Map<String, Object>)associatedObject.get("fields");

			objectMap.put(
				"classPK", fields.get(dataCreator.getClassPKFieldName()));
		}

		Map<String, Object> liferayUserMap = (Map<String, Object>)params[0];

		objectMap.put("emailAddress", liferayUserMap.get("emailAddress"));
		objectMap.put("userId", liferayUserMap.get("userId"));

		return HashMapBuilder.<String, Object>put(
			"action", "addAssociation"
		).put(
			"objectJSONObject", objectMap
		).put(
			"type", dataCreator.getClassName()
		).build();
	}

	protected String dataSourceId;

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayAssociationsDataCreator.class);

	private final List<DataCreator> _dataCreators = new ArrayList<>();
	private final ObjectMapper _objectMapper = new ObjectMapper();

}