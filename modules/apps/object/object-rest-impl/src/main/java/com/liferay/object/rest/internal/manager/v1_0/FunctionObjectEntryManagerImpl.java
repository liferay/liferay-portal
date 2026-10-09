/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.internal.configuration.FunctionObjectEntryManagerConfiguration;
import com.liferay.object.rest.manager.exception.ObjectEntryManagerHttpException;
import com.liferay.object.rest.manager.v1_0.BaseObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	configurationPid = "com.liferay.object.rest.internal.configuration.FunctionObjectEntryManagerConfiguration",
	factory = "com.liferay.object.rest.internal.manager.v1_0.FunctionObjectEntryManagerImpl",
	service = ObjectEntryManager.class
)
public class FunctionObjectEntryManagerImpl
	extends BaseObjectEntryManager
	implements CompanyScoped, ObjectEntryManager {

	@Override
	public ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		return _toObjectEntry(
			_launch(
				Http.Method.POST,
				_toJSONObject(
					dtoConverterContext, scopeKey
				).put(
					"objectEntry", _toJSONObject(objectEntry)
				),
				StringBundler.concat(
					_functionObjectEntryManagerConfiguration.resourcePath(),
					StringPool.SLASH,
					HttpComponentsUtil.encodePath(
						objectDefinition.getExternalReferenceCode())),
				dtoConverterContext.getUserId()),
			dtoConverterContext, objectDefinition, scopeKey);
	}

	@Override
	public void deleteObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.DELETE, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		String resourcePath = StringBundler.concat(
			_functionObjectEntryManagerConfiguration.resourcePath(),
			StringPool.SLASH,
			HttpComponentsUtil.encodePath(
				objectDefinition.getExternalReferenceCode()),
			StringPool.SLASH, externalReferenceCode);

		_launch(
			Http.Method.DELETE, null,
			_appendBaseParameters(dtoConverterContext, resourcePath, scopeKey),
			dtoConverterContext.getUserId());
	}

	@Override
	public long getAllowedCompanyId() {
		return _companyId;
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.VIEW, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		String resourcePath = StringBundler.concat(
			_functionObjectEntryManagerConfiguration.resourcePath(),
			StringPool.SLASH,
			HttpComponentsUtil.encodePath(
				objectDefinition.getExternalReferenceCode()));

		resourcePath = _appendBaseParameters(
			dtoConverterContext, resourcePath, scopeKey);

		resourcePath = _appendCollectionParameters(
			filterString, pagination, resourcePath, search, sorts);

		return _toObjectEntries(
			_launch(
				Http.Method.GET, null, resourcePath,
				dtoConverterContext.getUserId()),
			dtoConverterContext, objectDefinition, pagination, scopeKey);
	}

	@Override
	public ObjectEntry getObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.VIEW, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		String resourcePath = StringBundler.concat(
			_functionObjectEntryManagerConfiguration.resourcePath(),
			StringPool.SLASH,
			HttpComponentsUtil.encodePath(
				objectDefinition.getExternalReferenceCode()),
			StringPool.SLASH, externalReferenceCode);

		return _toObjectEntry(
			_launch(
				Http.Method.GET, null,
				_appendBaseParameters(
					dtoConverterContext, resourcePath, scopeKey),
				dtoConverterContext.getUserId()),
			dtoConverterContext, objectDefinition, scopeKey);
	}

	@Override
	public String getStorageLabel(Locale locale) {
		return _storageLabel;
	}

	@Override
	public String getStorageType() {
		return _storageType;
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.UPDATE, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		return _toObjectEntry(
			_launch(
				Http.Method.PUT,
				_toJSONObject(
					dtoConverterContext, scopeKey
				).put(
					"objectEntry", _toJSONObject(objectEntry)
				),
				StringBundler.concat(
					_functionObjectEntryManagerConfiguration.resourcePath(),
					StringPool.SLASH,
					HttpComponentsUtil.encodePath(
						objectDefinition.getExternalReferenceCode()),
					StringPool.SLASH, externalReferenceCode),
				dtoConverterContext.getUserId()),
			dtoConverterContext, objectDefinition, scopeKey);
	}

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		_companyId = ConfigurationFactoryUtil.getCompanyId(
			_companyLocalService, properties);
		_functionObjectEntryManagerConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionObjectEntryManagerConfiguration.class, properties);
		_storageLabel = GetterUtil.getString(properties.get("name"));
		_storageType = GetterUtil.getString(properties.get("storage.type"));
	}

	private String _appendBaseParameters(
		DTOConverterContext dtoConverterContext, String resourcePath,
		String scopeKey) {

		resourcePath = HttpComponentsUtil.addParameter(
			resourcePath, "companyId", _companyId);
		resourcePath = HttpComponentsUtil.addParameter(
			resourcePath, "languageId",
			LocaleUtil.toLanguageId(dtoConverterContext.getLocale()));
		resourcePath = HttpComponentsUtil.addParameter(
			resourcePath, "scopeKey", scopeKey);
		resourcePath = HttpComponentsUtil.addParameter(
			resourcePath, "userId", dtoConverterContext.getUserId());

		return resourcePath;
	}

	private String _appendCollectionParameters(
		String filterString, Pagination pagination, String resourcePath,
		String search, Sort[] sorts) {

		if (Validator.isNotNull(filterString)) {
			resourcePath = HttpComponentsUtil.addParameter(
				resourcePath, "filter", filterString);
		}

		if (pagination != null) {
			resourcePath = HttpComponentsUtil.addParameter(
				resourcePath, "page", pagination.getPage());
			resourcePath = HttpComponentsUtil.addParameter(
				resourcePath, "pageSize", pagination.getPageSize());
		}

		if (search != null) {
			resourcePath = HttpComponentsUtil.addParameter(
				resourcePath, "search", search);
		}

		if (ArrayUtil.isNotEmpty(sorts)) {
			StringBundler sb = new StringBundler(sorts.length * 3);

			for (int i = 0; i < sorts.length; i++) {
				Sort sort = sorts[i];

				sb.append(sort.getFieldName());

				sb.append(StringPool.COLON);

				if (sort.isReverse()) {
					sb.append("desc");
				}
				else {
					sb.append("asc");
				}

				if (i == (sorts.length - 1)) {
					continue;
				}

				sb.append(StringPool.COMMA);
			}

			resourcePath = HttpComponentsUtil.addParameter(
				resourcePath, "sort", sb.toString());
		}

		return resourcePath;
	}

	private byte[] _launch(
			Http.Method method, JSONObject payloadJSONObject,
			String resourcePath, long userId)
		throws Exception {

		Future<byte[]> future = _portalCatapult.launch(
			_companyId, method,
			_functionObjectEntryManagerConfiguration.
				oAuth2ApplicationExternalReferenceCode(),
			payloadJSONObject, resourcePath, userId);

		try {
			return future.get();
		}
		catch (ExecutionException executionException) {
			Throwable throwable = executionException.getCause();

			if (throwable == null) {
				throwable = executionException;
			}

			throw new ObjectEntryManagerHttpException(throwable.getMessage());
		}
	}

	private JSONObject _toJSONObject(
			DTOConverterContext dtoConverterContext, String scopeKey)
		throws Exception {

		return _jsonFactory.createJSONObject(
			_jsonFactory.looseSerialize(
				HashMapBuilder.<String, Object>put(
					"companyId", _companyId
				).put(
					"languageId",
					LocaleUtil.toLanguageId(dtoConverterContext.getLocale())
				).put(
					"scopeKey", scopeKey
				).put(
					"userId", dtoConverterContext.getUserId()
				).build()));
	}

	private JSONObject _toJSONObject(ObjectEntry objectEntry) throws Exception {
		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_jsonFactory.looseSerialize(objectEntry));

		jsonObject = JSONUtil.put(
			"creator", jsonObject.get("creator")
		).put(
			"dateCreated", jsonObject.get("dateCreated")
		).put(
			"dateModified", jsonObject.get("dateModified")
		).put(
			"externalReferenceCode", jsonObject.get("externalReferenceCode")
		).put(
			"status", jsonObject.get("status")
		);

		Map<String, Object> properties = objectEntry.getProperties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}

		return jsonObject;
	}

	private Page<ObjectEntry> _toObjectEntries(
			byte[] bytes, DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, Pagination pagination,
			String scopeKey)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			new String(bytes));

		return Page.of(
			JSONUtil.toList(
				(JSONArray)jsonObject.get("items"),
				itemJSONObject -> _toObjectEntry(
					dtoConverterContext, itemJSONObject.toString(),
					objectDefinition, scopeKey)),
			pagination, (Integer)jsonObject.get("totalCount"));
	}

	private ObjectEntry _toObjectEntry(
			byte[] bytes, DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		return _toObjectEntry(
			dtoConverterContext, new String(bytes), objectDefinition, scopeKey);
	}

	private ObjectEntry _toObjectEntry(
			DTOConverterContext dtoConverterContext, String json,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		dtoConverterContext = new DefaultDTOConverterContext(
			dtoConverterContext.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"delete",
				addDeleteAction(
					objectDefinition, scopeKey, dtoConverterContext.getUser())
			).build(),
			dtoConverterContext.getDTOConverterRegistry(),
			dtoConverterContext.getHttpServletRequest(),
			dtoConverterContext.getId(), dtoConverterContext.getLocale(),
			dtoConverterContext.getUriInfo(), dtoConverterContext.getUser());

		dtoConverterContext.setAttribute("objectDefinition", objectDefinition);
		dtoConverterContext.setAttribute("payload", json);

		return _objectEntryDTOConverter.toDTO(dtoConverterContext);
	}

	private long _companyId;

	@Reference
	private CompanyLocalService _companyLocalService;

	private FunctionObjectEntryManagerConfiguration
		_functionObjectEntryManagerConfiguration;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.object.rest.internal.dto.v1_0.converter.ObjectEntryDTOConverter)"
	)
	private DTOConverter<com.liferay.object.model.ObjectEntry, ObjectEntry>
		_objectEntryDTOConverter;

	@Reference
	private PortalCatapult _portalCatapult;

	private String _storageLabel;
	private String _storageType;

}