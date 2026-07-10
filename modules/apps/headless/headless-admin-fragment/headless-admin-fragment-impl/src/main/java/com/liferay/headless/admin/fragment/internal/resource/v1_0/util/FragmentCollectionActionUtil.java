/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0.util;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.FragmentSetResourceImpl;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.util.ActionUtil;

import jakarta.ws.rs.core.UriInfo;

import java.util.Collections;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentCollectionActionUtil {

	public static Map<String, Map<String, String>> getDesignLibraryActions(
		Object contextScopeChecker, String designLibraryExternalReferenceCode,
		FragmentCollection fragmentCollection, boolean manageFragmentEntries,
		UriInfo uriInfo) {

		if (!manageFragmentEntries) {
			return Collections.emptyMap();
		}

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"designLibraryExternalReferenceCode",
			designLibraryExternalReferenceCode
		).put(
			"fragmentSetExternalReferenceCode",
			fragmentCollection.getExternalReferenceCode()
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				contextScopeChecker, fragmentCollection,
				"deleteDesignLibraryFragmentSet", templateParameterMap, uriInfo)
		).build();
	}

	public static Map<String, Map<String, String>> getSiteActions(
		Object contextScopeChecker, FragmentCollection fragmentCollection,
		boolean manageFragmentEntries, String siteExternalReferenceCode,
		UriInfo uriInfo) {

		if (!manageFragmentEntries) {
			return Collections.emptyMap();
		}

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"fragmentSetExternalReferenceCode",
			fragmentCollection.getExternalReferenceCode()
		).put(
			"siteExternalReferenceCode", siteExternalReferenceCode
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				contextScopeChecker, fragmentCollection,
				"deleteSiteFragmentSet", templateParameterMap, uriInfo)
		).put(
			"get",
			_addAction(
				contextScopeChecker, fragmentCollection, "getSiteFragmentSet",
				templateParameterMap, uriInfo)
		).put(
			"replace",
			_addAction(
				contextScopeChecker, fragmentCollection, "putSiteFragmentSet",
				templateParameterMap, uriInfo)
		).build();
	}

	private static Map<String, String> _addAction(
		Object contextScopeChecker, FragmentCollection fragmentCollection,
		String methodName, Map<String, String> templateParameterMap,
		UriInfo uriInfo) {

		return ActionUtil.addAction(
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES,
			FragmentSetResourceImpl.class,
			fragmentCollection.getFragmentCollectionId(), methodName,
			contextScopeChecker, null, FragmentConstants.RESOURCE_NAME,
			fragmentCollection.getGroupId(), templateParameterMap, uriInfo);
	}

}