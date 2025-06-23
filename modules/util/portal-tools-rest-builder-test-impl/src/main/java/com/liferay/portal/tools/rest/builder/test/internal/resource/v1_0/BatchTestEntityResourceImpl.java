/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.BatchTestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/batch-test-entity.properties",
	scope = ServiceScope.PROTOTYPE, service = BatchTestEntityResource.class
)
public class BatchTestEntityResourceImpl
	extends BaseBatchTestEntityResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<BatchTestEntity> {

	@Override
	public Response deleteBatchTestEntityByExternalReferenceCode(
		String externalReferenceCode) {

		_batchTestEntities.remove(externalReferenceCode);

		return Response.status(
			204
		).build();
	}

	@Override
	public Page<BatchTestEntity> getBatchTestEntitiesPage() {
		return Page.of(_batchTestEntities.values());
	}

	@Override
	public BatchTestEntity getBatchTestEntity(Long batchTestEntityId) {
		for (BatchTestEntity batchTestEntity : _batchTestEntities.values()) {
			if (Objects.equals(batchTestEntity.getId(), batchTestEntityId)) {
				return batchTestEntity;
			}
		}

		return null;
	}

	@Override
	public BatchTestEntity getBatchTestEntityByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		BatchTestEntity batchTestEntity = _batchTestEntities.get(
			externalReferenceCode);

		if (batchTestEntity == null) {
			throw new NoSuchModelException();
		}

		return batchTestEntity;
	}

	@Override
	public String getPortletId() {
		return "com_liferay_portal_tools_rest_builder_test_portlet_" +
			"BatchTestEntityPortlet";
	}

	@Override
	public Scope getScope() {
		return Scope.COMPANY;
	}

	@Override
	public BatchTestEntity postBatchTestEntity(
		BatchTestEntity batchTestEntity) {

		_batchTestEntities.put(
			batchTestEntity.getExternalReferenceCode(), batchTestEntity);

		batchTestEntity.setId(_batchTestEntities.size() - 1L);

		return batchTestEntity;
	}

	@Override
	public BatchTestEntity putBatchTestEntityByExternalReferenceCode(
		String externalReferenceCode, BatchTestEntity batchTestEntity) {

		BatchTestEntity oldBatchTestEntity = _batchTestEntities.put(
			externalReferenceCode, batchTestEntity);

		batchTestEntity.setExternalReferenceCode(externalReferenceCode);
		batchTestEntity.setId(
			(oldBatchTestEntity != null) ? oldBatchTestEntity.getId() :
				_batchTestEntities.size() - 1L);
		batchTestEntity.setName(batchTestEntity.getName());

		return batchTestEntity;
	}

	private static final Map<String, BatchTestEntity> _batchTestEntities =
		new HashMap<>();

}