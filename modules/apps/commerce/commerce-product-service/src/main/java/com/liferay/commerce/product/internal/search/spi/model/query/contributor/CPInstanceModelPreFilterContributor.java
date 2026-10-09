/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.query.contributor;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian I. Kim
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPInstance",
	service = ModelPreFilterContributor.class
)
public class CPInstanceModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_filterByCPDefinitionId(booleanFilter, searchContext);
		_filterByCPDefinitionStatus(booleanFilter, searchContext);
		_filterByHasChildDefinitions(booleanFilter, searchContext);
		_filterByOptions(booleanFilter, searchContext);
		_filterByReplacedCPInstances(booleanFilter, searchContext);
		_filterByPublished(booleanFilter, searchContext);
		_filterByPurchasable(booleanFilter, searchContext);
		_filterByStatus(booleanFilter, searchContext);
	}

	private void _filterByCPDefinitionId(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		long cpDefinitionId = GetterUtil.getLong(
			searchContext.getAttribute(CPField.CP_DEFINITION_ID));

		if (cpDefinitionId > 0) {
			booleanFilter.addRequiredTerm(
				CPField.CP_DEFINITION_ID, cpDefinitionId);
		}
	}

	private void _filterByCPDefinitionStatus(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		int cpDefinitionStatus = GetterUtil.getInteger(
			searchContext.getAttribute(CPField.CP_DEFINITION_STATUS));

		if (cpDefinitionStatus == WorkflowConstants.STATUS_ANY) {
			booleanFilter.addRangeTerm(
				CPField.CP_DEFINITION_STATUS, WorkflowConstants.STATUS_APPROVED,
				WorkflowConstants.STATUS_SCHEDULED);
		}
		else {
			booleanFilter.addRequiredTerm(
				CPField.CP_DEFINITION_STATUS, cpDefinitionStatus);
		}
	}

	private void _filterByHasChildDefinitions(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		boolean published = GetterUtil.getBoolean(
			searchContext.getAttribute(CPField.HAS_CHILD_CP_DEFINITIONS));

		if (published) {
			booleanFilter.addRequiredTerm(
				CPField.HAS_CHILD_CP_DEFINITIONS, true);
		}
	}

	private void _filterByOptions(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		String[] fieldNames = GetterUtil.getStringValues(
			searchContext.getAttribute("OPTIONS"));

		if (fieldNames.length < 1) {
			return;
		}

		for (String fieldName : fieldNames) {
			String fieldValue = GetterUtil.getString(
				searchContext.getAttribute(fieldName));

			if (Validator.isNull(fieldValue)) {
				continue;
			}

			booleanFilter.addRequiredTerm(fieldName, fieldValue);
		}
	}

	private void _filterByPublished(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		boolean published = GetterUtil.getBoolean(
			searchContext.getAttribute(CPField.PUBLISHED));

		if (published) {
			booleanFilter.addRequiredTerm(CPField.PUBLISHED, true);
		}
	}

	private void _filterByPurchasable(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		boolean purchasable = GetterUtil.getBoolean(
			searchContext.getAttribute(CPField.PURCHASABLE));

		if (purchasable) {
			booleanFilter.addRequiredTerm(CPField.PURCHASABLE, true);
		}
	}

	private void _filterByReplacedCPInstances(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		String replacementCPInstanceUuid = GetterUtil.getString(
			searchContext.getAttribute(CPField.REPLACEMENT_CP_INSTANCE_UUID));

		long replacementCProductId = GetterUtil.getLong(
			searchContext.getAttribute(CPField.REPLACEMENT_CPRODUCT_ID));

		if (Validator.isNotNull(replacementCPInstanceUuid) &&
			(replacementCProductId > 0)) {

			booleanFilter.addRequiredTerm(
				CPField.REPLACEMENT_CP_INSTANCE_UUID,
				replacementCPInstanceUuid);
			booleanFilter.addRequiredTerm(
				CPField.REPLACEMENT_CPRODUCT_ID, replacementCProductId);
		}
	}

	private void _filterByStatus(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		int status = GetterUtil.getInteger(
			searchContext.getAttribute(Field.STATUS),
			WorkflowConstants.STATUS_APPROVED);

		if (status != WorkflowConstants.STATUS_ANY) {
			booleanFilter.addRequiredTerm(Field.STATUS, status);
		}
	}

}