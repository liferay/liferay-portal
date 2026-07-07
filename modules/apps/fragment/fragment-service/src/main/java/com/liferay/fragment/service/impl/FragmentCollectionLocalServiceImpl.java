/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.document.library.kernel.model.DLFileVersionTable;
import com.liferay.document.library.kernel.model.DLFolderTable;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.DuplicateFragmentCollectionKeyException;
import com.liferay.fragment.exception.FragmentCollectionNameException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentCollectionTable;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentCompositionTable;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryTable;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.base.FragmentCollectionLocalServiceBaseImpl;
import com.liferay.fragment.service.persistence.FragmentCompositionPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryPersistence;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.RepositoryTable;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.fragment.model.FragmentCollection",
	service = AopService.class
)
public class FragmentCollectionLocalServiceImpl
	extends FragmentCollectionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		return addFragmentCollection(
			externalReferenceCode, userId, groupId, StringPool.BLANK, name,
			description, false, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace, ServiceContext serviceContext)
		throws PortalException {

		// Fragment collection

		User user = _userLocalService.getUser(userId);

		long companyId = user.getCompanyId();

		if (serviceContext != null) {
			companyId = serviceContext.getCompanyId();
		}
		else {
			serviceContext = new ServiceContext();
		}

		_validate(name);

		if (Validator.isNull(fragmentCollectionKey)) {
			fragmentCollectionKey = generateFragmentCollectionKey(
				groupId, name);
		}

		fragmentCollectionKey = _getFragmentCollectionKey(
			fragmentCollectionKey);

		_validateFragmentCollectionKey(groupId, fragmentCollectionKey);

		long fragmentCollectionId = counterLocalService.increment();

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.create(fragmentCollectionId);

		fragmentCollection.setUuid(serviceContext.getUuid());
		fragmentCollection.setExternalReferenceCode(externalReferenceCode);
		fragmentCollection.setGroupId(groupId);
		fragmentCollection.setCompanyId(companyId);
		fragmentCollection.setUserId(user.getUserId());
		fragmentCollection.setUserName(user.getFullName());
		fragmentCollection.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		fragmentCollection.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		fragmentCollection.setFragmentCollectionKey(fragmentCollectionKey);
		fragmentCollection.setName(name);
		fragmentCollection.setDescription(description);
		fragmentCollection.setMarketplace(marketplace);

		return fragmentCollectionPersistence.update(fragmentCollection);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentCollection deleteFragmentCollection(
			FragmentCollection fragmentCollection)
		throws PortalException {

		// Fragment collection

		fragmentCollectionPersistence.remove(fragmentCollection);

		// Resources

		_resourceLocalService.deleteResource(
			fragmentCollection.getCompanyId(),
			FragmentCollection.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			fragmentCollection.getFragmentCollectionId());

		// Images

		PortletFileRepositoryUtil.deletePortletFolder(
			fragmentCollection.getResourcesFolderId(false));

		// Fragment compositions

		List<FragmentComposition> fragmentCompositions =
			_fragmentCompositionPersistence.findByFragmentCollectionId(
				fragmentCollection.getFragmentCollectionId());

		for (FragmentComposition fragmentComposition : fragmentCompositions) {
			_fragmentCompositionLocalService.deleteFragmentComposition(
				fragmentComposition);
		}

		// Fragment entries

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryPersistence.findByFragmentCollectionId(
				fragmentCollection.getFragmentCollectionId());

		for (FragmentEntry fragmentEntry : fragmentEntries) {
			_fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
		}

		return fragmentCollection;
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		return fragmentCollectionLocalService.deleteFragmentCollection(
			getFragmentCollection(fragmentCollectionId));
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return fragmentCollectionLocalService.deleteFragmentCollection(
			fragmentCollectionPersistence.findByERC_G(
				externalReferenceCode, groupId));
	}

	@Override
	public FragmentCollection fetchFragmentCollection(
		long fragmentCollectionId) {

		return fragmentCollectionPersistence.fetchByPrimaryKey(
			fragmentCollectionId);
	}

	@Override
	public FragmentCollection fetchFragmentCollection(
		long groupId, String fragmentCollectionKey) {

		return fragmentCollectionPersistence.fetchByG_FCK(
			groupId, _getFragmentCollectionKey(fragmentCollectionKey));
	}

	@Override
	public String generateFragmentCollectionKey(long groupId, String name) {
		String fragmentCollectionKey = _getFragmentCollectionKey(name);

		fragmentCollectionKey = StringUtil.replace(
			fragmentCollectionKey, CharPool.SPACE, CharPool.DASH);

		String curFragmentCollectionKey = fragmentCollectionKey;

		int count = 0;

		while (true) {
			FragmentCollection fragmentCollection =
				fragmentCollectionPersistence.fetchByG_FCK(
					groupId, curFragmentCollectionKey);

			if (fragmentCollection == null) {
				return curFragmentCollectionKey;
			}

			curFragmentCollectionKey =
				fragmentCollectionKey + CharPool.DASH + count++;
		}
	}

	@Override
	public List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds) {

		return fragmentCollectionPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				FragmentCollectionTable.INSTANCE
			).from(
				FragmentCollectionTable.INSTANCE
			).where(
				_getPredicate(fragmentCollectionIds, null, null)
			));
	}

	@Override
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				FragmentCollectionTable.INSTANCE
			).from(
				FragmentCollectionTable.INSTANCE
			).where(
				_getPredicate(null, groupIds, null)
			).orderBy(
				FragmentCollectionTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				FragmentCollectionTable.INSTANCE
			).from(
				FragmentCollectionTable.INSTANCE
			).where(
				_getPredicate(null, groupIds, name)
			).orderBy(
				FragmentCollectionTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds) {

		return fragmentCollectionPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				FragmentCollectionTable.INSTANCE
			).where(
				_getPredicate(fragmentCollectionIds, null, null)
			));
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(long[] groupIds) {
		return fragmentCollectionPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				FragmentCollectionTable.INSTANCE
			).where(
				_getPredicate(null, groupIds, null)
			));
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name) {

		return fragmentCollectionPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				FragmentCollectionTable.INSTANCE
			).where(
				_getPredicate(null, groupIds, name)
			));
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(long groupId) {
		return getFragmentCollections(groupId, false);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem) {

		return fragmentCollectionPersistence.findByGroupId(
			_getGroupIds(groupId, includeSystem));
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByGroupId(
			_getGroupIds(groupId, includeSystem), start, end,
			orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end) {

		return getFragmentCollections(groupId, start, end, null);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByG_LikeN(
			_getGroupIds(groupId, includeSystem),
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		if (Validator.isNull(name)) {
			return fragmentCollectionPersistence.findByGroupId(
				groupId, start, end, orderByComparator);
		}

		return fragmentCollectionPersistence.findByG_LikeN(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(long[] groupIds) {
		return fragmentCollectionPersistence.findByGroupId(groupIds);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByG_M(
			groupIds, marketplace, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByG_LikeN_M(
			groupIds,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			marketplace, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionPersistence.findByG_LikeN(
			groupIds,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public int getFragmentCollectionsCount(long groupId) {
		return getFragmentCollectionsCount(groupId, false);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, boolean includeSystem) {

		return fragmentCollectionPersistence.countByGroupId(
			_getGroupIds(groupId, includeSystem));
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem) {

		return fragmentCollectionPersistence.countByG_LikeN(
			_getGroupIds(groupId, includeSystem),
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds) {
		return fragmentCollectionPersistence.countByGroupId(groupIds);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace) {

		return fragmentCollectionPersistence.countByG_M(groupIds, marketplace);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds, String name) {
		return fragmentCollectionPersistence.countByG_LikeN(
			groupIds,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace) {

		return fragmentCollectionPersistence.countByG_LikeN_M(
			groupIds,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			marketplace);
	}

	@Override
	public String[] getTempFileNames(
			long userId, long groupId, String folderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(groupId, userId, folderName);
	}

	@Override
	public String getUniqueFragmentCollectionName(long groupId, String name) {
		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.fetchByG_LikeN_First(
				groupId, name, null);

		if (fragmentCollection == null) {
			return name;
		}

		int count = 1;

		while (true) {
			String newName = StringUtil.appendParentheticalSuffix(
				name, count++);

			fragmentCollection =
				fragmentCollectionPersistence.fetchByG_LikeN_First(
					groupId, newName, null);

			if (fragmentCollection == null) {
				return newName;
			}
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws PortalException {

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.findByPrimaryKey(
				fragmentCollectionId);

		_validate(name);

		Date modifiedDate = new Date();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			modifiedDate = serviceContext.getModifiedDate(modifiedDate);
		}

		fragmentCollection.setModifiedDate(modifiedDate);

		fragmentCollection.setName(name);
		fragmentCollection.setDescription(description);

		return fragmentCollectionPersistence.update(fragmentCollection);
	}

	private String _getFragmentCollectionKey(String fragmentCollectionKey) {
		if (fragmentCollectionKey != null) {
			fragmentCollectionKey = fragmentCollectionKey.trim();

			return StringUtil.toLowerCase(fragmentCollectionKey);
		}

		return StringPool.BLANK;
	}

	private Predicate _getFragmentCompositionsPredicate() {
		return FragmentCollectionTable.INSTANCE.fragmentCollectionId.in(
			DSLQueryFactoryUtil.selectDistinct(
				FragmentCompositionTable.INSTANCE.fragmentCollectionId
			).from(
				FragmentCompositionTable.INSTANCE
			).where(
				FragmentCompositionTable.INSTANCE.marketplace.eq(false)
			));
	}

	private Predicate _getFragmentEntriesPredicate() {
		return FragmentCollectionTable.INSTANCE.fragmentCollectionId.in(
			DSLQueryFactoryUtil.selectDistinct(
				FragmentEntryTable.INSTANCE.fragmentCollectionId
			).from(
				FragmentEntryTable.INSTANCE
			).where(
				FragmentEntryTable.INSTANCE.marketplace.eq(
					false
				).and(
					FragmentEntryTable.INSTANCE.type.neq(
						FragmentConstants.TYPE_REACT)
				).and(
					FragmentEntryTable.INSTANCE.head.eq(true)
				)
			));
	}

	private long[] _getGroupIds(long groupId, boolean includeSystem) {
		long[] groupIds = {groupId};

		if (includeSystem) {
			groupIds = ArrayUtil.append(groupIds, CompanyConstants.SYSTEM);
		}

		return groupIds;
	}

	private Predicate _getPredicate(
		long[] fragmentCollectionIds, long[] groupIds, String name) {

		return FragmentCollectionTable.INSTANCE.marketplace.eq(
			false
		).and(
			_getFragmentCompositionsPredicate(
			).or(
				_getFragmentEntriesPredicate()
			).or(
				_getResourcesPredicate()
			).withParentheses()
		).and(
			() -> {
				if (ArrayUtil.isEmpty(fragmentCollectionIds)) {
					return null;
				}

				return FragmentCollectionTable.INSTANCE.fragmentCollectionId.in(
					ArrayUtil.toLongArray(fragmentCollectionIds));
			}
		).and(
			() -> {
				if (ArrayUtil.isEmpty(groupIds)) {
					return null;
				}

				return FragmentCollectionTable.INSTANCE.groupId.in(
					ArrayUtil.toLongArray(groupIds));
			}
		).and(
			() -> {
				if (Validator.isNull(name)) {
					return null;
				}

				return FragmentCollectionTable.INSTANCE.name.like(
					_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
			}
		);
	}

	private Predicate _getResourcesPredicate() {
		RepositoryTable repositoryTable = RepositoryTable.INSTANCE.as(
			"repositoryTable");
		DLFolderTable rootDLFolderTable = DLFolderTable.INSTANCE.as(
			"rootDLFolderTable");
		DLFileVersionTable dlFileVersionTable = DLFileVersionTable.INSTANCE.as(
			"dlFileVersionTable");

		return FragmentCollectionTable.INSTANCE.fragmentCollectionKey.in(
			DSLQueryFactoryUtil.selectDistinct(
				rootDLFolderTable.name
			).from(
				repositoryTable
			).innerJoinON(
				rootDLFolderTable,
				repositoryTable.repositoryId.eq(rootDLFolderTable.repositoryId)
			).where(
				repositoryTable.groupId.eq(
					FragmentCollectionTable.INSTANCE.groupId
				).and(
					repositoryTable.portletId.eq(FragmentPortletKeys.FRAGMENT)
				).and(
					rootDLFolderTable.parentFolderId.eq(
						repositoryTable.dlFolderId)
				).and(
					rootDLFolderTable.folderId.in(
						DSLQueryFactoryUtil.selectDistinct(
							rootDLFolderTable.folderId
						).from(
							dlFileVersionTable
						).where(
							dlFileVersionTable.treePath.like(
								DSLFunctionFactoryUtil.concat(
									rootDLFolderTable.treePath,
									new Scalar<>("%"))
							).and(
								dlFileVersionTable.status.eq(
									WorkflowConstants.STATUS_APPROVED)
							)
						))
				)
			));
	}

	private void _validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new FragmentCollectionNameException("Name must not be null");
		}

		if (name.contains(StringPool.PERIOD) ||
			name.contains(StringPool.SLASH)) {

			throw new FragmentCollectionNameException(
				"Name contains invalid characters");
		}
	}

	private void _validateFragmentCollectionKey(
			long groupId, String fragmentCollectionKey)
		throws PortalException {

		fragmentCollectionKey = _getFragmentCollectionKey(
			fragmentCollectionKey);

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.fetchByG_FCK(
				groupId, fragmentCollectionKey);

		if (fragmentCollection != null) {
			throw new DuplicateFragmentCollectionKeyException(
				fragmentCollectionKey);
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@Reference
	private FragmentCompositionPersistence _fragmentCompositionPersistence;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryPersistence _fragmentEntryPersistence;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}