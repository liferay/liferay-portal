/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.upgrade.v5_7_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lianne Louie
 */
@RunWith(Arquillian.class)
public class CPDefinitionLinkUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		Map<Locale, String> nameMap = _getLocalizedMap();

		nameMap.put(LocaleUtil.JAPAN, null);

		Map<Locale, String> descriptionMap = _getLocalizedMap();

		descriptionMap.put(LocaleUtil.JAPAN, null);

		User user = UserTestUtil.addUser();

		Group group = GroupTestUtil.addGroup(
			user.getCompanyId(), user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			group.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		Date displayDate = cpDefinition.getDisplayDate();
		Date expirationDate = cpDefinition.getExpirationDate();

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition.getCPDefinitionId(), nameMap,
			cpDefinition.getShortDescriptionMap(), descriptionMap,
			cpDefinition.getUrlTitleMap(), cpDefinition.getMetaTitleMap(),
			cpDefinition.getMetaDescriptionMap(),
			cpDefinition.getMetaKeywordsMap(),
			cpDefinition.isIgnoreSKUCombinations(), true, true, true,
			cpDefinition.getShippingExtraPrice(), cpDefinition.getWidth(),
			cpDefinition.getHeight(), cpDefinition.getDepth(),
			cpDefinition.getWeight(), cpDefinition.getCPTaxCategoryId(),
			cpDefinition.isTaxExempt(), cpDefinition.isTelcoOrElectronics(),
			cpDefinition.getDDMStructureKey(), cpDefinition.isPublished(),
			displayDate.getMonth(), displayDate.getDate(),
			displayDate.getYear(), displayDate.getHours(),
			displayDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getDate(), expirationDate.getYear(),
			expirationDate.getHours(), expirationDate.getMinutes(),
			cpDefinition.isAccountGroupFilterEnabled(),
			cpDefinition.isChannelFilterEnabled(), true,
			ServiceContextTestUtil.getServiceContext());

		CPDefinitionLink cpDefinitionLink =
			_cpDefinitionLinkLocalService.addCPDefinitionLinkByCProductId(
				cpDefinition.getCPDefinitionId(), cpDefinition.getCProductId(),
				displayDate.getMonth(), displayDate.getDate(),
				displayDate.getYear(), displayDate.getHours(),
				displayDate.getMinutes(), expirationDate.getMonth(),
				expirationDate.getDate(), expirationDate.getYear(),
				expirationDate.getHours(), expirationDate.getMinutes(), true,
				0D, cpDefinition.getProductTypeName(),
				ServiceContextTestUtil.getServiceContext());

		_runUpgrade();

		EntityCacheUtil.clearCache();

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			"com.liferay.commerce.product.model.CPDefinitionLink",
			cpDefinitionLink.getCPDefinitionLinkId());

		Assert.assertEquals(
			assetEntry.getTitle(),
			LocalizationUtil.getXml(
				_getI18nMap(nameMap.get(LocaleUtil.US)),
				cpDefinition.getDefaultLanguageId(), "Name"));
		Assert.assertEquals(
			assetEntry.getDescription(),
			LocalizationUtil.getXml(
				_getI18nMap(descriptionMap.get(LocaleUtil.US)),
				cpDefinition.getDefaultLanguageId(), "Description"));
	}

	private Map<String, String> _getI18nMap(String value) {
		return HashMapBuilder.put(
			LanguageUtil.getLanguageId(LocaleUtil.US), value
		).build();
	}

	private Map<Locale, String> _getLocalizedMap() {
		return HashMapBuilder.put(
			LocaleUtil.JAPAN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.product.internal.upgrade.v5_7_0." +
			"CPDefinitionLinkUpgradeProcess";

	@Inject
	private static CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Inject
	private static CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

}