/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class KBArticleLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, TestPropsValues.getUserId(),
			_classNameLocalService.getClassNameId(KBFolder.class),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				kbArticle.getGroupId(),
				new InfoItemReference(
					KBArticle.class.getName(),
					new ERCInfoItemIdentifier(
						kbArticle.getExternalReferenceCode())));

		Assert.assertEquals(
			kbArticle, layoutDisplayPageObjectProvider.getDisplayObject());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					KBArticle.class.getName(),
					new ERCInfoItemIdentifier(
						kbArticle.getExternalReferenceCode(),
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			kbArticle, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					KBArticle.class.getName(),
					new ERCInfoItemIdentifier(
						kbArticle.getExternalReferenceCode())));

		Assert.assertNull(layoutDisplayPageObjectProvider);

		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(), StringPool.BLANK));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject(
		filter = "component.name=com.liferay.knowledge.base.web.internal.layout.display.page.KBArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<FileEntry> _layoutDisplayPageProvider;

}