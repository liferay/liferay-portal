/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ViewSpaceMembersSummarySectionDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetHeaderProps() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, _depotEntry);

		Group group = _depotEntry.getGroup();

		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"apiURL",
				StringBundler.concat(
					"/o/headless-asset-library/v1.0/asset-libraries/",
					group.getExternalReferenceCode(),
					"/user-accounts?page=1&pageSize=8&nestedFields=roles")
			).put(
				"creationMenu", StringPool.BLANK
			).put(
				"label", "View All Members"
			).put(
				"permissions",
				HashMapBuilder.<String, Object>put(
					"hasAssignMembersPermission", true
				).build()
			).put(
				"spaceModalProps",
				HashMapBuilder.<String, Object>put(
					"action", "open-members-modal"
				).put(
					"assetLibraryCreatorUserId", TestPropsValues.getUserId()
				).put(
					"externalReferenceCode", group.getExternalReferenceCode()
				).build()
			).put(
				"title", "Members (1)"
			).put(
				"url", StringPool.BLANK
			).build(),
			ReflectionTestUtil.invoke(
				_getSectionDisplayContext(), "getHeaderProps",
				new Class<?>[0]));
	}

	private Object _getSectionDisplayContext() throws Exception {
		_fragmentRenderer.render(
			fragmentRendererContext, mockHttpServletRequest,
			new MockHttpServletResponse());

		return mockHttpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewSpaceMembersSummarySectionDisplayContext");
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewSpaceMembersSummaryJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}