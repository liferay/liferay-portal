/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.RequiredFragmentEntryVersionException;
import com.liferay.fragment.exception.UnsupportedUnpublishFragmentEntryOperationException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.headless.admin.fragment.dto.v1_0.FormFragment;
import com.liferay.headless.admin.fragment.dto.v1_0.Fragment;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentVersion;
import com.liferay.headless.admin.fragment.internal.odata.entity.v1_0.FragmentEntityModel;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.internal.util.FieldTypeUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.FragmentResource;
import com.liferay.headless.admin.site.dto.v1_0.util.FileEntryUtil;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/fragment.properties",
	scope = ServiceScope.PROTOTYPE, service = FragmentResource.class
)
public class FragmentResourceImpl extends BaseFragmentResourceImpl {

	@Override
	public void deleteSiteFragment(
			String siteExternalReferenceCode,
			String fragmentExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_fragmentEntryService.deleteFragmentEntry(
			fragmentExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Fragment getSiteFragment(
			String siteExternalReferenceCode,
			String fragmentExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		try {
			return _toFragment(
				_fragmentEntryService.getFragmentEntryByExternalReferenceCode(
					fragmentExternalReferenceCode, groupId));
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			FragmentEntry draftFragmentEntry =
				_fragmentEntryLocalService.
					fetchFragmentEntryByExternalReferenceCode(
						fragmentExternalReferenceCode, groupId, false);

			if (draftFragmentEntry == null) {
				throw noSuchModelException;
			}

			return _toFragment(draftFragmentEntry);
		}
	}

	@Override
	public Page<Fragment> getSiteFragmentSetFragmentsPage(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId);

		return _getFragmentsPage(
			null, fragmentCollection.getFragmentCollectionId(), groupId,
			pagination);
	}

	@Override
	public Page<Fragment> getSiteFragmentsPage(
			String siteExternalReferenceCode, Filter filter,
			Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		return _getFragmentsPage(
			filter, 0,
			GroupUtil.getGroupId(
				true, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			pagination);
	}

	@Override
	public Fragment postSiteFragment(
			String siteExternalReferenceCode, Fragment fragment)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return _addFragmentEntry(
			fragment.getExternalReferenceCode(), fragment,
			_getOrAddFragmentCollection(fragment, groupId), groupId);
	}

	@Override
	public Fragment postSiteFragmentSetFragment(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, Fragment fragment)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return _addFragmentEntry(
			fragment.getExternalReferenceCode(), fragment,
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId),
			groupId);
	}

	@Override
	public Fragment putSiteFragment(
			String siteExternalReferenceCode,
			String fragmentExternalReferenceCode, Fragment fragment)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		try {
			FragmentEntry fragmentEntry =
				_fragmentEntryService.getFragmentEntryByExternalReferenceCode(
					fragmentExternalReferenceCode, groupId);

			return _updateFragmentEntry(fragment, fragmentEntry, groupId);
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			FragmentEntry draftFragmentEntry =
				_fragmentEntryLocalService.
					fetchFragmentEntryByExternalReferenceCode(
						fragmentExternalReferenceCode, groupId, false);

			if (draftFragmentEntry != null) {
				return _updateFragmentEntry(
					fragment, draftFragmentEntry, groupId);
			}

			return _addFragmentEntry(
				fragmentExternalReferenceCode, fragment,
				_getOrAddFragmentCollection(fragment, groupId), groupId);
		}
	}

	private Fragment _addFragmentEntry(
			String externalReferenceCode, Fragment fragment,
			FragmentCollection fragmentCollection, long groupId)
		throws Exception {

		FragmentEntry fragmentEntry = null;

		FragmentVersion approvedFragmentVersion = _getFragmentVersion(
			fragment, FragmentVersion.Status.APPROVED);
		FragmentVersion draftFragmentVersion = _getFragmentVersion(
			fragment, FragmentVersion.Status.DRAFT);
		int type = _getType(fragment);
		String typeOptions = _getTypeOptions(fragment);

		ServiceContext serviceContext = ServiceContextUtil.getServiceContext(
			contextCompany.getCompanyId(), fragment.getDateCreated(), groupId,
			contextHttpServletRequest, fragment.getDateModified(),
			contextUser.getUserId());

		long previewFileEntryId = _getPreviewFileEntryId(fragment, groupId);

		if (approvedFragmentVersion != null) {
			fragmentEntry = _fragmentEntryService.addFragmentEntry(
				externalReferenceCode, groupId,
				fragmentCollection.getFragmentCollectionId(), fragment.getKey(),
				fragment.getName(), approvedFragmentVersion.getCss(),
				approvedFragmentVersion.getHtml(),
				approvedFragmentVersion.getJs(),
				GetterUtil.getBoolean(fragment.getCacheable()),
				approvedFragmentVersion.getConfiguration(), fragment.getIcon(),
				previewFileEntryId,
				GetterUtil.getBoolean(fragment.getMarketplace()),
				GetterUtil.getBoolean(fragment.getReadOnly()), type,
				typeOptions, WorkflowConstants.STATUS_APPROVED, serviceContext);

			if (draftFragmentVersion != null) {
				_updateDraft(
					fragmentEntry.getFragmentEntryId(), draftFragmentVersion);
			}
		}
		else if (draftFragmentVersion != null) {
			fragmentEntry = _fragmentEntryService.addFragmentEntry(
				externalReferenceCode, groupId,
				fragmentCollection.getFragmentCollectionId(), fragment.getKey(),
				fragment.getName(), draftFragmentVersion.getCss(),
				draftFragmentVersion.getHtml(), draftFragmentVersion.getJs(),
				GetterUtil.getBoolean(fragment.getCacheable()),
				draftFragmentVersion.getConfiguration(), fragment.getIcon(),
				previewFileEntryId,
				GetterUtil.getBoolean(fragment.getMarketplace()),
				GetterUtil.getBoolean(fragment.getReadOnly()), type,
				typeOptions, WorkflowConstants.STATUS_DRAFT, serviceContext);
		}
		else {
			fragmentEntry = _fragmentEntryService.addFragmentEntry(
				externalReferenceCode, groupId,
				fragmentCollection.getFragmentCollectionId(), fragment.getKey(),
				fragment.getName(), null, null, null,
				GetterUtil.getBoolean(fragment.getCacheable()), null, null,
				previewFileEntryId,
				GetterUtil.getBoolean(fragment.getMarketplace()),
				GetterUtil.getBoolean(fragment.getReadOnly()), type,
				typeOptions, WorkflowConstants.STATUS_DRAFT, serviceContext);
		}

		return _toFragment(fragmentEntry);
	}

	private Page<Fragment> _getFragmentsPage(
			Filter filter, long fragmentCollectionId, long groupId,
			Pagination pagination)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			FragmentEntry.class.getName(), null, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"fragmentCollectionId", fragmentCollectionId);
				searchContext.setAttribute("headListable", Boolean.TRUE);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			null,
			document -> {
				FragmentEntry fragmentEntry =
					_fragmentEntryService.fetchFragmentEntry(
						GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				if (fragmentEntry == null) {
					return null;
				}

				return _toFragment(fragmentEntry);
			});
	}

	private FragmentVersion _getFragmentVersion(
		Fragment fragment, FragmentVersion.Status status) {

		FragmentVersion[] fragmentVersions = fragment.getFragmentVersions();

		if (fragmentVersions == null) {
			return null;
		}

		for (FragmentVersion fragmentVersion : fragmentVersions) {
			if (status == fragmentVersion.getStatus()) {
				return fragmentVersion;
			}
		}

		return null;
	}

	private FragmentCollection _getOrAddFragmentCollection(
			Fragment fragment, long groupId)
		throws Exception {

		return FragmentSetUtil.getOrAddFragmentCollection(
			contextCompany.getCompanyId(), fragment.getFragmentSet(),
			fragment.getFragmentSetExternalReferenceCode(), groupId,
			contextHttpServletRequest,
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-fragment",
			contextAcceptLanguage.getPreferredLocale(),
			contextUser.getUserId());
	}

	private long _getPreviewFileEntryId(Fragment fragment, long groupId)
		throws Exception {

		return FileEntryUtil.getPreviewFileEntryId(
			groupId, FragmentPortletKeys.FRAGMENT,
			fragment.getThumbnailURLReference(), contextUser.getUserId());
	}

	private int _getType(Fragment fragment) {
		Fragment.Type type = fragment.getType();

		if (type == Fragment.Type.BASIC_FRAGMENT) {
			return FragmentConstants.TYPE_COMPONENT;
		}

		if (type == Fragment.Type.FORM_FRAGMENT) {
			return FragmentConstants.TYPE_INPUT;
		}

		throw new IllegalArgumentException(
			_language.get(
				contextAcceptLanguage.getPreferredLocale(),
				"a-fragment-type-is-required"));
	}

	private String _getTypeOptions(Fragment fragment) {
		if (!(fragment instanceof FormFragment formFragment)) {
			return null;
		}

		return JSONUtil.put(
			"fieldTypes",
			JSONUtil.putAll(
				(Object[])FieldTypeUtil.toInternalFieldTypes(
					formFragment.getFieldTypes()))
		).toString();
	}

	private Fragment _toFragment(FragmentEntry fragmentEntry) throws Exception {
		return _fragmentDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false, null, _dtoConverterRegistry, contextHttpServletRequest,
				fragmentEntry.getFragmentEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			fragmentEntry);
	}

	private void _updateDraft(
			long fragmentEntryId, FragmentVersion fragmentVersion)
		throws Exception {

		FragmentEntry draftFragmentEntry = _fragmentEntryService.getDraft(
			fragmentEntryId);

		draftFragmentEntry.setCss(fragmentVersion.getCss());
		draftFragmentEntry.setHtml(fragmentVersion.getHtml());
		draftFragmentEntry.setJs(fragmentVersion.getJs());
		draftFragmentEntry.setConfiguration(fragmentVersion.getConfiguration());

		_fragmentEntryService.updateDraft(draftFragmentEntry);
	}

	private Fragment _updateFragmentEntry(
			Fragment fragment, FragmentEntry fragmentEntry, long groupId)
		throws Exception {

		FragmentVersion approvedFragmentVersion = _getFragmentVersion(
			fragment, FragmentVersion.Status.APPROVED);
		FragmentVersion draftFragmentVersion = _getFragmentVersion(
			fragment, FragmentVersion.Status.DRAFT);

		if ((approvedFragmentVersion == null) &&
			(draftFragmentVersion == null)) {

			throw new RequiredFragmentEntryVersionException();
		}

		if ((approvedFragmentVersion == null) && fragmentEntry.isHead()) {
			throw new UnsupportedUnpublishFragmentEntryOperationException();
		}

		if ((fragment instanceof FormFragment) != fragmentEntry.isTypeInput()) {
			throw new IllegalArgumentException(
				_language.get(
					contextAcceptLanguage.getPreferredLocale(),
					"the-fragment-type-cannot-be-changed"));
		}

		String typeOptions = _getTypeOptions(fragment);

		FragmentEntry updatedFragmentEntry = null;

		long fragmentCollectionId = fragmentEntry.getFragmentCollectionId();

		FragmentSet fragmentSet = fragment.getFragmentSet();

		if (((fragmentSet != null) &&
			 Validator.isNotNull(fragmentSet.getExternalReferenceCode())) ||
			Validator.isNotNull(
				fragment.getFragmentSetExternalReferenceCode())) {

			FragmentCollection fragmentCollection = _getOrAddFragmentCollection(
				fragment, groupId);

			fragmentCollectionId = fragmentCollection.getFragmentCollectionId();
		}

		long fragmentEntryId = fragmentEntry.getFragmentEntryId();

		if (fragmentEntry.isHead()) {
			FragmentEntry draftFragmentEntry = _fragmentEntryService.getDraft(
				fragmentEntry.getFragmentEntryId());

			fragmentEntryId = draftFragmentEntry.getFragmentEntryId();
		}

		long previewFileEntryId = _getPreviewFileEntryId(fragment, groupId);

		if (approvedFragmentVersion != null) {
			updatedFragmentEntry = _fragmentEntryService.updateFragmentEntry(
				fragmentEntryId, fragmentCollectionId, fragment.getName(),
				approvedFragmentVersion.getCss(),
				approvedFragmentVersion.getHtml(),
				approvedFragmentVersion.getJs(),
				GetterUtil.getBoolean(fragment.getCacheable()),
				approvedFragmentVersion.getConfiguration(), fragment.getIcon(),
				previewFileEntryId,
				GetterUtil.getBoolean(fragment.getReadOnly()), typeOptions,
				WorkflowConstants.STATUS_APPROVED);

			if (draftFragmentVersion != null) {
				_updateDraft(
					updatedFragmentEntry.getFragmentEntryId(),
					draftFragmentVersion);
			}
		}
		else {
			updatedFragmentEntry = _fragmentEntryService.updateFragmentEntry(
				fragmentEntryId, fragmentCollectionId, fragment.getName(),
				draftFragmentVersion.getCss(), draftFragmentVersion.getHtml(),
				draftFragmentVersion.getJs(),
				GetterUtil.getBoolean(fragment.getCacheable()),
				draftFragmentVersion.getConfiguration(), fragment.getIcon(),
				previewFileEntryId,
				GetterUtil.getBoolean(fragment.getReadOnly()), typeOptions,
				WorkflowConstants.STATUS_DRAFT);

			updatedFragmentEntry = _fragmentEntryService.updateDraft(
				updatedFragmentEntry);
		}

		return _toFragment(updatedFragmentEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentResourceImpl.class);

	private static final EntityModel _entityModel = new FragmentEntityModel();

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.FragmentDTOConverter)"
	)
	private DTOConverter<FragmentEntry, Fragment> _fragmentDTOConverter;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private Language _language;

}