/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineContentProcessor;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class ClientExtensionPortletIdBatchEngineContentProcessorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testProcess() {
		long sourceCompanyId = RandomTestUtil.randomLong(1, Long.MAX_VALUE - 1);

		long targetCompanyId = sourceCompanyId + 1;

		_mockImportProcess(targetCompanyId, true);

		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getPortletId(targetCompanyId, externalReferenceCode),
			_batchEngineContentProcessor.process(
				_getPortletId(sourceCompanyId, externalReferenceCode)));
	}

	@Test
	public void testProcessIgnoresContentFromSameCompany() {
		long companyId = RandomTestUtil.randomLong();

		_mockImportProcess(companyId, true);

		String content = _getPortletId(
			companyId, RandomTestUtil.randomString());

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessIgnoresContentWhenCompanyIdIsNotSet() {
		_mockImportProcess(0, true);

		String content = _getPortletId(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessIgnoresContentWhenImportInProcessIsNotSet() {
		long sourceCompanyId = RandomTestUtil.randomLong(1, Long.MAX_VALUE - 1);

		_mockImportProcess(sourceCompanyId + 1, false);

		String content = _getPortletId(
			sourceCompanyId, RandomTestUtil.randomString());

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessIgnoresNullContent() {
		_mockImportProcess(RandomTestUtil.randomLong(), true);

		Assert.assertNull(_batchEngineContentProcessor.process(null));
	}

	@Test
	public void testProcessIgnoresUnrelatedPortletIds() {
		_mockImportProcess(RandomTestUtil.randomLong(), true);

		String content = "com_liferay_journal_web_portlet_JournalPortlet";

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessPreservesPortletInstanceSuffix() {
		long sourceCompanyId = RandomTestUtil.randomLong(1, Long.MAX_VALUE - 1);

		long targetCompanyId = sourceCompanyId + 1;

		_mockImportProcess(targetCompanyId, true);

		String externalReferenceCode = RandomTestUtil.randomString();
		String instanceSuffix = "_INSTANCE_" + RandomTestUtil.randomString();

		Assert.assertEquals(
			_getPortletId(
				targetCompanyId, externalReferenceCode + instanceSuffix),
			_batchEngineContentProcessor.process(
				_getPortletId(
					sourceCompanyId, externalReferenceCode + instanceSuffix)));
	}

	@Test
	public void testProcessRewritesMultipleOccurrences() {
		long sourceCompanyId = RandomTestUtil.randomLong(1, Long.MAX_VALUE - 1);

		long targetCompanyId = sourceCompanyId + 1;

		_mockImportProcess(targetCompanyId, true);

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getPageDefinition(
				_getPortletId(targetCompanyId, externalReferenceCode1),
				_getPortletId(targetCompanyId, externalReferenceCode2)),
			_batchEngineContentProcessor.process(
				_getPageDefinition(
					_getPortletId(sourceCompanyId, externalReferenceCode1),
					_getPortletId(sourceCompanyId, externalReferenceCode2))));
	}

	private String _getPageDefinition(
		String firstPortletId, String secondPortletId) {

		return StringBundler.concat(
			"{\"widgetName\": \"", firstPortletId, "\", \"other\": \"",
			secondPortletId, "\"}");
	}

	private String _getPortletId(long companyId, String suffix) {
		return StringBundler.concat(
			_PORTLET_ID_PREFIX, companyId, StringPool.UNDERLINE, suffix);
	}

	private void _mockImportProcess(long companyId, boolean importInProcess) {
		_companyThreadLocalMockedStatic.when(
			CompanyThreadLocal::getCompanyId
		).thenReturn(
			companyId
		);

		_exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			importInProcess
		);
	}

	private static final String _PORTLET_ID_PREFIX =
		"com_liferay_client_extension_web_internal_portlet_" +
			"ClientExtensionEntryPortlet_";

	private final BatchEngineContentProcessor _batchEngineContentProcessor =
		new ClientExtensionPortletIdBatchEngineContentProcessorImpl();
	private final MockedStatic<CompanyThreadLocal>
		_companyThreadLocalMockedStatic = Mockito.mockStatic(
			CompanyThreadLocal.class);
	private final MockedStatic<ExportImportThreadLocal>
		_exportImportThreadLocalMockedStatic = Mockito.mockStatic(
			ExportImportThreadLocal.class);

}