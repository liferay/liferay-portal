/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.odata.retriever;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.segments.internal.odata.entity.OrganizationEntityModel;
import com.liferay.segments.odata.retriever.ODataRetriever;
import com.liferay.segments.odata.search.ODataSearchAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.model.Organization",
	service = ODataRetriever.class
)
public class OrganizationODataRetriever
	implements ODataRetriever<Organization> {

	@Override
	public long[] getResultPrimaryKeys(
			long companyId, String filterString, Locale locale, int start,
			int end)
		throws PortalException {

		return _oDataSearchAdapter.searchPrimaryKeys(
			companyId, _filterParserProvider.provide(_entityModel),
			filterString, Organization.class.getName(), _entityModel, locale,
			Field.ORGANIZATION_ID, start, end);
	}

	@Override
	public List<Organization> getResults(
			long companyId, String filterString, Locale locale, int start,
			int end)
		throws PortalException {

		FilterParser filterParser = _filterParserProvider.provide(_entityModel);

		Hits hits = _oDataSearchAdapter.search(
			companyId, filterParser, filterString, Organization.class.getName(),
			_entityModel, locale, start, end);

		return _getOrganizations(hits);
	}

	@Override
	public int getResultsCount(
			long companyId, String filterString, Locale locale)
		throws PortalException {

		FilterParser filterParser = _filterParserProvider.provide(_entityModel);

		return _oDataSearchAdapter.searchCount(
			companyId, filterParser, filterString, Organization.class.getName(),
			_entityModel, locale);
	}

	private Organization _getOrganization(Document document)
		throws PortalException {

		long organizationId = GetterUtil.getLong(
			document.get(Field.ORGANIZATION_ID));

		return _organizationLocalService.getOrganization(organizationId);
	}

	private List<Organization> _getOrganizations(Hits hits)
		throws PortalException {

		Document[] documents = hits.getDocs();

		List<Organization> organizations = new ArrayList<>(documents.length);

		for (Document document : documents) {
			organizations.add(_getOrganization(document));
		}

		return organizations;
	}

	@Reference(
		target = "(entity.model.name=" + OrganizationEntityModel.NAME + ")"
	)
	private EntityModel _entityModel;

	@Reference
	private FilterParserProvider _filterParserProvider;

	@Reference
	private ODataSearchAdapter _oDataSearchAdapter;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}