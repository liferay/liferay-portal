/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the SiteSitemapRegenerationEntry service. Represents a row in the &quot;SiteSitemapRegenerationEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.site.model.impl.SiteSitemapRegenerationEntryImpl"
)
@ProviderType
public interface SiteSitemapRegenerationEntry
	extends PersistedModel, SiteSitemapRegenerationEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<SiteSitemapRegenerationEntry, Long>
		SITE_SITEMAP_REGENERATION_ENTRY_ID_ACCESSOR =
			new Accessor<SiteSitemapRegenerationEntry, Long>() {

				@Override
				public Long get(
					SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

					return siteSitemapRegenerationEntry.
						getSiteSitemapRegenerationEntryId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<SiteSitemapRegenerationEntry> getTypeClass() {
					return SiteSitemapRegenerationEntry.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:751505431