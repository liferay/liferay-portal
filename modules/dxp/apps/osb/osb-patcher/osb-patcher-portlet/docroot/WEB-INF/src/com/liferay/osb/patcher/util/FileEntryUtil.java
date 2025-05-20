/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Repository;
import com.liferay.portal.model.User;
import com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;

import java.io.File;

import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class FileEntryUtil {

	public static FileEntry addFileEntry(
			long userId, long classNameId, long classPK, String portletId,
			File file)
		throws Exception {

		long groupId = 0;

		User user = UserLocalServiceUtil.getUser(userId);

		if (user.isDefaultUser()) {
			Group group = GroupLocalServiceUtil.getGroup(
				user.getCompanyId(), GroupConstants.GUEST);

			groupId = group.getGroupId();
		}
		else {
			groupId = user.getGroupId();
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			groupId, portletId, serviceContext);

		serviceContext.setAttribute(
			"className", PortalUtil.getClassName(classNameId));
		serviceContext.setAttribute("classPK", String.valueOf(classPK));

		String fileName = System.currentTimeMillis() + file.getName();

		String contentType = MimeTypesUtil.getContentType(fileName);

		return DLAppLocalServiceUtil.addFileEntry(
			userId, repository.getRepositoryId(), 0L, fileName, contentType,
			fileName, file.getName(), StringPool.BLANK, file, serviceContext);
	}

	public static String getURL(long patcherBuildId) throws Exception {
		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		Property classPKProperty = PropertyFactoryUtil.forName("classPK");

		OrderByComparator createDateOBC = OrderByComparatorFactoryUtil.create(
			PatcherFixModelImpl.TABLE_NAME, "createDate", false);

		DynamicQuery patcherBuildDynamicQuery =
			PatcherBuildLocalServiceUtil.dynamicQuery();

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.getPatcherBuild(patcherBuildId);

		Property keyProperty = PropertyFactoryUtil.forName("key");

		patcherBuildDynamicQuery.add(keyProperty.eq(patcherBuild.getKey()));

		Property keyVersionProperty = PropertyFactoryUtil.forName("keyVersion");

		patcherBuildDynamicQuery.add(
			keyVersionProperty.le(patcherBuild.getKeyVersion()));

		OrderByComparator keyVersionOBC = OrderByComparatorFactoryUtil.create(
			PatcherBuildModelImpl.TABLE_NAME, "keyVersion", false);

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.dynamicQuery(
				patcherBuildDynamicQuery, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				keyVersionOBC);

		for (PatcherBuild curPatcherBuild : patcherBuilds) {
			DynamicQuery dlFileEntryDynamicQuery =
				DLFileEntryLocalServiceUtil.dynamicQuery();

			dlFileEntryDynamicQuery.add(
				classNameIdProperty.eq(
					PortalUtil.getClassNameId(PatcherBuild.class)));

			dlFileEntryDynamicQuery.add(
				classPKProperty.eq(curPatcherBuild.getPatcherBuildId()));

			List<DLFileEntry> dlFileEntries =
				DLFileEntryLocalServiceUtil.dynamicQuery(
					dlFileEntryDynamicQuery, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, createDateOBC);

			if (dlFileEntries.isEmpty()) {
				continue;
			}

			DLFileEntry dlFileEntry = dlFileEntries.get(0);

			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
				dlFileEntry.getFileEntryId());

			return DLUtil.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK);
		}

		return StringPool.BLANK;
	}

}