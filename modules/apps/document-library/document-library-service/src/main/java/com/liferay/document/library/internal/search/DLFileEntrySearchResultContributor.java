/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.search;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.SummaryFactory;
import com.liferay.portal.kernel.search.result.SearchResultContributor;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author André de Oliveira
 */
@Component(service = SearchResultContributor.class)
public class DLFileEntrySearchResultContributor
	implements SearchResultContributor {

	@Override
	public void addRelatedModel(
			SearchResult searchResult, Document document, Locale locale,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		long entryClassPK = GetterUtil.getLong(
			document.get(Field.ENTRY_CLASS_PK));

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(entryClassPK);

		if (fileEntry != null) {
			Summary summary = _summaryFactory.getSummary(
				document, DLFileEntry.class.getName(),
				fileEntry.getFileEntryId(), locale, portletRequest,
				portletResponse);

			if (Validator.isNull(summary.getContent())) {
				summary.setContent(fileEntry.getTitle());
			}

			searchResult.addFileEntry(fileEntry, summary);
		}
		else {
			long classNameId = GetterUtil.getLong(
				document.get(Field.CLASS_NAME_ID));

			ClassName className = _classNameLocalService.getClassName(
				classNameId);

			long classPK = GetterUtil.getLong(document.get(Field.CLASS_PK));

			searchResult.setSummary(
				_summaryFactory.getSummary(
					document, className.getClassName(), classPK, locale,
					portletRequest, portletResponse));
		}
	}

	@Override
	public String getEntryClassName() {
		return DLFileEntryConstants.getClassName();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private SummaryFactory _summaryFactory;

}