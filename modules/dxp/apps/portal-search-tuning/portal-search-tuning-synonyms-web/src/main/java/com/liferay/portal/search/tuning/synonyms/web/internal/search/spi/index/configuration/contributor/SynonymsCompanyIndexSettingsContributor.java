/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.search.spi.index.configuration.contributor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexSettingsContributor;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.configuration.SynonymsConfiguration;
import com.liferay.portal.search.tuning.synonyms.web.internal.filter.SynonymSetFilterWriterUtil;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	configurationPid = "com.liferay.portal.search.tuning.synonyms.web.internal.configuration.SynonymsConfiguration",
	service = CompanyIndexSettingsContributor.class
)
public class SynonymsCompanyIndexSettingsContributor
	implements CompanyIndexSettingsContributor {

	@Override
	public String contributeSettings(long companyId) {
		if (companyId == CompanyConstants.SYSTEM) {
			return null;
		}

		try {
			SynonymSetIndexReader synonymSetIndexReader =
				new SynonymSetIndexReader(_searchEngineAdapter);

			synchronized (_synonymSetIndexNameBuilder) {
				return _buildSettings(
					TransformUtil.transformToArray(
						synonymSetIndexReader.search(
							_synonymSetIndexNameBuilder.getSynonymSetIndexName(
								companyId)),
						SynonymSet::getSynonyms, String.class));
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to load synonyms index settings for company ID " +
						companyId,
					exception);
			}
		}

		return null;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		SynonymsConfiguration synonymsConfiguration =
			ConfigurableUtil.createConfigurable(
				SynonymsConfiguration.class, properties);

		_filterNames = synonymsConfiguration.filterNames();
	}

	private String _buildSettings(String[] synonymSets) {
		return SynonymSetFilterWriterUtil.buildSettings(
			_filterNames, synonymSets);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SynonymsCompanyIndexSettingsContributor.class);

	private static volatile String[] _filterNames;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

}