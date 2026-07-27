/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;

/**
 * @author José Abelenda
 */
public class CMPProjectSelectionFDSFilter extends BaseSelectionFDSFilter {

	public CMPProjectSelectionFDSFilter(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	@Override
	public String getAPIURL() {
		StringBundler sb = new StringBundler(4);

		sb.append("/o/search/v1.0/search?emptySearch=true&");
		sb.append("filter=objectDefinitionId eq ");
		sb.append(_objectDefinition.getObjectDefinitionId());
		sb.append("&nestedFields=embedded");

		return sb.toString();
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "cmpTaskCMPProjectId";
	}

	@Override
	public String getItemKey() {
		return "embedded.id";
	}

	@Override
	public String getItemLabel() {
		return "embedded.title";
	}

	@Override
	public String getLabel() {
		return "project";
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	private final ObjectDefinition _objectDefinition;

}