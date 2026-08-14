/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.processor;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.util.JS;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Lourdes Fernández Besada
 */
public class PortletRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_portletRegistry = new PortletRegistryImpl();

		_portletLocalService = Mockito.mock(PortletLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_portletRegistry, "_portletLocalService", _portletLocalService);

		_setUpPortal();
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsAliasPortletName() {
		String portletAlias = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String portletName = RandomTestUtil.randomString();

		_portletRegistry.registerAlias(portletAlias, portletName);

		Assert.assertEquals(
			portletName, _portletRegistry.getPortletName(portletAlias));

		String elementId = RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"<lfr-widget-", portletAlias, " id=\"", elementId, "\">",
					RandomTestUtil.randomString(), "</div>"),
				namespace),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName),
				namespace + elementId));
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsAliasPortletNameUnregistered() {
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"<lfr-widget-", RandomTestUtil.randomString(), " id=\"",
					RandomTestUtil.randomString(), "\">",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()));
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsDynamicInstanceId() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), namespace + instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime", RandomTestUtil.randomString(),
					" instanceId=\"fragmentEntryLinkNamespace-", instanceId,
					"\" ", RandomTestUtil.randomString(), " portletName=\"",
					portletName, "\"", RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				namespace),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"]",
					RandomTestUtil.randomString(),
					" instanceId=\"fragmentEntryLinkNamespace-", instanceId,
					"\" ", RandomTestUtil.randomString(), " portletName=\"",
					portletName, "\"", RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				namespace),
			expectedPortletId);
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTag() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime", RandomTestUtil.randomString(),
					" instanceId=\"", instanceId, "\" ",
					RandomTestUtil.randomString(), " portletName=\"",
					portletName, "\"", RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"]",
					RandomTestUtil.randomString(), " instanceId=\"", instanceId,
					"\" ", RandomTestUtil.randomString(), " portletName=\"",
					portletName, "\"", RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagAfterActionURLTags() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.actionURL name=\"",
					RandomTestUtil.randomString(), "\" /] ",
					"[@liferay_portlet[\"actionURL\"] name=\"",
					RandomTestUtil.randomString(), "\" /] ",
					"[@liferay_portlet.runtime instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName), instanceId));
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagAfterUnterminatedQuote() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"<#-- [@liferay_portlet.runtime --> the author's note ",
					RandomTestUtil.randomString(),
					" [@liferay_portlet.runtime instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"<#-- [@liferay_portlet[\"runtime\"] --> the author's ",
					RandomTestUtil.randomString(),
					" [@liferay_portlet[\"runtime\"] instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagAttributeValueContainingClosingBrackets() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime defaultPreferences='{\"key\": ",
					"\"[#if x]/]\"}' instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet[\"runtime\"] defaultPreferences='",
					"{\"key\": \"[#if x]/]\"}' instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagAttributeValueContainingEscapedQuotes() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime defaultPreferences=\"",
					"{\\\"key\\\": \\\"/]\\\"}\" instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet[\"runtime\"] defaultPreferences=\"",
					"{\\\"key\\\": \\\"/]\\\"}\" instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagAttributeValueContainingPortletName() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime instanceId=\"", instanceId,
					"\" portletName=\"", portletName,
					"\" defaultPreferences=\"portletName=",
					RandomTestUtil.randomString(), "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet[\"runtime\"] instanceId=\"", instanceId,
					"\" portletName=\"", portletName,
					"\" defaultPreferences=\"portletName=",
					RandomTestUtil.randomString(), "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagAttributeValuesInSingleQuotes() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime instanceId='", instanceId,
					"' portletName='", portletName, "' /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet[\"runtime\"] instanceId='", instanceId,
					"' portletName='", portletName, "' /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagInBlockForm() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\"]",
					RandomTestUtil.randomString(),
					"[/@liferay_portlet.runtime]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet[\"runtime\"] instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\"]",
					RandomTestUtil.randomString(),
					"[/@liferay_portlet[\"runtime\"]]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagNameInSingleQuotes() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet['runtime'] instanceId=\"", instanceId,
					"\" portletName=\"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName), instanceId));
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagPortletNameAttributeFirst() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime", RandomTestUtil.randomString(),
					" portletName=\"", portletName, "\"",
					RandomTestUtil.randomString(), " instanceId=\"", instanceId,
					"\" ", RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"]",
					RandomTestUtil.randomString(), " portletName=\"",
					portletName, "\"", RandomTestUtil.randomString(),
					" instanceId=\"", instanceId, "\" ",
					RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsFreeMarkerRuntimeTagWhitespaceAroundEqualSign() {
		String instanceId = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime instanceId = \"", instanceId,
					"\" portletName = \"", portletName, "\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet[\"runtime\"] instanceId =\n\t\"",
					instanceId, "\" portletName =\n\t\"", portletName,
					"\" /]</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsMultipleFreeMarkerRuntimeTags() {
		String instanceId1 = RandomTestUtil.randomString();
		String instanceId2 = RandomTestUtil.randomString();
		String instanceId3 = RandomTestUtil.randomString();
		String portletName1 = RandomTestUtil.randomString();
		String portletName2 = RandomTestUtil.randomString();
		String portletName3 = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime\n\tinstanceId=\"", instanceId1,
					"\"\n\tportletName=\"", portletName1, "\"\n/]",
					RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime instanceId=\"", instanceId2,
					"\" portletName=\"", portletName2, "\" /]",
					RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"] instanceId=\"", instanceId3,
					"\" portletName=\"", portletName3, "\" /]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName1),
				PortletIdCodec.decodeUserId(portletName1), instanceId1),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName2),
				PortletIdCodec.decodeUserId(portletName2), instanceId2),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName3),
				PortletIdCodec.decodeUserId(portletName3), instanceId3));
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsMultipleFreeMarkerRuntimeTagsAttributeValueContainingMacro() {
		String instanceId1 = RandomTestUtil.randomString();
		String instanceId2 = RandomTestUtil.randomString();
		String portletName1 = RandomTestUtil.randomString();
		String portletName2 = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime defaultPreferences='",
					"[@liferay_portlet.runtime portletName=\"",
					RandomTestUtil.randomString(), "\" /]' instanceId=\"",
					instanceId1, "\" portletName=\"", portletName1, "\" /]",
					RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"] instanceId=\"", instanceId2,
					"\" portletName=\"", portletName2, "\" /]</div>"),
				RandomTestUtil.randomString()),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName1),
				PortletIdCodec.decodeUserId(portletName1), instanceId1),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName2),
				PortletIdCodec.decodeUserId(portletName2), instanceId2));
	}

	@Test
	@TestInfo("LPD-97875")
	public void testGetFragmentEntryLinkPortletIdsMultipleFreeMarkerRuntimeTagsInBlockForm() {
		String instanceId1 = RandomTestUtil.randomString();
		String instanceId2 = RandomTestUtil.randomString();
		String portletName1 = RandomTestUtil.randomString();
		String portletName2 = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">",
					"[@liferay_portlet.runtime instanceId=\"", instanceId1,
					"\" portletName=\"", portletName1, "\"]",
					RandomTestUtil.randomString(),
					"[/@liferay_portlet.runtime]",
					RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime instanceId=\"", instanceId2,
					"\" portletName=\"", portletName2, "\" /]</div>"),
				RandomTestUtil.randomString()),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName1),
				PortletIdCodec.decodeUserId(portletName1), instanceId1),
			PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName2),
				PortletIdCodec.decodeUserId(portletName2), instanceId2));
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsTypePortlet() {
		String instanceId = RandomTestUtil.randomString();

		_assertGetFragmentEntryLinkPortletIdsTypePortlet(
			instanceId, instanceId);

		_assertGetFragmentEntryLinkPortletIdsTypePortlet(
			StringPool.BLANK, StringPool.BLANK);
		_assertGetFragmentEntryLinkPortletIdsTypePortlet(StringPool.BLANK, "0");
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsWithoutInstanceId() {
		String portletName = RandomTestUtil.randomString();

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), null);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime", RandomTestUtil.randomString(),
					" portletName=\"", portletName, "\"",
					RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"]",
					RandomTestUtil.randomString(), " portletName=\"",
					portletName, "\"", RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				RandomTestUtil.randomString()),
			expectedPortletId);
	}

	@Test
	public void testGetFragmentEntryLinkPortletIdsWithSpecialCharacters() {
		String instanceId = RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();
		String portletName = RandomTestUtil.randomString();
		String specialCharacters = "-. ";

		String expectedPortletId = PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName), namespace + instanceId);

		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet.runtime", RandomTestUtil.randomString(),
					" instanceId=\"fragmentEntryLinkNamespace-", instanceId,
					specialCharacters, "\"", RandomTestUtil.randomString(),
					" portletName=\"", portletName, "\"",
					RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				namespace),
			expectedPortletId);
		_assertGetFragmentEntryLinkPortletIds(
			_getFragmentEntryLink(
				StringBundler.concat(
					"<div class=\"fragment_1\">", RandomTestUtil.randomString(),
					"[@liferay_portlet[\"runtime\"]",
					RandomTestUtil.randomString(),
					" instanceId=\"fragmentEntryLinkNamespace-", instanceId,
					specialCharacters, "\"", RandomTestUtil.randomString(),
					" portletName=\"", portletName, "\"",
					RandomTestUtil.randomString(), "/]",
					RandomTestUtil.randomString(), "</div>"),
				namespace),
			expectedPortletId);
	}

	private void _assertGetFragmentEntryLinkPortletIds(
		FragmentEntryLink fragmentEntryLink, String... portletIds) {

		List<String> fragmentEntryLinkPortletIds =
			_portletRegistry.getFragmentEntryLinkPortletIds(fragmentEntryLink);

		Assert.assertEquals(
			fragmentEntryLinkPortletIds.toString(), portletIds.length,
			fragmentEntryLinkPortletIds.size());

		for (int i = 0; i < fragmentEntryLinkPortletIds.size(); i++) {
			Assert.assertEquals(
				portletIds[i], fragmentEntryLinkPortletIds.get(i));
		}
	}

	private void _assertGetFragmentEntryLinkPortletIdsTypePortlet(
		String expectedInstanceId, String instanceId) {

		String portletId = RandomTestUtil.randomString();

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			JSONUtil.put(
				"instanceId", instanceId
			).put(
				"portletId", portletId
			).toString(),
			"<div class=\"fragment_1\"></div>", RandomTestUtil.randomString());

		Mockito.when(
			fragmentEntryLink.isTypePortlet()
		).thenReturn(
			true
		);

		_assertGetFragmentEntryLinkPortletIds(
			fragmentEntryLink,
			PortletIdCodec.encode(portletId, expectedInstanceId));
	}

	private FragmentEntryLink _getFragmentEntryLink(
		String html, String namespace) {

		return _getFragmentEntryLink("{}", html, namespace);
	}

	private FragmentEntryLink _getFragmentEntryLink(
		String editableValues, String html, String namespace) {

		FragmentEntryLink fragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			fragmentEntryLink.getEditableValues()
		).thenReturn(
			editableValues
		);

		Mockito.when(
			fragmentEntryLink.getEditableValuesJSONObject()
		).thenReturn(
			JSONFactoryUtil.safeCreateJSONObject(editableValues)
		);

		Mockito.when(
			fragmentEntryLink.getHtml()
		).thenReturn(
			html
		);

		Mockito.when(
			fragmentEntryLink.getNamespace()
		).thenReturn(
			namespace
		);

		return fragmentEntryLink;
	}

	private void _setUpPortal() {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.fetchClassName(Mockito.anyLong())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			portal.getClassName(Mockito.anyLong())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			portal.getJsSafePortletId(Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> JS.getSafeName(
				invocationOnMock.getArgument(0, String.class))
		);

		ReflectionTestUtil.setFieldValue(_portletRegistry, "_portal", portal);
	}

	private PortletLocalService _portletLocalService;
	private PortletRegistry _portletRegistry;

}