/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.taglib.aui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.aui.AMDRequire;
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.PortletData;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.CharArrayWriter;
import java.io.Writer;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Iván Zaera Avellón
 */
public class PortletDataRendererImplTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAMDCodeIsWrappedInIIFE() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				Arrays.asList(new AMDRequire("frontend-js-web")), null,
				"content", null));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(code.contains("(function() {\ncontent\n})();"));
	}

	@Test
	public void testAUICodeIsWrappedInIIFE() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, Arrays.asList("aui-use-1", "aui-use-2"), "content",
				null));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(code.contains("(function() {\ncontent\n})();"));
	}

	@Test
	public void testAliasedESImports() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, null, "content",
				Arrays.asList(new ESImport("alias", "module", "symbol"))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(
			code, code.contains("import {symbol as alias} from 'module';"));
	}

	@Test
	public void testESCodeIsWrappedInBlock() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, null, "content",
				Arrays.asList(new ESImport("frontend-js-web", "openDialog"))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(code.contains("{\ncontent\n}\n"));
	}

	@Test
	public void testESImports() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, null, "content",
				Arrays.asList(new ESImport("module", "symbol"))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(
			code, code.contains("import {symbol} from 'module';"));
	}

	@Test
	public void testGenerateVariableReplacesInvalidFirstCharacter()
		throws Exception {

		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				Arrays.asList(
					new AMDRequire("_Var"), new AMDRequire("1Var"),
					new AMDRequire("*Var"), new AMDRequire("/Var")),
				null, "content", null));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(
			Collections.singleton(portletData), writer);

		_assertAliases(writer.toString(), "_Var", "_Var1", "_Var2", "_Var3");
	}

	@Test
	public void testGenerateVariableStripsInvalidCharacters() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				Arrays.asList(
					new AMDRequire("var"), new AMDRequire("V ar"),
					new AMDRequire("va*r"), new AMDRequire("var/")),
				null, "content", null));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(
			Collections.singleton(portletData), writer);

		_assertAliases(writer.toString(), "_var", "vAr", "vaR", "var1");
	}

	@Test
	public void testGenerateVariableStripsLastInvalidCharacter()
		throws Exception {

		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				Arrays.asList(new AMDRequire("var!")), null, "content", null));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(
			Collections.singleton(portletData), writer);

		_assertAliases(writer.toString(), "_var");
	}

	@Test
	public void testRepeatedVariablesOfAllKind() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData1 = new PortletData();

		portletData1.add(
			new JSFragment(
				Arrays.asList(new AMDRequire("frontend-js-web")), null,
				"content1",
				Arrays.asList(
					new ESImport("frontend-js-web", "openDialog"),
					new ESImport(
						"myOpenDialog", "frontend-js-web", "openDialog"),
					new ESImport("react", "react"))));

		portletData1.add(
			new JSFragment(
				Arrays.asList(new AMDRequire("frontend-js-web")), null,
				"content2",
				Arrays.asList(
					new ESImport("frontend-js-web", "openDialog"),
					new ESImport(
						"myOpenDialog2", "frontend-js-web", "openDialog"),
					new ESImport("react", "react"))));

		PortletData portletData2 = new PortletData();

		portletData2.add(
			new JSFragment(
				Arrays.asList(
					new AMDRequire("frontend-js-web"), new AMDRequire("react")),
				null, "content3",
				Arrays.asList(
					new ESImport("frontend-js-web", "openDialog"),
					new ESImport(
						"myOpenDialog2", "frontend-js-web", "openDialog"))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(
			Arrays.asList(portletData1, portletData2), writer);

		String code = writer.toString();

		Assert.assertTrue(
			"ES imports are correctly rendered",
			code.contains(
				"import {openDialog} from 'frontend-js-web';\n" +
					"import {react} from 'react';\n"));

		Assert.assertTrue(
			"AMD requires are correctly rendered",
			code.contains(
				StringBundler.concat(
					"Liferay.Loader.require(\n", "'frontend-js-web',\n",
					"'react',\n", "function(frontendJsWeb, react0) {\n")));

		Assert.assertTrue(
			"First JS fragment is correctly rendered",
			code.contains(
				StringBundler.concat(
					"(function() {\n", "const myOpenDialog = openDialog;\n",
					"content1\n", "})();\n")));

		Assert.assertTrue(
			"Second JS fragment is correctly rendered",
			code.contains(
				StringBundler.concat(
					"(function() {\n", "const myOpenDialog2 = openDialog;\n",
					"content2\n", "})();\n")));

		Assert.assertTrue(
			"Third JS fragment is correctly rendered",
			code.contains(
				StringBundler.concat(
					"(function() {\n", "const react = react0;\n",
					"const myOpenDialog2 = openDialog;\n", "content3\n",
					"})();\n")));
	}

	@Test
	public void testSameAliasAndSymbolOmitsAlias() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, null, "content",
				Arrays.asList(new ESImport("symbol", "module", "symbol"))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(
			code, code.contains("import {symbol} from 'module';"));
	}

	@Test
	public void testScriptTypeIsModuleWhenESMImportsArePresent()
		throws Exception {

		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, null, "content",
				Arrays.asList(new ESImport("frontend-js-web", "openDialog"))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(code.contains("<script type=\"module\">"));
	}

	@Test
	public void testScriptTypeIsTextJavaScriptWhenESMImportsAreMissing()
		throws Exception {

		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(new JSFragment(null, null, "content", null));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(code.contains("<script type=\"text/javascript\">"));
	}

	@Test
	public void testSymbolLessESImports() throws Exception {
		PortletDataRendererImpl portletDataRendererImpl =
			new PortletDataRendererImpl();

		PortletData portletData = new PortletData();

		portletData.add(
			new JSFragment(
				null, null, "content",
				Arrays.asList(
					new ESImport("alias", "module", StringPool.BLANK))));

		Writer writer = new CharArrayWriter();

		portletDataRendererImpl.write(Arrays.asList(portletData), writer);

		String code = writer.toString();

		Assert.assertTrue(code, code.contains("import alias from 'module';"));
	}

	private void _assertAliases(String code, String... aliases) {
		for (String alias : aliases) {
			Assert.assertTrue(alias + " was not found", code.contains(alias));
		}
	}

}