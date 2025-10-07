/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class DepotEntryLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws PortalException {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		Assert.assertNotNull(
			_depotEntryLayoutDisplayPageProvider.
				getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						DepotEntry.class.getName(),
						_depotEntry.getDepotEntryId())));
		Assert.assertNotNull(
			_depotEntryLayoutDisplayPageProvider.
				getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						DepotEntry.class.getName(), _depotEntry.getGroupId())));

		Group group = _depotEntry.getGroup();

		Assert.assertNotNull(
			_depotEntryLayoutDisplayPageProvider.
				getLayoutDisplayPageObjectProvider(
					group.getGroupId(),
					new InfoItemReference(
						DepotEntry.class.getName(),
						new ERCInfoItemIdentifier(
							group.getExternalReferenceCode()))));
		Assert.assertNotNull(
			_depotEntryLayoutDisplayPageProvider.
				getLayoutDisplayPageObjectProvider(
					TestPropsValues.getGroupId(),
					new InfoItemReference(
						DepotEntry.class.getName(),
						new ERCInfoItemIdentifier(
							group.getExternalReferenceCode()))));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.layout.display.page.DepotEntryLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<DepotEntry>
		_depotEntryLayoutDisplayPageProvider;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}