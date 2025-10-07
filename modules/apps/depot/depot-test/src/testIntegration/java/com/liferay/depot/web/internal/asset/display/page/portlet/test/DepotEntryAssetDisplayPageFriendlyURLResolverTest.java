/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Method;

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
public class DepotEntryAssetDisplayPageFriendlyURLResolverTest {

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

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextThreadLocal.getServiceContext());
	}

	@Test
	public void testGetLayoutDisplayPageProvider() throws Exception {
		Assert.assertNotNull(
			_getLayoutDisplayPageProvider(
				_friendlyURLResolver.getURLSeparator() +
					_depotEntry.getDepotEntryId()));
		Assert.assertNotNull(
			_getLayoutDisplayPageProvider(
				_friendlyURLResolver.getURLSeparator() +
					_depotEntry.getGroupId()));
	}

	private LayoutDisplayPageProvider<?> _getLayoutDisplayPageProvider(
			String friendlyURL)
		throws Exception {

		Method method = ReflectionUtil.getDeclaredMethod(
			_friendlyURLResolver.getClass(), "getLayoutDisplayPageProvider",
			String.class);

		return (LayoutDisplayPageProvider<?>)method.invoke(
			_friendlyURLResolver, friendlyURL);
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.asset.display.page.portlet.DepotEntryAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

}