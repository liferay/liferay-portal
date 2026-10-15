/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipChangeResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@Ignore
@RunWith(Arquillian.class)
public class IndividualSegmentMembershipChangeResourceTest
	extends BaseIndividualSegmentMembershipChangeResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPage()
		throws Exception {

		_testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeEnd();
		_testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeEndAndRangeStart();
		_testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeStart();
		_testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithoutRange();
	}

	private void _assertBadRequest(HttpInvoker.HttpResponse httpResponse)
		throws Exception {

		assertHttpResponseStatusCode(400, httpResponse);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			httpResponse.getContent());

		Assert.assertEquals(
			"\"rangeEnd\" and \"rangeStart\" must be set together",
			jsonObject.getString("title"));
	}

	private Object[] _getIndividualSegmentMembershipChangesArguments(
			String rangeEnd, String rangeStart)
		throws Exception {

		AtomicReference<Object[]> argumentsAtomicReference =
			new AtomicReference<>();

		IndividualSegmentMembershipChangeResource
			individualSegmentMembershipChangeResource =
				ReflectionTestUtil.getFieldValue(
					this, "_individualSegmentMembershipChangeResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				individualSegmentMembershipChangeResource,
				"_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						argumentsAtomicReference.set(arguments);

						return new Results<>();
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				individualSegmentMembershipChangeResource,
				"_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPage(
					testGroup.getGroupId(), RandomTestUtil.randomString(), null,
					rangeEnd, null, rangeStart, null, null, null);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				individualSegmentMembershipChangeResource,
				"_contactsEngineClient", contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				individualSegmentMembershipChangeResource,
				"_faroProjectLocalService", faroProjectLocalService);
		}

		return argumentsAtomicReference.get();
	}

	private void _testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeEnd()
		throws Exception {

		_assertBadRequest(
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPageHttpResponse(
					testGroup.getGroupId(), RandomTestUtil.randomString(), null,
					"2026-01-31", null, null, null, Pagination.of(1, 10),
					null));
	}

	private void _testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeEndAndRangeStart()
		throws Exception {

		String rangeEnd = "2026-01-31";
		String rangeStart = "2026-01-01";

		Object[] arguments = _getIndividualSegmentMembershipChangesArguments(
			rangeEnd, rangeStart);

		Assert.assertEquals(_toDate(LocalDate.parse(rangeEnd)), arguments[4]);
		Assert.assertEquals(_toDate(LocalDate.parse(rangeStart)), arguments[3]);
	}

	private void _testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeStart()
		throws Exception {

		_assertBadRequest(
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPageHttpResponse(
					testGroup.getGroupId(), RandomTestUtil.randomString(), null,
					null, null, "2026-01-01", null, Pagination.of(1, 10),
					null));
	}

	private void _testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithoutRange()
		throws Exception {

		Object[] arguments = _getIndividualSegmentMembershipChangesArguments(
			null, null);

		LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

		Assert.assertEquals(_toDate(localDate), arguments[4]);
		Assert.assertEquals(_toDate(localDate.minusDays(30)), arguments[3]);
	}

	private Date _toDate(LocalDate localDate) {
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC);

		return Date.from(zonedDateTime.toInstant());
	}

}