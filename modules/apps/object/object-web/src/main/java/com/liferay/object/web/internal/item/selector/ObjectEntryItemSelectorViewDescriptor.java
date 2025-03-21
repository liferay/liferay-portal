/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.item.selector;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<ObjectEntry> {

	public ObjectEntryItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest,
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
		ObjectDefinition objectDefinition,
		ObjectEntryManager objectEntryManager,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		ObjectScopeProviderRegistry objectScopeProviderRegistry, Portal portal,
		PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
		_objectDefinition = objectDefinition;
		_objectEntryManager = objectEntryManager;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_portal = portal;
		_portletURL = portletURL;

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");
		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "descriptive";
	}

	@Override
	public ItemDescriptor getItemDescriptor(ObjectEntry objectEntry) {
		return new ObjectEntryItemDescriptor(
			_httpServletRequest, _objectDefinition, objectEntry, _portal);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new InfoItemItemSelectorReturnType();
	}

	@Override
	public SearchContainer<ObjectEntry> getSearchContainer()
		throws PortalException {

		SearchContainer<ObjectEntry> searchContainer = new SearchContainer<>(
			_portletRequest, null, null, "cur",
			ParamUtil.getInteger(_portletRequest, "cur"),
			ParamUtil.getInteger(_portletRequest, "delta"), _portletURL, null,
			"no-entries-were-found");

		try {
			if (ParamUtil.getLong(_portletRequest, "objectDefinitionId") != 0) {
				searchContainer.setResultsAndTotal(
					ArrayList::new, searchContainer.getEnd());

				String objectRelationshipType = ParamUtil.getString(
					_portletRequest, "objectRelationshipType");

				if (Validator.isNull(objectRelationshipType)) {
					return searchContainer;
				}

				ObjectRelatedModelsProvider objectRelatedModelsProvider =
					_objectRelatedModelsProviderRegistry.
						getObjectRelatedModelsProvider(
							_objectDefinition.getClassName(),
							CompanyThreadLocal.getCompanyId(),
							objectRelationshipType);

				ObjectScopeProvider objectScopeProvider =
					_objectScopeProviderRegistry.getObjectScopeProvider(
						_objectDefinition.getScope());

				long groupId = ParamUtil.getLong(_portletRequest, "groupId");

				if (!objectScopeProvider.isValidGroupId(groupId)) {
					groupId = 0;
				}

				long finalGroupId = groupId;

				searchContainer.setResultsAndTotal(
					() -> {
						if ((finalGroupId == 0) &&
							ObjectDefinitionConstants.SCOPE_SITE.equals(
								objectScopeProvider.getKey())) {

							return new ArrayList<>();
						}

						return objectRelatedModelsProvider.getUnrelatedModels(
							_objectDefinition.getCompanyId(), finalGroupId,
							_objectDefinition,
							ParamUtil.getLong(_portletRequest, "objectEntryId"),
							ParamUtil.getLong(
								_portletRequest, "objectRelationshipId"),
							_keywords, searchContainer.getStart(),
							searchContainer.getEnd());
					},
					objectRelatedModelsProvider.getUnrelatedModelsCount(
						_objectDefinition.getCompanyId(), finalGroupId,
						_objectDefinition,
						ParamUtil.getLong(_portletRequest, "objectEntryId"),
						ParamUtil.getLong(
							_portletRequest, "objectRelationshipId"),
						_keywords));
			}
			else {
				Group scopeGroup = _themeDisplay.getScopeGroup();

				Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> page =
					_objectEntryManager.getObjectEntries(
						_themeDisplay.getCompanyId(), _objectDefinition,
						scopeGroup.getGroupKey(), null,
						_getDTOConverterContext(), StringPool.BLANK,
						Pagination.of(
							searchContainer.getCur(),
							searchContainer.getDelta()),
						ParamUtil.getString(_portletRequest, "keywords"), null);

				searchContainer.setResultsAndTotal(
					() -> TransformUtil.transform(
						page.getItems(),
						objectEntry -> ObjectEntryUtil.toObjectEntry(
							_objectDefinition.getObjectDefinitionId(),
							objectEntry)),
					GetterUtil.getInteger(page.getTotalCount()));
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			searchContainer.setResultsAndTotal(ArrayList::new, 0);
		}

		return searchContainer;
	}

	@Override
	public boolean isMultipleSelection() {
		return _infoItemItemSelectorCriterion.isMultiSelection();
	}

	@Override
	public boolean isShowBreadcrumb() {
		return StringUtil.equals(
			_objectDefinition.getScope(), ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private DTOConverterContext _getDTOConverterContext() {
		return new DefaultDTOConverterContext(
			false, null, null, _httpServletRequest, null,
			_themeDisplay.getLocale(), null, _themeDisplay.getUser());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryItemSelectorViewDescriptor.class);

	private final HttpServletRequest _httpServletRequest;
	private final InfoItemItemSelectorCriterion _infoItemItemSelectorCriterion;
	private final String _keywords;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final Portal _portal;
	private final PortletRequest _portletRequest;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}