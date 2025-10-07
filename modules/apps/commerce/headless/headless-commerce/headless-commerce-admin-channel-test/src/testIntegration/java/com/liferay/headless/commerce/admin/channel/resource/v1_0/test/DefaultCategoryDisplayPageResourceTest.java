/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.DefaultCategoryDisplayPage;
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
public class DefaultCategoryDisplayPageResourceTest
	extends BaseDefaultCategoryDisplayPageResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_assetVocabulary = AssetTestUtil.addVocabulary(_user.getGroupId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceChannel =
			_commerceChannelLocalService.
				updateCommerceChannelExternalReferenceCode(
					RandomTestUtil.randomString(),
					_commerceChannel.getCommerceChannelId());
	}

	@Override
	@Test
	public void testDeleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage()
		throws Exception {

		_addDefaultCategoryPage(randomDefaultCategoryDisplayPage());

		assertHttpResponseStatusCode(
			204,
			defaultCategoryDisplayPageResource.
				deleteChannelByExternalReferenceCodeDefaultCategoryDisplayPageHttpResponse(
					_commerceChannel.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			defaultCategoryDisplayPageResource.
				getChannelByExternalReferenceCodeDefaultCategoryDisplayPageHttpResponse(
					_commerceChannel.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			defaultCategoryDisplayPageResource.
				getChannelByExternalReferenceCodeDefaultCategoryDisplayPageHttpResponse(
					_commerceChannel.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testDeleteChannelIdDefaultCategoryDisplayPage()
		throws Exception {

		_addDefaultCategoryPage(randomDefaultCategoryDisplayPage());

		assertHttpResponseStatusCode(
			204,
			defaultCategoryDisplayPageResource.
				deleteChannelIdDefaultCategoryDisplayPageHttpResponse(
					_commerceChannel.getCommerceChannelId()));

		assertHttpResponseStatusCode(
			404,
			defaultCategoryDisplayPageResource.
				getChannelIdDefaultCategoryDisplayPageHttpResponse(
					_commerceChannel.getCommerceChannelId()));

		assertHttpResponseStatusCode(
			404,
			defaultCategoryDisplayPageResource.
				getChannelIdDefaultCategoryDisplayPageHttpResponse(
					_commerceChannel.getCommerceChannelId()));
	}

	@Override
	@Test
	public void testGetChannelByExternalReferenceCodeDefaultCategoryDisplayPage()
		throws Exception {

		DefaultCategoryDisplayPage postDefaultCategoryDisplayPage =
			_addDefaultCategoryPage(randomDefaultCategoryDisplayPage());

		DefaultCategoryDisplayPage getDefaultCategoryDisplayPage =
			defaultCategoryDisplayPageResource.
				getChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
					_commerceChannel.getExternalReferenceCode());

		assertEquals(
			postDefaultCategoryDisplayPage, getDefaultCategoryDisplayPage);
		assertValid(getDefaultCategoryDisplayPage);
	}

	@Override
	@Test
	public void testGetChannelIdDefaultCategoryDisplayPage() throws Exception {
		DefaultCategoryDisplayPage postDefaultCategoryDisplayPage =
			_addDefaultCategoryPage(randomDefaultCategoryDisplayPage());

		DefaultCategoryDisplayPage getDefaultCategoryDisplayPage =
			defaultCategoryDisplayPageResource.
				getChannelIdDefaultCategoryDisplayPage(
					_commerceChannel.getCommerceChannelId());

		assertEquals(
			postDefaultCategoryDisplayPage, getDefaultCategoryDisplayPage);
		assertValid(getDefaultCategoryDisplayPage);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage()
		throws Exception {

		super.
			testGraphQLDeleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteChannelIdDefaultCategoryDisplayPage()
		throws Exception {

		super.testGraphQLDeleteChannelIdDefaultCategoryDisplayPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetChannelByExternalReferenceCodeDefaultCategoryDisplayPage()
		throws Exception {

		super.
			testGraphQLGetChannelByExternalReferenceCodeDefaultCategoryDisplayPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetChannelIdDefaultCategoryDisplayPage()
		throws Exception {

		super.testGraphQLGetChannelIdDefaultCategoryDisplayPage();
	}

	@Override
	protected DefaultCategoryDisplayPage randomDefaultCategoryDisplayPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			testGroup.getGroupId());

		_layouts.add(layout);

		return new DefaultCategoryDisplayPage() {
			{
				pageUuid = layout.getUuid();
			}
		};
	}

	@Override
	protected DefaultCategoryDisplayPage
			testPostChannelByExternalReferenceCodeDefaultCategoryDisplayPage_addDefaultCategoryDisplayPage(
				DefaultCategoryDisplayPage defaultCategoryDisplayPage)
		throws Exception {

		return _addDefaultCategoryPage(defaultCategoryDisplayPage);
	}

	@Override
	protected DefaultCategoryDisplayPage
			testPostChannelIdDefaultCategoryDisplayPage_addDefaultCategoryDisplayPage(
				DefaultCategoryDisplayPage defaultCategoryDisplayPage)
		throws Exception {

		return _addDefaultCategoryPage(defaultCategoryDisplayPage);
	}

	private DefaultCategoryDisplayPage _addDefaultCategoryPage(
			DefaultCategoryDisplayPage defaultCategoryDisplayPage)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CPConstants.RESOURCE_NAME_CP_DISPLAY_LAYOUT));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"assetCategoryLayoutUuid",
			defaultCategoryDisplayPage.getPageUuid());

		modifiableSettings.store();

		return new DefaultCategoryDisplayPage() {
			{
				setPageUuid(
					() -> modifiableSettings.getValue(
						"assetCategoryLayoutUuid", StringPool.BLANK));
			}
		};
	}

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories = new ArrayList<>();

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private List<Layout> _layouts = new ArrayList<>();

	@DeleteAfterTestRun
	private User _user;

}