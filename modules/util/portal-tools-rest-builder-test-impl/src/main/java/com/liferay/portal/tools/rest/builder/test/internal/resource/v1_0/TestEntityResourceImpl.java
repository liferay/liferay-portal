/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntity;
import com.liferay.portal.tools.rest.builder.test.internal.entity.v1_0.TestEntityEntityModel;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.TestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/test-entity.properties",
	scope = ServiceScope.PROTOTYPE, service = TestEntityResource.class
)
public class TestEntityResourceImpl extends BaseTestEntityResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new TestEntityEntityModel();
	}

	@Override
	public Page<TestEntity> getTestEntitiesPage() {
		return Page.of(_testEntities);
	}

	@Override
	public TestEntity getTestEntity(Long testEntityId) {
		return _testEntities.get(Math.toIntExact(testEntityId));
	}

	@Override
	public Integer getTestEntityCount() {
		return _testEntities.size();
	}

	@Override
	public TestEntity patchTestEntity(
		Long testEntityId, Long optionalParameter, TestEntity testEntity) {

		return putTestEntity(testEntityId, optionalParameter, testEntity);
	}

	@Override
	public TestEntity postTestEntity(TestEntity testEntity) {
		_testEntities.add(testEntity);

		testEntity.setDateCreated(new Date());
		testEntity.setDateModified(new Date());
		testEntity.setId(_testEntities.size() - 1L);

		return testEntity;
	}

	@Override
	public TestEntity putTestEntity(
		Long testEntityId, Long optionalParameter, TestEntity testEntity) {

		TestEntity oldTestEntity = _testEntities.set(
			Math.toIntExact(testEntityId), testEntity);

		testEntity.setDateCreated(oldTestEntity.getDateCreated());
		testEntity.setDateModified(oldTestEntity.getDateModified());
		testEntity.setId(oldTestEntity.getId());

		return testEntity;
	}

	private static final List<TestEntity> _testEntities = new ArrayList<>();

}