/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.change.tracking.spi.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.comparator.TemplateVersionVersionComparator;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class DDMTemplateVersionConstraintResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testResolveConflict() throws Exception {
		Group group = GroupTestUtil.addGroup();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		DDMTemplateVersion ddmTemplateVersion1 =
			ddmTemplate.getTemplateVersion();

		ddmTemplateVersion1.setVersion("1.10");

		ddmTemplateVersion1 =
			_ddmTemplateVersionLocalService.updateDDMTemplateVersion(
				ddmTemplateVersion1);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_ddmTemplateLocalService.updateTemplate(
				ddmTemplate.getUserId(), ddmTemplate.getTemplateId(),
				ddmTemplate.getClassPK(), ddmTemplate.getNameMap(),
				ddmTemplate.getDescriptionMap(), ddmTemplate.getType(),
				ddmTemplate.getMode(), ddmTemplate.getLanguage(),
				ddmTemplate.getScript(), ddmTemplate.isCacheable(),
				serviceContext);

			_ddmTemplateLocalService.updateTemplate(
				ddmTemplate.getUserId(), ddmTemplate.getTemplateId(),
				ddmTemplate.getClassPK(), ddmTemplate.getNameMap(),
				ddmTemplate.getDescriptionMap(), ddmTemplate.getType(),
				ddmTemplate.getMode(), ddmTemplate.getLanguage(),
				ddmTemplate.getScript(), ddmTemplate.isCacheable(),
				serviceContext);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_ddmTemplateLocalService.updateTemplate(
				ddmTemplate.getUserId(), ddmTemplate.getTemplateId(),
				ddmTemplate.getClassPK(), ddmTemplate.getNameMap(),
				ddmTemplate.getDescriptionMap(), ddmTemplate.getType(),
				ddmTemplate.getMode(), ddmTemplate.getLanguage(),
				ddmTemplate.getScript(), ddmTemplate.isCacheable(),
				serviceContext);

			_ddmTemplateLocalService.updateTemplate(
				ddmTemplate.getUserId(), ddmTemplate.getTemplateId(),
				ddmTemplate.getClassPK(), ddmTemplate.getNameMap(),
				ddmTemplate.getDescriptionMap(), ddmTemplate.getType(),
				ddmTemplate.getMode(), ddmTemplate.getLanguage(),
				ddmTemplate.getScript(), ddmTemplate.isCacheable(),
				serviceContext);
		}

		Map<Long, List<ConflictInfo>> conflictsMap =
			_ctCollectionLocalService.checkConflicts(ctCollection);

		List<ConflictInfo> conflictInfos = conflictsMap.get(
			_classNameLocalService.getClassNameId(DDMTemplateVersion.class));

		for (ConflictInfo conflictInfo : conflictInfos) {
			Assert.assertTrue(conflictInfo.isResolved());
		}

		_ctCollectionService.publishCTCollection(
			TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

		String version = "1.14";

		List<DDMTemplateVersion> ddmTemplateVersions = ListUtil.sort(
			_ddmTemplateVersionLocalService.getTemplateVersions(
				ddmTemplateVersion1.getTemplateId()),
			new TemplateVersionVersionComparator(false));

		for (DDMTemplateVersion ddmTemplateVersion2 : ddmTemplateVersions) {
			int[] versionParts = StringUtil.split(
				ddmTemplateVersion2.getVersion(), StringPool.PERIOD, 0);

			Assert.assertEquals(version, ddmTemplateVersion2.getVersion());

			version = versionParts[0] + StringPool.PERIOD + --versionParts[1];
		}
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private DDMTemplateVersionLocalService _ddmTemplateVersionLocalService;

}