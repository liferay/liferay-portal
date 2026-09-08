/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.fragment.contributor.util.FragmentCollectionContributorRegistryUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.admin.site.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.scope.Scope;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.util.structure.DropZoneLayoutStructureItem"
	},
	service = DTOConverter.class
)
public class DropZonePageElementDefinitionDTOConverter
	implements DTOConverter
		<DropZoneLayoutStructureItem, DropZonePageElementDefinition> {

	@Override
	public String getContentType() {
		return DropZonePageElementDefinition.class.getSimpleName();
	}

	@Override
	public DropZonePageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			DropZoneLayoutStructureItem dropZoneLayoutStructureItem)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		DropZonePageElementDefinition dropZonePageElementDefinition =
			new DropZonePageElementDefinition();

		dropZonePageElementDefinition.setAddNewFragmentEntries(
			dropZoneLayoutStructureItem::isAllowNewFragmentEntries);
		dropZonePageElementDefinition.setAllowedFragmentReferences(
			() -> _getFragmentReferences(
				companyId, dropZoneLayoutStructureItem, scopeGroupId));
		dropZonePageElementDefinition.setType(
			PageElementDefinition.Type.DROP_ZONE);

		return dropZonePageElementDefinition;
	}

	private FragmentReference[] _getFragmentReferences(
			List<String> fragmentEntryKeys, long scopeGroupId)
		throws Exception {

		List<FragmentReference> fragmentReferences = new ArrayList<>();

		for (String fragmentEntryKey : fragmentEntryKeys) {
			FragmentEntry fragmentEntry =
				FragmentCollectionContributorRegistryUtil.getFragmentEntry(
					fragmentEntryKey);

			if (fragmentEntry != null) {
				fragmentReferences.add(
					_toDefaultFragmentReference(fragmentEntryKey));
			}
			else {
				fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
					scopeGroupId, fragmentEntryKey);

				if (fragmentEntry != null) {
					fragmentReferences.add(
						_toFragmentItemExternalReference(
							fragmentEntry.getExternalReferenceCode(),
							ItemScopeUtil.getItemScope(
								fragmentEntry.getGroupId(), scopeGroupId)));
				}
			}
		}

		return fragmentReferences.toArray(new FragmentReference[0]);
	}

	private FragmentReference[] _getFragmentReferences(
			long companyId,
			DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
			long scopeGroupId)
		throws Exception {

		if (dropZoneLayoutStructureItem.getFragmentEntryKeys() != null) {
			return _getFragmentReferences(
				dropZoneLayoutStructureItem.getFragmentEntryKeys(),
				scopeGroupId);
		}
		else if (dropZoneLayoutStructureItem.getFragmentEntriesJSONArray() !=
					null) {

			return _getFragmentReferences(
				companyId,
				dropZoneLayoutStructureItem.getFragmentEntriesJSONArray(),
				scopeGroupId);
		}

		return null;
	}

	private FragmentReference[] _getFragmentReferences(
		long companyId, JSONArray fragmentEntriesJSONArray, long scopeGroupId) {

		List<FragmentReference> fragmentReferences = new ArrayList<>();

		for (int i = 0; i < fragmentEntriesJSONArray.length(); i++) {
			JSONObject jsonObject = fragmentEntriesJSONArray.getJSONObject(i);

			if (jsonObject.has("fragmentEntryERC")) {
				fragmentReferences.add(
					_toFragmentItemExternalReference(
						jsonObject.getString("fragmentEntryERC"),
						ItemScopeUtil.getItemScope(
							companyId,
							jsonObject.getString("fragmentEntryScopeERC"),
							scopeGroupId)));
			}
			else if (jsonObject.has("fragmentEntryRendererKey")) {
				fragmentReferences.add(
					_toDefaultFragmentReference(
						jsonObject.getString("fragmentEntryRendererKey")));
			}
		}

		return fragmentReferences.toArray(new FragmentReference[0]);
	}

	private DefaultFragmentReference _toDefaultFragmentReference(
		String defaultFragmentKey) {

		DefaultFragmentReference defaultFragmentReference =
			new DefaultFragmentReference();

		defaultFragmentReference.setDefaultFragmentKey(
			() -> defaultFragmentKey);
		defaultFragmentReference.setFragmentReferenceType(
			() ->
				FragmentReference.FragmentReferenceType.
					DEFAULT_FRAGMENT_REFERENCE);

		return defaultFragmentReference;
	}

	private FragmentItemExternalReference _toFragmentItemExternalReference(
		String externalReferenceCode, Scope scope) {

		FragmentItemExternalReference fragmentItemExternalReference =
			new FragmentItemExternalReference();

		fragmentItemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		fragmentItemExternalReference.setFragmentReferenceType(
			() ->
				FragmentReference.FragmentReferenceType.
					FRAGMENT_ITEM_EXTERNAL_REFERENCE);

		fragmentItemExternalReference.setScope(() -> scope);

		return fragmentItemExternalReference;
	}

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

}