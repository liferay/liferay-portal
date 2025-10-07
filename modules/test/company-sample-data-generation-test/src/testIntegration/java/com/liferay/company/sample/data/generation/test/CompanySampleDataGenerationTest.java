/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.sample.data.generation.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.increment.BufferedIncrementThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;

import java.io.BufferedWriter;
import java.io.File;

import java.net.InetSocketAddress;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Hai Yu
 */
@DataGuard(scope = DataGuard.Scope.CLASS)
@RunWith(Arquillian.class)
public class CompanySampleDataGenerationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_originalAtomicReference = ReflectionTestUtil.getFieldValue(
			_portal, "_portalServerInetSocketAddress");

		ReflectionTestUtil.setFieldValue(
			_portal, "_portalServerInetSocketAddress",
			new AtomicReference<InetSocketAddress>());

		Runtime runtime = Runtime.getRuntime();

		_executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());
	}

	@After
	public void tearDown() {
		_executorService.shutdownNow();

		ReflectionTestUtil.setFieldValue(
			_portal, "_portalServerInetSocketAddress",
			_originalAtomicReference);
	}

	@Test
	public void testGenerateSampleData() throws Exception {
		int originalCompaniesCount = _companyLocalService.getCompaniesCount();

		List<Future<Void>> futures = new ArrayList<>();

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			for (int i = 1; i <= _COMPANY_COUNT; i++) {
				int companyIndex = i;

				futures.add(
					_executorService.submit(
						() -> {
							BufferedIncrementThreadLocal.
								setForceSyncWithSafeCloseable(true);

							_addCompany(companyIndex);

							return null;
						}));
			}

			for (Future<Void> future : futures) {
				future.get();
			}
		}

		Assert.assertEquals(
			"Company count should be " +
				(_COMPANY_COUNT + originalCompaniesCount),
			_COMPANY_COUNT + originalCompaniesCount,
			_companyLocalService.getCompaniesCount());

		_exportCSVs();
	}

	private void _addCompany(int companyIndex) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer(
				String.valueOf(companyIndex))) {

			String webId = _generateCompanyWebId(companyIndex);

			// Add company

			Company company = _companyLocalService.addCompany(
				null, webId, webId, webId, 0, true, true, null, null, null,
				null, null, null);

			PortalInstances.initCompany(company);

			// Add user

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						company.getCompanyId())) {

				int originalCompanyUsersCount =
					_userLocalService.getCompanyUsersCount(
						company.getCompanyId());

				_addUsers(
					companyIndex, company.getCompanyId(), company.getGroupId(),
					webId);

				Assert.assertEquals(
					StringBundler.concat(
						"User count for ", webId, " should be ",
						_USER_PER_COMPANY_COUNT + originalCompanyUsersCount),
					_USER_PER_COMPANY_COUNT + originalCompanyUsersCount,
					_userLocalService.getCompanyUsersCount(
						company.getCompanyId()));
			}
		}
	}

	private void _addUsers(
			int companyIndex, long companyId, long groupId, String webId)
		throws Exception {

		String middleName = StringPool.BLANK;
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] organizationIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		long userStartIndex = (companyIndex * _USER_PER_COMPANY_COUNT) + 1;
		long userEndIndex = (companyIndex + 1) * _USER_PER_COMPANY_COUNT;

		for (long i = userStartIndex; i <= userEndIndex; i++) {
			String screenName = _generateUserScreenName(i);

			String firstName = screenName;
			String lastName = screenName;

			String emailAddress = screenName + StringPool.AT + webId;

			User user = _userLocalService.addUser(
				0, companyId, false, "test", "test", false, screenName,
				emailAddress, LocaleUtil.US, firstName, middleName, lastName,
				prefixListTypeId, suffixListTypeId, male, birthdayMonth,
				birthdayDay, birthdayYear, jobTitle, UserConstants.TYPE_REGULAR,
				new long[] {groupId}, organizationIds,
				new long[] {role.getRoleId()}, userGroupIds, sendEmail,
				_getServiceContext(companyId));

			user.setPasswordReset(false);
			user.setReminderQueryQuestion("What is your screen name?");
			user.setReminderQueryAnswer(screenName);
			user.setLoginDate(new Date());
			user.setLastLoginDate(new Date());
			user.setLockoutDate(new Date());
			user.setAgreedToTermsOfUse(true);
			user.setEmailAddressVerified(true);
			user.setPasswordModified(true);

			_userLocalService.updateUser(user);

			List<String> userScreenNames = _csvMap.computeIfAbsent(
				webId, key -> new ArrayList<>());

			userScreenNames.add(screenName);
		}
	}

	private void _exportCSVs() throws Exception {
		String outputDir = PropsUtil.get("sample.data.output.dir");

		if (Validator.isNull(outputDir)) {
			return;
		}

		Path outputDirPath = Paths.get(
			PropsUtil.get(PropsKeys.LIFERAY_HOME), outputDir);

		File outputDirFile = outputDirPath.toFile();

		if (outputDirFile.exists()) {
			FileUtil.deltree(outputDirFile);
		}

		outputDirFile.mkdir();

		try (LoggingTimer loggingTimer = new LoggingTimer(
				outputDirFile.getAbsolutePath());
			BufferedWriter companyBufferedWriter = Files.newBufferedWriter(
				outputDirPath.resolve("company.csv"));
			BufferedWriter hostBufferedWriter = Files.newBufferedWriter(
				outputDirPath.resolve("host.csv"));
			BufferedWriter userBufferedWriter = Files.newBufferedWriter(
				outputDirPath.resolve("user.csv"))) {

			List<String> keys = new ArrayList<>(_csvMap.keySet());

			Collections.sort(keys);

			for (String key : keys) {
				companyBufferedWriter.append(CSVUtil.encode(key));
				companyBufferedWriter.newLine();

				hostBufferedWriter.append("127.0.0.1 ");
				hostBufferedWriter.append(CSVUtil.encode(key));
				hostBufferedWriter.newLine();

				List<String> screenNames = _csvMap.get(key);

				Collections.sort(screenNames);

				for (String screenName : screenNames) {
					userBufferedWriter.append(CSVUtil.encode(key));
					userBufferedWriter.append(StringPool.COMMA);
					userBufferedWriter.append(CSVUtil.encode(screenName));
					userBufferedWriter.newLine();
				}
			}

			companyBufferedWriter.flush();
			hostBufferedWriter.flush();
			userBufferedWriter.flush();
		}
	}

	private String _generateCompanyWebId(int companyIndex) {
		return "liferay" + companyIndex + ".com";
	}

	private String _generateUserScreenName(long userIndex) {
		return "test" + userIndex;
	}

	private ServiceContext _getServiceContext(long companyId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);

		return serviceContext;
	}

	private static final int _COMPANY_COUNT = GetterUtil.get(
		PropsUtil.get("sample.data.company.count"), 2);

	private static final int _USER_PER_COMPANY_COUNT = GetterUtil.get(
		PropsUtil.get("sample.data.user.per.company.count"), 2);

	@Inject
	private CompanyLocalService _companyLocalService;

	private final Map<String, List<String>> _csvMap = new ConcurrentHashMap<>();
	private ExecutorService _executorService;
	private AtomicReference<InetSocketAddress> _originalAtomicReference;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}