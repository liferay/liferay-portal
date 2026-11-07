/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.resource.v1_0;

import com.liferay.batch.planner.rest.dto.v1_0.Field;
import com.liferay.batch.planner.rest.internal.vulcan.batch.engine.util.FieldProviderUtil;
import com.liferay.batch.planner.rest.resource.v1_0.FieldResource;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResourceProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.resource.OpenAPIResource;

import java.util.Comparator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Matija Petanjek
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/field.properties",
	scope = ServiceScope.PROTOTYPE, service = FieldResource.class
)
public class FieldResourceImpl extends BaseFieldResourceImpl {

	@Override
	public Page<Field> getPlanInternalClassNameKeyFieldsPage(
			String internalClassNameKey, Boolean export)
		throws Exception {

		List<com.liferay.portal.vulcan.batch.engine.Field> vulcanFields =
			FieldProviderUtil.getFields(
				contextCompany.getCompanyId(), internalClassNameKey,
				_objectDefinitionLocalService,
				_objectEntryOpenAPIResourceProvider, _openAPIResource,
				contextUriInfo, _vulcanBatchEngineTaskItemDelegateRegistry);

		if (GetterUtil.getBoolean(export)) {
			vulcanFields = FieldProviderUtil.filter(
				vulcanFields,
				com.liferay.portal.vulcan.batch.engine.Field.AccessType.WRITE);
		}
		else {
			vulcanFields = FieldProviderUtil.filter(
				vulcanFields,
				com.liferay.portal.vulcan.batch.engine.Field.AccessType.READ);
		}

		vulcanFields.sort(Comparator.comparing(field -> field.getName()));

		return Page.of(transform(vulcanFields, this::_toField));
	}

	private Field _toField(
		com.liferay.portal.vulcan.batch.engine.Field vulcanField) {

		return new Field() {
			{
				setAnyOfGroup(vulcanField::getAnyOfGroup);
				setDescription(vulcanField::getDescription);
				setName(vulcanField::getName);
				setRequired(vulcanField::isRequired);
				setType(vulcanField::getType);
				setUnsupportedFormats(vulcanField::getUnsupportedFormats);
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryOpenAPIResourceProvider
		_objectEntryOpenAPIResourceProvider;

	@Reference
	private OpenAPIResource _openAPIResource;

	@Reference
	private VulcanBatchEngineTaskItemDelegateRegistry
		_vulcanBatchEngineTaskItemDelegateRegistry;

}