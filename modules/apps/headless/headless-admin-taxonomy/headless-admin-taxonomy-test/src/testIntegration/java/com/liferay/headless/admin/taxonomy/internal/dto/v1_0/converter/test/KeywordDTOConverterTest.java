/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Project;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.Arrays;
import java.util.Collections;

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
public class KeywordDTOConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_depotEntryGroup = _depotEntry.getGroup();

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testToDTO() throws Exception {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		_assetTagGroupRelLocalService.setAssetTagGroupRels(
			assetTag.getTagId(), new long[] {_depotEntryGroup.getGroupId()},
			DepotConstants.TYPE_SPACE);
		_assetTagGroupRelLocalService.setAssetTagGroupRels(
			assetTag.getTagId(),
			new long[] {GroupConstants.ANY_PARENT_GROUP_ID},
			DepotConstants.TYPE_PROJECT);

		Keyword keyword = _toDTO(assetTag);

		AssetLibrary[] assetLibraries = keyword.getAssetLibraries();

		Assert.assertEquals(
			Arrays.toString(assetLibraries), 1, assetLibraries.length);

		AssetLibrary assetLibrary = assetLibraries[0];

		Assert.assertEquals(
			_depotEntryGroup.getGroupId(), (long)assetLibrary.getId());
		Assert.assertEquals(
			_depotEntryGroup.getExternalReferenceCode(),
			assetLibrary.getExternalReferenceCode());
	}

	@FeatureFlag("LPD-99403")
	@Test
	public void testToDTOWithProjects() throws Exception {
		_projectDepotEntry = _addDepotEntry(DepotConstants.TYPE_PROJECT);

		Group projectDepotEntryGroup = _projectDepotEntry.getGroup();

		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		_assetTagGroupRelLocalService.setAssetTagGroupRels(
			assetTag.getTagId(),
			new long[] {projectDepotEntryGroup.getGroupId()},
			DepotConstants.TYPE_PROJECT);

		Keyword keyword = _toDTO(assetTag);

		AssetLibrary[] assetLibraries = keyword.getAssetLibraries();

		Assert.assertEquals(
			Arrays.toString(assetLibraries), 0, assetLibraries.length);

		Project[] projects = keyword.getProjects();

		Assert.assertEquals(Arrays.toString(projects), 1, projects.length);

		Project project = projects[0];

		Assert.assertEquals(
			projectDepotEntryGroup.getGroupId(), (long)project.getId());
		Assert.assertEquals(
			projectDepotEntryGroup.getExternalReferenceCode(),
			project.getExternalReferenceCode());
	}

	private DepotEntry _addDepotEntry(int depotEntryType) throws Exception {
		return DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, depotEntryType,
			new ServiceContext() {
				{
					setCompanyId(TestPropsValues.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
	}

	private Keyword _toDTO(AssetTag assetTag) throws Exception {
		DTOConverter<AssetTag, Keyword> dtoConverter =
			(DTOConverter<AssetTag, Keyword>)
				_dtoConverterRegistry.getDTOConverter(AssetTag.class.getName());

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, TestPropsValues.getUserId(),
				LocaleUtil.getDefault(), null, null);

		return dtoConverter.toDTO(dtoConverterContext, assetTag);
	}

	@Inject
	private AssetTagGroupRelLocalService _assetTagGroupRelLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	private Group _depotEntryGroup;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private DepotEntry _projectDepotEntry;

	private ServiceContext _serviceContext;

}