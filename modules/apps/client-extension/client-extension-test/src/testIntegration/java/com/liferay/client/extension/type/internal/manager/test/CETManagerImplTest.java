/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.GlobalCSSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Evan Thibodeau
 */
@RunWith(Arquillian.class)
public class CETManagerImplTest {

	@Test
	public void testGetCET() throws PortalException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.client.extension.type.internal.manager." +
					"CETManagerImpl",
				LoggerTestUtil.WARN)) {

			String externalReferenceCode = RandomTestUtil.randomString();

			Assert.assertNull(
				_cetManager.getCET(
					TestPropsValues.getCompanyId(), externalReferenceCode));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"No CET found for external reference code " +
					externalReferenceCode,
				logEntry.getMessage());
		}
	}

	@Test
	public void testGetCETIsCached() throws Exception {
		ClientExtensionEntry clientExtensionEntry = _addClientExtensionEntry(
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
			"http://example.com/a.css");

		CET cet1 = _cetManager.getCET(
			TestPropsValues.getCompanyId(),
			clientExtensionEntry.getExternalReferenceCode());

		CET cet2 = _cetManager.getCET(
			TestPropsValues.getCompanyId(),
			clientExtensionEntry.getExternalReferenceCode());

		Assert.assertNotNull(cet1);
		Assert.assertSame(cet1, cet2);

		GlobalCSSCET globalCSSCET = (GlobalCSSCET)cet1;

		Assert.assertEquals("http://example.com/a.css", globalCSSCET.getURL());
	}

	@Test
	public void testGetCETIsRebuiltAfterUpdate() throws Exception {
		ClientExtensionEntry clientExtensionEntry = _addClientExtensionEntry(
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
			"http://example.com/a.css");

		CET cet1 = _cetManager.getCET(
			TestPropsValues.getCompanyId(),
			clientExtensionEntry.getExternalReferenceCode());

		clientExtensionEntry.setTypeSettings(
			UnicodePropertiesBuilder.create(
				true
			).put(
				"url", "http://example.com/b.css"
			).buildString());

		clientExtensionEntry =
			_clientExtensionEntryLocalService.updateClientExtensionEntry(
				clientExtensionEntry);

		CET cet2 = _cetManager.getCET(
			TestPropsValues.getCompanyId(),
			clientExtensionEntry.getExternalReferenceCode());

		Assert.assertNotSame(cet1, cet2);

		GlobalCSSCET globalCSSCET = (GlobalCSSCET)cet2;

		Assert.assertEquals("http://example.com/b.css", globalCSSCET.getURL());
	}

	@Test
	public void testGetCETsReturnsOnlyRequestedType() throws Exception {
		ClientExtensionEntry globalCSSClientExtensionEntry =
			_addClientExtensionEntry(
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
				"http://example.com/a.css");
		ClientExtensionEntry globalJSClientExtensionEntry =
			_addClientExtensionEntry(
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				"http://example.com/a.js");

		List<CET> cets = _cetManager.getCETs(
			TestPropsValues.getCompanyId(), null,
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
			Pagination.of(1, 200), null);

		boolean containsGlobalCSS = false;

		for (CET cet : cets) {
			Assert.assertEquals(
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS, cet.getType());
			Assert.assertNotEquals(
				globalJSClientExtensionEntry.getExternalReferenceCode(),
				cet.getExternalReferenceCode());

			if (Objects.equals(
					globalCSSClientExtensionEntry.getExternalReferenceCode(),
					cet.getExternalReferenceCode())) {

				containsGlobalCSS = true;
			}
		}

		Assert.assertTrue(containsGlobalCSS);
	}

	@Rule
	public final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	private ClientExtensionEntry _addClientExtensionEntry(
			String type, String url)
		throws Exception {

		return _clientExtensionEntryLocalService.addClientExtensionEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			StringPool.BLANK, StringPool.BLANK, type,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"url", url
			).buildString());
	}

	@Inject
	private CETManager _cetManager;

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

}