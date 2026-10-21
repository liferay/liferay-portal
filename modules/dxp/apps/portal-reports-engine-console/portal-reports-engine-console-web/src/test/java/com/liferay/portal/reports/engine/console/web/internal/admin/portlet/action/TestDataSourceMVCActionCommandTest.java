/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.SourceService;
import com.liferay.portal.reports.engine.console.util.ReportsEngineConsoleUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class TestDataSourceMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDoProcessAction() throws Exception {
		TestDataSourceMVCActionCommand testDataSourceMVCActionCommand =
			new TestDataSourceMVCActionCommand();

		ActionRequest actionRequest = Mockito.mock(ActionRequest.class);

		long sourceId = RandomTestUtil.randomLong();

		Mockito.when(
			actionRequest.getParameter("sourceId")
		).thenReturn(
			String.valueOf(sourceId)
		);

		SourceService sourceService = Mockito.mock(SourceService.class);

		Source source = Mockito.mock(Source.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			source.getCompanyId()
		).thenReturn(
			companyId
		);

		String driverPassword = RandomTestUtil.randomString();

		Mockito.when(
			source.getDriverPassword()
		).thenReturn(
			driverPassword
		);

		Mockito.when(
			source.getSourceId()
		).thenReturn(
			sourceId
		);

		Mockito.when(
			sourceService.getSource(sourceId)
		).thenReturn(
			source
		);

		ReflectionTestUtil.setFieldValue(
			testDataSourceMVCActionCommand, "_sourceService", sourceService);

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		String resolvedDriverPassword = RandomTestUtil.randomString();

		Mockito.when(
			secretResolver.resolve(
				companyId,
				StringBundler.concat(
					Source.class.getSimpleName(), StringPool.SLASH, companyId,
					StringPool.SLASH, sourceId, "/driverPassword"),
				driverPassword)
		).thenReturn(
			resolvedDriverPassword
		);

		ReflectionTestUtil.setFieldValue(
			testDataSourceMVCActionCommand, "_secretResolver", secretResolver);

		try (MockedStatic<ReportsEngineConsoleUtil>
				reportsEngineConsoleUtilMockedStatic = Mockito.mockStatic(
					ReportsEngineConsoleUtil.class)) {

			testDataSourceMVCActionCommand.doProcessAction(
				actionRequest, Mockito.mock(ActionResponse.class));

			reportsEngineConsoleUtilMockedStatic.verify(
				() -> ReportsEngineConsoleUtil.validateJDBCConnection(
					null, null, null, resolvedDriverPassword));
		}
	}

}