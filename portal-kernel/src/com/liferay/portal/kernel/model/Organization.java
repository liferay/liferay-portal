/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the Organization service. Represents a row in the &quot;Organization_&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.OrganizationImpl")
@ProviderType
public interface Organization
	extends OrganizationModel, PersistedModel, TreeModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.OrganizationImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<Organization, Long> ORGANIZATION_ID_ACCESSOR =
		new Accessor<Organization, Long>() {

			@Override
			public Long get(Organization organization) {
				return organization.getOrganizationId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<Organization> getTypeClass() {
				return Organization.class;
			}

		};
	public static final Accessor<Organization, String> NAME_ACCESSOR =
		new Accessor<Organization, String>() {

			@Override
			public String get(Organization organization) {
				return organization.getName();
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Organization> getTypeClass() {
				return Organization.class;
			}

		};

	public Address getAddress();

	public java.util.List<Address> getAddresses();

	public long[] getAncestorOrganizationIds()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Organization> getAncestors()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String[] getChildrenTypes();

	public java.util.List<Organization> getDescendants();

	public Group getGroup();

	public long getGroupId();

	public String getLogoURL();

	public Organization getParentOrganization()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getParentOrganizationName();

	public jakarta.portlet.PortletPreferences getPreferences();

	public int getPrivateLayoutsPageCount();

	public int getPublicLayoutsPageCount();

	public java.util.Set<String> getReminderQueryQuestions(
		java.util.Locale locale);

	public java.util.Set<String> getReminderQueryQuestions(String languageId);

	public java.util.List<Organization> getSuborganizations();

	public int getSuborganizationsSize();

	public int getTypeOrder();

	public boolean hasPrivateLayouts();

	public boolean hasPublicLayouts();

	public boolean hasSuborganizations();

	public boolean isParentable();

	public boolean isRoot();

}