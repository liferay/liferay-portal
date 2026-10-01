/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test.util;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

/**
 * @author Mariano Alvaro
 */
public class DisplayPageTestUtil {

	public static LayoutPageTemplateEntry addDisplayPageTemplate(
			long groupId, String className, String classTypeKey)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				groupId, PortalUtil.getClassNameId(className), classTypeKey);

		Layout layout = LayoutLocalServiceUtil.getLayout(
			layoutPageTemplateEntry.getPlid());

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("title", JSONUtil.put("mappedField", "title"))
			).toString(),
			StringPool.BLANK, StringPool.BLANK, null, null,
			"<div data-lfr-editable-id=\"title\" " +
				"data-lfr-editable-type=\"text\">Placeholder</div>",
			StringPool.BLANK, layout, null,
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		return layoutPageTemplateEntry;
	}

}