/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.folder.facet.portlet.shared.search;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.facet.folder.FolderFacetSearchContributor;
import com.liferay.portal.search.web.internal.folder.facet.constants.FolderFacetPortletKeys;
import com.liferay.portal.search.web.internal.folder.facet.portlet.FolderFacetPortletPreferences;
import com.liferay.portal.search.web.internal.folder.facet.portlet.FolderFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + FolderFacetPortletKeys.FOLDER_FACET,
	service = PortletSharedSearchContributor.class
)
public class FolderFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		FolderFacetPortletPreferences folderFacetPortletPreferences =
			new FolderFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		folderFacetSearchContributor.contribute(
			portletSharedSearchSettings.getSearchRequestBuilder(),
			folderFacetBuilder -> folderFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).frequencyThreshold(
				folderFacetPortletPreferences.getFrequencyThreshold()
			).maxTerms(
				folderFacetPortletPreferences.getMaxTerms()
			).selectedFolderIds(
				_toLongArray(
					portletSharedSearchSettings.getParameterValues(
						folderFacetPortletPreferences.getParameterName()))
			));
	}

	@Reference
	protected FolderFacetSearchContributor folderFacetSearchContributor;

	private long[] _toLongArray(String[] parameterValues) {
		if (ArrayUtil.isNotEmpty(parameterValues)) {
			return ListUtil.toLongArray(
				Arrays.asList(parameterValues), GetterUtil::getLong);
		}

		return new long[0];
	}

}