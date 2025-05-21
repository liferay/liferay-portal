/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildUtil
 * @generated
 */
@ProviderType
public interface PatcherBuildPersistence extends BasePersistence<PatcherBuild> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherBuildUtil} to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_KV(String key, double keyVersion);

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_KV(
		String key, double keyVersion, boolean useFinderCache);

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 */
	public PatcherBuild removeByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public int countByK_KV(String key, double keyVersion);

	/**
	 * Caches the patcher build in the entity cache if it is enabled.
	 *
	 * @param patcherBuild the patcher build
	 */
	public void cacheResult(PatcherBuild patcherBuild);

	/**
	 * Caches the patcher builds in the entity cache if it is enabled.
	 *
	 * @param patcherBuilds the patcher builds
	 */
	public void cacheResult(java.util.List<PatcherBuild> patcherBuilds);

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	public PatcherBuild create(long patcherBuildId);

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild remove(long patcherBuildId)
		throws NoSuchPatcherBuildException;

	public PatcherBuild updateImpl(PatcherBuild patcherBuild);

	/**
	 * Returns the patcher build with the primary key or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 */
	public PatcherBuild fetchByPrimaryKey(long patcherBuildId);

	/**
	 * Returns all the patcher builds.
	 *
	 * @return the patcher builds
	 */
	public java.util.List<PatcherBuild> findAll();

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	public java.util.List<PatcherBuild> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds
	 */
	public java.util.List<PatcherBuild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher builds
	 */
	public java.util.List<PatcherBuild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher builds from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	public int countAll();

	/**
	 * Returns the primaryKeys of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher accounts associated with the patcher build
	 */
	public long[] getPatcherAccountPrimaryKeys(long pk);

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 */
	public java.util.List<PatcherBuild> getPatcherAccountPatcherBuilds(long pk);

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 */
	public java.util.List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end);

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 */
	public java.util.List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 */
	public int getPatcherAccountsSize(long pk);

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 */
	public boolean containsPatcherAccount(long pk, long patcherAccountPK);

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 */
	public boolean containsPatcherAccounts(long pk);

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherAccount(long pk, long patcherAccountPK);

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherAccount(
		long pk, com.liferay.osb.patcher.model.PatcherAccount patcherAccount);

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherAccounts(long pk, long[] patcherAccountPKs);

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherAccounts(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts);

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 */
	public void clearPatcherAccounts(long pk);

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 */
	public void removePatcherAccount(long pk, long patcherAccountPK);

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 */
	public void removePatcherAccount(
		long pk, com.liferay.osb.patcher.model.PatcherAccount patcherAccount);

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 */
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs);

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 */
	public void removePatcherAccounts(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts);

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 */
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs);

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 */
	public void setPatcherAccounts(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts);

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher build
	 */
	public long[] getPatcherFixPrimaryKeys(long pk);

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 */
	public java.util.List<PatcherBuild> getPatcherFixPatcherBuilds(long pk);

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 */
	public java.util.List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end);

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 */
	public java.util.List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the number of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixes associated with the patcher build
	 */
	public int getPatcherFixesSize(long pk);

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 */
	public boolean containsPatcherFix(long pk, long patcherFixPK);

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher build has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	public boolean containsPatcherFixes(long pk);

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherFix(long pk, long patcherFixPK);

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

	/**
	 * Clears all associations between the patcher build and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixes from
	 */
	public void clearPatcherFixes(long pk);

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	public void removePatcherFix(long pk, long patcherFixPK);

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 */
	public void removePatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	public void removePatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 */
	public void removePatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher build
	 */
	public void setPatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes to be associated with the patcher build
	 */
	public void setPatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

}