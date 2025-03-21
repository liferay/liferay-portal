/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

import java.io.File;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
@Sync
public class ImportTranslationsMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testImportTranslations() throws Exception {
		String key1 = "language-key-1";
		String key2 = "language-key-2";
		String value1 = "Language Key 1";
		String value2 = "Language Key 2";

		StringBundler sb = new StringBundler(7);

		sb.append(key1);
		sb.append(StringPool.EQUAL);
		sb.append(value1);
		sb.append(StringPool.RETURN_NEW_LINE);
		sb.append(key2);
		sb.append(StringPool.EQUAL);
		sb.append(value2);

		File file = FileUtil.createTempFile("properties");

		FileUtil.write(file, sb.toString());

		Assert.assertNull(
			_ploEntryLocalService.fetchPLOEntry(
				TestPropsValues.getCompanyId(), key1, _LANGUAGE_ID));

		Assert.assertNull(
			_ploEntryLocalService.fetchPLOEntry(
				TestPropsValues.getCompanyId(), key2, _LANGUAGE_ID));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_importTranslations",
			new Class<?>[] {ActionRequest.class, File.class, String.class},
			new MockLiferayPortletActionRequest(), file, _LANGUAGE_ID);

		_assertLanguageKey(value1, key1);
		_assertLanguageKey(value2, key2);
	}

	@Test
	public void testImportTranslationsWithEmptyFile() throws Exception {
		File file = FileUtil.createTempFile("properties");

		FileUtil.write(file, StringPool.BLANK);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_importTranslations",
			new Class<?>[] {ActionRequest.class, File.class, String.class},
			mockLiferayPortletActionRequest, file, _LANGUAGE_ID);

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, "fileInvalid"));
	}

	@Test
	public void testImportTranslationsWithInvalidFileExtension()
		throws Exception {

		File file = FileUtil.createTempFile();

		FileUtil.write(file, RandomTestUtil.randomString());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_importTranslations",
			new Class<?>[] {ActionRequest.class, File.class, String.class},
			mockLiferayPortletActionRequest, file, _LANGUAGE_ID);

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, "fileExtensionInvalid"));
	}

	private void _assertLanguageKey(String expectedValue, String key)
		throws Exception {

		PLOEntry ploEntry = _ploEntryLocalService.fetchPLOEntry(
			TestPropsValues.getCompanyId(), key, _LANGUAGE_ID);

		Assert.assertNotNull(ploEntry);
		Assert.assertEquals(expectedValue, ploEntry.getValue());
	}

	private static final String _LANGUAGE_ID = "en_US";

	@Inject(
		filter = "mvc.command.name=/portal_language_override/import_translations"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

}