/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Thiago Buarque
 */
public class ConfigurationFilterStringUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetCompanyScopedFilterString() throws Exception {
		String filterString =
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				null, null);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).put(
				"siteExternalReferenceCode", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"dxp.lxc.liferay.com.virtualInstanceId", "any"
			).build());

		Long companyId = RandomTestUtil.randomLong();
		String virtualInstanceId = RandomTestUtil.randomString();

		filterString =
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				companyId, virtualInstanceId);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"siteExternalReferenceCode", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"dxp.lxc.liferay.com.virtualInstanceId", virtualInstanceId
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"dxp.lxc.liferay.com.virtualInstanceId", virtualInstanceId
			).build());

		filterString =
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				companyId, "(*)\\");

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"dxp.lxc.liferay.com.virtualInstanceId", "(x)\\"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"dxp.lxc.liferay.com.virtualInstanceId", "(*)\\"
			).build());

		String pid = "foo.scoped~123";

		filterString =
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				null, pid, null);

		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				"companyId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", "any"
			).build());

		pid = "foo.scoped~*";

		filterString =
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				null, pid, null);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, "foo.scoped~123"
			).put(
				"companyId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", "any"
			).build());
	}

	@Test
	public void testGetGroupScopedFilterString() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		String filterString =
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				companyId, null, null);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"groupId", "any"
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"siteExternalReferenceCode", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"groupId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"siteExternalReferenceCode", "any"
			).build());

		Long groupId = RandomTestUtil.randomLong();
		String siteExternalReferenceCode = RandomTestUtil.randomString();

		filterString = ConfigurationFilterStringUtil.getGroupScopedFilterString(
			companyId, groupId, siteExternalReferenceCode);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"groupId", groupId
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"groupId", groupId
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"siteExternalReferenceCode", siteExternalReferenceCode
			).build());

		filterString = ConfigurationFilterStringUtil.getGroupScopedFilterString(
			companyId, groupId, "(*)\\");

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"siteExternalReferenceCode", "(x)\\"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", companyId
			).put(
				"siteExternalReferenceCode", "(*)\\"
			).build());

		String pid = "foo.scoped~123";

		filterString = ConfigurationFilterStringUtil.getGroupScopedFilterString(
			companyId, null, pid, null);

		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				"companyId", companyId
			).put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid + ".scoped"
			).put(
				"companyId", companyId
			).put(
				"groupId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", companyId
			).put(
				"groupId", "any"
			).build());

		try {
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				null, null, pid, null);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"A valid company is expected when building a group scoped " +
					"configuration filter string",
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testGetPortletScopedFilterString() throws Exception {
		String filterString =
			ConfigurationFilterStringUtil.getPortletScopedFilterString(null);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"siteExternalReferenceCode", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"portletInstanceId", "any"
			).build());

		String portletInstanceId = RandomTestUtil.randomString();

		filterString =
			ConfigurationFilterStringUtil.getPortletScopedFilterString(
				portletInstanceId);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"portletInstanceId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"portletInstanceId", portletInstanceId
			).build());

		filterString =
			ConfigurationFilterStringUtil.getPortletScopedFilterString("(*)\\");

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"portletInstanceId", "(x)\\"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"portletInstanceId", "(*)\\"
			).build());

		String pid = "foo.scoped~123";

		filterString =
			ConfigurationFilterStringUtil.getPortletScopedFilterString(
				pid, null);

		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"portletInstanceId", "any"
			).build());
	}

	@Test
	public void testGetSystemScopedFilterString() throws Exception {
		String filterString =
			ConfigurationFilterStringUtil.getSystemScopedFilterString();

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"dxp.lxc.liferay.com.virtualInstanceId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"groupId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"portletInstanceId", "any"
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				"siteExternalReferenceCode", "any"
			).build());
		_test(true, filterString, Collections.emptyMap());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				"companyId", "0"
			).build());

		String pid = "foo";

		filterString =
			ConfigurationFilterStringUtil.getSystemScopedFilterString(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				Constants.SERVICE_PID, pid
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", "0"
			).build());

		pid = "foo~123";

		filterString =
			ConfigurationFilterStringUtil.getSystemScopedFilterString(pid);

		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).build());
		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", "any"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).put(
				"companyId", "0"
			).build());

		pid = "foo~*";

		filterString =
			ConfigurationFilterStringUtil.getSystemScopedFilterString(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, "foo~123"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid
			).put(
				Constants.SERVICE_PID, pid
			).build());

		pid = "(*)\\";

		filterString =
			ConfigurationFilterStringUtil.getSystemScopedFilterString(pid);

		_test(
			false, filterString,
			HashMapBuilder.<String, Serializable>put(
				Constants.SERVICE_PID, "(x)\\"
			).build());
		_test(
			true, filterString,
			HashMapBuilder.<String, Serializable>put(
				Constants.SERVICE_PID, pid
			).build());
	}

	private void _test(
			boolean matches, String pidFilterString,
			Map<String, Serializable> payload)
		throws Exception {

		Filter filter = FrameworkUtil.createFilter(pidFilterString);

		if (matches && !filter.matches(payload)) {
			throw new AssertionFailedError(
				filter + " does not match " + payload);
		}
		else if (!matches && filter.matches(payload)) {
			throw new AssertionFailedError(filter + " matches " + payload);
		}
	}

}