/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.DesignLibraryTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class SelectAssetDisplayPageDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetDefaultAssetDisplayPageName() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertNull(_getDefaultAssetDisplayPageName(journalArticle));

		long classNameId = _portal.getClassNameId(JournalArticle.class);
		Group designLibraryGroup = DesignLibraryTestUtil.addDesignLibraryGroup(
			_group.getGroupId(), true);

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), classNameId,
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			designLibraryLayoutPageTemplateEntry.getName(),
			_getDefaultAssetDisplayPageName(journalArticle));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId,
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			_getDefaultAssetDisplayPageName(journalArticle));
	}

	private String _getDefaultAssetDisplayPageName(
			JournalArticle journalArticle)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:classNameId",
			_portal.getClassNameId(JournalArticle.class));
		mockHttpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:classTypeId",
			journalArticle.getDDMStructureId());
		mockHttpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:groupId",
			_group.getGroupId());

		Bundle bundle = FrameworkUtil.getBundle(
			SelectAssetDisplayPageDisplayContextTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.asset.taglib");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.asset.taglib.internal.display.context." +
				"SelectAssetDisplayPageDisplayContext");

		Constructor<?> constructor = clazz.getConstructor(
			HttpServletRequest.class, LiferayPortletRequest.class,
			LiferayPortletResponse.class);

		Object selectAssetDisplayPageDisplayContext = constructor.newInstance(
			mockHttpServletRequest, null,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			selectAssetDisplayPageDisplayContext,
			"getDefaultAssetDisplayPageName", new Class<?>[0]);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}