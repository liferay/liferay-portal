/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.result;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultManager;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.SummaryFactory;
import com.liferay.portal.kernel.search.result.SearchResultContributor;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author André de Oliveira
 */
@Component(service = SearchResultManager.class)
public class SearchResultManagerImpl implements SearchResultManager {

	@Override
	public SearchResult createSearchResult(Document document)
		throws PortalException {

		SearchResultContributor searchResultContributor =
			_getSearchResultContributor(document);

		if (searchResultContributor == null) {
			return _createSearchResultWithEntryClass(document);
		}

		if (_isClassPresent(document)) {
			return _createSearchResultWithClass(document);
		}

		return _createSearchResultWithEntryClass(document);
	}

	@Override
	public void updateSearchResult(
			SearchResult searchResult, Document document, Locale locale,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		SearchResultContributor searchResultContributor =
			_getSearchResultContributor(document);

		if ((searchResultContributor != null) && _isClassPresent(document)) {
			searchResultContributor.addRelatedModel(
				searchResult, document, locale, portletRequest,
				portletResponse);

			if (searchResult.getSummary() == null) {
				searchResult.setSummary(
					_getSummaryWithClass(searchResult, locale));
			}

			return;
		}

		searchResult.setSummary(
			_getSummaryWithEntryClass(
				document, locale, portletRequest, portletResponse));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SearchResultContributor.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(searchResultContributor, emitter) -> emitter.emit(
					searchResultContributor.getEntryClassName())));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private SearchResult _createSearchResultWithClass(Document document)
		throws PortalException {

		long classNameId = GetterUtil.getLong(
			document.get(Field.CLASS_NAME_ID));

		ClassName className = _classNameLocalService.getClassName(classNameId);

		if (className == null) {
			throw new PortalException(
				"Unable to get class name from class name ID " + classNameId);
		}

		long classPK = GetterUtil.getLong(document.get(Field.CLASS_PK));

		return new SearchResult(className.getClassName(), classPK);
	}

	private SearchResult _createSearchResultWithEntryClass(Document document) {
		String entryClassName = GetterUtil.getString(
			document.get(Field.ENTRY_CLASS_NAME));
		long entryClassPK = GetterUtil.getLong(
			document.get(Field.ENTRY_CLASS_PK));

		return new SearchResult(entryClassName, entryClassPK);
	}

	private SearchResultContributor _getSearchResultContributor(
		Document document) {

		String entryClassName = GetterUtil.getString(
			document.get(Field.ENTRY_CLASS_NAME));

		return _serviceTrackerMap.getService(entryClassName);
	}

	private Summary _getSummaryWithClass(
			SearchResult searchResult, Locale locale)
		throws PortalException {

		return _summaryFactory.getSummary(
			searchResult.getClassName(), searchResult.getClassPK(), locale);
	}

	private Summary _getSummaryWithEntryClass(
			Document document, Locale locale, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws PortalException {

		String entryClassName = GetterUtil.getString(
			document.get(Field.ENTRY_CLASS_NAME));
		long entryClassPK = GetterUtil.getLong(
			document.get(Field.ENTRY_CLASS_PK));

		return _summaryFactory.getSummary(
			document, entryClassName, entryClassPK, locale, portletRequest,
			portletResponse);
	}

	private boolean _isClassPresent(Document document) {
		long classNameId = GetterUtil.getLong(
			document.get(Field.CLASS_NAME_ID));
		long classPK = GetterUtil.getLong(document.get(Field.CLASS_PK));

		if ((classNameId > 0) && (classPK > 0)) {
			return true;
		}

		return false;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private ServiceTrackerMap<String, SearchResultContributor>
		_serviceTrackerMap;

	@Reference
	private SummaryFactory _summaryFactory;

}