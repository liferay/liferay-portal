/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.contributor.inputs;

import com.liferay.fragment.contributor.BaseFragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.ServletContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Molina
 */
@Component(
	property = "fragment.collection.key=INPUTS",
	service = FragmentCollectionContributor.class
)
public class InputsFragmentCollectionContributor
	extends BaseFragmentCollectionContributor {

	@Override
	public String getFragmentCollectionKey() {
		return "INPUTS";
	}

	@Override
	public List<FragmentEntry> getFragmentEntries() {
		return _filter(super.getFragmentEntries());
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int type) {
		return _filter(super.getFragmentEntries(type));
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	private List<FragmentEntry> _filter(List<FragmentEntry> fragmentEntries) {
		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-21926")) {

			fragmentEntries = ListUtil.filter(
				fragmentEntries,
				fragmentEntry -> !Objects.equals(
					fragmentEntry.getFragmentEntryKey(),
					"INPUTS-friendly-url-input"));
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			Set<String> excludedKeys = Set.of(
				"INPUTS-drag-and-drop-upload", "INPUTS-video-previewer-input");

			fragmentEntries = ListUtil.filter(
				fragmentEntries,
				fragmentEntry -> !excludedKeys.contains(
					fragmentEntry.getFragmentEntryKey()));
		}

		return fragmentEntries;
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.collection.contributor.inputs)"
	)
	private ServletContext _servletContext;

}