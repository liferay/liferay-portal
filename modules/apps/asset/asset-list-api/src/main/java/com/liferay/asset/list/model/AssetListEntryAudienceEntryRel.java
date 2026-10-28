/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the AssetListEntryAudienceEntryRel service. Represents a row in the &quot;AssetListEntryAudienceEntryRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelImpl"
)
@ProviderType
public interface AssetListEntryAudienceEntryRel
	extends AssetListEntryAudienceEntryRelModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<AssetListEntryAudienceEntryRel, Long>
		ASSET_LIST_ENTRY_AUDIENCE_ENTRY_REL_ID_ACCESSOR =
			new Accessor<AssetListEntryAudienceEntryRel, Long>() {

				@Override
				public Long get(
					AssetListEntryAudienceEntryRel
						assetListEntryAudienceEntryRel) {

					return assetListEntryAudienceEntryRel.
						getAssetListEntryAudienceEntryRelId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<AssetListEntryAudienceEntryRel> getTypeClass() {
					return AssetListEntryAudienceEntryRel.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:1597446204