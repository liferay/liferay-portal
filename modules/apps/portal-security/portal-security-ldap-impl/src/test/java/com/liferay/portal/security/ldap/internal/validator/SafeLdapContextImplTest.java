/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.validator;

import com.liferay.portal.security.ldap.SafeLdapFilter;
import com.liferay.portal.security.ldap.SafeLdapName;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class SafeLdapContextImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSearch() throws Exception {
		LdapContext ldapContext = Mockito.mock(LdapContext.class);

		SearchResult searchResult = new SearchResult(
			"cn=test", null, new BasicAttributes());

		NamingEnumeration<SearchResult> enumeration = Mockito.mock(
			NamingEnumeration.class);

		Mockito.when(
			enumeration.hasMore()
		).thenReturn(
			true, false
		);

		Mockito.when(
			enumeration.next()
		).thenReturn(
			searchResult
		);

		Mockito.when(
			ldapContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			enumeration
		);

		SafeLdapContextImpl safeLdapContextImpl = new SafeLdapContextImpl(
			ldapContext, true);

		NamingEnumeration<SearchResult> resultEnumeration =
			safeLdapContextImpl.search(
				Mockito.mock(SafeLdapName.class), _mockSafeLdapFilter(),
				new SearchControls());

		Assert.assertTrue(resultEnumeration.hasMore());
		Assert.assertSame(searchResult, resultEnumeration.next());
		Assert.assertFalse(resultEnumeration.hasMore());

		ldapContext = Mockito.mock(LdapContext.class);
		enumeration = Mockito.mock(NamingEnumeration.class);

		Mockito.when(
			ldapContext.search(
				Mockito.any(Name.class), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.any(SearchControls.class))
		).thenReturn(
			enumeration
		);

		safeLdapContextImpl = new SafeLdapContextImpl(ldapContext, false);

		resultEnumeration = safeLdapContextImpl.search(
			Mockito.mock(SafeLdapName.class), _mockSafeLdapFilter(),
			new SearchControls());

		Assert.assertSame(enumeration, resultEnumeration);
	}

	private SafeLdapFilter _mockSafeLdapFilter() {
		SafeLdapFilter safeLdapFilter = Mockito.mock(SafeLdapFilter.class);

		Mockito.when(
			safeLdapFilter.getArguments()
		).thenReturn(
			new Object[0]
		);

		Mockito.when(
			safeLdapFilter.getFilterString()
		).thenReturn(
			"(cn=*)"
		);

		return safeLdapFilter;
	}

}