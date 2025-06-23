/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.interpreter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class AssetRendererSharingEntryInterpreterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetTitle() throws Exception {
		ObjectDefinition objectDefinition = _getObjectDefinition();
		User user = UserTestUtil.addUser();

		try {
			String title = RandomTestUtil.randomString();

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId());

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				_group.getGroupId(), TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"title", title
				).build(),
				serviceContext);

			SharingEntry sharingEntry =
				_sharingEntryLocalService.addSharingEntry(
					null, TestPropsValues.getUserId(), 0, user.getUserId(),
					_classNameLocalService.getClassNameId(
						objectDefinition.getClassName()),
					objectEntry.getObjectEntryId(), _group.getGroupId(), true,
					Arrays.asList(SharingEntryAction.VIEW), null,
					serviceContext);

			SharingEntryInterpreter sharingEntryInterpreter =
				_getSharingEntryInterpreter(sharingEntry);

			Assert.assertEquals(
				title,
				sharingEntryInterpreter.getTitle(sharingEntry, LocaleUtil.US));
		}
		catch (Exception exception) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
			_userLocalService.deleteUser(user);
		}
	}

	private ObjectDefinition _getObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).indexed(
						true
					).indexedAsKeyword(
						true
					).name(
						"title"
					).localized(
						false
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "title");

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	private SharingEntryInterpreter _getSharingEntryInterpreter(
		SharingEntry sharingEntry) {

		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterProvider.getSharingEntryInterpreter(
				sharingEntry);

		Assert.assertNotNull(sharingEntryInterpreter);

		return sharingEntryInterpreter;
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private SharingEntryInterpreterProvider _sharingEntryInterpreterProvider;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}