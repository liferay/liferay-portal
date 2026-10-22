/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.instance.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Attila Bakay
 */
@RunWith(Arquillian.class)
public class
	AddDefaultAssetVocabulariesInitialRequestPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testPortalInstanceRegistered() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

		long[] selectedClassNameIds = assetVocabulary.getSelectedClassNameIds();

		long classNameId = selectedClassNameIds[0];

		Set<Long> classNameIds = SetUtil.fromArray(selectedClassNameIds);

		classNameIds.remove(classNameId);

		_updateVocabulary(assetVocabulary, ArrayUtil.toLongArray(classNameIds));

		try {
			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			assetVocabulary = _assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

			Assert.assertFalse(
				ArrayUtil.contains(
					assetVocabulary.getSelectedClassNameIds(), classNameId));
		}
		finally {
			_updateVocabulary(assetVocabulary, selectedClassNameIds);
		}
	}

	@Test
	public void testPortalInstanceRegisteredAppendsUnregisteredAssetTypes()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

		String settings = assetVocabulary.getSettings();

		try {
			long[] selectedClassNameIds =
				assetVocabulary.getSelectedClassNameIds();

			long classNameId = selectedClassNameIds[0];

			AssetVocabularySettingsHelper assetVocabularySettingsHelper =
				new AssetVocabularySettingsHelper();

			assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
				new long[] {classNameId},
				new long[] {AssetCategoryConstants.ALL_CLASS_TYPE_PK},
				new boolean[] {false}, new boolean[] {true});

			_updateVocabulary(
				assetVocabulary, assetVocabularySettingsHelper.toString());

			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			assetVocabulary = _assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

			assetVocabularySettingsHelper = new AssetVocabularySettingsHelper(
				assetVocabulary.getSettings());

			Assert.assertTrue(
				assetVocabularySettingsHelper.
					isClassNameIdAndClassTypePKRequired(
						classNameId, AssetCategoryConstants.ALL_CLASS_TYPE_PK));

			for (long selectedClassNameId : selectedClassNameIds) {
				Assert.assertTrue(
					ArrayUtil.contains(
						assetVocabulary.getSelectedClassNameIds(),
						selectedClassNameId));

				if (selectedClassNameId == classNameId) {
					continue;
				}

				Assert.assertFalse(
					assetVocabularySettingsHelper.
						isClassNameIdAndClassTypePKRequired(
							selectedClassNameId,
							AssetCategoryConstants.ALL_CLASS_TYPE_PK));
			}
		}
		finally {
			_updateVocabulary(assetVocabulary, settings);
		}
	}

	@Test
	public void testPortalInstanceRegisteredPreservesAssetTypeSettings()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

		String settings = assetVocabulary.getSettings();

		try {
			long classNameId = assetVocabulary.getSelectedClassNameIds()[0];
			long classTypePK = RandomTestUtil.randomLong();

			_updateVocabulary(
				assetVocabulary, new long[] {classNameId},
				new long[] {classTypePK}, new boolean[] {true},
				new boolean[] {true});

			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			assetVocabulary = _assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

			Assert.assertTrue(
				ArrayUtil.contains(
					assetVocabulary.getSelectedClassTypePKs(), classTypePK));

			AssetVocabularySettingsHelper assetVocabularySettingsHelper =
				new AssetVocabularySettingsHelper(
					assetVocabulary.getSettings());

			Assert.assertTrue(
				assetVocabularySettingsHelper.
					isClassNameIdAndClassTypePKDepotRequired(
						classNameId, classTypePK));
			Assert.assertTrue(
				assetVocabularySettingsHelper.
					isClassNameIdAndClassTypePKRequired(
						classNameId, classTypePK));
		}
		finally {
			_updateVocabulary(assetVocabulary, settings);
		}
	}

	@Test
	public void testPortalInstanceRegisteredPreservesRequiredAssetTypeWithAllAssetTypesSelected()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

		String settings = assetVocabulary.getSettings();

		try {
			long classNameId = assetVocabulary.getSelectedClassNameIds()[0];

			_updateVocabulary(
				assetVocabulary,
				new long[] {
					AssetCategoryConstants.ALL_CLASS_NAME_ID, classNameId
				},
				new long[] {
					AssetCategoryConstants.ALL_CLASS_TYPE_PK,
					AssetCategoryConstants.ALL_CLASS_TYPE_PK
				},
				new boolean[] {false, false}, new boolean[] {false, true});

			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			assetVocabulary = _assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience");

			long[] selectedClassNameIds =
				assetVocabulary.getSelectedClassNameIds();

			Assert.assertTrue(
				ArrayUtil.contains(
					selectedClassNameIds,
					AssetCategoryConstants.ALL_CLASS_NAME_ID));
			Assert.assertTrue(
				ArrayUtil.contains(selectedClassNameIds, classNameId));

			AssetVocabularySettingsHelper assetVocabularySettingsHelper =
				new AssetVocabularySettingsHelper(
					assetVocabulary.getSettings());

			Assert.assertTrue(
				assetVocabularySettingsHelper.
					isClassNameIdAndClassTypePKRequired(
						classNameId, AssetCategoryConstants.ALL_CLASS_TYPE_PK));
		}
		finally {
			_updateVocabulary(assetVocabulary, settings);
		}
	}

	@Test
	public void testPortalInstanceRegisteredWithDLFileEntryVocabularies()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		AssetVocabulary globalAssetVocabulary = _addInternalAssetVocabulary(
			company.getGroupId());

		try {
			_group = GroupTestUtil.addGroup();

			AssetVocabulary groupAssetVocabulary = _addInternalAssetVocabulary(
				_group.getGroupId());

			List<AssetVocabulary> assetVocabularies =
				_assetVocabularyLocalService.getGroupsVocabularies(
					new long[] {company.getGroupId(), _group.getGroupId()},
					DLFileEntry.class.getName());

			for (String name : new String[] {"audience", "stage", "topic"}) {
				Assert.assertTrue(
					assetVocabularies.contains(
						_assetVocabularyLocalService.fetchGroupVocabulary(
							company.getGroupId(), name)));
			}

			Assert.assertTrue(
				assetVocabularies.contains(globalAssetVocabulary));
			Assert.assertTrue(assetVocabularies.contains(groupAssetVocabulary));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(
				globalAssetVocabulary);
		}
	}

	private AssetVocabulary _addInternalAssetVocabulary(long groupId)
		throws Exception {

		return _assetVocabularyLocalService.addVocabulary(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			new AssetVocabularySettingsHelper(
			).toString(),
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private void _updateVocabulary(
			AssetVocabulary assetVocabulary, long[] classNameIds)
		throws Exception {

		long[] classTypePKs = new long[classNameIds.length];

		Arrays.fill(classTypePKs, AssetCategoryConstants.ALL_CLASS_TYPE_PK);

		boolean[] depotRequireds = new boolean[classNameIds.length];

		Arrays.fill(depotRequireds, false);

		boolean[] requireds = new boolean[classNameIds.length];

		Arrays.fill(requireds, false);

		_updateVocabulary(
			assetVocabulary, classNameIds, classTypePKs, depotRequireds,
			requireds);
	}

	private void _updateVocabulary(
			AssetVocabulary assetVocabulary, long[] classNameIds,
			long[] classTypePKs, boolean[] depotRequireds, boolean[] requireds)
		throws Exception {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper(assetVocabulary.getSettings());

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			classNameIds, classTypePKs, depotRequireds, requireds);

		_updateVocabulary(
			assetVocabulary, assetVocabularySettingsHelper.toString());
	}

	private void _updateVocabulary(
			AssetVocabulary assetVocabulary, String settings)
		throws Exception {

		_assetVocabularyLocalService.updateVocabulary(
			assetVocabulary.getExternalReferenceCode(),
			assetVocabulary.getVocabularyId(), assetVocabulary.getTitleMap(),
			assetVocabulary.getDescriptionMap(), settings,
			assetVocabulary.getVisibilityType());
	}

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.web.internal.instance.lifecycle.AddDefaultAssetVocabulariesInitialRequestPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

}