/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webdav.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.webdav.test.rule.WebDAVEnvironmentConfigClassTestRule;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * Based on <a href="http://www.webdav.org/neon/litmus/">litmus</a> 0.12.1
 * "copymove" test.
 * </p>
 *
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class WebDAVLitmusCopyMoveTest extends BaseWebDAVTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			WebDAVEnvironmentConfigClassTestRule.INSTANCE);

	@Test
	public void test02Copy() {

		// Create

		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut("copysrc", _TEST_CONTENT.getBytes()));
		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "copycoll", null, null));

		// Copy

		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(Method.COPY, "copysrc", "copydest", false));

		// Overwrite

		assertCode(
			HttpServletResponse.SC_PRECONDITION_FAILED,
			serviceCopyOrMove(Method.COPY, "copysrc", "copydest", false));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(Method.COPY, "copysrc", "copydest", true));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(Method.COPY, "copysrc", "copycoll", true));

		// No destination

		assertCode(
			HttpServletResponse.SC_CONFLICT,
			serviceCopyOrMove(Method.COPY, "copysrc", "nonesuch/foo", false));

		// Delete

		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("copysrc"));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceDelete("copydest"));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceDelete("copycoll"));
	}

	@Test
	public void test03CopyColl() {
		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "ccsrc", null, null));

		for (int i = 0; i < 10; i++) {
			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					StringBundler.concat("ccsrc/foo", i, ".", i),
					_TEST_CONTENT.getBytes()));
		}

		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "ccsrc/subcoll", null, null));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(Method.COPY, "ccsrc", null, "ccdest", -1, false));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(
				Method.COPY, "ccsrc", null, "ccdest2", -1, false));
		assertCode(
			HttpServletResponse.SC_PRECONDITION_FAILED,
			serviceCopyOrMove(
				Method.COPY, "ccsrc", null, "ccdest2", -1, false));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(Method.COPY, "ccsrc", null, "ccdest", -1, true));
		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("ccsrc"));

		for (int i = 0; i < 10; i++) {
			assertCode(
				HttpServletResponse.SC_NO_CONTENT,
				serviceDelete("ccdest/foo" + i));
		}

		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceDelete("ccdest/subcoll"));
		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("ccdest"));
		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("ccdest2"));
	}

	@Test
	public void test04CopyShallow() {
		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "ccsrc", null, null));
		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut("ccsrc/foo", _TEST_CONTENT.getBytes()));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(Method.COPY, "ccsrc", "ccdest", false));
		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("ccsrc"));
		assertCode(HttpServletResponse.SC_NOT_FOUND, serviceDelete("foo"));
		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("ccdest"));
	}

	@Test
	public void test05Move() {
		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut("move", _TEST_CONTENT.getBytes()));
		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut("move2", _TEST_CONTENT.getBytes()));
		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "movecoll", null, null));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(Method.MOVE, "move", "movedest", false));
		assertCode(
			HttpServletResponse.SC_PRECONDITION_FAILED,
			serviceCopyOrMove(Method.MOVE, "move2", "movedest", false));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(Method.MOVE, "move2", "movedest", true));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(Method.MOVE, "movedest", "movecoll", true));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceDelete("movecoll"));
	}

	@Test
	public void test06MoveColl() {
		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "mvsrc", null, null));

		for (int i = 0; i < 10; i++) {
			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					StringBundler.concat("mvsrc/foo", i, ".", i),
					_TEST_CONTENT.getBytes()));
		}

		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut("mvnoncoll", _TEST_CONTENT.getBytes()));
		assertCode(
			HttpServletResponse.SC_CREATED,
			service(Method.MKCOL, "mvsrc/subcoll", null, null));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(
				Method.COPY, "mvsrc", null, "mvdest2", -1, false));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(Method.MOVE, "mvsrc", null, "mvdest", -1, false));
		assertCode(
			HttpServletResponse.SC_PRECONDITION_FAILED,
			serviceCopyOrMove(
				Method.MOVE, "mvdest", null, "mvdest2", -1, false));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(
				Method.MOVE, "mvdest2", null, "mvdest", -1, true));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(
				Method.COPY, "mvdest", null, "mvdest2", -1, false));

		for (int i = 0; i < 10; i++) {
			assertCode(
				HttpServletResponse.SC_NO_CONTENT,
				serviceDelete("mvdest/foo" + i));
		}

		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceDelete("mvdest/subcoll"));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceCopyOrMove(
				Method.MOVE, "mvdest2", null, "mvnoncoll", -1, true));
		assertCode(HttpServletResponse.SC_NO_CONTENT, serviceDelete("mvdest"));
		assertCode(HttpServletResponse.SC_NOT_FOUND, serviceDelete("mvdest2"));
		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceDelete("mvnoncoll"));
	}

	private static final String _TEST_CONTENT =
		"LIFERAY\nEnterprise. Open Source. For Life.";

}