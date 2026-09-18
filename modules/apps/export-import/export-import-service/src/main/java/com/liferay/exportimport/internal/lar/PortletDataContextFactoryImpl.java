/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.service.ChangesetCollectionLocalService;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.util.PortalInstances;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(service = PortletDataContextFactory.class)
public class PortletDataContextFactoryImpl
	implements PortletDataContextFactory {

	@Override
	public PortletDataContext clonePortletDataContext(
		PortletDataContext portletDataContext) {

		if (portletDataContext == null) {
			return null;
		}

		PortletDataContext clonePortletDataContext = new PortletDataContextImpl(
			_lockManager, false);

		clonePortletDataContext.setCompanyId(portletDataContext.getCompanyId());
		clonePortletDataContext.setCompanyGroupId(
			portletDataContext.getCompanyGroupId());
		clonePortletDataContext.setDataStrategy(
			portletDataContext.getDataStrategy());
		clonePortletDataContext.setEndDate(portletDataContext.getEndDate());
		clonePortletDataContext.setExportDataRootElement(
			portletDataContext.getExportDataRootElement());
		clonePortletDataContext.setGroupId(portletDataContext.getGroupId());
		clonePortletDataContext.setImportDataRootElement(
			portletDataContext.getImportDataRootElement());

		long[] layoutIds = portletDataContext.getLayoutIds();

		if (ArrayUtil.isNotEmpty(layoutIds)) {
			clonePortletDataContext.setLayoutIds(
				Arrays.copyOf(layoutIds, layoutIds.length));
		}

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		clonePortletDataContext.setManifestSummary(
			(ManifestSummary)manifestSummary.clone());

		Map<String, Map<?, ?>> cloneNewPrimaryKeysMaps =
			clonePortletDataContext.getNewPrimaryKeysMaps();

		cloneNewPrimaryKeysMaps.putAll(
			portletDataContext.getNewPrimaryKeysMaps());

		clonePortletDataContext.setParameterMap(
			portletDataContext.getParameterMap());
		clonePortletDataContext.setPortletId(portletDataContext.getPortletId());
		clonePortletDataContext.setPrivateLayout(
			portletDataContext.isPrivateLayout());
		clonePortletDataContext.setScopeGroupId(
			portletDataContext.getScopeGroupId());
		clonePortletDataContext.setSourceCompanyId(
			portletDataContext.getSourceCompanyId());
		clonePortletDataContext.setSourceCompanyGroupId(
			portletDataContext.getSourceCompanyGroupId());
		clonePortletDataContext.setSourceGroupId(
			portletDataContext.getSourceGroupId());
		clonePortletDataContext.setSourceUserPersonalSiteGroupId(
			portletDataContext.getSourceUserPersonalSiteGroupId());
		clonePortletDataContext.setStartDate(portletDataContext.getStartDate());
		clonePortletDataContext.setUserIdStrategy(
			portletDataContext.getUserIdStrategy());
		clonePortletDataContext.setUserPersonalSiteGroupId(
			portletDataContext.getUserPersonalSiteGroupId());
		clonePortletDataContext.setZipWriter(portletDataContext.getZipWriter());

		return clonePortletDataContext;
	}

	@Override
	public PortletDataContext createExportPortletDataContext(
			long companyId, long groupId, Map<String, String[]> parameterMap,
			Date startDate, Date endDate, ZipWriter zipWriter)
		throws PortletDataException {

		_validateDateRange(startDate, endDate);

		PortletDataContext portletDataContext = _createPortletDataContext(
			companyId, groupId);

		portletDataContext.setEndDate(endDate);
		portletDataContext.setParameterMap(parameterMap);
		portletDataContext.setStartDate(startDate);
		portletDataContext.setZipWriter(zipWriter);

		_setChangesetParameters(portletDataContext);

		return portletDataContext;
	}

	@Override
	public PortletDataContext createImportPortletDataContext(
			long companyId, long groupId, Map<String, String[]> parameterMap,
			UserIdStrategy userIdStrategy, ZipReader zipReader)
		throws PortletDataException {

		PortletDataContext portletDataContext = _createPortletDataContext(
			companyId, groupId);

		portletDataContext.setDataStrategy(
			MapUtil.getString(
				parameterMap, PortletDataHandlerKeys.DATA_STRATEGY,
				PortletDataHandlerKeys.DATA_STRATEGY_MIRROR));
		portletDataContext.setNewLayouts(new ArrayList<Layout>());
		portletDataContext.setParameterMap(parameterMap);
		portletDataContext.setUserIdStrategy(userIdStrategy);
		portletDataContext.setZipReader(zipReader);

		_readXML(portletDataContext);

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		groupIds.put(
			portletDataContext.getSourceCompanyGroupId(),
			portletDataContext.getCompanyGroupId());

		return portletDataContext;
	}

	@Override
	public PortletDataContext createPreparePortletDataContext(
			long companyId, long groupId, Date startDate, Date endDate)
		throws PortletDataException {

		return createPreparePortletDataContext(
			companyId, groupId, null, startDate, endDate);
	}

	@Override
	public PortletDataContext createPreparePortletDataContext(
			long companyId, long groupId, String range, Date startDate,
			Date endDate)
		throws PortletDataException {

		_validateDateRange(startDate, endDate);

		PortletDataContext portletDataContext = _createPortletDataContext(
			companyId, groupId);

		portletDataContext.setEndDate(endDate);

		Map<String, String[]> parameterMap = Collections.emptyMap();

		if (range != null) {
			parameterMap = HashMapBuilder.put(
				ExportImportDateUtil.RANGE, new String[] {range}
			).build();
		}

		portletDataContext.setParameterMap(parameterMap);

		portletDataContext.setStartDate(startDate);

		_setChangesetParameters(portletDataContext);

		return portletDataContext;
	}

	@Override
	public PortletDataContext createPreparePortletDataContext(
			ThemeDisplay themeDisplay, Date startDate, Date endDate)
		throws PortletDataException {

		return createPreparePortletDataContext(
			themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			startDate, endDate);
	}

	private PortletDataContext _createPortletDataContext(
		long companyId, long groupId) {

		PortletDataContext portletDataContext = new PortletDataContextImpl(
			_lockManager);

		try {
			Group companyGroup = _groupLocalService.fetchCompanyGroup(
				companyId);

			if (companyGroup != null) {
				portletDataContext.setCompanyGroupId(companyGroup.getGroupId());
			}
		}
		catch (Exception exception) {
			if (PortalInstances.isCurrentCompanyInDeletionProcess()) {
				PortletDataException portletDataException =
					new PortletDataException(
						PortletDataException.COMPANY_BEING_DELETED, exception);

				portletDataException.setCompanyId(companyId);

				throw new SystemException(portletDataException);
			}
		}

		portletDataContext.setCompanyId(companyId);
		portletDataContext.setGroupId(groupId);
		portletDataContext.setScopeGroupId(groupId);

		try {
			Group userPersonalSiteGroup =
				_groupLocalService.fetchUserPersonalSiteGroup(companyId);

			if (userPersonalSiteGroup != null) {
				portletDataContext.setUserPersonalSiteGroupId(
					userPersonalSiteGroup.getGroupId());
			}
		}
		catch (Exception exception) {
			if (PortalInstances.isCurrentCompanyInDeletionProcess()) {
				PortletDataException portletDataException =
					new PortletDataException(
						PortletDataException.COMPANY_BEING_DELETED, exception);

				portletDataException.setCompanyId(companyId);

				throw new SystemException(portletDataException);
			}
		}

		return portletDataContext;
	}

	private void _readXML(PortletDataContext portletDataContext)
		throws PortletDataException {

		String xml = portletDataContext.getZipEntryAsString("/manifest.xml");

		Element rootElement = null;

		try {
			Document document = SAXReaderUtil.read(xml);

			rootElement = document.getRootElement();
		}
		catch (Exception exception) {
			throw new PortletDataException(
				"Unable to create portlet data context for the import " +
					"process because of an invalid LAR manifest",
				exception);
		}

		portletDataContext.setImportDataRootElement(rootElement);

		Element headerElement = rootElement.element("header");

		portletDataContext.setSourceCompanyId(
			GetterUtil.getLong(headerElement.attributeValue("company-id")));
		portletDataContext.setSourceCompanyGroupId(
			GetterUtil.getLong(
				headerElement.attributeValue("company-group-id")));
		portletDataContext.setSourceGroupId(
			GetterUtil.getLong(headerElement.attributeValue("group-id")));
		portletDataContext.setSourceUserPersonalSiteGroupId(
			GetterUtil.getLong(
				headerElement.attributeValue("user-personal-site-group-id")));

		Element missingReferencesElement = rootElement.element(
			"missing-references");

		if (missingReferencesElement != null) {
			portletDataContext.setMissingReferencesElement(
				missingReferencesElement);
		}
	}

	private void _setChangesetParameters(
		PortletDataContext portletDataContext) {

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		if (!ExportImportDateUtil.isRangeFromLastPublishDate(parameterMap)) {
			return;
		}

		ChangesetCollection changesetCollection =
			_changesetCollectionLocalService.fetchChangesetCollection(
				portletDataContext.getScopeGroupId(),
				StagingConstants.RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

		if (changesetCollection == null) {
			return;
		}

		parameterMap.put(
			"changesetCollectionId",
			new String[] {
				String.valueOf(changesetCollection.getChangesetCollectionId())
			});

		try {
			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getStrictPortletSetup(
					portletDataContext.getCompanyId(),
					portletDataContext.getScopeGroupId(),
					ChangesetPortletKeys.CHANGESET);

			Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
				portletDataContext, portletPreferences);

			if (lastPublishDate == null) {
				return;
			}

			parameterMap.put(
				"changesetLastPublishDate",
				new String[] {String.valueOf(lastPublishDate.getTime())});
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _validateDateRange(Date startDate, Date endDate)
		throws PortletDataException {

		if ((startDate == null) && (endDate != null)) {
			throw new PortletDataException(
				PortletDataException.END_DATE_IS_MISSING_START_DATE);
		}
		else if ((startDate != null) && (endDate == null)) {
			throw new PortletDataException(
				PortletDataException.START_DATE_IS_MISSING_END_DATE);
		}

		if (startDate != null) {
			if (startDate.after(endDate) || startDate.equals(endDate)) {
				throw new PortletDataException(
					PortletDataException.START_DATE_AFTER_END_DATE);
			}

			Date date = new Date();

			if (startDate.after(date)) {
				throw new PortletDataException(
					PortletDataException.FUTURE_START_DATE);
			}

			if (endDate.after(date)) {
				throw new PortletDataException(
					PortletDataException.FUTURE_END_DATE);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletDataContextFactoryImpl.class);

	@Reference
	private ChangesetCollectionLocalService _changesetCollectionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LockManager _lockManager;

}