/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal.data.creator;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Matthew Kong
 */
public class LiferayUserGroupsDataCreator extends DataCreator {

	public LiferayUserGroupsDataCreator(
		ContactsEngineClient contactsEngineClient, FaroProject faroProject,
		String dataSourceId) {

		super(
			contactsEngineClient, faroProject, "osbasahdxpraw", "user-groups");

		_dataSourceId = dataSourceId;
	}

	@Override
	public String getClassName() {
		return "com.liferay.portal.kernel.model.UserGroup";
	}

	@Override
	public String getClassPKFieldName() {
		return "userGroupId";
	}

	@Override
	protected Map<String, Object> doCreate(Object[] params) {
		return HashMapBuilder.<String, Object>put(
			"dataSourceId", _dataSourceId
		).put(
			"fields",
			HashMapBuilder.<String, Object>put(
				"name", country.name()
			).put(
				"userGroupId", number.randomNumber(8, false)
			).build()
		).put(
			"id", number.randomNumber(8, false)
		).build();
	}

	private final String _dataSourceId;

}