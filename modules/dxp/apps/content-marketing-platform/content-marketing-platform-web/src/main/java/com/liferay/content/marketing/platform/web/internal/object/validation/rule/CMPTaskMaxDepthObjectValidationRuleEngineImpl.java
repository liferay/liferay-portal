/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.object.validation.rule;

import com.liferay.content.marketing.platform.web.internal.util.CMPObjectEntryUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Larissa Ribeiro
 */
@Component(service = ObjectValidationRuleEngine.class)
public class CMPTaskMaxDepthObjectValidationRuleEngineImpl
	implements ObjectDefinitionScoped, ObjectValidationRuleEngine {

	@Override
	public Map<String, Object> execute(
		Map<String, Object> inputObjects, String script) {

		return HashMapBuilder.<String, Object>put(
			"validationCriteriaMet",
			() -> {
				long parentCMPTaskObjectEntryId = GetterUtil.getLong(
					CMPObjectEntryUtil.getParentCMPTaskObjectEntryId(
						(Map<String, Object>)inputObjects.get("entryDTO")));

				if (parentCMPTaskObjectEntryId == 0) {
					return true;
				}

				if (!FeatureFlagManagerUtil.isEnabled(
						CompanyThreadLocal.getCompanyId(), "LPD-107678")) {

					return false;
				}

				ObjectEntry parentCMPTaskObjectEntry =
					_objectEntryLocalService.fetchObjectEntry(
						parentCMPTaskObjectEntryId);

				if (parentCMPTaskObjectEntry == null) {
					return true;
				}

				List<ObjectEntry> parentCMPTaskObjectEntries =
					CMPObjectEntryUtil.getParentCMPTaskObjectEntries(
						parentCMPTaskObjectEntry, _objectEntryLocalService);

				if ((parentCMPTaskObjectEntries.size() < 2) &&
					!parentCMPTaskObjectEntries.contains(
						parentCMPTaskObjectEntry)) {

					return true;
				}

				return false;
			}
		).build();
	}

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return Collections.singletonList("CMPTask");
	}

	@Override
	public String getKey() {
		return "javaDelegate#CMPTask#maxDepth";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "cmp-task-max-depth");
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}