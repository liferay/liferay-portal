/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalChildTestEntity1;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalChildTestEntity2;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalTestEntity1;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ReferencingTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.scope.Scope;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class ReferencingTestEntityResourceTest
	extends BaseReferencingTestEntityResourceTestCase {

	@Ignore
	@Override
	@Test
	@TestInfo("LPD-105194")
	public void testGraphQLPostReferencingTestEntity() throws Exception {
		super.testGraphQLPostReferencingTestEntity();
	}

	@Override
	@Test
	@TestInfo({"LPD-105194", "LPD-105650"})
	public void testPostReferencingTestEntity() throws Exception {
		super.testPostReferencingTestEntity();

		_testPostReferencingTestEntityExternalChildTestEntity2();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalScopedTestEntity", "externalTestEntity1"};
	}

	@Override
	protected ReferencingTestEntity randomReferencingTestEntity()
		throws Exception {

		ReferencingTestEntity referencingTestEntity =
			super.randomReferencingTestEntity();

		ExternalScopedTestEntity externalScopedTestEntity =
			new ExternalScopedTestEntity();

		externalScopedTestEntity.setExternalReferenceCode(
			RandomTestUtil.randomString());
		externalScopedTestEntity.setScope(_randomScope());

		referencingTestEntity.setExternalScopedTestEntity(
			externalScopedTestEntity);

		ExternalChildTestEntity1 externalChildTestEntity1 =
			new ExternalChildTestEntity1();

		externalChildTestEntity1.setExternalProperty(
			RandomTestUtil.randomString());
		externalChildTestEntity1.setType(
			ExternalTestEntity1.Type.EXTERNAL_CHILD_TEST_ENTITY1);

		referencingTestEntity.setExternalTestEntity1(externalChildTestEntity1);

		return referencingTestEntity;
	}

	@Override
	protected ReferencingTestEntity
			testPostReferencingTestEntity_addReferencingTestEntity(
				ReferencingTestEntity referencingTestEntity)
		throws Exception {

		return referencingTestEntityResource.postReferencingTestEntity(
			referencingTestEntity);
	}

	private Scope _randomScope() {
		Scope scope = new Scope();

		scope.setExternalReferenceCode(RandomTestUtil.randomString());
		scope.setType(Scope.Type.SITE);

		return scope;
	}

	private void _testPostReferencingTestEntityExternalChildTestEntity2()
		throws Exception {

		ReferencingTestEntity referencingTestEntity =
			randomReferencingTestEntity();

		ExternalChildTestEntity2 externalChildTestEntity2 =
			new ExternalChildTestEntity2();

		externalChildTestEntity2.setExternalReferenceCode(
			RandomTestUtil.randomString());
		externalChildTestEntity2.setScope(_randomScope());
		externalChildTestEntity2.setType(
			ExternalTestEntity1.Type.EXTERNAL_CHILD_TEST_ENTITY2);

		referencingTestEntity.setExternalTestEntity1(externalChildTestEntity2);

		assertEquals(
			referencingTestEntity,
			referencingTestEntityResource.postReferencingTestEntity(
				referencingTestEntity));
	}

}