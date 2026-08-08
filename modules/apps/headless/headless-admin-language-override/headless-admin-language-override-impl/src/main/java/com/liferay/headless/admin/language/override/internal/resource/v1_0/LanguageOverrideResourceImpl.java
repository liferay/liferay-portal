/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.resource.v1_0;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.language.override.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.language.override.internal.odata.entity.v1_0.LanguageOverrideEntityModel;
import com.liferay.headless.admin.language.override.resource.v1_0.LanguageOverrideResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.language.override.constants.PLOPortletKeys;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.language.override.service.PLOEntryService;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/language-override.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = LanguageOverrideResource.class
)
public class LanguageOverrideResourceImpl
	extends BaseLanguageOverrideResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<LanguageOverride> {

	@Override
	public void deleteLanguageOverrideByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

		_ploEntryService.deletePLOEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<PLOEntry> getExportImportDescriptor() {
		return new ExportImportDescriptor<PLOEntry>() {

			@Override
			public String getKey() {
				return LanguageOverrideResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "language-overrides";
			}

			@Override
			public Class<PLOEntry> getModelClass() {
				return PLOEntry.class;
			}

			@Override
			public List<String> getNestedFields() {
				return Collections.singletonList("creator");
			}

			@Override
			public String getPortletId() {
				return PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_CONFIGURATION;
			}

			@Override
			public boolean isActive(PortletDataContext portletDataContext) {
				return FeatureFlagManagerUtil.isEnabled(
					portletDataContext.getCompanyId(), "LPD-49852");
			}

		};
	}

	@Override
	public LanguageOverride getLanguageOverrideByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

		return _toLanguageOverride(
			_ploEntryService.getPLOEntryByExternalReferenceCode(
				externalReferenceCode));
	}

	@Override
	public Page<LanguageOverride> getLanguageOverridesPage(
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		_checkFeatureFlag();

		return Page.of(
			transform(
				_ploEntryService.getPLOEntries(
					search, pagination.getStartPosition(),
					pagination.getEndPosition(), _toOrderByComparator(sorts)),
				this::_toLanguageOverride),
			pagination, _ploEntryService.getPLOEntriesCount(search));
	}

	@Override
	public LanguageOverride postLanguageOverride(
			LanguageOverride languageOverride)
		throws Exception {

		_checkFeatureFlag();

		return _toLanguageOverride(
			_ploEntryService.addOrUpdatePLOEntry(
				languageOverride.getExternalReferenceCode(),
				contextUser.getUserId(), languageOverride.getKey(),
				languageOverride.getLanguageId(), languageOverride.getValue()));
	}

	@Override
	public LanguageOverride putLanguageOverrideByExternalReferenceCode(
			String externalReferenceCode, LanguageOverride languageOverride)
		throws Exception {

		_checkFeatureFlag();

		return _toLanguageOverride(
			_ploEntryService.addOrUpdatePLOEntry(
				externalReferenceCode, contextUser.getUserId(),
				languageOverride.getKey(), languageOverride.getLanguageId(),
				languageOverride.getValue()));
	}

	private void _checkFeatureFlag() {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-49852")) {

			throw new UnsupportedOperationException();
		}
	}

	private LanguageOverride _toLanguageOverride(PLOEntry ploEntry)
		throws Exception {

		return _languageOverrideDTOConverter.toDTO(ploEntry);
	}

	private OrderByComparator<PLOEntry> _toOrderByComparator(Sort[] sorts) {
		List<Object> objects = new ArrayList<>();

		if (ArrayUtil.isEmpty(sorts)) {
			objects.add(PLOEntryTable.INSTANCE.key.getName());
			objects.add(true);
		}
		else {
			for (Sort sort : sorts) {
				objects.add(sort.getFieldName());
				objects.add(!sort.isReverse());
			}
		}

		return OrderByComparatorFactoryUtil.create(
			PLOEntryTable.INSTANCE.getTableName(),
			objects.toArray(new Object[0]));
	}

	private static final EntityModel _entityModel =
		new LanguageOverrideEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.admin.language.override.internal.dto.v1_0.converter.LanguageOverrideDTOConverter)"
	)
	private DTOConverter<PLOEntry, LanguageOverride>
		_languageOverrideDTOConverter;

	@Reference
	private PLOEntryService _ploEntryService;

}