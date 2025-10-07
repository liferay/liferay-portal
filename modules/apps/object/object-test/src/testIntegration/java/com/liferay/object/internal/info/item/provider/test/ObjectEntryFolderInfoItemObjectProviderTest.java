/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderInfoItemObjectProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());
		_objectEntryFolder = ObjectEntryFolderTestUtil.addObjectEntryFolder();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetInfoItem() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchInfoItemException.class,
			StringBundler.concat(
				"com.liferay.object.exception.NoSuchObjectEntryFolder",
				"Exception: No ObjectEntryFolder exists with the key {",
				"externalReferenceCode=",
				_objectEntryFolder.getExternalReferenceCode(), ", groupId=",
				groupId, ", companyId=", TestPropsValues.getCompanyId(), "}"),
			() -> _objectEntryFolderInfoItemObjectProvider.getInfoItem(
				groupId,
				new ERCInfoItemIdentifier(
					_objectEntryFolder.getExternalReferenceCode())));

		Assert.assertEquals(
			_objectEntryFolder,
			_objectEntryFolderInfoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ClassPKInfoItemIdentifier(
					_objectEntryFolder.getObjectEntryFolderId())));
		Assert.assertEquals(
			_objectEntryFolder,
			_objectEntryFolderInfoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ERCInfoItemIdentifier(
					_objectEntryFolder.getExternalReferenceCode())));
		Assert.assertEquals(
			_objectEntryFolder,
			_objectEntryFolderInfoItemObjectProvider.getInfoItem(
				RandomTestUtil.randomLong(),
				new ERCInfoItemIdentifier(
					_objectEntryFolder.getExternalReferenceCode(),
					_group.getExternalReferenceCode())));
	}

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private ObjectEntryFolder _objectEntryFolder;

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.info.item.provider.ObjectEntryFolderInfoItemObjectProvider"
	)
	private InfoItemObjectProvider<ObjectEntryFolder>
		_objectEntryFolderInfoItemObjectProvider;

}