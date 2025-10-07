/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.test.util;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Kyle Miho
 */
public class DisplayPageTemplateTestUtil {

	public static LayoutPageTemplateEntry addDisplayPageTemplate(long groupId)
		throws PortalException {

		return addDisplayPageTemplate(groupId, RandomTestUtil.randomString());
	}

	public static LayoutPageTemplateEntry addDisplayPageTemplate(
			long groupId, long classNameId, long classTypeId)
		throws PortalException {

		return addDisplayPageTemplate(
			groupId, classNameId, classTypeId, false,
			WorkflowConstants.STATUS_APPROVED);
	}

	public static LayoutPageTemplateEntry addDisplayPageTemplate(
			long groupId, long classNameId, long classTypeId,
			boolean defaultTemplate, int status)
		throws PortalException {

		return addDisplayPageTemplate(
			groupId, classNameId, classTypeId, defaultTemplate, null, status);
	}

	public static LayoutPageTemplateEntry addDisplayPageTemplate(
			long groupId, long classNameId, long classTypeId,
			boolean defaultTemplate, String layoutPageTemplateEntryKey,
			int status)
		throws PortalException {

		return LayoutPageTemplateEntryLocalServiceUtil.
			addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), groupId, 0,
				layoutPageTemplateEntryKey, classNameId, classTypeId,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				defaultTemplate, 0, 0, 0, status,
				ServiceContextTestUtil.getServiceContext(
					groupId, TestPropsValues.getUserId()));
	}

	public static LayoutPageTemplateEntry addDisplayPageTemplate(
			long groupId, String name)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return LayoutPageTemplateEntryLocalServiceUtil.
			addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), groupId, 0, null, name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);
	}

}