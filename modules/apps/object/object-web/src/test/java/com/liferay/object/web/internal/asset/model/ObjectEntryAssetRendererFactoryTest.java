/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.web.internal.object.entries.display.context.ObjectEntryDisplayContextFactoryImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryAssetRendererFactoryTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_objectEntryAssetRendererFactory = new ObjectEntryAssetRendererFactory(
			_assetDisplayPageFriendlyURLProvider, _objectDefinition,
			_objectEntryDisplayContextFactoryImpl, _objectEntryLocalService,
			_objectEntryService, _servletContext);
	}

	@Test
	public void testIsActive() throws Exception {
		Mockito.when(
			_objectDefinition.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Assert.assertFalse(
			_objectEntryAssetRendererFactory.isActive(
				RandomTestUtil.randomLong()));

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		Assert.assertTrue(_objectEntryAssetRendererFactory.isActive(companyId));
	}

	@Test
	public void testIsSelectable() throws Exception {
		Mockito.when(
			_objectDefinition.getScope()
		).thenReturn(
			ObjectDefinitionConstants.SCOPE_COMPANY
		);

		Assert.assertFalse(_objectEntryAssetRendererFactory.isSelectable());

		Mockito.when(
			_objectDefinition.getScope()
		).thenReturn(
			ObjectDefinitionConstants.SCOPE_SITE
		);

		Assert.assertTrue(_objectEntryAssetRendererFactory.isSelectable());
	}

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider = Mockito.mock(
			AssetDisplayPageFriendlyURLProvider.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private ObjectEntryAssetRendererFactory _objectEntryAssetRendererFactory;
	private final ObjectEntryDisplayContextFactoryImpl
		_objectEntryDisplayContextFactoryImpl = Mockito.mock(
			ObjectEntryDisplayContextFactoryImpl.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final ObjectEntryService _objectEntryService = Mockito.mock(
		ObjectEntryService.class);
	private final ServletContext _servletContext = Mockito.mock(
		ServletContext.class);

}