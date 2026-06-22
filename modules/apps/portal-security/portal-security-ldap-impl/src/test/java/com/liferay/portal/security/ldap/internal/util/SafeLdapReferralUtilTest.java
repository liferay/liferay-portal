/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, ""));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "  ldap://host:389  "));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "LDAP://host:389"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "corba://host/exploit"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "dns://host"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "host:389"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "http://host/exploit"));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class},
				"ldap://host1:389 ldap://host2:389"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class},
				"ldap://host1:389 rmi://host2:1099/exploit"));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "ldap://host:389"));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "ldaps://host:636"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "ldapx://host"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, "rmi://host:1099/exploit"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				SafeLdapReferralUtil.class, "_isAllowedReferralURL",
				new Class<?>[] {String.class}, (String)null));
	}

	@Test
	public void testSearch() throws Exception {
		DirContext dirContext = Mockito.mock(DirContext.class);

		NamingEnumeration<SearchResult> enumeration = Mockito.mock(
			NamingEnumeration.class);

		ReferralException referralException = Mockito.mock(
			ReferralException.class);

		Mockito.when(
			referralException.getReferralInfo()
		).thenReturn(
			"rmi://attacker:1099/exploit"
		);

		Mockito.when(
			referralException.skipReferral()
		).thenReturn(
			false
		);

		Mockito.when(
			enumeration.hasMore()
		).thenThrow(
			referralException
		);

		Mockito.when(
			dirContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			enumeration
		);

		NamingEnumeration<SearchResult> resultEnumeration =
			SafeLdapReferralUtil.search(
				dirContext, "(cn=*)", new Object[0], Mockito.mock(Name.class),
				new SearchControls());

		Assert.assertFalse(resultEnumeration.hasMore());

		Mockito.verify(
			referralException, Mockito.never()
		).getReferralContext();

		dirContext = Mockito.mock(DirContext.class);

		enumeration = Mockito.mock(NamingEnumeration.class);

		referralException = Mockito.mock(ReferralException.class);

		DirContext referralContext = Mockito.mock(DirContext.class);

		NamingEnumeration<SearchResult> referralEnumeration = Mockito.mock(
			NamingEnumeration.class);

		SearchResult searchResult = new SearchResult(
			"cn=test", null, new BasicAttributes());

		Mockito.when(
			referralException.getReferralInfo()
		).thenReturn(
			"ldap://other:389"
		);

		Mockito.when(
			referralException.skipReferral()
		).thenReturn(
			false
		);

		Mockito.when(
			referralException.getReferralContext()
		).thenReturn(
			referralContext
		);

		Mockito.when(
			referralEnumeration.hasMore()
		).thenReturn(
			true, false
		);

		Mockito.when(
			referralEnumeration.next()
		).thenReturn(
			searchResult
		);

		Mockito.when(
			enumeration.hasMore()
		).thenThrow(
			referralException
		);

		Mockito.when(
			dirContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			enumeration
		);

		Mockito.when(
			referralContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			referralEnumeration
		);

		resultEnumeration = SafeLdapReferralUtil.search(
			dirContext, "(cn=*)", new Object[0], Mockito.mock(Name.class),
			new SearchControls());

		Assert.assertTrue(resultEnumeration.hasMore());
		Assert.assertSame(searchResult, resultEnumeration.next());
		Assert.assertFalse(resultEnumeration.hasMore());

		Mockito.verify(
			referralException, Mockito.times(1)
		).getReferralContext();
	}

	@Test
	public void testSetProperties() {
		Map<String, String> environment = new HashMap<>();

		SafeLdapReferralUtil.setProperties(environment, "follow");

		Assert.assertEquals("throw", environment.get(Context.REFERRAL));

		_assertTrustURLCodebaseDisabled(environment);

		SafeLdapReferralUtil.setProperties(environment, "ignore");

		Assert.assertEquals("ignore", environment.get(Context.REFERRAL));

		_assertTrustURLCodebaseDisabled(environment);

		SafeLdapReferralUtil.setProperties(environment, "throw");

		Assert.assertEquals("throw", environment.get(Context.REFERRAL));

		_assertTrustURLCodebaseDisabled(environment);
	}

	private void _assertTrustURLCodebaseDisabled(
		Map<String, String> environment) {

		Assert.assertEquals(
			"false",
			environment.get("com.sun.jndi.cosnaming.object.trustURLCodebase"));
		Assert.assertEquals(
			"false",
			environment.get("com.sun.jndi.ldap.object.trustURLCodebase"));
		Assert.assertEquals(
			"false",
			environment.get("com.sun.jndi.rmi.object.trustURLCodebase"));
	}

}