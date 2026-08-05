/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.search.spi.model.index.contributor;

import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Kim
 */
@Component(
	property = "indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = ModelDocumentContributor.class
)
public class DLFileEntryModelDocumentContributor
	implements ModelDocumentContributor<DLFileEntry> {

	@Override
	public void contribute(Document document, DLFileEntry dlFileEntry) {
		long companyId = dlFileEntry.getCompanyId();
		long fileEntryId = dlFileEntry.getFileEntryId();

		Map<Long, String> requestStatusesByFileEntryId =
			_dsRequestManager.getRequestStatusesByFileEntryId(
				companyId, Collections.singletonList(fileEntryId));

		String requestStatus = requestStatusesByFileEntryId.get(fileEntryId);

		if (requestStatus != null) {
			document.addKeyword("signatureStatus", requestStatus);
		}

		if (!Objects.equals(requestStatus, "created") &&
			!Objects.equals(requestStatus, "sent")) {

			return;
		}

		Map<Long, Map<Long, String>> recipientStatusesByFileEntryId =
			_dsRequestManager.getRecipientStatusesByFileEntryId(
				companyId, Collections.singletonList(fileEntryId));

		Map<Long, String> statusesByUserId = recipientStatusesByFileEntryId.get(
			fileEntryId);

		if (MapUtil.isEmpty(statusesByUserId)) {
			return;
		}

		document.addKeyword(
			"signatureRecipientStatuses",
			TransformUtil.transformToArray(
				statusesByUserId.entrySet(),
				entry -> entry.getKey() + "_" + entry.getValue(),
				String.class));
	}

	@Reference
	private DSRequestManager _dsRequestManager;

}