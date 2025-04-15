/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorReturnType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class DLFileEntryAdditionalMetadataSetsDisplayContext {

	public DLFileEntryAdditionalMetadataSetsDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<DDMStructure> getDDMStructures() throws PortalException {
		if (_ddmStructures != null) {
			return _ddmStructures;
		}

		DLFileEntryType dlFileEntryType = getDLFileEntryType();

		if (dlFileEntryType == null) {
			_ddmStructures = Collections.emptyList();

			return _ddmStructures;
		}

		DDMStructure ddmStructure = _getDDMStructure();

		if (ddmStructure == null) {
			_ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
				dlFileEntryType);
		}
		else {
			_ddmStructures = ListUtil.filter(
				DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType),
				currentDDMStructure ->
					currentDDMStructure.getStructureId() !=
						ddmStructure.getStructureId());
		}

		return _ddmStructures;
	}

	public int getDDMStructuresCount() throws PortalException {
		List<DDMStructure> ddmStructures = getDDMStructures();

		return ddmStructures.size();
	}

	public DLFileEntryType getDLFileEntryType() throws PortalException {
		if (_dlFileEntryType != null) {
			return _dlFileEntryType;
		}

		long fileEntryTypeId = ParamUtil.getLong(
			_httpServletRequest, "fileEntryTypeId");

		if (fileEntryTypeId != 0) {
			_dlFileEntryType = DLFileEntryTypeServiceUtil.getFileEntryType(
				fileEntryTypeId);
		}

		return _dlFileEntryType;
	}

	public String getSelectDDMStructureURL() {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		DDMStructureItemSelectorCriterion ddmStructureItemSelectorCriterion =
			new DDMStructureItemSelectorCriterion();

		ddmStructureItemSelectorCriterion.setClassNameId(
			PortalUtil.getClassNameId(DLFileEntryMetadata.class));
		ddmStructureItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DDMStructureItemSelectorReturnType());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "selectDDMStructure",
				ddmStructureItemSelectorCriterion));
	}

	private DDMStructure _getDDMStructure() throws PortalException {
		if (_ddmStructure != null) {
			return _ddmStructure;
		}

		DLFileEntryType dlFileEntryType = getDLFileEntryType();

		if ((dlFileEntryType == null) ||
			(dlFileEntryType.getDataDefinitionId() == 0)) {

			return null;
		}

		_ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			dlFileEntryType.getDataDefinitionId());

		return _ddmStructure;
	}

	private DDMStructure _ddmStructure;
	private List<DDMStructure> _ddmStructures;
	private DLFileEntryType _dlFileEntryType;
	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}