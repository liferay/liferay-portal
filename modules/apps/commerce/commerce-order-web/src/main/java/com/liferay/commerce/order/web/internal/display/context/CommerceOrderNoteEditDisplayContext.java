/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceOrderNoteEditDisplayContext {

	public CommerceOrderNoteEditDisplayContext(
			CommerceOrderNoteService commerceOrderNoteService,
			RenderRequest renderRequest)
		throws PortalException {

		long commerceOrderNoteId = ParamUtil.getLong(
			renderRequest, "commerceOrderNoteId");

		_commerceOrderNote = commerceOrderNoteService.getCommerceOrderNote(
			commerceOrderNoteId);
	}

	public CommerceOrderNote getCommerceOrderNote() {
		return _commerceOrderNote;
	}

	private final CommerceOrderNote _commerceOrderNote;

}