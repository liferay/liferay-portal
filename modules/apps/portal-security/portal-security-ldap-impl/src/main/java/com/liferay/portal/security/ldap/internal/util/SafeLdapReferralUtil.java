/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.util;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.constants.LDAPConstants;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ReferralException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * @author Lucas Miranda
 */
public class SafeLdapReferralUtil {

	public static NamingEnumeration<SearchResult> search(
			String filter, Object[] filterArguments, Name name,
			DirContext rootDirContext, SearchControls searchControls)
		throws NamingException {

		ListNamingEnumeration listNamingEnumeration =
			new ListNamingEnumeration();

		Queue<DirContext> dirContexts = new ArrayDeque<>();

		dirContexts.add(rootDirContext);

		int referralCount = 0;

		try {
			while (!dirContexts.isEmpty()) {
				DirContext dirContext = dirContexts.poll();

				NamingEnumeration<SearchResult> enumeration = null;

				try {
					enumeration = dirContext.search(
						name, filter, filterArguments, searchControls);

					listNamingEnumeration.addAll(enumeration);
				}
				catch (ReferralException referralException) {
					boolean skipReferral = true;

					while (skipReferral) {
						Object referralInfo =
							referralException.getReferralInfo();

						if ((referralInfo instanceof String) &&
							_isAllowedReferralURL((String)referralInfo) &&
							(referralCount < _MAX_REFERRAL_COUNT)) {

							dirContexts.add(
								(DirContext)
									referralException.getReferralContext());

							referralCount++;
						}

						skipReferral = referralException.skipReferral();
					}
				}
				finally {
					if (enumeration != null) {
						enumeration.close();
					}

					if (dirContext != rootDirContext) {
						dirContext.close();
					}
				}
			}
		}
		finally {
			for (DirContext dirContext : dirContexts) {
				if (dirContext == rootDirContext) {
					continue;
				}

				dirContext.close();
			}
		}

		return listNamingEnumeration;
	}

	public static void setProperties(
		Map<? super String, ? super String> environment, String referral) {

		environment.put(
			"com.sun.jndi.cosnaming.object.trustURLCodebase", "false");
		environment.put("com.sun.jndi.ldap.object.trustURLCodebase", "false");
		environment.put("com.sun.jndi.rmi.object.trustURLCodebase", "false");

		if (Objects.equals(referral, LDAPConstants.REFERRAL_FOLLOW)) {
			environment.put(Context.REFERRAL, LDAPConstants.REFERRAL_THROW);
		}
		else {
			environment.put(Context.REFERRAL, referral);
		}
	}

	private static boolean _isAllowedReferralURL(String url) {
		if (Validator.isNull(url)) {
			return false;
		}

		String normalizedURL = StringUtil.toLowerCase(StringUtil.trim(url));

		for (String referralURL : normalizedURL.split("\\s+")) {
			if (!referralURL.startsWith("ldap://") &&
				!referralURL.startsWith("ldaps://")) {

				return false;
			}
		}

		return true;
	}

	private static final int _MAX_REFERRAL_COUNT = 10;

	private static class ListNamingEnumeration
		implements NamingEnumeration<SearchResult> {

		public void addAll(NamingEnumeration<SearchResult> namingEnumeration)
			throws NamingException {

			while (namingEnumeration.hasMore()) {
				_searchResults.add(namingEnumeration.next());
			}
		}

		@Override
		public void close() {
		}

		@Override
		public boolean hasMore() {
			Iterator<SearchResult> iterator = _getIterator();

			return iterator.hasNext();
		}

		@Override
		public boolean hasMoreElements() {
			Iterator<SearchResult> iterator = _getIterator();

			return iterator.hasNext();
		}

		@Override
		public SearchResult next() {
			Iterator<SearchResult> iterator = _getIterator();

			return iterator.next();
		}

		@Override
		public SearchResult nextElement() {
			Iterator<SearchResult> iterator = _getIterator();

			return iterator.next();
		}

		private Iterator<SearchResult> _getIterator() {
			if (_iterator == null) {
				_iterator = _searchResults.iterator();
			}

			return _iterator;
		}

		private Iterator<SearchResult> _iterator;
		private final List<SearchResult> _searchResults = new ArrayList<>();

	}

}