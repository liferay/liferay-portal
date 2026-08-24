/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roselaine Marques
 */
@RunWith(Arquillian.class)
public class DefaultPermissionObjectBulkSelectionActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoExecute() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		_invokeDoExecute(objectEntry, TestPropsValues.getUser());

		ObjectEntry updatedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		Map<String, Serializable> values = updatedObjectEntry.getValues();

		Assert.assertEquals(
			_DEFAULT_PERMISSIONS, values.get("defaultPermissions"));
	}

	@Test
	public void testDoExecuteWithoutUpdatePermission() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		_user = UserTestUtil.addUser();

		try {
			_invokeDoExecute(objectEntry, _user);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					"User " + _user.getUserId() +
						" must have UPDATE permission for"));
		}

		ObjectEntry updatedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		Map<String, Serializable> values = updatedObjectEntry.getValues();

		Assert.assertNotEquals(
			_DEFAULT_PERMISSIONS, values.get("defaultPermissions"));
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"defaultPermissions"
					).build()));

		objectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());
	}

	private void _invokeDoExecute(ObjectEntry objectEntry, User user)
		throws Exception {

		ReflectionTestUtil.invoke(
			_defaultPermissionObjectBulkSelectionAction, "doExecute",
			new Class<?>[] {User.class, Map.class, Object.class}, user,
			HashMapBuilder.<String, Serializable>put(
				"defaultPermissions", _DEFAULT_PERMISSIONS
			).build(),
			objectEntry);
	}

	private static final String _DEFAULT_PERMISSIONS =
		"{\"UPDATED_BY_THE_BULK_ACTION\": true}";

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.bulk.selection.DefaultPermissionObjectBulkSelectionAction"
	)
	private BulkSelectionAction<Object>
		_defaultPermissionObjectBulkSelectionAction;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}