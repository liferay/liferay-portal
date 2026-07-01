/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.sharing.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juanjo Fernandez
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-34594"), @FeatureFlag("LPS-164563")}
)
@RunWith(Arquillian.class)
public class DataSetSnapshotSharingEntryInterpreterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			DataSetSnapshotSharingEntryInterpreterTest.class);

		_objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", TestPropsValues.getCompanyId());

		Assert.assertNotNull(_objectDefinition);

		_toUser = UserTestUtil.addUser();
	}

	@Test
	public void testGetSharingEntryInterpreter() throws Exception {
		String label = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"label", label
			).build(),
			serviceContext);

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, _toUser.getUserId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), 0, true,
			Arrays.asList(SharingEntryAction.VIEW), null, serviceContext);

		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterProvider.getSharingEntryInterpreter(
				sharingEntry);

		Assert.assertEquals(
			StringUtil.quote(label),
			sharingEntryInterpreter.getTitle(sharingEntry, LocaleUtil.US));
		Assert.assertEquals(
			"Data Set User View",
			sharingEntryInterpreter.getAssetTypeTitle(
				sharingEntry, LocaleUtil.US));
		Assert.assertTrue(sharingEntryInterpreter.isVisible(sharingEntry));

		if (sharingEntry != null) {
			_sharingEntryLocalService.deleteSharingEntry(
				sharingEntry.getSharingEntryId());
		}

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SharingEntryInterpreterProvider _sharingEntryInterpreterProvider;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@DeleteAfterTestRun
	private User _toUser;

}