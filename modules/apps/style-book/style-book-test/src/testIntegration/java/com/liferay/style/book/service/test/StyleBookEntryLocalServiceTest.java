/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.exception.DuplicateStyleBookEntryExternalReferenceCodeException;
import com.liferay.style.book.exception.DuplicateStyleBookEntryKeyException;
import com.liferay.style.book.exception.NoSuchEntryException;
import com.liferay.style.book.exception.StyleBookEntryThemeIdException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class StyleBookEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test(expected = StyleBookEntryThemeIdException.MustNotBeNull.class)
	public void testAddStyleBookEntry() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		Assert.assertTrue(
			Validator.isNotNull(styleBookEntry.getExternalReferenceCode()));

		styleBookEntry = _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), true, null, null,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);

		StyleBookEntry defaultStyleBookEntry1 =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), styleBookEntry.getThemeId());

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			defaultStyleBookEntry1.getStyleBookEntryId());

		styleBookEntry = _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), true, null, null,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);

		StyleBookEntry defaultStyleBookEntry2 =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), styleBookEntry.getThemeId());

		Assert.assertNotEquals(
			defaultStyleBookEntry1.getStyleBookEntryId(),
			defaultStyleBookEntry2.getStyleBookEntryId());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			defaultStyleBookEntry2.getStyleBookEntryId());

		_styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), null, null, _serviceContext);
	}

	@Test(
		expected = DuplicateStyleBookEntryExternalReferenceCodeException.class
	)
	public void testAddStyleBookEntryWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);
		_styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Test
	public void testCopyStyleBookEntry() throws Exception {
		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		StyleBookEntry sourceStyleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, frontendTokenDefinition, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(sourceStyleBookEntry);

		String draftFrontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		draftStyleBookEntry.setFrontendTokenDefinition(
			draftFrontendTokenDefinition);

		_styleBookEntryLocalService.updateDraft(draftStyleBookEntry);

		StyleBookEntry copyStyleBookEntry =
			_styleBookEntryLocalService.copyStyleBookEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				sourceStyleBookEntry.getStyleBookEntryId(), _serviceContext);

		Assert.assertEquals(
			frontendTokenDefinition,
			copyStyleBookEntry.getFrontendTokenDefinition());

		StyleBookEntry copyDraftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(copyStyleBookEntry);

		Assert.assertEquals(
			draftFrontendTokenDefinition,
			copyDraftStyleBookEntry.getFrontendTokenDefinition());
	}

	@Test
	public void testDeleteGroup() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		_groupLocalService.deleteGroup(_group);

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));
		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				draftStyleBookEntry.getStyleBookEntryId()));
	}

	@Test
	public void testDeleteStyleBookEntryByExternalReferenceCode()
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_group.getGroupId(), StyleBookPortletKeys.STYLE_BOOK,
			_serviceContext);

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(),
			styleBookEntry.getStyleBookEntryId(),
			StyleBookPortletKeys.STYLE_BOOK, repository.getDlFolderId(),
			RandomTestUtil.randomBytes(), RandomTestUtil.randomString(),
			ContentTypes.TEXT_PLAIN, false);

		styleBookEntry = _styleBookEntryLocalService.updatePreviewFileEntryId(
			styleBookEntry.getStyleBookEntryId(), fileEntry.getFileEntryId(),
			_serviceContext);

		_styleBookEntryLocalService.deleteStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			styleBookEntry.getGroupId());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));

		Assert.assertNull(
			_systemEventLocalService.fetchSystemEvent(
				_group.getGroupId(), _portal.getClassNameId(DLFileEntry.class),
				fileEntry.getFileEntryId(), SystemEventConstants.TYPE_DELETE));

		SystemEvent systemEvent = _systemEventLocalService.fetchSystemEvent(
			styleBookEntry.getGroupId(),
			_portal.getClassNameId(StyleBookEntry.class),
			styleBookEntry.getStyleBookEntryId(),
			SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(
			styleBookEntry.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());

		try {
			_styleBookEntryLocalService.deleteStyleBookEntry(
				styleBookEntry.getExternalReferenceCode(),
				styleBookEntry.getGroupId());

			Assert.fail();
		}
		catch (NoSuchEntryException noSuchEntryException) {
		}
	}

	@Test
	public void testPublishDraft() throws Throwable {
		_testPublishDraftWithCheckedOutDefaultStyleBookEntryVersion();
		_testPublishDraftWithDefaultDraftStyleBookEntry();
		_testPublishDraftWithPublishedStyleBookEntry();
	}

	@Test
	public void testUpdateDefaultStyleBookEntry() throws Throwable {
		_testUpdateDefaultStyleBookEntry();
		_testUpdateDefaultStyleBookEntryWithDefaultStyleBookEntry();
		_testUpdateDefaultStyleBookEntryWithDraftStyleBookEntryId();
		_testUpdateDefaultStyleBookEntryWithDuplicateDefaultStyleBookEntries();
		_testUpdateDefaultStyleBookEntryWithPublishedDraft();
	}

	@Test
	public void testUpdateFrontendTokenDefinition() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		long styleBookEntryId = styleBookEntry.getStyleBookEntryId();

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		styleBookEntry =
			_styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition, _serviceContext);

		Assert.assertEquals(
			frontendTokenDefinition,
			styleBookEntry.getFrontendTokenDefinition());
	}

	@Test
	public void testUpdateStyleBookEntry() throws Throwable {
		_testUpdateStyleBookEntryWithDefaultStyleBookEntry();
		_testUpdateStyleBookEntryWithDefaultStyleBookEntryAndThemeId();
		_testUpdateStyleBookEntryWithDeletedUser();
		_testUpdateStyleBookEntryWithDraftStyleBookEntry();
		_testUpdateStyleBookEntryWithDraftStyleBookEntryId();
		_testUpdateStyleBookEntryWithDuplicateStyleBookEntryKey();
		_testUpdateStyleBookEntryWithFrontendTokens();
		_testUpdateStyleBookEntryWithFrontendTokensAndDraftStyleBookEntry();
		_testUpdateStyleBookEntryWithFrontendTokensAndDraftStyleBookEntryId();
		_testUpdateStyleBookEntryWithFrontendTokensAndFrontendTokenDefinition();
		_testUpdateStyleBookEntryWithUser();
	}

	private StyleBookEntry _addStyleBookEntry(
			boolean defaultStyleBookEntry, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), defaultStyleBookEntry, null, null,
			RandomTestUtil.randomString(), null, themeId, _serviceContext);
	}

	private void _assertDefaultStyleBookEntry(
			boolean defaultStyleBookEntry, long styleBookEntryId)
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.getStyleBookEntry(styleBookEntryId);

		Assert.assertEquals(
			defaultStyleBookEntry, styleBookEntry.isDefaultStyleBookEntry());

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		Assert.assertEquals(
			defaultStyleBookEntry,
			draftStyleBookEntry.isDefaultStyleBookEntry());
	}

	private void _assertDefaultStyleBookEntry(
			StyleBookEntry defaultStyleBookEntry,
			StyleBookEntry... styleBookEntries)
		throws Exception {

		Assert.assertEquals(
			defaultStyleBookEntry.getStyleBookEntryId(),
			_getDefaultStyleBookEntryId(defaultStyleBookEntry.getThemeId()));

		_assertDefaultStyleBookEntry(
			true, defaultStyleBookEntry.getStyleBookEntryId());

		for (StyleBookEntry styleBookEntry : styleBookEntries) {
			_assertDefaultStyleBookEntry(
				false, styleBookEntry.getStyleBookEntryId());
		}
	}

	private long _getDefaultStyleBookEntryId(String themeId) {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId);

		return styleBookEntry.getStyleBookEntryId();
	}

	private void _testPublishDraftWithCheckedOutDefaultStyleBookEntryVersion()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry2.getStyleBookEntryId(), true);

		_styleBookEntryLocalService.publishDraft(
			_styleBookEntryLocalService.checkout(
				_styleBookEntryLocalService.getStyleBookEntry(
					styleBookEntry1.getStyleBookEntryId()),
				1));

		Assert.assertEquals(
			styleBookEntry2.getStyleBookEntryId(),
			_getDefaultStyleBookEntryId(themeId));

		styleBookEntry1 = _styleBookEntryLocalService.getStyleBookEntry(
			styleBookEntry1.getStyleBookEntryId());

		Assert.assertFalse(styleBookEntry1.isDefaultStyleBookEntry());
	}

	private void _testPublishDraftWithDefaultDraftStyleBookEntry()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		StyleBookEntry draftStyleBookEntry2 =
			_styleBookEntryLocalService.getDraft(styleBookEntry2);

		draftStyleBookEntry2.setDefaultStyleBookEntry(true);

		_styleBookEntryLocalService.publishDraft(
			_styleBookEntryLocalService.updateDraft(draftStyleBookEntry2));

		Assert.assertEquals(
			styleBookEntry1.getStyleBookEntryId(),
			_getDefaultStyleBookEntryId(themeId));

		styleBookEntry2 = _styleBookEntryLocalService.getStyleBookEntry(
			styleBookEntry2.getStyleBookEntryId());

		Assert.assertFalse(styleBookEntry2.isDefaultStyleBookEntry());
	}

	private void _testPublishDraftWithPublishedStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		try {
			_styleBookEntryLocalService.publishDraft(styleBookEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testUpdateDefaultStyleBookEntry() throws Exception {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), true, null, null,
				RandomTestUtil.randomString(), null, themeId, _serviceContext);

		Assert.assertTrue(styleBookEntry1.isDefaultStyleBookEntry());

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry1);

		Assert.assertTrue(draftStyleBookEntry.isDefaultStyleBookEntry());

		StyleBookEntry styleBookEntry2 =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null, themeId, _serviceContext);

		Assert.assertFalse(styleBookEntry2.isDefaultStyleBookEntry());

		styleBookEntry2 =
			_styleBookEntryLocalService.updateDefaultStyleBookEntry(
				styleBookEntry2.getStyleBookEntryId(), true);

		Assert.assertTrue(styleBookEntry2.isDefaultStyleBookEntry());

		styleBookEntry1 = _styleBookEntryLocalService.getStyleBookEntry(
			styleBookEntry1.getStyleBookEntryId());

		Assert.assertFalse(styleBookEntry1.isDefaultStyleBookEntry());

		draftStyleBookEntry = _styleBookEntryLocalService.getDraft(
			styleBookEntry1);

		Assert.assertFalse(draftStyleBookEntry.isDefaultStyleBookEntry());
	}

	private void _testUpdateDefaultStyleBookEntryWithDefaultStyleBookEntry()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(true, themeId);

		StyleBookEntry draftStyleBookEntry2 =
			_styleBookEntryLocalService.getDraft(styleBookEntry2);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry2.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			draftStyleBookEntry2.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);
	}

	private void _testUpdateDefaultStyleBookEntryWithDraftStyleBookEntryId()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		StyleBookEntry draftStyleBookEntry2 =
			_styleBookEntryLocalService.getDraft(styleBookEntry2);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			draftStyleBookEntry2.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);
	}

	private void _testUpdateDefaultStyleBookEntryWithDuplicateDefaultStyleBookEntries()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		_updateDefaultStyleBookEntry(styleBookEntry2.getStyleBookEntryId());

		_styleBookEntryLocalService.getDraft(
			styleBookEntry2.getStyleBookEntryId());

		StyleBookEntry styleBookEntry3 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry3);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry3.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(
			styleBookEntry3, styleBookEntry1, styleBookEntry2);
	}

	private void _testUpdateDefaultStyleBookEntryWithPublishedDraft()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(false, themeId);

		StyleBookEntry draftStyleBookEntry1 =
			_styleBookEntryLocalService.getDraft(styleBookEntry1);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry1.getStyleBookEntryId(), true);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry2);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry2.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);

		_styleBookEntryLocalService.publishDraft(
			_styleBookEntryLocalService.getStyleBookEntry(
				draftStyleBookEntry1.getStyleBookEntryId()));

		Assert.assertEquals(
			styleBookEntry2.getStyleBookEntryId(),
			_getDefaultStyleBookEntryId(themeId));

		styleBookEntry1 = _styleBookEntryLocalService.getStyleBookEntry(
			styleBookEntry1.getStyleBookEntryId());

		Assert.assertFalse(styleBookEntry1.isDefaultStyleBookEntry());
	}

	private void _testUpdateStyleBookEntryWithDefaultStyleBookEntry()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry2);

		_updateStyleBookEntry(
			true, styleBookEntry2, styleBookEntry2.getThemeId());

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);

		_updateStyleBookEntry(
			true, styleBookEntry2, styleBookEntry2.getThemeId());

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);
	}

	private void _testUpdateStyleBookEntryWithDefaultStyleBookEntryAndThemeId()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(
			false, RandomTestUtil.randomString());

		styleBookEntry2 = _updateStyleBookEntry(true, styleBookEntry2, themeId);

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);
	}

	private void _testUpdateStyleBookEntryWithDeletedUser() throws Exception {
		User user = UserTestUtil.addUser();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, user.getUserId(), _group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null, _THEME_ID,
				_serviceContext);

		_userLocalService.deleteUser(user);

		styleBookEntry = _styleBookEntryLocalService.updateStyleBookEntry(
			user.getUserId(), styleBookEntry.getStyleBookEntryId(), false,
			styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(), styleBookEntry.getName(),
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), _THEME_ID, _serviceContext);

		Assert.assertEquals(user.getUserId(), styleBookEntry.getUserId());
		Assert.assertEquals(user.getFullName(), styleBookEntry.getUserName());
	}

	private void _testUpdateStyleBookEntryWithDraftStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		draftStyleBookEntry.setFrontendTokenDefinition(
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString()));

		_styleBookEntryLocalService.updateDraft(draftStyleBookEntry);

		styleBookEntry = _updateStyleBookEntry(
			false, styleBookEntry, _THEME_ID);

		Assert.assertNull(
			_styleBookEntryLocalService.fetchDraft(styleBookEntry));

		draftStyleBookEntry = _styleBookEntryLocalService.getDraft(
			styleBookEntry);

		Assert.assertEquals(
			styleBookEntry.getFrontendTokenDefinition(),
			draftStyleBookEntry.getFrontendTokenDefinition());
		Assert.assertEquals(
			styleBookEntry.getName(), draftStyleBookEntry.getName());
	}

	private void _testUpdateStyleBookEntryWithDraftStyleBookEntryId()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.updateStyleBookEntry(
				TestPropsValues.getUserId(),
				draftStyleBookEntry.getStyleBookEntryId(), false,
				styleBookEntry.getFrontendTokenDefinition(),
				styleBookEntry.getFrontendTokensValues(),
				styleBookEntry.getName(), styleBookEntry.getStyleBookEntryKey(),
				styleBookEntry.getPreviewFileEntryId(), themeId,
				_serviceContext);

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			updatedStyleBookEntry.getStyleBookEntryId());
		Assert.assertEquals(themeId, updatedStyleBookEntry.getThemeId());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				draftStyleBookEntry.getStyleBookEntryId()));
	}

	private void _testUpdateStyleBookEntryWithDuplicateStyleBookEntryKey()
		throws Exception {

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(false, _THEME_ID);
		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, _THEME_ID);

		try {
			_styleBookEntryLocalService.updateStyleBookEntry(
				TestPropsValues.getUserId(),
				styleBookEntry2.getStyleBookEntryId(), false,
				styleBookEntry2.getFrontendTokenDefinition(),
				styleBookEntry2.getFrontendTokensValues(),
				styleBookEntry2.getName(),
				styleBookEntry1.getStyleBookEntryKey(),
				styleBookEntry2.getPreviewFileEntryId(), _THEME_ID,
				_serviceContext);

			Assert.fail();
		}
		catch (DuplicateStyleBookEntryKeyException
					duplicateStyleBookEntryKeyException) {
		}
	}

	private void _testUpdateStyleBookEntryWithFrontendTokens()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		String frontendTokenName = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		styleBookEntry = _updateStyleBookEntry(
			null, frontendTokenName, name,
			styleBookEntry.getStyleBookEntryId());

		Assert.assertEquals(name, styleBookEntry.getName());

		JSONObject frontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				styleBookEntry.getFrontendTokensValues());

		Assert.assertTrue(
			frontendTokensValuesJSONObject.has(
				_THEME_ID + StringPool.COLON + frontendTokenName));
	}

	private void _testUpdateStyleBookEntryWithFrontendTokensAndDraftStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		draftStyleBookEntry.setFrontendTokenDefinition(
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString()));

		_styleBookEntryLocalService.updateDraft(draftStyleBookEntry);

		styleBookEntry = _updateStyleBookEntry(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			styleBookEntry.getStyleBookEntryId());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchDraft(styleBookEntry));

		draftStyleBookEntry = _styleBookEntryLocalService.getDraft(
			styleBookEntry);

		Assert.assertEquals(
			styleBookEntry.getFrontendTokenDefinition(),
			draftStyleBookEntry.getFrontendTokenDefinition());
		Assert.assertEquals(
			styleBookEntry.getFrontendTokensValues(),
			draftStyleBookEntry.getFrontendTokensValues());
		Assert.assertEquals(
			styleBookEntry.getName(), draftStyleBookEntry.getName());
	}

	private void _testUpdateStyleBookEntryWithFrontendTokensAndDraftStyleBookEntryId()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		String frontendTokenName = RandomTestUtil.randomString();

		StyleBookEntry updatedStyleBookEntry = _updateStyleBookEntry(
			null, frontendTokenName, styleBookEntry.getName(),
			draftStyleBookEntry.getStyleBookEntryId());

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			updatedStyleBookEntry.getStyleBookEntryId());

		JSONObject frontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				updatedStyleBookEntry.getFrontendTokensValues());

		Assert.assertTrue(
			frontendTokensValuesJSONObject.has(
				_THEME_ID + StringPool.COLON + frontendTokenName));

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				draftStyleBookEntry.getStyleBookEntryId()));
	}

	private void _testUpdateStyleBookEntryWithFrontendTokensAndFrontendTokenDefinition()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		String frontendTokenName = RandomTestUtil.randomString();

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				frontendTokenName);

		styleBookEntry = _updateStyleBookEntry(
			frontendTokenDefinition, frontendTokenName,
			RandomTestUtil.randomString(),
			styleBookEntry.getStyleBookEntryId());

		Assert.assertEquals(
			frontendTokenDefinition,
			styleBookEntry.getFrontendTokenDefinition());

		JSONObject frontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				styleBookEntry.getFrontendTokensValues());

		Assert.assertTrue(
			frontendTokensValuesJSONObject.has(
				StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM +
					StringPool.COLON + frontendTokenName));
	}

	private void _testUpdateStyleBookEntryWithUser() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry(false, _THEME_ID);

		_user = UserTestUtil.addUser();

		styleBookEntry = _styleBookEntryLocalService.updateStyleBookEntry(
			_user.getUserId(), styleBookEntry.getStyleBookEntryId(), false,
			styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(), styleBookEntry.getName(),
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), _THEME_ID, _serviceContext);

		Assert.assertEquals(_user.getUserId(), styleBookEntry.getUserId());
		Assert.assertEquals(_user.getFullName(), styleBookEntry.getUserName());
	}

	private void _updateDefaultStyleBookEntry(long styleBookEntryId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update StyleBookEntry set defaultStyleBookEntry = ? where " +
					"styleBookEntryId = ?")) {

			preparedStatement.setBoolean(1, true);
			preparedStatement.setLong(2, styleBookEntryId);

			preparedStatement.executeUpdate();
		}

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();
	}

	private StyleBookEntry _updateStyleBookEntry(
			boolean defaultStyleBookEntry, StyleBookEntry styleBookEntry,
			String themeId)
		throws Exception {

		return _styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntry.getStyleBookEntryId(),
			defaultStyleBookEntry, styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(),
			RandomTestUtil.randomString(),
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), themeId, _serviceContext);
	}

	private StyleBookEntry _updateStyleBookEntry(
			String frontendTokenDefinition, String frontendTokenName,
			String name, long styleBookEntryId)
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.getStyleBookEntry(styleBookEntryId);

		return _styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntryId,
			styleBookEntry.isDefaultStyleBookEntry(), frontendTokenDefinition,
			JSONUtil.put(
				frontendTokenName,
				JSONUtil.put("value", RandomTestUtil.randomString())
			).toString(),
			name, styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), styleBookEntry.getThemeId(),
			_serviceContext);
	}

	private static final String _THEME_ID = RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}