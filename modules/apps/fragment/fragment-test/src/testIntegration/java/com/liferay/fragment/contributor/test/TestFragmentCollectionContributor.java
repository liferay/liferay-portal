/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.contributor.test;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class TestFragmentCollectionContributor
	implements FragmentCollectionContributor {

	public TestFragmentCollectionContributor(
		String fragmentCollectionKey,
		Map<Integer, FragmentEntry> fragmentEntriesMap,
		FragmentComposition... fragmentCompositions) {

		_fragmentCollectionKey = fragmentCollectionKey;
		_fragmentEntriesMap = fragmentEntriesMap;

		_fragmentCompositions = ListUtil.fromArray(fragmentCompositions);
	}

	@Override
	public String getFragmentCollectionKey() {
		return _fragmentCollectionKey;
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions() {
		return _fragmentCompositions;
	}

	@Override
	public List<FragmentEntry> getFragmentEntries() {
		return Collections.emptyList();
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int type) {
		FragmentEntry fragmentEntry = _fragmentEntriesMap.get(type);

		if (fragmentEntry != null) {
			return Collections.singletonList(fragmentEntry);
		}

		return Collections.emptyList();
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int[] types) {
		List<FragmentEntry> fragmentEntries = new ArrayList<>();

		for (int type : types) {
			FragmentEntry fragmentEntry = _fragmentEntriesMap.get(type);

			if (fragmentEntry != null) {
				fragmentEntries.add(fragmentEntry);
			}
		}

		return fragmentEntries;
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(Locale locale) {
		return Collections.emptyList();
	}

	@Override
	public String getFragmentEntryName(String fragmentEntryKey, Locale locale) {
		return null;
	}

	@Override
	public String getName() {
		return "Test Fragment Collection Contributor";
	}

	@Override
	public Map<Locale, String> getNames() {
		return HashMapBuilder.put(
			LocaleUtil.getSiteDefault(), getName()
		).build();
	}

	private final String _fragmentCollectionKey;
	private final List<FragmentComposition> _fragmentCompositions;
	private final Map<Integer, FragmentEntry> _fragmentEntriesMap;

}