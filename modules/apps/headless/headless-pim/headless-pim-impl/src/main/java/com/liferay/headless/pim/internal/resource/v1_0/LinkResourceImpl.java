/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.internal.resource.v1_0;

import com.liferay.headless.pim.dto.v1_0.Link;
import com.liferay.headless.pim.dto.v1_0.LinkReference;
import com.liferay.headless.pim.resource.v1_0.LinkResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.site.pim.site.initializer.engine.PIMLinkEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/link.properties",
	scope = ServiceScope.PROTOTYPE, service = LinkResource.class
)
public class LinkResourceImpl extends BaseLinkResourceImpl {

	@Override
	public void deleteScopeScopeKeyLink(
			String scopeKey, String className, String externalReferenceCode,
			String type)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-96666")) {

			throw new UnsupportedOperationException();
		}

		_pimLinkEngine.deletePIMLink(
			_getObjectEntry(
				className, externalReferenceCode, _getGroupId(scopeKey)),
			type);
	}

	@Override
	public void postScopeScopeKeyLink(String scopeKey, Link link)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-96666")) {

			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		LinkReference sourceLinkReference = link.getSourceLinkReference();

		_pimLinkEngine.addPIMLinks(
			_getObjectEntry(
				sourceLinkReference.getClassName(),
				sourceLinkReference.getExternalReferenceCode(), groupId),
			transformToList(
				link.getTargetLinkReferences(),
				linkReference -> _getObjectEntry(
					linkReference.getClassName(),
					linkReference.getExternalReferenceCode(), groupId)),
			link.getType());
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

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PIMLinkEngine _pimLinkEngine;

}