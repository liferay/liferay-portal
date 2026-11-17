/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.exportimport.test.util.lar.BaseCompanyStagedModelDataHandlerTestCase;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
@Sync
public class StagedExpandoColumnStagedModelDataHandlerTest
	extends BaseCompanyStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_stagedModelRepository =
			(StagedModelRepository<StagedExpandoColumn>)
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					StagedExpandoColumn.class.getName());
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.addTable(
			group.getCompanyId(), ExpandoTable.class.getName(),
			RandomTestUtil.randomString());

		ExpandoColumn expandoColumn = ExpandoColumnLocalServiceUtil.addColumn(
			expandoTable.getTableId(), RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		return ModelAdapterUtil.adapt(
			expandoColumn, ExpandoColumn.class, StagedExpandoColumn.class);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		List<StagedExpandoColumn> stagedExpandoColumns =
			_stagedModelRepository.fetchStagedModelsByUuidAndCompanyId(
				uuid, group.getCompanyId());

		if (ListUtil.isNotEmpty(stagedExpandoColumns)) {
			return stagedExpandoColumns.get(0);
		}

		throw new PortalException(
			StringBundler.concat(
				"Unable to find StagedExpandoColumn with uuid: ", uuid,
				", companyId: ", group.getCompanyId()));
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return StagedExpandoColumn.class;
	}

	@Override
	protected boolean supportLastPublishDateUpdate() {
		return false;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		StagedExpandoColumn stagedExpandoColumn =
			(StagedExpandoColumn)stagedModel;
		StagedExpandoColumn importedStagedExpandoColumn =
			(StagedExpandoColumn)importedStagedModel;

		Assert.assertEquals(
			stagedExpandoColumn.getCompanyId(),
			importedStagedExpandoColumn.getCompanyId());
		Assert.assertEquals(
			StringUtil.trim(stagedExpandoColumn.getDefaultData()),
			StringUtil.trim(importedStagedExpandoColumn.getDefaultData()));
		Assert.assertEquals(
			stagedExpandoColumn.getName(),
			importedStagedExpandoColumn.getName());
		Assert.assertEquals(
			stagedExpandoColumn.getType(),
			importedStagedExpandoColumn.getType());
		Assert.assertEquals(
			stagedExpandoColumn.getTypeSettingsProperties(),
			importedStagedExpandoColumn.getTypeSettingsProperties());
		Assert.assertEquals(
			stagedExpandoColumn.getUuid(),
			importedStagedExpandoColumn.getUuid());
	}

	private StagedModelRepository<StagedExpandoColumn> _stagedModelRepository;

}