/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Debora Buriti
 */
@RunWith(Arquillian.class)
public class TemplateRestrictedClassesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		FileUtil.delete(_file);
	}

	@Test
	public void testRestrictedClasses() throws Exception {
		File tempDirFile = new File(
			SystemProperties.get(SystemProperties.TMP_DIR));

		_file = new File(tempDirFile, RandomTestUtil.randomString());

		_assertDeniedAccess(
			tempDirFile,
			"${object.toPath().resolve(\"" + _file.getName() +
				"\").toFile().createNewFile()?c}");

		Assert.assertFalse(_file.getAbsolutePath(), _file.exists());

		Path path = tempDirFile.toPath();

		_assertDeniedAccess(path, "${object.getFileSystem()}");

		FileSystem fileSystem = path.getFileSystem();

		_assertDeniedAccess(fileSystem, "${object.provider()}");
		_assertDeniedAccess(fileSystem.provider(), "${object.getScheme()}");
	}

	private void _assertDeniedAccess(Object object, String templateContent)
		throws Exception {

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			new StringTemplateResource(
				RandomTestUtil.randomString(), templateContent),
			true);

		template.put("object", object);

		try {
			template.processTemplate(new UnsyncStringWriter());

			Assert.fail(templateContent);
		}
		catch (TemplateException templateException) {
			Throwable throwable = templateException;

			while (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}

			String message = throwable.getMessage();

			Assert.assertTrue(
				message, message.contains("Denied access to method or field "));
		}
	}

	private File _file;

}