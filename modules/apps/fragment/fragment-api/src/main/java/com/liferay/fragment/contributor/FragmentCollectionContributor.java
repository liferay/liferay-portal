/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.contributor;

import aQute.bnd.annotation.ProviderType;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
@ProviderType
public interface FragmentCollectionContributor {

	public String getFragmentCollectionKey();

	public default List<FragmentComposition> getFragmentCompositions() {
		return Collections.emptyList();
	}

	public default List<FragmentComposition> getFragmentCompositions(
		Locale locale) {

		return Collections.emptyList();
	}

	public List<FragmentEntry> getFragmentEntries();

	public List<FragmentEntry> getFragmentEntries(int type);

	public default List<FragmentEntry> getFragmentEntries(
		int type, Locale locale) {

		return getFragmentEntries(type);
	}

	public List<FragmentEntry> getFragmentEntries(int[] types);

	public default List<FragmentEntry> getFragmentEntries(
		int[] types, Locale locale) {

		return getFragmentEntries(types);
	}

	public default List<FragmentEntry> getFragmentEntries(Locale locale) {
		return getFragmentEntries();
	}

	public String getFragmentEntryName(String fragmentEntryKey, Locale locale);

	public String getName();

	public default String getName(Locale locale) {
		return getName();
	}

	public Map<Locale, String> getNames();

	public default ResourceBundleLoader getResourceBundleLoader() {
		return ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
	}

	public default boolean isDeprecated() {
		return false;
	}

	public default boolean isEnabled() {
		return true;
	}

}