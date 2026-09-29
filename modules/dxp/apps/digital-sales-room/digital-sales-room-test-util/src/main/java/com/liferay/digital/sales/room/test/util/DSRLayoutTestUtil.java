/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.test.util;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;

/**
 * @author Stefano Motta
 */
public class DSRLayoutTestUtil {

	public static void assertFragmentEntryLink(long groupId) {
		boolean found = false;

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(
					groupId, "digital-sales-room-master");

		for (FragmentEntryLink fragmentEntryLink :
				FragmentEntryLinkLocalServiceUtil.getFragmentEntryLinksByPlid(
					groupId, layoutPageTemplateEntry.getPlid())) {

			if (!Objects.equals(
					fragmentEntryLink.getRendererKey(), _RENDERER_KEY)) {

				continue;
			}

			found = true;

			JSONObject jsonObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			jsonObject = jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			String source = jsonObject.getString("source");

			Assert.assertTrue(
				Validator.isNull(source) || !source.contains("privateLayout"));
		}

		Assert.assertTrue(found);
	}

	public static void assertLayouts(
			long groupId, String[] names, boolean privateLayout)
		throws Exception {

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			groupId, privateLayout);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				TransformUtil.transformToArray(
					layouts,
					layout -> layout.getName(LocaleUtil.getSiteDefault()),
					String.class),
				names));

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), "Guest");

		for (Layout layout : layouts) {
			boolean hasResourcePermission =
				ResourcePermissionLocalServiceUtil.hasResourcePermission(
					layout.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()), role.getRoleId(),
					ActionKeys.VIEW);

			if (Objects.equals(
					layout.getName(LocaleUtil.getSiteDefault()), "Documents") ||
				Objects.equals(
					layout.getName(LocaleUtil.getSiteDefault()),
					"Onboarding")) {

				Assert.assertFalse(hasResourcePermission);
			}
			else {
				Assert.assertTrue(hasResourcePermission);
			}
		}
	}

	private static final String _RENDERER_KEY =
		"com.liferay.fragment.renderer.menu.display.internal." +
			"MenuDisplayFragmentRenderer";

}