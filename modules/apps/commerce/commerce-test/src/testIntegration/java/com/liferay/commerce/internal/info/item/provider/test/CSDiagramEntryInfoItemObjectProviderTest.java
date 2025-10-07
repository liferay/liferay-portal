/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
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
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class CSDiagramEntryInfoItemObjectProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		_csDiagramEntry = _csDiagramEntryLocalService.addCSDiagramEntry(
			TestPropsValues.getUserId(), cpInstance.getCPDefinitionId(),
			cpInstance.getCPInstanceId(), cpDefinition.getCProductId(), false,
			0, null, cpInstance.getSku(), serviceContext);
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
			"No group found with group ID " + groupId,
			() -> _csDiagramEntryInfoItemObjectProvider.getInfoItem(
				groupId,
				new ERCInfoItemIdentifier(
					_csDiagramEntry.getExternalReferenceCode())));

		Assert.assertEquals(
			_csDiagramEntry,
			_csDiagramEntryInfoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ClassPKInfoItemIdentifier(
					_csDiagramEntry.getCSDiagramEntryId())));
		Assert.assertEquals(
			_csDiagramEntry,
			_csDiagramEntryInfoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ERCInfoItemIdentifier(
					_csDiagramEntry.getExternalReferenceCode())));
		Assert.assertEquals(
			_csDiagramEntry,
			_csDiagramEntryInfoItemObjectProvider.getInfoItem(
				RandomTestUtil.randomLong(),
				new ERCInfoItemIdentifier(
					_csDiagramEntry.getExternalReferenceCode(),
					_group.getExternalReferenceCode())));
	}

	private CSDiagramEntry _csDiagramEntry;

	@Inject(
		filter = "component.name=com.liferay.commerce.shop.by.diagram.web.internal.info.item.provider.CSDiagramEntryInfoItemObjectProvider"
	)
	private InfoItemObjectProvider<CSDiagramEntry>
		_csDiagramEntryInfoItemObjectProvider;

	@Inject
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

}