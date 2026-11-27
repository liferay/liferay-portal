/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.client.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.client.problem.Problem;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualResource;
import com.liferay.osb.faro.rest.resource.v1_0.test.util.ContactsEngineHttpServer;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class IndividualResourceTest extends BaseIndividualResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_contactsEngineHttpServer = new ContactsEngineHttpServer(
			_contactsEngineClient, "/individuals", "individual");
		_faroProject = _faroProjectLocalService.addFaroProject(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), null,
			null, null, null, Collections.emptyList(), null, null, null, null,
			null, FaroProjectConstants.STATE_READY, "{}", "UTC",
			RandomTestUtil.randomString());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_contactsEngineHttpServer.close();
		_faroProjectLocalService.deleteFaroProjectByGroupId(
			_faroProject.getGroupId());

		super.tearDown();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPage() throws Exception {
		_testGetWorkspaceGroupChannelIndividualsPageWithProfileTypeAndSort();
		_testGetWorkspaceGroupChannelIndividualsPageWithoutProfileType();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortDateTime()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortInteger()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortString();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupIndividual() throws Exception {
		super.testGetWorkspaceGroupIndividual();

		_testGetWorkspaceGroupIndividualWithEmptyIndividual();
		_testGetWorkspaceGroupIndividualWithMissingIndividual();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"accountName", "activitiesCount", "activityStatus", "sessionsCount"
		};
	}

	@Override
	protected Individual testGetWorkspaceGroupIndividual_addIndividual()
		throws Exception {

		Individual individual = randomIndividual();

		Date dateCreated = individual.getDateCreated();
		Date dateModified = individual.getDateModified();

		_contactsEngineHttpServer.addJSON(
			individual.getId(),
			JSONUtil.put(
				"accountName", individual.getAccountName()
			).put(
				"activitiesCount", individual.getActivitiesCount()
			).put(
				"activityStatus", individual.getActivityStatus()
			).put(
				"dateCreated", dateCreated.getTime()
			).put(
				"dateModified", dateModified.getTime()
			).put(
				"id", individual.getId()
			).put(
				"sessionsCount", individual.getSessionsCount()
			).toString());

		return individual;
	}

	@Override
	protected Long testGetWorkspaceGroupIndividual_getGroupId()
		throws Exception {

		return _faroProject.getGroupId();
	}

	private Object[] _getWorkspaceGroupChannelIndividualsPage(
			String profileType, Sort[] sorts)
		throws Exception {

		AtomicReference<Object[]> argumentsAtomicReference =
			new AtomicReference<>();

		IndividualResource individualResource =
			ReflectionTestUtil.getFieldValue(this, "_individualResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				individualResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						argumentsAtomicReference.set(arguments);

						return new Results<>();
					}));

		try {
			individualResource.getWorkspaceGroupChannelIndividualsPage(
				_faroProject.getGroupId(), RandomTestUtil.randomString(), null,
				null, null, null, profileType, null, null, null, null,
				Pagination.of(1, 20), sorts);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				individualResource, "_contactsEngineClient",
				contactsEngineClient);
		}

		return argumentsAtomicReference.get();
	}

	private void _testGetWorkspaceGroupChannelIndividualsPageWithProfileTypeAndSort()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelIndividualsPage(
			"KNOWN",
			new Sort[] {new Sort("activitiesCount", Sort.LONG_TYPE, true)});

		Assert.assertEquals(Collections.singletonList("KNOWN"), objects[11]);

		List<OrderByField> orderByFields = (List<OrderByField>)objects[20];

		Assert.assertEquals(orderByFields.toString(), 1, orderByFields.size());

		OrderByField orderByField = orderByFields.get(0);

		Assert.assertEquals("activitiesCount", orderByField.getFieldName());
		Assert.assertEquals(
			OrderByField.OrderBy.desc, orderByField.getOrderBy());
		Assert.assertTrue(orderByField.isSystem());
	}

	private void _testGetWorkspaceGroupChannelIndividualsPageWithoutProfileType()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelIndividualsPage(null, null);

		Assert.assertNull(objects[11]);
		Assert.assertNull(objects[20]);
	}

	private void _testGetWorkspaceGroupIndividualWithEmptyIndividual()
		throws Exception {

		String individualId = RandomTestUtil.randomString();

		_contactsEngineHttpServer.addJSON(individualId, "{}");

		try {
			individualResource.getWorkspaceGroupIndividual(
				_faroProject.getGroupId(), individualId, null);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertEquals(
				"No individual was found with ID " + individualId,
				problem.getTitle());
			Assert.assertNull(problem.getType());
		}
	}

	private void _testGetWorkspaceGroupIndividualWithMissingIndividual()
		throws Exception {

		String individualId = RandomTestUtil.randomString();

		try {
			individualResource.getWorkspaceGroupIndividual(
				_faroProject.getGroupId(), individualId, null);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertEquals(
				"No individual was found with ID " + individualId,
				problem.getTitle());
			Assert.assertNull(problem.getType());
		}
	}

	@Inject
	private ContactsEngineClient _contactsEngineClient;

	private ContactsEngineHttpServer _contactsEngineHttpServer;
	private FaroProject _faroProject;

	@Inject
	private FaroProjectLocalService _faroProjectLocalService;

}