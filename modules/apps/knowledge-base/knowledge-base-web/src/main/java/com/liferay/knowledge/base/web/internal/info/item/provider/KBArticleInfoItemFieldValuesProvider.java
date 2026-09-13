/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.WebImage;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.web.internal.info.item.KBArticleInfoItemFields;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = Constants.SERVICE_RANKING + ":Integer=10",
	service = InfoItemFieldValuesProvider.class
)
public class KBArticleInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<KBArticle> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(KBArticle kbArticle) {
		try {
			return InfoItemFieldValues.builder(
			).infoFieldValues(
				_getKBArticleInfoFieldValues(kbArticle)
			).infoFieldValues(
				_assetEntryInfoItemFieldSetProvider.getInfoFieldValues(
					kbArticle.getCompanyId(), KBArticle.class.getName(),
					kbArticle.getResourcePrimKey())
			).infoFieldValues(
				_displayPageInfoItemFieldSetProvider.getInfoFieldValues(
					new InfoItemReference(
						KBArticle.class.getName(),
						kbArticle.getResourcePrimKey()),
					StringPool.BLANK, KBArticle.class.getSimpleName(),
					kbArticle, _getThemeDisplay())
			).infoFieldValues(
				_expandoInfoItemFieldSetProvider.getInfoFieldValues(
					KBArticle.class.getName(), kbArticle)
			).infoFieldValues(
				_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
					KBArticle.class.getName(), kbArticle)
			).infoFieldValues(
				_templateInfoItemFieldSetProvider.getInfoFieldValues(
					KBArticle.class.getName(), kbArticle)
			).infoItemReference(
				new InfoItemReference(
					KBArticle.class.getName(), kbArticle.getResourcePrimKey())
			).build();
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			throw new RuntimeException(noSuchInfoItemException);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private List<InfoFieldValue<Object>> _getKBArticleInfoFieldValues(
		KBArticle kbArticle) {

		List<InfoFieldValue<Object>> kbArticleFieldValues = new ArrayList<>();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		try {
			kbArticleFieldValues.add(
				new InfoFieldValue<>(
					KBArticleInfoItemFields.titleInfoField,
					kbArticle.getTitle()));
			kbArticleFieldValues.add(
				new InfoFieldValue<>(
					KBArticleInfoItemFields.descriptionInfoField,
					kbArticle.getDescription()));
			kbArticleFieldValues.add(
				new InfoFieldValue<>(
					KBArticleInfoItemFields.createDateInfoField,
					kbArticle.getCreateDate()));
			kbArticleFieldValues.add(
				new InfoFieldValue<>(
					KBArticleInfoItemFields.modifiedDateInfoField,
					kbArticle.getModifiedDate()));

			User user = _userLocalService.fetchUser(kbArticle.getUserId());

			if (user != null) {
				kbArticleFieldValues.add(
					new InfoFieldValue<>(
						KBArticleInfoItemFields.authorNameInfoField,
						user.getFullName()));

				if (themeDisplay != null) {
					WebImage webImage = new WebImage(
						user.getPortraitURL(themeDisplay));

					webImage.setAlt(user.getFullName());

					kbArticleFieldValues.add(
						new InfoFieldValue<>(
							KBArticleInfoItemFields.authorProfileImageInfoField,
							webImage));
				}
			}

			kbArticleFieldValues.add(
				new InfoFieldValue<>(
					KBArticleInfoItemFields.contentInfoField,
					kbArticle.getContent()));

			return kbArticleFieldValues;
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