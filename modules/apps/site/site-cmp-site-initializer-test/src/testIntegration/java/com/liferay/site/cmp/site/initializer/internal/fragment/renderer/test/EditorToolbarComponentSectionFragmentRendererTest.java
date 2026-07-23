/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class EditorToolbarComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProps() throws Exception {
		Assert.assertEquals(
			"/redirect-url", MapUtil.getString(getProps(), "backURL"));
		Assert.assertEquals(
			StringBundler.concat(
				themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/e/project/",
				PortalUtil.getClassNameId(
					cmpProjectObjectDefinition.getClassName()),
				StringPool.SLASH, cmpProjectObjectEntry.getObjectEntryId()),
			MapUtil.getString(getProps(), "formSubmitURL"));
		Assert.assertEquals(
			"New Project", MapUtil.getString(getProps(), "title"));

		mockHttpServletRequest.setParameter(
			"action", "createProjectGlobalTask");

		Assert.assertEquals(
			"/redirect-url", MapUtil.getString(getProps(), "backURL"));
		Assert.assertEquals(
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_task?objectDefinitionId=",
				cmpTaskObjectDefinition.getObjectDefinitionId(), "&plid=",
				themeDisplay.getPlid(), "&projectGroupId=",
				cmpProjectObjectEntry.getGroupId(), "&projectId=",
				cmpProjectObjectEntry.getObjectEntryId(),
				"&redirect=/redirect-url&action=createGlobalTask"),
			MapUtil.getString(getProps(), "formSubmitURL"));
		Assert.assertEquals(
			"New Project", MapUtil.getString(getProps(), "title"));

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpProjectObjectDefinition,
			_partialUpdateObjectEntry(cmpProjectObjectEntry));

		Assert.assertEquals(
			"Edit Project", MapUtil.getString(getProps(), "title"));

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition, cmpTaskObjectEntry);

		Assert.assertEquals(
			"/redirect-url", MapUtil.getString(getProps(), "backURL"));
		Assert.assertEquals("", MapUtil.getString(getProps(), "formSubmitURL"));
		Assert.assertEquals("New Task", MapUtil.getString(getProps(), "title"));

		mockHttpServletRequest.setParameter("action", "createGlobalTask");

		Assert.assertEquals(
			"/redirect-url", MapUtil.getString(getProps(), "backURL"));
		Assert.assertEquals(
			"/redirect-url", MapUtil.getString(getProps(), "formSubmitURL"));
		Assert.assertEquals("New Task", MapUtil.getString(getProps(), "title"));

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition,
			_partialUpdateObjectEntry(cmpTaskObjectEntry));

		Assert.assertEquals(
			"Edit Task", MapUtil.getString(getProps(), "title"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	private ObjectEntry _partialUpdateObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.EditorToolbarComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}