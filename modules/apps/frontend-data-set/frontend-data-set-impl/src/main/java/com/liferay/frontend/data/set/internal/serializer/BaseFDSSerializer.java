/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.internal.url.FDSAPIURLBuilder;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
public abstract class BaseFDSSerializer {

	public FDSAPIURLBuilder createFDSAPIURLBuilder(
		HttpServletRequest httpServletRequest, String restApplication,
		String restEndpoint, String restSchema) {

		return new FDSAPIURLBuilder(
			fdsAPIURLResolverRegistry, httpServletRequest, restApplication,
			restEndpoint, restSchema);
	}

	protected JSONArray serializeSnapshots(
			String fdsName, HttpServletRequest httpServletRequest,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryManagerRegistry objectEntryManagerRegistry)
		throws Exception {

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			List<ObjectEntry> ownedObjectEntries = new ArrayList<>();
			List<ObjectEntry> sharedObjectEntries = new ArrayList<>();

			long companyId = portal.getCompanyId(httpServletRequest);

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_DATA_SET_SNAPSHOT", companyId);

			ObjectEntryManager objectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getCompanyId(),
						objectDefinition.getStorageType()));

			long userId = portal.getUserId(httpServletRequest);

			Set<Long> sharingEntriesClassPKs = _getSharingEntriesClassPKs(
				classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				userId);

			Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
				companyId, objectDefinition, null, null,
				new DefaultDTOConverterContext(
					false, null, null, null, null,
					LocaleUtil.getMostRelevantLocale(), null, null),
				_getFilterString(fdsName, sharingEntriesClassPKs, userId), null,
				null, null);

			for (ObjectEntry objectEntry : page.getItems()) {
				if (sharingEntriesClassPKs.contains(objectEntry.getId())) {
					sharedObjectEntries.add(objectEntry);
				}
				else {
					ownedObjectEntries.add(objectEntry);
				}
			}

			JSONArray jsonArray = JSONUtil.putAll(
				JSONUtil.put(
					"headerVisible", false
				).put(
					"items", _toJSONArray(ownedObjectEntries)
				).put(
					"label",
					language.get(portal.getLocale(httpServletRequest), "owned")
				));

			if (!sharedObjectEntries.isEmpty()) {
				jsonArray.put(
					JSONUtil.put(
						"headerVisible", true
					).put(
						"items", _toJSONArray(sharedObjectEntries)
					).put(
						"label",
						language.get(
							portal.getLocale(httpServletRequest),
							"shared-with-me")
					));
			}

			return jsonArray;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to serialize snapshots", exception);
			}

			return JSONUtil.putAll();
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}
	}

	protected JSONObject serializeStartupSnapshot(
			String fdsName, HttpServletRequest httpServletRequest,
			ObjectDefinitionLocalService objectDefinitionLocalService)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_STARTUP_SNAPSHOT",
					portal.getCompanyId(httpServletRequest));

		if (objectDefinition == null) {
			return null;
		}

		User user = portal.getUser(httpServletRequest);

		if (user == null) {
			return null;
		}

		com.liferay.object.model.ObjectEntry dataSetStartupSnapshotObjectEntry =
			objectEntryLocalService.fetchObjectEntry(
				user.getExternalReferenceCode() + StringPool.UNDERLINE +
					fdsName,
				0, objectDefinition.getObjectDefinitionId());

		if (dataSetStartupSnapshotObjectEntry == null) {
			return null;
		}

		long dataSetSnapshotObjectEntryId = GetterUtil.getLong(
			dataSetStartupSnapshotObjectEntry.getValues(
			).get(
				"r_dataSetSnapshotToStartupSnapshots_l_dataSetSnapshotId"
			));

		com.liferay.object.model.ObjectEntry dataSetSnapshotObjectEntry =
			objectEntryLocalService.fetchObjectEntry(
				dataSetSnapshotObjectEntryId);

		if (dataSetSnapshotObjectEntry == null) {
			return null;
		}

		return _toJSONObject(dataSetSnapshotObjectEntry);
	}

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected FDSAPIURLResolverRegistry fdsAPIURLResolverRegistry;

	@Reference
	protected Language language;

	@Reference
	protected ObjectEntryLocalService objectEntryLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected SharingEntryLocalService sharingEntryLocalService;

	@Reference
	protected UserGroupLocalService userGroupLocalService;

	private String _getFilterString(
		String fdsName, Set<Long> sharingEntriesClassPKs, long userId) {

		StringBundler sb = new StringBundler(8);

		sb.append("(fdsName eq '");
		sb.append(fdsName);
		sb.append("' and (creatorId eq ");
		sb.append(userId);

		if (!sharingEntriesClassPKs.isEmpty()) {
			sb.append(" or id in ('");
			sb.append(StringUtil.merge(sharingEntriesClassPKs, "','"));
			sb.append("')");
		}

		sb.append("))");

		return sb.toString();
	}

	private Set<Long> _getSharingEntriesClassPKs(
		long classNameId, long userId) {

		Set<Long> sharingEntriesClassPKs = SetUtil.fromCollection(
			TransformUtil.transform(
				sharingEntryLocalService.getToUserSharingEntries(
					userId, classNameId),
				SharingEntry::getClassPK));

		for (UserGroup userGroup :
				userGroupLocalService.getUserUserGroups(userId)) {

			sharingEntriesClassPKs.addAll(
				TransformUtil.transform(
					sharingEntryLocalService.getToUserGroupSharingEntries(
						userGroup.getUserGroupId()),
					sharingEntry -> {
						if (sharingEntry.getClassNameId() != classNameId) {
							return null;
						}

						return sharingEntry.getClassPK();
					}));
		}

		return sharingEntriesClassPKs;
	}

	private JSONArray _toJSONArray(List<ObjectEntry> objectEntries)
		throws Exception {

		return JSONUtil.toJSONArray(
			objectEntries,
			(ObjectEntry objectEntry) -> {
				Map<String, Object> properties = objectEntry.getProperties();

				Object viewConfig = properties.get("viewConfig");

				if (Validator.isNull(viewConfig)) {
					return null;
				}

				return JSONUtil.put(
					"configuration", viewConfig
				).put(
					"erc", objectEntry.getExternalReferenceCode()
				).put(
					"id", objectEntry.getId()
				).put(
					"label", String.valueOf(properties.get("label"))
				);
			});
	}

	private JSONObject _toJSONObject(
		com.liferay.object.model.ObjectEntry objectEntry) {

		return JSONUtil.put("erc", objectEntry.getExternalReferenceCode());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFDSSerializer.class);

}