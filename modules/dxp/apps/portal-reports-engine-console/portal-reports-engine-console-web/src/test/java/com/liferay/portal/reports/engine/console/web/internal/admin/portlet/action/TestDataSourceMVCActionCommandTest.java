/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.SourceService;
import com.liferay.portal.reports.engine.console.util.ReportsEngineConsoleUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.After;
import org.junit.Before;
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

	@Before
	public void setUp() {
		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		long sourceId = RandomTestUtil.randomLong();

		String keyReferenceString = _getKeyReferenceString(companyId, sourceId);

		String driverPassword = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			driverPassword
		);

		_testDoProcessAction(
			companyId, keyReferenceString, driverPassword, sourceId);

		keyReferenceString = _getKeyReferenceString(companyId, sourceId + 1);

		_testDoProcessAction(
			companyId, keyReferenceString, keyReferenceString, sourceId);
	}

	private String _getKeyReferenceString(long companyId, long sourceId) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					Source.class.getSimpleName(), StringPool.SLASH, companyId,
					StringPool.SLASH, sourceId),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private void _testDoProcessAction(
			long companyId, String driverPassword,
			String expectedDriverPassword, long sourceId)
		throws Exception {

		ActionRequest actionRequest = Mockito.mock(ActionRequest.class);

		Mockito.when(
			actionRequest.getParameter("sourceId")
		).thenReturn(
			String.valueOf(sourceId)
		);

		Source source = Mockito.mock(Source.class);

		Mockito.when(
			source.getCompanyId()
		).thenReturn(
			companyId
		);

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

		SourceService sourceService = Mockito.mock(SourceService.class);

		Mockito.when(
			sourceService.getSource(sourceId)
		).thenReturn(
			source
		);

		TestDataSourceMVCActionCommand testDataSourceMVCActionCommand =
			new TestDataSourceMVCActionCommand();

		ReflectionTestUtil.setFieldValue(
			testDataSourceMVCActionCommand, "_sourceService", sourceService);

		try (MockedStatic<ReportsEngineConsoleUtil>
				reportsEngineConsoleUtilMockedStatic = Mockito.mockStatic(
					ReportsEngineConsoleUtil.class)) {

			testDataSourceMVCActionCommand.doProcessAction(
				actionRequest, Mockito.mock(ActionResponse.class));

			reportsEngineConsoleUtilMockedStatic.verify(
				() -> ReportsEngineConsoleUtil.validateJDBCConnection(
					null, null, null, expectedDriverPassword));
		}
	}

	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}