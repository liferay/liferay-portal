/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.search.experiences.constants.SXPBlueprintConstants;
import com.liferay.search.experiences.exception.SXPBlueprintTitleException;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.util.ConfigurationUtil;
import com.liferay.search.experiences.service.base.SXPBlueprintLocalServiceBaseImpl;
import com.liferay.search.experiences.validator.SXPBlueprintValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Petteri Karttunen
 */
@Component(
	enabled = false,
	property = "model.class.name=com.liferay.search.experiences.model.SXPBlueprint",
	service = AopService.class
)
public class SXPBlueprintLocalServiceImpl
	extends SXPBlueprintLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SXPBlueprint addSXPBlueprint(
			String externalReferenceCode, long userId, String configurationJSON,
			Map<Locale, String> descriptionMap, String elementInstancesJSON,
			String schemaVersion, Map<Locale, String> titleMap,
			ServiceContext serviceContext)
		throws PortalException {

		_validate(titleMap, serviceContext);

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.create(
			counterLocalService.increment());

		sxpBlueprint.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		sxpBlueprint.setCompanyId(user.getCompanyId());
		sxpBlueprint.setUserId(user.getUserId());
		sxpBlueprint.setUserName(user.getFullName());

		sxpBlueprint.setConfigurationJSON(
			_enhanceConfiguration(configurationJSON));
		sxpBlueprint.setDescriptionMap(descriptionMap);
		sxpBlueprint.setElementInstancesJSON(elementInstancesJSON);
		sxpBlueprint.setSchemaVersion(schemaVersion);
		sxpBlueprint.setTitleMap(titleMap);
		sxpBlueprint.setVersion(
			String.format(
				"%.1f",
				GetterUtil.getFloat(sxpBlueprint.getVersion(), 0.9F) + 0.1));
		sxpBlueprint.setStatus(WorkflowConstants.STATUS_APPROVED);
		sxpBlueprint.setStatusByUserId(user.getUserId());
		sxpBlueprint.setStatusDate(serviceContext.getModifiedDate(null));

		sxpBlueprint = _upgradeSXPBlueprint(sxpBlueprint);

		sxpBlueprint = sxpBlueprintPersistence.update(sxpBlueprint);

		_resourceLocalService.addModelResources(sxpBlueprint, serviceContext);

		return sxpBlueprint;
	}

	@Override
	public void deleteCompanySXPBlueprints(long companyId)
		throws PortalException {

		List<SXPBlueprint> sxpBlueprints =
			sxpBlueprintPersistence.findByCompanyId(companyId);

		for (SXPBlueprint sxpBlueprint : sxpBlueprints) {
			sxpBlueprintLocalService.deleteSXPBlueprint(sxpBlueprint);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SXPBlueprint deleteSXPBlueprint(long sxpBlueprintId)
		throws PortalException {

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.findByPrimaryKey(
			sxpBlueprintId);

		return deleteSXPBlueprint(sxpBlueprint);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SXPBlueprint deleteSXPBlueprint(SXPBlueprint sxpBlueprint)
		throws PortalException {

		sxpBlueprint = sxpBlueprintPersistence.remove(sxpBlueprint);

		_resourceLocalService.deleteResource(
			sxpBlueprint, ResourceConstants.SCOPE_INDIVIDUAL);

		return sxpBlueprint;
	}

	@Override
	public List<SXPBlueprint> getSXPBlueprints(long companyId) {
		return sxpBlueprintPersistence.findByCompanyId(companyId);
	}

	@Override
	public int getSXPBlueprintsCount(long companyId) {
		return sxpBlueprintPersistence.countByCompanyId(companyId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SXPBlueprint updateStatus(
			long userId, long sxpBlueprintId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.findByPrimaryKey(
			sxpBlueprintId);

		if (sxpBlueprint.getStatus() == status) {
			return sxpBlueprint;
		}

		sxpBlueprint.setStatus(status);

		User user = _userLocalService.getUser(userId);

		sxpBlueprint.setStatusByUserId(user.getUserId());
		sxpBlueprint.setStatusByUserName(user.getFullName());

		sxpBlueprint.setStatusDate(serviceContext.getModifiedDate(null));

		return sxpBlueprintPersistence.update(sxpBlueprint);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SXPBlueprint updateSXPBlueprint(
			String externalReferenceCode, long userId, long sxpBlueprintId,
			String configurationJSON, Map<Locale, String> descriptionMap,
			String elementInstancesJSON, String schemaVersion,
			Map<Locale, String> titleMap, ServiceContext serviceContext)
		throws PortalException {

		_validate(titleMap, serviceContext);

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.findByPrimaryKey(
			sxpBlueprintId);

		sxpBlueprint.setExternalReferenceCode(externalReferenceCode);
		sxpBlueprint.setConfigurationJSON(
			_enhanceConfiguration(configurationJSON));
		sxpBlueprint.setDescriptionMap(descriptionMap);
		sxpBlueprint.setElementInstancesJSON(elementInstancesJSON);
		sxpBlueprint.setTitleMap(titleMap);
		sxpBlueprint.setVersion(
			String.format(
				"%.1f",
				GetterUtil.getFloat(sxpBlueprint.getVersion(), 0.9F) + 0.1));

		return updateSXPBlueprint(sxpBlueprint);
	}

	private String _enhanceConfiguration(String configuration)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-129412")) {
			return configuration;
		}

		try {
			JSONObject configurationJSONObject = _jsonFactory.createJSONObject(
				configuration);

			JSONObject generalConfigurationJSONObject =
				configurationJSONObject.getJSONObject("generalConfiguration");

			if (generalConfigurationJSONObject == null) {
				return configuration;
			}

			JSONArray searchableAssetTypesJSONArray =
				(JSONArray)generalConfigurationJSONObject.get(
					"searchableAssetTypes");

			if ((searchableAssetTypesJSONArray == null) ||
				generalConfigurationJSONObject.getBoolean(
					"legacyAssetCollectionProvider")) {

				return _setCollectionProviderType(
					configurationJSONObject, generalConfigurationJSONObject,
					AssetEntry.class.getName());
			}

			String[] searchableAssetTypesArray = JSONUtil.toStringArray(
				searchableAssetTypesJSONArray);

			if (searchableAssetTypesArray.length == 0) {
				return _setCollectionProviderType(
					configurationJSONObject, generalConfigurationJSONObject,
					AssetEntry.class.getName());
			}

			if (searchableAssetTypesArray.length == 1) {
				return _setCollectionProviderType(
					configurationJSONObject, generalConfigurationJSONObject,
					searchableAssetTypesArray[0]);
			}

			AssetSubtypeIdentifier assetSubtypeIdentifier1 =
				_assetSubtypeIdentifierBuilder.searchableAssetType(
					searchableAssetTypesArray[0]
				).build();

			for (int i = 1; i < searchableAssetTypesArray.length; i++) {
				AssetSubtypeIdentifier assetSubtypeIdentifier2 =
					_assetSubtypeIdentifierBuilder.searchableAssetType(
						searchableAssetTypesArray[i]
					).build();

				if (!StringUtil.equals(
						assetSubtypeIdentifier1.getClassName(),
						assetSubtypeIdentifier2.getClassName())) {

					return _setCollectionProviderType(
						configurationJSONObject, generalConfigurationJSONObject,
						AssetEntry.class.getName());
				}
			}

			return _setCollectionProviderType(
				configurationJSONObject, generalConfigurationJSONObject,
				assetSubtypeIdentifier1.getClassName());
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private String _setCollectionProviderType(
		JSONObject configurationJSONObject,
		JSONObject generalConfigurationJSONObject, String type) {

		AssetSubtypeIdentifier assetSubtypeIdentifier =
			_assetSubtypeIdentifierBuilder.searchableAssetType(
				type
			).build();

		String className = assetSubtypeIdentifier.getClassName();

		if (!_collectionProviderTypes.contains(className) &&
			!className.startsWith(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

			type = AssetEntry.class.getName();
		}

		generalConfigurationJSONObject.put("collectionProviderType", type);

		return configurationJSONObject.toString();
	}

	private SXPBlueprint _upgradeSXPBlueprint(SXPBlueprint sxpBlueprint) {
		if (!Objects.equals(sxpBlueprint.getSchemaVersion(), "1.0")) {
			return sxpBlueprint;
		}

		sxpBlueprint.setSchemaVersion(SXPBlueprintConstants.SCHEMA_VERSION);

		Configuration configuration = ConfigurationUtil.toConfiguration(
			sxpBlueprint.getConfigurationJSON());

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		String[] clauseContributorsExcludes =
			generalConfiguration.getClauseContributorsExcludes();
		String[] clauseContributorsIncludes =
			generalConfiguration.getClauseContributorsIncludes();

		if (clauseContributorsExcludes.length == 0) {
			generalConfiguration.setClauseContributorsIncludes(() -> _WILDCARD);
		}
		else if (clauseContributorsIncludes.length == 0) {
			generalConfiguration.setClauseContributorsExcludes(() -> _WILDCARD);
		}
		else {
			generalConfiguration.setClauseContributorsExcludes(
				() -> new String[0]);
		}

		sxpBlueprint.setConfigurationJSON(configuration.toString());

		return sxpBlueprint;
	}

	private void _validate(
			Map<Locale, String> titleMap, ServiceContext serviceContext)
		throws SXPBlueprintTitleException {

		if (GetterUtil.getBoolean(
				serviceContext.getAttribute(
					SXPBlueprintLocalServiceImpl.class.getName() +
						"#_validate"),
				true)) {

			_sxpBlueprintValidator.validate(titleMap);
		}
	}

	private static final String[] _WILDCARD = {StringPool.STAR};

	@Reference
	private AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;

	private final List<String> _collectionProviderTypes = new ArrayList<>(
		Arrays.asList(
			BlogsEntry.class.getName(), CalendarBooking.class.getName(),
			DLFileEntry.class.getName(), JournalArticle.class.getName(),
			KBArticle.class.getName()));

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SXPBlueprintValidator _sxpBlueprintValidator;

	@Reference
	private UserLocalService _userLocalService;

}