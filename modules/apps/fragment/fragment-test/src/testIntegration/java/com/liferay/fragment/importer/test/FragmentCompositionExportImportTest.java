/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentExportImportConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
public class FragmentCompositionExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.fragment.service"));

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());
	}

	@Test
	public void testFragmentCompositionExportFormat() throws Exception {
		FragmentComposition fragmentComposition =
			_fragmentCompositionLocalService.addFragmentComposition(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomId(), StringUtil.randomId(),
				StringUtil.randomId(), StringPool.BLANK, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext());

		ZipWriter zipWriter = new ZipWriter();

		fragmentComposition.populateZipWriter(zipWriter, "test");

		try (ZipReader zipReader = new ZipReader(zipWriter.getFile())) {
			for (String entry : zipReader.getEntries()) {
				if (!StringUtil.endsWith(
						entry,
						FragmentExportImportConstants.
							FILE_NAME_FRAGMENT_COMPOSITION)) {

					continue;
				}

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					zipReader.getEntryAsString(entry));

				Assert.assertNotNull(jsonObject);
				Assert.assertEquals(
					jsonObject.getString("name"),
					fragmentComposition.getName());
				Assert.assertEquals(
					jsonObject.getString("description"),
					fragmentComposition.getDescription());
				Assert.assertEquals(
					"fragment-composition-definition.json",
					jsonObject.getString("fragmentCompositionDefinitionPath"));
			}
		}

		FileUtil.delete(zipWriter.getFile());
	}

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@DeleteAfterTestRun
	private Group _group;

}