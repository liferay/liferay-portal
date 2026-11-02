/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadFlagLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class MBThreadFlagStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		MBMessage message = MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		addDependentStagedModel(
			dependentStagedModelsMap, MBMessage.class, message);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBMessage.class.getSimpleName());

		MBMessage message = (MBMessage)dependentStagedModels.get(0);

		return MBThreadFlagLocalServiceUtil.addThreadFlag(
			TestPropsValues.getUserId(), message.getThread(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return MBThreadFlagLocalServiceUtil.getMBThreadFlagByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return MBThreadFlag.class;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBMessage.class.getSimpleName());

		Assert.assertEquals(
			dependentStagedModels.toString(), 1, dependentStagedModels.size());

		MBMessage message = (MBMessage)dependentStagedModels.get(0);

		MBMessageLocalServiceUtil.getMBMessageByUuidAndGroupId(
			message.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImport(
			StagedModel stagedModel, StagedModelAssets stagedModelAssets,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		validateImport(dependentStagedModelsMap, group);

		MBThreadFlag threadFlag = (MBThreadFlag)stagedModel;

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBMessage.class.getSimpleName());

		MBMessage message = (MBMessage)dependentStagedModels.get(0);

		Assert.assertTrue(
			MBThreadFlagLocalServiceUtil.hasThreadFlag(
				threadFlag.getUserId(), message.getThread()));
	}

}