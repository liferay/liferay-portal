/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the AssetListEntry service. Represents a row in the &quot;AssetListEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryModel
 * @generated
 */
@ImplementationClassName("com.liferay.asset.list.model.impl.AssetListEntryImpl")
@ProviderType
public interface AssetListEntry extends AssetListEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.asset.list.model.impl.AssetListEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<AssetListEntry, Long>
		ASSET_LIST_ENTRY_ID_ACCESSOR = new Accessor<AssetListEntry, Long>() {

			@Override
			public Long get(AssetListEntry assetListEntry) {
				return assetListEntry.getAssetListEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<AssetListEntry> getTypeClass() {
				return AssetListEntry.class;
			}

		};

	public String getTypeLabel();

	public String getTypeSettings(long segmentsEntryId);

	public String getTypeSettings(String audienceEntryERC);

	public String getUnambiguousTitle(java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:107645966