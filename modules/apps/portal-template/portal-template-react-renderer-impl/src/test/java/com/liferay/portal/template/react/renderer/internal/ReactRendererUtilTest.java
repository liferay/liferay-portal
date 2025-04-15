/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.react.renderer.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.servlet.taglib.aui.AMDRequire;
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.PortletData;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Iván Zaera Avellón
 */
public class ReactRendererUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRenderEcmaScript() throws Exception {
		AbsolutePortalURLBuilder absolutePortalURLBuilder = Mockito.mock(
			AbsolutePortalURLBuilder.class);

		Mockito.when(
			absolutePortalURLBuilder.forESModule(
				Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			(Answer<ESModuleAbsolutePortalURLBuilder>)invocationOnMock -> {
				ESModuleAbsolutePortalURLBuilder
					esModuleAbsolutePortalURLBuilder = Mockito.mock(
						ESModuleAbsolutePortalURLBuilder.class);

				Mockito.when(
					esModuleAbsolutePortalURLBuilder.build()
				).thenReturn(
					invocationOnMock.getArgument(0, String.class) +
						StringPool.POUND +
							invocationOnMock.getArgument(1, String.class)
				);

				return esModuleAbsolutePortalURLBuilder;
			}
		);

		ComponentDescriptor componentDescriptor = new ComponentDescriptor(
			"{component} from my-context", "componentId",
			Arrays.asList(
				"{dep1} from deps-context", "{dep2} from deps-context"),
			false, "{myTransformer} from props-transformer-context");

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		List<ScriptData> scriptDatas = new ArrayList<>();

		Mockito.doAnswer(
			(Answer<Void>)invocationOnMock -> {
				scriptDatas.add(
					invocationOnMock.getArgument(1, ScriptData.class));

				return null;
			}
		).when(
			httpServletRequest
		).setAttribute(
			Mockito.anyString(), Mockito.any()
		);

		JSONFactory jsonFactory = Mockito.mock(JSONFactory.class);

		Mockito.when(
			jsonFactory.createJSONSerializer()
		).thenAnswer(
			(Answer<JSONSerializer>)invocationOnMock -> {
				JSONSerializer jsonSerializer = Mockito.mock(
					JSONSerializer.class);

				Mockito.when(
					jsonSerializer.serializeDeep(Mockito.any())
				).thenAnswer(
					(Answer<String>)invocationOnMock1 -> {
						Map<?, ?> map = invocationOnMock1.getArgument(
							0, Map.class);

						return map.toString();
					}
				);

				return jsonSerializer;
			}
		);

		Portal portal = Mockito.mock(Portal.class);

		Map<String, Object> props = HashMapBuilder.<String, Object>put(
			"prop1", "val1"
		).put(
			"prop2", "val2"
		).build();

		Writer writer = new PrintWriter(new ByteArrayOutputStream());

		ReactRendererUtil.renderEcmaScript(
			absolutePortalURLBuilder, componentDescriptor, httpServletRequest,
			jsonFactory, "placeholderId", portal, props, writer);

		ScriptData scriptData = scriptDatas.get(0);

		ConcurrentMap<String, PortletData> portletDataMap =
			(ConcurrentMap<String, PortletData>)
				ReflectionTestUtils.invokeGetterMethod(
					scriptData, "portletDataMap");

		Assert.assertEquals(
			portletDataMap.toString(), 1, portletDataMap.size());

		PortletData portletData = portletDataMap.get(StringPool.BLANK);

		Collection<JSFragment> jsFragments = portletData.getJSFragments();

		Assert.assertEquals(jsFragments.toString(), 1, jsFragments.size());

		Iterator<JSFragment> iterator = jsFragments.iterator();

		JSFragment jsFragment = iterator.next();

		Assert.assertEquals(
			"render(componentModule, propsTransformer(" +
				"{prop2=val2, prop1=val1}), 'placeholderId');\n",
			jsFragment.getCode());

		List<AMDRequire> amdRequires = jsFragment.getAMDRequires();

		Assert.assertEquals(amdRequires.toString(), 0, amdRequires.size());

		List<String> auiUses = jsFragment.getAUIUses();

		Assert.assertEquals(auiUses.toString(), 0, auiUses.size());

		List<ESImport> esImports = jsFragment.getESImports();

		Assert.assertEquals(esImports.toString(), 5, esImports.size());

		_assertESImportEquals(
			StringPool.BLANK, "deps-context#index.js", "dep1",
			esImports.get(0));
		_assertESImportEquals(
			StringPool.BLANK, "deps-context#index.js", "dep2",
			esImports.get(1));
		_assertESImportEquals(
			StringPool.BLANK, "portal-template-react-renderer-impl#index.js",
			"render", esImports.get(2));
		_assertESImportEquals(
			"componentModule", "my-context#index.js", "component",
			esImports.get(3));
		_assertESImportEquals(
			"propsTransformer", "props-transformer-context#index.js",
			"myTransformer", esImports.get(4));
	}

	private void _assertESImportEquals(
		String expectedAlias, String expectedModule, String expectedSymbol,
		ESImport esImport) {

		Assert.assertEquals(expectedAlias, esImport.getAlias());
		Assert.assertEquals(expectedModule, esImport.getModule());
		Assert.assertEquals(expectedSymbol, esImport.getSymbol());
	}

}