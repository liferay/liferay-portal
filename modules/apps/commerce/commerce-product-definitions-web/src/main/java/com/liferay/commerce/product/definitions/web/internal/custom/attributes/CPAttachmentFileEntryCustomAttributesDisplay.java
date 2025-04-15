/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.custom.attributes;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.expando.kernel.model.BaseCustomAttributesDisplay;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ethan Bustad
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
	service = CustomAttributesDisplay.class
)
public class CPAttachmentFileEntryCustomAttributesDisplay
	extends BaseCustomAttributesDisplay {

	@Override
	public String getClassName() {
		return CPAttachmentFileEntry.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "tag";
	}

}