/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Víctor Galán
 */
public class
	DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelException
		extends PortalException {

	public DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelException() {
	}

	public DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelException(
		String msg) {

		super(msg);
	}

	public DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelException(
		Throwable throwable) {

		super(throwable);
	}

}