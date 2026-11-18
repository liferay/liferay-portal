/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.helper;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.helper.DropZoneAllowedFragmentEntriesHelper;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DropZoneAllowedFragmentEntriesHelper.class)
public class DropZoneAllowedFragmentEntriesHelperImpl
	implements DropZoneAllowedFragmentEntriesHelper {

	@Override
	public List<String> getFragmentEntryKeys(
		long companyId, DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		long scopeGroupId) {

		return JSONUtil.toList(
			dropZoneLayoutStructureItem.getFragmentEntriesJSONArray(),
			jsonObject -> {
				String fragmentEntryRendererKey = jsonObject.getString(
					"fragmentEntryRendererKey");

				if (Validator.isNotNull(fragmentEntryRendererKey)) {
					return fragmentEntryRendererKey;
				}

				String fragmentEntryERC = jsonObject.getString(
					"fragmentEntryERC");

				if (Validator.isNull(fragmentEntryERC)) {
					return null;
				}

				FragmentEntry fragmentEntry = _fetchFragmentEntry(
					companyId, fragmentEntryERC,
					jsonObject.getString("fragmentEntryScopeERC"),
					scopeGroupId);

				if (fragmentEntry == null) {
					return null;
				}

				return fragmentEntry.getFragmentEntryKey();
			},
			_log);
	}

	@Override
	public boolean isAllowedFragmentEntryKey(
		long companyId, DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		String fragmentEntryKey, long scopeGroupId) {

		if (dropZoneLayoutStructureItem == null) {
			return true;
		}

		List<String> fragmentEntryKeys = getFragmentEntryKeys(
			companyId, dropZoneLayoutStructureItem, scopeGroupId);

		boolean contains = fragmentEntryKeys.contains(fragmentEntryKey);

		if (dropZoneLayoutStructureItem.isAllowNewFragmentEntries()) {
			return !contains;
		}

		return contains;
	}

	@Override
	public JSONObject normalizeItemConfigJSONObject(
			long companyId, JSONObject itemConfigJSONObject, long scopeGroupId)
		throws PortalException {

		if (!itemConfigJSONObject.has("fragmentEntryKeys")) {
			return itemConfigJSONObject;
		}

		List<String> fragmentEntryKeys = JSONUtil.toStringList(
			itemConfigJSONObject.getJSONArray("fragmentEntryKeys"));

		itemConfigJSONObject.remove("fragmentEntryKeys");

		long[] groupIds = _getGroupIds(companyId, scopeGroupId);

		return itemConfigJSONObject.put(
			"fragmentEntries",
			JSONUtil.toJSONArray(
				fragmentEntryKeys,
				fragmentEntryKey -> {
					FragmentEntry defaultFragmentEntry =
						_fragmentCollectionContributorRegistry.getFragmentEntry(
							fragmentEntryKey);
					FragmentRenderer fragmentRenderer =
						_fragmentRendererRegistry.getFragmentRenderer(
							fragmentEntryKey);

					if ((defaultFragmentEntry != null) ||
						(fragmentRenderer != null)) {

						return JSONUtil.put(
							"fragmentEntryRendererKey", fragmentEntryKey);
					}

					FragmentEntry fragmentEntry = _fetchFragmentEntry(
						fragmentEntryKey, groupIds);

					if (fragmentEntry == null) {
						return null;
					}

					return JSONUtil.put(
						"fragmentEntryERC",
						fragmentEntry.getExternalReferenceCode()
					).put(
						"fragmentEntryScopeERC",
						ScopeUtil.getItemScopeExternalReferenceCode(
							fragmentEntry.getGroupId(), scopeGroupId)
					);
				},
				_log));
	}

	private FragmentEntry _fetchFragmentEntry(
		long companyId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long scopeGroupId) {

		Long fragmentEntryScopeGroupId = ScopeUtil.getItemGroupId(
			companyId, fragmentEntryScopeERC, scopeGroupId);

		if (fragmentEntryScopeGroupId == null) {
			return null;
		}

		return _fragmentEntryLocalService.
			fetchFragmentEntryByExternalReferenceCode(
				fragmentEntryERC, fragmentEntryScopeGroupId);
	}

	private FragmentEntry _fetchFragmentEntry(
		String fragmentEntryKey, long[] groupIds) {

		for (long groupId : groupIds) {
			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.fetchFragmentEntry(
					groupId, fragmentEntryKey);

			if (fragmentEntry == null) {
				continue;
			}

			return fragmentEntry;
		}

		return null;
	}

	private long[] _getGroupIds(long companyId, long scopeGroupId)
		throws PortalException {

		Group companyGroup = _groupLocalService.getCompanyGroup(companyId);

		return ArrayUtil.append(
			new long[] {scopeGroupId, companyGroup.getGroupId()},
			DesignLibraryUtil.fetchConnectedDesignLibraryGroupIds(
				scopeGroupId));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DropZoneAllowedFragmentEntriesHelperImpl.class);

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

}