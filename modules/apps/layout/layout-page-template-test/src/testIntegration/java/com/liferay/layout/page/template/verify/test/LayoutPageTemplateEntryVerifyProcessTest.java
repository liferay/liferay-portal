/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateEntryVerifyProcessTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo({"LPD-81587", "LPD-103532"})
	public void testUpdateClassTypeKey() throws Exception {
		_testUpdateClassTypeKey(null);
		_testUpdateClassTypeKey(StringPool.BLANK);
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return _verifyProcess;
	}

	private void _testUpdateClassTypeKey(String classTypeKey) throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		layoutPageTemplateEntry.setClassTypeId(ddmStructure.getStructureId());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		if (classTypeKey != null) {
			_updateClassTypeKey(
				classTypeKey,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
		}

		Assert.assertTrue(
			Validator.isNull(layoutPageTemplateEntry.getClassTypeKey()));

		doVerify();

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			ddmStructure.getStructureId(),
			layoutPageTemplateEntry.getClassTypeId());
		Assert.assertEquals(
			ddmStructure.getStructureKey(),
			layoutPageTemplateEntry.getClassTypeKey());
	}

	private void _updateClassTypeKey(
			String classTypeKey, long layoutPageTemplateEntryId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update LayoutPageTemplateEntry set classTypeKey = ? where " +
					"layoutPageTemplateEntryId = ?")) {

			preparedStatement.setString(1, classTypeKey);
			preparedStatement.setLong(2, layoutPageTemplateEntryId);

			preparedStatement.executeUpdate();
		}
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.layout.page.template.internal.verify.LayoutPageTemplateEntryVerifyProcess"
	)
	private VerifyProcess _verifyProcess;

}