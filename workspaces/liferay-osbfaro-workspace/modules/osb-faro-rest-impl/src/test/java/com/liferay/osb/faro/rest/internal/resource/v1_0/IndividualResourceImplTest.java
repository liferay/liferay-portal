/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class IndividualResourceImplTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_individualResourceImpl, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtil.setFieldValue(
			_individualResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetWorkspaceGroupIndividualWithMissingIndividual()
		throws Exception {

		String emptyIndividualId = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.getIndividual(
				Mockito.any(), Mockito.eq(emptyIndividualId), Mockito.isNull())
		).thenReturn(
			new Individual()
		);

		String missingIndividualId = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.getIndividual(
				Mockito.any(), Mockito.eq(missingIndividualId),
				Mockito.isNull())
		).thenThrow(
			new NoSuchEntryException(RandomTestUtil.randomString())
		);

		_assertGetWorkspaceGroupIndividual(emptyIndividualId);
		_assertGetWorkspaceGroupIndividual(missingIndividualId);
	}

	private void _assertGetWorkspaceGroupIndividual(String individualId)
		throws Exception {

		try {
			_individualResourceImpl.getWorkspaceGroupIndividual(
				RandomTestUtil.randomLong(), individualId, null);

			Assert.fail();
		}
		catch (NotFoundException notFoundException) {
			String expectedMessage =
				"No individual was found with ID " + individualId;

			Assert.assertEquals(
				expectedMessage, notFoundException.getMessage());

			Response response = notFoundException.getResponse();

			Problem problem = (Problem)response.getEntity();

			Assert.assertEquals(Response.Status.NOT_FOUND, problem.getStatus());
			Assert.assertEquals(expectedMessage, problem.getTitle());
			Assert.assertNull(problem.getType());
		}
	}

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final IndividualResourceImpl _individualResourceImpl =
		new IndividualResourceImpl();

}