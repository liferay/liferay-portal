/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.on.demand.user.ticket.generator.CTOnDemandUserTicketGenerator;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTReviewChangesServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDoGet() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		ctCollection.setShareable(true);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		Ticket ticket = _ctOnDemandUserTicketGenerator.generate(
			ctCollection.getCtCollectionId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", RandomTestUtil.randomString());

		mockHttpServletRequest.addParameter("ticketKey", ticket.getKey());
		mockHttpServletRequest.setServerName("www.example.com");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_FOUND, mockHttpServletResponse.getStatus());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(
			redirectedURL.startsWith(
				"http://www.example.com/group/control_panel/manage?"));
		Assert.assertTrue(
			redirectedURL.contains("ticketKey=" + ticket.getKey()));
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTOnDemandUserTicketGenerator _ctOnDemandUserTicketGenerator;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.change.tracking.web.internal.servlet.CTReviewChangesServlet"
	)
	private Servlet _servlet;

}