/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.links;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jürgen Kappler
 */
public class OutboundLinksUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetObjectEntryExternalReferenceCodes() {
		_testGetObjectEntryExternalReferenceCodesWithBlankContent();
		_testGetObjectEntryExternalReferenceCodesWithEmbeddedDocument();
		_testGetObjectEntryExternalReferenceCodesWithEmbeddedVideo();
		_testGetObjectEntryExternalReferenceCodesWithEncodedValue();
		_testGetObjectEntryExternalReferenceCodesWithMalformedValue();
		_testGetObjectEntryExternalReferenceCodesWithMultipleReferences();
		_testGetObjectEntryExternalReferenceCodesWithUnescapedQueryString();
		_testGetObjectEntryExternalReferenceCodesWithoutReferences();
	}

	private String _getDocumentURL(String objectEntryExternalReferenceCode) {
		return StringBundler.concat(
			"/documents/1/2/document.jpg/3?version=1.0&amp;",
			"download=true&amp;groupExternalReferenceCode=4&amp;",
			"objectDefinitionExternalReferenceCode=L_CMS_BASIC_DOCUMENT&amp;",
			"objectEntryExternalReferenceCode=",
			objectEntryExternalReferenceCode,
			"&amp;objectFieldExternalReferenceCode=FILE");
	}

	private void _testGetObjectEntryExternalReferenceCodesWithBlankContent() {
		Assert.assertEquals(
			Collections.emptySet(),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(null));
		Assert.assertEquals(
			Collections.emptySet(),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(""));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithEmbeddedDocument() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			Collections.singleton(externalReferenceCode),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				StringBundler.concat(
					"<p><img src=\"", _getDocumentURL(externalReferenceCode),
					"\"></p>")));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithEmbeddedVideo() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			Collections.singleton(externalReferenceCode),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				StringBundler.concat(
					"<figure class=\"media\"><div data-oembed-url=\"https://",
					_getDocumentURL(externalReferenceCode),
					"\"><video controls=\"\" src=\"",
					_getDocumentURL(externalReferenceCode),
					"\"></video></div></figure>")));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithEncodedValue() {
		Assert.assertEquals(
			Collections.singleton("a b"),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				"<img src=\"" + _getDocumentURL("a%20b") + "\">"));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithMalformedValue() {
		Assert.assertEquals(
			Collections.singleton("a%"),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				"<img src=\"" + _getDocumentURL("a%") + "\">"));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithMultipleReferences() {
		String externalReferenceCode = RandomTestUtil.randomString();
		String otherExternalReferenceCode = RandomTestUtil.randomString();

		Set<String> objectEntryExternalReferenceCodes =
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				StringBundler.concat(
					"<img src=\"", _getDocumentURL(externalReferenceCode),
					"\"><img src=\"",
					_getDocumentURL(otherExternalReferenceCode), "\">"));

		Assert.assertEquals(
			objectEntryExternalReferenceCodes.toString(), 2,
			objectEntryExternalReferenceCodes.size());
		Assert.assertTrue(
			objectEntryExternalReferenceCodes.toString(),
			objectEntryExternalReferenceCodes.contains(externalReferenceCode));
		Assert.assertTrue(
			objectEntryExternalReferenceCodes.toString(),
			objectEntryExternalReferenceCodes.contains(
				otherExternalReferenceCode));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithoutReferences() {
		Assert.assertEquals(
			Collections.emptySet(),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				StringBundler.concat(
					"<p>Some rich text</p><p><a href=\"www.claude.com\">",
					"www.claude.com</a></p><p>",
					"<a href=\"/web/guest/home\">A page</a></p>")));
	}

	private void _testGetObjectEntryExternalReferenceCodesWithUnescapedQueryString() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			Collections.singleton(externalReferenceCode),
			OutboundLinksUtil.getObjectEntryExternalReferenceCodes(
				StringBundler.concat(
					"/documents/1/2?objectEntryExternalReferenceCode=",
					externalReferenceCode,
					"&objectFieldExternalReferenceCode=FILE")));
	}

}