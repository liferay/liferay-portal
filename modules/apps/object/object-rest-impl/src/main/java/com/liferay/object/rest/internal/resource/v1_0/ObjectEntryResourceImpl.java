/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.exception.ObjectEntryValidationException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectRelationshipModel;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.TranslationResponse;
import com.liferay.object.rest.dto.v1_0.ValidationError;
import com.liferay.object.rest.dto.v1_0.ValidationRequest;
import com.liferay.object.rest.dto.v1_0.ValidationResponse;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.rest.odata.entity.v1_0.provider.EntityModelProvider;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.NestedFieldsContextResource;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.NestedFieldsContextUtil;
import com.liferay.translation.manager.Translation;
import com.liferay.translation.manager.TranslationManager;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.File;
import java.io.Serializable;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Javier Gamarra
 */
public class ObjectEntryResourceImpl
	extends BaseObjectEntryResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<ObjectEntry>,
			   NestedFieldsContextResource {

	public ObjectEntryResourceImpl(
		DTOConverterRegistry dtoConverterRegistry,
		EntityModelProvider entityModelProvider,
		ObjectDefinition objectDefinition,
		Map<Long, ObjectDefinition> objectDefinitions,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		TranslationManager translationManager,
		UserLocalService userLocalService) {

		_dtoConverterRegistry = dtoConverterRegistry;
		_entityModelProvider = entityModelProvider;
		_objectDefinition = objectDefinition;
		_objectDefinitions = objectDefinitions;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectEntryService = objectEntryService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_translationManager = translationManager;
		_userLocalService = userLocalService;
	}

	@Override
	public void create(
			Collection<ObjectEntry> objectEntries,
			Map<String, Serializable> parameters)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			_create(
				objectEntries, parameters, GroupUtil.getScopeKey(parameters));
		}
		else {
			_create(objectEntries, parameters, null);
		}
	}

	@Override
	public NestedFieldsContext customizeNestedFieldsContext(
		NestedFieldsContext nestedFieldsContext) {

		if (nestedFieldsContext == null) {
			return null;
		}

		List<String> nestedFields = new ArrayList<>(
			nestedFieldsContext.getNestedFields());

		if (!nestedFields.contains("rootModelHierarchy")) {
			return nestedFieldsContext;
		}

		int treeHeight = 1;

		try {
			ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
				new ObjectDefinitionTreeFactory(
					_objectDefinitionLocalService,
					_objectRelationshipLocalService);

			Tree tree = objectDefinitionTreeFactory.create(
				_objectDefinition.getObjectDefinitionId());

			treeHeight += tree.getHeight(tree.getRootNode());

			Iterator<Node> iterator = tree.iterator();

			while (iterator.hasNext()) {
				Node node = iterator.next();

				List<Node> childNodes = node.getChildNodes();

				if (ListUtil.isEmpty(childNodes)) {
					continue;
				}

				for (int i = childNodes.size() - 1; i >= 0; i--) {
					Node childNode = childNodes.get(i);

					Edge edge = childNode.getEdge();

					if (edge == null) {
						continue;
					}

					ObjectRelationship objectRelationship =
						_objectRelationshipLocalService.getObjectRelationship(
							edge.getObjectRelationshipId());

					nestedFields.add(objectRelationship.getName());
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			return nestedFieldsContext;
		}

		ListUtil.distinct(nestedFields);

		return new NestedFieldsContext(
			NestedFieldsContextUtil.limitDepth(treeHeight),
			nestedFieldsContext.getMessage(), nestedFields,
			nestedFieldsContext.getPathParameters(),
			nestedFieldsContext.getQueryParameters(),
			nestedFieldsContext.getResourceVersion());
	}

	@Override
	public void delete(
			Collection<ObjectEntry> objectEntries,
			Map<String, Serializable> parameters)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			UnsafeFunction<ObjectEntry, ObjectEntry, Exception> unsafeFunction =
				objectEntry -> {
					if (objectEntry.getId() != null) {
						try {
							deleteObjectEntry(objectEntry.getId());

							return objectEntry;
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}

							if (objectEntry.getExternalReferenceCode() !=
									null) {

								deleteScopeScopeKeyByExternalReferenceCode(
									GroupUtil.getScopeKey(parameters),
									objectEntry.getExternalReferenceCode());

								return objectEntry;
							}
						}
					}
					else if (objectEntry.getExternalReferenceCode() != null) {
						deleteScopeScopeKeyByExternalReferenceCode(
							GroupUtil.getScopeKey(parameters),
							objectEntry.getExternalReferenceCode());

						return objectEntry;
					}

					throw new UnsupportedOperationException(
						"Unable to delete by external reference code or ID");
				};

			_applyUnsafeFunction(objectEntries, unsafeFunction);
		}
		else {
			super.delete(objectEntries, parameters);
		}
	}

	@Override
	public void deleteByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		objectEntryManager.deleteObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public void deleteByExternalReferenceCodeByVersion(
			String externalReferenceCode, Integer version)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntryByVersion(
			externalReferenceCode, _objectDefinition, null, version);
	}

	@Override
	public void deleteObjectEntry(Long objectEntryId) throws Exception {
		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition, objectEntryId);
	}

	@Override
	public Response deleteObjectEntryBatch(String callbackURL, Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.deleteObjectEntryBatch(callbackURL, object);
	}

	@Override
	public void deleteScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		objectEntryManager.deleteObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public void deleteScopeScopeKeyByExternalReferenceCodeByVersion(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntryByVersion(
			externalReferenceCode, _objectDefinition, scopeKey, version);
	}

	@Override
	public ObjectEntry getApprovedByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public Page<ObjectEntry> getApprovedPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, null, aggregation,
			_getDTOConverterContext(null), _getFilterString(), pagination,
			search, sorts);
	}

	@Override
	public ObjectEntry getByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public ObjectEntry getByExternalReferenceCodeByVersion(
			String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public Page<ObjectEntry> getByExternalReferenceCodeVersionsPage(
			String externalReferenceCode, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getVersionedObjectEntries(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, pagination, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		if (_objectDefinition != null) {
			return _entityModelProvider.getEntityModel(_objectDefinition);
		}

		return _entityModelProvider.getEntityModel(
			_objectDefinitions.get(contextCompany.getCompanyId()));
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getDescription(Locale locale) {
				if (!_objectDefinition.isRootNode()) {
					return null;
				}

				try {
					List<String> childLabels = new ArrayList<>();

					ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
						new ObjectDefinitionTreeFactory(
							_objectDefinitionLocalService,
							_objectRelationshipLocalService);

					Tree tree = objectDefinitionTreeFactory.create(
						_objectDefinition.getObjectDefinitionId());

					Iterator<Node> iterator = tree.iterator();

					while (iterator.hasNext()) {
						Node node = iterator.next();

						if (node.isRoot()) {
							continue;
						}

						childLabels.add(
							LanguageUtil.get(
								locale,
								_getLabelLanguageKey(
									_objectDefinitionLocalService.
										getObjectDefinition(
											node.getPrimaryKey()))));
					}

					return StringUtil.merge(
						childLabels, StringPool.COMMA_AND_SPACE);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return null;
				}
			}

			@Override
			public String getKey() {
				return _objectDefinition.getExternalReferenceCode();
			}

			@Override
			public String getLabelLanguageKey() {
				return _getLabelLanguageKey(_objectDefinition);
			}

			@Override
			public Class<?> getModelClass() {
				return null;
			}

			@Override
			public String getModelClassName() {
				return _objectDefinition.getClassName();
			}

			@Override
			public List<String> getNestedFields() {
				return ListUtil.concat(
					List.of("rootModelHierarchy"),
					transform(
						_objectRelationshipLocalService.
							getObjectRelationshipsByObjectDefinitionId2(
								_objectDefinition.getObjectDefinitionId()),
						ObjectRelationshipModel::getName));
			}

			@Override
			public String getPortletId() {
				return _objectDefinition.getPortletId();
			}

			@Override
			public Map<String, String[]> getReferences() {
				Map<String, String[]> references = new HashMap<>();

				for (ObjectField objectField :
						_objectFieldLocalService.getObjectFieldsByBusinessType(
							_objectDefinition.getObjectDefinitionId(),
							ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

					references.put(
						objectField.getName(), new String[] {"DLReferences"});
				}

				return references;
			}

			@Override
			public Scope getScope() {
				if (StringUtil.equalsIgnoreCase(
						_objectDefinition.getScope(), "company")) {

					return Scope.COMPANY;
				}

				if (StringUtil.equalsIgnoreCase(
						_objectDefinition.getScope(), "depot")) {

					return Scope.DEPOT;
				}

				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_OBJECTS;
			}

			@Override
			public String getTag(Locale locale) {
				if (!_objectDefinition.isRootNode()) {
					return null;
				}

				return LanguageUtil.get(locale, "root-object");
			}

			@Override
			public boolean isHidden() {
				if (!_objectDefinition.isAllowStandaloneObjectEntry() &&
					_objectDefinition.isRootDescendantNode()) {

					return true;
				}

				return false;
			}

			@Override
			public boolean isMissingPortletSupported() {
				return true;
			}

		};
	}

	@Override
	public Page<ObjectEntry> getObjectEntriesPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, null, aggregation,
			_getDTOConverterContext(null), _getFilterString(), pagination,
			search, sorts);
	}

	@Override
	public ObjectEntry getObjectEntry(Long objectEntryId) throws Exception {
		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntry(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId);
	}

	@Override
	public Response getObjectEntryTranslation(
			Long objectEntryId, String sourceLanguageId,
			String targetLanguageIds, String version)
		throws Exception {

		String className = _objectDefinition.getClassName();

		String xliffMimeType = null;

		if (version != null) {
			xliffMimeType = _xliffMimeTypes.get(version);
		}

		if (xliffMimeType == null) {
			xliffMimeType = "application/xliff+xml";
		}

		File xliffZipFile = _translationManager.getXLIFFZipFile(
			className, new long[] {objectEntryId}, xliffMimeType,
			contextAcceptLanguage.getPreferredLocale(), sourceLanguageId,
			StringUtil.split(targetLanguageIds, CharPool.COMMA));

		return Response.ok(
			_getStreamingOutput(xliffZipFile)
		).header(
			"content-disposition",
			"attachment; filename=\"" + xliffZipFile.getName() + "\""
		).build();
	}

	@Override
	public Response getObjectEntryTranslationLanguage(
			Long objectEntryId, String languageId, String targetLanguageId)
		throws Exception {

		File xliffFile = _translationManager.getXLIFFFile(
			_objectDefinition.getClassName(), objectEntryId,
			_getXLIFFMimeType(
				contextHttpServletRequest.getHeader(HttpHeaders.ACCEPT)),
			contextAcceptLanguage.getPreferredLocale(), languageId,
			targetLanguageId);

		return Response.ok(
			_getStreamingOutput(xliffFile)
		).header(
			"content-disposition",
			"attachment; filename=\"" + xliffFile.getName() + "\""
		).build();
	}

	@Override
	public String getResourceName() {
		return _objectDefinition.getShortName();
	}

	@Override
	public ObjectEntry getScopeScopeKeyApprovedByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public Page<ObjectEntry> getScopeScopeKeyApprovedPage(
			String scopeKey, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, scopeKey,
			aggregation, _getDTOConverterContext(null), _getFilterString(),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry getScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public ObjectEntry getScopeScopeKeyByExternalReferenceCodeByVersion(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public Response getScopeScopeKeyByExternalReferenceCodeTranslation(
			String scopeKey, String externalReferenceCode,
			String sourceLanguageId, String targetLanguageIds, String version)
		throws Exception {

		ObjectEntry objectEntry = getScopeScopeKeyByExternalReferenceCode(
			scopeKey, externalReferenceCode);

		return getObjectEntryTranslation(
			objectEntry.getId(), sourceLanguageId, targetLanguageIds, version);
	}

	@Override
	public Response getScopeScopeKeyByExternalReferenceCodeTranslationLanguage(
			String scopeKey, String externalReferenceCode, String languageId,
			String targetLanguageId)
		throws Exception {

		ObjectEntry objectEntry = getScopeScopeKeyByExternalReferenceCode(
			scopeKey, externalReferenceCode);

		return getObjectEntryTranslationLanguage(
			objectEntry.getId(), languageId, targetLanguageId);
	}

	@Override
	public Page<ObjectEntry>
			getScopeScopeKeyByExternalReferenceCodeVersionsPage(
				String scopeKey, String externalReferenceCode,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getVersionedObjectEntries(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, pagination, sorts);
	}

	@Override
	public Page<ObjectEntry> getScopeScopeKeyPage(
			String scopeKey, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, scopeKey,
			aggregation, _getDTOConverterContext(null), _getFilterString(),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry patchByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.partialUpdateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, null);
	}

	@Override
	public ObjectEntry patchObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.partialUpdateObjectEntry(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, objectEntry);
	}

	@Override
	public ObjectEntry patchScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.partialUpdateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, scopeKey);
	}

	@Override
	public ObjectEntry postByExternalReferenceCodeByVersionCopy(
			String externalReferenceCode, Integer version)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public ObjectEntry postByExternalReferenceCodeByVersionExpire(
			String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public ObjectEntry postByExternalReferenceCodeExpire(
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, StringPool.BLANK);
	}

	@Override
	public void postByExternalReferenceCodeSubscribe(
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.subscribeObjectEntry(
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public void postByExternalReferenceCodeUnsubscribe(
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.unsubscribeObjectEntry(
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public Response postObjectEntriesPageExportBatch(
			String search, Filter filter, Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.postObjectEntriesPageExportBatch(
			search, filter, sorts, callbackURL, contentType, fieldNames);
	}

	@Override
	public ObjectEntry postObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.addObjectEntry(
			_getDTOConverterContext(null), _objectDefinition, objectEntry,
			null);
	}

	@Override
	public Response postObjectEntryBatch(String callbackURL, Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.postObjectEntryBatch(callbackURL, object);
	}

	@Override
	public ObjectEntry postObjectEntryByObjectEntryFolderCopy(
			Long objectEntryId, Long objectEntryFolderId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntry(
			_getDTOConverterContext(objectEntryId), objectEntryId,
			objectEntryFolderId, false);
	}

	@Override
	public ObjectEntry postObjectEntryByObjectEntryFolderCopyReplace(
			Long objectEntryId, Long objectEntryFolderId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntry(
			_getDTOConverterContext(objectEntryId), objectEntryId,
			objectEntryFolderId, true);
	}

	@Override
	public ObjectEntry postObjectEntryByObjectEntryFolderMove(
			Long objectEntryId, Long objectEntryFolderId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.moveObjectEntry(
			_getDTOConverterContext(objectEntryId), objectEntryId,
			objectEntryFolderId, false);
	}

	@Override
	public ObjectEntry postObjectEntryByObjectEntryFolderMoveReplace(
			Long objectEntryId, Long objectEntryFolderId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.moveObjectEntry(
			_getDTOConverterContext(objectEntryId), objectEntryId,
			objectEntryFolderId, true);
	}

	@Override
	public TranslationResponse postObjectEntryTranslation(
			Long objectEntryId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		List<Map<String, String>> failureMessages = new LinkedList<>();
		List<String> successMessages = new ArrayList<>();

		_initThemeDisplay(serviceBuilderObjectEntry);

		_translationManager.processXLIFFTranslation(
			serviceBuilderObjectEntry.getGroupId(),
			_objectDefinition.getClassName(), objectEntryId,
			new Translation(
				() -> MimeTypesUtil.getContentType(binaryFile.getFileName()),
				binaryFile.getFileName(), binaryFile::getInputStream),
			successMessages, failureMessages,
			contextAcceptLanguage.getPreferredLocale(),
			ServiceContextFactory.getInstance(contextHttpServletRequest));

		return _toTranslationResponse(failureMessages, successMessages);
	}

	@Override
	public ObjectEntry postScopeScopeKey(
			String scopeKey, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.addObjectEntry(
			_getDTOConverterContext(null), _objectDefinition, objectEntry,
			scopeKey);
	}

	@Override
	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeByVersionCopy(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeByVersionExpire(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeExpire(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey);
	}

	@Override
	public void postScopeScopeKeyByExternalReferenceCodeSubscribe(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.subscribeObjectEntry(
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public TranslationResponse
			postScopeScopeKeyByExternalReferenceCodeTranslation(
				String scopeKey, String externalReferenceCode,
				MultipartBody multipartBody)
		throws Exception {

		ObjectEntry objectEntry = getScopeScopeKeyByExternalReferenceCode(
			scopeKey, externalReferenceCode);

		return postObjectEntryTranslation(objectEntry.getId(), multipartBody);
	}

	@Override
	public void postScopeScopeKeyByExternalReferenceCodeUnsubscribe(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.unsubscribeObjectEntry(
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public ValidationResponse postScopeScopeKeyValidate(
			String scopeKey, ValidationRequest validationRequest)
		throws Exception {

		return _validateObjectEntry(scopeKey, validationRequest);
	}

	@Override
	public ValidationResponse postValidate(ValidationRequest validationRequest)
		throws Exception {

		return _validateObjectEntry(null, validationRequest);
	}

	@Override
	public ObjectEntry putByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.updateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, null);
	}

	@Override
	public ObjectEntry putByExternalReferenceCodeByVersionRestore(
			String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public void putByExternalReferenceCodeObjectActionObjectActionName(
			String externalReferenceCode, String objectActionName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.executeObjectAction(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, objectActionName, _objectDefinition, null);
	}

	@Override
	public ObjectEntry putByExternalReferenceCodeRestore(
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null);
	}

	@Override
	public ObjectEntry putObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.updateObjectEntry(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, objectEntry);
	}

	@Override
	public Response putObjectEntryBatch(String callbackURL, Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.putObjectEntryBatch(callbackURL, object);
	}

	@Override
	public void putObjectEntryObjectActionObjectActionName(
			Long objectEntryId, String objectActionName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.executeObjectAction(
			_getDTOConverterContext(objectEntryId), objectActionName,
			_objectDefinition, objectEntryId);
	}

	@Override
	public ObjectEntry putScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		return objectEntryManager.updateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, scopeKey);
	}

	@Override
	public ObjectEntry putScopeScopeKeyByExternalReferenceCodeByVersionRestore(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public void
			putScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName(
				String scopeKey, String externalReferenceCode,
				String objectActionName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.executeObjectAction(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, objectActionName, _objectDefinition,
			scopeKey);
	}

	@Override
	public ObjectEntry putScopeScopeKeyByExternalReferenceCodeRestore(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey);
	}

	@Override
	public Page<ObjectEntry> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			return getScopeScopeKeyPage(
				GroupUtil.getScopeKey(parameters), search, null, filter,
				pagination, sorts);
		}

		return getObjectEntriesPage(search, null, filter, pagination, sorts);
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	@Override
	public void update(
			Collection<ObjectEntry> objectEntries,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<ObjectEntry, ObjectEntry, Exception> unsafeFunction =
			null;

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		String scopeKey =
			objectScopeProvider.isGroupAware() ?
				GroupUtil.getScopeKey(parameters) : null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			unsafeFunction = objectEntry -> {
				if ((objectEntry.getId() != null) &&
					StringUtil.equals(
						_objectDefinition.getStorageType(),
						ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT)) {

					return patchObjectEntry(objectEntry.getId(), objectEntry);
				}

				if (scopeKey != null) {
					return patchScopeScopeKeyByExternalReferenceCode(
						scopeKey, objectEntry.getExternalReferenceCode(),
						objectEntry);
				}

				return patchByExternalReferenceCode(
					objectEntry.getExternalReferenceCode(), objectEntry);
			};
		}
		else if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			unsafeFunction = objectEntry -> {
				if ((objectEntry.getId() != null) &&
					StringUtil.equals(
						_objectDefinition.getStorageType(),
						ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT)) {

					return putObjectEntry(objectEntry.getId(), objectEntry);
				}

				if (scopeKey != null) {
					return putScopeScopeKeyByExternalReferenceCode(
						scopeKey, objectEntry.getExternalReferenceCode(),
						objectEntry);
				}

				return putByExternalReferenceCode(
					objectEntry.getExternalReferenceCode(), objectEntry);
			};
		}

		if (unsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for object entry");
		}

		_applyUnsafeFunction(objectEntries, unsafeFunction);
	}

	@Override
	protected String getApplicationPath() {
		String restContextPath = null;

		if (_objectDefinition != null) {
			restContextPath = _objectDefinition.getRESTContextPath();
		}
		else {
			ObjectDefinition objectDefinition = _objectDefinitions.get(
				contextCompany.getCompanyId());

			restContextPath = objectDefinition.getRESTContextPath();
		}

		return StringUtil.removeFirst(restContextPath, "/");
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryLocalService.getObjectEntry(GetterUtil.getLong(id));

		return serviceBuilderObjectEntry.getGroupId();
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id)
		throws Exception {

		return _objectDefinition.getClassName();
	}

	@Override
	protected void preparePatch(
		ObjectEntry objectEntry, ObjectEntry existingObjectEntry) {

		if (objectEntry.getStatus() != null) {
			existingObjectEntry.setStatus(objectEntry::getStatus);
		}
	}

	private void _applyUnsafeFunction(
			Collection<ObjectEntry> objectEntries,
			UnsafeFunction<ObjectEntry, ObjectEntry, Exception> unsafeFunction)
		throws Exception {

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(objectEntries, unsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				objectEntries, unsafeFunction::apply);
		}
		else {
			for (ObjectEntry objectEntry : objectEntries) {
				unsafeFunction.apply(objectEntry);
			}
		}
	}

	private void _create(
			Collection<ObjectEntry> objectEntries,
			Map<String, Serializable> parameters, String scopeKey)
		throws Exception {

		UnsafeFunction<ObjectEntry, ObjectEntry, Exception> unsafeFunction =
			null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			if (scopeKey != null) {
				unsafeFunction = objectEntry -> postScopeScopeKey(
					scopeKey, objectEntry);
			}
			else {
				unsafeFunction = objectEntry -> postObjectEntry(objectEntry);
			}
		}
		else if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				unsafeFunction = objectEntry -> {
					try {
						ObjectEntry getObjectEntry = null;

						if (scopeKey != null) {
							getObjectEntry =
								getScopeScopeKeyByExternalReferenceCode(
									scopeKey,
									objectEntry.getExternalReferenceCode());
						}
						else {
							getObjectEntry = getByExternalReferenceCode(
								objectEntry.getExternalReferenceCode());
						}

						if (getObjectEntry.getId() != null) {
							return patchObjectEntry(
								getObjectEntry.getId(), objectEntry);
						}

						if (scopeKey != null) {
							return patchScopeScopeKeyByExternalReferenceCode(
								scopeKey,
								objectEntry.getExternalReferenceCode(),
								objectEntry);
						}

						return patchByExternalReferenceCode(
							objectEntry.getExternalReferenceCode(),
							objectEntry);
					}
					catch (NoSuchModelException noSuchModelException) {
						if (_log.isDebugEnabled()) {
							_log.debug(noSuchModelException);
						}

						if (scopeKey != null) {
							return postScopeScopeKey(scopeKey, objectEntry);
						}

						return postObjectEntry(objectEntry);
					}
				};
			}
			else if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				if (scopeKey != null) {
					unsafeFunction =
						objectEntry -> putScopeScopeKeyByExternalReferenceCode(
							scopeKey, objectEntry.getExternalReferenceCode(),
							objectEntry);
				}
				else {
					unsafeFunction = objectEntry -> putByExternalReferenceCode(
						objectEntry.getExternalReferenceCode(), objectEntry);
				}
			}
		}

		if (unsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for object entry");
		}

		_applyUnsafeFunction(objectEntries, unsafeFunction);
	}

	private void _deleteTempFile(File file) {
		if (file == null) {
			return;
		}

		if (file.exists()) {
			file.delete();
		}

		File parentFile = file.getParentFile();

		if ((parentFile != null) && parentFile.exists() &&
			ArrayUtil.isEmpty(parentFile.list())) {

			parentFile.delete();
		}
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
		Long objectEntryId) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			_dtoConverterRegistry, contextHttpServletRequest, objectEntryId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private String _getFilterString() {
		if (contextHttpServletRequest != null) {
			return _getFilterString(
				ParamUtil.getString(
					contextHttpServletRequest,
					"assigneeUserExternalReferenceCode"),
				ParamUtil.getString(contextHttpServletRequest, "filter"));
		}

		if (contextUriInfo == null) {
			return null;
		}

		MultivaluedMap<String, String> queryParameters =
			contextUriInfo.getQueryParameters();

		return _getFilterString(
			queryParameters.getFirst("assigneeUserExternalReferenceCode"),
			queryParameters.getFirst("filter"));
	}

	private String _getFilterString(
		String assigneeUserExternalReferenceCode, String filterString) {

		if (Validator.isNull(assigneeUserExternalReferenceCode)) {
			return filterString;
		}

		ObjectField objectField =
			_objectFieldLocalService.fetchObjectFieldByBusinessType(
				_objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE, null);

		if (objectField == null) {
			return filterString;
		}

		long userId = 0;

		User user = _userLocalService.fetchUserByExternalReferenceCode(
			assigneeUserExternalReferenceCode, contextCompany.getCompanyId());

		if (user != null) {
			userId = user.getUserId();
		}

		String assigneeFilterString = StringBundler.concat(
			objectField.getName(), " eq ", userId);

		if (Validator.isNull(filterString)) {
			return assigneeFilterString;
		}

		return StringBundler.concat(
			"(", filterString, ") and ", assigneeFilterString);
	}

	private String _getLabelLanguageKey(ObjectDefinition objectDefinition) {
		String modelResourceNamePrefix =
			ResourceActionsUtil.getModelResourceNamePrefix();

		return modelResourceNamePrefix.concat(
			objectDefinition.getResourceName());
	}

	private StreamingOutput _getStreamingOutput(File file) {
		return streamingOutput -> {
			try {
				Files.copy(file.toPath(), streamingOutput);
			}
			finally {
				_deleteTempFile(file);
			}
		};
	}

	private String _getXLIFFMimeType(String accept) {
		if (Validator.isBlank(accept)) {
			return null;
		}

		for (Map.Entry<String, String> entry : _xliffMimeTypes.entrySet()) {
			if (accept.contains(entry.getValue())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private void _initThemeDisplay(
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setCompany(contextCompany);
		themeDisplay.setScopeGroupId(serviceBuilderObjectEntry.getGroupId());
		themeDisplay.setUser(contextUser);
	}

	private TranslationResponse _toTranslationResponse(
		List<Map<String, String>> failureMessages,
		List<String> successMessages) {

		TranslationResponse translationResponse = new TranslationResponse();

		translationResponse.setFailureMessagesJSON(
			() -> transformToArray(
				failureMessages,
				failureMessage -> {
					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						failureMessage);

					return jsonObject.toString();
				},
				String.class));
		translationResponse.setSuccessMessages(
			() -> transformToArray(
				successMessages, successMessage -> successMessage,
				String.class));

		return translationResponse;
	}

	private ValidationResponse _validateObjectEntry(
			String scopeKey, ValidationRequest validationRequest)
		throws Exception {

		if (validationRequest.getValues() == null) {
			throw new BadRequestException("No values found in body");
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getStorageType()));

		try {
			defaultObjectEntryManager.validateObjectEntry(
				_getDTOConverterContext(null), _objectDefinition,
				validationRequest.getValues(),
				ListUtil.fromArray(
					validationRequest.
						getObjectValidationRuleExternalReferenceCodes()),
				scopeKey);
		}
		catch (ObjectEntryValidationException objectEntryValidationException) {
			return new ValidationResponse() {
				{
					setValidationErrors(
						() -> transformToArray(
							objectEntryValidationException.
								getValidationErrors(),
							validationError -> new ValidationError() {
								{
									setErrorMessage(
										validationError::getErrorMessage);
									setObjectFieldName(
										validationError::getObjectFieldName);
									setObjectValidationRuleExternalReferenceCode(
										validationError::
											getObjectValidationRuleExternalReferenceCode);
								}
							},
							ValidationError.class));
				}
			};
		}

		return new ValidationResponse();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryResourceImpl.class);

	private static final Map<String, String> _xliffMimeTypes = Map.of(
		"1.2", "application/x-xliff+xml", "2.0", "application/xliff+xml");

	private final DTOConverterRegistry _dtoConverterRegistry;
	private final EntityModelProvider _entityModelProvider;

	@Context
	private ObjectDefinition _objectDefinition;

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final Map<Long, ObjectDefinition> _objectDefinitions;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectEntryService _objectEntryService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final TranslationManager _translationManager;
	private final UserLocalService _userLocalService;

}