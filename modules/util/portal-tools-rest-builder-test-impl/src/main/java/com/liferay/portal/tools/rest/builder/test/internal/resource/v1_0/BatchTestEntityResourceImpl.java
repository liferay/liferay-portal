/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.BatchTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.CompanyTestEntityResource;
import com.liferay.portal.vulcan.custom.field.CustomField;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/batch-test-entity.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = BatchTestEntityResource.class
)
public class BatchTestEntityResourceImpl
	extends BaseBatchTestEntityResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<BatchTestEntity> {

	@Override
	public void deleteBatchTestEntityByExternalReferenceCode(
		String externalReferenceCode) {

		BatchTestEntity batchTestEntity = _fetchBatchTestEntity(
			externalReferenceCode);

		if (batchTestEntity != null) {
			_batchTestEntities.remove(batchTestEntity.getId());
			_relationships.remove(batchTestEntity.getId());
		}
	}

	@Override
	public Page<BatchTestEntity> getBatchTestEntitiesPage() {
		return Page.of(
			transform(_batchTestEntities.values(), this::_toBatchTestEntity));
	}

	@Override
	public BatchTestEntity getBatchTestEntity(Long batchTestEntityId)
		throws NoSuchModelException {

		BatchTestEntity originalBatchTestEntity = _fetchBatchTestEntity(
			batchTestEntityId);

		if (originalBatchTestEntity == null) {
			throw new NoSuchModelException();
		}

		return _toBatchTestEntity(originalBatchTestEntity);
	}

	@Override
	public BatchTestEntity getBatchTestEntityByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		BatchTestEntity batchTestEntity = _fetchBatchTestEntity(
			externalReferenceCode);

		if (batchTestEntity == null) {
			throw new NoSuchModelException();
		}

		return _toBatchTestEntity(batchTestEntity);
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return BatchTestEntity.class.getName();
			}

			@Override
			public List<String> getNestedFields() {
				return Arrays.asList("nestedField", "relatedCompanyTestEntity");
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

		};
	}

	@Override
	public BatchTestEntity postBatchTestEntity(BatchTestEntity batchTestEntity)
		throws Exception {

		long batchTestEntityId = _counter.increment();

		if (Validator.isNull(batchTestEntity.getExternalReferenceCode())) {
			batchTestEntity.setExternalReferenceCode(StringUtil.randomString());
		}

		batchTestEntity.setId(batchTestEntityId);

		CompanyTestEntity companyTestEntity =
			batchTestEntity.getRelatedCompanyTestEntity();

		if (companyTestEntity != null) {
			CompanyTestEntityResource companyTestEntityResource =
				_factory.create(
				).uriInfo(
					contextUriInfo
				).user(
					contextUser
				).build();

			CompanyTestEntity finalCompanyTestEntity = companyTestEntity;

			companyTestEntity = _emptyModelManager.getOrAddEmptyModel(
				CompanyTestEntity.class, contextCompany.getCompanyId(),
				() -> {
					try {
						return companyTestEntityResource.postCompanyTestEntity(
							finalCompanyTestEntity);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				},
				companyTestEntity.getExternalReferenceCode(),
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						return null;
					}
				},
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				});

			batchTestEntity.setRelatedCompanyTestEntity(companyTestEntity);

			_relationships.put(
				batchTestEntity.getId(), companyTestEntity.getId());
		}
		else {
			_relationships.remove(batchTestEntityId);
		}

		_batchTestEntities.put(batchTestEntityId, batchTestEntity);

		return _toBatchTestEntity(batchTestEntity);
	}

	@Override
	public BatchTestEntity putBatchTestEntityByExternalReferenceCode(
			String externalReferenceCode, BatchTestEntity batchTestEntity)
		throws Exception {

		BatchTestEntity existingBatchTestEntity = _fetchBatchTestEntity(
			externalReferenceCode);

		if (existingBatchTestEntity == null) {
			return postBatchTestEntity(batchTestEntity);
		}

		batchTestEntity.setExternalReferenceCode(externalReferenceCode);
		batchTestEntity.setId(existingBatchTestEntity.getId());

		CompanyTestEntity companyTestEntity =
			batchTestEntity.getRelatedCompanyTestEntity();

		if (companyTestEntity != null) {
			CompanyTestEntityResource companyTestEntityResource =
				_factory.create(
				).uriInfo(
					contextUriInfo
				).user(
					contextUser
				).build();

			CompanyTestEntity finalCompanyTestEntity = companyTestEntity;

			companyTestEntity = _emptyModelManager.getOrAddEmptyModel(
				CompanyTestEntity.class, contextCompany.getCompanyId(),
				() -> {
					try {
						return companyTestEntityResource.postCompanyTestEntity(
							finalCompanyTestEntity);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				},
				companyTestEntity.getExternalReferenceCode(),
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						return null;
					}
				},
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				});

			_relationships.put(
				batchTestEntity.getId(), companyTestEntity.getId());

			batchTestEntity.setRelatedCompanyTestEntity(companyTestEntity);
		}
		else {
			_relationships.remove(batchTestEntity.getId());
		}

		_batchTestEntities.put(batchTestEntity.getId(), batchTestEntity);

		return _toBatchTestEntity(batchTestEntity);
	}

	private BatchTestEntity _fetchBatchTestEntity(long id) {
		if (_batchTestEntities.containsKey(id)) {
			return _batchTestEntities.get(id);
		}

		return null;
	}

	private BatchTestEntity _fetchBatchTestEntity(
		String externalReferenceCode) {

		for (BatchTestEntity batchTestEntity : _batchTestEntities.values()) {
			if (Objects.equals(
					externalReferenceCode,
					batchTestEntity.getExternalReferenceCode())) {

				return batchTestEntity;
			}
		}

		return null;
	}

	private BatchTestEntity _toBatchTestEntity(
		BatchTestEntity originalBatchTestEntity) {

		return new BatchTestEntity() {
			{
				setCustomFields(
					() -> transform(
						originalBatchTestEntity.getCustomFields(),
						originalCustomField -> {
							CustomField customField = new CustomField();

							customField.setAttributeType(
								() -> NestedFieldsSupplier.supply(
									"customFields.attributeType",
									nestedField ->
										originalCustomField.
											getAttributeType()));
							customField.setCustomValue(
								originalCustomField.getCustomValue());
							customField.setDataType(
								originalCustomField.getDataType());
							customField.setName(originalCustomField.getName());

							return customField;
						},
						CustomField.class));
				setExternalReferenceCode(
					originalBatchTestEntity.getExternalReferenceCode());
				setId(originalBatchTestEntity.getId());
				setName(originalBatchTestEntity.getName());
				setNestedField(
					() -> NestedFieldsSupplier.supply(
						"nestedField",
						nestedField ->
							originalBatchTestEntity.getNestedField()));
				setRelatedCompanyTestEntity(
					() -> NestedFieldsSupplier.supply(
						"relatedCompanyTestEntity",
						nestedField -> {
							if (!_relationships.containsKey(
									originalBatchTestEntity.getId())) {

								return null;
							}

							CompanyTestEntityResource
								companyTestEntityResource = _factory.create(
								).uriInfo(
									contextUriInfo
								).user(
									contextUser
								).build();

							return companyTestEntityResource.
								getCompanyTestEntity(
									_relationships.get(
										originalBatchTestEntity.getId()));
						}));
			}
		};
	}

	private static final Map<Long, BatchTestEntity> _batchTestEntities =
		new TreeMap<>();
	private static final LongWrapper _counter = new LongWrapper();
	private static final Map<Long, Long> _relationships = new TreeMap<>();

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private CompanyTestEntityResource.Factory _factory;

}