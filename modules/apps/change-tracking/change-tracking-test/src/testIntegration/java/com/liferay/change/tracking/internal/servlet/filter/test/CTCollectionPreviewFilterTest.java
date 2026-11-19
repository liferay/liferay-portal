/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

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
public class CTCollectionPreviewFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoFilter() throws Exception {
		String documentURL =
			"/documents/d/guest/" + RandomTestUtil.randomString();

		String html = "<img src=\"" + documentURL + "\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"", documentURL,
				"?p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" />"),
			_testDoFilter(
				html, ContentTypes.TEXT_HTML_UTF8,
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				"/web/guest/" + RandomTestUtil.randomString()));

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"", documentURL,
				"?p_l_mode=preview&previewCTCollectionId=",
				ctCollection.getCtCollectionId(), "\" />"),
			_testDoFilter(
				html, ContentTypes.TEXT_HTML_UTF8,
				ctCollection.getCtCollectionId(),
				"/web/guest/" + RandomTestUtil.randomString()));

		Assert.assertEquals(
			html,
			_testDoFilter(
				html, ContentTypes.TEXT_HTML_UTF8,
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				"/documents/d/guest/" + RandomTestUtil.randomString()));

		String json = "{\"src\": \"" + documentURL + "\"}";

		Assert.assertEquals(
			json,
			_testDoFilter(
				json, ContentTypes.APPLICATION_JSON,
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				"/web/guest/" + RandomTestUtil.randomString()));
	}

	@Test
	public void testDoFilterWithForward() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				"/web/guest/" + RandomTestUtil.randomString());

		mockHttpServletRequest.setDispatcherType(DispatcherType.FORWARD);

		String documentURL =
			"/documents/d/guest/" + RandomTestUtil.randomString();

		String expectedHTML = StringBundler.concat(
			"<img src=\"", documentURL,
			"?p_l_mode=preview&previewCTCollectionId=",
			CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" />");
		String html = "<img src=\"" + documentURL + "\" />";

		Assert.assertEquals(
			expectedHTML,
			_testDoFilter(
				(servletRequest, servletResponse) -> _write(
					html, ContentTypes.TEXT_HTML_UTF8, servletResponse),
				mockHttpServletRequest));

		mockHttpServletRequest = _getMockHttpServletRequest(
			CTConstants.CT_COLLECTION_ID_PRODUCTION,
			"/w/" + RandomTestUtil.randomString());

		Assert.assertEquals(
			expectedHTML,
			_testDoFilter(
				(servletRequest, servletResponse) -> _filter.doFilter(
					servletRequest, servletResponse,
					(forwardServletRequest, forwardServletResponse) -> _write(
						html, ContentTypes.TEXT_HTML_UTF8,
						forwardServletResponse)),
				mockHttpServletRequest));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		long previewCTCollectionId, String requestURI) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", requestURI);

		mockHttpServletRequest.addParameter("p_l_mode", Constants.PREVIEW);
		mockHttpServletRequest.addParameter(
			"previewCTCollectionId", String.valueOf(previewCTCollectionId));

		return mockHttpServletRequest;
	}

	private String _testDoFilter(
			FilterChain filterChain,
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_filter.doFilter(
			mockHttpServletRequest, mockHttpServletResponse, filterChain);

		return mockHttpServletResponse.getContentAsString();
	}

	private String _testDoFilter(
			String content, String contentType, long previewCTCollectionId,
			String requestURI)
		throws Exception {

		return _testDoFilter(
			(servletRequest, servletResponse) -> _write(
				content, contentType, servletResponse),
			_getMockHttpServletRequest(previewCTCollectionId, requestURI));
	}

	private void _write(
			String content, String contentType, ServletResponse servletResponse)
		throws IOException {

		servletResponse.setContentType(contentType);

		PrintWriter printWriter = servletResponse.getWriter();

		printWriter.write(content);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject(filter = "servlet-filter-name=CTCollection Preview Filter")
	private Filter _filter;

}