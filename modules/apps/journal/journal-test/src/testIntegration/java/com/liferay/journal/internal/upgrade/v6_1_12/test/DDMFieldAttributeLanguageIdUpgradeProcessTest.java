/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_12.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DDMFieldAttributeLanguageIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_storageId = RandomTestUtil.randomLong();
	}

	@After
	public void tearDown() {
		_ddmFieldLocalService.deleteDDMFormValues(_storageId);
	}

	@Test
	@TestInfo("LPD-110186")
	public void testUpgradeLocalizableField() throws Exception {
		String value = RandomTestUtil.randomString();

		Value upgradedValue = _upgrade(
			JournalArticle.class.getName(), true, new UnlocalizedValue(value));

		Assert.assertEquals(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, value
			).put(
				LocaleUtil.US, value
			).build(),
			upgradedValue.getValues());
	}

	@Test
	@TestInfo("LPD-110186")
	public void testUpgradeLocalizableFieldInPublication() throws Exception {
		String value = RandomTestUtil.randomString();

		_upgradeInPublication(
			HashMapBuilder.put(
				LocaleUtil.US, value
			).build(),
			true, new UnlocalizedValue(value));
	}

	@Test
	@TestInfo("LPD-110186")
	public void testUpgradeNonlocalizableField() throws Exception {
		String value = RandomTestUtil.randomString();

		Value upgradedValue = _upgrade(
			JournalArticle.class.getName(), false,
			_getLocalizedValue(value, RandomTestUtil.randomString()));

		Assert.assertFalse(upgradedValue.isLocalized());
		Assert.assertEquals(value, upgradedValue.getString(LocaleUtil.US));

		List<DDMFieldAttribute> ddmFieldAttributes =
			_ddmFieldLocalService.getDDMFieldAttributes(
				_storageId, StringPool.BLANK);

		Assert.assertEquals(
			ddmFieldAttributes.toString(), 1, ddmFieldAttributes.size());
	}

	@Test
	@TestInfo("LPD-110186")
	public void testUpgradeNonlocalizableFieldInPublication() throws Exception {
		String value = RandomTestUtil.randomString();

		_upgradeInPublication(
			HashMapBuilder.put(
				LocaleUtil.ROOT, value
			).build(),
			false, _getLocalizedValue(value, RandomTestUtil.randomString()));
	}

	@Test
	@TestInfo("LPD-110186")
	public void testUpgradeOtherModelStructure() throws Exception {
		Assert.assertNull(
			_upgrade(
				LayoutSEOEntry.class.getName(), false,
				_getLocalizedValue(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())));
	}

	private void _assertValues(
		DDMForm ddmForm, Map<Locale, String> expectedValues) {

		Value value = _getValue(ddmForm);

		Assert.assertEquals(expectedValues, value.getValues());
	}

	private DDMForm _createDDMForm(boolean localizable) {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(
				LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.US);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				_FIELD_NAME, _FIELD_NAME, DDMFormFieldTypeConstants.TEXT,
				"string", localizable, false, false, LocaleUtil.SPAIN,
				LocaleUtil.US));

		return ddmForm;
	}

	private LocalizedValue _getLocalizedValue(
		String defaultValue, String translatedValue) {

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.SPAIN, translatedValue);
		localizedValue.addString(LocaleUtil.US, defaultValue);

		return localizedValue;
	}

	private Value _getValue(DDMForm ddmForm) {
		_entityCache.clearCache();
		_multiVMPool.clear();

		DDMFormValues ddmFormValues = _ddmFieldLocalService.getDDMFormValues(
			ddmForm, _storageId);

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			_FIELD_NAME);

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		return ddmFormFieldValue.getValue();
	}

	private void _updateDDMFormValues(
			DDMForm ddmForm, DDMStructure ddmStructure, Value value)
		throws Exception {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, ddmForm.getAvailableLocales(), LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(_FIELD_NAME, value));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _storageId, ddmFormValues);
	}

	private void _upgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.journal.internal.upgrade.v6_1_12." +
				"DDMFieldAttributeLanguageIdUpgradeProcess");

		upgradeProcess.upgrade();
	}

	private Value _upgrade(String className, boolean localizable, Value value)
		throws Exception {

		DDMForm ddmForm = _createDDMForm(localizable);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), className, ddmForm);

		_updateDDMFormValues(ddmForm, ddmStructure, value);

		_upgrade();

		return _getValue(ddmStructure.getDDMForm());
	}

	private void _upgradeInPublication(
			Map<Locale, String> expectedValues, boolean localizable,
			Value value)
		throws Exception {

		DDMForm ddmForm = _createDDMForm(localizable);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), ddmForm);

		DDMForm productionDDMForm = _createDDMForm(!localizable);

		_updateDDMFormValues(productionDDMForm, ddmStructure, value);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_updateDDMFormValues(ddmForm, ddmStructure, value);
		}

		_upgrade();

		_assertValues(productionDDMForm, value.getValues());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_assertValues(ddmForm, expectedValues);
		}

		_ctCollectionService.publishCTCollection(
			TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

		_assertValues(ddmForm, expectedValues);
	}

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _storageId;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}