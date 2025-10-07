/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael Bowerman
 */
@RunWith(Arquillian.class)
public class LayoutClassedModelUsageStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_classNameLocalService.addClassName(_CLASS_NAME);
		_classNameLocalService.addClassName(_CLASS_NAME_CONTAINER_TYPE);

		_classPK = RandomTestUtil.nextLong();
		_containerKey = String.valueOf(RandomTestUtil.nextLong());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_classNameLocalService.deleteClassName(
			_classNameLocalService.getClassName(_CLASS_NAME));
		_classNameLocalService.deleteClassName(
			_classNameLocalService.getClassName(_CLASS_NAME_CONTAINER_TYPE));
	}

	@Test
	public void testCorrectContainerTypeWhenClassNamesDesynced()
		throws Exception {

		initExport();

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		initImport();

		StagedModel exportedStagedModel = readExportedStagedModel(stagedModel);

		_classNameLocalService.deleteClassName(
			_classNameLocalService.getClassName(_CLASS_NAME_CONTAINER_TYPE));

		_classNameLocalService.addClassName(_CLASS_NAME_CONTAINER_TYPE);

		Assert.assertNotNull(exportedStagedModel);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);

		LayoutClassedModelUsage importedLayoutClassedModelUsage =
			(LayoutClassedModelUsage)getStagedModel(
				stagedModel.getUuid(), liveGroup);

		Assert.assertEquals(
			_classNameLocalService.getClassNameId(_CLASS_NAME_CONTAINER_TYPE),
			importedLayoutClassedModelUsage.getContainerType());
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		addDependentStagedModel(dependentStagedModelsMap, Layout.class, layout);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> layoutDependentStagedModels =
			dependentStagedModelsMap.get(Layout.class.getSimpleName());

		Layout layout = (Layout)layoutDependentStagedModels.get(0);

		return _layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			group.getGroupId(), StringPool.BLANK,
			_classNameLocalService.getClassNameId(_CLASS_NAME), _classPK,
			_containerKey,
			_classNameLocalService.getClassNameId(_CLASS_NAME_CONTAINER_TYPE),
			layout.getPlid(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _layoutClassedModelUsageLocalService.
			getLayoutClassedModelUsageByUuidAndGroupId(
				uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return LayoutClassedModelUsage.class;
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final String _CLASS_NAME_CONTAINER_TYPE =
		RandomTestUtil.randomString();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private long _classPK;
	private String _containerKey;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

}