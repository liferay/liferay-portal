/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.display;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Pei-Jung Lan
 */
public interface CTDisplayRendererRegistry {

	public <T extends BaseModel<T>> T fetchCTModel(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		long modelClassNameId, long modelClassPK);

	public <T extends BaseModel<T>> T fetchCTModel(
		long modelClassNameId, long modelClassPK);

	public <T extends BaseModel<T>> Map<Serializable, T> fetchCTModelMap(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		long modelClassNameId, Set<Long> primaryKeys);

	public <T extends BaseModel<T>> String[] getAvailableLanguageIds(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode, T model,
		long modelClassNameId);

	public <T extends BaseModel<T>> int getChangeType(CTEntry ctEntry, T model);

	public long getCtCollectionId(CTCollection ctCollection, CTEntry ctEntry)
		throws PortalException;

	public <T extends BaseModel<?>> CTDisplayRenderer<T> getCTDisplayRenderer(
		long modelClassNameId);

	public CTService<?> getCTService(CTModel<?> ctModel);

	public CTSQLModeThreadLocal.CTSQLMode getCTSQLMode(
		long ctCollectionId, CTEntry ctEntry);

	public <T extends BaseModel<T>> String getDefaultLanguageId(
		T model, long modelClassNameId);

	public <T extends BaseModel<?>> CTDisplayRenderer<T> getDefaultRenderer();

	public <T extends BaseModel<T>> String getEditURL(
		HttpServletRequest httpServletRequest, CTEntry ctEntry);

	public <T extends BaseModel<T>> String getEditURL(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctsqlMode,
		HttpServletRequest httpServletRequest, T model, long modelClassNameId);

	public String getEntryDescription(
		HttpServletRequest httpServletRequest, CTEntry ctEntry);

	public <T extends BaseModel<T>> String getTitle(
		long ctCollectionId, CTEntry ctEntry, Locale locale);

	public <T extends BaseModel<T>> String getTitle(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		Locale locale, T model, long modelClassNameId);

	public <T extends BaseModel<T>> String getTypeName(
		Locale locale, long modelClassNameId);

	public <T extends BaseModel<T>> boolean isHideable(
		T model, long modelClassNameId);

	public <T extends BaseModel<T>> boolean isWorkflowEnabled(
		CTEntry ctEntry, T model);

}