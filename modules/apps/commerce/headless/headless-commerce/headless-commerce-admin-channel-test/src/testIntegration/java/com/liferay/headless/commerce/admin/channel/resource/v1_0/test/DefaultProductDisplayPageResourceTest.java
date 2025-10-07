/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.DefaultProductDisplayPage;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class DefaultProductDisplayPageResourceTest
	extends BaseDefaultProductDisplayPageResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addOmniadminUser();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testCompany.getGroupId(), _commerceCurrency.getCode());

		_commerceChannel =
			_commerceChannelLocalService.
				updateCommerceChannelExternalReferenceCode(
					RandomTestUtil.randomString(),
					_commerceChannel.getCommerceChannelId());
	}

	@Override
	@Test
	public void testDeleteChannelByExternalReferenceCodeDefaultProductDisplayPage()
		throws Exception {

		_addDefaultProductDisplayPage(randomDefaultProductDisplayPage());

		assertHttpResponseStatusCode(
			204,
			defaultProductDisplayPageResource.
				deleteChannelByExternalReferenceCodeDefaultProductDisplayPageHttpResponse(
					_commerceChannel.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			defaultProductDisplayPageResource.
				getChannelByExternalReferenceCodeDefaultProductDisplayPageHttpResponse(
					_commerceChannel.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			defaultProductDisplayPageResource.
				getChannelByExternalReferenceCodeDefaultProductDisplayPageHttpResponse(
					_commerceChannel.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testDeleteChannelIdDefaultProductDisplayPage()
		throws Exception {

		_addDefaultProductDisplayPage(randomDefaultProductDisplayPage());

		assertHttpResponseStatusCode(
			204,
			defaultProductDisplayPageResource.
				deleteChannelIdDefaultProductDisplayPageHttpResponse(
					_commerceChannel.getCommerceChannelId()));

		assertHttpResponseStatusCode(
			404,
			defaultProductDisplayPageResource.
				getChannelIdDefaultProductDisplayPageHttpResponse(
					_commerceChannel.getCommerceChannelId()));

		assertHttpResponseStatusCode(
			404,
			defaultProductDisplayPageResource.
				getChannelIdDefaultProductDisplayPageHttpResponse(
					_commerceChannel.getCommerceChannelId()));
	}

	@Override
	@Test
	public void testGetChannelByExternalReferenceCodeDefaultProductDisplayPage()
		throws Exception {

		DefaultProductDisplayPage postDefaultProductDisplayPage =
			_addDefaultProductDisplayPage(randomDefaultProductDisplayPage());

		DefaultProductDisplayPage getDefaultProductDisplayPage =
			defaultProductDisplayPageResource.
				getChannelByExternalReferenceCodeDefaultProductDisplayPage(
					_commerceChannel.getExternalReferenceCode());

		assertEquals(
			postDefaultProductDisplayPage, getDefaultProductDisplayPage);
		assertValid(getDefaultProductDisplayPage);
	}

	@Override
	@Test
	public void testGetChannelIdDefaultProductDisplayPage() throws Exception {
		DefaultProductDisplayPage postDefaultProductDisplayPage =
			_addDefaultProductDisplayPage(randomDefaultProductDisplayPage());

		DefaultProductDisplayPage getDefaultProductDisplayPage =
			defaultProductDisplayPageResource.
				getChannelIdDefaultProductDisplayPage(
					_commerceChannel.getCommerceChannelId());

		assertEquals(
			postDefaultProductDisplayPage, getDefaultProductDisplayPage);
		assertValid(getDefaultProductDisplayPage);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteChannelByExternalReferenceCodeDefaultProductDisplayPage()
		throws Exception {

		super.
			testGraphQLDeleteChannelByExternalReferenceCodeDefaultProductDisplayPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteChannelIdDefaultProductDisplayPage()
		throws Exception {

		super.testGraphQLDeleteChannelIdDefaultProductDisplayPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetChannelByExternalReferenceCodeDefaultProductDisplayPage()
		throws Exception {

		super.
			testGraphQLGetChannelByExternalReferenceCodeDefaultProductDisplayPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetChannelIdDefaultProductDisplayPage()
		throws Exception {

		super.testGraphQLGetChannelIdDefaultProductDisplayPage();
	}

	@Override
	protected DefaultProductDisplayPage randomDefaultProductDisplayPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			testGroup.getGroupId());

		_layouts.add(layout);

		return new DefaultProductDisplayPage() {
			{
				pageUuid = layout.getUuid();
			}
		};
	}

	@Override
	protected DefaultProductDisplayPage
			testPostChannelByExternalReferenceCodeDefaultProductDisplayPage_addDefaultProductDisplayPage(
				DefaultProductDisplayPage defaultProductDisplayPage)
		throws Exception {

		return _addDefaultProductDisplayPage(defaultProductDisplayPage);
	}

	@Override
	protected DefaultProductDisplayPage
			testPostChannelIdDefaultProductDisplayPage_addDefaultProductDisplayPage(
				DefaultProductDisplayPage defaultProductDisplayPage)
		throws Exception {

		return _addDefaultProductDisplayPage(defaultProductDisplayPage);
	}

	private DefaultProductDisplayPage _addDefaultProductDisplayPage(
			DefaultProductDisplayPage defaultProductDisplayPage)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CPConstants.RESOURCE_NAME_CP_DISPLAY_LAYOUT));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"productLayoutUuid", defaultProductDisplayPage.getPageUuid());

		modifiableSettings.store();

		return new DefaultProductDisplayPage() {
			{
				setPageUuid(
					() -> modifiableSettings.getValue(
						"productLayoutUuid", StringPool.BLANK));
			}
		};
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private List<CommerceChannel> _commerceChannels = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private List<Layout> _layouts = new ArrayList<>();

	@DeleteAfterTestRun
	private User _user;

}