/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.dto.v1_0.converter;

import com.liferay.change.tracking.rest.dto.v1_0.CTCollection;
import com.liferay.change.tracking.rest.dto.v1_0.Status;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "dto.class.name=com.liferay.change.tracking.model.CTCollection",
	service = DTOConverter.class
)
public class CTCollectionDTOConverter
	implements DTOConverter
		<com.liferay.change.tracking.model.CTCollection, CTCollection> {

	@Override
	public String getContentType() {
		return CTCollection.class.getSimpleName();
	}

	@Override
	public CTCollection toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.change.tracking.model.CTCollection ctCollection)
		throws Exception {

		if (ctCollection == null) {
			return null;
		}

		return new CTCollection() {
			{
				setActions(dtoConverterContext::getActions);
				setDateCreated(ctCollection::getCreateDate);
				setDateModified(ctCollection::getModifiedDate);
				setDateScheduled(() -> _getDateScheduled(ctCollection));
				setDescription(ctCollection::getDescription);
				setExternalReferenceCode(
					ctCollection::getExternalReferenceCode);
				setId(ctCollection::getCtCollectionId);
				setName(ctCollection::getName);
				setOwnerName(ctCollection::getUserName);
				setStatus(
					() -> _toStatus(
						dtoConverterContext.getLocale(),
						ctCollection.getStatus()));
				setStatusMessage(
					() -> _getStatusMessage(
						ctCollection,
						dtoConverterContext.getHttpServletRequest()));
			}
		};
	}

	private Date _getDateScheduled(
		com.liferay.change.tracking.model.CTCollection ctCollection) {

		if (ctCollection.getStatus() != WorkflowConstants.STATUS_SCHEDULED) {
			return null;
		}

		return ctCollection.getScheduledDate();
	}

	private String _getStatusMessage(
		com.liferay.change.tracking.model.CTCollection ctCollection,
		HttpServletRequest httpServletRequest) {

		if (ctCollection == null) {
			return StringPool.BLANK;
		}

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			Date statusDate = ctCollection.getStatusDate();

			return _language.format(
				httpServletRequest, "published-x-ago-by-x",
				new String[] {
					_language.getTimeDescription(
						httpServletRequest,
						System.currentTimeMillis() - statusDate.getTime(),
						true),
					HtmlUtil.escape(ctCollection.getUserName())
				});
		}
		else if (ctCollection.isInProgress()) {
			Date modifiedDate = ctCollection.getModifiedDate();

			return _language.format(
				httpServletRequest, "modified-x-ago-by-x",
				new String[] {
					_language.getTimeDescription(
						httpServletRequest,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true),
					HtmlUtil.escape(ctCollection.getUserName())
				});
		}
		else if (ctCollection.getStatus() ==
					WorkflowConstants.STATUS_SCHEDULED) {

			Date scheduledDate = ctCollection.getScheduledDate();

			if (scheduledDate == null) {
				return StringPool.BLANK;
			}

			return _language.format(
				httpServletRequest, "schedule-to-publish-in-x-by-x",
				new String[] {
					_language.getTimeDescription(
						httpServletRequest,
						scheduledDate.getTime() - System.currentTimeMillis(),
						true),
					HtmlUtil.escape(ctCollection.getUserName())
				});
		}

		return StringPool.BLANK;
	}

	private Status _toStatus(Locale locale, int status) throws Exception {
		String statusLabel;

		if (status == WorkflowConstants.STATUS_APPROVED) {
			statusLabel = "published";
		}
		else if (status == WorkflowConstants.STATUS_EXPIRED) {
			statusLabel = "out-of-date";
		}
		else if (status == WorkflowConstants.STATUS_DRAFT) {
			statusLabel = "in-progress";
		}
		else if (status == WorkflowConstants.STATUS_DENIED) {
			statusLabel = "failed";
		}
		else if (status == WorkflowConstants.STATUS_INCOMPLETE) {
			statusLabel = "pending-approval";
		}
		else if (status == WorkflowConstants.STATUS_SCHEDULED) {
			statusLabel = "scheduled";
		}
		else {
			statusLabel = StringPool.BLANK;
		}

		return new Status() {
			{
				setCode(() -> status);
				setLabel(() -> statusLabel);
				setLabel_i18n(() -> _language.get(locale, statusLabel));
			}
		};
	}

	@Reference
	private Language _language;

}