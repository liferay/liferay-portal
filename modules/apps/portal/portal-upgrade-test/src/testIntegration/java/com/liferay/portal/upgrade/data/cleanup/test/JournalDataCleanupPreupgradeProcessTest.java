/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.JournalDataCleanupPreupgradeProcess;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class JournalDataCleanupPreupgradeProcessTest
	extends JournalDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		if (_group != null) {
			_groupLocalService.deleteGroup(_group);
		}

		for (ClassName className :
				_classNameLocalService.getClassNames(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (!_classNames.contains(className)) {
				_classNameLocalService.deleteClassName(className);
			}
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		JournalFeed journalFeed = JournalTestUtil.addFeed(
			_group.getGroupId(), layout.getPlid(),
			RandomTestUtil.randomString(), journalArticle.getDDMStructureId(),
			journalArticle.getDDMTemplateKey(),
			journalArticle.getDDMTemplateKey());

		runSQL(
			"delete from JournalArticle where articleId = '" +
				journalArticle.getArticleId() + "'");
		runSQL(
			"delete from JournalFeed where feedId = '" +
				journalFeed.getFeedId() + "'");

		upgrade();

		_ddmTemplateLocalService.deleteTemplate(
			journalArticle.getDDMTemplate());

		_deleteDDMStructure(journalArticle);

		String originalName = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			_layoutLocalService.deleteLayout(layout);
		}
		finally {
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	public void testUpgradeJournalArticleResourcePermissionScopeCheck()
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), "Owner");

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), JournalArticle.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		runSQL(
			"delete from JournalArticle where articleId = '" +
				journalArticle.getArticleId() + "'");

		upgrade();

		CacheRegistryUtil.clear();

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()),
				role.getRoleId(), ActionKeys.VIEW));

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_group.getGroupId()), role.getRoleId(),
				ActionKeys.VIEW));

		_ddmTemplateLocalService.deleteTemplate(
			journalArticle.getDDMTemplate());

		_deleteDDMStructure(journalArticle);
	}

	private void _deleteDDMStructure(JournalArticle journalArticle)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getStructureVersion();

		_ddmFieldLocalService.deleteDDMFields(
			ddmStructureVersion.getStructureId());

		_ddmStructureLocalService.deleteStructure(ddmStructure);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private List<ClassName> _classNames;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}