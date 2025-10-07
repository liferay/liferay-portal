/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalDefaultTemplateProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.template.TemplateVariableDefinition;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pavel Savinov
 */
@Component(service = JournalDefaultTemplateProvider.class)
public class JournalDefaultTemplateProviderImpl
	implements JournalDefaultTemplateProvider {

	@Override
	public String getLanguage() {
		return TemplateConstants.LANG_TYPE_FTL;
	}

	@Override
	public String getScript(long ddmStructureId) throws Exception {
		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(
				JournalArticle.class.getName());

		Map<String, TemplateVariableGroup> templateVariableGroups =
			templateHandler.getTemplateVariableGroups(
				ddmStructureId, getLanguage(),
				LocaleUtil.getMostRelevantLocale());

		TemplateVariableGroup templateVariableGroup =
			templateVariableGroups.get("fields");

		if (templateVariableGroup == null) {
			return StringPool.BLANK;
		}

		Collection<TemplateVariableDefinition> templateVariableDefinitions =
			templateVariableGroup.getTemplateVariableDefinitions();

		StringBundler sb = new StringBundler(
			(templateVariableDefinitions.size() * 5) + 2);

		sb.append("<dl>");

		for (TemplateVariableDefinition templateVariableDefinition :
				templateVariableDefinitions) {

			if ((templateVariableDefinition == null) ||
				(templateVariableDefinition.getTemplateVariableCodeHandler() ==
					null)) {

				continue;
			}

			String code =
				templateVariableDefinition.generateCode(getLanguage())[0];

			sb.append("<dt class=\"text-capitalize\">");
			sb.append(HtmlUtil.escape(templateVariableDefinition.getLabel()));
			sb.append("</dt><dd>");
			sb.append(code);
			sb.append("</dd>");
		}

		sb.append("</dl>");

		return sb.toString();
	}

	@Override
	public boolean isCacheable() {
		return false;
	}

}