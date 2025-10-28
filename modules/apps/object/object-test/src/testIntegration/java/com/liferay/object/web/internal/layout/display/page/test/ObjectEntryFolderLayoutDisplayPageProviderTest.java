/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
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
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		_objectEntryFolder = ObjectEntryFolderTestUtil.addObjectEntryFolder();
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		_testGetLayoutDisplayPageObjectProvider(
			new ClassPKInfoItemIdentifier(
				_objectEntryFolder.getObjectEntryFolderId()));
		_testGetLayoutDisplayPageObjectProvider(
			new ERCInfoItemIdentifier(
				_objectEntryFolder.getExternalReferenceCode()));
	}

	private void _testGetLayoutDisplayPageObjectProvider(
			InfoItemIdentifier infoItemIdentifier)
		throws Exception {

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_objectEntryFolder.getGroupId(),
				new InfoItemReference(
					ObjectEntryFolder.class.getName(), infoItemIdentifier));

		Assert.assertEquals(
			_objectEntryFolder,
			layoutDisplayPageObjectProvider.getDisplayObject());
	}

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.layout.display.page.ObjectEntryFolderLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider _layoutDisplayPageProvider;

	private ObjectEntryFolder _objectEntryFolder;

}