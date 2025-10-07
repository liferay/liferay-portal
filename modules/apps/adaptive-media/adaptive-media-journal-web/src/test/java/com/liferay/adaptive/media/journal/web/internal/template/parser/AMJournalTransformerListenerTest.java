/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.journal.web.internal.template.parser;

import com.liferay.adaptive.media.content.transformer.ContentTransformerHandler;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Tardín
 */
public class AMJournalTransformerListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_amJournalTransformerListener, "_contentTransformerHandler",
			_contentTransformerHandler);
		ReflectionTestUtil.setFieldValue(
			_amJournalTransformerListener, "_journalContent", _journalContent);
	}

	@Test
	public void testOnActivationClearsJournalCache() throws Exception {
		_amJournalTransformerListener.activate();

		Mockito.verify(
			_journalContent, Mockito.times(1)
		).clearCache();
	}

	@Test
	public void testOnDeactivationClearsJournalCache() throws Exception {
		_amJournalTransformerListener.deactivate();

		Mockito.verify(
			_journalContent, Mockito.times(1)
		).clearCache();
	}

	@Test
	public void testOnOutputTransformsTheOutput() throws Exception {
		String originalOutput = RandomTestUtil.randomString();
		String transformedOutput = RandomTestUtil.randomString();

		Mockito.when(
			_contentTransformerHandler.transform(originalOutput)
		).thenReturn(
			transformedOutput
		);

		String newOutput = _amJournalTransformerListener.onOutput(
			originalOutput, _LANGUAGE_ID, _tokens);

		Assert.assertSame(transformedOutput, newOutput);
	}

	@Test
	public void testOnScriptDoesNotModifyTheScript() throws Exception {
		String originalScript = RandomTestUtil.randomString();

		String newScript = _amJournalTransformerListener.onScript(
			originalScript, _document, _LANGUAGE_ID, _tokens);

		Assert.assertSame(originalScript, newScript);

		Mockito.verifyNoInteractions(_document);
	}

	@Test
	public void testOnXmlDoesNotModifyTheXml() throws Exception {
		Document newDocument = _amJournalTransformerListener.onXml(
			_document, _LANGUAGE_ID, _tokens);

		Assert.assertSame(_document, newDocument);

		Mockito.verifyNoInteractions(_document);
	}

	private static final String _LANGUAGE_ID = "en";

	private final AMJournalTransformerListener _amJournalTransformerListener =
		new AMJournalTransformerListener();
	private final ContentTransformerHandler _contentTransformerHandler =
		Mockito.mock(ContentTransformerHandler.class);
	private final Document _document = Mockito.mock(Document.class);
	private final JournalContent _journalContent = Mockito.mock(
		JournalContent.class);
	private final Map<String, String> _tokens = new HashMap<>();

}