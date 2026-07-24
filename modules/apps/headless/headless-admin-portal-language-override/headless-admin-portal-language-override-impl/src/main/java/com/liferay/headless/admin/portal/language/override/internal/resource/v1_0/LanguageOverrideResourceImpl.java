/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.portal.language.override.internal.resource.v1_0;

import com.liferay.headless.admin.portal.language.override.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.portal.language.override.internal.odata.entity.v1_0.LanguageOverrideEntityModel;
import com.liferay.headless.admin.portal.language.override.resource.v1_0.LanguageOverrideResource;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryService;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/language-override.properties",
	scope = ServiceScope.PROTOTYPE, service = LanguageOverrideResource.class
)
public class LanguageOverrideResourceImpl
	extends BaseLanguageOverrideResourceImpl implements EntityModelResource {

	@Override
	public void deleteLanguageOverrideByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		_ploEntryService.deletePLOEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public LanguageOverride getLanguageOverrideByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toLanguageOverride(
			_ploEntryService.getPLOEntryByExternalReferenceCode(
				externalReferenceCode));
	}

	@Override
	public Page<LanguageOverride> getLanguageOverridesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		long companyId = contextCompany.getCompanyId();

		OrderByComparator<PLOEntry> orderByComparator = _toOrderByComparator(
			sorts);

		if (Validator.isNotNull(search)) {
			return Page.of(
				transform(
					_ploEntryService.getPLOEntries(
						companyId, search, null, pagination.getStartPosition(),
						pagination.getEndPosition(), orderByComparator),
					this::_toLanguageOverride),
				pagination,
				_ploEntryService.getPLOEntriesCount(companyId, search, null));
		}

		return Page.of(
			transform(
				_ploEntryService.getPLOEntries(
					companyId, pagination.getStartPosition(),
					pagination.getEndPosition(), orderByComparator),
				this::_toLanguageOverride),
			pagination, _ploEntryService.getPLOEntriesCount(companyId));
	}

	@Override
	public LanguageOverride postLanguageOverride(
			LanguageOverride languageOverride)
		throws Exception {

		return _toLanguageOverride(
			_ploEntryService.addOrUpdatePLOEntry(
				languageOverride.getExternalReferenceCode(),
				languageOverride.getKey(), languageOverride.getLanguageId(),
				languageOverride.getValue()));
	}

	@Override
	public LanguageOverride putLanguageOverrideByExternalReferenceCode(
			String externalReferenceCode, LanguageOverride languageOverride)
		throws Exception {

		return _toLanguageOverride(
			_ploEntryService.addOrUpdatePLOEntry(
				externalReferenceCode, languageOverride.getKey(),
				languageOverride.getLanguageId(), languageOverride.getValue()));
	}

	private LanguageOverride _toLanguageOverride(PLOEntry ploEntry)
		throws Exception {

		return _languageOverrideResourceDTOConverter.toDTO(ploEntry);
	}

	private OrderByComparator<PLOEntry> _toOrderByComparator(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts) || (sorts[0] == null)) {
			return null;
		}

		Sort sort = sorts[0];

		String fieldName = sort.getFieldName();

		String columnName = null;

		if (fieldName.contains("createDate") ||
			fieldName.contains("dateCreated")) {

			columnName = "createDate";
		}
		else if (fieldName.contains("modifiedDate") ||
				 fieldName.contains("dateModified")) {

			columnName = "modifiedDate";
		}
		else if (fieldName.contains("externalReferenceCode")) {
			columnName = "externalReferenceCode";
		}
		else if (fieldName.contains("languageId")) {
			columnName = "languageId";
		}
		else if (fieldName.contains("value")) {
			columnName = "value";
		}
		else if (fieldName.contains("key")) {
			columnName = "key_";
		}

		if (columnName == null) {
			return null;
		}

		return OrderByComparatorFactoryUtil.create(
			"PLOEntry", columnName, !sort.isReverse());
	}

	private static final EntityModel _entityModel =
		new LanguageOverrideEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.admin.portal.language.override.internal.dto.v1_0.converter.LanguageOverrideResourceDTOConverter)"
	)
	private DTOConverter<PLOEntry, LanguageOverride>
		_languageOverrideResourceDTOConverter;

	@Reference
	private PLOEntryService _ploEntryService;

}