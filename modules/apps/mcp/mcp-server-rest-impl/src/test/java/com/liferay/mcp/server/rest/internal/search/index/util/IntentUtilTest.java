/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Petteri Karttunen
 */
public class IntentUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetIntents() {
		Assert.assertTrue(SetUtil.isEmpty(IntentUtil.getIntents(null)));
		Assert.assertEquals(
			SetUtil.fromArray("delete"), IntentUtil.getIntents("delete"));
		Assert.assertEquals(
			SetUtil.fromArray("export"), IntentUtil.getIntents("export"));
		Assert.assertEquals(
			SetUtil.fromArray("delete"), IntentUtil.getIntents("get rid of"));
		Assert.assertTrue(SetUtil.isEmpty(IntentUtil.getIntents("hello")));
		Assert.assertEquals(
			SetUtil.fromArray("create"), IntentUtil.getIntents("import"));
		Assert.assertEquals(
			SetUtil.fromArray(IntentUtil.INTENT_LIST, IntentUtil.INTENT_READ),
			IntentUtil.getIntents("show"));
	}

	@Test
	public void testGetOtherIntents() {
		Set<String> otherIntents = IntentUtil.getOtherIntents("show");

		Assert.assertTrue(otherIntents.contains(IntentUtil.INTENT_CREATE));
		Assert.assertFalse(otherIntents.contains(IntentUtil.INTENT_LIST));
		Assert.assertFalse(otherIntents.contains(IntentUtil.INTENT_READ));
	}

	@Test
	public void testGetPrimaryVerbs() {
		Assert.assertTrue(
			SetUtil.isEmpty(IntentUtil.getPrimaryVerbs(StringPool.BLANK)));
		Assert.assertEquals(
			SetUtil.fromArray("clone", "copy", "duplicate"),
			IntentUtil.getPrimaryVerbs("copy"));
		Assert.assertEquals(
			SetUtil.fromArray(
				"browse", "find", "get all", "list", "see", "show"),
			IntentUtil.getPrimaryVerbs(IntentUtil.INTENT_LIST));
		Assert.assertEquals(
			SetUtil.fromArray(
				"fetch", "get", "look up", "open", "read", "retrieve", "view"),
			IntentUtil.getPrimaryVerbs(IntentUtil.INTENT_READ));
	}

	@Test
	public void testGetToolIntent() {
		Assert.assertEquals(
			"delete", IntentUtil.getToolIntent(false, "delete", null));
		Assert.assertEquals(
			IntentUtil.INTENT_LIST,
			IntentUtil.getToolIntent(true, "get", null));
		Assert.assertEquals(
			IntentUtil.INTENT_READ,
			IntentUtil.getToolIntent(false, "get", null));
		Assert.assertEquals(
			StringPool.BLANK, IntentUtil.getToolIntent(false, "head", null));
		Assert.assertEquals(
			IntentUtil.INTENT_CREATE,
			IntentUtil.getToolIntent(false, "post", null));
		Assert.assertEquals(
			"unpublish", IntentUtil.getToolIntent(false, "post", "Expire"));
		Assert.assertEquals(
			"export", IntentUtil.getToolIntent(false, "post", "ExportBatch"));
		Assert.assertEquals(
			StringPool.BLANK,
			IntentUtil.getToolIntent(false, "post", "Unknown"));
		Assert.assertEquals(
			"subscribe", IntentUtil.getToolIntent(false, "put", "Subscribe"));
	}

	@Test
	public void testGetVerb() {
		Assert.assertNull(IntentUtil.getVerb(new String[0]));
		Assert.assertNull(IntentUtil.getVerb(new String[] {"hello", "world"}));
		Assert.assertEquals(
			"delete",
			IntentUtil.getVerb(new String[] {"delete", "twenty", "users"}));
		Assert.assertEquals(
			"delete",
			IntentUtil.getVerb(
				new String[] {"please", "delete", "the", "user"}));
		Assert.assertEquals(
			"delete",
			IntentUtil.getVerb(
				new String[] {"delete", "and", "copy", "the", "page"}));
		Assert.assertEquals(
			"get all",
			IntentUtil.getVerb(new String[] {"get", "all", "users"}));
		Assert.assertEquals(
			"get rid of",
			IntentUtil.getVerb(new String[] {"get", "rid", "of", "a", "user"}));
		Assert.assertEquals(
			"get rid of",
			IntentUtil.getVerb(
				new String[] {
					"show", "me", "how", "to", "get", "rid", "of", "a", "user"
				}));
		Assert.assertEquals(
			"make live",
			IntentUtil.getVerb(new String[] {"make", "live", "the", "page"}));
	}

	@Test
	public void testIsVerb() {
		Assert.assertFalse(IntentUtil.isVerb(null));
		Assert.assertTrue(IntentUtil.isVerb("delete"));
		Assert.assertFalse(IntentUtil.isVerb("hello"));
		Assert.assertFalse(IntentUtil.isVerb("get rid"));
		Assert.assertTrue(IntentUtil.isVerb("get rid of"));
	}

}