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
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sorin Pop
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
	service = ExportImportPortletPreferencesProcessor.class
)
public class KBDisplayExportImportPortletPreferencesProcessor
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

		long resourceClassNameId = GetterUtil.getLong(
			portletPreferences.getValue(
				"resourceClassNameId", StringPool.BLANK));

		String resourceClassName;

		if ((resourcePrimKey ==
				KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY) &&
			(resourceClassNameId == 0)) {

			resourceClassName = KBFolderConstants.getClassName();
		}
		else {
			resourceClassName = _portal.getClassName(resourceClassNameId);
		}

		try {
			portletPreferences.setValue(
				"resourceClassNameId", resourceClassName);
		}
		catch (ReadOnlyException readOnlyException) {
			throw new PortletDataException(
				StringBundler.concat(
					"Unable to save converted portlet preference ",
					"\"resourceClassNameId\" from ", resourceClassNameId,
					" to ", resourceClassName,
					" while exporting KB Display portlet ",
					portletDataContext.getPortletId()),
				readOnlyException);
		}

		if (resourceClassName.equals(KBArticleConstants.getClassName())) {
			if (resourcePrimKey !=
					KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY) {

				List<KBArticle> kbArticles =
					_kbArticleLocalService.
						getKBArticleAndAllDescendantKBArticles(
							resourcePrimKey, WorkflowConstants.STATUS_APPROVED,
							null);

				for (KBArticle kbArticle : kbArticles) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletDataContext.getPortletId(),
						kbArticle);
				}
			}
		}
		else if (resourceClassName.equals(KBFolderConstants.getClassName())) {
			if (resourcePrimKey != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				KBFolder rootFolder = _kbFolderLocalService.fetchKBFolder(
					resourcePrimKey);

				if (rootFolder == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Portlet ", portletDataContext.getPortletId(),
								" refers to an invalid root folder ID ",
								resourcePrimKey));
					}
				}
				else {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletDataContext.getPortletId(),
						rootFolder);
				}
			}
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String resourceClassName = GetterUtil.getString(
			portletPreferences.getValue(
				"resourceClassNameId", StringPool.BLANK));

		try {
			portletPreferences.setValue(
				"resourceClassNameId",
				String.valueOf(_portal.getClassNameId(resourceClassName)));
		}
		catch (ReadOnlyException readOnlyException) {
			throw new PortletDataException(
				StringBundler.concat(
					"Unable to save reconverted portlet preference ",
					"\"resourceClassNameId\" from ", resourceClassName, " to ",
					_portal.getClassNameId(resourceClassName),
					" while importing KB Display portlet ",
					portletDataContext.getPortletId()),
				readOnlyException);
		}

		long resourcePrimKey = GetterUtil.getLong(
			portletPreferences.getValue("resourcePrimKey", StringPool.BLANK));

		Map<Long, Long> resourcePrimKeys = new HashMap<>();

		if (resourceClassName.equals(KBArticleConstants.getClassName())) {
			resourcePrimKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					KBArticle.class);
		}
		else if (resourceClassName.equals(KBFolderConstants.getClassName())) {
			resourcePrimKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					KBFolder.class);
		}

		resourcePrimKey = MapUtil.getLong(
			resourcePrimKeys, resourcePrimKey, resourcePrimKey);

		try {
			portletPreferences.setValue(
				"resourcePrimKey", String.valueOf(resourcePrimKey));
		}
		catch (ReadOnlyException readOnlyException) {
			throw new PortletDataException(
				StringBundler.concat(
					"Unable to save converted portlet preference ",
					"\"resourcePrimKey\" ", resourcePrimKey,
					" while importing KB Display portlet ",
					portletDataContext.getPortletId()),
				readOnlyException);
		}

		return portletPreferences;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBDisplayExportImportPortletPreferencesProcessor.class);

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _capability;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private KBFolderLocalService _kbFolderLocalService;

	@Reference
	private Portal _portal;

}