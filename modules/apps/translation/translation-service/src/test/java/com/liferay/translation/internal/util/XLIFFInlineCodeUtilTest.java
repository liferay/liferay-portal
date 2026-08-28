/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import net.sf.okapi.common.filterwriter.XLIFFContent;
import net.sf.okapi.common.resource.TextFragment;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Akhash Ramprakash
 */
public class XLIFFInlineCodeUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRenumberCodesAssignsFreshIdsToUnmatchedTargetCodes() {
		TextFragment sourceTextFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<p>Hello <b>world</b> &amp; more</p>");
		TextFragment targetTextFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<p>Hola &amp; mas</p><br/>");

		int startId = XLIFFInlineCodeUtil.renumberCodes(
			1, sourceTextFragment, targetTextFragment);

		Assert.assertEquals(5, startId);

		Assert.assertEquals(
			StringBundler.concat(
				"<bpt id=\"1\">&lt;p&gt;</bpt>Hola <ph id=\"3\">&amp;amp;",
				"</ph> mas<ept id=\"1\">&lt;/p&gt;</ept><ph id=\"4\">",
				"&lt;br/&gt;</ph>"),
			_toXLIFF12(targetTextFragment));
	}

	@Test
	public void testRenumberCodesReservesDisjointRangesPerValue() {
		TextFragment sourceTextFragment1 = XLIFFInlineCodeUtil.toTextFragment(
			"<p>one</p>");
		TextFragment targetTextFragment1 = XLIFFInlineCodeUtil.toTextFragment(
			"<p>uno</p>");

		int startId = XLIFFInlineCodeUtil.renumberCodes(
			1, sourceTextFragment1, targetTextFragment1);

		Assert.assertEquals(2, startId);

		TextFragment sourceTextFragment2 = XLIFFInlineCodeUtil.toTextFragment(
			"<p>two</p><br/>");
		TextFragment targetTextFragment2 = XLIFFInlineCodeUtil.toTextFragment(
			"<p>dos</p><br/>");

		startId = XLIFFInlineCodeUtil.renumberCodes(
			startId, sourceTextFragment2, targetTextFragment2);

		Assert.assertEquals(4, startId);

		Assert.assertEquals(
			"<bpt id=\"1\">&lt;p&gt;</bpt>uno<ept id=\"1\">&lt;/p&gt;</ept>",
			_toXLIFF12(targetTextFragment1));
		Assert.assertEquals(
			StringBundler.concat(
				"<bpt id=\"2\">&lt;p&gt;</bpt>dos",
				"<ept id=\"2\">&lt;/p&gt;</ept>",
				"<ph id=\"3\">&lt;br/&gt;</ph>"),
			_toXLIFF12(targetTextFragment2));
	}

	@Test
	public void testRenumberCodesShareIdsBetweenSourceAndTarget() {
		TextFragment sourceTextFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<p class=\"intro\">Hello <b>world</b> &amp; more</p>");
		TextFragment targetTextFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<p class=\"intro\">Hola <b>mundo</b> &amp; mas</p>");

		XLIFFInlineCodeUtil.renumberCodes(
			1, sourceTextFragment, targetTextFragment);

		Assert.assertEquals(
			StringBundler.concat(
				"<bpt id=\"1\">&lt;p class=&quot;intro&quot;&gt;</bpt>Hello ",
				"<bpt id=\"2\">&lt;b&gt;</bpt>world<ept id=\"2\">&lt;/b&gt;",
				"</ept> <ph id=\"3\">&amp;amp;</ph> more<ept id=\"1\">",
				"&lt;/p&gt;</ept>"),
			_toXLIFF12(sourceTextFragment));
		Assert.assertEquals(
			StringBundler.concat(
				"<bpt id=\"1\">&lt;p class=&quot;intro&quot;&gt;</bpt>Hola ",
				"<bpt id=\"2\">&lt;b&gt;</bpt>mundo<ept id=\"2\">&lt;/b&gt;",
				"</ept> <ph id=\"3\">&amp;amp;</ph> mas<ept id=\"1\">",
				"&lt;/p&gt;</ept>"),
			_toXLIFF12(targetTextFragment));
	}

	@Test
	public void testToTextFragmentIsolatesOverlappingTags() {
		TextFragment textFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<b><i></b></i>");

		XLIFFInlineCodeUtil.renumberCodes(1, textFragment, new TextFragment());

		Assert.assertEquals(
			StringBundler.concat(
				"<it id=\"1\" pos=\"open\">&lt;b&gt;</it>",
				"<it id=\"2\" pos=\"open\">&lt;i&gt;</it>",
				"<it id=\"1\" pos=\"close\">&lt;/b&gt;</it>",
				"<it id=\"2\" pos=\"close\">&lt;/i&gt;</it>"),
			_toXLIFF12(textFragment));
	}

	@Test
	public void testToTextFragmentIsolatesUnclosedTag() {
		TextFragment textFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<em>unclosed");

		XLIFFInlineCodeUtil.renumberCodes(1, textFragment, new TextFragment());

		Assert.assertEquals(
			"<it id=\"1\" pos=\"open\">&lt;em&gt;</it>unclosed",
			_toXLIFF12(textFragment));
	}

	@Test
	public void testToTextFragmentProtectsScriptBlock() {
		TextFragment textFragment = XLIFFInlineCodeUtil.toTextFragment(
			"<script>console.log(\"protect\");</script>text");

		XLIFFInlineCodeUtil.renumberCodes(1, textFragment, new TextFragment());

		Assert.assertEquals(
			"<ph id=\"1\">&lt;script&gt;console.log(&quot;protect&quot;);" +
				"&lt;/script&gt;</ph>text",
			_toXLIFF12(textFragment));
	}

	@Test
	public void testToTextFragmentStripsInvalidXMLCharacters() {
		TextFragment textFragment = XLIFFInlineCodeUtil.toTextFragment(
			"a\u0001b<br/>\u000B");

		XLIFFInlineCodeUtil.renumberCodes(1, textFragment, new TextFragment());

		Assert.assertEquals(
			"ab<ph id=\"1\">&lt;br/&gt;</ph>", _toXLIFF12(textFragment));
	}

	@Test
	public void testToTextFragmentStripsUnpairedSurrogates() {
		TextFragment textFragment = XLIFFInlineCodeUtil.toTextFragment(
			"a\uD800b<br/>\uDC00");

		XLIFFInlineCodeUtil.renumberCodes(1, textFragment, new TextFragment());

		Assert.assertEquals(
			"ab<ph id=\"1\">&lt;br/&gt;</ph>", _toXLIFF12(textFragment));
	}

	@Test
	public void testToTextFragmentWithNullHTML() {
		TextFragment textFragment = XLIFFInlineCodeUtil.toTextFragment(null);

		Assert.assertTrue(textFragment.isEmpty());
	}

	private String _toXLIFF12(TextFragment textFragment) {
		XLIFFContent xliffContent = new XLIFFContent();

		xliffContent.setContent(textFragment);

		return xliffContent.toString();
	}

}