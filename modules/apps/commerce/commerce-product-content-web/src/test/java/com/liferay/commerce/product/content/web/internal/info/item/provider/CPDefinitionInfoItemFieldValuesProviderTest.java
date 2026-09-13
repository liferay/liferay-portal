/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class CPDefinitionInfoItemFieldValuesProviderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_cpDefinitionInfoItemFieldValuesProvider =
			new CPDefinitionInfoItemFieldValuesProvider();

		_setUpMocks();
		_setUpServiceContext();
	}

	@Test
	public void testGetInfoItemFieldValuesWithNullAccountEntry()
		throws Exception {

		CommerceContext commerceContext = Mockito.mock(CommerceContext.class);

		Mockito.doReturn(
			null
		).when(
			commerceContext
		).getAccountEntry();

		CommerceContextThreadLocal.set(commerceContext);

		CPDefinition cpDefinition = _createMockCPDefinition();

		Mockito.doReturn(
			true
		).when(
			cpDefinition
		).isIgnoreSKUCombinations();

		Mockito.doReturn(
			false
		).when(
			_cpContentHelper
		).hasChildCPDefinitions(
			Mockito.anyLong()
		);

		CPInstance cpInstance = Mockito.mock(CPInstance.class);

		Mockito.doReturn(
			cpInstance
		).when(
			_cpInstanceHelper
		).getDefaultCPInstance(
			Mockito.anyLong()
		);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		Mockito.doReturn(
			LocaleUtil.US
		).when(
			themeDisplay
		).getLocale();

		Mockito.doReturn(
			RandomTestUtil.randomLong()
		).when(
			themeDisplay
		).getScopeGroupId();

		InfoItemFieldValues values =
			_cpDefinitionInfoItemFieldValuesProvider.getInfoItemFieldValues(
				cpDefinition);

		Assert.assertNotNull(values);

		Mockito.verify(
			commerceContext, Mockito.atLeastOnce()
		).getAccountEntry();
	}

	@Test
	public void testGetInfoItemFieldValuesWithNullCommerceContext()
		throws Exception {

		CommerceContextThreadLocal.set(null);

		CPDefinition cpDefinition = _createMockCPDefinition();

		InfoItemFieldValues values =
			_cpDefinitionInfoItemFieldValuesProvider.getInfoItemFieldValues(
				cpDefinition);

		Assert.assertNotNull(values);
	}

	private CPDefinition _createMockCPDefinition() {
		CPDefinition cpDefinition = Mockito.mock(CPDefinition.class);

		Mockito.when(
			cpDefinition.getCPDefinitionId()
		).thenReturn(
			_CP_DEFINITION_ID
		);

		Mockito.when(
			cpDefinition.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			cpDefinition.getDefaultLanguageId()
		).thenReturn(
			"en_US"
		);

		return cpDefinition;
	}

	private void _setUpMocks() throws Exception {
		Mockito.when(
			_assetEntryInfoItemFieldSetProvider.getInfoFieldValues(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			new ArrayList<>()
		);

		Mockito.when(
			_commerceMoney.isEmpty()
		).thenReturn(
			true
		);

		Mockito.when(
			_commerceProductPriceCalculation.getFinalPrice(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			_commerceMoney
		);

		Mockito.when(
			_cpDefinitionInventoryEngine.isDisplayAvailability(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			true
		);

		Mockito.when(
			_cpDefinitionInventoryEngine.isDisplayStockQuantity(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			true
		);

		Mockito.when(
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				_cpDefinitionInventory)
		).thenReturn(
			_cpDefinitionInventoryEngine
		);

		Mockito.when(
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(Mockito.anyLong())
		).thenReturn(
			_cpDefinitionInventory
		);

		Mockito.when(
			_expandoInfoItemFieldSetProvider.getInfoFieldValues(
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			new ArrayList<>()
		);

		Mockito.when(
			_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			new ArrayList<>()
		);

		Mockito.when(
			_language.isAvailableLocale(Mockito.any(Locale.class))
		).thenReturn(
			true
		);

		Mockito.when(
			_templateInfoItemFieldSetProvider.getInfoFieldValues(
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			new ArrayList<>()
		);

		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_assetCategoryLocalService", _assetCategoryLocalService);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_assetEntryInfoItemFieldSetProvider",
			_assetEntryInfoItemFieldSetProvider);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_commerceChannelLocalService", _commerceChannelLocalService);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_commerceInventoryEngine", _commerceInventoryEngine);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_commerceProductPriceCalculation",
			_commerceProductPriceCalculation);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider, "_cpContentHelper",
			_cpContentHelper);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_cpDefinitionInventoryEngineRegistry",
			_cpDefinitionInventoryEngineRegistry);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_cpDefinitionInventoryLocalService",
			_cpDefinitionInventoryLocalService);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider, "_cpInstanceHelper",
			_cpInstanceHelper);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_displayPageInfoItemFieldSetProvider",
			_displayPageInfoItemFieldSetProvider);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_expandoInfoItemFieldSetProvider",
			_expandoInfoItemFieldSetProvider);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_infoItemFieldReaderFieldSetProvider",
			_infoItemFieldReaderFieldSetProvider);
		ReflectionTestUtil.setFieldValue(
			_cpDefinitionInfoItemFieldValuesProvider,
			"_templateInfoItemFieldSetProvider",
			_templateInfoItemFieldSetProvider);

		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language", _language);
	}

	private void _setUpServiceContext() {
		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			serviceContext.getThemeDisplay()
		).thenReturn(
			themeDisplay
		);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private static final long _CP_DEFINITION_ID = RandomTestUtil.randomLong();

	private final AssetCategoryLocalService _assetCategoryLocalService =
		Mockito.mock(AssetCategoryLocalService.class);
	private final AssetEntryInfoItemFieldSetProvider
		_assetEntryInfoItemFieldSetProvider = Mockito.mock(
			AssetEntryInfoItemFieldSetProvider.class);
	private final CommerceChannelLocalService _commerceChannelLocalService =
		Mockito.mock(CommerceChannelLocalService.class);
	private final CommerceInventoryEngine _commerceInventoryEngine =
		Mockito.mock(CommerceInventoryEngine.class);
	private final CommerceMoney _commerceMoney = Mockito.mock(
		CommerceMoney.class);
	private final CommerceProductPriceCalculation
		_commerceProductPriceCalculation = Mockito.mock(
			CommerceProductPriceCalculation.class);
	private final CPContentHelper _cpContentHelper = Mockito.mock(
		CPContentHelper.class);
	private CPDefinitionInfoItemFieldValuesProvider
		_cpDefinitionInfoItemFieldValuesProvider;
	private final CPDefinitionInventory _cpDefinitionInventory = Mockito.mock(
		CPDefinitionInventory.class);
	private final CPDefinitionInventoryEngine _cpDefinitionInventoryEngine =
		Mockito.mock(CPDefinitionInventoryEngine.class);
	private final CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry = Mockito.mock(
			CPDefinitionInventoryEngineRegistry.class);
	private final CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService = Mockito.mock(
			CPDefinitionInventoryLocalService.class);
	private final CPInstanceHelper _cpInstanceHelper = Mockito.mock(
		CPInstanceHelper.class);
	private final DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider = Mockito.mock(
			DisplayPageInfoItemFieldSetProvider.class);
	private final ExpandoInfoItemFieldSetProvider
		_expandoInfoItemFieldSetProvider = Mockito.mock(
			ExpandoInfoItemFieldSetProvider.class);
	private final InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider = Mockito.mock(
			InfoItemFieldReaderFieldSetProvider.class);
	private final Language _language = Mockito.mock(Language.class);
	private final TemplateInfoItemFieldSetProvider
		_templateInfoItemFieldSetProvider = Mockito.mock(
			TemplateInfoItemFieldSetProvider.class);

}