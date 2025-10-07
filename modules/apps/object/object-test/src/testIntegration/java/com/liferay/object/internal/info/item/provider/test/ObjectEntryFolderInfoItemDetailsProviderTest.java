/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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
public class ObjectEntryFolderInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
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
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetInfoItem() {
		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				ObjectEntryFolder.class.getName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_objectEntryFolder);

		Assert.assertEquals(
			ObjectEntryFolder.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				ObjectEntryFolder.class.getName(),
				_objectEntryFolder.getObjectEntryFolderId()),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			_group.getGroupId(), ERCInfoItemIdentifier.class,
			_objectEntryFolder);

		Assert.assertEquals(
			ObjectEntryFolder.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				ObjectEntryFolder.class.getName(),
				new ERCInfoItemIdentifier(
					_objectEntryFolder.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
			_objectEntryFolder);

		Assert.assertEquals(
			ObjectEntryFolder.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				ObjectEntryFolder.class.getName(),
				new ERCInfoItemIdentifier(
					_objectEntryFolder.getExternalReferenceCode(),
					_group.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());
	}

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private ObjectEntryFolder _objectEntryFolder;

}