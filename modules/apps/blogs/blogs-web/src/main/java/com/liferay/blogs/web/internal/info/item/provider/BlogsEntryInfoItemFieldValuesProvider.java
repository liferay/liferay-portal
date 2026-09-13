/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.info.item.BlogsEntryInfoItemFields;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.WebImage;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Jorge Ferrer
 */
@Component(
	property = Constants.SERVICE_RANKING + ":Integer=10",
	service = InfoItemFieldValuesProvider.class
)
public class BlogsEntryInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<BlogsEntry> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(BlogsEntry blogsEntry) {
		try {
			return InfoItemFieldValues.builder(
			).infoFieldValues(
				_getBlogsEntryInfoFieldValues(blogsEntry)
			).infoFieldValues(
				_assetEntryInfoItemFieldSetProvider.getInfoFieldValues(
					blogsEntry.getCompanyId(), BlogsEntry.class.getName(),
					blogsEntry.getEntryId())
			).infoFieldValues(
				_displayPageInfoItemFieldSetProvider.getInfoFieldValues(
					new InfoItemReference(
						BlogsEntry.class.getName(), blogsEntry.getEntryId()),
					StringPool.BLANK, BlogsEntry.class.getSimpleName(),
					blogsEntry, _getThemeDisplay())
			).infoFieldValues(
				_expandoInfoItemFieldSetProvider.getInfoFieldValues(
					BlogsEntry.class.getName(), blogsEntry)
			).infoFieldValues(
				_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
					BlogsEntry.class.getName(), blogsEntry)
			).infoFieldValues(
				_templateInfoItemFieldSetProvider.getInfoFieldValues(
					BlogsEntry.class.getName(), blogsEntry)
			).infoItemReference(
				new InfoItemReference(
					BlogsEntry.class.getName(), blogsEntry.getEntryId())
			).build();
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			throw new RuntimeException(
				"Caught unexpected exception", noSuchInfoItemException);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private List<InfoFieldValue<Object>> _getBlogsEntryInfoFieldValues(
		BlogsEntry blogsEntry) {

		List<InfoFieldValue<Object>> blogsEntryInfoFieldValues =
			new ArrayList<>();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		try {
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.titleInfoField,
					blogsEntry.getTitle()));
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.subtitleInfoField,
					blogsEntry.getSubtitle()));
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.descriptionInfoField,
					blogsEntry.getDescription()));
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.createDateInfoField,
					blogsEntry.getCreateDate()));
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.modifiedDateInfoField,
					blogsEntry.getModifiedDate()));

			if (themeDisplay != null) {
				WebImage smallWebImage = new WebImage(
					blogsEntry.getSmallImageURL(themeDisplay),
					new InfoItemReference(
						FileEntry.class.getName(),
						new ClassPKInfoItemIdentifier(
							blogsEntry.getSmallImageFileEntryId())));

				smallWebImage.setAlt(blogsEntry.getSmallImageAlt());

				blogsEntryInfoFieldValues.add(
					new InfoFieldValue<>(
						BlogsEntryInfoItemFields.smallImageInfoField,
						smallWebImage));

				String coverImageURL = blogsEntry.getCoverImageURL(
					themeDisplay);

				WebImage coverWebImage = new WebImage(
					coverImageURL,
					new InfoItemReference(
						FileEntry.class.getName(),
						new ClassPKInfoItemIdentifier(
							blogsEntry.getCoverImageFileEntryId())));

				coverWebImage.setAlt(blogsEntry.getCoverImageAlt());

				blogsEntryInfoFieldValues.add(
					new InfoFieldValue<>(
						BlogsEntryInfoItemFields.coverImageInfoField,
						coverWebImage));

				if (Validator.isNotNull(coverImageURL)) {
					blogsEntryInfoFieldValues.add(
						new InfoFieldValue<>(
							BlogsEntryInfoItemFields.previewImageInfoField,
							coverWebImage));
				}
				else {
					blogsEntryInfoFieldValues.add(
						new InfoFieldValue<>(
							BlogsEntryInfoItemFields.previewImageInfoField,
							smallWebImage));
				}
			}

			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.coverImageCaptionInfoField,
					blogsEntry.getCoverImageCaption()));

			User user = _userLocalService.fetchUser(blogsEntry.getUserId());

			if (user != null) {
				blogsEntryInfoFieldValues.add(
					new InfoFieldValue<>(
						BlogsEntryInfoItemFields.authorNameInfoField,
						user.getFullName()));

				if (themeDisplay != null) {
					WebImage webImage = new WebImage(
						user.getPortraitURL(themeDisplay));

					webImage.setAlt(user.getFullName());

					blogsEntryInfoFieldValues.add(
						new InfoFieldValue<>(
							BlogsEntryInfoItemFields.
								authorProfileImageInfoField,
							webImage));
				}
			}

			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.displayDateInfoField,
					blogsEntry.getDisplayDate()));
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.publishDateInfoField,
					blogsEntry.getDisplayDate()));
			blogsEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					BlogsEntryInfoItemFields.contentInfoField,
					blogsEntry.getContent()));

			return blogsEntryInfoFieldValues;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getThemeDisplay();
		}

		return null;
	}

	@Reference
	private AssetEntryInfoItemFieldSetProvider
		_assetEntryInfoItemFieldSetProvider;

	@Reference
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@Reference
	private ExpandoInfoItemFieldSetProvider _expandoInfoItemFieldSetProvider;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

	@Reference
	private UserLocalService _userLocalService;

}