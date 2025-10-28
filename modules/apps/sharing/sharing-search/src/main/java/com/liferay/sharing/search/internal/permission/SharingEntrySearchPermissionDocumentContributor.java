/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.search.internal.permission;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFieldContributor;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.model.SharingEntryTable;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Indexes a new field in the search document to include all users the resource
 * has been shared with. This information is used to do permission checks when
 * returning search results via {@link
 * SharingEntrySearchPermissionFilterContributor}.
 *
 * <p>
 * Each time a resource is shared, the associated search document is reindexed
 * and this {@code SearchPermissionFieldContributor} ensures that the user the
 * resource is shared with is added to the search field.
 * </p>
 *
 * @author Sergio González
 */
@Component(service = SearchPermissionFieldContributor.class)
public class SharingEntrySearchPermissionDocumentContributor
	implements SearchPermissionFieldContributor {

	@Override
	public void contribute(Document document, String className, long classPK) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext != null) && serviceContext.isStrictAdd()) {
			return;
		}

		List<Object[]> sharingEntryObjectsList = _lookupSharingEntryObjectsList(
			_classNameLocalService.getClassNameId(className), classPK);

		if (ListUtil.isEmpty(sharingEntryObjectsList)) {
			return;
		}

		document.addKeyword(
			"sharedToUserGroupId",
			TransformUtil.transformToLongArray(
				sharingEntryObjectsList,
				sharingEntryObjects -> {
					long toUserGroupId = (long)sharingEntryObjects[0];

					if (toUserGroupId == 0) {
						return null;
					}

					return toUserGroupId;
				}));
		document.addKeyword(
			"sharedToUserId",
			TransformUtil.transformToLongArray(
				sharingEntryObjectsList,
				sharingEntryObjects -> {
					long toUserId = (long)sharingEntryObjects[1];

					if (toUserId == 0) {
						return null;
					}

					return toUserId;
				}));
	}

	private List<Object[]> _lookupSharingEntryObjectsList(
		long classNameId, long classPK) {

		Map<Long, Map<Long, List<Object[]>>> sharingEntryObjectsListsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> _sharingEntryLocalService.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						SharingEntryTable.INSTANCE
					),
					false),
				SharingEntrySearchPermissionDocumentContributor.class.getName(),
				count -> {
					Map<Long, Map<Long, List<Object[]>>>
						localSharingEntryObjectsListsMap = new HashMap<>();

					if (count == 0) {
						return localSharingEntryObjectsListsMap;
					}

					DSLQuery dslQuery = DSLQueryFactoryUtil.select(
						SharingEntryTable.INSTANCE.classNameId,
						SharingEntryTable.INSTANCE.classPK,
						SharingEntryTable.INSTANCE.toUserGroupId,
						SharingEntryTable.INSTANCE.toUserId
					).from(
						SharingEntryTable.INSTANCE
					);

					for (Object[] values :
							(List<Object[]>)_sharingEntryLocalService.dslQuery(
								dslQuery, false)) {

						Map<Long, List<Object[]>> sharingEntryObjectsLists =
							localSharingEntryObjectsListsMap.computeIfAbsent(
								(Long)values[0], key -> new HashMap<>());

						List<Object[]> sharingEntryObjectsList =
							sharingEntryObjectsLists.computeIfAbsent(
								(Long)values[1], key -> new ArrayList<>());

						sharingEntryObjectsList.add(
							new Object[] {values[2], values[3]});
					}

					return localSharingEntryObjectsListsMap;
				});

		if (sharingEntryObjectsListsMap == null) {
			List<SharingEntry> sharingEntries =
				_sharingEntryLocalService.getSharingEntries(
					classNameId, classPK);

			if (sharingEntries.isEmpty()) {
				return null;
			}

			List<Object[]> sharingEntryObjectsList = new ArrayList<>(
				sharingEntries.size());

			for (SharingEntry sharingEntry : sharingEntries) {
				sharingEntryObjectsList.add(
					new Object[] {
						sharingEntry.getToUserGroupId(),
						sharingEntry.getToUserId()
					});
			}

			return sharingEntryObjectsList;
		}

		Map<Long, List<Object[]>> sharingEntryObjectsLists =
			sharingEntryObjectsListsMap.get(classNameId);

		if (sharingEntryObjectsLists == null) {
			return null;
		}

		return sharingEntryObjectsLists.get(classPK);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

}