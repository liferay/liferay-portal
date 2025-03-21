/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.portlet.preferences.processor.capability;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "name=ReferencedStagedModelImporter", service = Capability.class
)
public class ReferencedStagedModelImporterCapability implements Capability {

	@Override
	public PortletPreferences process(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		Element referencesElement = importDataRootElement.element("references");

		if (referencesElement == null) {
			return portletPreferences;
		}

		List<Element> referenceElements = referencesElement.elements();

		long originalScopeGroupId = portletDataContext.getScopeGroupId();

		for (Element referenceElement : referenceElements) {
			try {
				String className = referenceElement.attributeValue(
					"class-name");
				long classPK = GetterUtil.getLong(
					referenceElement.attributeValue("class-pk"));

				String scopeLayoutUuid = GetterUtil.getString(
					referenceElement.attributeValue("scope-layout-uuid"));

				if (Validator.isNotNull(scopeLayoutUuid)) {
					try {
						Layout scopeLayout =
							_layoutLocalService.getLayoutByUuidAndGroupId(
								scopeLayoutUuid,
								portletDataContext.getGroupId(),
								portletDataContext.isPrivateLayout());

						Group scopeGroup = _groupLocalService.checkScopeGroup(
							scopeLayout, portletDataContext.getUserId(null));

						portletDataContext.setScopeGroupId(
							scopeGroup.getGroupId());
					}
					catch (PortalException portalException) {
						StringBundler sb = new StringBundler(9);

						sb.append("Unable to import the layout scoped ");
						sb.append("element with class name ");
						sb.append(className);
						sb.append(" and class primary key ");
						sb.append(classPK);
						sb.append(" because the layout with UUID ");
						sb.append(scopeLayoutUuid);
						sb.append(" is missing from group ");
						sb.append(portletDataContext.getGroupId());

						if (_log.isDebugEnabled()) {
							_log.debug(sb.toString(), portalException);
						}

						if (portalException instanceof NoSuchLayoutException) {
							continue;
						}

						throw new PortletDataException(
							sb.toString(), portalException);
					}
				}

				StagedModelDataHandlerUtil.importReferenceStagedModel(
					portletDataContext, className, Long.valueOf(classPK));
			}
			finally {
				portletDataContext.setScopeGroupId(originalScopeGroupId);
			}
		}

		return portletPreferences;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReferencedStagedModelImporterCapability.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

}