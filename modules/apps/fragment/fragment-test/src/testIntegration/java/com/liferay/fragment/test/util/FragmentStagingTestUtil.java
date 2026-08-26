/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.test.util;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.util.Map;

/**
 * @author Kyle Miho
 */
public class FragmentStagingTestUtil {

	public static Group enableLocalStaging(Group group) throws PortalException {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		addStagingAttribute(
			serviceContext, PortletDataHandlerKeys.DATA_STRATEGY_MIRROR, true);
		addStagingAttribute(
			serviceContext, PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			true);
		addStagingAttribute(
			serviceContext, PortletDataHandlerKeys.PORTLET_DATA_ALL, true);
		addStagingAttribute(
			serviceContext, PortletDataHandlerKeys.PORTLET_SETUP_ALL, true);

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false, serviceContext);

		return group.getStagingGroup();
	}

	public static void publishLayouts(Group stagingGroup, Group liveGroup)
		throws PortalException {

		Map<String, String[]> parameters =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			liveGroup.getGroupId(), false, parameters);
	}

	public static void publishLayoutsRangeFromLastPublishedDate(
			Group stagingGroup, Group liveGroup)
		throws PortalException {

		ExportImportTestUtil.publishLayoutsRangeFromLastPublishedDate(
			stagingGroup, liveGroup);
	}

	protected static void addStagingAttribute(
		ServiceContext serviceContext, String key, Object value) {

		serviceContext.setAttribute(
			StagingConstants.STAGED_PREFIX + key + StringPool.DOUBLE_DASH,
			String.valueOf(value));
	}

}