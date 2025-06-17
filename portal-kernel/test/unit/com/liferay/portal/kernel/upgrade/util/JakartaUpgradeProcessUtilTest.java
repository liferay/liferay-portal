/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.util;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luis Ortiz
 */
public class JakartaUpgradeProcessUtilTest {

	@Test
	public void testReplace() {
		_testReplace(
			HashMapBuilder.put(
				"javax-batch-operations", "jakarta-batch-operations"
			).put(
				"javax.portlet.Portlet", "jakarta.portlet.Portlet"
			).put(
				"javax/persistence/cache", "jakarta/persistence/cache"
			).build());
	}

	@Test
	public void testReplaceNullValue() {
		Assert.assertNull(JakartaUpgradeProcessUtil.replace(null));
	}

	@Test
	public void testReplaceWithCustomSeparators() {
		_testReplace(
			HashMapBuilder.put(
				"javax$persistence$cache", "jakarta$persistence$cache"
			).put(
				"javax.portlet.Portlet", "jakarta.portlet.Portlet"
			).put(
				"javax@batch@operations", "jakarta@batch@operations"
			).build(),
			new char[] {'@', '$'});
	}

	@Test
	public void testReplaceWithJSCode() {
		_testReplace(
			HashMapBuilder.put(
				HtmlUtil.escapeJS(
					"onclick=openWindow(\"javax.portlet.Action\")"),
				HtmlUtil.escapeJS(
					"onclick=openWindow(\"jakarta.portlet.Action\")")
			).build());
	}

	@Test
	public void testReplaceWithMultipleSubpackages() {
		_testReplace(
			HashMapBuilder.put(
				"import javax.portlet.Portlet;\nimport javax.batch.operations;",
				"import jakarta.portlet.Portlet;\nimport " +
					"jakarta.batch.operations;"
			).build());
	}

	@Test
	public void testReplaceWithMultipleSubpackagesAndCustomSeparator() {
		_testReplace(
			HashMapBuilder.put(
				"import javax@portlet@Portlet;\nimport javax$batch$operations;",
				"import jakarta@portlet@Portlet;\nimport " +
					"jakarta$batch$operations;"
			).build(),
			new char[] {'@', '$'});
	}

	@Test
	public void testReplaceWithNoMatch() {
		_testReplace(
			HashMapBuilder.put(
				"com.liferay.portal.kernel.util.StringUtil",
				"com.liferay.portal.kernel.util.StringUtil"
			).build());
	}

	@Test
	public void testReplaceWithNoMatchAndCustomSeparator() {
		_testReplace(
			HashMapBuilder.put(
				"com@liferay@portal@kernel@util@StringUtil",
				"com@liferay@portal@kernel@util@StringUtil"
			).put(
				"javax$activity$ActivityCompletedException",
				"javax$activity$ActivityCompletedException"
			).build(),
			new char[] {'@', '$'});
	}

	@Test
	public void testReplaceWithPreservedSubpackage() {
		_testReplace(
			HashMapBuilder.put(
				"javax-transaction-xa-XAResource",
				"javax-transaction-xa-XAResource"
			).put(
				"javax.annotation.processing.Processor",
				"javax.annotation.processing.Processor"
			).build());
	}

	@Test
	public void testReplaceWithPreservedSubpackageAndCustomSeparator() {
		_testReplace(
			HashMapBuilder.put(
				"javax$transaction$xa$XAResource",
				"javax$transaction$xa$XAResource"
			).put(
				"javax@annotation@processing@Processor",
				"javax@annotation@processing@Processor"
			).build(),
			new char[] {'@', '$'});
	}

	@Test
	public void testReplaceWithXJavaxPortletNamespacedResponse() {
		_testReplace(
			HashMapBuilder.put(
				"X-JAVAX-PORTLET-NAMESPACED-RESPONSE",
				"X-JAKARTA-PORTLET-NAMESPACED-RESPONSE"
			).build());
	}

	private void _testReplace(Map<String, String> replacements) {
		for (Map.Entry<String, String> entry : replacements.entrySet()) {
			Assert.assertEquals(
				entry.getValue(),
				JakartaUpgradeProcessUtil.replace(entry.getKey()));
		}
	}

	private void _testReplace(
		Map<String, String> replacements, char[] customSeparators) {

		for (Map.Entry<String, String> entry : replacements.entrySet()) {
			Assert.assertEquals(
				entry.getValue(),
				JakartaUpgradeProcessUtil.replace(
					entry.getKey(), customSeparators));
		}
	}

}