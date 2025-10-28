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
 * The extended model interface for the FragmentEntry service. Represents a row in the &quot;FragmentEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryModel
 * @generated
 */
@ImplementationClassName("com.liferay.fragment.model.impl.FragmentEntryImpl")
@ProviderType
public interface FragmentEntry extends FragmentEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.fragment.model.impl.FragmentEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<FragmentEntry, Long>
		FRAGMENT_ENTRY_ID_ACCESSOR = new Accessor<FragmentEntry, Long>() {

			@Override
			public Long get(FragmentEntry fragmentEntry) {
				return fragmentEntry.getFragmentEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<FragmentEntry> getTypeClass() {
				return FragmentEntry.class;
			}

		};

	public FragmentEntry fetchDraftFragmentEntry();

	public String getContent();

	public int getGlobalUsageCount();

	public String getImagePreviewURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public String getScopeERC();

	public String getTypeLabel();

	public int getUsageCount();

	public boolean isTypeComponent();

	public boolean isTypeInput();

	public boolean isTypeReact();

	public boolean isTypeSection();

	public void populateVersionModel(FragmentEntryVersion fragmentEntryVersion);

	public void populateZipWriter(
			com.liferay.portal.kernel.zip.ZipWriter zipWriter, String path)
		throws Exception;

	public void setImagePreviewURL(String imagePreviewURL);

}