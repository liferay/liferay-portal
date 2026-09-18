/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_11.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
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
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class DDMFieldAttributeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_storageId = RandomTestUtil.randomLong();
	}

	@After
	public void tearDown() {
		_ddmFieldLocalService.deleteDDMFormValues(_storageId);
	}

	@Test
	@TestInfo("LPD-104003")
	public void testUpgradeDocumentLibraryURL() throws Exception {
		DDMFormValues ddmFormValues = _upgrade(
			JournalArticle.class.getName(),
			StringBundler.concat(
				"/c/document_library/get_file?uuid=", _fileEntry.getUuid(),
				"&groupId=", _fileEntry.getGroupId()));

		_assertDocumentLibraryValue(ddmFormValues, LocaleUtil.US);
	}

	@Test
	@TestInfo("LPD-104003")
	public void testUpgradeImageGalleryURL() throws Exception {
		DDMFormValues ddmFormValues = _upgrade(
			JournalArticle.class.getName(),
			_getImageGalleryURL(_fileEntry.getUuid()));

		_assertDocumentLibraryValue(ddmFormValues, LocaleUtil.US);
	}

	@Test
	@TestInfo("LPD-104003")
	public void testUpgradeImageGalleryURLWithMissingFileEntry()
		throws Exception {

		String url = _getImageGalleryURL(RandomTestUtil.randomString());

		DDMFormValues ddmFormValues = _upgrade(
			JournalArticle.class.getName(), url);

		Assert.assertEquals(url, _getValueString(ddmFormValues, LocaleUtil.US));
	}

	@Test
	@TestInfo("LPD-104003")
	public void testUpgradeLocalizedImageGalleryURLs() throws Exception {
		String url = _getImageGalleryURL(RandomTestUtil.randomString());

		DDMFormValues ddmFormValues = _upgrade(
			JournalArticle.class.getName(),
			_getImageGalleryURL(_fileEntry.getUuid()), url);

		_assertDocumentLibraryValue(ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(
			url, _getValueString(ddmFormValues, LocaleUtil.SPAIN));
	}

	@Test
	@TestInfo("LPD-104003")
	public void testUpgradeOtherModelStructure() throws Exception {
		String url = _getImageGalleryURL(_fileEntry.getUuid());

		DDMFormValues ddmFormValues = _upgrade(
			LayoutSEOEntry.class.getName(), url);

		Assert.assertEquals(url, _getValueString(ddmFormValues, LocaleUtil.US));
	}

	@Test
	@TestInfo("LPD-104003")
	public void testUpgradeUnknownValue() throws Exception {
		String value = RandomTestUtil.randomString();

		DDMFormValues ddmFormValues = _upgrade(
			JournalArticle.class.getName(), value);

		Assert.assertEquals(
			value, _getValueString(ddmFormValues, LocaleUtil.US));
	}

	private void _assertDocumentLibraryValue(
			DDMFormValues ddmFormValues, Locale locale)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_getValueString(ddmFormValues, locale));

		Assert.assertEquals(
			_fileEntry.getGroupId(), jsonObject.getLong("groupId"));
		Assert.assertEquals(
			_fileEntry.getTitle(), jsonObject.getString("title"));
		Assert.assertEquals("document", jsonObject.getString("type"));
		Assert.assertEquals(_fileEntry.getUuid(), jsonObject.getString("uuid"));

		List<DDMFieldAttribute> ddmFieldAttributes =
			_ddmFieldLocalService.getDDMFieldAttributes(_storageId, "uuid");

		Assert.assertEquals(
			ddmFieldAttributes.toString(), 1, ddmFieldAttributes.size());
	}

	private String _getImageGalleryURL(String uuid) {
		return StringBundler.concat(
			"/image/image_gallery?uuid=", uuid, "&groupId=",
			_fileEntry.getGroupId(), "&t=", RandomTestUtil.randomLong());
	}

	private String _getValueString(DDMFormValues ddmFormValues, Locale locale) {
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			_FIELD_NAME);

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		LocalizedValue localizedValue =
			(LocalizedValue)ddmFormFieldValue.getValue();

		return localizedValue.getString(locale);
	}

	private DDMFormValues _upgrade(String className, String... values)
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(
				LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.US);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				_FIELD_NAME, _FIELD_NAME,
				DDMFormFieldTypeConstants.DOCUMENT_LIBRARY,
				DDMFormFieldTypeConstants.DOCUMENT_LIBRARY, true, false, false,
				LocaleUtil.SPAIN, LocaleUtil.US));

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), className, ddmForm);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, ddmForm.getAvailableLocales(), LocaleUtil.US);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		Locale[] locales = {LocaleUtil.US, LocaleUtil.SPAIN};

		for (int i = 0; i < values.length; i++) {
			localizedValue.addString(locales[i], values[i]);
		}

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				_FIELD_NAME, localizedValue));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _storageId, ddmFormValues);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.journal.internal.upgrade.v6_1_11." +
				"DDMFieldAttributeUpgradeProcess");

		upgradeProcess.upgrade();

		_entityCache.clearCache();

		_multiVMPool.clear();

		return _ddmFieldLocalService.getDDMFormValues(
			ddmStructure.getDDMForm(), _storageId);
	}

	private static final String _FIELD_NAME = "DocumentLibrary";

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private EntityCache _entityCache;

	private FileEntry _fileEntry;

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