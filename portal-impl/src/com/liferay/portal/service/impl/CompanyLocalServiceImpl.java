/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.instance.lifecycle.PortalInstanceLifecycleManager;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiServiceUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.service.persistence.CompanyInfoPersistence;
import com.liferay.portal.kernel.service.persistence.ContactPersistence;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesPersistence;
import com.liferay.portal.kernel.service.persistence.PortletPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.VirtualHostPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.auth.EmailAddressValidatorFactory;
import com.liferay.portal.service.base.CompanyLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.IDN;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Provides the local service for adding, checking, and updating companies. Each
 * company refers to a separate portal instance.
 *
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
public class CompanyLocalServiceImpl extends CompanyLocalServiceBaseImpl {

	public CompanyLocalServiceImpl() {
		_serviceTracker = new ServiceTracker<>(
			_bundleContext, PortalInstanceLifecycleManager.class,
			new PortalInstanceLifecycleManagerServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	@Override
	public Company addCompany(Company company) {
		_companyInfoPersistence.update(company.getCompanyInfo());

		return super.addCompany(company);
	}

	/**
	 * Adds a company with the primary key.
	 *
	 * @param  companyId the primary key of the company (optionally <code>null</code> or
	 *         <code>0</code> to generate a key automatically)
	 * @param  webId the the company's web domain
	 * @param  virtualHostname the company's virtual host name
	 * @param  mx the company's mail domain
	 * @param  maxUsers the max number of company users (optionally
	 *         <code>0</code>)
	 * @param  active whether the company is active
	 * @return the company
	 */
	@Override
	public Company addCompany(
			Long companyId, String webId, String virtualHostname, String mx,
			int maxUsers, boolean active, boolean addDefaultAdminUser,
			String defaultAdminPassword, String defaultAdminScreenName,
			String defaultAdminEmailAddress, String defaultAdminFirstName,
			String defaultAdminMiddleName, String defaultAdminLastName)
		throws PortalException {

		// Company

		String lowerCaseVirtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		validateWebId(webId);
		validateVirtualHost(webId, lowerCaseVirtualHostname);
		validateMx(-1, mx);

		if ((companyId == null) || (companyId == 0)) {
			companyId = _getNextCompanyId();
		}

		Company company = companyPersistence.create(companyId);

		company.setUserId(0);
		company.setUserName(StringPool.BLANK);
		company.setCreateDate(new Date());
		company.setModifiedDate(new Date());

		if (webId.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {
			DBPartitionUtil.setDefaultCompanyId(company.getCompanyId());
		}

		boolean newDBPartitionAdded = DBPartitionUtil.addDBPartition(companyId);

		SafeCloseable safeCloseable =
			CompanyThreadLocal.setInitializingCompanyIdWithSafeCloseable(
				company.getCompanyId());

		try {
			return _transactionAwareInvoke(
				() -> {
					company.setWebId(webId);
					company.setMx(mx);
					company.setMaxUsers(maxUsers);
					company.setActive(active);

					String name = webId;

					if (webId.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {
						name = PropsValues.COMPANY_DEFAULT_NAME;
					}

					company.setName(name);

					Company updatedCompany = companyPersistence.update(company);

					User guestUser = _addGuestUser(updatedCompany);

					// Virtual host

					updateVirtualHostname(
						updatedCompany.getCompanyId(),
						lowerCaseVirtualHostname);

					if (newDBPartitionAdded) {
						_dlFileEntryTypeLocalService.
							createBasicDocumentDLFileEntryType();
					}

					// Company info

					try {
						updatedCompany.setKey(
							EncryptorUtil.serializeKey(
								EncryptorUtil.generateKey()));
					}
					catch (EncryptorException encryptorException) {
						throw new SystemException(encryptorException);
					}

					_companyInfoPersistence.update(
						updatedCompany.getCompanyInfo());

					// Demo settings

					if (webId.equals("liferay.net")) {
						_addDemoSettings(updatedCompany);
					}

					updatedCompany = checkCompany(updatedCompany, true);

					if (addDefaultAdminUser) {
						_userLocalService.addDefaultAdminUser(
							updatedCompany.getCompanyId(),
							GetterUtil.getString(
								defaultAdminPassword,
								PropsValues.DEFAULT_ADMIN_PASSWORD),
							GetterUtil.getString(
								defaultAdminScreenName,
								PropsValues.DEFAULT_ADMIN_SCREEN_NAME),
							GetterUtil.getString(
								defaultAdminEmailAddress,
								PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX +
									"@" + mx),
							guestUser.getLocale(),
							GetterUtil.getString(
								defaultAdminFirstName,
								PropsValues.DEFAULT_ADMIN_FIRST_NAME),
							GetterUtil.getString(
								defaultAdminMiddleName,
								PropsValues.DEFAULT_ADMIN_MIDDLE_NAME),
							GetterUtil.getString(
								defaultAdminLastName,
								PropsValues.DEFAULT_ADMIN_LAST_NAME));
					}

					// Guest user must have the Guest role

					Role guestRole = _roleLocalService.getRole(
						updatedCompany.getCompanyId(), RoleConstants.GUEST);

					_roleLocalService.setUserRoles(
						guestUser.getUserId(),
						new long[] {guestRole.getRoleId()});

					return updatedCompany;
				});
		}
		catch (Throwable throwable) {
			try {
				if (newDBPartitionAdded) {
					long addedCompanyId = companyId;

					_transactionAwareInvoke(
						() -> {
							DBPartitionUtil.removeDBPartition(addedCompanyId);

							return null;
						});
				}
			}
			finally {
				safeCloseable.close();
			}

			throw new PortalException(throwable);
		}
		finally {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					safeCloseable.close();

					return null;
				});
		}
	}

	@Override
	public Company addDBPartitionCompany(
			long companyId, String name, String virtualHostname, String webId)
		throws PortalException {

		FeatureFlagManagerUtil.checkEnabled("LPD-11342");

		if (!DBPartition.isPartitionEnabled()) {
			throw new UnsupportedOperationException(
				"Database partitioning must be enabled");
		}

		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			throw new IllegalArgumentException(
				"Company ID " + companyId + " is the default company ID");
		}

		SafeCloseable safeCloseable1 =
			PortalInstances.setInsertionInProcessCompanyIdWithSafeCloseable(
				companyId);

		try {
			DBPartitionUtil.importDBPartition(companyId);
		}
		catch (Throwable throwable) {
			safeCloseable1.close();

			throw throwable;
		}

		SafeCloseable safeCloseable2 =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);

		companyPersistence.clearCache();
		_virtualHostPersistence.clearCache();

		try {
			return _transactionAwareInvoke(
				() -> {
					Company company = companyPersistence.findByPrimaryKey(
						companyId);

					if (Validator.isNotNull(name) &&
						!StringUtil.equals(company.getName(), name)) {

						validateName(companyId, name);

						company.setName(name);

						company = companyPersistence.update(company);
					}

					String lowerCaseVirtualHostname = StringUtil.toLowerCase(
						StringUtil.trim(virtualHostname));

					if (Validator.isNotNull(lowerCaseVirtualHostname) &&
						!StringUtil.equals(
							company.getVirtualHostname(),
							lowerCaseVirtualHostname)) {

						validateVirtualHost(
							company.getWebId(), lowerCaseVirtualHostname);

						company = updateVirtualHostname(
							companyId, lowerCaseVirtualHostname);
					}

					if (Validator.isNotNull(webId) &&
						!StringUtil.equals(company.getWebId(), webId)) {

						validateWebId(webId);

						company.setWebId(webId);

						company = companyPersistence.update(company);
					}

					return _addDBPartitionCompany(company);
				});
		}
		catch (Throwable throwable) {
			try (SafeCloseable safeCloseable3 =
					PortalInstances.
						setCompanyInDeletionProcessWithSafeCloseable(
							companyId)) {

				_transactionAwareInvoke(
					() -> {
						exportCompany(companyId);

						DBPartitionUtil.removeDBPartition(companyId);

						return null;
					});
			}
			finally {
				safeCloseable1.close();
				safeCloseable2.close();
			}

			throw new PortalException(throwable);
		}
		finally {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					safeCloseable1.close();
					safeCloseable2.close();

					return null;
				});
		}
	}

	@Override
	public Company checkCompany(Company company, boolean newCompany)
		throws PortalException {

		Locale localeThreadLocalDefaultLocale =
			LocaleThreadLocal.getDefaultLocale();
		Locale localeThreadSiteDefaultLocale =
			LocaleThreadLocal.getSiteDefaultLocale();

		try {
			preregisterCompany(company);

			Locale companyDefaultLocale = LocaleUtil.fromLanguageId(
				PropsValues.COMPANY_DEFAULT_LOCALE);

			LocaleThreadLocal.setDefaultLocale(companyDefaultLocale);

			LocaleThreadLocal.setSiteDefaultLocale(null);

			// Key

			checkCompanyKey(company.getCompanyId());

			// Virtual host

			company = syncVirtualHost(company);

			// Resource actions

			if (DBPartition.isPartitionEnabled()) {
				_resourceActionLocalService.checkResourceActions();
			}

			// System roles

			_roleLocalService.checkSystemRoles(company.getCompanyId());

			// System groups

			_groupLocalService.checkSystemGroups(company.getCompanyId());

			// Default password policy

			_passwordPolicyLocalService.checkDefaultPasswordPolicy(
				company.getCompanyId());

			// Portlets

			_portletLocalService.checkPortlets(company.getCompanyId());

			Company finalCompany = company;

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					registerCompany(finalCompany);

					if (newCompany) {
						PortalInstances.initCompany(finalCompany, true);

						_synchronizePortalInstances();
					}

					return null;
				});

			// Preferences

			_portalPreferencesLocalService.getPreferences(
				company.getCompanyId(), PortletKeys.PREFS_OWNER_TYPE_COMPANY);
		}
		finally {
			LocaleThreadLocal.setDefaultLocale(localeThreadLocalDefaultLocale);
			LocaleThreadLocal.setSiteDefaultLocale(
				localeThreadSiteDefaultLocale);
		}

		return company;
	}

	/**
	 * Returns the company with the web domain.
	 *
	 * The method sets mail domain to the web domain to the default name set in
	 * portal.properties
	 *
	 * @param  webId the company's web domain
	 * @return the company with the web domain
	 */
	@Override
	public Company checkCompany(String webId) throws PortalException {
		Company company = getCompanyByWebId(webId);

		return checkCompany(company, false);
	}

	/**
	 * Checks if the company has an encryption key. It will create a key if one
	 * does not exist.
	 *
	 * @param companyId the primary key of the company
	 */
	@Override
	public void checkCompanyKey(long companyId) throws PortalException {
		Company company = companyPersistence.findByPrimaryKey(companyId);

		if (company.getKeyObj() != null) {
			return;
		}

		try {
			company.setKey(
				EncryptorUtil.serializeKey(EncryptorUtil.generateKey()));
		}
		catch (EncryptorException encryptorException) {
			throw new SystemException(encryptorException);
		}

		_companyInfoPersistence.update(company.getCompanyInfo());
	}

	@Override
	public Company copyDBPartitionCompany(
			long fromCompanyId, Long toCompanyId, String name,
			String virtualHostname, String webId)
		throws PortalException {

		FeatureFlagManagerUtil.checkEnabled("LPD-11342");

		if (!DBPartition.isPartitionEnabled()) {
			throw new UnsupportedOperationException(
				"Database partitioning must be enabled");
		}

		Company fromCompany = companyPersistence.findByPrimaryKey(
			fromCompanyId);

		if (fromCompany == null) {
			throw new IllegalArgumentException(
				"Company ID " + fromCompanyId + " does not exist");
		}

		if (fromCompanyId == PortalInstancePool.getDefaultCompanyId()) {
			throw new IllegalArgumentException(
				"Company ID " + fromCompanyId + " is the default company ID");
		}

		if (toCompanyId == null) {
			toCompanyId = _getNextCompanyId();
		}
		else if ((toCompanyId == 0) ||
				 ArrayUtil.contains(
					 PortalInstancePool.getCompanyIds(), toCompanyId)) {

			throw new IllegalArgumentException(
				"Company ID " + toCompanyId + " already exists");
		}

		if (Validator.isNull(name)) {
			throw new IllegalArgumentException("Name must not be null");
		}

		if (Validator.isNull(virtualHostname)) {
			throw new IllegalArgumentException(
				"Virtual hostname must not be null");
		}

		if (Validator.isNull(webId)) {
			throw new IllegalArgumentException("Web ID must not be null");
		}

		validateName(toCompanyId, name);

		String lowerCaseVirtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		validateVirtualHost(webId, lowerCaseVirtualHostname);

		validateWebId(webId);

		SafeCloseable safeCloseable1 =
			PortalInstances.setCopyInProcessCompanyIdWithSafeCloseable(
				fromCompanyId);

		try {
			DBPartitionUtil.copyDBPartition(fromCompanyId, toCompanyId);
		}
		catch (Throwable throwable) {
			safeCloseable1.close();

			throw throwable;
		}

		SafeCloseable safeCloseable2 =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(toCompanyId);

		long companyId = toCompanyId;

		try {
			return _transactionAwareInvoke(
				() -> {
					Company company = fromCompany.cloneWithOriginalValues();

					company.setCompanyId(companyId);
					company.setWebId(webId);
					company.setName(name);
					company.setNew(true);

					company = companyPersistence.update(company);

					company = updateVirtualHostname(
						company.getCompanyId(), lowerCaseVirtualHostname);

					return _addDBPartitionCompany(company);
				});
		}
		catch (Throwable throwable) {
			try (SafeCloseable safeCloseable3 =
					PortalInstances.
						setCompanyInDeletionProcessWithSafeCloseable(
							companyId)) {

				_transactionAwareInvoke(
					() -> {
						DBPartitionUtil.removeDBPartition(companyId);

						return null;
					});
			}
			finally {
				safeCloseable1.close();
				safeCloseable2.close();
			}

			throw new PortalException(throwable);
		}
		finally {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					safeCloseable1.close();
					safeCloseable2.close();

					return null;
				});
		}
	}

	@Override
	public Company deleteCompany(Company company) throws PortalException {
		return deleteCompany(company.getCompanyId());
	}

	@Override
	public Company deleteCompany(long companyId) throws PortalException {
		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			throw new RequiredCompanyException(
				"Select another default company before deleting company " +
					companyId);
		}

		try (SafeCloseable safeCloseable1 =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);
			SafeCloseable safeCloseable2 =
				PortalInstances.setCompanyInDeletionProcessWithSafeCloseable(
					companyId)) {

			return doDeleteCompany(companyId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			throw portalException;
		}
	}

	/**
	 * Deletes the company's logo.
	 *
	 * @param  companyId the primary key of the company
	 * @return the deleted logo's company
	 */
	@Override
	public Company deleteLogo(long companyId) throws PortalException {
		Company company = companyPersistence.findByPrimaryKey(companyId);

		PortalUtil.updateImageId(company, false, null, "logoId", 0, 0, 0);

		return company;
	}

	@Override
	public void destroy() {
		super.destroy();

		_serviceTracker.close();
	}

	@Override
	public Company exportCompany(long companyId) throws PortalException {
		FeatureFlagManagerUtil.checkEnabled("LPD-11342");

		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			throw new RequiredCompanyException(
				"Select another default company before exporting company " +
					companyId);
		}

		Company company = companyPersistence.findByPrimaryKey(companyId);

		try {
			if (!DBPartition.isPartitionEnabled()) {
				DBPartitionUtil.exportCompany(companyId);

				return company;
			}

			DBPartitionUtil.exportDBPartition(companyId);
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}

		return company;
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param  companyId the primary key of the company
	 * @return the company with the primary key, <code>null</code> if a company
	 *         with the primary key could not be found
	 */
	@Override
	public Company fetchCompanyById(long companyId) {
		return companyPersistence.fetchByPrimaryKey(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param  virtualHostname the virtual host name
	 * @return the company with the virtual host name, <code>null</code> if a
	 *         company with the virtual host could not be found
	 */
	@Override
	public Company fetchCompanyByVirtualHost(String virtualHostname) {
		virtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		VirtualHost virtualHost = _virtualHostPersistence.fetchByHostname(
			virtualHostname);

		if ((virtualHost == null) && virtualHostname.contains("xn--")) {
			virtualHost = _virtualHostPersistence.fetchByHostname(
				IDN.toUnicode(virtualHostname));
		}

		if ((virtualHost == null) || (virtualHost.getLayoutSetId() != 0)) {
			return null;
		}

		return companyPersistence.fetchByPrimaryKey(virtualHost.getCompanyId());
	}

	@Override
	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompany(
			UnsafeConsumer<Company, E> unsafeConsumer)
		throws E {

		List<Company> companies = null;

		if (!CompanyThreadLocal.isLocked()) {
			companies = companyLocalService.getCompanies();
		}

		forEachCompany(unsafeConsumer, companies);
	}

	@Override
	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompany(
			UnsafeConsumer<Company, E> unsafeConsumer, List<Company> companies)
		throws E {

		if (CompanyThreadLocal.isLocked()) {
			unsafeConsumer.accept(
				companyLocalService.fetchCompanyById(
					CompanyThreadLocal.getCompanyId()));

			return;
		}

		for (Company company : companies) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						company.getCompanyId())) {

				unsafeConsumer.accept(company);
			}
		}
	}

	@Override
	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompanyId(
			UnsafeConsumer<Long, E> unsafeConsumer)
		throws E {

		long[] companyIds = null;

		if (!CompanyThreadLocal.isLocked()) {
			companyIds = ListUtil.toLongArray(
				companyLocalService.getCompanies(), Company::getCompanyId);
		}

		forEachCompanyId(unsafeConsumer, companyIds);
	}

	@Override
	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompanyId(
			UnsafeConsumer<Long, E> unsafeConsumer, long[] companyIds)
		throws E {

		if (CompanyThreadLocal.isLocked()) {
			unsafeConsumer.accept(CompanyThreadLocal.getCompanyId());

			return;
		}

		for (long companyId : companyIds) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				unsafeConsumer.accept(companyId);
			}
		}
	}

	/**
	 * Returns all the companies.
	 *
	 * @return the companies
	 */
	@Override
	public List<Company> getCompanies() {
		return companyPersistence.findAll();
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param  companyId the primary key of the company
	 * @return the company with the primary key
	 */
	@Override
	public Company getCompanyById(long companyId) throws PortalException {
		return companyPersistence.findByPrimaryKey(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param  virtualHostname the company's virtual host name
	 * @return the company with the virtual host name
	 */
	@Override
	public Company getCompanyByVirtualHost(String virtualHostname)
		throws PortalException {

		try {
			virtualHostname = StringUtil.toLowerCase(
				StringUtil.trim(virtualHostname));

			VirtualHost virtualHost = _virtualHostLocalService.fetchVirtualHost(
				virtualHostname);

			if ((virtualHost == null) && virtualHostname.contains("xn--")) {
				virtualHost = _virtualHostPersistence.findByHostname(
					IDN.toUnicode(virtualHostname));
			}

			if (virtualHost.getLayoutSetId() != 0) {
				throw new CompanyVirtualHostException(
					"Virtual host is associated with layout set " +
						virtualHost.getLayoutSetId());
			}

			return companyPersistence.findByPrimaryKey(
				virtualHost.getCompanyId());
		}
		catch (NoSuchVirtualHostException noSuchVirtualHostException) {
			throw new CompanyVirtualHostException(noSuchVirtualHostException);
		}
	}

	/**
	 * Returns the company with the web domain.
	 *
	 * @param  webId the company's web domain
	 * @return the company with the web domain
	 */
	@Override
	public Company getCompanyByWebId(String webId) throws PortalException {
		return companyPersistence.findByWebId(webId);
	}

	/**
	 * Returns the user's company.
	 *
	 * @param  userId the primary key of the user
	 * @return Returns the first company if there is only one company or the
	 *         user's company if there are more than one company; <code>0</code>
	 *         otherwise
	 * @throws Exception if a user with the primary key could not be found
	 */
	@Override
	public long getCompanyIdByUserId(long userId) throws Exception {
		long[] companyIds = PortalInstancePool.getCompanyIds();

		long companyId = 0;

		if (companyIds.length == 1) {
			companyId = companyIds[0];
		}
		else if (companyIds.length > 1) {
			try {
				User user = _userPersistence.findByPrimaryKey(userId);

				companyId = user.getCompanyId();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get the company ID for user " + userId,
						exception);
				}
			}
		}

		return companyId;
	}

	/**
	 * Removes the values that match the keys of the company's preferences.
	 *
	 * This method is called by {@link
	 * com.liferay.portlet.portalsettings.action.EditLDAPServerAction} remotely
	 * through {@link com.liferay.portal.kernel.service.CompanyService}.
	 *
	 * @param companyId the primary key of the company
	 * @param keys the company's preferences keys to be remove
	 */
	@Override
	public void removePreferences(long companyId, String[] keys) {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		try {
			for (String key : keys) {
				portletPreferences.reset(key);
			}

			portletPreferences.store();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	/**
	 * Returns an ordered range of all assets that match the keywords in the
	 * company.
	 *
	 * The method is called in {@link
	 * com.liferay.portal.search.PortalOpenSearchImpl} which is not longer used
	 * by the Search portlet.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  keywords the keywords (space separated),which may occur in assets
	 *         in the company (optionally <code>null</code>)
	 * @param  start the lower bound of the range of assets to return
	 * @param  end the upper bound of the range of assets to return (not
	 *         inclusive)
	 * @return the matching assets in the company
	 */
	@Override
	public Hits search(
		long companyId, long userId, String keywords, int start, int end) {

		return search(companyId, userId, null, 0, null, keywords, start, end);
	}

	/**
	 * Returns an ordered range of all assets that match the keywords in the
	 * portlet within the company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  portletId the primary key of the portlet (optionally
	 *         <code>null</code>)
	 * @param  groupId the primary key of the group (optionally <code>0</code>)
	 * @param  type the mime type of assets to return(optionally
	 *         <code>null</code>)
	 * @param  keywords the keywords (space separated), which may occur in any
	 *         assets in the portlet (optionally <code>null</code>)
	 * @param  start the lower bound of the range of assets to return
	 * @param  end the upper bound of the range of assets to return (not
	 *         inclusive)
	 * @return the matching assets in the portlet within the company
	 */
	@Override
	public Hits search(
		long companyId, long userId, String portletId, long groupId,
		String type, String keywords, int start, int end) {

		FacetedSearcher facetedSearcher =
			FacetedSearcherManagerUtil.createFacetedSearcher();

		SearchContext searchContext = createSearchContext(
			companyId, userId, portletId, groupId, keywords, start, end);

		try {
			return facetedSearcher.search(searchContext);
		}
		catch (SearchException searchException) {
			throw new SystemException(searchException);
		}
	}

	@Override
	public Company updateCompany(Company company) {
		_companyInfoPersistence.update(company.getCompanyInfo());

		return super.updateCompany(company);
	}

	/**
	 * Updates the company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  virtualHostname the company's virtual host name
	 * @param  mx the company's mail domain
	 * @param  maxUsers the max number of company users (optionally
	 *         <code>0</code>)
	 * @param  active whether the company is active
	 * @return the company with the primary key
	 */
	@Override
	public Company updateCompany(
			long companyId, String virtualHostname, String mx, int maxUsers,
			boolean active)
		throws PortalException {

		// Company

		virtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		if (!active &&
			(companyId == PortalInstancePool.getDefaultCompanyId())) {

			throw new RequiredCompanyException(
				"Select another default company before deactivating company " +
					companyId);
		}

		Company company = companyPersistence.findByPrimaryKey(companyId);

		validateVirtualHost(company.getWebId(), virtualHostname);

		if (PropsValues.MAIL_MX_UPDATE) {
			validateMx(companyId, mx);

			company.setMx(mx);
		}

		company.setMaxUsers(maxUsers);
		company.setActive(active);

		companyPersistence.update(company);

		// Virtual host

		return updateVirtualHostname(companyId, virtualHostname);
	}

	/**
	 * Update the company with additional account information.
	 *
	 * @param  companyId the primary key of the company
	 * @param  virtualHostname the company's virtual host name
	 * @param  mx the company's mail domain
	 * @param  homeURL the company's home URL (optionally <code>null</code>)
	 * @param  hasLogo if the company has a custom logo
	 * @param  logoBytes the new logo image data
	 * @param  name the company's account name(optionally <code>null</code>)
	 * @param  legalName the company's account legal name (optionally
	 *         <code>null</code>)
	 * @param  legalId the company's account legal ID (optionally
	 *         <code>null</code>)
	 * @param  legalType the company's account legal type (optionally
	 *         <code>null</code>)
	 * @param  sicCode the company's account SIC code (optionally
	 *         <code>null</code>)
	 * @param  tickerSymbol the company's account ticker symbol (optionally
	 *         <code>null</code>)
	 * @param  industry the company's account industry (optionally
	 *         <code>null</code>)
	 * @param  type the company's account type (optionally <code>null</code>)
	 * @param  size the company's account size (optionally <code>null</code>)
	 * @return the company with the primary key
	 */
	@Override
	public Company updateCompany(
			long companyId, String virtualHostname, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size)
		throws PortalException {

		// Company

		virtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		Company company = companyPersistence.findByPrimaryKey(companyId);

		validateVirtualHost(company.getWebId(), virtualHostname);

		if (PropsValues.MAIL_MX_UPDATE) {
			validateMx(companyId, mx);
		}

		validateName(companyId, name);

		if (PropsValues.MAIL_MX_UPDATE) {
			company.setMx(mx);
		}

		company.setHomeURL(homeURL);

		PortalUtil.updateImageId(
			company, hasLogo, logoBytes, "logoId", 0, 0, 0);

		company.setName(name);
		company.setLegalName(legalName);
		company.setLegalId(legalId);
		company.setLegalType(legalType);
		company.setSicCode(sicCode);
		company.setTickerSymbol(tickerSymbol);
		company.setIndustry(industry);
		company.setType(type);
		company.setSize(size);

		companyPersistence.update(company);

		// Virtual host

		return updateVirtualHostname(companyId, virtualHostname);
	}

	/**
	 * Update the company's display.
	 *
	 * @param companyId the primary key of the company
	 * @param languageId the ID of the company's default user's language
	 * @param timeZoneId the ID of the company's default user's time zone
	 */
	@Override
	public void updateDisplay(
			long companyId, String languageId, String timeZoneId)
		throws PortalException {

		User user = _userLocalService.getGuestUser(companyId);

		user.setLanguageId(languageId);
		user.setTimeZoneId(timeZoneId);

		_userPersistence.update(user);

		updateDisplayGroupNames(companyId);
	}

	@Async
	@Override
	public void updateDisplayGroupNames(long companyId) throws PortalException {
		User user = _userLocalService.getGuestUser(companyId);

		Locale locale = user.getLocale();

		if (locale.equals(LocaleUtil.getDefault())) {
			return;
		}

		ActionableDynamicQuery groupActionableDynamicQuery =
			_groupLocalService.getActionableDynamicQuery();

		groupActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property activeProperty = PropertyFactoryUtil.forName("active");

				dynamicQuery.add(activeProperty.eq(Boolean.TRUE));

				Property nameProperty = PropertyFactoryUtil.forName("name");

				dynamicQuery.add(nameProperty.isNotNull());
			});
		groupActionableDynamicQuery.setCompanyId(user.getCompanyId());
		groupActionableDynamicQuery.setPerformActionMethod(
			(Group group) -> {
				Map<Locale, String> nameMap = group.getNameMap();

				if (MapUtil.isEmpty(nameMap)) {
					return;
				}

				String groupDefaultName = nameMap.get(locale);

				if (Validator.isNotNull(groupDefaultName)) {
					return;
				}

				String oldGroupDefaultName = nameMap.get(
					LocaleUtil.getDefault());

				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No name was found for locale ", locale,
							". Using \"", oldGroupDefaultName,
							"\" as the name instead."));
				}

				nameMap.put(locale, oldGroupDefaultName);

				group.setNameMap(nameMap);

				_groupLocalService.updateGroup(group);
			});

		groupActionableDynamicQuery.performActions();
	}

	@Override
	public Company updateIndexNameNext(long companyId, String indexNameNext)
		throws PortalException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		company.setIndexNameNext(indexNameNext);

		return companyPersistence.update(company);
	}

	@Override
	public Company updateIndexNames(
			long companyId, String indexNameCurrent, String indexNameNext)
		throws PortalException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		company.setIndexNameCurrent(indexNameCurrent);
		company.setIndexNameNext(indexNameNext);

		return companyPersistence.update(company);
	}

	/**
	 * Updates the company's logo.
	 *
	 * @param  companyId the primary key of the company
	 * @param  bytes the bytes of the company's logo image
	 * @return the company with the primary key
	 */
	@Override
	public Company updateLogo(long companyId, byte[] bytes)
		throws PortalException {

		Company company = checkLogo(companyId);

		_imageLocalService.updateImage(company.getLogoId(), bytes);

		return company;
	}

	/**
	 * Updates the company's logo.
	 *
	 * @param  companyId the primary key of the company
	 * @param  file the file of the company's logo image
	 * @return the company with the primary key
	 */
	@Override
	public Company updateLogo(long companyId, File file)
		throws PortalException {

		Company company = checkLogo(companyId);

		_imageLocalService.updateImage(
			company.getCompanyId(), company.getLogoId(), file);

		return company;
	}

	/**
	 * Update the company's logo.
	 *
	 * @param  companyId the primary key of the company
	 * @param  inputStream the input stream of the company's logo image
	 * @return the company with the primary key
	 */
	@Override
	public Company updateLogo(long companyId, InputStream inputStream)
		throws PortalException {

		Company company = checkLogo(companyId);

		_imageLocalService.updateImage(
			company.getCompanyId(), company.getLogoId(), inputStream);

		return company;
	}

	/**
	 * Updates the company's preferences. The company's default properties are
	 * found in portal.properties.
	 *
	 * @param companyId the primary key of the company
	 * @param unicodeProperties the company's properties. See {@link
	 *        UnicodeProperties}
	 */
	@Override
	public void updatePreferences(
			long companyId, UnicodeProperties unicodeProperties)
		throws PortalException {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		try {
			String newLanguageIds = unicodeProperties.getProperty(
				PropsKeys.LOCALES);

			boolean invalidateLayoutSetTemplates = false;

			if (Validator.isNotNull(newLanguageIds)) {
				String oldLanguageIds = portletPreferences.getValue(
					PropsKeys.LOCALES, StringPool.BLANK);

				if (!Objects.equals(oldLanguageIds, newLanguageIds)) {
					validateLanguageIds(newLanguageIds);

					_updateGroupLanguageIds(
						companyId, newLanguageIds, oldLanguageIds);

					LanguageUtil.resetAvailableLocales(companyId);

					invalidateLayoutSetTemplates = true;
				}
			}

			List<String> resetKeys = new ArrayList<>();

			for (Map.Entry<String, String> entry :
					unicodeProperties.entrySet()) {

				String value = entry.getValue();

				if (value.equals(Portal.TEMP_OBFUSCATION_VALUE)) {
					continue;
				}

				String key = entry.getKey();

				String propsUtilValue = PropsUtil.get(key);

				if (!value.equals(propsUtilValue)) {
					portletPreferences.setValue(key, value);
				}
				else {
					String portletPreferencesValue =
						portletPreferences.getValue(key, null);

					if (portletPreferencesValue != null) {
						resetKeys.add(key);
					}
				}
			}

			for (String key : resetKeys) {
				portletPreferences.reset(key);
			}

			portletPreferences.store();

			if (invalidateLayoutSetTemplates) {

				// Invalidate cache of all layout set prototypes that belong
				// to this company. See LPS-36403.

				Date date = new Date();

				for (LayoutSetPrototype layoutSetPrototype :
						_layoutSetPrototypeLocalService.getLayoutSetPrototypes(
							companyId)) {

					layoutSetPrototype.setModifiedDate(date);

					_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
						layoutSetPrototype);
				}
			}
		}
		catch (LocaleException localeException) {
			throw localeException;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		companyPersistence.clearCache(SetUtil.fromArray(companyId));
		_clearCacheCallback(companyId, false);
	}

	/**
	 * Updates the company's security properties.
	 *
	 * @param companyId the primary key of the company
	 * @param authType the company's method of authenticating users
	 * @param autoLogin whether to allow users to select the "remember me"
	 *        feature
	 * @param sendPassword whether to allow users to ask the company to send
	 *        their password
	 * @param strangers whether to allow strangers to create accounts register
	 *        themselves in the company
	 * @param strangersWithMx whether to allow strangers to create accounts with
	 *        email addresses that match the company mail suffix
	 * @param strangersVerify whether to require strangers who create accounts
	 *        to be verified via email
	 * @param siteLogo whether to allow site administrators to use their own
	 *        logo instead of the enterprise logo
	 */
	@Override
	public void updateSecurity(
		long companyId, String authType, boolean autoLogin,
		boolean sendPassword, boolean strangers, boolean strangersWithMx,
		boolean strangersVerify, boolean siteLogo) {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		try {
			portletPreferences.setValue(
				PropsKeys.COMPANY_SECURITY_AUTH_TYPE, authType);
			portletPreferences.setValue(
				PropsKeys.COMPANY_SECURITY_AUTO_LOGIN,
				String.valueOf(autoLogin));
			portletPreferences.setValue(
				PropsKeys.COMPANY_SECURITY_STRANGERS,
				String.valueOf(strangers));
			portletPreferences.setValue(
				PropsKeys.COMPANY_SECURITY_STRANGERS_WITH_MX,
				String.valueOf(strangersWithMx));
			portletPreferences.setValue(
				PropsKeys.COMPANY_SECURITY_STRANGERS_VERIFY,
				String.valueOf(strangersVerify));
			portletPreferences.setValue(
				PropsKeys.COMPANY_SECURITY_SITE_LOGO, String.valueOf(siteLogo));

			portletPreferences.store();
		}
		catch (IOException | PortletException exception) {
			throw new SystemException(exception);
		}

		_clearCacheCallback(companyId, false);
	}

	protected Company checkLogo(long companyId) throws PortalException {
		Company company = companyPersistence.findByPrimaryKey(companyId);

		long logoId = company.getLogoId();

		if (logoId <= 0) {
			logoId = counterLocalService.increment();

			company.setLogoId(logoId);

			company = companyPersistence.update(company);
		}

		return company;
	}

	protected SearchContext createSearchContext(
		long companyId, long userId, String portletId, long groupId,
		String keywords, int start, int end) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setEntryClassNames(
			SearchEngineHelperUtil.getEntryClassNames());

		if (groupId > 0) {
			searchContext.setGroupIds(new long[] {groupId});
		}

		searchContext.setKeywords(keywords);

		if (Validator.isNotNull(portletId)) {
			searchContext.setPortletIds(new String[] {portletId});
		}

		searchContext.setStart(start);
		searchContext.setUserId(userId);

		return searchContext;
	}

	protected Company doDeleteCompany(long companyId) throws PortalException {

		// Company

		Company company = companyPersistence.findByPrimaryKey(companyId);

		preunregisterCompany(company);

		if (DBPartition.isPartitionEnabled()) {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					_clearCache(companyId);

					PortalInstances.removeCompany(company.getCompanyId());

					unregisterCompany(company);

					_synchronizePortalInstances();

					try (SafeCloseable safeCloseable =
							CompanyThreadLocal.setCompanyIdWithSafeCloseable(
								companyId)) {

						CacheRegistryUtil.clear();
					}

					return null;
				});

			_clearCacheCallback(companyId, true);

			DBPartitionUtil.removeDBPartition(companyId);

			return company;
		}

		companyPersistence.remove(company);

		_companyInfoPersistence.remove(company.getCompanyInfo());

		// Expando

		DeleteExpandoColumnActionableDynamicQuery
			deleteExpandoColumnActionableDynamicQuery =
				new DeleteExpandoColumnActionableDynamicQuery(
					company.getCompanyId());

		deleteExpandoColumnActionableDynamicQuery.performActions();

		DeleteExpandoTableActionableDynamicQuery
			deleteExpandoTableActionableDynamicQuery =
				new DeleteExpandoTableActionableDynamicQuery(
					company.getCompanyId());

		deleteExpandoTableActionableDynamicQuery.performActions();

		// Group

		DeleteGroupActionableDynamicQuery deleteGroupActionableDynamicQuery =
			new DeleteGroupActionableDynamicQuery();

		deleteGroupActionableDynamicQuery.setCompanyId(companyId);

		deleteGroupActionableDynamicQuery.performActions();

		String[] systemGroups = PortalUtil.getSystemGroups();

		for (String groupName : systemGroups) {
			if (groupName.equals(GroupConstants.CMS) &&
				!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {

				continue;
			}

			deleteGroupActionableDynamicQuery.deleteGroup(
				_groupLocalService.getGroup(companyId, groupName));
		}

		deleteGroupActionableDynamicQuery.deleteGroup(
			_groupLocalService.getCompanyGroup(companyId));

		// Layout prototype

		ActionableDynamicQuery layoutPrototypeActionableDynamicQuery =
			_layoutPrototypeLocalService.getActionableDynamicQuery();

		layoutPrototypeActionableDynamicQuery.setCompanyId(companyId);
		layoutPrototypeActionableDynamicQuery.setPerformActionMethod(
			(LayoutPrototype layoutPrototype) ->
				_layoutPrototypeLocalService.deleteLayoutPrototype(
					layoutPrototype));

		layoutPrototypeActionableDynamicQuery.performActions();

		// Layout set prototype

		ActionableDynamicQuery layoutSetPrototypeActionableDynamicQuery =
			_layoutSetPrototypeLocalService.getActionableDynamicQuery();

		layoutSetPrototypeActionableDynamicQuery.setCompanyId(companyId);
		layoutSetPrototypeActionableDynamicQuery.setPerformActionMethod(
			(LayoutSetPrototype layoutSetPrototype) ->
				_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
					layoutSetPrototype));

		layoutSetPrototypeActionableDynamicQuery.performActions();

		// Organization

		DeleteOrganizationActionableDynamicQuery
			deleteOrganizationActionableDynamicQuery =
				new DeleteOrganizationActionableDynamicQuery();

		deleteOrganizationActionableDynamicQuery.setCompanyId(companyId);

		deleteOrganizationActionableDynamicQuery.performActions();

		// User group

		DeleteUserGroupActionableDynamicQuery
			deleteUserGroupActionableDynamicQuery =
				new DeleteUserGroupActionableDynamicQuery(
					company.getCompanyId());

		deleteUserGroupActionableDynamicQuery.performActions();

		// Password policy

		_passwordPolicyLocalService.deleteNondefaultPasswordPolicies(companyId);

		PasswordPolicy defaultPasswordPolicy =
			_passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);

		if (defaultPasswordPolicy != null) {
			_passwordPolicyLocalService.deletePasswordPolicy(
				defaultPasswordPolicy);
		}

		// Portal preferences

		PortalPreferences portalPreferences =
			_portalPreferencesPersistence.findByO_O(
				companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		_portalPreferencesLocalService.deletePortalPreferences(
			portalPreferences);

		// User

		User guestUser = _userLocalService.getGuestUser(companyId);

		String name = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(guestUser.getUserId());

			ActionableDynamicQuery userActionableDynamicQuery =
				_userLocalService.getActionableDynamicQuery();

			userActionableDynamicQuery.setCompanyId(companyId);
			userActionableDynamicQuery.setPerformActionMethod(
				(User user) -> {
					if (!user.isGuestUser()) {
						_userLocalService.deleteUser(user.getUserId());
					}
				});

			userActionableDynamicQuery.performActions();
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}

		_userLocalService.deleteUser(guestUser);

		// Role

		ActionableDynamicQuery roleActionableDynamicQuery =
			_roleLocalService.getActionableDynamicQuery();

		roleActionableDynamicQuery.setCompanyId(companyId);
		roleActionableDynamicQuery.setPerformActionMethod(
			(Role role) -> _roleLocalService.deleteRole(role));

		roleActionableDynamicQuery.performActions();

		// System event

		DeleteSystemEventActionableDynamicQuery
			deleteSystemEventActionableDynamicQuery =
				new DeleteSystemEventActionableDynamicQuery(
					company.getCompanyId());

		deleteSystemEventActionableDynamicQuery.performActions();

		_deletePortalInstance(company);

		return company;
	}

	protected void preregisterCompany(Company company) {
		PortalInstanceLifecycleManager portalInstanceLifecycleManager =
			_serviceTracker.getService();

		if (portalInstanceLifecycleManager != null) {
			portalInstanceLifecycleManager.preregisterCompany(company);
		}
		else {
			synchronized (_preregisterPendingCompanies) {
				_preregisterPendingCompanies.add(company);
			}
		}
	}

	protected void preunregisterCompany(Company company) {
		PortalInstanceLifecycleManager portalInstanceLifecycleManager =
			_serviceTracker.getService();

		if (portalInstanceLifecycleManager != null) {
			portalInstanceLifecycleManager.preunregisterCompany(company);
		}
	}

	protected void registerCompany(Company company) {
		PortalInstanceLifecycleManager portalInstanceLifecycleManager =
			_serviceTracker.getService();

		if (portalInstanceLifecycleManager != null) {
			portalInstanceLifecycleManager.registerCompany(company);
		}
		else {
			synchronized (_pendingCompanies) {
				_pendingCompanies.add(company);
			}
		}
	}

	protected Company syncVirtualHost(Company company) throws PortalException {
		if (!PropsValues.COMPANY_DEFAULT_VIRTUAL_HOST_SYNC_ON_STARTUP ||
			!Objects.equals(
				PropsValues.COMPANY_DEFAULT_WEB_ID, company.getWebId())) {

			return company;
		}

		return updateCompany(
			company.getCompanyId(),
			GetterUtil.getString(
				PropsValues.COMPANY_DEFAULT_VIRTUAL_HOST_NAME,
				company.getVirtualHostname()),
			GetterUtil.getString(
				PropsValues.COMPANY_DEFAULT_VIRTUAL_HOST_MAIL_DOMAIN,
				company.getMx()),
			company.getMaxUsers(), company.isActive());
	}

	protected void unregisterCompany(Company company) {
		PortalInstanceLifecycleManager portalInstanceLifecycleManager =
			_serviceTracker.getService();

		if (portalInstanceLifecycleManager != null) {
			portalInstanceLifecycleManager.unregisterCompany(company);
		}
	}

	protected Company updateVirtualHostname(
			long companyId, String virtualHostname)
		throws CompanyVirtualHostException {

		if (Validator.isNotNull(virtualHostname)) {
			try {
				if (Validator.isIPv6Address(virtualHostname)) {
					Inet6Address address = (Inet6Address)InetAddress.getByName(
						virtualHostname);

					virtualHostname = address.getHostAddress();
				}
			}
			catch (UnknownHostException unknownHostException) {
				if (_log.isDebugEnabled()) {
					_log.debug(unknownHostException);
				}

				throw new CompanyVirtualHostException(
					"Virtual hostname is not a valid IPv6 address");
			}

			VirtualHost virtualHost = _virtualHostPersistence.fetchByHostname(
				virtualHostname);

			if (virtualHost == null) {
				_virtualHostLocalService.updateVirtualHosts(
					companyId, 0,
					TreeMapBuilder.put(
						virtualHostname, StringPool.BLANK
					).build());
			}
			else {
				if ((virtualHost.getCompanyId() != companyId) ||
					(virtualHost.getLayoutSetId() != 0)) {

					throw new CompanyVirtualHostException();
				}
			}
		}
		else {
			List<VirtualHost> virtualHosts = _virtualHostPersistence.findByC_L(
				companyId, 0);

			if (!virtualHosts.isEmpty()) {
				for (VirtualHost virtualHost : virtualHosts) {
					_virtualHostPersistence.remove(virtualHost);
				}
			}
		}

		return companyPersistence.fetchByPrimaryKey(companyId);
	}

	protected void validateLanguageIds(String languageIds)
		throws PortalException {

		String[] languageIdsArray = StringUtil.split(
			languageIds, StringPool.COMMA);

		for (String languageId : languageIdsArray) {
			if (!ArrayUtil.contains(PropsValues.LOCALES, languageId)) {
				LocaleException localeException = new LocaleException(
					LocaleException.TYPE_DISPLAY_SETTINGS);

				localeException.setSourceAvailableLanguageIds(
					Arrays.asList(PropsValues.LOCALES));
				localeException.setTargetAvailableLanguageIds(
					Arrays.asList(languageIdsArray));

				throw localeException;
			}
		}
	}

	protected void validateMx(long companyId, String mx)
		throws PortalException {

		if (Validator.isNull(mx) || !Validator.isDomain(mx)) {
			throw new CompanyMxException("Invalid domain " + mx);
		}

		String emailAddress =
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + "@" + mx;

		EmailAddressValidator emailAddressValidator =
			EmailAddressValidatorFactory.getInstance();

		if (!emailAddressValidator.validate(companyId, emailAddress)) {
			throw new CompanyMxException(
				"Invalid email address " + emailAddress);
		}
	}

	protected void validateName(long companyId, String name)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(companyId, name);

		if ((group != null) || Validator.isNull(name)) {
			throw new CompanyNameException();
		}
	}

	protected void validateVirtualHost(String webId, String virtualHostname)
		throws PortalException {

		try {
			if (Validator.isNull(virtualHostname)) {
				throw new CompanyVirtualHostException(
					"Virtual hostname is null");
			}
			else if (virtualHostname.equals(_DEFAULT_VIRTUAL_HOST) &&
					 !webId.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {

				throw new CompanyVirtualHostException(
					"localhost can only be used with the default web ID " +
						webId);
			}
			else if (!Validator.isDomain(virtualHostname) &&
					 !Validator.isIPAddress(virtualHostname)) {

				throw new CompanyVirtualHostException(
					"Virtual hostname is invalid");
			}

			VirtualHost virtualHost = _virtualHostLocalService.fetchVirtualHost(
				virtualHostname);

			if (virtualHost == null) {
				return;
			}

			Company virtualHostnameCompany =
				companyPersistence.findByPrimaryKey(virtualHost.getCompanyId());

			if (!webId.equals(virtualHostnameCompany.getWebId())) {
				throw new CompanyVirtualHostException(
					"Duplicate virtual hostname " + virtualHostname);
			}
		}
		catch (CompanyVirtualHostException companyVirtualHostException) {
			if (_log.isWarnEnabled()) {
				_log.warn(companyVirtualHostException);
			}

			throw companyVirtualHostException;
		}
	}

	protected void validateWebId(String webId) throws CompanyWebIdException {
		if (Validator.isNull(webId)) {
			throw new CompanyWebIdException("Web ID is null");
		}

		if (companyPersistence.fetchByWebId(webId) != null) {
			throw new CompanyWebIdException("Duplicate web ID " + webId);
		}
	}

	protected class DeleteExpandoColumnActionableDynamicQuery {

		protected DeleteExpandoColumnActionableDynamicQuery(long companyId) {
			_actionableDynamicQuery =
				_expandoColumnLocalService.getActionableDynamicQuery();

			_actionableDynamicQuery.setCompanyId(companyId);
			_actionableDynamicQuery.setPerformActionMethod(
				(ExpandoColumn expandoColumn) ->
					_expandoColumnLocalService.deleteColumn(expandoColumn));
		}

		protected void performActions() throws PortalException {
			_actionableDynamicQuery.performActions();
		}

		private ActionableDynamicQuery _actionableDynamicQuery;

	}

	protected class DeleteExpandoTableActionableDynamicQuery {

		protected DeleteExpandoTableActionableDynamicQuery(long companyId) {
			_actionableDynamicQuery =
				_expandoTableLocalService.getActionableDynamicQuery();

			_actionableDynamicQuery.setCompanyId(companyId);
			_actionableDynamicQuery.setPerformActionMethod(
				(ExpandoTable expandoTable) ->
					_expandoTableLocalService.deleteExpandoTable(expandoTable));
		}

		protected void performActions() throws PortalException {
			_actionableDynamicQuery.performActions();
		}

		private ActionableDynamicQuery _actionableDynamicQuery;

	}

	protected class DeleteGroupActionableDynamicQuery {

		protected DeleteGroupActionableDynamicQuery() {
			_actionableDynamicQuery =
				_groupLocalService.getActionableDynamicQuery();

			_actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property parentGroupIdProperty =
						PropertyFactoryUtil.forName("parentGroupId");

					dynamicQuery.add(parentGroupIdProperty.eq(_parentGroupId));

					Disjunction disjunction =
						RestrictionsFactoryUtil.disjunction();

					Property siteProperty = PropertyFactoryUtil.forName("site");

					disjunction.add(siteProperty.eq(Boolean.TRUE));

					Property typeProperty = PropertyFactoryUtil.forName("type");

					disjunction.add(typeProperty.eq(GroupConstants.TYPE_DEPOT));

					dynamicQuery.add(disjunction);
				});
			_actionableDynamicQuery.setPerformActionMethod(
				(Group group) -> {
					if (!PortalUtil.isSystemGroup(group.getGroupKey()) &&
						!group.isCompany() && !group.isStagingGroup()) {

						deleteGroup(group);
					}
				});
		}

		protected void deleteGroup(Group group) throws PortalException {
			DeleteGroupActionableDynamicQuery
				deleteGroupActionableDynamicQuery =
					new DeleteGroupActionableDynamicQuery();

			deleteGroupActionableDynamicQuery.setCompanyId(
				group.getCompanyId());
			deleteGroupActionableDynamicQuery.setParentGroupId(
				group.getGroupId());

			deleteGroupActionableDynamicQuery.performActions();

			_groupLocalService.deleteGroup(group);

			LiveUsers.deleteGroup(group.getCompanyId(), group.getGroupId());
		}

		protected void performActions() throws PortalException {
			_actionableDynamicQuery.performActions();
		}

		protected void setCompanyId(long companyId) {
			_actionableDynamicQuery.setCompanyId(companyId);
		}

		protected void setParentGroupId(long parentGroupId) {
			_parentGroupId = parentGroupId;
		}

		private ActionableDynamicQuery _actionableDynamicQuery;
		private long _parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;

	}

	protected class DeleteOrganizationActionableDynamicQuery {

		public void setParentOrganizationId(long parentOrganizationId) {
			_parentOrganizationId = parentOrganizationId;
		}

		protected DeleteOrganizationActionableDynamicQuery() {
			_actionableDynamicQuery =
				_organizationLocalService.getActionableDynamicQuery();

			_actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property property = PropertyFactoryUtil.forName(
						"parentOrganizationId");

					dynamicQuery.add(property.eq(_parentOrganizationId));
				});
			_actionableDynamicQuery.setPerformActionMethod(
				(Organization organization) -> deleteOrganization(
					organization));
		}

		protected void deleteOrganization(Organization organization)
			throws PortalException {

			DeleteOrganizationActionableDynamicQuery
				deleteOrganizationActionableDynamicQuery =
					new DeleteOrganizationActionableDynamicQuery();

			deleteOrganizationActionableDynamicQuery.setCompanyId(
				organization.getCompanyId());
			deleteOrganizationActionableDynamicQuery.setParentOrganizationId(
				organization.getOrganizationId());

			deleteOrganizationActionableDynamicQuery.performActions();

			_organizationLocalService.deleteOrganization(organization);
		}

		protected void performActions() throws PortalException {
			_actionableDynamicQuery.performActions();
		}

		protected void setCompanyId(long companyId) {
			_actionableDynamicQuery.setCompanyId(companyId);
		}

		private ActionableDynamicQuery _actionableDynamicQuery;
		private long _parentOrganizationId =
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID;

	}

	protected class DeleteSystemEventActionableDynamicQuery {

		protected DeleteSystemEventActionableDynamicQuery(long companyId) {
			_actionableDynamicQuery =
				_systemEventLocalService.getActionableDynamicQuery();

			_actionableDynamicQuery.setCompanyId(companyId);
			_actionableDynamicQuery.setPerformActionMethod(
				(SystemEvent systemEvent) ->
					_systemEventLocalService.deleteSystemEvent(systemEvent));
		}

		protected void performActions() throws PortalException {
			_actionableDynamicQuery.performActions();
		}

		private ActionableDynamicQuery _actionableDynamicQuery;

	}

	protected class DeleteUserGroupActionableDynamicQuery {

		protected DeleteUserGroupActionableDynamicQuery(long companyId) {
			_actionableDynamicQuery =
				_userGroupLocalService.getActionableDynamicQuery();

			_actionableDynamicQuery.setCompanyId(companyId);
			_actionableDynamicQuery.setPerformActionMethod(
				(UserGroup userGroup) -> _userGroupLocalService.deleteUserGroup(
					userGroup));
		}

		protected void performActions() throws PortalException {
			_actionableDynamicQuery.performActions();
		}

		private ActionableDynamicQuery _actionableDynamicQuery;

	}

	private static void _doSynchronizePortalInstances() {
		CompanyLocalService companyLocalService =
			(CompanyLocalService)
				IdentifiableOSGiServiceUtil.getIdentifiableOSGiService(
					CompanyLocalService.class.getName());

		Set<Long> companyIds = SetUtil.fromArray(
			PortalInstancePool.getCompanyIds());

		companyLocalService.forEachCompany(
			company -> {
				if (companyIds.remove(company.getCompanyId())) {
					return;
				}

				PortalInstances.initCompany(company);
			});

		companyLocalService.forEachCompanyId(
			companyId -> {
				PortalInstances.removeCompany(companyId);

				CacheRegistryUtil.clear();
			},
			ArrayUtil.toLongArray(companyIds));
	}

	private Company _addDBPartitionCompany(Company company)
		throws PortalException {

		preregisterCompany(company);

		_classNameLocalService.checkClassNames();

		_resourceActionLocalService.checkResourceActions();

		_portletLocalService.checkPortlets(company.getCompanyId());

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Company dbPartitionCompany =
					companyPersistence.findByPrimaryKey(company.getCompanyId());

				registerCompany(dbPartitionCompany);

				PortalInstances.initCompany(dbPartitionCompany, true);

				_synchronizePortalInstances();

				return null;
			});

		return company;
	}

	private void _addDemoSettings(Company company) throws PortalException {
		updateVirtualHostname(company.getCompanyId(), "demo.liferay.net");

		updateSecurity(
			company.getCompanyId(), CompanyConstants.AUTH_TYPE_EA, true, true,
			true, true, false, true);

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			company.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.ADMIN_EMAIL_FROM_NAME, "Liferay Demo");
			portletPreferences.setValue(
				PropsKeys.ADMIN_EMAIL_FROM_ADDRESS, "test@liferay.net");

			portletPreferences.store();
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
		catch (PortletException portletException) {
			throw new SystemException(portletException);
		}
	}

	private User _addGuestUser(Company company) throws PortalException {
		Date date = new Date();

		User guestUser = _userPersistence.create(
			counterLocalService.increment());

		guestUser.setCompanyId(company.getCompanyId());
		guestUser.setContactId(counterLocalService.increment());
		guestUser.setPassword("password");
		guestUser.setScreenName(String.valueOf(guestUser.getUserId()));
		guestUser.setEmailAddress("default@" + company.getMx());

		Locale locale = null;

		if (Validator.isNotNull(PropsValues.COMPANY_DEFAULT_LOCALE)) {
			locale = LocaleUtil.fromLanguageId(
				PropsValues.COMPANY_DEFAULT_LOCALE);
		}
		else {
			User defaultCompanyGuestUser = _userLocalService.fetchGuestUser(
				PortalUtil.getDefaultCompanyId());

			if (defaultCompanyGuestUser != null) {
				locale = defaultCompanyGuestUser.getLocale();
			}
		}

		guestUser.setLanguageId(LocaleUtil.toLanguageId(locale));

		if (Validator.isNotNull(PropsValues.COMPANY_DEFAULT_TIME_ZONE)) {
			guestUser.setTimeZoneId(PropsValues.COMPANY_DEFAULT_TIME_ZONE);
		}
		else {
			TimeZone timeZone = TimeZoneUtil.getDefault();

			guestUser.setTimeZoneId(timeZone.getID());
		}

		guestUser.setLoginDate(date);
		guestUser.setFailedLoginAttempts(0);
		guestUser.setAgreedToTermsOfUse(true);
		guestUser.setType(UserConstants.TYPE_GUEST);
		guestUser.setStatus(WorkflowConstants.STATUS_APPROVED);

		// Invoke updateImpl so that we do not trigger model listeners. See
		// LPS-108239.

		_userPersistence.updateImpl(guestUser);

		// Force update _guestUsers map

		guestUser = _userLocalService.getGuestUser(company.getCompanyId());

		// Contact

		Contact guestContact = _contactPersistence.create(
			guestUser.getContactId());

		guestContact.setCompanyId(guestUser.getCompanyId());
		guestContact.setUserId(guestUser.getUserId());
		guestContact.setUserName(StringPool.BLANK);
		guestContact.setClassName(User.class.getName());
		guestContact.setClassPK(guestUser.getUserId());
		guestContact.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		guestContact.setEmailAddress(guestUser.getEmailAddress());
		guestContact.setFirstName(StringPool.BLANK);
		guestContact.setMiddleName(StringPool.BLANK);
		guestContact.setLastName(StringPool.BLANK);
		guestContact.setMale(true);
		guestContact.setBirthday(date);

		_contactPersistence.update(guestContact);

		return guestUser;
	}

	private void _clearCache(long companyId) {
		Company company = companyPersistence.fetchByPrimaryKey(companyId);

		if (company != null) {
			companyPersistence.clearCache(company);

			VirtualHost virtualHost = _virtualHostPersistence.fetchByHostname(
				company.getVirtualHostname());

			_virtualHostPersistence.clearCache(virtualHost);
		}
	}

	private void _clearCacheCallback(
		long companyId, boolean removePortalCache) {

		Company company = companyPersistence.fetchByPrimaryKey(companyId);

		if (company == null) {
			return;
		}

		VirtualHost virtualHost = _virtualHostPersistence.fetchByHostname(
			company.getVirtualHostname());

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				EntityCacheUtil.removeResult(
					company.getClass(), company.getPrimaryKeyObj());

				EntityCacheUtil.removeResult(
					virtualHost.getClass(), virtualHost.getPrimaryKeyObj());

				if (removePortalCache) {
					PortalCacheHelperUtil.removePortalCaches(
						PortalCacheManagerNames.MULTI_VM, companyId);
				}

				return null;
			});
	}

	private void _deletePortalInstance(Company company) throws PortalException {

		// Portlet

		List<Portlet> portlets = _portletPersistence.findByCompanyId(
			company.getCompanyId());

		for (Portlet portlet : portlets) {
			_portletLocalService.deletePortlet(portlet.getId());
		}

		_portletLocalService.removeCompanyPortletsPool(company.getCompanyId());

		// Virtual host

		VirtualHost companyVirtualHost =
			_virtualHostLocalService.fetchCompanyDefaultVirtualHost(
				company.getCompanyId());

		_virtualHostLocalService.deleteVirtualHost(companyVirtualHost);

		// Portal instance

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				PortalInstances.removeCompany(company.getCompanyId());

				unregisterCompany(company);

				_synchronizePortalInstances();

				return null;
			});
	}

	private long _getNextCompanyId() {
		if (PropsValues.COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED) {
			if (StartupHelperUtil.isDBNew()) {
				return counterLocalService.increment(
					Company.class.getName(), 9999);
			}

			return counterLocalService.increment(Company.class.getName());
		}

		long nextLong = 0;

		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

		while ((nextLong == 0) ||
			   ArrayUtil.contains(
				   PortalInstancePool.getCompanyIds(), nextLong)) {

			nextLong = threadLocalRandom.nextLong(
				(long)Math.pow(10, 13), (long)Math.pow(10, 14));
		}

		return nextLong;
	}

	private void _synchronizePortalInstances() {
		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			_methodHandler, true);

		clusterRequest.setFireAndForget(true);

		ClusterExecutorUtil.execute(clusterRequest);
	}

	private Company _transactionAwareInvoke(Callable<Company> callable)
		throws PortalException {

		try {
			if (DBPartition.isPartitionEnabled()) {
				return TransactionInvokerUtil.invoke(
					_transactionConfig, callable);
			}

			return callable.call();
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	private void _updateGroupLanguageIds(
		long companyId, String newLanguageIds, String oldLanguageIds) {

		String[] oldLanguageIdsArray = StringUtil.split(oldLanguageIds);

		if (ArrayUtil.isEmpty(oldLanguageIdsArray)) {
			oldLanguageIdsArray = LocaleUtil.toLanguageIds(
				LanguageUtil.getCompanyAvailableLocales(companyId));
		}

		List<String> removedLanguageIds = ListUtil.remove(
			ListUtil.fromArray(oldLanguageIdsArray),
			ListUtil.fromArray(StringUtil.split(newLanguageIds)));

		List<Group> groups = _groupLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				GroupTable.INSTANCE
			).from(
				GroupTable.INSTANCE
			).where(
				GroupTable.INSTANCE.companyId.eq(
					companyId
				).and(
					GroupTable.INSTANCE.active.eq(true)
				).and(
					GroupTable.INSTANCE.site.eq(true)
				)
			));

		for (Group group : groups) {
			UnicodeProperties groupTypeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			boolean inheritLocales = GetterUtil.getBoolean(
				groupTypeSettingsUnicodeProperties.getProperty(
					"inheritLocales"),
				true);

			if (inheritLocales) {
				_updateGroupLocales(
					group, groupTypeSettingsUnicodeProperties, newLanguageIds);

				continue;
			}

			String[] groupLanguageIds = StringUtil.split(
				groupTypeSettingsUnicodeProperties.getProperty(
					PropsKeys.LOCALES));

			boolean updateLocales = false;

			if (ListUtil.isNotEmpty(removedLanguageIds)) {
				for (String removedLanguageId : removedLanguageIds) {
					if (ArrayUtil.contains(
							groupLanguageIds, removedLanguageId)) {

						groupLanguageIds = ArrayUtil.remove(
							groupLanguageIds, removedLanguageId);

						updateLocales = true;
					}
				}
			}

			if (updateLocales) {
				_updateGroupLocales(
					group, groupTypeSettingsUnicodeProperties,
					StringUtil.merge(groupLanguageIds, StringPool.COMMA));
			}
		}
	}

	private void _updateGroupLocales(
		Group group, UnicodeProperties groupTypeSettingsUnicodeProperties,
		String newLanguageIds) {

		LanguageUtil.resetAvailableGroupLocales(group.getGroupId());

		groupTypeSettingsUnicodeProperties.setProperty(
			PropsKeys.LOCALES, newLanguageIds);

		_groupLocalService.updateGroup(group);
	}

	private static final String _DEFAULT_VIRTUAL_HOST = "localhost";

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyLocalServiceImpl.class);

	private static final MethodHandler _methodHandler = new MethodHandler(
		new MethodKey(
			CompanyLocalServiceImpl.class, "_doSynchronizePortalInstances"));
	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = CompanyInfoPersistence.class)
	private CompanyInfoPersistence _companyInfoPersistence;

	@BeanReference(type = ContactPersistence.class)
	private ContactPersistence _contactPersistence;

	@BeanReference(type = DLFileEntryTypeLocalService.class)
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@BeanReference(type = ExpandoColumnLocalService.class)
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@BeanReference(type = ExpandoTableLocalService.class)
	private ExpandoTableLocalService _expandoTableLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = ImageLocalService.class)
	private ImageLocalService _imageLocalService;

	@BeanReference(type = LayoutPrototypeLocalService.class)
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@BeanReference(type = LayoutSetPrototypeLocalService.class)
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = PasswordPolicyLocalService.class)
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	private final Set<Company> _pendingCompanies = new HashSet<>();

	@BeanReference(type = PortalPreferencesLocalService.class)
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@BeanReference(type = PortalPreferencesPersistence.class)
	private PortalPreferencesPersistence _portalPreferencesPersistence;

	@BeanReference(type = PortletLocalService.class)
	private PortletLocalService _portletLocalService;

	@BeanReference(type = PortletPersistence.class)
	private PortletPersistence _portletPersistence;

	private final Set<Company> _preregisterPendingCompanies = new HashSet<>();

	@BeanReference(type = ResourceActionLocalService.class)
	private ResourceActionLocalService _resourceActionLocalService;

	@BeanReference(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

	private final ServiceTracker
		<PortalInstanceLifecycleManager, PortalInstanceLifecycleManager>
			_serviceTracker;

	@BeanReference(type = SystemEventLocalService.class)
	private SystemEventLocalService _systemEventLocalService;

	@BeanReference(type = UserGroupLocalService.class)
	private UserGroupLocalService _userGroupLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = VirtualHostLocalService.class)
	private VirtualHostLocalService _virtualHostLocalService;

	@BeanReference(type = VirtualHostPersistence.class)
	private VirtualHostPersistence _virtualHostPersistence;

	private class PortalInstanceLifecycleManagerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<PortalInstanceLifecycleManager, PortalInstanceLifecycleManager> {

		@Override
		public PortalInstanceLifecycleManager addingService(
			ServiceReference<PortalInstanceLifecycleManager> serviceReference) {

			PortalInstanceLifecycleManager portalInstanceLifecycleManager =
				_bundleContext.getService(serviceReference);

			synchronized (_preregisterPendingCompanies) {
				forEachCompany(
					company ->
						portalInstanceLifecycleManager.preregisterCompany(
							company),
					new ArrayList<Company>(_preregisterPendingCompanies));

				_preregisterPendingCompanies.clear();
			}

			synchronized (_pendingCompanies) {
				forEachCompany(
					company -> portalInstanceLifecycleManager.registerCompany(
						company),
					new ArrayList<Company>(_pendingCompanies));

				_pendingCompanies.clear();
			}

			return portalInstanceLifecycleManager;
		}

		@Override
		public void modifiedService(
			ServiceReference<PortalInstanceLifecycleManager> serviceReference,
			PortalInstanceLifecycleManager portalInstanceLifecycleManager) {

			removedService(serviceReference, portalInstanceLifecycleManager);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<PortalInstanceLifecycleManager> serviceReference,
			PortalInstanceLifecycleManager portalInstanceLifecycleManager) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}