/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.display.context.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class CPPublisherDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@Test
	public void testGetSearchContainerWithAccountGroupFilterEnabled()
		throws Exception {

		AccountEntry accountEntry1 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		AccountGroup accountGroup =
			CommerceAccountTestUtil.addAccountGroupAndAccountRel(
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
				accountEntry1.getAccountEntryId(), _serviceContext);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableAccountGroupFilter(accountGroup, cpDefinition);

		AssetCategory assetCategory = CPTestUtil.addCategoryToCPDefinitions(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId());

		CommerceChannel commerceChannel = _addCommerceChannel();

		Assert.assertEquals(
			Collections.singletonList(cpDefinition.getCPDefinitionId()),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					accountEntry1.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));

		AccountEntry accountEntry2 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			accountEntry2.getAccountEntryId(), _serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					accountEntry2.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));

		AccountEntry accountEntry3 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					accountEntry3.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));
		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(assetCategory, commerceChannel, null));
	}

	@Test
	public void testGetSearchContainerWithChannelFilterEnabled()
		throws Exception {

		CommerceChannel commerceChannel = _addCommerceChannel();

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableChannelFilter(commerceChannel, cpDefinition1);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableChannelFilter(_addCommerceChannel(), cpDefinition2);

		AssetCategory assetCategory = CPTestUtil.addCategoryToCPDefinitions(
			_commerceCatalog.getGroupId(), cpDefinition1.getCPDefinitionId(),
			cpDefinition2.getCPDefinitionId());

		Assert.assertEquals(
			Collections.singletonList(cpDefinition1.getCPDefinitionId()),
			_getCPDefinitionIds(assetCategory, commerceChannel, null));

		AccountEntry accountEntry1 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		_addEligibleAccountEntry(accountEntry1, commerceChannel);

		Assert.assertEquals(
			Collections.singletonList(cpDefinition1.getCPDefinitionId()),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					accountEntry1.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));

		AccountEntry accountEntry2 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					accountEntry2.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				assetCategory, commerceChannel,
				_commerceContextFactory.create(
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId())));
		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(assetCategory, commerceChannel, null));
	}

	private CommerceChannel _addCommerceChannel() throws Exception {
		Group group = GroupTestUtil.addGroup();

		return CommerceTestUtil.addCommerceChannel(group.getGroupId(), null);
	}

	private void _addEligibleAccountEntry(
			AccountEntry accountEntry, CommerceChannel commerceChannel)
		throws Exception {

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				TestPropsValues.getUserId(), accountEntry.getAccountEntryId(),
				AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
				commerceChannel.getCommerceChannelId(), true, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);
	}

	private void _enableAccountGroupFilter(
			AccountGroup accountGroup, CPDefinition cpDefinition)
		throws Exception {

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		cpDefinition.setAccountGroupFilterEnabled(true);

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private void _enableChannelFilter(
			CommerceChannel commerceChannel, CPDefinition cpDefinition)
		throws Exception {

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		cpDefinition.setChannelFilterEnabled(true);

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private List<Long> _getCPDefinitionIds(
			AssetCategory assetCategory, CommerceChannel commerceChannel,
			CommerceContext commerceContext)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		MockPortletPreferences mockPortletPreferences =
			new MockPortletPreferences();

		mockPortletPreferences.setValue("queryContains0", "true");
		mockPortletPreferences.setValue("queryName0", "assetCategories");
		mockPortletPreferences.setValues(
			"queryValues0",
			new String[] {String.valueOf(assetCategory.getCategoryId())});

		mockLiferayPortletRenderRequest.setPreferences(mockPortletPreferences);

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		mockHttpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			mockLiferayPortletRenderResponse);
		mockHttpServletRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
					"/product_publisher/view.jsp",
			new MockLiferayPortletContext("/product_publisher/view.jsp"));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(commerceChannel.getCompanyId()));
		themeDisplay.setLayout(
			LayoutTestUtil.addTypePortletLayout(
				commerceChannel.getSiteGroupId()));
		themeDisplay.setScopeGroupId(commerceChannel.getSiteGroupId());
		themeDisplay.setSiteGroupId(commerceChannel.getSiteGroupId());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(CPPortletKeys.CP_PUBLISHER_WEB);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_portlet.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);

		SearchContainer<CPCatalogEntry> searchContainer =
			ReflectionTestUtil.invoke(
				mockLiferayPortletRenderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT),
				"getSearchContainer", new Class<?>[0]);

		return TransformUtil.transform(
			searchContainer.getResults(),
			cpCatalogEntry -> cpCatalogEntry.getCPDefinitionId());
	}

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Inject
	private CommerceContextFactory _commerceContextFactory;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject(filter = "jakarta.portlet.name=" + CPPortletKeys.CP_PUBLISHER_WEB)
	private Portlet _portlet;

	private ServiceContext _serviceContext;

}