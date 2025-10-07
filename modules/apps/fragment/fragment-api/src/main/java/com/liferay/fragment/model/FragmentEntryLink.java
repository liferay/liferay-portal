/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the FragmentEntryLink service. Represents a row in the &quot;FragmentEntryLink&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.fragment.model.impl.FragmentEntryLinkImpl"
)
@ProviderType
public interface FragmentEntryLink
	extends FragmentEntryLinkModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.fragment.model.impl.FragmentEntryLinkImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<FragmentEntryLink, Long>
		FRAGMENT_ENTRY_LINK_ID_ACCESSOR =
			new Accessor<FragmentEntryLink, Long>() {

				@Override
				public Long get(FragmentEntryLink fragmentEntryLink) {
					return fragmentEntryLink.getFragmentEntryLinkId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<FragmentEntryLink> getTypeClass() {
					return FragmentEntryLink.class;
				}

			};

	public com.liferay.portal.kernel.json.JSONObject
		getConfigurationJSONObject();

	public com.liferay.portal.kernel.json.JSONObject getConfigurationJSONObject(
		boolean strict);

	public com.liferay.portal.kernel.json.JSONObject
		getEditableValuesJSONObject();

	public com.liferay.portal.kernel.json.JSONObject
		getEditableValuesJSONObject(boolean strict);

	public boolean isCacheable();

	public boolean isLatestVersion()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isSystem()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isTypeComponent();

	public boolean isTypeInput();

	public boolean isTypePortlet();

	public boolean isTypeReact();

	public boolean isTypeSection();

}