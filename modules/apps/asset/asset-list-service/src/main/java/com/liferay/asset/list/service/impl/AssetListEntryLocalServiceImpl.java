/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.exception.AssetListEntryTitleException;
import com.liferay.asset.list.exception.DuplicateAssetListEntryTitleException;
import com.liferay.asset.list.exception.RequiredAssetListEntryException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRelTable;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.asset.list.service.base.AssetListEntryLocalServiceBaseImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntrySegmentsEntryRelPersistence;
import com.liferay.asset.util.AssetRendererFactoryWrapper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntry",
	service = AopService.class
)
public class AssetListEntryLocalServiceImpl
	extends AssetListEntryLocalServiceBaseImpl {

	@Override
	public void addAssetEntrySelection(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		addAssetEntrySelections(
			assetListEntryId, new long[] {assetEntryId}, segmentsEntryId,
			serviceContext);
	}

	@Override
	public void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		// Asset list entry

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		if (Objects.equals(
				assetListEntry.getType(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC)) {

			throw new PortalException();
		}

		// Asset list entry segments entry rel

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntryId, segmentsEntryId);

		if (assetListEntrySegmentsEntryRel == null) {
			assetListEntrySegmentsEntryRel =
				_assetListEntrySegmentsEntryRelLocalService.
					addAssetListEntrySegmentsEntryRel(
						serviceContext.getUserId(),
						serviceContext.getScopeGroupId(), assetListEntryId,
						segmentsEntryId, StringPool.BLANK, serviceContext);
		}

		List<Long> selectedAssetEntryIds = new ArrayList<>(
			dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					AssetListEntryAssetEntryRelTable.INSTANCE.assetEntryId
				).from(
					AssetListEntryAssetEntryRelTable.INSTANCE
				).where(
					AssetListEntryAssetEntryRelTable.INSTANCE.assetListEntryId.
						eq(
							assetListEntryId
						).and(
							AssetListEntryAssetEntryRelTable.INSTANCE.
								segmentsEntryId.eq(
									assetListEntrySegmentsEntryRel.
										getSegmentsEntryId())
						)
				)));

		for (long assetEntryId : ArrayUtil.unique(assetEntryIds)) {
			if (selectedAssetEntryIds.contains(assetEntryId)) {
				continue;
			}

			_assetListEntryAssetEntryRelLocalService.
				addAssetListEntryAssetEntryRel(
					assetListEntryId, assetEntryId, segmentsEntryId,
					serviceContext);
		}

		// Asset list entry

		if (Validator.isNull(assetListEntry.getAssetEntryType())) {
			String assetEntryType = _getManualAssetEntryType(assetListEntryId);

			assetListEntry.setAssetEntrySubtype(
				_getManualAssetEntrySubtype(assetEntryType, assetListEntryId));
			assetListEntry.setAssetEntryType(assetEntryType);
		}

		assetListEntryPersistence.update(assetListEntry);
	}

	@Override
	public AssetListEntry addAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, int type, ServiceContext serviceContext)
		throws PortalException {

		return addAssetListEntry(
			externalReferenceCode, userId, groupId, title, type, null,
			serviceContext);
	}

	@Override
	public AssetListEntry addAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, int type, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		// Asset list entry

		_validateTitle(groupId, title);

		User user = _userLocalService.getUser(userId);

		long assetListEntryId = counterLocalService.increment();

		AssetListEntry assetListEntry = assetListEntryPersistence.create(
			assetListEntryId);

		assetListEntry.setUuid(serviceContext.getUuid());
		assetListEntry.setExternalReferenceCode(externalReferenceCode);
		assetListEntry.setGroupId(groupId);
		assetListEntry.setCompanyId(user.getCompanyId());
		assetListEntry.setUserId(user.getUserId());
		assetListEntry.setUserName(user.getFullName());

		_updateAssetListEntryKey(
			assetListEntry,
			GetterUtil.getString(
				serviceContext.getAttribute("assetListEntryKey")),
			groupId, title);

		assetListEntry.setTitle(title);
		assetListEntry.setType(type);

		if (type == AssetListEntryTypeConstants.TYPE_DYNAMIC) {
			String assetEntryType = _getAssetEntryType(typeSettings);

			assetListEntry.setAssetEntrySubtype(
				_getAssetEntrySubtype(assetEntryType, typeSettings));
			assetListEntry.setAssetEntryType(assetEntryType);
		}

		assetListEntry = assetListEntryPersistence.update(assetListEntry);

		// Resources

		_resourceLocalService.addResources(
			assetListEntry.getCompanyId(), assetListEntry.getGroupId(),
			assetListEntry.getUserId(), AssetListEntry.class.getName(),
			assetListEntry.getPrimaryKey(), false, true, true);

		// Asset list entry segments entry rel

		if (!ExportImportThreadLocal.isImportInProcess()) {
			_assetListEntrySegmentsEntryRelLocalService.
				addAssetListEntrySegmentsEntryRel(
					userId, groupId, assetListEntryId,
					SegmentsEntryConstants.ID_DEFAULT, typeSettings,
					serviceContext);
		}

		return assetListEntry;
	}

	@Override
	public AssetListEntry addDynamicAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		return addAssetListEntry(
			externalReferenceCode, userId, groupId, title,
			AssetListEntryTypeConstants.TYPE_DYNAMIC, typeSettings,
			serviceContext);
	}

	@Override
	public AssetListEntry addManualAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, long[] assetEntryIds, ServiceContext serviceContext)
		throws PortalException {

		AssetListEntry assetListEntry = addAssetListEntry(
			externalReferenceCode, userId, groupId, title,
			AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, serviceContext);

		return assetListEntry;
	}

	@Override
	public void deleteAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position)
		throws PortalException {

		// Asset list entry

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		if (Objects.equals(
				assetListEntry.getType(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC)) {

			throw new PortalException();
		}

		// Asset list entry segments entry rel

		_assetListEntryAssetEntryRelLocalService.
			deleteAssetListEntryAssetEntryRel(
				assetListEntryId, segmentsEntryId, position);

		// Asset list entry

		assetListEntry.setModifiedDate(new Date());

		if (Validator.isNull(assetListEntry.getAssetEntryType())) {
			String assetEntryType = _getManualAssetEntryType(assetListEntryId);

			assetListEntry.setAssetEntrySubtype(
				_getManualAssetEntrySubtype(assetEntryType, assetListEntryId));
			assetListEntry.setAssetEntryType(assetEntryType);
		}

		assetListEntryPersistence.update(assetListEntry);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetListEntry deleteAssetListEntry(AssetListEntry assetListEntry)
		throws PortalException {

		// Asset list entry

		if (!GroupThreadLocal.isDeleteInProcess()) {
			_checkCompanyAssetListEntryUsages(assetListEntry);
		}

		assetListEntryPersistence.remove(assetListEntry);

		// Resources

		_resourceLocalService.deleteResource(
			assetListEntry, ResourceConstants.SCOPE_INDIVIDUAL);

		// Asset list entry rels

		_assetListEntryAssetEntryRelPersistence.removeByAssetListEntryId(
			assetListEntry.getAssetListEntryId());

		// Asset list segments entry rels

		_assetListEntrySegmentsEntryRelPersistence.removeByAssetListEntryId(
			assetListEntry.getAssetListEntryId());

		return assetListEntry;
	}

	@Override
	public AssetListEntry deleteAssetListEntry(long assetListEntryId)
		throws PortalException {

		return deleteAssetListEntry(getAssetListEntry(assetListEntryId));
	}

	@Override
	public AssetListEntry deleteAssetListEntry(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		// Asset list entry

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		assetListEntry.setModifiedDate(new Date());

		assetListEntry = assetListEntryPersistence.update(assetListEntry);

		// Asset list segments entry rel

		_assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRel(
				assetListEntryId, segmentsEntryId);

		return assetListEntry;
	}

	@Override
	public AssetListEntry deleteAssetListEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetListEntry assetListEntry = assetListEntryPersistence.findByERC_G(
			externalReferenceCode, groupId);

		return deleteAssetListEntry(assetListEntry);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(long groupId) {
		return assetListEntryPersistence.findByGroupId(groupId);
	}

	@Override
	public AssetListEntry getAssetListEntry(
			long groupId, String assetListEntryKey)
		throws PortalException {

		return assetListEntryPersistence.findByG_ALEK(
			groupId, assetListEntryKey);
	}

	@Override
	public void moveAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException {

		// Asset list entry

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		if (Objects.equals(
				assetListEntry.getType(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC)) {

			throw new PortalException();
		}

		assetListEntry.setModifiedDate(new Date());

		assetListEntryPersistence.update(assetListEntry);

		// Asset list entry asset entry rel

		_assetListEntryAssetEntryRelLocalService.
			moveAssetListEntryAssetEntryRel(
				assetListEntryId, segmentsEntryId, position, newPosition);
	}

	@Override
	public AssetListEntry updateAssetListEntry(
			long assetListEntryId, long segmentsEntryId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		// Asset list entry

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		if (assetListEntry.getType() ==
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			String assetEntryType = _getSegmentsAssetEntryType(
				assetListEntryId, segmentsEntryId, typeSettings);

			assetListEntry.setAssetEntrySubtype(
				_getSegmentsAssetEntrySubtype(
					assetEntryType, assetListEntryId, segmentsEntryId,
					typeSettings));
			assetListEntry.setAssetEntryType(assetEntryType);
		}

		assetListEntry = assetListEntryPersistence.update(assetListEntry);

		// Asset list entry segments entry rel

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntryId, segmentsEntryId);

		if (assetListEntrySegmentsEntryRel == null) {
			_assetListEntrySegmentsEntryRelLocalService.
				addAssetListEntrySegmentsEntryRel(
					serviceContext.getUserId(),
					serviceContext.getScopeGroupId(), assetListEntryId,
					segmentsEntryId, typeSettings, serviceContext);
		}
		else {
			_assetListEntrySegmentsEntryRelLocalService.
				updateAssetListEntrySegmentsEntryRelTypeSettings(
					assetListEntryId, segmentsEntryId, typeSettings);
		}

		return assetListEntry;
	}

	@Override
	public AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title)
		throws PortalException {

		return updateAssetListEntry(
			assetListEntryId, title, new ServiceContext());
	}

	@Override
	public AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title, ServiceContext serviceContext)
		throws PortalException {

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		String assetListEntryKey = GetterUtil.getString(
			serviceContext.getAttribute("assetListEntryKey"));

		if (Objects.equals(assetListEntry.getTitle(), title) &&
			(Validator.isNull(assetListEntryKey) ||
			 Objects.equals(
				 assetListEntry.getAssetListEntryKey(), assetListEntryKey))) {

			return assetListEntry;
		}

		_validateTitle(assetListEntry.getGroupId(), title);

		_updateAssetListEntryKey(
			assetListEntry, assetListEntryKey, assetListEntry.getGroupId(),
			title);

		assetListEntry.setTitle(title);

		return assetListEntryPersistence.update(assetListEntry);
	}

	@Override
	public void updateAssetListEntryTypeSettings(
			long companyId, long classNameId)
		throws PortalException {

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				_assetListEntrySegmentsEntryRelLocalService.
					getAssetListEntrySegmentsEntryRelsByClassNameId(
						companyId, classNameId)) {

			_updateAssetListEntryTypeSettings(
				assetListEntrySegmentsEntryRel, String.valueOf(classNameId));
		}
	}

	@Override
	public void updateAssetListEntryTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings)
		throws PortalException {

		// Asset list entry

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		assetListEntry.setModifiedDate(new Date());

		String assetEntryType = _getSegmentsAssetEntryType(
			assetListEntryId, segmentsEntryId, typeSettings);

		assetListEntry.setAssetEntrySubtype(
			_getSegmentsAssetEntrySubtype(
				assetEntryType, assetListEntryId, segmentsEntryId,
				typeSettings));
		assetListEntry.setAssetEntryType(assetEntryType);

		assetListEntryPersistence.update(assetListEntry);

		// Asset list entry segments entry rel

		_assetListEntrySegmentsEntryRelLocalService.
			updateAssetListEntrySegmentsEntryRelTypeSettings(
				assetListEntryId, segmentsEntryId, typeSettings);
	}

	private void _checkCompanyAssetListEntryUsages(
			AssetListEntry assetListEntry)
		throws PortalException {

		int count =
			_assetListEntryUsageLocalService.
				getCompanyAssetListEntryUsagesCount(
					assetListEntry.getCompanyId(),
					_portal.getClassNameId(AssetListEntry.class),
					String.valueOf(assetListEntry.getAssetListEntryId()));

		if (count > 0) {
			throw new RequiredAssetListEntryException();
		}
	}

	private String _generateAssetListEntryKey(long groupId, String title) {
		String assetListEntryKey = StringUtil.toLowerCase(title.trim());

		assetListEntryKey = StringUtil.replace(
			assetListEntryKey, CharPool.SPACE, CharPool.DASH);

		String curAssetListEntryKey = assetListEntryKey;

		int count = 0;

		while (true) {
			AssetListEntry assetListEntry =
				assetListEntryPersistence.fetchByG_ALEK(
					groupId, curAssetListEntryKey);

			if (assetListEntry == null) {
				return curAssetListEntryKey;
			}

			curAssetListEntryKey = assetListEntryKey + CharPool.DASH + count++;
		}
	}

	private String _getAssetEntrySubtype(
		String assetEntryType, String typeSettings) {

		if (Validator.isNull(typeSettings) ||
			Validator.isNull(assetEntryType) ||
			!_isSupportsItemSubtypes(assetEntryType)) {

			return StringPool.BLANK;
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			typeSettings
		).build();

		String anyAssetClassTypeString = unicodeProperties.getProperty(
			"anyClassType" + _getAssetRendererFactoryName(assetEntryType));

		boolean anyAssetClassType = GetterUtil.getBoolean(
			anyAssetClassTypeString);

		if (anyAssetClassType) {
			return StringPool.BLANK;
		}

		long defaultAssetClassTypeId = GetterUtil.getLong(
			anyAssetClassTypeString, -1);

		if (defaultAssetClassTypeId >= 0) {
			return String.valueOf(defaultAssetClassTypeId);
		}

		String classTypeIdsString = unicodeProperties.getProperty(
			"classTypeIds" + _getAssetRendererFactoryName(assetEntryType));

		if (Validator.isNotNull(classTypeIdsString)) {
			return classTypeIdsString;
		}

		return StringPool.BLANK;
	}

	private String _getAssetEntryType(String typeSettings) {
		if (Validator.isNull(typeSettings)) {
			return AssetEntry.class.getName();
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			typeSettings
		).build();

		String anyAssetTypeString = unicodeProperties.getProperty(
			"anyAssetType");

		boolean anyAssetType = GetterUtil.getBoolean(anyAssetTypeString);

		if (anyAssetType) {
			return AssetEntry.class.getName();
		}

		long defaultAssetType = GetterUtil.getLong(anyAssetTypeString);

		if (defaultAssetType <= 0) {
			return AssetEntry.class.getName();
		}

		String className = _portal.fetchClassName(defaultAssetType);

		if (Validator.isNull(className)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to resolve class name ID " + defaultAssetType);
			}

			return AssetEntry.class.getName();
		}

		return className;
	}

	private String _getAssetRendererFactoryName(String assetEntryType) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntryType);

		Class<?> clazz = assetRendererFactory.getClass();

		if (assetRendererFactory instanceof AssetRendererFactoryWrapper) {
			AssetRendererFactoryWrapper<?> assetRendererFactoryWrapper =
				(AssetRendererFactoryWrapper<?>)assetRendererFactory;

			clazz = assetRendererFactoryWrapper.getWrappedClass();
		}

		String className = clazz.getName();

		int pos = className.lastIndexOf(StringPool.PERIOD);

		return className.substring(pos + 1);
	}

	private String _getClassNameIds(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel,
		String classNameId) {

		return StringUtil.merge(
			ArrayUtil.remove(
				AssetRendererFactoryRegistryUtil.getClassNameIds(
					assetListEntrySegmentsEntryRel.getCompanyId(), true),
				GetterUtil.getLong(classNameId)));
	}

	private String _getManualAssetEntrySubtype(
		String assetEntryType, long assetListEntryId) {

		if (Validator.isNull(assetEntryType) ||
			!_isSupportsItemSubtypes(assetEntryType)) {

			return StringPool.BLANK;
		}

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (ListUtil.isEmpty(assetListEntryAssetEntryRels)) {
			return StringPool.BLANK;
		}

		String assetEntrySubtype = StringPool.BLANK;

		for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
				assetListEntryAssetEntryRels) {

			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				assetListEntryAssetEntryRel.getAssetEntryId());

			if (Validator.isNull(assetEntrySubtype)) {
				assetEntrySubtype = String.valueOf(assetEntry.getClassTypeId());
			}
			else if (!Objects.equals(
						assetEntrySubtype,
						String.valueOf(assetEntry.getClassTypeId()))) {

				return StringPool.BLANK;
			}
		}

		return assetEntrySubtype;
	}

	private String _getManualAssetEntryType(long assetListEntryId) {
		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (ListUtil.isEmpty(assetListEntryAssetEntryRels)) {
			return AssetEntry.class.getName();
		}

		String assetEntryType = StringPool.BLANK;

		for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
				assetListEntryAssetEntryRels) {

			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				assetListEntryAssetEntryRel.getAssetEntryId());

			if (Validator.isNull(assetEntryType)) {
				assetEntryType = assetEntry.getClassName();
			}
			else if (!Objects.equals(
						assetEntryType, assetEntry.getClassName())) {

				return AssetEntry.class.getName();
			}
		}

		return assetEntryType;
	}

	private String _getSegmentsAssetEntrySubtype(
		String assetEntryType, long assetListEntryId, long segmentsEntryId,
		String typeSettings) {

		String assetEntrySubtype = _getAssetEntrySubtype(
			assetEntryType, typeSettings);

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRels(
					assetListEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			if ((assetListEntrySegmentsEntryRel.getSegmentsEntryId() ==
					segmentsEntryId) ||
				Objects.equals(
					assetEntrySubtype,
					_getAssetEntrySubtype(
						assetEntryType,
						assetListEntrySegmentsEntryRel.getTypeSettings()))) {

				continue;
			}

			return StringPool.BLANK;
		}

		return assetEntrySubtype;
	}

	private String _getSegmentsAssetEntryType(
		long assetListEntryId, long segmentsEntryId, String typeSettings) {

		String assetEntryType = _getAssetEntryType(typeSettings);

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRels(
					assetListEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			if ((assetListEntrySegmentsEntryRel.getSegmentsEntryId() ==
					segmentsEntryId) ||
				Objects.equals(
					assetEntryType,
					_getAssetEntryType(
						assetListEntrySegmentsEntryRel.getTypeSettings()))) {

				continue;
			}

			return AssetEntry.class.getName();
		}

		return assetEntryType;
	}

	private boolean _isSupportsItemSubtypes(String assetEntryType) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntryType);

		if ((assetRendererFactory != null) &&
			assetRendererFactory.isSupportsClassTypes()) {

			return true;
		}

		return false;
	}

	private void _updateAssetListEntryKey(
		AssetListEntry assetListEntry, String assetListEntryKey, long groupId,
		String title) {

		if (Validator.isNull(assetListEntryKey) ||
			!(ExportImportThreadLocal.isImportInProcess() ||
			  ExportImportThreadLocal.isStagingInProcess())) {

			assetListEntry.setAssetListEntryKey(
				_generateAssetListEntryKey(groupId, title));

			return;
		}

		assetListEntry.setAssetListEntryKey(assetListEntryKey);
	}

	private void _updateAssetListEntryTypeSettings(
			AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel,
			String classNameId)
		throws PortalException {

		String typeSettings = assetListEntrySegmentsEntryRel.getTypeSettings();

		if (Validator.isNull(typeSettings)) {
			return;
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			typeSettings
		).build();

		String[] classNameIds = StringUtil.split(
			unicodeProperties.getProperty("classNameIds"));

		if (!ArrayUtil.contains(classNameIds, classNameId)) {
			return;
		}

		classNameIds = ArrayUtil.remove(classNameIds, classNameId);

		String anyAssetTypeValue = GetterUtil.getString(
			unicodeProperties.getProperty("anyAssetType"));

		if (ArrayUtil.isNotEmpty(classNameIds)) {
			if ((classNameIds.length == 1) &&
				StringUtil.equalsIgnoreCase(
					anyAssetTypeValue, Boolean.FALSE.toString())) {

				unicodeProperties.setProperty("anyAssetType", classNameIds[0]);
				unicodeProperties.setProperty(
					"classNameIds",
					_getClassNameIds(
						assetListEntrySegmentsEntryRel, classNameId));

				assetListEntryLocalService.updateAssetListEntryTypeSettings(
					assetListEntrySegmentsEntryRel.getAssetListEntryId(),
					assetListEntrySegmentsEntryRel.getSegmentsEntryId(),
					unicodeProperties.toString());

				return;
			}

			unicodeProperties.setProperty(
				"classNameIds", StringUtil.merge(classNameIds));
		}
		else {
			unicodeProperties.setProperty(
				"anyAssetType", Boolean.TRUE.toString());
			unicodeProperties.setProperty(
				"classNameIds",
				_getClassNameIds(assetListEntrySegmentsEntryRel, classNameId));

			assetListEntryLocalService.updateAssetListEntryTypeSettings(
				assetListEntrySegmentsEntryRel.getAssetListEntryId(),
				assetListEntrySegmentsEntryRel.getSegmentsEntryId(),
				unicodeProperties.toString());

			return;
		}

		if (anyAssetTypeValue.equals(classNameId)) {
			unicodeProperties.setProperty(
				"anyAssetType", Boolean.TRUE.toString());
		}

		assetListEntryLocalService.updateAssetListEntryTypeSettings(
			assetListEntrySegmentsEntryRel.getAssetListEntryId(),
			assetListEntrySegmentsEntryRel.getSegmentsEntryId(),
			unicodeProperties.toString());
	}

	private void _validateTitle(long groupId, String title)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new AssetListEntryTitleException("Title is null");
		}

		int titleMaxLength = ModelHintsUtil.getMaxLength(
			AssetListEntry.class.getName(), "title");

		if (title.length() > titleMaxLength) {
			throw new AssetListEntryTitleException(
				"Title has more than " + titleMaxLength + " characters");
		}

		AssetListEntry assetListEntry = assetListEntryPersistence.fetchByG_T(
			groupId, title);

		if (assetListEntry != null) {
			throw new DuplicateAssetListEntryTitleException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Reference
	private AssetListEntryAssetEntryRelPersistence
		_assetListEntryAssetEntryRelPersistence;

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Reference
	private AssetListEntrySegmentsEntryRelPersistence
		_assetListEntrySegmentsEntryRelPersistence;

	@Reference
	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}