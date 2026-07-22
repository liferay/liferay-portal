/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.internal.importer.exception.DropzoneLayoutStructureItemException;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rubén Pulido
 */
public class DropZoneLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	public DropZoneLayoutStructureItemImporter(
		CompanyLocalService companyLocalService,
		DepotEntryLocalService depotEntryLocalService,
		FragmentCollectionContributorRegistry
			fragmentCollectionContributorRegistry,
		FragmentCollectionLocalService fragmentCollectionLocalService,
		FragmentEntryLocalService fragmentEntryLocalService,
		FragmentRendererRegistry fragmentRendererRegistry) {

		_companyLocalService = companyLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_fragmentCollectionContributorRegistry =
			fragmentCollectionContributorRegistry;
		_fragmentCollectionLocalService = fragmentCollectionLocalService;
		_fragmentEntryLocalService = fragmentEntryLocalService;
		_fragmentRendererRegistry = fragmentRendererRegistry;
	}

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		LayoutStructureItem existingLayoutStructureItem =
			layoutStructure.getDropZoneLayoutStructureItem();

		if (existingLayoutStructureItem != null) {
			throw new DropzoneLayoutStructureItemException();
		}

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)
				layoutStructure.addDropZoneLayoutStructureItem(
					layoutStructureItemImporterContext.getItemId(pageElement),
					layoutStructureItemImporterContext.getParentItemId(),
					layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return dropZoneLayoutStructureItem;
		}

		Object fragmentSettings = definitionMap.get("fragmentSettings");

		Map<String, Object> fragmentSettingsMap =
			(Map<String, Object>)fragmentSettings;

		if (fragmentSettingsMap == null) {
			return dropZoneLayoutStructureItem;
		}

		if ((!fragmentSettingsMap.containsKey(_KEY_ALLOWED_FRAGMENTS) &&
			 !fragmentSettingsMap.containsKey(_KEY_UNALLOWED_FRAGMENTS)) ||
			(fragmentSettingsMap.containsKey(_KEY_ALLOWED_FRAGMENTS) &&
			 fragmentSettingsMap.containsKey(_KEY_UNALLOWED_FRAGMENTS))) {

			return dropZoneLayoutStructureItem;
		}

		Set<String> fragmentEntryKeys = new HashSet<>();

		Set<String> fragmentCollectionKeys = new HashSet<>();

		List<Map<String, String>> allowedFragments = new ArrayList<>();

		if (fragmentSettingsMap.containsKey(_KEY_ALLOWED_FRAGMENTS)) {
			dropZoneLayoutStructureItem.setAllowNewFragmentEntries(false);

			allowedFragments.addAll(
				(List<Map<String, String>>)fragmentSettingsMap.get(
					_KEY_ALLOWED_FRAGMENTS));
		}

		if (fragmentSettingsMap.containsKey(_KEY_UNALLOWED_FRAGMENTS)) {
			dropZoneLayoutStructureItem.setAllowNewFragmentEntries(true);

			allowedFragments.addAll(
				(List<Map<String, String>>)fragmentSettingsMap.get(
					_KEY_UNALLOWED_FRAGMENTS));
		}

		Layout layout = layoutStructureItemImporterContext.getLayout();

		long[] groupIds = _getGroupIds(
			layout.getCompanyId(), layout.getGroupId());

		for (Map<String, String> allowedFragmentMap : allowedFragments) {
			fragmentEntryKeys.add(allowedFragmentMap.get(_KEY_KEY));

			String fragmentCollectionKey = _getFragmentCollectionKey(
				allowedFragmentMap.get(_KEY_KEY), groupIds);

			if (Validator.isNotNull(fragmentCollectionKey)) {
				fragmentCollectionKeys.add(fragmentCollectionKey);
			}
		}

		for (String fragmentCollectionKey : fragmentCollectionKeys) {
			fragmentEntryKeys.add(fragmentCollectionKey);
		}

		dropZoneLayoutStructureItem.setFragmentEntryKeys(
			new ArrayList<>(fragmentEntryKeys));

		return dropZoneLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.DROP_ZONE;
	}

	private FragmentEntry _fetchFragmentEntry(
		String fragmentKey, long[] groupIds) {

		for (long groupId : groupIds) {
			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.fetchFragmentEntry(
					groupId, fragmentKey);

			if (fragmentEntry != null) {
				return fragmentEntry;
			}
		}

		return null;
	}

	private String _getFragmentCollectionKey(
			String fragmentKey, long[] groupIds)
		throws Exception {

		FragmentEntry fragmentEntry = _fetchFragmentEntry(
			fragmentKey, groupIds);

		if (fragmentEntry != null) {
			FragmentCollection fragmentCollection =
				_fragmentCollectionLocalService.getFragmentCollection(
					fragmentEntry.getFragmentCollectionId());

			return fragmentCollection.getFragmentCollectionKey();
		}

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributors();

		for (FragmentCollectionContributor fragmentCollectionContributor :
				fragmentCollectionContributors) {

			String fragmentCollectionKey =
				fragmentCollectionContributor.getFragmentCollectionKey();

			if (fragmentKey.startsWith(
					fragmentCollectionKey + StringPool.DASH)) {

				return fragmentCollectionKey;
			}
		}

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(fragmentKey);

		if (fragmentRenderer != null) {
			return fragmentRenderer.getCollectionKey();
		}

		return null;
	}

	private long[] _getGroupIds(long companyId, long groupId) throws Exception {
		Company company = _companyLocalService.getCompany(companyId);

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {
			return new long[] {groupId, company.getGroupId()};
		}

		return ArrayUtil.append(
			new long[] {groupId},
			TransformUtil.transformToLongArray(
				_depotEntryLocalService.getGroupConnectedDepotEntries(
					groupId, DepotConstants.TYPE_DESIGN_LIBRARY,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				DepotEntry::getGroupId),
			new long[] {company.getGroupId()});
	}

	private static final String _KEY_ALLOWED_FRAGMENTS = "allowedFragments";

	private static final String _KEY_KEY = "key";

	private static final String _KEY_UNALLOWED_FRAGMENTS = "unallowedFragments";

	private final CompanyLocalService _companyLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private final FragmentCollectionLocalService
		_fragmentCollectionLocalService;
	private final FragmentEntryLocalService _fragmentEntryLocalService;
	private final FragmentRendererRegistry _fragmentRendererRegistry;

}