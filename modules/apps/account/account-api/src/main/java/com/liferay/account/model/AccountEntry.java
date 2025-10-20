/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the AccountEntry service. Represents a row in the &quot;AccountEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryModel
 * @generated
 */
@ImplementationClassName("com.liferay.account.model.impl.AccountEntryImpl")
@ProviderType
public interface AccountEntry extends AccountEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.account.model.impl.AccountEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<AccountEntry, Long> ACCOUNT_ENTRY_ID_ACCESSOR =
		new Accessor<AccountEntry, Long>() {

			@Override
			public Long get(AccountEntry accountEntry) {
				return accountEntry.getAccountEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<AccountEntry> getTypeClass() {
				return AccountEntry.class;
			}

		};

	public com.liferay.portal.kernel.model.Contact fetchContact()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<com.liferay.portal.kernel.model.Organization>
		fetchOrganizations();

	public java.util.List<com.liferay.portal.kernel.model.User> fetchUsers();

	public com.liferay.portal.kernel.model.Group getAccountEntryGroup();

	public long getAccountEntryGroupId();

	public com.liferay.portal.kernel.model.Address getDefaultBillingAddress();

	public com.liferay.portal.kernel.model.Address getDefaultShippingAddress();

	public String[] getDomainsArray();

	public java.util.List<com.liferay.portal.kernel.model.EmailAddress>
		getEmailAddresses();

	public java.util.List<com.liferay.portal.kernel.model.Address>
		getListTypeAddresses(long[] listTypeIds);

	public java.util.List<com.liferay.portal.kernel.model.Phone> getPhones();

	public java.util.List<com.liferay.portal.kernel.model.Website>
		getWebsites();

	public boolean isBusinessAccount();

	public boolean isGuestAccount();

	public boolean isPersonalAccount();

}