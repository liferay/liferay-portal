/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.internal.resource.v1_0;

import com.liferay.headless.pim.dto.v1_0.LinkReference;
import com.liferay.headless.pim.resource.v1_0.LinkReferenceResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.site.pim.site.initializer.engine.PIMLinkEngine;
import com.liferay.site.pim.site.initializer.link.PIMLink;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/link-reference.properties",
	scope = ServiceScope.PROTOTYPE, service = LinkReferenceResource.class
)
public class LinkReferenceResourceImpl extends BaseLinkReferenceResourceImpl {

	@Override
	public Page<LinkReference> getScopeScopeKeyLinksPage(
			String scopeKey, String className, String externalReferenceCode,
			Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-96666")) {

			throw new UnsupportedOperationException();
		}

		List<PIMLink> pimLinks = _pimLinkEngine.getPIMLinks(
			_getFilterString(),
			_getObjectEntry(
				className, externalReferenceCode, _getGroupId(scopeKey)));

		return Page.of(
			transform(
				ListUtil.subList(
					pimLinks, pagination.getStartPosition(),
					pagination.getEndPosition()),
				pimLink -> _toLinkReference(
					pimLink.getObjectEntry(), pimLink.getType())),
			pagination, pimLinks.size());
	}

	private Map<String, Map<String, String>> _getActions(
			ObjectEntry objectEntry, String type)
		throws Exception {

		if (!_hasUpdatePermission(objectEntry)) {
			return null;
		}

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			HashMapBuilder.put(
				"href",
				StringBundler.concat(
					"/o/headless-pim/v1.0/scopes/", objectEntry.getGroupId(),
					"/links?className=",
					URLCodec.encodeURL(objectEntry.getModelClassName()),
					"&externalReferenceCode=",
					URLCodec.encodeURL(objectEntry.getExternalReferenceCode()),
					"&type=", URLCodec.encodeURL(type))
			).put(
				"method", "DELETE"
			).build()
		).build();
	}

	private String _getFilterString() {
		if (contextUriInfo == null) {
			return null;
		}

		MultivaluedMap<String, String> queryParameters =
			contextUriInfo.getQueryParameters();

		return queryParameters.getFirst("filter");
	}

	private long _getGroupId(String scopeKey) throws Exception {
		Long groupId = GroupUtil.getGroupId(
			contextCompany.getCompanyId(), scopeKey, groupLocalService);

		if (groupId == null) {
			throw new NoSuchGroupException();
		}

		return groupId;
	}

	private ObjectEntry _getObjectEntry(
			String className, String externalReferenceCode, long groupId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinitionByClassName(
				contextCompany.getCompanyId(), className);

		return _objectEntryLocalService.getObjectEntry(
			externalReferenceCode, groupId,
			objectDefinition.getObjectDefinitionId());
	}

	private boolean _hasUpdatePermission(ObjectEntry objectEntry)
		throws Exception {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			_objectEntryService.getModelResourcePermission(
				objectEntry.getObjectDefinitionId());

		return modelResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
	}

	private LinkReference _toLinkReference(ObjectEntry objectEntry, String type)
		throws Exception {

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry);

		return new LinkReference() {
			{
				setActions(() -> _getActions(objectEntry, type));
				setClassName(objectEntry::getModelClassName);
				setCode(() -> MapUtil.getString(values, "code"));
				setExternalReferenceCode(objectEntry::getExternalReferenceCode);
				setId(objectEntry::getObjectEntryId);
				setName(() -> MapUtil.getString(values, "name"));
				setStatus(
					() -> WorkflowConstants.getStatusLabel(
						objectEntry.getStatus()));
				setType(() -> type);
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private PIMLinkEngine _pimLinkEngine;

}