/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.version.VersionService;
import com.liferay.portal.kernel.service.version.VersionServiceListener;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryException;
import com.liferay.portal.tools.service.builder.test.model.LVEntry;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationVersion;
import com.liferay.portal.tools.service.builder.test.model.LVEntryVersion;
import com.liferay.portal.tools.service.builder.test.service.LVEntryLocalService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class LVEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddLVEntryLocalization() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		LVEntryLocalization lvEntryLocalization =
			_lvEntryLocalService.addLVEntryLocalization(
				draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		Assert.assertEquals(
			lvEntryLocalization,
			_lvEntryLocalService.fetchLVEntryLocalization(
				draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1));

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		try {
			_lvEntryLocalService.addLVEntryLocalization(
				_lvEntry, _LANGUAGE_ID_2, _TITLE_2, _CONTENT_2);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Unable to add a localization to published LVEntry " +
					_lvEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testCheckout() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry.setGroupId(_GROUP_ID_1);

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		LVEntryLocalization lvEntryLocalization1 =
			_lvEntryLocalService.updateLVEntryLocalization(
				draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		LVEntryLocalization lvEntryLocalization2 =
			_lvEntryLocalService.updateLVEntryLocalization(
				draftLVEntry, _LANGUAGE_ID_2, _TITLE_2, _CONTENT_2);

		List<LVEntryVersion> lvEntryVersions = _versionService.getVersions(
			draftLVEntry);

		Assert.assertSame(Collections.emptyList(), lvEntryVersions);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		List<LVEntryLocalization> lvEntryLocalizations =
			_lvEntryLocalService.getLVEntryLocalizations(
				_lvEntry.getPrimaryKey());

		Assert.assertEquals(
			lvEntryLocalizations.toString(), 2, lvEntryLocalizations.size());

		_assertContains(lvEntryLocalization1, lvEntryLocalizations);
		_assertContains(lvEntryLocalization2, lvEntryLocalizations);

		Assert.assertEquals(_GROUP_ID_1, _lvEntry.getGroupId());

		draftLVEntry = _versionService.getDraft(_lvEntry);

		Map<String, String> languageIdToTitleMap =
			draftLVEntry.getLanguageIdToTitleMap();
		Map<String, String> languageIdToContentMap =
			draftLVEntry.getLanguageIdToContentMap();

		Assert.assertEquals(
			_TITLE_2, languageIdToTitleMap.remove(_LANGUAGE_ID_2));
		Assert.assertEquals(
			_CONTENT_2, languageIdToContentMap.remove(_LANGUAGE_ID_2));

		Assert.assertNull(languageIdToTitleMap.put(_LANGUAGE_ID_3, _TITLE_3));
		Assert.assertNull(
			languageIdToContentMap.put(_LANGUAGE_ID_3, _CONTENT_3));

		_lvEntryLocalService.updateLVEntryLocalizations(
			draftLVEntry, languageIdToTitleMap, languageIdToContentMap);

		draftLVEntry.setGroupId(_GROUP_ID_2);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		lvEntryLocalizations = _lvEntryLocalService.getLVEntryLocalizations(
			_lvEntry.getPrimaryKey());

		Assert.assertEquals(
			lvEntryLocalizations.toString(), 2, lvEntryLocalizations.size());

		_assertContains(lvEntryLocalization1, lvEntryLocalizations);
		_assertNotContains(lvEntryLocalization2, lvEntryLocalizations);

		LVEntryLocalization lvEntryLocalization3 =
			_lvEntryLocalService.getLVEntryLocalization(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_3);

		_assertContains(lvEntryLocalization3, lvEntryLocalizations);

		Assert.assertEquals(_GROUP_ID_2, _lvEntry.getGroupId());

		draftLVEntry = _versionService.checkout(_lvEntry, 1);

		Assert.assertEquals(_GROUP_ID_1, draftLVEntry.getGroupId());
		Assert.assertFalse(draftLVEntry.toString(), draftLVEntry.isHead());

		lvEntryVersions = _versionService.getVersions(_lvEntry);

		Assert.assertEquals(
			lvEntryVersions.toString(), 2, lvEntryVersions.size());

		LVEntryVersion lvEntryVersion2 = lvEntryVersions.get(0);
		LVEntryVersion lvEntryVersion1 = lvEntryVersions.get(1);

		Assert.assertEquals(2, lvEntryVersion2.getVersion());
		Assert.assertEquals(_GROUP_ID_2, lvEntryVersion2.getGroupId());

		Assert.assertEquals(1, lvEntryVersion1.getVersion());
		Assert.assertEquals(_GROUP_ID_1, lvEntryVersion1.getGroupId());

		lvEntryLocalizations = _lvEntryLocalService.getLVEntryLocalizations(
			draftLVEntry.getPrimaryKey());

		Assert.assertEquals(
			lvEntryLocalizations.toString(), 2, lvEntryLocalizations.size());

		_assertContains(lvEntryLocalization1, lvEntryLocalizations);
		_assertContains(lvEntryLocalization2, lvEntryLocalizations);
		_assertNotContains(lvEntryLocalization3, lvEntryLocalizations);

		try {
			_versionService.checkout(draftLVEntry, 1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Unable to checkout with unpublished changes " +
					draftLVEntry.getHeadId(),
				illegalArgumentException.getMessage());
		}

		try {
			_versionService.checkout(_lvEntry, 1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Unable to checkout with unpublished changes " +
					draftLVEntry.getHeadId(),
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testCreate() {
		LVEntry lvEntry = _versionService.create();

		Assert.assertFalse(lvEntry.isHead());
		Assert.assertTrue(lvEntry.isNew());
		Assert.assertEquals(lvEntry.getHeadId(), lvEntry.getPrimaryKey());

		Assert.assertNull(_versionService.fetchDraft(lvEntry.getPrimaryKey()));
	}

	@Test
	public void testDelete() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		try {
			_lvEntryLocalService.updateLVEntryLocalization(
				draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

			Assert.fail();
		}
		catch (NoSuchLVEntryException noSuchLVEntryException) {
			Assert.assertEquals(
				"No LVEntry exists with the primary key " +
					draftLVEntry.getPrimaryKey(),
				noSuchLVEntryException.getMessage());
		}

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		draftLVEntry = _versionService.getDraft(_lvEntry);

		try {
			_versionService.delete(draftLVEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"LVEntry is a draft " + draftLVEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_2, _CONTENT_2);

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		Assert.assertNotNull(draftLVEntry);
		Assert.assertNotEquals(draftLVEntry, _lvEntry);

		Assert.assertEquals(_lvEntry, _versionService.delete(_lvEntry));

		List<LVEntryVersion> lvEntryVersions = _versionService.getVersions(
			_lvEntry);

		Assert.assertEquals(
			lvEntryVersions.toString(), 0, lvEntryVersions.size());

		LVEntryLocalization lvEntryLocalization =
			_lvEntryLocalService.fetchLVEntryLocalization(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNull(lvEntryLocalization);

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			_lvEntryLocalService.fetchLVEntryLocalizationVersion(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1, 1);

		Assert.assertNull(lvEntryLocalizationVersion);

		lvEntryLocalization = _lvEntryLocalService.fetchLVEntryLocalization(
			draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNull(lvEntryLocalization);

		_lvEntry = _versionService.fetchPublished(_lvEntry.getLvEntryId());

		Assert.assertNull(_lvEntry);

		draftLVEntry = _versionService.fetchDraft(draftLVEntry.getHeadId());

		Assert.assertNull(draftLVEntry);
	}

	@Test
	public void testDeleteDraft() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		try {
			_versionService.deleteDraft(_lvEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"LVEntry is not a draft " + _lvEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}

		LVEntryLocalization lvEntryLocalization =
			_lvEntryLocalService.fetchLVEntryLocalization(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNotNull(lvEntryLocalization);
		Assert.assertEquals(_TITLE_1, lvEntryLocalization.getTitle());
		Assert.assertEquals(_CONTENT_1, lvEntryLocalization.getContent());

		draftLVEntry = _versionService.getDraft(_lvEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_2);

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		Assert.assertNotNull(draftLVEntry);
		Assert.assertNotEquals(draftLVEntry, _lvEntry);

		Assert.assertSame(
			draftLVEntry, _versionService.deleteDraft(draftLVEntry));

		lvEntryLocalization = _lvEntryLocalService.fetchLVEntryLocalization(
			_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNotNull(lvEntryLocalization);

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			_lvEntryLocalService.fetchLVEntryLocalizationVersion(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1, 1);

		Assert.assertNotNull(lvEntryLocalizationVersion);

		lvEntryLocalization = _lvEntryLocalService.fetchLVEntryLocalization(
			draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNull(lvEntryLocalization);

		_lvEntry = _versionService.fetchPublished(_lvEntry.getLvEntryId());

		Assert.assertNotNull(_lvEntry);

		draftLVEntry = _versionService.fetchDraft(draftLVEntry.getHeadId());

		Assert.assertNull(draftLVEntry);
	}

	@Test
	public void testDeleteLatestVersion() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		LVEntryVersion lvEntryVersion = _versionService.fetchLatestVersion(
			_lvEntry);

		Assert.assertNotNull(lvEntryVersion);

		try {
			_versionService.deleteVersion(lvEntryVersion);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Unable to delete latest version 1",
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testDeleteLVEntry() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		_lvEntryLocalService.deleteLVEntry(_lvEntry.getPrimaryKey());

		_lvEntry = _lvEntryLocalService.deleteLVEntry(_lvEntry.getPrimaryKey());

		Assert.assertNull(_lvEntry);

		draftLVEntry = _versionService.create();

		try {
			_lvEntryLocalService.deleteLVEntry(draftLVEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"LVEntry is a draft " + draftLVEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}

		try {
			_lvEntryLocalService.deleteLVEntry(draftLVEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"LVEntry is a draft " + draftLVEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		PortalException portalException1 = new PortalException();

		TestVersionServiceListener testVersionServiceListener =
			new TestVersionServiceListener(Collections.emptyMap()) {

				@Override
				public void afterDelete(LVEntry lvEntry)
					throws PortalException {

					throw portalException1;
				}

			};

		_versionService.registerListener(testVersionServiceListener);

		try {
			try {
				_lvEntryLocalService.deleteLVEntry(_lvEntry.getPrimaryKey());

				Assert.fail();
			}
			catch (PortalException portalException2) {
				Assert.assertSame(portalException1, portalException2);
			}

			try {
				_lvEntryLocalService.deleteLVEntry(_lvEntry);

				Assert.fail();
			}
			catch (SystemException systemException) {
				Assert.assertSame(portalException1, systemException.getCause());
			}
		}
		finally {
			_versionService.unregisterListener(testVersionServiceListener);
		}
	}

	@Test
	public void testDeleteVersion() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		draftLVEntry = _versionService.getDraft(_lvEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		draftLVEntry = _versionService.getDraft(_lvEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_2, _CONTENT_2);

		LVEntryVersion lvEntryVersion1 = _versionService.getVersion(
			_lvEntry, 1);

		Assert.assertEquals(
			lvEntryVersion1, _versionService.deleteVersion(lvEntryVersion1));

		List<LVEntryVersion> lvEntryVersions = _versionService.getVersions(
			_lvEntry);

		Assert.assertEquals(
			lvEntryVersions.toString(), 1, lvEntryVersions.size());

		LVEntryVersion lvEntryVersion2 = lvEntryVersions.get(0);

		Assert.assertEquals(2, lvEntryVersion2.getVersion());

		LVEntryLocalization lvEntryLocalization =
			_lvEntryLocalService.fetchLVEntryLocalization(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNotNull(lvEntryLocalization);

		lvEntryLocalization = _lvEntryLocalService.fetchLVEntryLocalization(
			draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNotNull(lvEntryLocalization);

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			_lvEntryLocalService.fetchLVEntryLocalizationVersion(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1, 1);

		Assert.assertNull(lvEntryLocalizationVersion);

		lvEntryLocalizationVersion =
			_lvEntryLocalService.fetchLVEntryLocalizationVersion(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1, 2);

		Assert.assertNotNull(lvEntryLocalizationVersion);
	}

	@Test
	public void testFetchDraft() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry.setGroupId(_GROUP_ID_1);

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		Assert.assertFalse(draftLVEntry.isHead());

		Assert.assertSame(
			draftLVEntry, _versionService.fetchDraft(draftLVEntry));

		LVEntryLocalization draftLVEntryLocalization =
			_lvEntryLocalService.updateLVEntryLocalization(
				draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		Assert.assertEquals(
			draftLVEntryLocalization,
			_lvEntryLocalService.getLVEntryLocalization(
				draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1));

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		LVEntryLocalization publishedVersionEntryContent =
			_lvEntryLocalService.getLVEntryLocalization(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertEquals(_TITLE_1, publishedVersionEntryContent.getTitle());
		Assert.assertEquals(
			_CONTENT_1, publishedVersionEntryContent.getContent());

		Assert.assertNotEquals(draftLVEntry, _lvEntry);

		draftLVEntryLocalization =
			_lvEntryLocalService.fetchLVEntryLocalization(
				draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertNull(draftLVEntryLocalization);

		Assert.assertNull(_versionService.fetchDraft(_lvEntry));

		draftLVEntry = _versionService.getDraft(_lvEntry);

		Assert.assertNotNull(draftLVEntry);
		Assert.assertNotEquals(draftLVEntry, _lvEntry);

		Assert.assertEquals(draftLVEntry.getGroupId(), _lvEntry.getGroupId());
	}

	@Test
	public void testFetchLatestVersion() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		draftLVEntry = _versionService.getDraft(_lvEntry);

		LVEntryVersion lvEntryVersion = _versionService.fetchLatestVersion(
			draftLVEntry);

		Assert.assertEquals(
			lvEntryVersion, _versionService.getVersion(draftLVEntry, 1));
	}

	@Test
	public void testFetchPublished() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		Assert.assertFalse(draftLVEntry.isHead());

		Assert.assertNull(_versionService.fetchPublished(draftLVEntry));
		Assert.assertNull(
			_versionService.fetchPublished(draftLVEntry.getPrimaryKey()));

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		LVEntryLocalization lvEntryLocalization =
			_lvEntryLocalService.getLVEntryLocalization(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertEquals(_TITLE_1, lvEntryLocalization.getTitle());
		Assert.assertEquals(_CONTENT_1, lvEntryLocalization.getContent());

		Assert.assertEquals(_lvEntry, _versionService.fetchPublished(_lvEntry));
		Assert.assertEquals(
			_lvEntry, _versionService.fetchPublished(_lvEntry.getPrimaryKey()));

		draftLVEntry = _versionService.getDraft(_lvEntry);

		Assert.assertEquals(
			_lvEntry, _versionService.fetchPublished(draftLVEntry));
		Assert.assertEquals(
			_lvEntry, _versionService.fetchPublished(draftLVEntry.getHeadId()));
	}

	@Test
	public void testGetDraft() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		Assert.assertSame(draftLVEntry, _versionService.getDraft(draftLVEntry));

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		Assert.assertEquals(
			draftLVEntry,
			_versionService.getDraft(draftLVEntry.getPrimaryKey()));

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		draftLVEntry = _versionService.getDraft(_lvEntry.getPrimaryKey());

		Assert.assertNotEquals(_lvEntry, draftLVEntry);

		LVEntryLocalization lvEntryLocalization =
			_lvEntryLocalService.getLVEntryLocalization(
				draftLVEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertEquals(_TITLE_1, lvEntryLocalization.getTitle());
		Assert.assertEquals(_CONTENT_1, lvEntryLocalization.getContent());
	}

	@Test
	public void testGetVersions() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		draftLVEntry.setGroupId(_GROUP_ID_1);

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		draftLVEntry = _versionService.getDraft(_lvEntry);

		draftLVEntry.setGroupId(_GROUP_ID_2);

		_lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, _LANGUAGE_ID_1, _TITLE_2, _CONTENT_2);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		List<LVEntryVersion> lvEntryVersions = _versionService.getVersions(
			draftLVEntry);

		Assert.assertEquals(
			lvEntryVersions.toString(), 2, lvEntryVersions.size());

		LVEntryVersion lvEntryVersion2 = lvEntryVersions.get(0);

		Assert.assertEquals(2, lvEntryVersion2.getVersion());
		Assert.assertEquals(_GROUP_ID_2, lvEntryVersion2.getGroupId());

		LVEntryVersion lvEntryVersion1 = lvEntryVersions.get(1);

		Assert.assertEquals(1, lvEntryVersion1.getVersion());
		Assert.assertEquals(_GROUP_ID_1, lvEntryVersion1.getGroupId());

		List<LVEntryLocalizationVersion> lvEntryLocalizationVersions =
			_lvEntryLocalService.getLVEntryLocalizationVersions(
				_lvEntry.getPrimaryKey(), _LANGUAGE_ID_1);

		Assert.assertEquals(
			lvEntryLocalizationVersions.toString(), 2,
			lvEntryLocalizationVersions.size());

		LVEntryLocalizationVersion lvEntryLocalizationVersion2 =
			lvEntryLocalizationVersions.get(0);

		Assert.assertEquals(2, lvEntryLocalizationVersion2.getVersion());
		Assert.assertEquals(_TITLE_2, lvEntryLocalizationVersion2.getTitle());
		Assert.assertEquals(
			_CONTENT_2, lvEntryLocalizationVersion2.getContent());

		LVEntryLocalizationVersion lvEntryLocalizationVersion1 =
			lvEntryLocalizationVersions.get(1);

		Assert.assertEquals(1, lvEntryLocalizationVersion1.getVersion());
		Assert.assertEquals(_TITLE_1, lvEntryLocalizationVersion1.getTitle());
		Assert.assertEquals(
			_CONTENT_1, lvEntryLocalizationVersion1.getContent());
	}

	@Test
	public void testPublish() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		Assert.assertFalse(draftLVEntry.toString(), draftLVEntry.isHead());

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		Assert.assertTrue(_lvEntry.toString(), _lvEntry.isHead());

		LVEntryVersion lvEntryVersion = _versionService.fetchLatestVersion(
			_lvEntry);

		Assert.assertNotNull(lvEntryVersion);

		Assert.assertEquals(1, lvEntryVersion.getVersion());

		try {
			_versionService.publishDraft(_lvEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Can only publish drafts " + _lvEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testSameService() throws Exception {
		Assert.assertSame(_lvEntryLocalService, _versionService);
	}

	@Test
	public void testServiceListener() throws Exception {
		Map<String, Object[]> methodNameToParametersMap = new HashMap<>();

		TestVersionServiceListener testVersionServiceListener =
			new TestVersionServiceListener(methodNameToParametersMap);

		_versionService.registerListener(testVersionServiceListener);

		LVEntry draftLVEntry = _versionService.create();

		Assert.assertTrue(
			methodNameToParametersMap.toString(),
			methodNameToParametersMap.isEmpty());

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		Object[] parameters = methodNameToParametersMap.remove(
			"afterCreateDraft");

		Assert.assertSame(draftLVEntry, parameters[0]);

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		parameters = methodNameToParametersMap.remove("afterUpdateDraft");

		Assert.assertSame(draftLVEntry, parameters[0]);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		parameters = methodNameToParametersMap.remove("afterPublishDraft");

		Assert.assertSame(draftLVEntry, parameters[0]);
		Assert.assertSame(1, parameters[1]);

		draftLVEntry = _versionService.getDraft(_lvEntry);

		parameters = methodNameToParametersMap.remove("afterCreateDraft");

		Assert.assertSame(draftLVEntry, parameters[0]);

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		parameters = methodNameToParametersMap.remove("afterPublishDraft");

		Assert.assertSame(draftLVEntry, parameters[0]);
		Assert.assertSame(2, parameters[1]);

		LVEntryVersion lvEntryVersion = _versionService.getVersion(_lvEntry, 1);

		_versionService.deleteVersion(lvEntryVersion);

		parameters = methodNameToParametersMap.remove("afterDeleteVersion");

		Assert.assertEquals(lvEntryVersion, parameters[0]);

		draftLVEntry = _versionService.checkout(_lvEntry, 2);

		parameters = methodNameToParametersMap.remove("afterCheckout");

		Assert.assertSame(draftLVEntry, parameters[0]);
		Assert.assertEquals(2, parameters[1]);

		_versionService.deleteDraft(draftLVEntry);

		parameters = methodNameToParametersMap.remove("afterDeleteDraft");

		Assert.assertSame(draftLVEntry, parameters[0]);

		_versionService.delete(_lvEntry);

		parameters = methodNameToParametersMap.remove("afterDelete");

		Assert.assertSame(_lvEntry, parameters[0]);

		Assert.assertTrue(
			methodNameToParametersMap.toString(),
			methodNameToParametersMap.isEmpty());

		_lvEntry = null;

		_versionService.unregisterListener(testVersionServiceListener);

		draftLVEntry = _versionService.create();

		draftLVEntry = _versionService.updateDraft(draftLVEntry);

		_versionService.deleteDraft(draftLVEntry);

		Assert.assertTrue(
			methodNameToParametersMap.toString(),
			methodNameToParametersMap.isEmpty());
	}

	@Test
	public void testUpdate() throws Exception {
		LVEntry draftLVEntry = _versionService.create();

		_lvEntry = _versionService.publishDraft(draftLVEntry);

		try {
			_versionService.updateDraft(_lvEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Can only update draft entries " + _lvEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}

		try {
			_lvEntryLocalService.updateLVEntryLocalization(
				_lvEntry, _LANGUAGE_ID_1, _TITLE_1, _CONTENT_1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Can only update draft entries " + _lvEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}

		try {
			_lvEntryLocalService.updateLVEntryLocalizations(
				_lvEntry, Collections.emptyMap(), Collections.emptyMap());

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Can only update draft entries " + _lvEntry.getPrimaryKey(),
				illegalArgumentException.getMessage());
		}
	}

	private void _assertContains(
		LVEntryLocalization expectedLVEntryLocalization,
		List<LVEntryLocalization> lvEntryLocalizations) {

		boolean found = false;

		for (LVEntryLocalization lvEntryLocalization : lvEntryLocalizations) {
			if (Objects.equals(
					expectedLVEntryLocalization.getLanguageId(),
					lvEntryLocalization.getLanguageId())) {

				Assert.assertEquals(
					expectedLVEntryLocalization.getTitle(),
					lvEntryLocalization.getTitle());

				Assert.assertEquals(
					expectedLVEntryLocalization.getContent(),
					lvEntryLocalization.getContent());

				found = true;

				break;
			}
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Failed to find ", expectedLVEntryLocalization, " in ",
				lvEntryLocalizations),
			found);
	}

	private void _assertNotContains(
		LVEntryLocalization expectedLVEntryLocalization,
		List<LVEntryLocalization> lvEntryLocalizations) {

		for (LVEntryLocalization lvEntryLocalization : lvEntryLocalizations) {
			if (Objects.equals(
					expectedLVEntryLocalization.getLanguageId(),
					lvEntryLocalization.getLanguageId())) {

				Assert.assertNotEquals(
					expectedLVEntryLocalization.getTitle(),
					lvEntryLocalization.getTitle());

				Assert.assertNotEquals(
					expectedLVEntryLocalization.getContent(),
					lvEntryLocalization.getContent());

				return;
			}
		}
	}

	private static final String _CONTENT_1 = "CONTENT 1";

	private static final String _CONTENT_2 = "CONTENT 2";

	private static final String _CONTENT_3 = "CONTENT 3";

	private static final long _GROUP_ID_1 = 1;

	private static final long _GROUP_ID_2 = 2;

	private static final String _LANGUAGE_ID_1 = "LANGUAGE ID 1";

	private static final String _LANGUAGE_ID_2 = "LANGUAGE ID 2";

	private static final String _LANGUAGE_ID_3 = "LANGUAGE ID 3";

	private static final String _TITLE_1 = "TITLE 1";

	private static final String _TITLE_2 = "TITLE 2";

	private static final String _TITLE_3 = "TITLE 3";

	@DeleteAfterTestRun
	private LVEntry _lvEntry;

	@Inject
	private LVEntryLocalService _lvEntryLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.portal.tools.service.builder.test.model.LVEntry"
	)
	private VersionService<LVEntry, LVEntryVersion> _versionService;

	private static class TestVersionServiceListener
		implements VersionServiceListener<LVEntry, LVEntryVersion> {

		@Override
		public void afterCheckout(LVEntry lvEntry, int version) {
			_methodNameToParametersMap.put(
				"afterCheckout", new Object[] {lvEntry, version});
		}

		@Override
		public void afterCreateDraft(LVEntry lvEntry) {
			_methodNameToParametersMap.put(
				"afterCreateDraft", new Object[] {lvEntry});
		}

		@Override
		public void afterDelete(LVEntry lvEntry) throws PortalException {
			_methodNameToParametersMap.put(
				"afterDelete", new Object[] {lvEntry});
		}

		@Override
		public void afterDeleteDraft(LVEntry lvEntry) {
			_methodNameToParametersMap.put(
				"afterDeleteDraft", new Object[] {lvEntry});
		}

		@Override
		public void afterDeleteVersion(LVEntryVersion lvEntryVersion) {
			_methodNameToParametersMap.put(
				"afterDeleteVersion", new Object[] {lvEntryVersion});
		}

		@Override
		public void afterPublishDraft(LVEntry lvEntry, int version) {
			_methodNameToParametersMap.put(
				"afterPublishDraft", new Object[] {lvEntry, version});
		}

		@Override
		public void afterUpdateDraft(LVEntry lvEntry) {
			_methodNameToParametersMap.put(
				"afterUpdateDraft", new Object[] {lvEntry});
		}

		private TestVersionServiceListener(
			Map<String, Object[]> methodNameToParametersMap) {

			_methodNameToParametersMap = methodNameToParametersMap;
		}

		private final Map<String, Object[]> _methodNameToParametersMap;

	}

}