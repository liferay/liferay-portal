/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Victor Kammerer
 */
@RunWith(Arquillian.class)
public class ObjectEntryInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			RandomTestUtil.randomString(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), _objectDefinition, 0);
	}

	@Test
	public void testGetInfoItemDetails() throws Exception {
		InfoItemDetailsProvider<ObjectEntry> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				_objectEntry.getModelClassName());

		InfoItemDetails classPKInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_objectEntry);

		Assert.assertEquals(
			_objectEntry.getModelClassName(),
			classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				_objectEntry.getModelClassName(),
				_objectEntry.getObjectEntryId()),
			classPKInfoItemDetails.getInfoItemReference());

		InfoItemDetails ercInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ERCInfoItemIdentifier.class, _objectEntry);

		Group objectEntryGroup = GroupLocalServiceUtil.getGroup(
			_objectEntry.getGroupId());

		Assert.assertEquals(
			_objectEntry.getModelClassName(),
			classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				_objectEntry.getModelClassName(),
				new ERCInfoItemIdentifier(
					_objectEntry.getExternalReferenceCode(),
					objectEntryGroup.getExternalReferenceCode())),
			ercInfoItemDetails.getInfoItemReference());

		InfoItemDetails randomGroupERCInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
				_objectEntry);

		Assert.assertEquals(
			_objectEntry.getModelClassName(),
			classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				_objectEntry.getModelClassName(),
				new ERCInfoItemIdentifier(
					_objectEntry.getExternalReferenceCode(),
					objectEntryGroup.getExternalReferenceCode())),
			randomGroupERCInfoItemDetails.getInfoItemReference());
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}