/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.search.instance.lifecycle;

import com.liferay.commerce.machine.learning.internal.search.constants.IndexMappingFileNames;
import com.liferay.commerce.machine.learning.internal.search.constants.IndexNamePatterns;
import com.liferay.commerce.machine.learning.internal.search.index.CommerceMLIndexer;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CommerceMLIndexerPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		try {
			for (CommerceMLIndexer commerceMLIndexer : _commerceMLIndexers) {
				commerceMLIndexer.createIndex(
					_indexNameBuilder, _searchCapabilities,
					_searchEngineAdapter, company.getCompanyId());
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to add commerce recommend index for company " + company,
				exception);
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		try {
			for (CommerceMLIndexer commerceMLIndexer : _commerceMLIndexers) {
				commerceMLIndexer.dropIndex(
					_indexNameBuilder, _searchCapabilities,
					_searchEngineAdapter, company.getCompanyId());
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to remove commerce recommend index for company " +
					company,
				exception);
		}
	}

	@Activate
	protected void activate() {
		_commerceMLIndexers.add(
			new CommerceMLIndexer(
				IndexMappingFileNames.FORECAST, IndexNamePatterns.FORECAST));
		_commerceMLIndexers.add(
			new CommerceMLIndexer(
				IndexMappingFileNames.FREQUENT_PATTERN_RECOMMENDATION,
				IndexNamePatterns.FREQUENT_PATTERN_RECOMMENDATION));
		_commerceMLIndexers.add(
			new CommerceMLIndexer(
				IndexMappingFileNames.PRODUCT_RECOMMENDATION,
				IndexNamePatterns.PRODUCT_CONTENT_RECOMMENDATION));
		_commerceMLIndexers.add(
			new CommerceMLIndexer(
				IndexMappingFileNames.PRODUCT_RECOMMENDATION,
				IndexNamePatterns.PRODUCT_INTERACTION_RECOMMENDATION));
		_commerceMLIndexers.add(
			new CommerceMLIndexer(
				IndexMappingFileNames.USER_RECOMMENDATION,
				IndexNamePatterns.USER_RECOMMENDATION));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceMLIndexerPortalInstanceLifecycleListener.class);

	private final List<CommerceMLIndexer> _commerceMLIndexers =
		new ArrayList<>();

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}