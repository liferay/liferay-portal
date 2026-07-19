/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.service.impl.LayoutLocalServiceHelper;
import com.liferay.redirect.exception.CircularRedirectEntryException;
import com.liferay.redirect.exception.DuplicateRedirectEntrySourceURLException;
import com.liferay.redirect.exception.RequiredRedirectEntryDestinationURLException;
import com.liferay.redirect.exception.RequiredRedirectEntrySourceURLException;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;
import com.liferay.redirect.service.base.RedirectEntryLocalServiceBaseImpl;
import com.liferay.redirect.service.persistence.RedirectNotFoundEntryPersistence;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.redirect.model.RedirectEntry",
	service = AopService.class
)
public class RedirectEntryLocalServiceImpl
	extends RedirectEntryLocalServiceBaseImpl {

	@Override
	public void addEntryResources(
			RedirectEntry entry, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			entry.getCompanyId(), entry.getGroupId(), entry.getUserId(),
			RedirectEntry.class.getName(), entry.getRedirectEntryId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			RedirectEntry entry, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			entry.getCompanyId(), entry.getGroupId(), entry.getUserId(),
			RedirectEntry.class.getName(), entry.getRedirectEntryId(),
			modelPermissions);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, Date expirationDate,
			boolean permanent, String sourceURL, ServiceContext serviceContext)
		throws PortalException {

		sourceURL = _friendlyURLNormalizer.normalizeWithEncoding(sourceURL);

		_validate(destinationURL, sourceURL);

		if (redirectEntryPersistence.fetchByG_S(groupId, sourceURL) != null) {
			throw new DuplicateRedirectEntrySourceURLException();
		}

		RedirectEntry redirectEntry = redirectEntryPersistence.create(
			counterLocalService.increment());

		redirectEntry.setUuid(serviceContext.getUuid());
		redirectEntry.setGroupId(groupId);
		redirectEntry.setCompanyId(serviceContext.getCompanyId());
		redirectEntry.setUserId(serviceContext.getUserId());
		redirectEntry.setDestinationURL(destinationURL);
		redirectEntry.setExpirationDate(expirationDate);
		redirectEntry.setPermanent(permanent);
		redirectEntry.setSourceURL(sourceURL);

		redirectEntry = redirectEntryPersistence.update(redirectEntry);

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addEntryResources(
				redirectEntry, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addEntryResources(
				redirectEntry, serviceContext.getModelPermissions());
		}

		RedirectNotFoundEntry redirectNotFoundEntry =
			_redirectNotFoundEntryPersistence.fetchByG_U(groupId, sourceURL);

		if (redirectNotFoundEntry != null) {
			_redirectNotFoundEntryLocalService.deleteRedirectNotFoundEntry(
				redirectNotFoundEntry);
		}

		return redirectEntry;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries, ServiceContext serviceContext)
		throws PortalException {

		sourceURL = _friendlyURLNormalizer.normalizeWithEncoding(sourceURL);

		_checkDestinationURLMustNotBeEqualToSourceURL(
			destinationURL, groupBaseURL, sourceURL);

		RedirectEntry redirectEntry = addRedirectEntry(
			groupId, destinationURL, expirationDate, permanent, sourceURL,
			serviceContext);

		_checkChainedRedirectEntries(groupBaseURL, redirectEntry);

		if (updateChainedRedirectEntries) {
			redirectEntry = _updateChainedRedirectEntries(
				groupBaseURL, redirectEntry);
		}

		return redirectEntry;
	}

	@Override
	public void deleteRedirectEntries(long groupId) throws PortalException {
		for (RedirectEntry redirectEntry :
				redirectEntryPersistence.findByGroupId(groupId)) {

			redirectEntryLocalService.deleteRedirectEntry(redirectEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public RedirectEntry deleteRedirectEntry(long redirectEntryId)
		throws PortalException {

		RedirectEntry redirectEntry = fetchRedirectEntry(redirectEntryId);

		if (redirectEntry == null) {
			return null;
		}

		return deleteRedirectEntry(redirectEntry);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public RedirectEntry deleteRedirectEntry(RedirectEntry redirectEntry)
		throws PortalException {

		_resourceLocalService.deleteResource(
			redirectEntry.getCompanyId(), RedirectEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			redirectEntry.getRedirectEntryId());

		return super.deleteRedirectEntry(redirectEntry);
	}

	@Override
	public RedirectEntry fetchRedirectEntry(long groupId, String sourceURL) {
		return redirectEntryLocalService.fetchRedirectEntry(
			groupId, sourceURL, false);
	}

	@Override
	public RedirectEntry fetchRedirectEntry(
		long groupId, String sourceURL, boolean updateLastOccurrenceDate) {

		RedirectEntry redirectEntry = redirectEntryPersistence.fetchByG_S(
			groupId, sourceURL);

		if (redirectEntry != null) {
			if (_isExpired(redirectEntry)) {
				return null;
			}

			if (updateLastOccurrenceDate &&
				((redirectEntry.getLastOccurrenceDate() == null) ||
				 !_isInTheSameDay(
					 redirectEntry.getLastOccurrenceDate(),
					 DateUtil.newDate()))) {

				redirectEntry.setLastOccurrenceDate(new Date());

				redirectEntry = redirectEntryLocalService.updateRedirectEntry(
					redirectEntry);
			}
		}

		return redirectEntry;
	}

	@Override
	public List<RedirectEntry> getRedirectEntries(
		long groupId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return redirectEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<RedirectEntry> getRedirectEntries(
		long groupId, String destinationURL) {

		return ListUtil.filter(
			redirectEntryPersistence.findByG_D(groupId, destinationURL),
			redirectEntry -> !_isExpired(redirectEntry));
	}

	@Override
	public int getRedirectEntriesCount(long groupId) {
		return redirectEntryPersistence.countByGroupId(groupId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL, Date expirationDate,
			boolean permanent, String sourceURL)
		throws PortalException {

		sourceURL = _friendlyURLNormalizer.normalizeWithEncoding(sourceURL);

		_validate(destinationURL, sourceURL);

		RedirectEntry redirectEntry = getRedirectEntry(redirectEntryId);

		RedirectEntry existingRedirectEntry =
			redirectEntryPersistence.fetchByG_S(
				redirectEntry.getGroupId(), sourceURL);

		if ((existingRedirectEntry != null) &&
			(existingRedirectEntry.getRedirectEntryId() != redirectEntryId)) {

			throw new DuplicateRedirectEntrySourceURLException();
		}

		redirectEntry.setDestinationURL(destinationURL);
		redirectEntry.setExpirationDate(expirationDate);
		redirectEntry.setPermanent(permanent);
		redirectEntry.setSourceURL(sourceURL);

		return redirectEntryPersistence.update(redirectEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL, Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries)
		throws PortalException {

		sourceURL = _friendlyURLNormalizer.normalizeWithEncoding(sourceURL);

		_checkDestinationURLMustNotBeEqualToSourceURL(
			destinationURL, groupBaseURL, sourceURL);

		RedirectEntry redirectEntry =
			redirectEntryLocalService.updateRedirectEntry(
				redirectEntryId, destinationURL, expirationDate, permanent,
				sourceURL);

		_checkChainedRedirectEntries(groupBaseURL, redirectEntry);

		if (updateChainedRedirectEntries) {
			redirectEntry = _updateChainedRedirectEntries(
				groupBaseURL, redirectEntry);
		}

		return redirectEntry;
	}

	private void _checkChainedRedirectEntries(
			String groupBaseURL, RedirectEntry redirectEntry)
		throws PortalException {

		List<RedirectEntry> chainedRedirectEntries =
			redirectEntryLocalService.getRedirectEntries(
				redirectEntry.getGroupId(),
				groupBaseURL + StringPool.SLASH + redirectEntry.getSourceURL());

		for (RedirectEntry chainedRedirectEntry : chainedRedirectEntries) {
			_checkMustNotFormALoopWithAnotherRedirectEntry(
				redirectEntry.getDestinationURL(), groupBaseURL,
				chainedRedirectEntry.getSourceURL());
		}

		RedirectEntry chainedRedirectEntry =
			redirectEntryLocalService.fetchRedirectEntry(
				redirectEntry.getGroupId(),
				StringUtil.removeSubstring(
					redirectEntry.getDestinationURL(),
					groupBaseURL + StringPool.SLASH));

		if (chainedRedirectEntry != null) {
			_checkMustNotFormALoopWithAnotherRedirectEntry(
				chainedRedirectEntry.getDestinationURL(), groupBaseURL,
				redirectEntry.getSourceURL());
		}
	}

	private void _checkDestinationURLMustNotBeEqualToSourceURL(
			String destinationURL, String groupBaseURL, String sourceURL)
		throws CircularRedirectEntryException.
			DestinationURLMustNotBeEqualToSourceURL {

		String completeSourceURL = groupBaseURL + StringPool.SLASH + sourceURL;

		if (StringUtil.equalsIgnoreCase(completeSourceURL, destinationURL)) {
			throw new CircularRedirectEntryException.
				DestinationURLMustNotBeEqualToSourceURL(
					sourceURL, destinationURL);
		}
	}

	private void _checkMustNotFormALoopWithAnotherRedirectEntry(
			String destinationURL, String groupBaseURL, String sourceURL)
		throws CircularRedirectEntryException.
			MustNotFormALoopWithAnotherRedirectEntry {

		String completeSourceURL = groupBaseURL + StringPool.SLASH + sourceURL;

		if (StringUtil.equalsIgnoreCase(completeSourceURL, destinationURL)) {
			throw new CircularRedirectEntryException.
				MustNotFormALoopWithAnotherRedirectEntry();
		}
	}

	private Instant _getDayInstant(Date date) {
		Instant instant = date.toInstant();

		return instant.truncatedTo(ChronoUnit.DAYS);
	}

	private boolean _isExpired(RedirectEntry redirectEntry) {
		Date expirationDate = redirectEntry.getExpirationDate();

		if ((expirationDate != null) &&
			(DateUtil.compareTo(expirationDate, DateUtil.newDate()) <= 0)) {

			return true;
		}

		return false;
	}

	private boolean _isInTheSameDay(Date date1, Date date2) {
		Instant instant1 = _getDayInstant(date1);
		Instant instant2 = _getDayInstant(date2);

		return instant1.equals(instant2);
	}

	private RedirectEntry _updateChainedRedirectEntries(
			String groupBaseURL, RedirectEntry redirectEntry)
		throws PortalException {

		List<RedirectEntry> chainedRedirectEntries =
			redirectEntryLocalService.getRedirectEntries(
				redirectEntry.getGroupId(),
				groupBaseURL + StringPool.SLASH + redirectEntry.getSourceURL());

		for (RedirectEntry chainedRedirectEntry : chainedRedirectEntries) {
			RedirectEntry updatedRedirectEntry =
				redirectEntryLocalService.updateRedirectEntry(
					chainedRedirectEntry.getRedirectEntryId(),
					redirectEntry.getDestinationURL(),
					chainedRedirectEntry.getExpirationDate(),
					chainedRedirectEntry.isPermanent(),
					chainedRedirectEntry.getSourceURL());

			RedirectEntry destinationRedirectEntry =
				redirectEntryLocalService.fetchRedirectEntry(
					redirectEntry.getGroupId(),
					StringUtil.removeSubstring(
						updatedRedirectEntry.getDestinationURL(),
						groupBaseURL + StringPool.SLASH));

			if (destinationRedirectEntry != null) {
				_checkMustNotFormALoopWithAnotherRedirectEntry(
					updatedRedirectEntry.getDestinationURL(), groupBaseURL,
					destinationRedirectEntry.getSourceURL());
			}
		}

		RedirectEntry chainedRedirectEntry =
			redirectEntryLocalService.fetchRedirectEntry(
				redirectEntry.getGroupId(),
				StringUtil.removeSubstring(
					redirectEntry.getDestinationURL(),
					groupBaseURL + StringPool.SLASH));

		if (chainedRedirectEntry != null) {
			return redirectEntryLocalService.updateRedirectEntry(
				redirectEntry.getRedirectEntryId(),
				chainedRedirectEntry.getDestinationURL(),
				redirectEntry.getExpirationDate(), redirectEntry.isPermanent(),
				redirectEntry.getSourceURL());
		}

		return redirectEntry;
	}

	private void _validate(String destinationURL, String sourceURL)
		throws PortalException {

		if (Validator.isNull(destinationURL)) {
			throw new RequiredRedirectEntryDestinationURLException();
		}

		if (Validator.isNull(sourceURL)) {
			throw new RequiredRedirectEntrySourceURLException();
		}

		if (sourceURL.startsWith(StringPool.SLASH)) {
			throw new LayoutFriendlyURLException(
				LayoutFriendlyURLException.DOES_NOT_START_WITH_SLASH);
		}

		int exceptionType = LayoutImpl.validateFriendlyURL(
			StringPool.SLASH + sourceURL, true);

		if (exceptionType != -1) {
			throw new LayoutFriendlyURLException(exceptionType);
		}

		_layoutLocalServiceHelper.validateFriendlyURLKeyword(
			StringPool.SLASH + sourceURL);
	}

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private LayoutLocalServiceHelper _layoutLocalServiceHelper;

	@Reference
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

	@Reference
	private RedirectNotFoundEntryPersistence _redirectNotFoundEntryPersistence;

	@Reference
	private ResourceLocalService _resourceLocalService;

}