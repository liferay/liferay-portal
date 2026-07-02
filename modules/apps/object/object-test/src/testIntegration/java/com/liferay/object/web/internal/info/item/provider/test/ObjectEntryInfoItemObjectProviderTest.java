/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roselaine Marques
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
@Sync
public class ObjectEntryInfoItemObjectProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			false, false, true,
			List.of(
				new TextObjectFieldBuilder(
				).userId(
					TestPropsValues.getUserId()
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					_OBJECT_FIELD_NAME
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	@Test
	public void testGetInfoItemExpiredObjectEntryWithLatestApprovedVersion()
		throws Exception {

		String approvedValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, approvedValue
			).build());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry = _objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(objectEntry.isExpired());

		ObjectEntry infoItemObjectEntry = _getInfoItem(
			objectEntry.getObjectEntryId());

		Assert.assertTrue(infoItemObjectEntry.isApproved());

		Map<String, Serializable> values = infoItemObjectEntry.getValues();

		Assert.assertEquals(approvedValue, values.get(_OBJECT_FIELD_NAME));
	}

	@Test
	public void testGetInfoItemExpiredObjectEntryWithoutLatestApprovedVersion()
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build());

		objectEntry = _objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(objectEntry.isExpired());

		_assertNoSuchInfoItemException(objectEntry.getObjectEntryId());
	}

	@Test
	public void testGetInfoItemScheduledObjectEntryWithLatestApprovedVersion()
		throws Exception {

		String approvedValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, approvedValue
			).build());

		Assert.assertTrue(objectEntry.isApproved());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"displayDate",
				new Date(System.currentTimeMillis() + TimeUnit.DAY.toMillis(1))
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(objectEntry.isScheduled());

		ObjectEntry infoItemObjectEntry = _getInfoItem(
			objectEntry.getObjectEntryId());

		Assert.assertTrue(infoItemObjectEntry.isApproved());

		Map<String, Serializable> values = infoItemObjectEntry.getValues();

		Assert.assertEquals(approvedValue, values.get(_OBJECT_FIELD_NAME));
	}

	@Test
	public void testGetInfoItemScheduledObjectEntryWithoutLatestApprovedVersion()
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"displayDate",
				new Date(System.currentTimeMillis() + TimeUnit.DAY.toMillis(1))
			).build());

		Assert.assertTrue(objectEntry.isScheduled());

		_assertNoSuchInfoItemException(objectEntry.getObjectEntryId());
	}

	private void _assertNoSuchInfoItemException(long objectEntryId) {
		try {
			_getInfoItem(objectEntryId);

			Assert.fail();
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			Assert.assertEquals(
				"Unable to get an approved object entry " + objectEntryId,
				noSuchInfoItemException.getMessage());
		}
	}

	private ObjectEntry _getInfoItem(long classPK)
		throws NoSuchInfoItemException {

		InfoItemObjectProvider<ObjectEntry> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, _objectDefinition.getClassName(),
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		return infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));
	}

	private static final String _OBJECT_FIELD_NAME =
		"a" + RandomTestUtil.randomString();

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}