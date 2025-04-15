/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sorin Pop
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
	service = ExportImportPortletPreferencesProcessor.class
)
public class KBArticleExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return null;
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_capability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		long resourcePrimKey = GetterUtil.getLong(
			portletPreferences.getValue("resourcePrimKey", StringPool.BLANK));

		if (resourcePrimKey !=
				KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY) {

			List<KBArticle> kbArticles =
				_kbArticleLocalService.getKBArticleAndAllDescendantKBArticles(
					resourcePrimKey, WorkflowConstants.STATUS_APPROVED, null);

			for (KBArticle kbArticle : kbArticles) {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletDataContext.getPortletId(),
					kbArticle);
			}
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		long resourcePrimKey = GetterUtil.getLong(
			portletPreferences.getValue("resourcePrimKey", StringPool.BLANK));

		Map<Long, Long> kbArticleResourcePrimKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				KBArticle.class);

		resourcePrimKey = MapUtil.getLong(
			kbArticleResourcePrimKeys, resourcePrimKey, resourcePrimKey);

		try {
			portletPreferences.setValue(
				"resourcePrimKey", String.valueOf(resourcePrimKey));
		}
		catch (ReadOnlyException readOnlyException) {
			throw new PortletDataException(
				StringBundler.concat(
					"Unable to save converted portlet preference ",
					"resourcePrimKey=", resourcePrimKey,
					" (the root article) while importing KB Article portlet. ",
					"(portletId=", portletDataContext.getPortletId(), ")"),
				readOnlyException);
		}

		return portletPreferences;
	}

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _capability;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

}