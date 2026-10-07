/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.exception.DuplicateStyleBookEntryExternalReferenceCodeException;
import com.liferay.style.book.exception.StyleBookEntryThemeIdException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

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
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, styleBookEntry.getStatus());

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

		_styleBookEntryLocalService.deleteStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			styleBookEntry.getGroupId());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));
	}

	@Test
	public void testGetOrAddEmptyStyleBookEntry() throws Exception {
		_testGetOrAddEmptyStyleBookEntry();
		_testGetOrAddEmptyStyleBookEntryWithInvalidName();
		_testGetOrAddEmptyStyleBookEntryWithLongExternalReferenceCode();
		_testGetOrAddEmptyStyleBookEntryWithoutThemeId();
	}

	@Test
	public void testPublishDraft() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null, _THEME_ID,
				_serviceContext);

		_testPublishDraft(styleBookEntry);

		try {
			_styleBookEntryLocalService.publishDraft(styleBookEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		StyleBookEntry emptyStyleBookEntry = _getOrAddEmptyStyleBookEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), _THEME_ID);

		_testPublishDraft(emptyStyleBookEntry);
	}

	@Test
	public void testUpdateDefaultStyleBookEntry() throws Exception {
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
	public void testUpdateStyleBookEntry() throws Exception {
		_testUpdateStyleBookEntryWithApprovedStyleBookEntry();
		_testUpdateStyleBookEntryWithEmptyStyleBookEntry();
		_testUpdateStyleBookEntryWithEmptyStyleBookEntryAndWithoutThemeId();
	}

	private StyleBookEntry _getOrAddEmptyStyleBookEntry(
			String externalReferenceCode, long groupId, String themeId)
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			return _styleBookEntryLocalService.getOrAddEmptyStyleBookEntry(
				externalReferenceCode, TestPropsValues.getUserId(), groupId,
				themeId);
		}
	}

	private void _testGetOrAddEmptyStyleBookEntry() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry = _getOrAddEmptyStyleBookEntry(
			externalReferenceCode, _group.getGroupId(), _THEME_ID);

		Assert.assertEquals(
			externalReferenceCode, styleBookEntry.getExternalReferenceCode());
		Assert.assertEquals(_group.getGroupId(), styleBookEntry.getGroupId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, styleBookEntry.getStatus());
		Assert.assertEquals(_THEME_ID, styleBookEntry.getThemeId());
		Assert.assertFalse(styleBookEntry.isDefaultStyleBookEntry());

		StyleBookEntry existingStyleBookEntry = _getOrAddEmptyStyleBookEntry(
			externalReferenceCode, _group.getGroupId(), _THEME_ID);

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			existingStyleBookEntry.getStyleBookEntryId());

		StyleBookEntry approvedStyleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null, _THEME_ID,
				_serviceContext);

		existingStyleBookEntry = _getOrAddEmptyStyleBookEntry(
			approvedStyleBookEntry.getExternalReferenceCode(),
			_group.getGroupId(), RandomTestUtil.randomString());

		Assert.assertEquals(
			approvedStyleBookEntry.getStyleBookEntryId(),
			existingStyleBookEntry.getStyleBookEntryId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			existingStyleBookEntry.getStatus());
		Assert.assertEquals(_THEME_ID, existingStyleBookEntry.getThemeId());
	}

	private void _testGetOrAddEmptyStyleBookEntryWithInvalidName()
		throws Exception {

		String suffix = RandomTestUtil.randomString(70);

		StyleBookEntry styleBookEntry = _getOrAddEmptyStyleBookEntry(
			"a.b/c" + suffix, _group.getGroupId(), _THEME_ID);

		String name = "a-b-c" + suffix;

		Assert.assertEquals(name, styleBookEntry.getName());

		styleBookEntry = _getOrAddEmptyStyleBookEntry(
			"a/b.c" + suffix, _group.getGroupId(), _THEME_ID);

		Assert.assertEquals(name.substring(0, 71), styleBookEntry.getName());
	}

	private void _testGetOrAddEmptyStyleBookEntryWithLongExternalReferenceCode()
		throws Exception {

		String styleBookEntryKey = StringUtil.toLowerCase(
			RandomTestUtil.randomString(75));

		_styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), styleBookEntryKey, _THEME_ID,
			_serviceContext);

		StyleBookEntry styleBookEntry = _getOrAddEmptyStyleBookEntry(
			styleBookEntryKey, _group.getGroupId(), _THEME_ID);

		Assert.assertEquals(
			styleBookEntryKey.substring(0, 73),
			styleBookEntry.getStyleBookEntryKey());
	}

	private void _testGetOrAddEmptyStyleBookEntryWithoutThemeId()
		throws Exception {

		StyleBookEntry styleBookEntry = _getOrAddEmptyStyleBookEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), null);

		Assert.assertEquals(
			"classic_WAR_classictheme", styleBookEntry.getThemeId());

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_DESIGN_LIBRARY, _serviceContext);

		styleBookEntry = _getOrAddEmptyStyleBookEntry(
			RandomTestUtil.randomString(), depotEntry.getGroupId(), null);

		Assert.assertEquals(
			"classic_WAR_classictheme", styleBookEntry.getThemeId());
	}

	private void _testPublishDraft(StyleBookEntry styleBookEntry)
		throws Exception {

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		draftStyleBookEntry.setName(RandomTestUtil.randomString());

		draftStyleBookEntry = _styleBookEntryLocalService.updateDraft(
			draftStyleBookEntry);

		draftStyleBookEntry = _styleBookEntryLocalService.getStyleBookEntry(
			draftStyleBookEntry.getStyleBookEntryId());

		styleBookEntry = _styleBookEntryLocalService.publishDraft(
			draftStyleBookEntry);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, styleBookEntry.getStatus());
	}

	private void _testUpdateStyleBookEntryWithApprovedStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null, _THEME_ID,
				_serviceContext);

		styleBookEntry = _updateStyleBookEntry(
			styleBookEntry, RandomTestUtil.randomString());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, styleBookEntry.getStatus());
		Assert.assertEquals(_THEME_ID, styleBookEntry.getThemeId());
	}

	private void _testUpdateStyleBookEntryWithEmptyStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry = _getOrAddEmptyStyleBookEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), _THEME_ID);

		_styleBookEntryLocalService.getDraft(styleBookEntry);

		String frontendTokenName = RandomTestUtil.randomString();
		String name = styleBookEntry.getName();
		String styleBookEntryKey = styleBookEntry.getStyleBookEntryKey();
		String themeId = RandomTestUtil.randomString();

		styleBookEntry = _styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntry.getStyleBookEntryId(),
			false, styleBookEntry.getFrontendTokenDefinition(),
			JSONUtil.put(
				frontendTokenName,
				JSONUtil.put("value", RandomTestUtil.randomString())
			).toString(),
			RandomTestUtil.randomString(), null,
			styleBookEntry.getPreviewFileEntryId(), themeId, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, styleBookEntry.getStatus());
		Assert.assertEquals(themeId, styleBookEntry.getThemeId());

		JSONObject frontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				styleBookEntry.getFrontendTokensValues());

		Assert.assertTrue(
			frontendTokensValuesJSONObject.has(
				themeId + StringPool.COLON + frontendTokenName));

		Assert.assertNull(
			_styleBookEntryLocalService.fetchDraft(styleBookEntry));

		StyleBookEntry newStyleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null, name, null, _THEME_ID,
				_serviceContext);

		Assert.assertEquals(
			styleBookEntryKey, newStyleBookEntry.getStyleBookEntryKey());
	}

	private void _testUpdateStyleBookEntryWithEmptyStyleBookEntryAndWithoutThemeId()
		throws Exception {

		StyleBookEntry styleBookEntry = _getOrAddEmptyStyleBookEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), _THEME_ID);

		try {
			_updateStyleBookEntry(styleBookEntry, null);

			Assert.fail();
		}
		catch (StyleBookEntryThemeIdException.MustNotBeNull
					styleBookEntryThemeIdException) {
		}
	}

	private StyleBookEntry _updateStyleBookEntry(
			StyleBookEntry styleBookEntry, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntry.getStyleBookEntryId(),
			false, styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(), styleBookEntry.getName(),
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), themeId, _serviceContext);
	}

	private static final String _THEME_ID = RandomTestUtil.randomString();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}