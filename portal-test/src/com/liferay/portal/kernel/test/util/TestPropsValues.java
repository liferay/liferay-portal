/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.portal.kernel.exception.LoggedExceptionInInitializerError;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Raymond Augé
 * @author Manuel de la Peña
 * @author Sampsa Sohlman
 */
public class TestPropsValues {

	public static final boolean ASSERT_LOGS = GetterUtil.getBoolean(
		TestPropsUtil.get("assert.logs"));

	public static final long CI_TEST_TIMEOUT_TIME = GetterUtil.getLong(
		TestPropsUtil.get("ci.test.timeout.time"), 3 * Time.MINUTE);

	public static final String COMPANY_WEB_ID;

	public static final boolean DATABASE_PARTITION_COPY = GetterUtil.getBoolean(
		TestPropsUtil.get("database.partition.copy"));

	public static final boolean DATABASE_PARTITION_EXPORT_AND_IMPORT =
		GetterUtil.getBoolean(
			TestPropsUtil.get("database.partition.export.and.import"));

	public static final boolean DL_FILE_ENTRY_PROCESSORS_TRIGGER_SYNCHRONOUSLY =
		GetterUtil.getBoolean(
			TestPropsUtil.get(
				"dl.file.entry.processors.trigger.synchronously"));

	public static final int JUNIT_DELAY_FACTOR = GetterUtil.getInteger(
		TestPropsUtil.get("junit.delay.factor"));

	public static final String PORTAL_URL = TestPropsUtil.get("portal.url");

	public static final String USER_PASSWORD = TestPropsUtil.get(
		"user.password");

	static {
		String companyWebId = TestPropsUtil.get("company.web.id");

		try {
			if (PropsValues.DATABASE_PARTITION_ENABLED) {
				companyWebId = TestPropsUtil.get(
					"database.partition.company.web.id");
			}
			else if (Validator.isNull(companyWebId)) {
				companyWebId = GetterUtil.getString(
					PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
			}

			TestPropsUtil.set("company.web.id", companyWebId);
		}
		catch (Exception exception) {
			throw new LoggedExceptionInInitializerError(exception);
		}

		TestPropsUtil.printProperties();

		COMPANY_WEB_ID = companyWebId;
	}

	public static long getCompanyId() throws PortalException {
		if (_companyId > 0) {
			return _companyId;
		}

		Company company = CompanyLocalServiceUtil.getCompanyByWebId(
			TestPropsValues.COMPANY_WEB_ID);

		_companyId = company.getCompanyId();

		return _companyId;
	}

	public static long getGroupId() throws PortalException {
		if (_groupId > 0) {
			return _groupId;
		}

		Group group = GroupLocalServiceUtil.getGroup(
			getCompanyId(), GroupConstants.GUEST);

		_groupId = group.getGroupId();

		return _groupId;
	}

	public static long getPlid() throws PortalException {
		return getPlid(getGroupId());
	}

	public static long getPlid(long groupId) {
		if (_plid > 0) {
			return _plid;
		}

		_plid = LayoutLocalServiceUtil.getDefaultPlid(groupId);

		return _plid;
	}

	public static User getUser() throws PortalException {
		if (_user == null) {
			_user = UserTestUtil.getAdminUser(getCompanyId());
		}

		return _user;
	}

	public static long getUserId() throws PortalException {
		if (_userId == 0) {
			User user = getUser();

			if (user != null) {
				_userId = user.getUserId();
			}
		}

		return _userId;
	}

	private static long _companyId;
	private static long _groupId;
	private static long _plid;
	private static User _user;
	private static long _userId;

}