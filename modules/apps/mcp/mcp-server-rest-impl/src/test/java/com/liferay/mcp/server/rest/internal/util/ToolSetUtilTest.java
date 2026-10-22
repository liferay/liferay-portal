/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.liferay.mcp.server.rest.internal.search.exception.ToolSearchUnavailableException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class ToolSetUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		Snapshot<HeadlessApplicationProvider>
			headlessApplicationProviderSnapshot = Mockito.mock(Snapshot.class);

		HeadlessApplicationProvider headlessApplicationProvider = Mockito.mock(
			HeadlessApplicationProvider.class);

		List<HeadlessApplicationProvider.Application> applications =
			Arrays.asList(
				_mockApplication("/headless-admin-user", "v1.0"),
				_mockApplication("/headless-admin-user-workflow", "v1.0"),
				_mockApplication(
					"/headless-commerce-admin-pricing", "v1.0", "v2.0",
					"v10.0"));

		Mockito.when(
			headlessApplicationProvider.getApplications(_COMPANY_ID)
		).thenReturn(
			applications
		);

		Mockito.when(
			headlessApplicationProviderSnapshot.get()
		).thenReturn(
			headlessApplicationProvider
		);

		ReflectionTestUtil.setFieldValue(
			ToolSetUtil.class, "_headlessApplicationProviderSnapshot",
			headlessApplicationProviderSnapshot);
	}

	@Test
	public void testGetLatestOpenAPIDocuments() {
		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = ToolSetUtil.getLatestOpenAPIDocuments(
				_COMPANY_ID);

		Assert.assertEquals(
			Arrays.asList(
				"headless-admin-user-v1.0", "headless-admin-user-workflow-v1.0",
				"headless-commerce-admin-pricing-v10.0"),
			new ArrayList<>(openAPIDocuments.keySet()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetToolSearchResultsPageWhenSearchIsBlank() {
		_mockSearchCapabilities(true);

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			ToolSetUtil.getToolSearchResultsPage(
				null, _httpServletRequest, false, null, StringPool.BLANK);
		}
	}

	@Test
	public void testGetToolSearchResultsPageWhenToolSearchIsUnsupported() {
		_mockSearchCapabilities(false);

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			ToolSetUtil.getToolSearchResultsPage(
				null, _httpServletRequest, false, null,
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (ToolSearchUnavailableException toolSearchUnavailableException) {
			String message = toolSearchUnavailableException.getMessage();

			Assert.assertTrue(message.contains("Elasticsearch or OpenSearch"));
		}
	}

	@Test
	public void testGetToolSetName() {
		Assert.assertNull(
			ToolSetUtil.getToolSetName(_COMPANY_ID, "/headless-admin"));
		Assert.assertEquals(
			"headless-admin-user-v1.0",
			ToolSetUtil.getToolSetName(_COMPANY_ID, "/headless-admin-user"));
		Assert.assertEquals(
			"headless-admin-user-workflow-v1.0",
			ToolSetUtil.getToolSetName(
				_COMPANY_ID, "/headless-admin-user-workflow/v1.0/tasks"));
		Assert.assertEquals(
			"headless-admin-user-v1.0",
			ToolSetUtil.getToolSetName(
				_COMPANY_ID, "/headless-admin-user/v1.0/user-accounts"));
		Assert.assertNull(ToolSetUtil.getToolSetName(_COMPANY_ID, null));
	}

	private HeadlessApplicationProvider.Application _mockApplication(
		String basePath, String... versions) {

		HeadlessApplicationProvider.Application application = Mockito.mock(
			HeadlessApplicationProvider.Application.class);

		Mockito.when(
			application.getBasePath()
		).thenReturn(
			basePath
		);

		List<HeadlessApplicationProvider.OpenAPIDocument> openAPIDocuments =
			new ArrayList<>();

		for (String version : versions) {
			HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
				Mockito.mock(HeadlessApplicationProvider.OpenAPIDocument.class);

			Mockito.when(
				openAPIDocument.getApplication()
			).thenReturn(
				application
			);

			Mockito.when(
				openAPIDocument.getVersion()
			).thenReturn(
				version
			);

			openAPIDocuments.add(openAPIDocument);
		}

		Mockito.when(
			application.getOpenAPIDocuments()
		).thenReturn(
			openAPIDocuments
		);

		return application;
	}

	private void _mockSearchCapabilities(boolean mcpToolSearchSupported) {
		Snapshot<SearchCapabilities> searchCapabilitiesSnapshot = Mockito.mock(
			Snapshot.class);

		SearchCapabilities searchCapabilities = Mockito.mock(
			SearchCapabilities.class);

		Mockito.when(
			searchCapabilities.isMCPToolSearchSupported()
		).thenReturn(
			mcpToolSearchSupported
		);

		Mockito.when(
			searchCapabilitiesSnapshot.get()
		).thenReturn(
			searchCapabilities
		);

		ReflectionTestUtil.setFieldValue(
			ToolSetUtil.class, "_searchCapabilitiesSnapshot",
			searchCapabilitiesSnapshot);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);

}