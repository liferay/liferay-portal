/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.contributor.featured.content;

import com.liferay.fragment.contributor.BaseFragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.collection.key=FEATURED_CONTENT",
	service = FragmentCollectionContributor.class
)
public class FeaturedContentFragmentCollectionContributor
	extends BaseFragmentCollectionContributor {

	@Override
	public String getFragmentCollectionKey() {
		return "FEATURED_CONTENT";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isDeprecated() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return FeatureFlagManagerUtil.isEnabled("LPD-40529");
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.collection.contributor.featured.content)"
	)
	private ServletContext _servletContext;

}