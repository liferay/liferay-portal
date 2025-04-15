/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.options.web.internal.constants.CommerceSpecificationOptionFDSNames;
import com.liferay.commerce.product.options.web.internal.model.ListTypeDefinition;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "fds.data.provider.key=" + CommerceSpecificationOptionFDSNames.LIST_TYPE_DEFINITIONS,
	service = FDSDataProvider.class
)
public class CommerceSpecificationOptionListTypeDefinitionFDSDataProvider
	implements FDSDataProvider<ListTypeDefinition> {

	@Override
	public List<ListTypeDefinition> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long specificationId = ParamUtil.getLong(
			httpServletRequest, "specificationId");

		if (specificationId == 0) {
			return Collections.emptyList();
		}

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.getCPSpecificationOption(
				specificationId);

		if (ListUtil.isEmpty(cpSpecificationOption.getListTypeDefinitions())) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			cpSpecificationOption.getListTypeDefinitions(),
			this::_toListTypeDefinition);
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long specificationId = ParamUtil.getLong(
			httpServletRequest, "specificationId");

		if (specificationId == 0) {
			return 0;
		}

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.getCPSpecificationOption(
				specificationId);

		return (int)cpSpecificationOption.getListTypeDefinitionsCount();
	}

	private ListTypeDefinition _toListTypeDefinition(
		com.liferay.list.type.model.ListTypeDefinition listTypeDefinition) {

		return new ListTypeDefinition(
			listTypeDefinition.getListTypeDefinitionId(),
			listTypeDefinition.getName(
				listTypeDefinition.getDefaultLanguageId()),
			listTypeDefinition.getExternalReferenceCode());
	}

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

}