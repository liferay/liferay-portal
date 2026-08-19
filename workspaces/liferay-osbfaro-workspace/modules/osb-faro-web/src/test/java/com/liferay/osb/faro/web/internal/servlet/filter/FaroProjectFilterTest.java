/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet.filter;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Thiago Buarque
 */
public class FaroProjectFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtils.setField(
			_faroProjectFilter, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtils.setField(
			_faroProjectFilter, "_groupLocalService", _groupLocalService);
		ReflectionTestUtils.setField(
			_faroProjectFilter, "_jsonFactory", _jsonFactory);
		ReflectionTestUtils.setField(_faroProjectFilter, "_portal", _portal);

		Mockito.when(
			_portal.getDefaultCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	@Test
	public void testProcessFilterWithIndividualsLimitExceeded()
		throws Exception {

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			"{}"
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(123L)
		).thenReturn(
			faroProject
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			123L
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq("/123"))
		).thenReturn(
			group
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/workspace/123"
		);

		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.when(
			jsonObject.getLong("individualsCountSinceLastAnniversary")
		).thenReturn(
			1500L
		);

		Mockito.when(
			jsonObject.getLong("individualsLimit")
		).thenReturn(
			1000L
		);

		Mockito.when(
			jsonObject.getString("name")
		).thenReturn(
			ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			jsonObject
		);

		_faroProjectFilter.processFilter(
			_httpServletRequest, _httpServletResponse, _filterChain);

		Mockito.verify(
			_httpServletResponse
		).sendRedirect(
			StringPool.FORWARD_SLASH
		);

		Mockito.verifyNoInteractions(_filterChain);
	}

	@Test
	public void testProcessFilterWithLimitsNotExceeded() throws Exception {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			"{}"
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(123L)
		).thenReturn(
			faroProject
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			123L
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq("/123"))
		).thenReturn(
			group
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/workspace/123"
		);

		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.when(
			jsonObject.getLong("individualsCountSinceLastAnniversary")
		).thenReturn(
			500L
		);

		Mockito.when(
			jsonObject.getLong("individualsLimit")
		).thenReturn(
			1000L
		);

		Mockito.when(
			jsonObject.getString("name")
		).thenReturn(
			ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA
		);

		Mockito.when(
			jsonObject.getLong("pageViewsCountSinceLastAnniversary")
		).thenReturn(
			5000L
		);

		Mockito.when(
			jsonObject.getLong("pageViewsLimit")
		).thenReturn(
			10000L
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			jsonObject
		);

		_faroProjectFilter.processFilter(
			_httpServletRequest, _httpServletResponse, _filterChain);

		Mockito.verify(
			_filterChain
		).doFilter(
			_httpServletRequest, _httpServletResponse
		);
	}

	@Test
	public void testProcessFilterWithNoProjectFound() throws Exception {
		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(123L)
		).thenReturn(
			null
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq("/123"))
		).thenReturn(
			null
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/workspace/123"
		);

		_faroProjectFilter.processFilter(
			_httpServletRequest, _httpServletResponse, _filterChain);

		Mockito.verify(
			_httpServletResponse
		).sendRedirect(
			StringPool.FORWARD_SLASH
		);

		Mockito.verifyNoInteractions(_filterChain);
	}

	@Test
	public void testProcessFilterWithoutDataPlatformProject() throws Exception {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			"{}"
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(123L)
		).thenReturn(
			faroProject
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			123L
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq("/123"))
		).thenReturn(
			group
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/workspace/123"
		);

		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.when(
			jsonObject.getString("name")
		).thenReturn(
			ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE_CONTACTS
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			jsonObject
		);

		_faroProjectFilter.processFilter(
			_httpServletRequest, _httpServletResponse, _filterChain);

		Mockito.verify(
			_filterChain
		).doFilter(
			_httpServletRequest, _httpServletResponse
		);
	}

	@Test
	public void testProcessFilterWithPageViewsLimitExceeded() throws Exception {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			"{}"
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(123L)
		).thenReturn(
			faroProject
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			123L
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq("/123"))
		).thenReturn(
			group
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/workspace/123"
		);

		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.when(
			jsonObject.getLong("individualsCountSinceLastAnniversary")
		).thenReturn(
			500L
		);

		Mockito.when(
			jsonObject.getLong("individualsLimit")
		).thenReturn(
			1000L
		);

		Mockito.when(
			jsonObject.getString("name")
		).thenReturn(
			ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA
		);

		Mockito.when(
			jsonObject.getLong("pageViewsCountSinceLastAnniversary")
		).thenReturn(
			15000L
		);

		Mockito.when(
			jsonObject.getLong("pageViewsLimit")
		).thenReturn(
			10000L
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			jsonObject
		);

		_faroProjectFilter.processFilter(
			_httpServletRequest, _httpServletResponse, _filterChain);

		Mockito.verify(
			_httpServletResponse
		).sendRedirect(
			StringPool.FORWARD_SLASH
		);

		Mockito.verifyNoInteractions(_filterChain);
	}

	@Test
	public void testProcessFilterWithRedirectParameter() throws Exception {
		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(456L)
		).thenReturn(
			null
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq("/456"))
		).thenReturn(
			null
		);

		Mockito.when(
			_httpServletRequest.getParameter("redirect")
		).thenReturn(
			"/workspace/456"
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/web/guest/home"
		);

		_faroProjectFilter.processFilter(
			_httpServletRequest, _httpServletResponse, _filterChain);

		Mockito.verify(
			_httpServletResponse
		).sendRedirect(
			StringPool.FORWARD_SLASH
		);
	}

	private final FaroProjectFilter _faroProjectFilter =
		new FaroProjectFilter();

	@Mock
	private FaroProjectLocalService _faroProjectLocalService;

	@Mock
	private FilterChain _filterChain;

	@Mock
	private GroupLocalService _groupLocalService;

	@Mock
	private HttpServletRequest _httpServletRequest;

	@Mock
	private HttpServletResponse _httpServletResponse;

	@Mock
	private JSONFactory _jsonFactory;

	@Mock
	private Portal _portal;

}