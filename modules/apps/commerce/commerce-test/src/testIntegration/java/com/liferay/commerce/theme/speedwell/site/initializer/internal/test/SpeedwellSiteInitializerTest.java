/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.theme.speedwell.site.initializer.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.util.Map;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class SpeedwellSiteInitializerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testInitialize() throws Exception {
		frutillaRule.scenario(
			"Initialize a site with the Speedwell site initializer"
		).given(
			"A site"
		).when(
			"The Speedwell site initializer runs on the site"
		).then(
			"The site has a B2C channel and a catalog with the Speedwell " +
				"products"
		);

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"speedwell-initializer");

		siteInitializer.initialize(_group.getGroupId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_group.getGroupId());

		String name = _group.getName(LocaleUtil.getSiteDefault());

		Assert.assertEquals("USD", commerceChannel.getCommerceCurrencyCode());
		Assert.assertEquals(name + " Portal", commerceChannel.getName());
		Assert.assertEquals(
			CommerceChannelConstants.CHANNEL_TYPE_SITE,
			commerceChannel.getType());

		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAccountGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannel.getGroupId(),
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		Assert.assertEquals(
			CommerceChannelConstants.SITE_TYPE_B2C,
			commerceAccountGroupServiceConfiguration.commerceSiteType());

		CommerceCatalog commerceCatalog = _fetchCommerceCatalog(name);

		Assert.assertEquals(
			LocaleUtil.toLanguageId(LocaleUtil.US),
			commerceCatalog.getCatalogDefaultLanguageId());
		Assert.assertEquals("USD", commerceCatalog.getCommerceCurrencyCode());

		Assert.assertEquals(
			50,
			_cpDefinitionLocalService.getCPDefinitionsCount(
				commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY));

		CPOption cpOption = _cpOptionLocalService.getCPOption(
			_group.getCompanyId(), "package-quantity");

		Assert.assertEquals(
			"Package Quantity", cpOption.getName(LocaleUtil.US));

		Map<String, String> cpOptionCategoryKeysBySpecificationKey =
			HashMapBuilder.put(
				"dimensions", "dimensions"
			).put(
				"fit", "fit"
			).put(
				"height", "dimensions"
			).put(
				"length", "dimensions"
			).put(
				"material", "material"
			).put(
				"quantity", "quantity"
			).put(
				"warranty", "warranty"
			).put(
				"weight", "dimensions"
			).put(
				"width", "dimensions"
			).build();

		for (Map.Entry<String, String> entry :
				cpOptionCategoryKeysBySpecificationKey.entrySet()) {

			String key = entry.getKey();

			CPSpecificationOption cpSpecificationOption =
				_cpSpecificationOptionLocalService.getCPSpecificationOption(
					_group.getCompanyId(), key);

			Assert.assertEquals(
				StringUtil.upperCaseFirstLetter(key),
				cpSpecificationOption.getTitle(LocaleUtil.US));

			CPOptionCategory cpOptionCategory =
				_cpOptionCategoryLocalService.getCPOptionCategory(
					cpSpecificationOption.getCPOptionCategoryId());

			String value = entry.getValue();

			Assert.assertEquals(value, cpOptionCategory.getKey());
			Assert.assertEquals(
				StringUtil.upperCaseFirstLetter(value),
				cpOptionCategory.getTitle(LocaleUtil.US));
		}
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceCatalog _fetchCommerceCatalog(String name) {
		for (CommerceCatalog commerceCatalog :
				_commerceCatalogLocalService.getCommerceCatalogs(
					_group.getCompanyId(), false)) {

			if (name.equals(commerceCatalog.getName())) {
				return commerceCatalog;
			}
		}

		return null;
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@Inject
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	private Group _group;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}