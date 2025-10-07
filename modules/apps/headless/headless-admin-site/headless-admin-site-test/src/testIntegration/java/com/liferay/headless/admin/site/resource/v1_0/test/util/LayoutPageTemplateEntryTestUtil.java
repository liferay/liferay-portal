/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutPageTemplateEntryTestUtil {

	public static LayoutPageTemplateEntry getBasicLayoutPageTemplateEntry(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionLocalServiceUtil.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(),
					serviceContext.getScopeGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		return LayoutPageTemplateEntryLocalServiceUtil.
			addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);
	}

	public static Layout getBasicLayoutPageTemplateEntryLayout(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getBasicLayoutPageTemplateEntry(serviceContext);

		return LayoutLocalServiceUtil.getLayout(
			layoutPageTemplateEntry.getPlid());
	}

	public static LayoutPageTemplateEntry getDisplayPageLayoutPageTemplateEntry(
			ServiceContext serviceContext)
		throws Exception {

		return LayoutPageTemplateEntryLocalServiceUtil.
			addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null,
				PortalUtil.getClassNameId(
					"com.liferay.asset.kernel.model.AssetCategory"),
				0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);
	}

	public static Layout getDisplayPageLayoutPageTemplateEntryLayout(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getDisplayPageLayoutPageTemplateEntry(serviceContext);

		return LayoutLocalServiceUtil.getLayout(
			layoutPageTemplateEntry.getPlid());
	}

	public static LayoutPageTemplateEntry getMasterLayoutPageTemplateEntry(
			ServiceContext serviceContext, int status)
		throws Exception {

		return LayoutPageTemplateEntryLocalServiceUtil.
			addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0, status,
				serviceContext);
	}

	public static Layout getMasterLayoutPageTemplateEntryLayout(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT);

		return LayoutLocalServiceUtil.getLayout(
			layoutPageTemplateEntry.getPlid());
	}

	public static LayoutPageTemplateEntry getWidgetPageLayoutPageTemplateEntry(
			ServiceContext serviceContext)
		throws Exception {

		long layoutPageTemplateCollectionId =
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;

		Group group = GroupLocalServiceUtil.getGroup(
			serviceContext.getScopeGroupId());

		if (!group.isCompany()) {
			LayoutPageTemplateCollection layoutPageTemplateCollection =
				LayoutPageTemplateCollectionLocalServiceUtil.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(),
						serviceContext.getScopeGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			layoutPageTemplateCollectionId =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();
		}

		return LayoutPageTemplateEntryLocalServiceUtil.
			addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(),
				layoutPageTemplateCollectionId, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

}