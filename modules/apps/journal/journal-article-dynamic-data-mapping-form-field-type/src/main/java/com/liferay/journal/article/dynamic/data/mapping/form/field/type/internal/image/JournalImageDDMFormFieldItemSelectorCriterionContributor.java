/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.article.dynamic.data.mapping.form.field.type.internal.image;

import com.liferay.dynamic.data.mapping.form.field.type.image.ImageDDMFormFieldItemSelectorCriterionContributor;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.item.selector.JournalItemSelectorCriterion;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ImageDDMFormFieldItemSelectorCriterionContributor.class)
public class JournalImageDDMFormFieldItemSelectorCriterionContributor
	implements ImageDDMFormFieldItemSelectorCriterionContributor {

	@Override
	public ItemSelectorCriterion getItemSelectorCriterion(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");
		String articleId = ParamUtil.getString(httpServletRequest, "articleId");

		long resourcePrimaryKey = _getResourcePrimaryKey(groupId, articleId);

		long folderId = ParamUtil.getLong(httpServletRequest, "folderId");

		JournalItemSelectorCriterion journalItemSelectorCriterion =
			new JournalItemSelectorCriterion(resourcePrimaryKey, folderId);

		journalItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType());

		return journalItemSelectorCriterion;
	}

	@Override
	public boolean isVisible(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		return Objects.equals(
			ddmFormFieldRenderingContext.getPortletNamespace(),
			_portal.getPortletNamespace(JournalPortletKeys.JOURNAL));
	}

	private long _getResourcePrimaryKey(long groupId, String articleId) {
		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(groupId, articleId);

		if (journalArticle != null) {
			return journalArticle.getResourcePrimKey();
		}

		return 0L;
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Portal _portal;

}