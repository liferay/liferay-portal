/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.model.listener;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.util.Portal;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Díaz
 */
@Component(service = ModelListener.class)
public class DDMStructureModelListener extends BaseModelListener<DDMStructure> {

	@Override
	public void onAfterUpdate(
			DDMStructure originalDDMStructure, DDMStructure ddmStructure)
		throws ModelListenerException {

		if ((ddmStructure.getClassNameId() != _portal.getClassNameId(
				JournalArticle.class)) ||
			(Objects.equals(
				originalDDMStructure.getStructureKey(),
				ddmStructure.getStructureKey()) &&
			 Objects.equals(
				 originalDDMStructure.getDefinition(),
				 ddmStructure.getDefinition())) ||
			_hasModifiedPredefinedValue(
				originalDDMStructure.getDDMForm(), ddmStructure.getDDMForm())) {

			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_journalArticleLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property ddmStructureIdProperty = PropertyFactoryUtil.forName(
					"DDMStructureId");

				dynamicQuery.add(
					ddmStructureIdProperty.eq(
						originalDDMStructure.getStructureId()));
			});
		actionableDynamicQuery.setCompanyId(
			originalDDMStructure.getCompanyId());

		ActionableDynamicQuery.PerformActionMethod<?> performActionMethod =
			null;

		if (Objects.equals(
				originalDDMStructure.getDefinition(),
				ddmStructure.getDefinition())) {

			Indexer<JournalArticle> indexer =
				_indexerRegistry.nullSafeGetIndexer(JournalArticle.class);

			performActionMethod = (JournalArticle journalArticle) -> {
				try {
					indexer.reindex(journalArticle);
				}
				catch (Exception exception) {
					throw new PortalException(exception);
				}
			};
		}
		else {
			performActionMethod = (JournalArticle journalArticle) -> {
				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ddmStructure.getCtCollectionId())) {

					_ddmFieldLocalService.updateDDMFormValues(
						ddmStructure.getStructureId(), journalArticle.getId(),
						_fieldsToDDMFormValuesConverter.convert(
							ddmStructure,
							_journalConverter.getDDMFields(
								ddmStructure, journalArticle.getContent())));
				}
			};
		}

		actionableDynamicQuery.setParallel(true);
		actionableDynamicQuery.setPerformActionMethod(performActionMethod);

		try {
			actionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeRemove(DDMStructure ddmStructure)
		throws ModelListenerException {

		try {
			_journalArticleLocalService.deleteArticles(
				ddmStructure.getGroupId(), DDMStructure.class.getName(),
				ddmStructure.getStructureId());
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private boolean _hasModifiedPredefinedValue(
		DDMForm ddmForm1, DDMForm ddmForm2) {

		Map<String, DDMFormField> ddmFormFieldsMap1 =
			ddmForm1.getDDMFormFieldsMap(true);

		Map<String, DDMFormField> ddmFormFieldsMap2 =
			ddmForm2.getDDMFormFieldsMap(true);

		if (ddmFormFieldsMap1.size() != ddmFormFieldsMap2.size()) {
			return false;
		}

		for (Map.Entry<String, DDMFormField> entry :
				ddmFormFieldsMap1.entrySet()) {

			DDMFormField ddmFormField2 = ddmFormFieldsMap2.get(entry.getKey());

			if (ddmFormField2 == null) {
				return false;
			}

			DDMFormField ddmFormField1 = entry.getValue();

			if (!Objects.equals(
					ddmFormField1.getPredefinedValue(),
					ddmFormField2.getPredefinedValue())) {

				return true;
			}
		}

		return false;
	}

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private Portal _portal;

}