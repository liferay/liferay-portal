/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.CompanyConstants;

import java.util.Locale;

/**
 * @author Rubén Pulido
 */
public class FragmentCollectionContributorItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public FragmentCollectionContributorItemDescriptor(
		FragmentCollectionContributor fragmentCollectionContributor,
		Locale locale) {

		_fragmentCollectionContributor = fragmentCollectionContributor;
		_locale = locale;
	}

	@Override
	public String getIcon() {
		return "documents-and-media";
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"fragmentCollectionKey",
			_fragmentCollectionContributor.getFragmentCollectionKey()
		).put(
			"groupId", CompanyConstants.SYSTEM
		).put(
			"name", _fragmentCollectionContributor.getName(_locale)
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _fragmentCollectionContributor.getName(locale);
	}

	@Override
	public boolean isCompact() {
		return true;
	}

	private final FragmentCollectionContributor _fragmentCollectionContributor;
	private final Locale _locale;

}