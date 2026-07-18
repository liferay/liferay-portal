/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.SQLStateAcceptor;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.base.RedirectNotFoundEntryLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.redirect.model.RedirectNotFoundEntry",
	service = AopService.class
)
public class RedirectNotFoundEntryLocalServiceImpl
	extends RedirectNotFoundEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION
			)
		}
	)
	public RedirectNotFoundEntry addOrUpdateRedirectNotFoundEntry(
		Group group, String url) {

		url = _friendlyURLNormalizer.normalizeWithEncoding(url);

		RedirectNotFoundEntry redirectNotFoundEntry =
			redirectNotFoundEntryPersistence.fetchByG_U(
				group.getGroupId(), url);

		if (redirectNotFoundEntry == null) {
			redirectNotFoundEntry = redirectNotFoundEntryPersistence.create(
				counterLocalService.increment());

			redirectNotFoundEntry.setGroupId(group.getGroupId());
			redirectNotFoundEntry.setCompanyId(group.getCompanyId());
			redirectNotFoundEntry.setUrl(url);

			redirectNotFoundEntry = redirectNotFoundEntryPersistence.update(
				redirectNotFoundEntry);
		}

		_viewCountManager.incrementViewCount(
			redirectNotFoundEntry.getCompanyId(),
			_portal.getClassNameId(RedirectNotFoundEntry.class),
			redirectNotFoundEntry.getRedirectNotFoundEntryId(), 1);

		return redirectNotFoundEntry;
	}

	@Override
	public void deleteRedirectNotFoundEntries(long groupId)
		throws PortalException {

		for (RedirectNotFoundEntry redirectNotFoundEntry :
				redirectNotFoundEntryPersistence.findByGroupId(groupId)) {

			redirectNotFoundEntryLocalService.deleteRedirectNotFoundEntry(
				redirectNotFoundEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public RedirectNotFoundEntry deleteRedirectNotFoundEntry(
			RedirectNotFoundEntry redirectNotFoundEntry)
		throws PortalException {

		_viewCountManager.deleteViewCount(
			redirectNotFoundEntry.getCompanyId(),
			_portal.getClassNameId(RedirectNotFoundEntry.class),
			redirectNotFoundEntry.getRedirectNotFoundEntryId());

		return super.deleteRedirectNotFoundEntry(redirectNotFoundEntry);
	}

	@Override
	public RedirectNotFoundEntry fetchRedirectNotFoundEntry(
		long groupId, String url) {

		return redirectNotFoundEntryPersistence.fetchByG_U(groupId, url);
	}

	@Override
	public List<RedirectNotFoundEntry> getRedirectNotFoundEntries(
		long groupId, Boolean ignored, Date minModifiedDate, int start, int end,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		return redirectNotFoundEntryPersistence.findWithDynamicQuery(
			_getRedirectNotFoundEntriesDynamicQuery(
				groupId, ignored, minModifiedDate, orderByComparator),
			start, end);
	}

	@Override
	public List<RedirectNotFoundEntry> getRedirectNotFoundEntries(
		long groupId, Date minModifiedDate, int start, int end,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		return redirectNotFoundEntryPersistence.findWithDynamicQuery(
			_getRedirectNotFoundEntriesDynamicQuery(
				groupId, null, minModifiedDate, orderByComparator),
			start, end);
	}

	@Override
	public List<RedirectNotFoundEntry> getRedirectNotFoundEntries(
		long groupId, int start, int end,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		return redirectNotFoundEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getRedirectNotFoundEntriesCount(long groupId) {
		return redirectNotFoundEntryPersistence.countByGroupId(groupId);
	}

	@Override
	public int getRedirectNotFoundEntriesCount(
		long groupId, Boolean ignored, Date minModifiedDate) {

		return GetterUtil.getInteger(
			redirectNotFoundEntryPersistence.countWithDynamicQuery(
				_getRedirectNotFoundEntriesDynamicQuery(
					groupId, ignored, minModifiedDate)));
	}

	@Override
	public int getRedirectNotFoundEntriesCount(
		long groupId, Date minModifiedDate) {

		return GetterUtil.getInteger(
			redirectNotFoundEntryPersistence.countWithDynamicQuery(
				_getRedirectNotFoundEntriesDynamicQuery(
					groupId, null, minModifiedDate)));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public RedirectNotFoundEntry updateRedirectNotFoundEntry(
			long redirectNotFoundEntryId, boolean ignored)
		throws PortalException {

		RedirectNotFoundEntry redirectNotFoundEntry =
			redirectNotFoundEntryPersistence.findByPrimaryKey(
				redirectNotFoundEntryId);

		redirectNotFoundEntry.setIgnored(ignored);

		return redirectNotFoundEntryPersistence.update(redirectNotFoundEntry);
	}

	private DynamicQuery _getRedirectNotFoundEntriesDynamicQuery(
		long groupId, Boolean ignored, Date minModifiedDate) {

		DynamicQuery redirectNotFoundEntriesDynamicQuery =
			redirectNotFoundEntryLocalService.dynamicQuery();

		redirectNotFoundEntriesDynamicQuery.add(
			RestrictionsFactoryUtil.eq("groupId", groupId));

		if (ignored != null) {
			redirectNotFoundEntriesDynamicQuery.add(
				RestrictionsFactoryUtil.eq("ignored", ignored));
		}

		if (minModifiedDate != null) {
			redirectNotFoundEntriesDynamicQuery.add(
				RestrictionsFactoryUtil.gt("modifiedDate", minModifiedDate));
		}

		return redirectNotFoundEntriesDynamicQuery;
	}

	private DynamicQuery _getRedirectNotFoundEntriesDynamicQuery(
		long groupId, Boolean ignored, Date minModifiedDate,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		DynamicQuery redirectNotFoundEntriesDynamicQuery =
			_getRedirectNotFoundEntriesDynamicQuery(
				groupId, ignored, minModifiedDate);

		if (orderByComparator != null) {
			OrderFactoryUtil.addOrderByComparator(
				redirectNotFoundEntriesDynamicQuery, orderByComparator);
		}
		else {
			redirectNotFoundEntriesDynamicQuery.addOrder(
				OrderFactoryUtil.asc("createDate"));
		}

		return redirectNotFoundEntriesDynamicQuery;
	}

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private Portal _portal;

	@Reference
	private ViewCountManager _viewCountManager;

}