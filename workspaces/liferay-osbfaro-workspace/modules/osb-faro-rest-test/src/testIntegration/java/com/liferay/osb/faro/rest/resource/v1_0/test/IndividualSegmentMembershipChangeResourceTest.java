/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class IndividualSegmentMembershipChangeResourceTest
	extends BaseIndividualSegmentMembershipChangeResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPage()
		throws Exception {

		_testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeEnd();
		_testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeStart();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithPagination()
		throws Exception {

		super.
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortDateTime()
		throws Exception {

		super.
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortDouble()
		throws Exception {

		super.
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortInteger()
		throws Exception {

		super.
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortString()
		throws Exception {

		super.
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortString();
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

	private void _testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeEnd()
		throws Exception {

		_assertBadRequest(
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPageHttpResponse(
					testGroup.getGroupId(), RandomTestUtil.randomString(),
					"2026-01-31", null, null, null, Pagination.of(1, 10),
					null));
	}

	private void _testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithRangeStart()
		throws Exception {

		_assertBadRequest(
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPageHttpResponse(
					testGroup.getGroupId(), RandomTestUtil.randomString(), null,
					null, "2026-01-01", null, Pagination.of(1, 10), null));
	}

}