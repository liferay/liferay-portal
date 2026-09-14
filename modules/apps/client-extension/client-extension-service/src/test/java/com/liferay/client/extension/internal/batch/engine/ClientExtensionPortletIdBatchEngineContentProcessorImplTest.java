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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() {
		_companyThreadLocalMockedStatic = Mockito.mockStatic(
			CompanyThreadLocal.class);
		_exportImportThreadLocalMockedStatic = Mockito.mockStatic(
			ExportImportThreadLocal.class);

		_setCompanyId(_TARGET_COMPANY_ID);
		_setImportInProcess(true);
	}

	@After
	public void tearDown() {
		_companyThreadLocalMockedStatic.close();
		_exportImportThreadLocalMockedStatic.close();
	}

	@Test
	public void testProcess() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getPortletId(_TARGET_COMPANY_ID, externalReferenceCode),
			_batchEngineContentProcessor.process(
				_getPortletId(_SOURCE_COMPANY_ID, externalReferenceCode)));
	}

	@Test
	public void testProcessIgnoresContentFromSameCompany() {
		String content = _getPortletId(
			_TARGET_COMPANY_ID, RandomTestUtil.randomString());

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessIgnoresContentWhenCompanyIdIsNotSet() {
		_setCompanyId(0);

		String content = _getPortletId(
			_SOURCE_COMPANY_ID, RandomTestUtil.randomString());

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessIgnoresContentWhenImportIsNotInProcess() {
		_setImportInProcess(false);

		String content = _getPortletId(
			_SOURCE_COMPANY_ID, RandomTestUtil.randomString());

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessIgnoresNullContent() {
		Assert.assertNull(_batchEngineContentProcessor.process(null));
	}

	@Test
	public void testProcessIgnoresUnrelatedPortletIds() {
		String content = "com_liferay_journal_web_portlet_JournalPortlet";

		Assert.assertEquals(
			content, _batchEngineContentProcessor.process(content));
	}

	@Test
	public void testProcessPreservesPortletInstanceSuffix() {
		String externalReferenceCode = RandomTestUtil.randomString();
		String instanceSuffix = "_INSTANCE_" + RandomTestUtil.randomString();

		Assert.assertEquals(
			_getPortletId(
				_TARGET_COMPANY_ID, externalReferenceCode + instanceSuffix),
			_batchEngineContentProcessor.process(
				_getPortletId(
					_SOURCE_COMPANY_ID,
					externalReferenceCode + instanceSuffix)));
	}

	@Test
	public void testProcessRewritesMultipleOccurrences() {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getPageDefinition(
				_getPortletId(_TARGET_COMPANY_ID, externalReferenceCode1),
				_getPortletId(_TARGET_COMPANY_ID, externalReferenceCode2)),
			_batchEngineContentProcessor.process(
				_getPageDefinition(
					_getPortletId(_SOURCE_COMPANY_ID, externalReferenceCode1),
					_getPortletId(
						_SOURCE_COMPANY_ID, externalReferenceCode2))));
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

	private void _setCompanyId(long companyId) {
		_companyThreadLocalMockedStatic.when(
			CompanyThreadLocal::getCompanyId
		).thenReturn(
			companyId
		);
	}

	private void _setImportInProcess(boolean importInProcess) {
		_exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			importInProcess
		);
	}

	private static final String _PORTLET_ID_PREFIX =
		"com_liferay_client_extension_web_internal_portlet_" +
			"ClientExtensionEntryPortlet_";

	private static final long _SOURCE_COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _TARGET_COMPANY_ID = _SOURCE_COMPANY_ID + 1;

	private final BatchEngineContentProcessor _batchEngineContentProcessor =
		new ClientExtensionPortletIdBatchEngineContentProcessorImpl();
	private MockedStatic<CompanyThreadLocal> _companyThreadLocalMockedStatic;
	private MockedStatic<ExportImportThreadLocal>
		_exportImportThreadLocalMockedStatic;

}