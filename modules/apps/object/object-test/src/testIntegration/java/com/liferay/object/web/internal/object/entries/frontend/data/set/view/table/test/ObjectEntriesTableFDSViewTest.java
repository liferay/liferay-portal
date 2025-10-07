/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectViewColumn;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.service.persistence.ObjectViewColumnPersistence;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nathaly Gomes
 */
@RunWith(Arquillian.class)
public class ObjectEntriesTableFDSViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFDSTableSchema() throws Exception {
		User user = UserTestUtil.addUser();

		user.setTimeZoneId("Europe/Madrid");

		user = _userLocalService.updateUser(user);

		String originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(user.getUserId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectViewLocalService.addObjectView(
			user.getUserId(), objectDefinition.getObjectDefinitionId(), true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Arrays.asList(
				_createObjectViewColumn("createDate"),
				_createObjectViewColumn("modifiedDate")),
			Collections.emptyList(), Collections.emptyList());

		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(
			objectDefinition.getPortletId());

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		_assertTimeZone(fdsTableSchema, "dateCreated", user);
		_assertTimeZone(fdsTableSchema, "dateModified", user);

		PrincipalThreadLocal.setName(originalName);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		_userLocalService.deleteUser(user);
	}

	private void _assertTimeZone(
		FDSTableSchema fdsTableSchema, String fieldName, User user) {

		Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap =
			fdsTableSchema.getFDSTableSchemaFieldsMap();

		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			fieldName);

		JSONObject jsonObject = fdsTableSchemaField.toJSONObject();

		jsonObject = jsonObject.getJSONObject("format");

		Assert.assertEquals(
			user.getTimeZoneId(), jsonObject.getString("timeZone"));
	}

	private ObjectViewColumn _createObjectViewColumn(String objectFieldName) {
		ObjectViewColumn objectViewColumn = _objectViewColumnPersistence.create(
			0);

		objectViewColumn.setLabelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));
		objectViewColumn.setObjectFieldName(objectFieldName);
		objectViewColumn.setPriority(0);

		return objectViewColumn;
	}

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectViewColumnPersistence _objectViewColumnPersistence;

	@Inject
	private ObjectViewLocalService _objectViewLocalService;

	@Inject
	private UserLocalService _userLocalService;

}