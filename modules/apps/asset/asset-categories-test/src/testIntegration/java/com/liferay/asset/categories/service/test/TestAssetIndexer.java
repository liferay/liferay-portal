/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Summary;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.junit.Assert;

/**
 * @author Michael C. Han
 */
public class TestAssetIndexer extends BaseIndexer<Organization> {

	public static final String CLASS_NAME = TestAssetIndexer.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void reindex(String className, long classPK) {
		Assert.assertEquals(_className, className);
		Assert.assertEquals(_classPK, classPK);
	}

	public void setExpectedValues(String className, long classPK) {
		_className = className;
		_classPK = classPK;
	}

	@Override
	protected void doDelete(Organization organization) {
	}

	@Override
	protected Document doGetDocument(Organization organization) {
		return null;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	@Override
	protected void doReindex(Organization organization) {
	}

	@Override
	protected void doReindex(String className, long classPK) {
	}

	@Override
	protected void doReindex(String[] ids) {
	}

	private String _className;
	private long _classPK;

}