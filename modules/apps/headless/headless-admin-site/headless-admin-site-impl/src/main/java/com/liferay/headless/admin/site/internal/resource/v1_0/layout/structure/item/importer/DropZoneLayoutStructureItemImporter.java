/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.headless.admin.site.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEntryReference;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEntryReferenceUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class DropZoneLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement)
		throws Exception {

		PageElement[] pageElements = pageElement.getPageElements();

		if ((pageElements != null) && (pageElements.length > 1)) {
			String message =
				"A drop zone page element cannot have more than one child " +
					"page element";

			if (Validator.isNotNull(pageElement.getExternalReferenceCode())) {
				message = StringBundler.concat(
					"The drop zone page element with the external reference ",
					"code \"", pageElement.getExternalReferenceCode(),
					"\" cannot have more than one child page element");
			}

			throw new IllegalArgumentException(message);
		}

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)layoutStructure.getLayoutStructureItem(
				pageElement.getExternalReferenceCode());

		if (dropZoneLayoutStructureItem == null) {
			dropZoneLayoutStructureItem =
				(DropZoneLayoutStructureItem)
					layoutStructure.addDropZoneLayoutStructureItem(
						pageElement.getExternalReferenceCode(),
						LayoutStructureUtil.getParentExternalReferenceCode(
							pageElement, layoutStructure),
						pageElement.getPosition());
		}

		DropZonePageElementDefinition dropZonePageElementDefinition =
			(DropZonePageElementDefinition)
				pageElement.getPageElementDefinition();

		if (dropZonePageElementDefinition == null) {
			return dropZoneLayoutStructureItem;
		}

		dropZoneLayoutStructureItem.setAllowNewFragmentEntries(
			dropZonePageElementDefinition.getAddNewFragmentEntries());

		List<FragmentEntryReference> fragmentEntryReferences =
			_getFragmentEntryReference(
				dropZonePageElementDefinition,
				layoutStructureItemImporterContext);

		if (fragmentEntryReferences != null) {
			dropZoneLayoutStructureItem.setFragmentEntriesJSONArray(
				_toFragmentEntriesJSONArray(fragmentEntryReferences));
		}
		else {
			dropZoneLayoutStructureItem.setFragmentEntriesJSONArray(null);
		}

		return dropZoneLayoutStructureItem;
	}

	private List<FragmentEntryReference> _getFragmentEntryReference(
			DropZonePageElementDefinition dropZonePageElementDefinition,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		if (dropZonePageElementDefinition.getAllowedFragmentReferences() ==
				null) {

			return null;
		}

		List<FragmentEntryReference> fragmentEntryReferences =
			new ArrayList<>();

		for (FragmentReference fragmentReference :
				dropZonePageElementDefinition.getAllowedFragmentReferences()) {

			fragmentEntryReferences.add(
				FragmentEntryReferenceUtil.getFragmentEntryReference(
					layoutStructureItemImporterContext.getCompanyId(),
					fragmentReference,
					layoutStructureItemImporterContext.getGroupId()));
		}

		return fragmentEntryReferences;
	}

	private JSONArray _toFragmentEntriesJSONArray(
			List<FragmentEntryReference> fragmentEntryReferences)
		throws Exception {

		return JSONUtil.toJSONArray(
			fragmentEntryReferences,
			fragmentEntryReference -> {
				if (Validator.isNotNull(
						fragmentEntryReference.getRendererKey())) {

					return JSONUtil.put(
						"fragmentEntryRendererKey",
						fragmentEntryReference.getRendererKey());
				}

				if (Validator.isNull(
						fragmentEntryReference.getFragmentEntryERC())) {

					return null;
				}

				return JSONUtil.put(
					"fragmentEntryERC",
					fragmentEntryReference.getFragmentEntryERC()
				).put(
					"fragmentEntryScopeERC",
					() -> {
						if (Validator.isNull(
								fragmentEntryReference.
									getFragmentEntryScopeERC())) {

							return null;
						}

						return fragmentEntryReference.
							getFragmentEntryScopeERC();
					}
				);
			});
	}

}