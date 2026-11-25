/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.FDSDataProviderRegistry;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class PreviewCommerceOrderItemFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fdsDataProvider = _fdsDataProviderRegistry.getFDSDataProvider(
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT +
				"-previewOrderItems");
	}

	@Test
	public void testGetItemsCount() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		User user1 = UserTestUtil.addUser();

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			user1.getUserId(), commerceChannel.getGroupId(),
			commerceCurrency.getCommerceCurrencyId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1)) {

			Assert.assertEquals(
				1,
				_fdsDataProvider.getItemsCount(
					null, _getMockHttpServletRequest(commerceOrder, user1)));
		}

		User user2 = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user2);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.order.content.web.internal.frontend." +
					"data.set.provider.PreviewCommerceOrderItemFDSDataProvider",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				0,
				_fdsDataProvider.getItemsCount(
					null, _getMockHttpServletRequest(commerceOrder, user2)));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			Assert.assertTrue(
				logEntry.getThrowable() instanceof PrincipalException);

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			CommerceOrder commerceOrder, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"commerceOrderId",
			String.valueOf(commerceOrder.getCommerceOrderId()));
		mockHttpServletRequest.setParameter(
			"commerceOrderImporterTypeKey", "csv");

		FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
			company.getGroupId(), user.getUserId(),
			"com.liferay.commerce.order.content.web.internal.portlet.action." +
				"ImportCSVMVCActionCommand",
			RandomTestUtil.randomString() + ".csv",
			new ByteArrayInputStream("sku,quantity\nerc-test,1".getBytes()),
			ContentTypes.TEXT_CSV);

		mockHttpServletRequest.setParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private FDSDataProvider<?> _fdsDataProvider;

	@Inject
	private FDSDataProviderRegistry _fdsDataProviderRegistry;

}