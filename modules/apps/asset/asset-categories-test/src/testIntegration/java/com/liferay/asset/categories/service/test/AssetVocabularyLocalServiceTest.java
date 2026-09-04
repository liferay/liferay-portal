/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.exception.SystemVocabularyException;
import com.liferay.asset.kernel.exception.VocabularyExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.VocabularyVisibilityTypeException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class AssetVocabularyLocalServiceTest {

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
	@TestInfo("LPD-88752")
	public void testAddVocabulary() throws Exception {
		_testAddVocabularyWithInvalidVisibilityType();
		_testAddVocabularyWithLongExternalReferenceCode();
	}

	@Test
	public void testDeleteVocabulary() throws Exception {
		_testDeleteVocabularySystem();
		_testDeleteVocabularySystemWhenImporting();
	}

	@Test
	public void testGetGroupsVocabularies() throws Exception {
		AssetTestUtil.addVocabulary(_group.getGroupId());

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(new long[0]);

		Assert.assertTrue(
			assetVocabularies.toString(), assetVocabularies.isEmpty());
	}

	@Test
	public void testGetGroupVocabularies() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupVocabularies(new long[0]);

		Assert.assertTrue(
			assetVocabularies.toString(), assetVocabularies.isEmpty());

		assetVocabularies = _assetVocabularyLocalService.getGroupVocabularies(
			new long[0], new int[] {assetVocabulary.getVisibilityType()});

		Assert.assertTrue(
			assetVocabularies.toString(), assetVocabularies.isEmpty());
	}

	@Test
	public void testGetGroupVocabulariesCount() throws Exception {
		AssetTestUtil.addVocabulary(_group.getGroupId());

		Assert.assertEquals(
			0,
			_assetVocabularyLocalService.getGroupVocabulariesCount(
				new long[0]));
	}

	@Test
	public void testGetOrAddEmptyVocabulary() throws Exception {
		_testGetOrAddEmptyVocabularyWithLazyReferencingDisabled();
		_testGetOrAddEmptyVocabularyWithLazyReferencingEnabled();
	}

	@Test
	public void testUpdateVocabulary() throws Exception {
		_testUpdateVocabularySystemAssetTypes();
		_testUpdateVocabularySystemDescription();
		_testUpdateVocabularySystemMultiValued();
		_testUpdateVocabularySystemRename();
		_testUpdateVocabularySystemVisibilityType();
		_testUpdateVocabularySystemWhenImporting();
		_testUpdateVocabularySystemWithNullDescription();
		_testUpdateVocabularyWithInvalidVisibilityType();
		_testUpdateVocabularyWithLazyReferencingEnabled();
	}

	private AssetVocabulary _addSystemVocabulary() throws Exception {
		return _addSystemVocabulary(_group.getGroupId());
	}

	private AssetVocabulary _addSystemVocabulary(long groupId)
		throws Exception {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setMultiValued(true);
		assetVocabularySettingsHelper.setSystem(true);

		return _assetVocabularyLocalService.addVocabulary(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), null,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			null, assetVocabularySettingsHelper.toString(),
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _testAddVocabularyWithInvalidVisibilityType()
		throws Exception {

		AssertUtils.assertFailure(
			VocabularyVisibilityTypeException.class, null,
			() -> _assetVocabularyLocalService.addVocabulary(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null, new HashMap<>(), null,
				null, 3, ServiceContextTestUtil.getServiceContext()));
	}

	private void _testAddVocabularyWithLongExternalReferenceCode()
		throws Exception {

		int maxLength = ModelHintsUtil.getMaxLength(
			AssetVocabulary.class.getName(), "externalReferenceCode");

		AssertUtils.assertFailure(
			VocabularyExternalReferenceCodeException.class,
			StringBundler.concat(
				"External reference code length cannot exceed ", maxLength,
				" characters"),
			() -> _assetVocabularyLocalService.addVocabulary(
				StringUtil.randomString(maxLength + 1),
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null, new HashMap<>(), null,
				null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				ServiceContextTestUtil.getServiceContext()));
	}

	private void _testDeleteVocabularySystem() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		AssertUtils.assertFailure(
			SystemVocabularyException.MustNotDelete.class,
			StringBundler.concat(
				"Vocabulary ", vocabulary.getVocabularyId(),
				" cannot be deleted"),
			() -> _assetVocabularyLocalService.deleteVocabulary(
				vocabulary.getVocabularyId()));
	}

	private void _testDeleteVocabularySystemWhenImporting() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		ExportImportThreadLocal.setPortletImportInProcess(true);

		try {
			_assetVocabularyLocalService.deleteVocabulary(
				vocabulary.getVocabularyId());
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}

		Assert.assertNull(
			_assetVocabularyLocalService.fetchAssetVocabulary(
				vocabulary.getVocabularyId()));
	}

	private void _testGetOrAddEmptyVocabularyWithLazyReferencingDisabled()
		throws Exception {

		try {
			_assetVocabularyLocalService.getOrAddEmptyVocabulary(
				StringUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchVocabularyException noSuchVocabularyException) {
			Assert.assertNotNull(noSuchVocabularyException);
		}
	}

	private void _testGetOrAddEmptyVocabularyWithLazyReferencingEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AssetVocabulary vocabulary =
				_assetVocabularyLocalService.getOrAddEmptyVocabulary(
					StringUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, vocabulary.getStatus());
		}
	}

	private void _testUpdateVocabularySystemAssetTypes() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper(vocabulary.getSettings());

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			new long[] {RandomTestUtil.randomLong()},
			new long[] {RandomTestUtil.randomLong()},
			new boolean[] {RandomTestUtil.randomBoolean()});

		AssertUtils.assertFailure(
			SystemVocabularyException.MustNotModify.class,
			StringBundler.concat(
				"Vocabulary ", vocabulary.getVocabularyId(),
				" cannot be modified"),
			() -> _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getExternalReferenceCode(),
				vocabulary.getVocabularyId(), vocabulary.getTitleMap(),
				vocabulary.getDescriptionMap(),
				assetVocabularySettingsHelper.toString(),
				vocabulary.getVisibilityType()));
	}

	private void _testUpdateVocabularySystemDescription() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		AssertUtils.assertFailure(
			SystemVocabularyException.MustNotModify.class,
			StringBundler.concat(
				"Vocabulary ", vocabulary.getVocabularyId(),
				" cannot be modified"),
			() -> _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getExternalReferenceCode(),
				vocabulary.getVocabularyId(), vocabulary.getTitleMap(),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				vocabulary.getSettings(), vocabulary.getVisibilityType()));
	}

	private void _testUpdateVocabularySystemMultiValued() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper(vocabulary.getSettings());

		assetVocabularySettingsHelper.setMultiValued(false);

		vocabulary = _assetVocabularyLocalService.updateVocabulary(
			vocabulary.getExternalReferenceCode(), vocabulary.getVocabularyId(),
			vocabulary.getTitleMap(), vocabulary.getDescriptionMap(),
			assetVocabularySettingsHelper.toString(),
			vocabulary.getVisibilityType());

		Assert.assertFalse(vocabulary.isMultiValued());
		Assert.assertTrue(vocabulary.isSystem());
	}

	private void _testUpdateVocabularySystemRename() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		AssertUtils.assertFailure(
			SystemVocabularyException.MustNotRename.class,
			StringBundler.concat(
				"Vocabulary ", vocabulary.getVocabularyId(),
				" cannot be renamed"),
			() -> _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getExternalReferenceCode(),
				vocabulary.getVocabularyId(),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				vocabulary.getDescriptionMap(), vocabulary.getSettings(),
				vocabulary.getVisibilityType()));
	}

	private void _testUpdateVocabularySystemVisibilityType() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		AssertUtils.assertFailure(
			SystemVocabularyException.MustNotModify.class,
			StringBundler.concat(
				"Vocabulary ", vocabulary.getVocabularyId(),
				" cannot be modified"),
			() -> _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getExternalReferenceCode(),
				vocabulary.getVocabularyId(), vocabulary.getTitleMap(),
				vocabulary.getDescriptionMap(), vocabulary.getSettings(),
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL));
	}

	private void _testUpdateVocabularySystemWhenImporting() throws Exception {
		AssetVocabulary vocabulary = _addSystemVocabulary();

		ExportImportThreadLocal.setPortletImportInProcess(true);

		try {
			vocabulary = _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getExternalReferenceCode(),
				vocabulary.getVocabularyId(),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				vocabulary.getDescriptionMap(), vocabulary.getSettings(),
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}

		Assert.assertTrue(vocabulary.isSystem());
		Assert.assertEquals(
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
			vocabulary.getVisibilityType());
	}

	private void _testUpdateVocabularySystemWithNullDescription()
		throws Exception {

		AssetVocabulary vocabulary = _addSystemVocabulary();

		_assetVocabularyLocalService.updateVocabulary(
			vocabulary.getExternalReferenceCode(), vocabulary.getVocabularyId(),
			vocabulary.getTitleMap(), null, vocabulary.getSettings(),
			vocabulary.getVisibilityType());
	}

	private void _testUpdateVocabularyWithInvalidVisibilityType()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssertUtils.assertFailure(
			VocabularyVisibilityTypeException.class, null,
			() -> _assetVocabularyLocalService.updateVocabulary(
				assetVocabulary.getExternalReferenceCode(),
				assetVocabulary.getVocabularyId(), null, null, null, 3));
		AssertUtils.assertFailure(
			VocabularyVisibilityTypeException.class, null,
			() -> _assetVocabularyLocalService.updateVocabulary(
				assetVocabulary.getExternalReferenceCode(),
				assetVocabulary.getVocabularyId(),
				RandomTestUtil.randomString(), null, null, null, 3,
				ServiceContextTestUtil.getServiceContext()));
	}

	private void _testUpdateVocabularyWithLazyReferencingEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AssetVocabulary vocabulary =
				_assetVocabularyLocalService.getOrAddEmptyVocabulary(
					StringUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, vocabulary.getStatus());

			String title = RandomTestUtil.randomString();

			vocabulary = _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getExternalReferenceCode(),
				vocabulary.getVocabularyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), title
				).build(),
				null, vocabulary.getSettings(),
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

			Assert.assertEquals(
				StringUtil.toLowerCase(title), vocabulary.getName());
			Assert.assertEquals(
				title, vocabulary.getTitle(LocaleUtil.getDefault()));
			Assert.assertEquals(
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
				vocabulary.getVisibilityType());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, vocabulary.getStatus());
		}
	}

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

}