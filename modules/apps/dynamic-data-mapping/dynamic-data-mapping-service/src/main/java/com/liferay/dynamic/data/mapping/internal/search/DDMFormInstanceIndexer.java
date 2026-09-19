/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Summary;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

/**
 * @author Leonardo Barros
 */
public class DDMFormInstanceIndexer extends BaseIndexer<DDMFormInstance> {

	public static final String CLASS_NAME = DDMFormInstance.class.getName();

	public DDMFormInstanceIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	protected void doDelete(DDMFormInstance ddmFormInstance) throws Exception {
		deleteDocument(
			ddmFormInstance.getCompanyId(),
			ddmFormInstance.getFormInstanceId());
	}

	@Override
	protected Document doGetDocument(DDMFormInstance ddmFormInstance)
		throws Exception {

		return getBaseModelDocument(CLASS_NAME, ddmFormInstance);
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return createSummary(document, Field.TITLE, Field.DESCRIPTION);
	}

	@Override
	protected void doReindex(DDMFormInstance ddmFormInstance) throws Exception {
		indexWriterHelper.updateDocument(
			ddmFormInstance.getCompanyId(), getDocument(ddmFormInstance));

		_reindexRecords(ddmFormInstance);
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		DDMFormInstance ddmFormInstance =
			ddmFormInstanceLocalService.getFormInstance(classPK);

		doReindex(ddmFormInstance);
	}

	@Override
	protected IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return ddmFormInstanceLocalService.getIndexableActionableDynamicQuery();
	}

	protected DDMFormInstanceLocalService ddmFormInstanceLocalService;
	protected IndexWriterHelper indexWriterHelper;
	protected IndexerRegistry indexerRegistry;

	private void _reindexRecords(DDMFormInstance ddmFormInstance)
		throws Exception {

		Indexer<DDMFormInstanceRecord> indexer =
			indexerRegistry.nullSafeGetIndexer(DDMFormInstanceRecord.class);

		indexer.reindex(ddmFormInstance.getFormInstanceRecords());
	}

}