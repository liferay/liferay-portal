/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.contributor.basic.component;

import com.liferay.fragment.contributor.BaseFragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.model.FragmentEntry;

import jakarta.servlet.ServletContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "fragment.collection.key=BASIC_COMPONENT",
	service = FragmentCollectionContributor.class
)
public class BasicComponentFragmentCollectionContributor
	extends BaseFragmentCollectionContributor {

	@Override
	public String getFragmentCollectionKey() {
		return "BASIC_COMPONENT";
	}

	@Override
	public List<FragmentEntry> getFragmentEntries() {
		return super.getFragmentEntries();
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int type) {
		return super.getFragmentEntries(type);
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.collection.contributor.basic.component)"
	)
	private ServletContext _servletContext;

}