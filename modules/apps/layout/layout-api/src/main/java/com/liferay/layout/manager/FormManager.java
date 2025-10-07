/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.manager;

import aQute.bnd.annotation.ProviderType;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.info.field.InfoField;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;
import java.util.Locale;

/**
 * @author Lourdes Fernández Besada
 */
@ProviderType
public interface FormManager {

	public FragmentStyledLayoutStructureItem
			addFragmentEntryLinksLayoutStructureItem(
				String fragmentEntryKey, InfoField<?> infoField, Layout layout,
				LayoutStructure layoutStructure,
				LayoutStructureItem layoutStructureItem, boolean readOnly,
				long segmentsExperienceId, ServiceContext serviceContext)
		throws PortalException;

	public List<LayoutStructureItem> addFragmentEntryLinksLayoutStructureItems(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			JSONObject errorJSONObject,
			FormStyledLayoutStructureItem formStyledLayoutStructureItem,
			boolean includeSubmitButton, Layout layout,
			LayoutStructure layoutStructure, Locale locale, boolean readOnly,
			long segmentsExperienceId, ServiceContext serviceContext,
			String[] uniqueInfoFieldIds)
		throws PortalException;

}