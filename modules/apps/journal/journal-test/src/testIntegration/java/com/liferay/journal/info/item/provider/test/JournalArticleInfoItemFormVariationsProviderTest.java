/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class JournalArticleInfoItemFormVariationsProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
		_group3 = GroupTestUtil.addGroup();
	}

	@Test
	public void testInfoItemFormVariationsContainsCompanyDDMStructures()
		throws Exception {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				JournalArticle.class.getName());

		Collection<InfoItemFormVariation> infoItemFormVariations =
			infoItemFormVariationsProvider.getInfoItemFormVariationsByCompanyId(
				TestPropsValues.getCompanyId());

		int size = infoItemFormVariations.size();

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());
		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group2.getGroupId(), JournalArticle.class.getName());
		DDMStructure ddmStructure3 = DDMStructureTestUtil.addStructure(
			_group3.getGroupId(), JournalArticle.class.getName());

		Map<Group, DDMStructure> ddmStructureMap =
			HashMapBuilder.<Group, DDMStructure>put(
				_group1, ddmStructure1
			).put(
				_group2, ddmStructure2
			).put(
				_group3, ddmStructure3
			).build();

		infoItemFormVariations =
			infoItemFormVariationsProvider.getInfoItemFormVariationsByCompanyId(
				TestPropsValues.getCompanyId());

		_assertInfoItemFormVariations(
			ddmStructureMap, infoItemFormVariations, size);
	}

	@Test
	public void testInfoItemFormVariationsContainsGroupsDDMStructures()
		throws Exception {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				JournalArticle.class.getName());

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());
		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group2.getGroupId(), JournalArticle.class.getName());
		DDMStructure ddmStructure3 = DDMStructureTestUtil.addStructure(
			_group3.getGroupId(), JournalArticle.class.getName());

		_assertInfoItemFormVariations(
			HashMapBuilder.put(
				_group1, ddmStructure1
			).put(
				_group2, ddmStructure2
			).put(
				_group3, ddmStructure3
			).build(),
			infoItemFormVariationsProvider.getInfoItemFormVariations(
				new long[] {
					_group1.getGroupId(), _group2.getGroupId(),
					_group3.getGroupId()
				}),
			0);
	}

	@Test
	public void testInfoItemFormVariationsContainsNewDDMStructure()
		throws Exception {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				JournalArticle.class.getName());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		Assert.assertNotNull(
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				_group1.getGroupId(),
				String.valueOf(ddmStructure.getStructureId())));
	}

	@Test
	@TestInfo("LPD-56469")
	public void testInfoItemFormVariationsNoDDMStructureNameForDefaultLanguage()
		throws Exception {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				JournalArticle.class.getName());

		Collection<InfoItemFormVariation> infoItemFormVariations =
			infoItemFormVariationsProvider.getInfoItemFormVariationsByCompanyId(
				TestPropsValues.getCompanyId());

		Assert.assertFalse(
			infoItemFormVariations.toString(),
			infoItemFormVariations.isEmpty());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User user = company.getGuestUser();

		String originalLanguageId = user.getLanguageId();

		Map<String, String> infoItemFormVariationsMap = new HashMap<>();

		for (InfoItemFormVariation infoItemFormVariation :
				infoItemFormVariations) {

			String label = infoItemFormVariation.getLabel(
				LocaleUtil.fromLanguageId(originalLanguageId));

			Assert.assertTrue(Validator.isNotNull(label));

			infoItemFormVariationsMap.put(
				infoItemFormVariation.getKey(), label);
		}

		Set<Locale> availableLocales = _language.getAvailableLocales();

		_companyLocalService.updatePreferences(
			company.getCompanyId(),
			UnicodePropertiesBuilder.put(
				PropsKeys.LOCALES, "en_CA,en_US,fr_CA"
			).build());
		_companyLocalService.updateDisplay(
			company.getCompanyId(), "en_CA", user.getTimeZoneId());
		_companyLocalService.updatePreferences(
			company.getCompanyId(),
			UnicodePropertiesBuilder.put(
				PropsKeys.LOCALES, "en_CA,fr_CA"
			).build());

		try {
			Collection<InfoItemFormVariation> curInfoItemFormVariations =
				infoItemFormVariationsProvider.
					getInfoItemFormVariationsByCompanyId(
						TestPropsValues.getCompanyId());

			Assert.assertEquals(
				curInfoItemFormVariations.toString(),
				infoItemFormVariations.size(),
				curInfoItemFormVariations.size());

			for (InfoItemFormVariation curInfoItemFormVariation :
					curInfoItemFormVariations) {

				Assert.assertTrue(
					Validator.isNotNull(
						curInfoItemFormVariation.getLabel(LocaleUtil.CANADA)));
				Assert.assertTrue(
					Validator.isNotNull(
						curInfoItemFormVariation.getLabel(
							LocaleUtil.CANADA_FRENCH)));
				Assert.assertEquals(
					infoItemFormVariationsMap.get(
						curInfoItemFormVariation.getKey()),
					curInfoItemFormVariation.getLabel(LocaleUtil.CANADA));
			}
		}
		finally {
			_companyLocalService.updateDisplay(
				company.getCompanyId(), originalLanguageId,
				user.getTimeZoneId());

			PropsValues.LOCALES_ENABLED = PropsUtil.getArray(
				PropsKeys.LOCALES_ENABLED);

			_language.init();

			CompanyTestUtil.resetCompanyLocales(
				TestPropsValues.getCompanyId(), availableLocales,
				LocaleUtil.getDefault());
		}
	}

	private void _assertInfoItemFormVariations(
		Map<Group, DDMStructure> ddmStructureMap,
		Collection<InfoItemFormVariation> infoItemFormVariations,
		int originalSize) {

		Assert.assertEquals(
			infoItemFormVariations.toString(),
			originalSize + ddmStructureMap.size(),
			infoItemFormVariations.size());

		Map<Long, Long> infoItemFormVariationsMap = new HashMap<>();

		for (InfoItemFormVariation infoItemFormVariation :
				infoItemFormVariations) {

			infoItemFormVariationsMap.put(
				GetterUtil.getLong(infoItemFormVariation.getKey()),
				infoItemFormVariation.getGroupId());
		}

		for (Map.Entry<Group, DDMStructure> entry :
				ddmStructureMap.entrySet()) {

			Group group = entry.getKey();
			DDMStructure ddmStructure = entry.getValue();

			Assert.assertEquals(
				Long.valueOf(group.getGroupId()),
				infoItemFormVariationsMap.get(ddmStructure.getStructureId()));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "ddm.form.deserializer.type=json")
	private DDMFormDeserializer _ddmFormDeserializer;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@DeleteAfterTestRun
	private Group _group3;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Language _language;

}