/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = CTDisplayRendererRegistry.class)
public class CTDisplayRendererRegistryImpl
	implements CTDisplayRendererRegistry {

	@Override
	public <T extends BaseModel<T>> T fetchCTModel(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		long modelClassNameId, long modelClassPK) {

		ClassName className = _classNameLocalService.fetchByClassNameId(
			modelClassNameId);

		if (className == null) {
			return null;
		}

		CTService<?> ctService = _ctServiceServiceTrackerMap.getService(
			className.getValue());

		if (ctService == null) {
			return null;
		}

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId);
			SafeCloseable safeCloseable2 =
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctSQLMode)) {

			return (T)ctService.updateWithUnsafeFunction(
				ctPersistence -> ctPersistence.fetchByPrimaryKey(modelClassPK));
		}
	}

	@Override
	public <T extends BaseModel<T>> T fetchCTModel(
		long modelClassNameId, long modelClassPK) {

		return fetchCTModel(
			CTConstants.CT_COLLECTION_ID_PRODUCTION,
			CTSQLModeThreadLocal.CTSQLMode.DEFAULT, modelClassNameId,
			modelClassPK);
	}

	@Override
	public <T extends BaseModel<T>> Map<Serializable, T> fetchCTModelMap(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		long modelClassNameId, Set<Long> primaryKeys) {

		ClassName className = _classNameLocalService.fetchByClassNameId(
			modelClassNameId);

		if (className == null) {
			return null;
		}

		CTService<?> ctService = _ctServiceServiceTrackerMap.getService(
			className.getValue());

		if (ctService == null) {
			return null;
		}

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId);
			SafeCloseable safeCloseable2 =
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctSQLMode)) {

			return (Map<Serializable, T>)ctService.updateWithUnsafeFunction(
				ctPersistence -> ctPersistence.fetchByPrimaryKeys(
					(Set)primaryKeys));
		}
	}

	@Override
	public <T extends BaseModel<T>> String[] getAvailableLanguageIds(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode, T model,
		long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer = _getCTDisplayRenderer(
			modelClassNameId);

		if (ctDisplayRenderer == null) {
			return null;
		}

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId);
			SafeCloseable safeCloseable2 =
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctSQLMode)) {

			return ctDisplayRenderer.getAvailableLanguageIds(model);
		}
	}

	@Override
	public <T extends BaseModel<T>> int getChangeType(
		CTEntry ctEntry, T model) {

		if (model instanceof TrashedModel) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctEntry.getCtCollectionId())) {

				TrashedModel trashedModel = (TrashedModel)model;

				if (trashedModel.isInTrash()) {
					return CTConstants.CT_CHANGE_TYPE_DELETION;
				}
			}
		}
		else if (model instanceof WorkflowedModel) {
			WorkflowedModel workflowedModel = (WorkflowedModel)model;

			if (workflowedModel.getStatus() ==
					WorkflowConstants.STATUS_IN_TRASH) {

				return CTConstants.CT_CHANGE_TYPE_DELETION;
			}
		}

		return ctEntry.getChangeType();
	}

	@Override
	public long getCtCollectionId(CTCollection ctCollection, CTEntry ctEntry)
		throws PortalException {

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			if (ctEntry.getChangeType() ==
					CTConstants.CT_CHANGE_TYPE_DELETION) {

				return ctCollection.getCtCollectionId();
			}

			return _ctEntryLocalService.getCTRowCTCollectionId(ctEntry);
		}
		else if (ctEntry.getChangeType() ==
					CTConstants.CT_CHANGE_TYPE_DELETION) {

			return CTConstants.CT_COLLECTION_ID_PRODUCTION;
		}

		return ctCollection.getCtCollectionId();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BaseModel<?>> CTDisplayRenderer<T> getCTDisplayRenderer(
		long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer =
			(CTDisplayRenderer<T>)_getCTDisplayRenderer(modelClassNameId);

		if (ctDisplayRenderer == null) {
			ctDisplayRenderer = getDefaultRenderer();
		}

		return ctDisplayRenderer;
	}

	@Override
	public CTService<?> getCTService(CTModel<?> ctModel) {
		Class<?> modelClass = ctModel.getModelClass();

		return _ctServiceServiceTrackerMap.getService(modelClass.getName());
	}

	@Override
	public CTSQLModeThreadLocal.CTSQLMode getCTSQLMode(
		long ctCollectionId, CTEntry ctEntry) {

		if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return CTSQLModeThreadLocal.CTSQLMode.DEFAULT;
		}

		if (ctCollectionId != ctEntry.getCtCollectionId()) {
			ctEntry = _ctEntryLocalService.fetchCTEntry(
				ctCollectionId, ctEntry.getModelClassNameId(),
				ctEntry.getModelClassPK());

			if (ctEntry == null) {
				return CTSQLModeThreadLocal.CTSQLMode.DEFAULT;
			}
		}

		if (ctEntry.getChangeType() == CTConstants.CT_CHANGE_TYPE_DELETION) {
			return CTSQLModeThreadLocal.CTSQLMode.CT_ONLY;
		}

		return CTSQLModeThreadLocal.CTSQLMode.DEFAULT;
	}

	@Override
	public <T extends BaseModel<T>> String getDefaultLanguageId(
		T model, long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer = _getCTDisplayRenderer(
			modelClassNameId);

		if (ctDisplayRenderer == null) {
			return null;
		}

		return ctDisplayRenderer.getDefaultLanguageId(model);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BaseModel<?>> CTDisplayRenderer<T> getDefaultRenderer() {
		return (CTDisplayRenderer<T>)_defaultCTDisplayRenderer;
	}

	@Override
	public <T extends BaseModel<T>> String getEditURL(
		HttpServletRequest httpServletRequest, CTEntry ctEntry) {

		T model = fetchCTModel(
			ctEntry.getCtCollectionId(), CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
			ctEntry.getModelClassNameId(), ctEntry.getModelClassPK());

		if (model == null) {
			return null;
		}

		return getEditURL(
			ctEntry.getCtCollectionId(), CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
			httpServletRequest, model, ctEntry.getModelClassNameId());
	}

	@Override
	public <T extends BaseModel<T>> String getEditURL(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctsqlMode,
		HttpServletRequest httpServletRequest, T model, long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer = _getCTDisplayRenderer(
			modelClassNameId);

		if (ctDisplayRenderer == null) {
			return null;
		}

		try {
			if ((ctCollectionId != 0) &&
				!CTCollectionPermission.contains(
					PermissionThreadLocal.getPermissionChecker(),
					ctCollectionId, ActionKeys.UPDATE)) {

				return null;
			}
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId);
			SafeCloseable safeCloseable2 =
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctsqlMode)) {

			return ctDisplayRenderer.getEditURL(httpServletRequest, model);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	@Override
	public String getEntryDescription(
		HttpServletRequest httpServletRequest, CTEntry ctEntry) {

		String languageKey = "x-modified-a-x-x-ago";

		if (ctEntry.getChangeType() == CTConstants.CT_CHANGE_TYPE_ADDITION) {
			languageKey = "x-added-a-x-x-ago";
		}
		else if (ctEntry.getChangeType() ==
					CTConstants.CT_CHANGE_TYPE_DELETION) {

			languageKey = "x-deleted-a-x-x-ago";
		}

		Locale locale = _portal.getLocale(httpServletRequest);
		Date modifiedDate = ctEntry.getModifiedDate();

		return _language.format(
			httpServletRequest, languageKey,
			new Object[] {
				ctEntry.getUserName(),
				getTypeName(locale, ctEntry.getModelClassNameId()),
				_language.getTimeDescription(
					locale, System.currentTimeMillis() - modifiedDate.getTime(),
					true)
			},
			false);
	}

	@Override
	public <T extends BaseModel<T>> String getTitle(
		long ctCollectionId, CTEntry ctEntry, Locale locale) {

		CTSQLModeThreadLocal.CTSQLMode ctSQLMode = getCTSQLMode(
			ctCollectionId, ctEntry);

		T model = fetchCTModel(
			ctCollectionId, ctSQLMode, ctEntry.getModelClassNameId(),
			ctEntry.getModelClassPK());

		if (model == null) {
			return StringBundler.concat(
				getTypeName(locale, ctEntry.getModelClassNameId()),
				StringPool.SPACE, ctEntry.getModelClassPK());
		}

		return getTitle(
			ctCollectionId, ctSQLMode, locale, model,
			ctEntry.getModelClassNameId());
	}

	@Override
	public <T extends BaseModel<T>> String getTitle(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		Locale locale, T model, long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer = _getCTDisplayRenderer(
			modelClassNameId);

		String name = null;

		if (ctDisplayRenderer != null) {
			try (SafeCloseable safeCloseable1 =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId);
				SafeCloseable safeCloseable2 =
					CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
						ctSQLMode)) {

				name = ctDisplayRenderer.getTitle(locale, model);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}

				String typeName = ctDisplayRenderer.getTypeName(locale);

				if (Validator.isNotNull(typeName)) {
					return StringBundler.concat(
						typeName, StringPool.SPACE, model.getPrimaryKeyObj());
				}
			}
		}

		if (Validator.isNotNull(name)) {
			return name;
		}

		return StringBundler.concat(
			getTypeName(locale, modelClassNameId), StringPool.SPACE,
			model.getPrimaryKeyObj());
	}

	@Override
	public <T extends BaseModel<T>> String getTypeName(
		Locale locale, long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer = _getCTDisplayRenderer(
			modelClassNameId);

		String name = null;

		if (ctDisplayRenderer != null) {
			name = ctDisplayRenderer.getTypeName(locale);
		}

		if (Validator.isNull(name)) {
			ClassName className = _classNameLocalService.fetchClassName(
				modelClassNameId);

			if (className != null) {
				name = _resourceActions.getModelResource(
					locale, className.getClassName());

				if (name.startsWith(
						_resourceActions.getModelResourceNamePrefix())) {

					name = className.getClassName();
				}
			}
		}

		return name;
	}

	@Override
	public <T extends BaseModel<T>> boolean isHideable(
		T model, long modelClassNameId) {

		CTDisplayRenderer<T> ctDisplayRenderer = getCTDisplayRenderer(
			modelClassNameId);

		return ctDisplayRenderer.isHideable(model);
	}

	@Override
	public <T extends BaseModel<T>> boolean isWorkflowEnabled(
		CTEntry ctEntry, T model) {

		if (!(model instanceof WorkflowedModel)) {
			return false;
		}

		long groupId = 0;

		if (model instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)model;

			groupId = groupedModel.getGroupId();
		}

		return _workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
			ctEntry.getCompanyId(), groupId,
			_portal.getClassName(ctEntry.getModelClassNameId()));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_ctDisplayServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<CTDisplayRenderer<?>>)(Class<?>)CTDisplayRenderer.class,
				null,
				(serviceReference, emitter) -> {
					CTDisplayRenderer<?> ctDisplayRenderer =
						bundleContext.getService(serviceReference);

					Class<?> modelClass = ctDisplayRenderer.getModelClass();

					emitter.emit(modelClass.getName());

					bundleContext.ungetService(serviceReference);
				});

		_ctServiceServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, (Class<CTService<?>>)(Class<?>)CTService.class,
				null,
				(serviceReference, emitter) -> {
					CTService<?> ctService = bundleContext.getService(
						serviceReference);

					Class<?> modelClass = ctService.getModelClass();

					emitter.emit(modelClass.getName());

					bundleContext.ungetService(serviceReference);
				});

		_defaultCTDisplayRenderer = new CTModelDisplayRendererAdapter<>(this);
	}

	@Deactivate
	protected void deactivate() {
		_ctDisplayServiceTrackerMap.close();
		_ctServiceServiceTrackerMap.close();
	}

	private <T extends BaseModel<T>> CTDisplayRenderer<T> _getCTDisplayRenderer(
		long modelClassNameId) {

		ClassName className = _classNameLocalService.fetchByClassNameId(
			modelClassNameId);

		if (className == null) {
			return null;
		}

		return (CTDisplayRenderer<T>)_ctDisplayServiceTrackerMap.getService(
			className.getValue());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTDisplayRendererRegistryImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private ServiceTrackerMap<String, CTDisplayRenderer<?>>
		_ctDisplayServiceTrackerMap;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	private ServiceTrackerMap<String, CTService<?>> _ctServiceServiceTrackerMap;
	private CTDisplayRenderer<?> _defaultCTDisplayRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}