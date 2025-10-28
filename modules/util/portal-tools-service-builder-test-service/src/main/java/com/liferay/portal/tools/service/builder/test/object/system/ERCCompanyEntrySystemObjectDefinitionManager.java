/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.object.system;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.tools.service.builder.test.model.ERCCompanyEntry;
import com.liferay.portal.tools.service.builder.test.model.ERCCompanyEntryTable;
import com.liferay.portal.tools.service.builder.test.service.ERCCompanyEntryLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(service = SystemObjectDefinitionManager.class)
public class ERCCompanyEntrySystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(
			boolean checkPermissions, User user, Map<String, Object> values)
		throws Exception {

		ERCCompanyEntry ercCompanyEntry =
			_ercCompanyEntryLocalService.createERCCompanyEntry(
				_counterLocalService.increment());

		ercCompanyEntry.setExternalReferenceCode(
			GetterUtil.getString(values.get("externalReferenceCode")));
		ercCompanyEntry.setColumn1(
			GetterUtil.getInteger(values.get("column1")));

		ercCompanyEntry = _ercCompanyEntryLocalService.addERCCompanyEntry(
			ercCompanyEntry);

		return ercCompanyEntry.getErcCompanyEntryId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _ercCompanyEntryLocalService.deleteERCCompanyEntry(
			(ERCCompanyEntry)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _ercCompanyEntryLocalService.
			fetchERCCompanyEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _ercCompanyEntryLocalService.
			getERCCompanyEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		ERCCompanyEntry ercCompanyEntry =
			_ercCompanyEntryLocalService.getERCCompanyEntry(primaryKey);

		return ercCompanyEntry.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "ERC_COMPANY_ENTRY";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK);
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "ERCCompanyEntry"
		).put(
			"pluralLabel", "ERCCompanyEntries"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return ERCCompanyEntry.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Collections.singletonList(
			new IntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("column1")
			).name(
				"column1"
			).required(
				false
			).system(
				true
			).build());
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return ERCCompanyEntryTable.INSTANCE.ercCompanyEntryId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return ERCCompanyEntryTable.INSTANCE;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		ERCCompanyEntry ercCompanyEntry =
			_ercCompanyEntryLocalService.getERCCompanyEntry(primaryKey);

		ercCompanyEntry.setColumn1(
			GetterUtil.getInteger(values.get("column1")));

		_ercCompanyEntryLocalService.updateERCCompanyEntry(ercCompanyEntry);
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private ERCCompanyEntryLocalService _ercCompanyEntryLocalService;

}