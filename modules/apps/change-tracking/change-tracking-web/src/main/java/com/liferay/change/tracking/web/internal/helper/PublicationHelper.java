/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.helper;

import com.liferay.change.tracking.on.demand.user.ticket.generator.CTOnDemandUserTicketGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = PublicationHelper.class)
public class PublicationHelper {

	public String getShareURL(
			long ctCollectionId, PortletRequest portletRequest)
		throws PortalException {

		Ticket ticket = _ctOnDemandUserTicketGenerator.generate(ctCollectionId);

		if (ticket == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_portal.getPortalURL(portletRequest));
		sb.append(Portal.PATH_MODULE);
		sb.append("/change_tracking/review_changes?ticketKey=");
		sb.append(ticket.getKey());

		return sb.toString();
	}

	@Reference
	private CTOnDemandUserTicketGenerator _ctOnDemandUserTicketGenerator;

	@Reference
	private Portal _portal;

}