/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.ReferralException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class SafeLdapReferralUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsAllowedReferralURL() {
		_testIsAllowedReferralURL(
			false, "corba://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(
			false, "dns://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(
			false,
			RandomTestUtil.randomString() + ":" + RandomTestUtil.randomInt());
		_testIsAllowedReferralURL(
			false, "http://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(
			false,
			StringBundler.concat(
				"ldap://", RandomTestUtil.randomString(), " rmi://",
				RandomTestUtil.randomString()));
		_testIsAllowedReferralURL(
			false, "ldapx://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(
			false, "rmi://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(false, StringPool.BLANK);
		_testIsAllowedReferralURL(false, null);
		_testIsAllowedReferralURL(
			true, "  ldap://" + RandomTestUtil.randomString() + "  ");
		_testIsAllowedReferralURL(
			true, "LDAP://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(
			true,
			StringBundler.concat(
				"ldap://", RandomTestUtil.randomString(), " ldap://",
				RandomTestUtil.randomString()));
		_testIsAllowedReferralURL(
			true, "ldap://" + RandomTestUtil.randomString());
		_testIsAllowedReferralURL(
			true, "ldaps://" + RandomTestUtil.randomString());
	}

	@Test
	public void testSearch() throws Exception {
		NamingEnumeration<SearchResult> enumeration = Mockito.mock(
			NamingEnumeration.class);

		Mockito.when(
			enumeration.hasMore()
		).thenReturn(
			true, false
		);

		SearchResult searchResult = new SearchResult(
			"cn=test", null, new BasicAttributes());

		Mockito.when(
			enumeration.next()
		).thenReturn(
			searchResult
		);

		DirContext dirContext = Mockito.mock(DirContext.class);

		Mockito.when(
			dirContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			enumeration
		);

		ReferralException referralException = _mockReferralException(
			dirContext, "ldap://" + RandomTestUtil.randomString());

		NamingEnumeration<SearchResult> resultEnumeration =
			SafeLdapReferralUtil.search(
				"(cn=*)", new Object[0], Mockito.mock(Name.class),
				_mockRootDirContext(referralException), new SearchControls());

		Assert.assertTrue(resultEnumeration.hasMore());
		Assert.assertSame(searchResult, resultEnumeration.next());
		Assert.assertFalse(resultEnumeration.hasMore());

		Mockito.verify(
			referralException, Mockito.times(1)
		).getReferralContext();

		referralException = _mockReferralException(
			null, "rmi://" + RandomTestUtil.randomString());

		resultEnumeration = SafeLdapReferralUtil.search(
			"(cn=*)", new Object[0], Mockito.mock(Name.class),
			_mockRootDirContext(referralException), new SearchControls());

		Assert.assertFalse(resultEnumeration.hasMore());

		Mockito.verify(
			referralException, Mockito.never()
		).getReferralContext();
	}

	@Test
	public void testSetProperties() {
		Map<String, String> environment = new HashMap<>();

		SafeLdapReferralUtil.setProperties(
			environment, LDAPConstants.REFERRAL_FOLLOW);

		Assert.assertEquals(
			LDAPConstants.REFERRAL_THROW, environment.get(Context.REFERRAL));
		_assertTrustURLCodebaseDisabled(environment);

		SafeLdapReferralUtil.setProperties(
			environment, LDAPConstants.REFERRAL_IGNORE);

		Assert.assertEquals(
			LDAPConstants.REFERRAL_IGNORE, environment.get(Context.REFERRAL));
		_assertTrustURLCodebaseDisabled(environment);

		SafeLdapReferralUtil.setProperties(
			environment, LDAPConstants.REFERRAL_THROW);

		Assert.assertEquals(
			LDAPConstants.REFERRAL_THROW, environment.get(Context.REFERRAL));
		_assertTrustURLCodebaseDisabled(environment);
	}

	private void _assertTrustURLCodebaseDisabled(
		Map<String, String> environment) {

		Assert.assertEquals(
			StringPool.FALSE,
			environment.get("com.sun.jndi.cosnaming.object.trustURLCodebase"));
		Assert.assertEquals(
			StringPool.FALSE,
			environment.get("com.sun.jndi.ldap.object.trustURLCodebase"));
		Assert.assertEquals(
			StringPool.FALSE,
			environment.get("com.sun.jndi.rmi.object.trustURLCodebase"));
	}

	private ReferralException _mockReferralException(
			DirContext dirContext, String referralInfo)
		throws Exception {

		ReferralException referralException = Mockito.mock(
			ReferralException.class);

		if (dirContext != null) {
			Mockito.when(
				referralException.getReferralContext()
			).thenReturn(
				dirContext
			);
		}

		Mockito.when(
			referralException.getReferralInfo()
		).thenReturn(
			referralInfo
		);

		Mockito.when(
			referralException.skipReferral()
		).thenReturn(
			false
		);

		return referralException;
	}

	private DirContext _mockRootDirContext(ReferralException referralException)
		throws Exception {

		DirContext rootDirContext = Mockito.mock(DirContext.class);

		NamingEnumeration<SearchResult> enumeration = Mockito.mock(
			NamingEnumeration.class);

		Mockito.when(
			enumeration.hasMore()
		).thenThrow(
			referralException
		);

		Mockito.when(
			rootDirContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			enumeration
		);

		return rootDirContext;
	}

	private void _testIsAllowedReferralURL(boolean expected, String urlString) {
		Assert.assertEquals(
			expected,
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, urlString));
	}

}