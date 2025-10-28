/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.test.util;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.Date;

/**
 * @author Pavel Savinov
 */
public class FragmentTestUtil {

	public static FragmentCollection addFragmentCollection(long groupId)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return FragmentCollectionLocalServiceUtil.addFragmentCollection(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	public static FragmentCollection addFragmentCollection(
			long groupId, String name)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return FragmentCollectionLocalServiceUtil.addFragmentCollection(
			null, TestPropsValues.getUserId(), groupId, name, StringPool.BLANK,
			serviceContext);
	}

	public static FragmentCollection addFragmentCollection(
			long groupId, String name, Date createDate)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCreateDate(createDate);
		serviceContext.setModifiedDate(createDate);

		return FragmentCollectionLocalServiceUtil.addFragmentCollection(
			null, TestPropsValues.getUserId(), groupId, name, StringPool.BLANK,
			serviceContext);
	}

	public static FragmentCollection addFragmentCollection(
			long groupId, String name, String fragmentCollectionKey)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return FragmentCollectionLocalServiceUtil.addFragmentCollection(
			null, TestPropsValues.getUserId(), groupId, fragmentCollectionKey,
			name, StringPool.BLANK, false, serviceContext);
	}

	public static FragmentEntryLink addFragmentEntryLink(
			FragmentEntry fragmentEntry, long plid)
		throws PortalException {

		long defaultSegmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				plid);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				fragmentEntry.getGroupId());

		return FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			null, fragmentEntry.getExternalReferenceCode(), null,
			defaultSegmentsExperienceId, plid, fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(),
			fragmentEntry.getConfiguration(), StringPool.BLANK,
			StringPool.BLANK, 1, StringPool.BLANK, fragmentEntry.getType(),
			serviceContext);
	}

	public static FragmentEntryLink addFragmentEntryLink(
			long groupId, long fragmentEntryId, long plid)
		throws PortalException {

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.getFragmentEntry(fragmentEntryId);

		long defaultSegmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				plid);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), groupId, null,
			fragmentEntry.getExternalReferenceCode(), null,
			defaultSegmentsExperienceId, plid, fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(),
			fragmentEntry.getConfiguration(), StringPool.BLANK,
			StringPool.BLANK, 1, StringPool.BLANK, fragmentEntry.getType(),
			serviceContext);
	}

	public static FragmentEntryLink fetchFragmentEntryLink(
		String uuid, long groupId) {

		return FragmentEntryLinkLocalServiceUtil.
			fetchFragmentEntryLinkByUuidAndGroupId(uuid, groupId);
	}

}