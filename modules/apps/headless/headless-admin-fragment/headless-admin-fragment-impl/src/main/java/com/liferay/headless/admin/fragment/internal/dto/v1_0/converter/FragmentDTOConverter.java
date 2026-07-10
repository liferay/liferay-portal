/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.dto.v1_0.converter;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.admin.fragment.constant.v1_0.FieldType;
import com.liferay.headless.admin.fragment.dto.v1_0.BasicFragment;
import com.liferay.headless.admin.fragment.dto.v1_0.FormFragment;
import com.liferay.headless.admin.fragment.dto.v1_0.Fragment;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentVersion;
import com.liferay.headless.admin.fragment.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.fragment.internal.util.FieldTypeUtil;
import com.liferay.headless.admin.site.dto.v1_0.util.ThumbnailURLReferenceUtil;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = "dto.class.name=com.liferay.fragment.model.FragmentEntry",
	service = DTOConverter.class
)
public class FragmentDTOConverter
	implements DTOConverter<FragmentEntry, Fragment> {

	@Override
	public String getContentType() {
		return Fragment.class.getSimpleName();
	}

	@Override
	public Fragment toDTO(
		DTOConverterContext dtoConverterContext, FragmentEntry fragmentEntry) {

		FragmentEntry headListableFragmentEntry =
			_fetchHeadListableFragmentEntry(fragmentEntry);

		if (headListableFragmentEntry == null) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Fragment entry draft with ID ",
					fragmentEntry.getFragmentEntryId(), " references head ID ",
					fragmentEntry.getHeadId(), " that no longer exists"));
		}

		return _toFragment(headListableFragmentEntry);
	}

	private FragmentEntry _fetchHeadListableFragmentEntry(
		FragmentEntry fragmentEntry) {

		if (fragmentEntry.isHead()) {
			return fragmentEntry;
		}

		return _fragmentEntryLocalService.fetchFragmentEntry(
			fragmentEntry.getHeadId());
	}

	private BasicFragment _toBasicFragment(
		FragmentEntry fragmentEntry, List<FragmentVersion> fragmentVersions) {

		UnsafeSupplierValue<FragmentCollection, Exception> unsafeSupplierValue =
			new UnsafeSupplierValue<>(
				() -> _fragmentCollectionLocalService.getFragmentCollection(
					fragmentEntry.getFragmentCollectionId()));

		BasicFragment basicFragment = new BasicFragment() {
			{
				setCacheable(fragmentEntry::isCacheable);
				setCreator(
					() -> CreatorUtil.toCreator(fragmentEntry.getUserId()));
				setDateCreated(fragmentEntry::getCreateDate);
				setDateModified(fragmentEntry::getModifiedDate);
				setExternalReferenceCode(
					fragmentEntry::getExternalReferenceCode);
				setFragmentSet(
					() -> NestedFieldsSupplier.supply(
						"fragmentSet",
						fieldName -> _fragmentSetDTOConverter.toDTO(
							null, unsafeSupplierValue.getValue())));
				setFragmentSetExternalReferenceCode(
					() -> {
						FragmentCollection fragmentCollection =
							unsafeSupplierValue.getValue();

						return fragmentCollection.getExternalReferenceCode();
					});
				setIcon(fragmentEntry::getIcon);
				setKey(fragmentEntry::getFragmentEntryKey);
				setMarketplace(fragmentEntry::isMarketplace);
				setName(fragmentEntry::getName);
				setReadOnly(fragmentEntry::isReadOnly);
				setThumbnailURLReference(
					() -> NestedFieldsSupplier.supply(
						"thumbnailURLReference",
						fieldName ->
							ThumbnailURLReferenceUtil.
								getFileEntryThumbnailURLReference(
									fragmentEntry.getPreviewFileEntryId())));
				setType(() -> Fragment.Type.BASIC_FRAGMENT);
			}
		};

		basicFragment.setFragmentVersions(
			() -> fragmentVersions.toArray(new FragmentVersion[0]));

		return basicFragment;
	}

	private FormFragment _toFormFragment(
		FragmentEntry fragmentEntry, List<FragmentVersion> fragmentVersions) {

		UnsafeSupplierValue<FragmentCollection, Exception> unsafeSupplierValue =
			new UnsafeSupplierValue<>(
				() -> _fragmentCollectionLocalService.getFragmentCollection(
					fragmentEntry.getFragmentCollectionId()));

		FormFragment formFragment = new FormFragment() {
			{
				setCacheable(fragmentEntry::isCacheable);
				setCreator(
					() -> CreatorUtil.toCreator(fragmentEntry.getUserId()));
				setDateCreated(fragmentEntry::getCreateDate);
				setDateModified(fragmentEntry::getModifiedDate);
				setExternalReferenceCode(
					fragmentEntry::getExternalReferenceCode);
				setFieldTypes(
					() -> {
						String typeOptions = fragmentEntry.getTypeOptions();

						if (Validator.isNull(typeOptions)) {
							return null;
						}

						JSONObject typeOptionsJSONObject =
							_jsonFactory.createJSONObject(typeOptions);

						JSONArray fieldTypesJSONArray =
							typeOptionsJSONObject.getJSONArray("fieldTypes");

						if (fieldTypesJSONArray == null) {
							return null;
						}

						return TransformUtil.transform(
							JSONUtil.toStringArray(fieldTypesJSONArray),
							FieldTypeUtil::toExternalFieldType,
							FieldType.class);
					});
				setFragmentSet(
					() -> NestedFieldsSupplier.supply(
						"fragmentSet",
						fieldName -> _fragmentSetDTOConverter.toDTO(
							null, unsafeSupplierValue.getValue())));
				setFragmentSetExternalReferenceCode(
					() -> {
						FragmentCollection fragmentCollection =
							unsafeSupplierValue.getValue();

						return fragmentCollection.getExternalReferenceCode();
					});
				setIcon(fragmentEntry::getIcon);
				setKey(fragmentEntry::getFragmentEntryKey);
				setMarketplace(fragmentEntry::isMarketplace);
				setName(fragmentEntry::getName);
				setReadOnly(fragmentEntry::isReadOnly);
				setThumbnailURLReference(
					() -> NestedFieldsSupplier.supply(
						"thumbnailURLReference",
						fieldName ->
							ThumbnailURLReferenceUtil.
								getFileEntryThumbnailURLReference(
									fragmentEntry.getPreviewFileEntryId())));
				setType(() -> Fragment.Type.FORM_FRAGMENT);
			}
		};

		formFragment.setFragmentVersions(
			() -> fragmentVersions.toArray(new FragmentVersion[0]));

		return formFragment;
	}

	private Fragment _toFragment(FragmentEntry fragmentEntry) {
		List<FragmentVersion> fragmentVersions = new ArrayList<>();

		if (fragmentEntry.isHead()) {
			fragmentVersions.add(
				_toFragmentVersion(
					fragmentEntry, FragmentVersion.Status.APPROVED));
		}

		FragmentEntry draftFragmentEntry =
			_fragmentEntryLocalService.fetchDraft(
				fragmentEntry.getPrimaryKey());

		if (draftFragmentEntry != null) {
			fragmentVersions.add(
				_toFragmentVersion(
					draftFragmentEntry, FragmentVersion.Status.DRAFT));
		}
		else if (!fragmentEntry.isHead()) {
			fragmentVersions.add(
				_toFragmentVersion(
					fragmentEntry, FragmentVersion.Status.DRAFT));
		}

		if (fragmentEntry.isTypeInput()) {
			return _toFormFragment(fragmentEntry, fragmentVersions);
		}

		return _toBasicFragment(fragmentEntry, fragmentVersions);
	}

	private FragmentVersion _toFragmentVersion(
		FragmentEntry fragmentEntry,
		FragmentVersion.Status fragmentVersionStatus) {

		FragmentVersion fragmentVersion = new FragmentVersion() {
			{
				setStatus(() -> fragmentVersionStatus);
			}
		};

		if (!fragmentEntry.isMarketplace()) {
			fragmentVersion.setConfiguration(fragmentEntry::getConfiguration);
			fragmentVersion.setCss(fragmentEntry::getCss);
			fragmentVersion.setHtml(fragmentEntry::getHtml);
			fragmentVersion.setJs(fragmentEntry::getJs);
		}

		return fragmentVersion;
	}

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.FragmentSetDTOConverter)"
	)
	private DTOConverter<FragmentCollection, FragmentSet>
		_fragmentSetDTOConverter;

	@Reference
	private JSONFactory _jsonFactory;

}