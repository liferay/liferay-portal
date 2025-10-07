/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CTProcessLocalServicePerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_ctCollectionLocalService.deleteCTCollection(_ctCollection);

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testAddCTProcessDDMForm() throws Exception {
		String formFieldName = RandomTestUtil.randomString();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			DDMFormTestUtil.addTextDDMFormFields(ddmForm, formFieldName);

			DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(ddmForm);

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(ddmForm);

		DDMFormTestUtil.addTextDDMFormFields(ddmForm, formFieldName);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctProcessLocalService.addCTProcess(
				_ctCollection.getUserId(), _ctCollection.getCtCollectionId());
		}
	}

	@Test
	public void testAddCTProcessJournalArticle() throws Exception {
		JournalFolder journalFolder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), journalFolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());

		JournalArticle ctJournalArticle = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			ctJournalArticle = JournalTestUtil.updateArticle(journalArticle);
		}

		Assert.assertNotEquals(
			journalArticle.getTitle(), ctJournalArticle.getTitle());

		journalArticle = JournalTestUtil.updateArticle(journalArticle);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctProcessLocalService.addCTProcess(
				_ctCollection.getUserId(), _ctCollection.getCtCollectionId());
		}

		journalArticle = _journalArticleLocalService.getLatestArticle(
			journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getTitle(), ctJournalArticle.getTitle());
	}

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Portal _portal;

}