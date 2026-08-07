/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = CMSStructureObjectFolderContributor.class)
public class ProductTypesCMSStructureObjectFolderContributor
	implements CMSStructureObjectFolderContributor {

	@Override
	public String getLabel() {
		return "product";
	}

	@Override
	public String getObjectFolderExternalReferenceCode() {
		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-96666")) {

			return null;
		}

		return PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES;
	}

	@Override
	public Map<String, List<String>> getSystemObjectFieldNames() {
		long companyId = CompanyThreadLocal.getCompanyId();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-96666")) {
			return Collections.emptyMap();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					companyId);

		if (objectDefinition == null) {
			return Collections.emptyMap();
		}

		List<String> systemObjectFieldNames = TransformUtil.transform(
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			objectField -> {
				String businessType = objectField.getBusinessType();

				if (objectField.isSystem() &&
					!GetterUtil.getBoolean(objectField.getReadOnly()) &&
					!businessType.equals(
						ObjectFieldConstants.BUSINESS_TYPE_DATE) &&
					!businessType.equals(
						ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

					return objectField.getName();
				}

				return null;
			});

		if (systemObjectFieldNames.isEmpty()) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, List<String>>put(
			PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_BASE_SKU,
			systemObjectFieldNames
		).build();
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}