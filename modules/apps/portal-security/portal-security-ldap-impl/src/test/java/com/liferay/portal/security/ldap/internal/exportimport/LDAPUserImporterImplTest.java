/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.exportimport;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.LDAPUser;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge Díaz
 * @author Manuele Castro
 */
public class LDAPUserImporterImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddUser() throws Exception {
		ConfigurationProvider<LDAPImportConfiguration>
			ldapImportConfigurationProvider = Mockito.mock(
				ConfigurationProvider.class);

		LDAPImportConfiguration ldapImportConfiguration = Mockito.mock(
			LDAPImportConfiguration.class);

		Mockito.when(
			ldapImportConfiguration.importUserPasswordEnabled()
		).thenReturn(
			true
		);

		Mockito.when(
			ldapImportConfigurationProvider.getConfiguration(Mockito.anyLong())
		).thenReturn(
			ldapImportConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_ldapImportConfigurationProvider",
			ldapImportConfigurationProvider);

		UserLocalService userLocalService = Mockito.mock(
			UserLocalService.class, Mockito.RETURNS_MOCKS);

		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_userLocalService", userLocalService);

		LDAPUser ldapUser = new LDAPUser();

		Contact contact = Mockito.mock(Contact.class);

		Mockito.when(
			contact.getBirthday()
		).thenReturn(
			new Date()
		);

		ldapUser.setContact(contact);

		ldapUser.setUser(Mockito.mock(User.class));

		String password = RandomTestUtil.randomString(13);

		_ldapUserImporterImpl.addUser(
			RandomTestUtil.randomLong(), ldapUser, password);

		_verifyAddUser(false, password, userLocalService);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_ldapUserImporterImpl.addUser(
				RandomTestUtil.randomLong(), ldapUser, password);
		}

		_verifyAddUser(true, null, userLocalService);
	}

	@Test
	public void testUpdateUser() throws Exception {
		ConfigurationProvider<LDAPImportConfiguration>
			ldapImportConfigurationProvider = Mockito.mock(
				ConfigurationProvider.class);

		Mockito.when(
			ldapImportConfigurationProvider.getConfiguration(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(LDAPImportConfiguration.class)
		);

		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_ldapImportConfigurationProvider",
			ldapImportConfigurationProvider);

		ConfigurationProvider<LDAPServerConfiguration>
			ldapServerConfigurationProvider = Mockito.mock(
				ConfigurationProvider.class);

		LDAPServerConfiguration ldapServerConfiguration = Mockito.mock(
			LDAPServerConfiguration.class);

		String modifiedDateString = "Thu Apr 2 19:18:33 GMT 2026";

		Mockito.when(
			ldapServerConfiguration.modifiedDate()
		).thenReturn(
			modifiedDateString
		);

		Mockito.when(
			ldapServerConfigurationProvider.getConfiguration(
				Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			ldapServerConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_ldapServerConfigurationProvider",
			ldapServerConfigurationProvider);

		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_ldapSettings",
			Mockito.mock(LDAPSettings.class));

		LDAPImportContext ldapImportContext = Mockito.mock(
			LDAPImportContext.class);

		Mockito.when(
			ldapImportContext.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			ldapImportContext.getLdapServerId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		LDAPUser ldapUser = new LDAPUser();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setLanguageId(LocaleUtil.BRAZIL.toLanguageTag());

		ldapUser.setServiceContext(serviceContext);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getModifiedDate()
		).thenReturn(
			new Date()
		);

		try (MockedStatic<DateUtil> dateUtilMockedStatic = Mockito.mockStatic(
				DateUtil.class, Mockito.CALLS_REAL_METHODS)) {

			ReflectionTestUtil.invoke(
				_ldapUserImporterImpl, "_updateUser",
				new Class<?>[] {
					LDAPImportContext.class, LDAPUser.class, User.class,
					String.class, String.class, boolean.class
				},
				ldapImportContext, ldapUser, user,
				RandomTestUtil.randomString(),
				String.valueOf(RandomTestUtil.randomLong()), false);

			dateUtilMockedStatic.verify(
				() -> DateUtil.parseDate(
					Mockito.eq("EEE MMM d HH:mm:ss zzz yyyy"),
					Mockito.eq(modifiedDateString), Mockito.eq(LocaleUtil.US)),
				Mockito.times(1));
		}
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		LDAPImportConfiguration ldapImportConfiguration = Mockito.mock(
			LDAPImportConfiguration.class);

		Mockito.when(
			ldapImportConfiguration.importUserPasswordAutogenerated()
		).thenReturn(
			true
		);

		UserLocalService userLocalService = Mockito.mock(
			UserLocalService.class);

		Mockito.when(
			userLocalService.updatePassword(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyBoolean(), Mockito.anyBoolean())
		).thenReturn(
			Mockito.mock(User.class)
		);

		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_userLocalService", userLocalService);

		String password = _updateUserPassword(
			ldapImportConfiguration, RandomTestUtil.randomString());

		Assert.assertTrue(
			password.length() < FIPSModeValidator.PASSWORDS_MIN_LENGTH);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			password = _updateUserPassword(
				ldapImportConfiguration, RandomTestUtil.randomString());

			Assert.assertEquals(
				FIPSModeValidator.PASSWORDS_MIN_LENGTH, password.length());

			Mockito.when(
				ldapImportConfiguration.importUserPasswordEnabled()
			).thenReturn(
				true
			);

			password = _updateUserPassword(
				ldapImportConfiguration, RandomTestUtil.randomString(13));

			Assert.assertEquals(
				FIPSModeValidator.PASSWORDS_MIN_LENGTH, password.length());

			String ldapPassword = RandomTestUtil.randomString(14);

			Assert.assertEquals(
				ldapPassword,
				_updateUserPassword(ldapImportConfiguration, ldapPassword));
		}
	}

	private String _updateUserPassword(
		LDAPImportConfiguration ldapImportConfiguration, String password) {

		return ReflectionTestUtil.invoke(
			_ldapUserImporterImpl, "_updateUserPassword",
			new Class<?>[] {
				long.class, LDAPImportConfiguration.class, long.class,
				String.class, String.class, boolean.class, Date.class
			},
			RandomTestUtil.randomLong(), ldapImportConfiguration,
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			password, false, new Date());
	}

	private void _verifyAddUser(
			boolean autoPassword, String password,
			UserLocalService userLocalService)
		throws Exception {

		Mockito.verify(
			userLocalService
		).addUser(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(autoPassword),
			Mockito.eq(password), Mockito.eq(password), Mockito.anyBoolean(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
			Mockito.any()
		);
	}

	private static final LDAPUserImporterImpl _ldapUserImporterImpl =
		new LDAPUserImporterImpl();

}