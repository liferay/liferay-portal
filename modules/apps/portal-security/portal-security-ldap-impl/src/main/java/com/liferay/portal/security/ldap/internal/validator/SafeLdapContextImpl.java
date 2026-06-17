/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.ldap.SafeLdapContext;
import com.liferay.portal.security.ldap.SafeLdapFilter;
import com.liferay.portal.security.ldap.SafeLdapName;
import com.liferay.portal.security.ldap.SafeLdapNameFactory;
import com.liferay.portal.security.ldap.internal.util.SafeLDAPReferralUtil;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;

/**
 * @author Tomas Polesovsky
 */
public class SafeLdapContextImpl implements SafeLdapContext {

	public SafeLdapContextImpl(LdapContext ldapContext) {
		this(ldapContext, false);
	}

	public SafeLdapContextImpl(
		LdapContext ldapContext, boolean manageReferrals) {

		_ldapContext = ldapContext;
		_manageReferrals = manageReferrals;
	}

	@Override
	public Object addToEnvironment(String propertyName, Object propertyValue)
		throws NamingException {

		return _ldapContext.addToEnvironment(propertyName, propertyValue);
	}

	@Override
	public void bind(Name name, Object object) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.bind(name, object);
	}

	@Override
	public void bind(Name name, Object object, Attributes attributes)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.bind(name, object, attributes);
	}

	@Override
	public void bind(String name, Object object) throws NamingException {
		_logUnsafeMethod();

		_ldapContext.bind(SafeLdapNameFactory.fromUnsafe(name), object);
	}

	@Override
	public void bind(String name, Object object, Attributes attributes)
		throws NamingException {

		_logUnsafeMethod();

		_ldapContext.bind(
			SafeLdapNameFactory.fromUnsafe(name), object, attributes);
	}

	@Override
	public void close() throws NamingException {
		_ldapContext.close();
	}

	@Override
	public Name composeName(Name name, Name prefixName) throws NamingException {
		if (!(name instanceof SafeLdapName) ||
			!(prefixName instanceof SafeLdapName)) {

			_logUnsafeParameter();
		}

		return _ldapContext.composeName(name, prefixName);
	}

	@Override
	public String composeName(String name, String prefix)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.composeName(name, prefix);
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.createSubcontext(name);
	}

	@Override
	public DirContext createSubcontext(Name name, Attributes attributes)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.createSubcontext(name, attributes);
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		_logUnsafeMethod();

		return _ldapContext.createSubcontext(
			SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public DirContext createSubcontext(String name, Attributes attributes)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.createSubcontext(
			SafeLdapNameFactory.fromUnsafe(name), attributes);
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.destroySubcontext(name);
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		_logUnsafeMethod();

		_ldapContext.destroySubcontext(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public ExtendedResponse extendedOperation(ExtendedRequest request)
		throws NamingException {

		return _ldapContext.extendedOperation(request);
	}

	@Override
	public Attributes getAttributes(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.getAttributes(name);
	}

	@Override
	public Attributes getAttributes(Name name, String[] attrIds)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.getAttributes(name, attrIds);
	}

	@Override
	public Attributes getAttributes(String name) throws NamingException {
		_logUnsafeMethod();

		return _ldapContext.getAttributes(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public Attributes getAttributes(String name, String[] attrIds)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.getAttributes(
			SafeLdapNameFactory.fromUnsafe(name), attrIds);
	}

	@Override
	public Control[] getConnectControls() throws NamingException {
		return _ldapContext.getConnectControls();
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return _ldapContext.getEnvironment();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		return _ldapContext.getNameInNamespace();
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.getNameParser(name);
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		_logUnsafeMethod();

		return _ldapContext.getNameParser(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public Control[] getRequestControls() throws NamingException {
		return _ldapContext.getRequestControls();
	}

	@Override
	public Control[] getResponseControls() throws NamingException {
		return _ldapContext.getResponseControls();
	}

	@Override
	public DirContext getSchema(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.getSchema(name);
	}

	@Override
	public DirContext getSchema(String name) throws NamingException {
		_logUnsafeMethod();

		return _ldapContext.getSchema(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public DirContext getSchemaClassDefinition(Name name)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.getSchemaClassDefinition(name);
	}

	@Override
	public DirContext getSchemaClassDefinition(String name)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.getSchemaClassDefinition(
			SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.list(name);
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.list(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.listBindings(name);
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.listBindings(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.lookup(name);
	}

	@Override
	public Object lookup(String name) throws NamingException {
		_logUnsafeMethod();

		return _ldapContext.lookup(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.lookupLink(name);
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		_logUnsafeMethod();

		return _ldapContext.lookupLink(SafeLdapNameFactory.fromUnsafe(name));
	}

	@Override
	public void modifyAttributes(Name name, int modOp, Attributes attributes)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.modifyAttributes(name, modOp, attributes);
	}

	@Override
	public void modifyAttributes(Name name, ModificationItem[] mods)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.modifyAttributes(name, mods);
	}

	@Override
	public void modifyAttributes(String name, int modOp, Attributes attributes)
		throws NamingException {

		_logUnsafeMethod();

		_ldapContext.modifyAttributes(
			SafeLdapNameFactory.fromUnsafe(name), modOp, attributes);
	}

	@Override
	public void modifyAttributes(String name, ModificationItem[] mods)
		throws NamingException {

		_logUnsafeMethod();

		_ldapContext.modifyAttributes(
			SafeLdapNameFactory.fromUnsafe(name), mods);
	}

	@Override
	public LdapContext newInstance(Control[] requestControls)
		throws NamingException {

		return _ldapContext.newInstance(requestControls);
	}

	@Override
	public void rebind(Name name, Object object) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.rebind(name, object);
	}

	@Override
	public void rebind(Name name, Object object, Attributes attributes)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.rebind(name, object, attributes);
	}

	@Override
	public void rebind(String name, Object object) throws NamingException {
		_logUnsafeMethod();

		_ldapContext.rebind(SafeLdapNameFactory.fromUnsafe(name), object);
	}

	@Override
	public void rebind(String name, Object object, Attributes attributes)
		throws NamingException {

		_logUnsafeMethod();

		_ldapContext.rebind(
			SafeLdapNameFactory.fromUnsafe(name), object, attributes);
	}

	@Override
	public void reconnect(Control[] connCtls) throws NamingException {
		_ldapContext.reconnect(connCtls);
	}

	@Override
	public Object removeFromEnvironment(String propName)
		throws NamingException {

		return _ldapContext.removeFromEnvironment(propName);
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		if (!(oldName instanceof SafeLdapName) ||
			!(newName instanceof SafeLdapName)) {

			_logUnsafeParameter();
		}

		_ldapContext.rename(oldName, newName);
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		_logUnsafeMethod();

		_ldapContext.rename(
			SafeLdapNameFactory.fromUnsafe(oldName),
			SafeLdapNameFactory.fromUnsafe(newName));
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			Name name, Attributes matchingAttributes)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.search(name, matchingAttributes);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			Name name, Attributes matchingAttributes,
			String[] attributesToReturn)
		throws NamingException {

		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		return _ldapContext.search(
			name, matchingAttributes, attributesToReturn);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			Name name, String filterExpr, Object[] filterArgs,
			SearchControls cons)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.search(name, filterExpr, filterArgs, cons);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			Name name, String filter, SearchControls cons)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.search(name, filter, cons);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			SafeLdapName safeLdapName, SafeLdapFilter safeLdapFilter,
			SearchControls searchControls)
		throws NamingException {

		if (_manageReferrals) {
			return SafeLDAPReferralUtil.search(
				_ldapContext, safeLdapFilter.getFilterString(),
				safeLdapFilter.getArguments(), safeLdapName, searchControls);
		}

		return _ldapContext.search(
			safeLdapName, safeLdapFilter.getFilterString(),
			safeLdapFilter.getArguments(), searchControls);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			String name, Attributes matchingAttributes)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.search(
			SafeLdapNameFactory.fromUnsafe(name), matchingAttributes);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			String name, Attributes matchingAttributes,
			String[] attributesToReturn)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.search(
			SafeLdapNameFactory.fromUnsafe(name), matchingAttributes,
			attributesToReturn);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			String name, String filter, Object[] filterArgs,
			SearchControls cons)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.search(
			SafeLdapNameFactory.fromUnsafe(name), filter, filterArgs, cons);
	}

	@Override
	public NamingEnumeration<SearchResult> search(
			String name, String filter, SearchControls cons)
		throws NamingException {

		_logUnsafeMethod();

		return _ldapContext.search(
			SafeLdapNameFactory.fromUnsafe(name), filter, cons);
	}

	@Override
	public void setRequestControls(Control[] requestControls)
		throws NamingException {

		_ldapContext.setRequestControls(requestControls);
	}

	@Override
	public String toString() {
		return "{_ldapContext=" + _ldapContext + '}';
	}

	@Override
	public void unbind(Name name) throws NamingException {
		if (!(name instanceof SafeLdapName)) {
			_logUnsafeParameter();
		}

		_ldapContext.unbind(name);
	}

	@Override
	public void unbind(String name) throws NamingException {
		_logUnsafeMethod();

		_ldapContext.unbind(SafeLdapNameFactory.fromUnsafe(name));
	}

	private void _log(String s) {
		if (_log.isDebugEnabled()) {
			_log.debug(s, new Exception());
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(s);
		}
	}

	private void _logUnsafeMethod() {
		_log("Unsafe LDAP method used");
	}

	private void _logUnsafeParameter() {
		_log("Unsafe LDAP parameter used");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SafeLdapContextImpl.class);

	private final LdapContext _ldapContext;
	private final boolean _manageReferrals;

}