/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.helper;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.helper.DropZoneAllowedFragmentEntriesHelper;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
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

		JSONArray fragmentEntriesJSONArray =
			dropZoneLayoutStructureItem.getFragmentEntriesJSONArray();

		if (JSONUtil.isEmpty(fragmentEntriesJSONArray)) {
			return Collections.emptyList();
		}

		List<String> fragmentEntryKeys = new ArrayList<>();

		for (int i = 0; i < fragmentEntriesJSONArray.length(); i++) {
			JSONObject jsonObject = fragmentEntriesJSONArray.getJSONObject(i);

			String fragmentEntryRendererKey = jsonObject.getString(
				"fragmentEntryRendererKey");

			if (Validator.isNotNull(fragmentEntryRendererKey)) {
				fragmentEntryKeys.add(fragmentEntryRendererKey);

				continue;
			}

			String fragmentEntryERC = jsonObject.getString("fragmentEntryERC");

			if (Validator.isNull(fragmentEntryERC)) {
				continue;
			}

			FragmentEntry fragmentEntry = _fetchFragmentEntry(
				companyId, fragmentEntryERC,
				jsonObject.getString("fragmentEntryScopeERC"), scopeGroupId);

			if (fragmentEntry == null) {
				continue;
			}

			fragmentEntryKeys.add(fragmentEntry.getFragmentEntryKey());
		}

		return fragmentEntryKeys;
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

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

}