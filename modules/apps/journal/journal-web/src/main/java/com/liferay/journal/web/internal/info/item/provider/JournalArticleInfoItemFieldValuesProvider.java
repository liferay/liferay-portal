/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.dynamic.data.mapping.info.item.provider.DDMFormValuesInfoFieldValuesProvider;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.display.request.attributes.contributor.InfoDisplayRequestAttributesContributor;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.WebImage;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.web.internal.info.item.JournalArticleInfoItemFields;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@Component(
	property = Constants.SERVICE_RANKING + ":Integer=10",
	service = InfoItemFieldValuesProvider.class
)
public class JournalArticleInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<JournalArticle> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(
		JournalArticle journalArticle) {

		try {
			return InfoItemFieldValues.builder(
			).infoFieldValues(
				_getJournalArticleInfoFieldValues(journalArticle)
			).infoFieldValues(
				_assetEntryInfoItemFieldSetProvider.getInfoFieldValues(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey())
			).infoFieldValues(
				_displayPageInfoItemFieldSetProvider.getInfoFieldValues(
					new InfoItemReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()),
					String.valueOf(journalArticle.getDDMStructureId()),
					JournalArticle.class.getSimpleName(), journalArticle,
					_getThemeDisplay())
			).infoFieldValues(
				_expandoInfoItemFieldSetProvider.getInfoFieldValues(
					JournalArticle.class.getName(), journalArticle)
			).infoFieldValues(
				_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
					JournalArticle.class.getName(), journalArticle)
			).infoFieldValues(
				_getDDMStructureInfoFieldValues(journalArticle)
			).infoFieldValues(
				_getDDMTemplateInfoFieldValues(journalArticle)
			).infoFieldValues(
				_templateInfoItemFieldSetProvider.getInfoFieldValues(
					JournalArticle.class.getName(),
					_getInfoItemFormVariationKey(journalArticle),
					journalArticle)
			).infoItemReference(
				new InfoItemReference(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey())
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

	private List<InfoFieldValue<Object>> _getDDMStructureInfoFieldValues(
		JournalArticle article) {

		return _ddmFormValuesInfoFieldValuesProvider.getInfoFieldValues(
			article, article.getDDMFormValues(true));
	}

	private List<InfoFieldValue<Object>> _getDDMTemplateInfoFieldValues(
		JournalArticle journalArticle) {

		List<InfoFieldValue<Object>> infoFieldValues = new ArrayList<>();

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		List<DDMTemplate> ddmTemplates = ddmStructure.getTemplates();

		ddmTemplates.forEach(
			ddmTemplate -> {
				String fieldName = _getTemplateKey(ddmTemplate);

				infoFieldValues.add(
					_getJournalTemplateInfoFieldValue(
						ddmTemplate, fieldName, journalArticle));
			});

		return infoFieldValues;
	}

	private String _getInfoItemFormVariationKey(JournalArticle journalArticle) {
		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		return String.valueOf(ddmStructure.getStructureId());
	}

	private List<InfoFieldValue<Object>> _getJournalArticleInfoFieldValues(
		JournalArticle journalArticle) {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		try {
			List<InfoFieldValue<Object>> journalArticleFieldValues =
				new ArrayList<>();

			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.titleInfoField,
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							journalArticle.getDefaultLanguageId())
					).values(
						journalArticle.getTitleMap()
					).build()));
			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.descriptionInfoField,
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							journalArticle.getDefaultLanguageId())
					).values(
						journalArticle.getDescriptionMap()
					).build()));
			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.createDateInfoField,
					journalArticle.getCreateDate()));
			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.modifiedDateInfoField,
					journalArticle.getModifiedDate()));

			if (themeDisplay != null) {
				String articleImageURL = journalArticle.getArticleImageURL(
					themeDisplay);

				if (Validator.isNotNull(articleImageURL)) {
					journalArticleFieldValues.add(
						new InfoFieldValue<>(
							JournalArticleInfoItemFields.previewImageInfoField,
							new WebImage(articleImageURL)));

					journalArticleFieldValues.add(
						new InfoFieldValue<>(
							JournalArticleInfoItemFields.smallImageInfoField,
							new WebImage(articleImageURL)));
				}
			}

			User user = _userLocalService.fetchUser(journalArticle.getUserId());

			if (user != null) {
				journalArticleFieldValues.add(
					new InfoFieldValue<>(
						JournalArticleInfoItemFields.authorNameInfoField,
						user.getFullName()));

				if (themeDisplay != null) {
					WebImage webImage = new WebImage(
						user.getPortraitURL(themeDisplay));

					webImage.setAlt(user.getFullName());

					journalArticleFieldValues.add(
						new InfoFieldValue<>(
							JournalArticleInfoItemFields.
								authorProfileImageInfoField,
							webImage));
				}
			}

			User lastEditorUser = _userLocalService.fetchUser(
				journalArticle.getStatusByUserId());

			if (lastEditorUser != null) {
				journalArticleFieldValues.add(
					new InfoFieldValue<>(
						JournalArticleInfoItemFields.lastEditorNameInfoField,
						lastEditorUser.getFullName()));

				if (themeDisplay != null) {
					WebImage webImage = new WebImage(
						lastEditorUser.getPortraitURL(themeDisplay));

					webImage.setAlt(lastEditorUser.getFullName());

					journalArticleFieldValues.add(
						new InfoFieldValue<>(
							JournalArticleInfoItemFields.
								lastEditorProfileImageInfoField,
							webImage));
				}
			}

			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.displayDateInfoField,
					journalArticle.getDisplayDate()));
			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.expirationDateInfoField,
					journalArticle.getExpirationDate()));
			journalArticleFieldValues.add(
				new InfoFieldValue<>(
					JournalArticleInfoItemFields.publishDateInfoField,
					journalArticle.getDisplayDate()));

			return journalArticleFieldValues;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private InfoFieldValue<Object> _getJournalTemplateInfoFieldValue(
		DDMTemplate ddmTemplate, String fieldName,
		JournalArticle journalArticle) {

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		String languageId = LocaleUtil.toLanguageId(locale);

		return new InfoFieldValue<>(
			InfoField.builder(
			).infoFieldType(
				HTMLInfoFieldType.INSTANCE
			).namespace(
				StringPool.BLANK
			).name(
				fieldName
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(getClass(), fieldName)
			).build(),
			() -> {
				ThemeDisplay themeDisplay = _getThemeDisplay();

				HttpServletRequest httpServletRequest =
					themeDisplay.getRequest();

				InfoItemDetailsProvider infoItemDetailsProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemDetailsProvider.class,
						JournalArticle.class.getName());

				httpServletRequest.setAttribute(
					InfoDisplayWebKeys.INFO_ITEM_DETAILS,
					infoItemDetailsProvider.getInfoItemDetails(journalArticle));

				for (InfoDisplayRequestAttributesContributor
						infoDisplayRequestAttributesContributor :
							_infoDisplayRequestAttributesContributors) {

					infoDisplayRequestAttributesContributor.addAttributes(
						httpServletRequest);
				}

				PortletRequestModel portletRequestModel = null;

				PortletRequest portletRequest =
					(PortletRequest)httpServletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_REQUEST);

				PortletResponse portletResponse =
					(PortletResponse)httpServletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_RESPONSE);

				if ((portletRequest != null) && (portletResponse != null)) {
					portletRequestModel = new PortletRequestModel(
						portletRequest, portletResponse);
				}

				JournalArticleDisplay journalArticleDisplay =
					_journalContent.getDisplay(
						journalArticle, ddmTemplate.getTemplateKey(),
						com.liferay.portal.kernel.util.Constants.VIEW,
						languageId, 1, portletRequestModel, themeDisplay);

				if (journalArticleDisplay != null) {
					return journalArticleDisplay.getContent();
				}

				try {
					journalArticleDisplay =
						_journalArticleLocalService.getArticleDisplay(
							journalArticle, ddmTemplate.getTemplateKey(), null,
							languageId, 1, null, themeDisplay);

					return journalArticleDisplay.getContent();
				}
				catch (Exception exception) {
					throw new RuntimeException(
						"Unable to render dynamic data mapping template " +
							ddmTemplate.getTemplateId(),
						exception);
				}
			});
	}

	private String _getTemplateKey(DDMTemplate ddmTemplate) {
		String templateKey = ddmTemplate.getTemplateKey();

		return PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
			templateKey.replaceAll("\\W", "_");
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
	private DDMFormValuesInfoFieldValuesProvider
		_ddmFormValuesInfoFieldValuesProvider;

	@Reference
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@Reference
	private ExpandoInfoItemFieldSetProvider _expandoInfoItemFieldSetProvider;

	@Reference
	private volatile List<InfoDisplayRequestAttributesContributor>
		_infoDisplayRequestAttributesContributors;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

	@Reference
	private UserLocalService _userLocalService;

}